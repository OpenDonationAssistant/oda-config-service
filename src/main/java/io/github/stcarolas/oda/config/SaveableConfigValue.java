package io.github.stcarolas.oda.config;

import java.util.Map;
import java.util.Optional;

public class SaveableConfigValue extends ConfigValue {

  private ConfigRepository repository;

  public SaveableConfigValue(
    String name,
    String ownerId,
    Map<String, Object> value,
    ConfigRepository repository
  ) {
    super(name, ownerId, value);
    this.repository = repository;
  }

  public void save() {
    Optional<ConfigValue> existing = repository.find(getOwnerId(), getName());
    existing.ifPresentOrElse(repository::update, () -> repository.save(this));
  }
}
