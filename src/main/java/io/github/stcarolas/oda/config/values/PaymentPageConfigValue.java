package io.github.stcarolas.oda.config.values;

import com.fasterxml.uuid.Generators;
import io.github.stcarolas.oda.config.ConfigRepository;
import io.github.stcarolas.oda.config.SaveableConfigValue;
import io.micronaut.core.util.StringUtils;
import java.util.HashMap;
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

    Map<String, Object> merged = defaultValues();
    if (values != null) {
      values.forEach((key, value) -> merged.put(key, value));
    }
    this.setValue(merged);
  }

  public Map<String, Object> defaultValues() {
    HashMap<String, Object> values = new HashMap<String, Object>();
    values.put("fio", "Иванов Иван Иванович");
    values.put("inn", "1122334455");
    values.put("email", "test@mail.com");
    values.put("nickname", "testuser");
    values.put("media.requests.cost", 100);
    values.put("media.requests.enabled", false);
    values.put("media.requests.disabled.permanently", true);
    values.put("minimalAmount", 40);
    return values;
  }
}
