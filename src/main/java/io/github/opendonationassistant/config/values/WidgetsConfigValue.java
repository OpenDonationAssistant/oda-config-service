package io.github.opendonationassistant.config.values;

import io.github.opendonationassistant.config.ConfigRepository;
import io.micronaut.core.util.StringUtils;
import io.micronaut.serde.annotation.Serdeable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Serdeable
public class WidgetsConfigValue extends SaveableConfigValue {

  public WidgetsConfigValue(
    String id,
    String ownerId,
    String url,
    java.util.Map<String, Object> values,
    ConfigRepository repository
  ) {
    super(id, "widgets", ownerId, url, values, List.of(), repository);
    if (values == null) {
      values = new HashMap<>();
    }

    var topicValues = new HashMap(
      (Map<String, Object>) values.getOrDefault("topic", new HashMap<>())
    );

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
    defaultValues.put("reel", "/topic/%sreel".formatted(ownerId));
    defaultValues.put("goal", "/topic/%sgoal".formatted(ownerId));
    defaultValues.put(
      "donaterstoplist",
      "/topic/%sdonaterstoplist".formatted(ownerId)
    );
    defaultValues.put("variables", "/topic/%svariables".formatted(ownerId));

    return defaultValues;
  }
}
