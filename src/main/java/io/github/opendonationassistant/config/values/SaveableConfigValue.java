package io.github.opendonationassistant.config.values;

import io.github.opendonationassistant.config.ConfigRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SaveableConfigValue extends ConfigValue {

  private ConfigRepository repository;

  public SaveableConfigValue(
    String id,
    String name,
    String ownerId,
    String url,
    Map<String, Object> value,
    List<Action> actions,
    ConfigRepository repository
  ) {
    super(id, name, ownerId, url, value, actions);
    this.repository = repository;
  }

  public void save() {
    Optional<ConfigValue> existing = repository.find(getOwnerId(), getName());
    existing.ifPresentOrElse(repository::update, () -> repository.save(this));
  }
}
