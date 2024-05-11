package io.github.stcarolas.oda.config;

import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RabbitListener
public class ConfigCommandListener {

  private Logger log = LoggerFactory.getLogger(ConfigCommandListener.class);

  private final ConfigValueAbstractFactory factory;
  private final ConfigRepository repository;

  public ConfigCommandListener(
    ConfigValueAbstractFactory factory,
    ConfigRepository repository
  ) {
    this.factory = factory;
    this.repository = repository;
  }

  @Queue(RabbitConfiguration.COMMANDS_QUEUE_NAME)
  public void listen(ConfigPutCommand command) {
    log.info("Received command: {}", command);
    Optional<ConfigValue> config = factory.findExisting(
      command.getOwnerId(),
      command.getName()
    );
    config.ifPresent(conf -> {
      Map<String, Object> values = conf.getValue();
      values.put(command.getKey(), command.getValue());
      conf.setValue(values);
      log.info("Updated config:{}", conf);
      repository.update(conf);
    });
  }
}
