package io.github.opendonationassistant.config.listeners;

import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.voting.VotingState;
import io.micronaut.messaging.annotation.MessageHeader;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import java.util.Map;

@RabbitListener
public class ConfigEventsListener {

  private final ODALogger log = new ODALogger(this);

  @Queue(io.github.opendonationassistant.rabbit.Queue.Configs.EVENTS)
  public void listenConfigEvents(@MessageHeader String type, byte[] message) {
    switch (type) {
      case "VotingState" -> {
      }
      default -> {
        log.error(
          "Received unknown config event",
          Map.of("type", type)
        );
      }
    }
  }

  private void handleVotingState(VotingState state) {
  }
}
