package io.github.stcarolas.oda.config;

import java.io.IOException;
import java.util.HashMap;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import io.micronaut.rabbitmq.connect.ChannelInitializer;
import jakarta.inject.Singleton;

@Singleton
public class RabbitConfiguration extends ChannelInitializer {

  public final static String COMMANDS_QUEUE_NAME = "commands.config";

  @Override
  public void initialize(Channel channel, String name) throws IOException {
        channel.exchangeDeclare("commands", BuiltinExchangeType.TOPIC);
        channel.queueDeclare(COMMANDS_QUEUE_NAME, true, false, false, new HashMap<>());
        channel.queueBind(COMMANDS_QUEUE_NAME, "commands", "config");
  }
}
