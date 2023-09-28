package io.github.stcarolas.oda.config;

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
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Controller("/config")
@Slf4j
public class ConfigController {

  private final ConfigRepository configRepository;

  @Inject
  public ConfigController(ConfigRepository repository) {
    this.configRepository = repository;
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
    var owner = auth == null
      ? ownerId
      : String.valueOf(
        auth.getAttributes().getOrDefault("preferred_username", "")
      );
    return HttpResponse.ok(configRepository.find(owner, name));
  }

  @Secured(SecurityRule.IS_ANONYMOUS)
  @Put(consumes = { MediaType.APPLICATION_JSON })
  public void put(@Body ConfigValue configValue) {
    configValue.setId(UUID.randomUUID().toString());
    configRepository.save(configValue);
  }

  @Secured(SecurityRule.IS_ANONYMOUS)
  @Post(value = "{id}", consumes = { MediaType.APPLICATION_JSON })
  public void update(
    @PathVariable("id") String id,
    @Body ConfigValue configValue
  ) {
    configValue.setId(id);
    configRepository.update(configValue);
  }
}
