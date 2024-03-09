package io.github.stcarolas.oda.config.values;

import io.github.stcarolas.oda.config.ConfigRepository;
import io.github.stcarolas.oda.config.SaveableConfigValue;
import io.micronaut.core.util.StringUtils;
import io.micronaut.serde.annotation.Serdeable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.uuid.Generators;

@Serdeable
public class WidgetsConfigValue extends SaveableConfigValue {

  public WidgetsConfigValue(
    String id,
    String ownerId,
    java.util.Map<String, Object> values,
    ConfigRepository repository
  ) {
    super("widgets", ownerId, values, repository);
    this.setId(
        StringUtils.isEmpty(id)
          ? Generators.timeBasedEpochGenerator().generate().toString()
          : id
      );
    this.setName("widgets");

    if (values == null) {
      values = new HashMap<>();
    }

    var topicValues = new HashMap((Map<String, Object>) values.getOrDefault(
      "topic",
      new HashMap<>()
    ));

    var loglevel = (String) values.get("loglevel");

    defaultValues(ownerId)
      .entrySet()
      .stream()
      .forEach(entry -> {
        topicValues.putIfAbsent(entry.getKey(), entry.getValue());
      });
    var updated = new HashMap<>(values);
    updated.put("topic", topicValues);
    updated.put("loglevel", StringUtils.isEmpty(loglevel) ? "error" : loglevel);
    this.setValue(updated);
  }

  private Map<String, String> defaultValues(String ownerId) {
    Map<String, String> defaultValues = new HashMap<>();

    defaultValues.put("alerts", "/topic/%salerts".formatted(ownerId));
    defaultValues.put("alertStatus", "/topic/%salertStatus".formatted(ownerId));
    defaultValues.put(
      "alertWidgetCommans",
      "/topic/%salertWidgetCommands".formatted(ownerId)
    );
    defaultValues.put("player", "/topic/%splayer".formatted(ownerId));
    defaultValues.put(
      "playerCommands",
      "/topic/%splayerCommands".formatted(ownerId)
    );
    defaultValues.put("media", "/topic/%smedia".formatted(ownerId));
    defaultValues.put(
      "paymentWidgetCommands",
      "/topic/%spaymentWidgetCommands".formatted(ownerId)
    );
    defaultValues.put(
      "mediaWidgetCommands",
      "/topic/%smediaWidgetCommands".formatted(ownerId)
    );
    defaultValues.put(
      "remoteplayerfeedback",
      "/topic/%sremoteplayerfeedback".formatted(ownerId)
    );
    defaultValues.put(
      "remoteplayer",
      "/topic/%sremoteplayer".formatted(ownerId)
    );

    return defaultValues;
  }
}
