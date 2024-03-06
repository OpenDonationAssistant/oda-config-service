package io.github.stcarolas.oda.config;

import io.github.stcarolas.oda.config.values.WidgetsConfigValue;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@Singleton
public class ConfigValueAbstractFactory {

  private final ConfigRepository repository;

  @Inject
  public ConfigValueAbstractFactory(@NonNull ConfigRepository repository) {
    Objects.requireNonNull(repository, "ConfigRepository should be configured");
    this.repository = repository;
  }

  public @NonNull Optional<ConfigValue> findExisting(
    @NonNull String ownerId,
    @NonNull String name
  ) {
    Objects.requireNonNull(ownerId, "Missing ownerId to search for config");
    Objects.requireNonNull(name, "Missing config's name to search for it");
    Optional<ConfigValue> value = repository.find(ownerId, name);
    if (value.isEmpty() && "widgets".equalsIgnoreCase(name)) {
      SaveableConfigValue widgetsValue = new SaveableConfigValue(
        "widgets",
        ownerId,
        new HashMap<>(),
        repository
      );
      widgetsValue.save();
      return Optional.of(
        new WidgetsConfigValue(
          widgetsValue.getId(),
          widgetsValue.getOwnerId(),
          widgetsValue.getValue(),
          repository
        )
      );
    }
    return value.map(it ->
      "widgets".equals(it.getName())
        ? new WidgetsConfigValue(
          it.getId(),
          it.getOwnerId(),
          it.getValue(),
          repository
        )
        : it
    );
  }
}
