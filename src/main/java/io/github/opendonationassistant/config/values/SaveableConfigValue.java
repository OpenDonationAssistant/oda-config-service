package io.github.opendonationassistant.config.values;

import java.util.Map;
import java.util.Optional;

import io.github.opendonationassistant.config.ConfigRepository;

public class SaveableConfigValue extends ConfigValue {

  private ConfigRepository repository;

  public SaveableConfigValue(
    String id,
    String name,
    String ownerId,
    String url,
    Map<String, Object> value,
    ConfigRepository repository
  ) {
    super(id, name, ownerId, url, value);
    this.repository = repository;
  }

  public void save() {
    Optional<ConfigValue> existing = repository.find(getOwnerId(), getName());
    existing.ifPresentOrElse(repository::update, () -> repository.save(this));
  }
}
