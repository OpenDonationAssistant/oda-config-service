package io.github.opendonationassistant.config.listeners;

import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.config.ConfigRepository;
import io.github.opendonationassistant.config.ConfigValueAbstractFactory;
import io.github.opendonationassistant.config.values.ConfigValue;
import io.github.opendonationassistant.config.values.PaymentPageConfigValue;
import io.github.opendonationassistant.events.config.ConfigCommand;
import io.micronaut.messaging.annotation.MessageHeader;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import io.micronaut.serde.ObjectMapper;
import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RabbitListener
public class ConfigCommandListener {

  private final ODALogger log = new ODALogger(this);
  private final ConfigValueAbstractFactory factory;
  private final ConfigRepository repository;
  private final ObjectMapper mapper = ObjectMapper.getDefault();

  @Inject
  public ConfigCommandListener(
    ConfigValueAbstractFactory factory,
    ConfigRepository repository
  ) {
    this.factory = factory;
    this.repository = repository;
  }

  @Queue(io.github.opendonationassistant.rabbit.Queue.Commands.CONFIG)
  public void listen(byte[] command, @Nullable @MessageHeader String type)
    throws IOException {
    if (type == null) {
      handlePutCommand(
        mapper.readValue(command, ConfigCommand.PutKeyValue.class)
      ); // TODO переделать всех на PutKeyValue
    }
    switch (type) {
      case "UpsertAction" -> {
        handleAddActionCommand(
          mapper.readValue(command, ConfigCommand.UpsertAction.class)
        );
      }
      case "DeleteAction" -> {
        handleDeleteActionCommand(
          mapper.readValue(command, ConfigCommand.DeleteAction.class)
        );
      }
      case "PutKeyValue" -> {
        handlePutCommand(
          mapper.readValue(command, ConfigCommand.PutKeyValue.class)
        );
      }
      default -> {
        handlePutCommand(
          mapper.readValue(command, ConfigCommand.PutKeyValue.class)
        ); // TODO переделать всех на PutKeyValue
      }
    }
  }

  private void handleDeleteActionCommand(ConfigCommand.DeleteAction command) {
    log.info("Received ConfigListUpsertCommand", Map.of("command", command));
    Optional<ConfigValue> config = factory.findExisting(
      command.recipientId(),
      "paymentpage"
    );
    if (config.isEmpty()) {
      log.info(
        "PaymentPageConfig not found",
        Map.of("recipientId", command.recipientId())
      );
      return;
    }
    config
      .map(conf -> (PaymentPageConfigValue) conf)
      .ifPresent(conf -> {
        conf.removeAction(command.id());
        conf.save();
      });
  }

  private void handleAddActionCommand(ConfigCommand.UpsertAction command) {
    log.info("Received ConfigListUpsertCommand", Map.of("command", command));
    Optional<ConfigValue> config = factory.findExisting(
      command.recipientId(),
      "paymentpage"
    );
    if (config.isEmpty()) {
      log.info(
        "PaymentPageConfig not found",
        Map.of("recipientId", command.recipientId())
      );
      return;
    }
    config
      .map(conf -> (PaymentPageConfigValue) conf)
      .ifPresent(conf -> {
        conf.upsertAction(
          new PaymentPageConfigValue.Action(
            command.id(),
            command.name(),
            command.price(),
            command.category(),
            command.game()
          )
        );
        conf.save();
      });
  }

  private void handlePutCommand(ConfigCommand.PutKeyValue command) {
    log.info("Received ConfigPutCommand", Map.of("command", command));
    Optional<ConfigValue> config = factory.findExisting(
      command.ownerId(),
      command.name()
    );
    config.ifPresent(conf -> {
      Map<String, Object> values = conf.getValue();
      values.put(command.key(), command.value());
      conf.setValue(values);
      log.debug("Updated config", Map.of("config", conf));
      repository.update(conf);
    });
  }
}
