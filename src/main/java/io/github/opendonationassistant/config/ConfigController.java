package io.github.opendonationassistant.config;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.config.values.ConfigValue;
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
import jakarta.annotation.Nonnull;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.Optional;

@Controller("/config")
public class ConfigController extends BaseController {

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
    @Nonnull @QueryValue(value = "ownerId", defaultValue = "") String ownerId,
    @Nonnull @QueryValue(value = "url", defaultValue = "") String url,
    @Nullable Authentication auth
  ) {
    if (auth == null && !"paymentpage".equals(name)) {
      return HttpResponse.unauthorized();
    }
    return getOwnerId(auth)
      .or(() -> Optional.ofNullable(ownerId))
      .map(id ->
        factory
          .findExisting(id, name)
          .orElseGet(() -> factory.create(name, id, url, new HashMap<>()))
      )
      .or(() ->
        Optional.ofNullable(url).map(it ->
          factory
            .findByUrl(name, it)
            .orElseGet(() -> factory.create(name, ownerId, it, new HashMap<>()))
        )
      )
      .map(HttpResponse::ok)
      .orElse(HttpResponse.notFound());
  }

  @Secured(SecurityRule.IS_AUTHENTICATED)
  @Put(consumes = { MediaType.APPLICATION_JSON })
  public HttpResponse<Void> put(
    @Body ConfigValue configValue,
    Authentication auth
  ) {
    var ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    configValue.setId(
      Generators.timeBasedEpochGenerator().generate().toString()
    );
    configValue.setOwnerId(ownerId.get());
    configRepository.save(configValue);
    return HttpResponse.ok();
  }

  @Secured(SecurityRule.IS_AUTHENTICATED)
  @Post(value = "{id}", consumes = { MediaType.APPLICATION_JSON })
  public HttpResponse<Void> update(
    @PathVariable("id") String id,
    @Body ConfigValue configValue,
    Authentication auth
  ) {
    final Optional<String> ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    configValue.setId(id);
    configValue.setOwnerId(ownerId.get());
    if (configRepository.existsById(id)) {
      configRepository.update(configValue);
    } else {
      configRepository.save(configValue);
    }
    return HttpResponse.ok();
  }
}
