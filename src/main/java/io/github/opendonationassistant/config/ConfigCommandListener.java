package io.github.opendonationassistant.config;

import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.config.values.ConfigValue;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.Optional;

@RabbitListener
public class ConfigCommandListener {

  private final ODALogger log = new ODALogger(this);
  private final ConfigValueAbstractFactory factory;
  private final ConfigRepository repository;

  @Inject
  public ConfigCommandListener(
    ConfigValueAbstractFactory factory,
    ConfigRepository repository
  ) {
    this.factory = factory;
    this.repository = repository;
  }

  @Queue(io.github.opendonationassistant.rabbit.Queue.Commands.CONFIG)
  public void listen(ConfigPutCommand command) {
    log.info("Received ConfigPutCommand", Map.of("command", command));
    Optional<ConfigValue> config = factory.findExisting(
      command.getOwnerId(),
      command.getName()
    );
    config.ifPresent(conf -> {
      Map<String, Object> values = conf.getValue();
      values.put(command.getKey(), command.getValue());
      conf.setValue(values);
      log.debug("Updated config", Map.of("config", conf));
      repository.update(conf);
    });
  }
}
