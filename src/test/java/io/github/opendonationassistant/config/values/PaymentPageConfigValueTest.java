package io.github.opendonationassistant.config.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.github.opendonationassistant.config.ConfigRepository;
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
            "limits.char": {"type":"fixed", "value":300},
            "media.requests.cost": 100,
            "media.requests.enabled": true,
            "media.requests.amount": 12,
            "media.requests.tooltip": "",
            "url":[
              { "twitch":"https://twitch.tv/ownerId" }
            ],
            "media.requests.disabled.permanently": true,
            "minimalAmount":40,
            "payButtonText": "Задонатить <amount>",
            "gateway":"yookassa",
            "streamer.description":"российский игровой стример. Эта страница для сбора средств на развитие и поддержку канала.",
            "customCss": ""
          }
    """;

  @Test
  public void testCreatingWithDefaultValues() throws IOException {
    var repository = mock(ConfigRepository.class);
    var expected = ObjectMapper.getDefault()
      .readValue(DEFAULT_VALUE, Map.class);
    var actual = new PaymentPageConfigValue(
      "id",
      "ownerId",
      "url",
      new HashMap<>(),
      repository
    );
    assertEquals(expected, actual.getValue());
    assertTrue(StringUtils.isNotEmpty(actual.getId()));
  }

  @Test
  public void testUsingSavedValues() throws IOException {
    var repository = mock(ConfigRepository.class);
    var savedValues = ObjectMapper.getDefault()
      .readValue(DEFAULT_VALUE, Map.class);
    var actual = new PaymentPageConfigValue(
      "id",
      "ownerId",
      "url",
      savedValues,
      repository
    ).getValue();
    assertEquals(savedValues, actual);
  }
}
