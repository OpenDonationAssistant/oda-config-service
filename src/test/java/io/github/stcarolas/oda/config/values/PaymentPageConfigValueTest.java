package io.github.stcarolas.oda.config.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.github.stcarolas.oda.config.ConfigRepository;
import io.micronaut.core.util.StringUtils;
import io.micronaut.serde.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class PaymentPageConfigValueTest {

  private final String DEFAULT_VALUE =
    """
          {
            "fio":"Иванов Иван Иванович",
            "inn":"1122334455",
            "email":"test@mail.com",
            "nickname":"ownerId",
            "media.requests.cost": 100,
            "media.requests.enabled": true,
            "url":[
              { "twitch":"https://twitch.tv/ownerId" }
            ],
            "media.requests.disabled.permanently": true,
            "minimalAmount":40,
            "payButtonText": "Задонатить <amount>",
            "gateway":"yookassa",
            "customCss": ""
          }
    """;

  @Test
  public void testCreatingWithDefaultValues() throws IOException {
    var repository = mock(ConfigRepository.class);
    var expected = ObjectMapper
      .getDefault()
      .readValue(DEFAULT_VALUE, Map.class);
    var actual = new PaymentPageConfigValue(
      null,
      "ownerId",
      new HashMap<>(),
      repository
    );
    assertTrue(StringUtils.isNotEmpty(actual.getId()));
    assertEquals(expected, actual.getValue());
  }

  @Test
  public void testUsingSavedValues() throws IOException {
    var repository = mock(ConfigRepository.class);
    var savedValues = ObjectMapper
      .getDefault()
      .readValue(DEFAULT_VALUE, Map.class);
    var actual = new PaymentPageConfigValue(
      null,
      "ownerId",
      savedValues,
      repository
    )
      .getValue();
    assertEquals(savedValues, actual);
  }
}
