package io.github.opendonationassistant.config.values;

import io.github.opendonationassistant.config.ConfigRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentPageConfigValue extends SaveableConfigValue {

  public PaymentPageConfigValue(
    String id,
    String ownerId,
    String url,
    Map<String, Object> values,
    ConfigRepository repository
  ) {
    super(id, "paymentpage", ownerId, url, values, repository);
    this.setName("paymentpage");

    Map<String, Object> merged = defaultValues(ownerId);
    if (values != null) {
      values.forEach((key, value) -> merged.put(key, value));
    }
    this.setValue(merged);
  }

  public Map<String, Object> defaultValues(String ownerId) {
    HashMap<String, Object> values = new HashMap<String, Object>();
    values.put("fio", "Иванов Иван Иванович");
    values.put("inn", "1122334455");
    values.put("email", "test@mail.com");
    values.put("nickname", ownerId);
    values.put("media.requests.cost", 100);
    values.put("media.requests.enabled", true);
    values.put("media.requests.disabled.permanently", true);
    values.put("media.requests.amount", 12);
    values.put("media.requests.tooltip", "");
    values.put("minimalAmount", 40);
    values.put("payButtonText", "Задонатить <amount>");
    values.put("customCss", "");
    values.put("gateway", "yookassa");
    values.put(
      "streamer.description",
      "российский игровой стример. Эта страница для сбора средств на развитие и поддержку канала."
    );
    values.put(
      "url",
      List.of(Map.of("twitch", "https://twitch.tv/" + ownerId))
    );
    values.put("limits.char", charLimits());
    return values;
  }

  public Map<String, Object> charLimits() {
    HashMap<String, Object> values = new HashMap<String, Object>();
    values.put("type", "fixed");
    values.put("value", 300);
    return values;
  }
}
