package io.github.stcarolas.oda.config.values;

import com.fasterxml.uuid.Generators;
import io.github.stcarolas.oda.config.ConfigRepository;
import io.github.stcarolas.oda.config.SaveableConfigValue;
import io.micronaut.core.util.StringUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentPageConfigValue extends SaveableConfigValue {

  public PaymentPageConfigValue(
    String id,
    String ownerId,
    Map<String, Object> values,
    ConfigRepository repository
  ) {
    super("paymentpage", ownerId, values, repository);
    this.setId(
        StringUtils.isEmpty(id)
          ? Generators.timeBasedEpochGenerator().generate().toString()
          : id
      );
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
    values.put("minimalAmount", 40);
    values.put("payButtonText", "Задонатить <amount>");
    values.put("customCss", "");
    values.put("gateway", "yookassa");
    values.put(
      "url",
      List.of(Map.of("twitch", "https://twitch.tv/" + ownerId))
    );
    return values;
  }
}
