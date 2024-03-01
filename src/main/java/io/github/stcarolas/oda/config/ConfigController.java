package io.github.stcarolas.oda.config;

import com.fasterxml.uuid.Generators;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;

@Controller("/config")
public class ConfigController {

  private final ConfigRepository configRepository;
  private final ConfigValueAbstractFactory factory;

  @Inject
  public ConfigController(
    ConfigRepository repository,
    ConfigValueAbstractFactory factory
  ) {
    this.configRepository = repository;
    this.factory = factory;
  }

  @Secured(SecurityRule.IS_ANONYMOUS)
  @Get("{name}")
  public HttpResponse<ConfigValue> get(
    @PathVariable("name") String name,
    @QueryValue(value = "ownerId", defaultValue = "") String ownerId,
    @Nullable Authentication auth
  ) {
    if (auth == null && !"paymentpage".equals(name)) {
      return HttpResponse.unauthorized();
    }
    var owner = auth == null ? ownerId : getOwnerId(auth);
    return factory
      .findExisting(ownerId, name)
      .map(HttpResponse::ok)
      .orElse(HttpResponse.notFound());
  }

  @Secured(SecurityRule.IS_AUTHENTICATED)
  @Put(consumes = { MediaType.APPLICATION_JSON })
  public void put(@Body ConfigValue configValue, Authentication auth) {
    configValue.setId(
      Generators.timeBasedEpochGenerator().generate().toString()
    );
    configValue.setOwnerId(getOwnerId(auth));
    configRepository.save(configValue);
  }

  @Secured(SecurityRule.IS_AUTHENTICATED)
  @Post(value = "{id}", consumes = { MediaType.APPLICATION_JSON })
  public void update(
    @PathVariable("id") String id,
    @Body ConfigValue configValue,
    Authentication auth
  ) {
    configValue.setId(id);
    configValue.setOwnerId(getOwnerId(auth));
    configRepository.update(configValue);
  }

  private String getOwnerId(Authentication auth) {
    return String.valueOf(
      auth.getAttributes().getOrDefault("preferred_username", "")
    );
  }
}
