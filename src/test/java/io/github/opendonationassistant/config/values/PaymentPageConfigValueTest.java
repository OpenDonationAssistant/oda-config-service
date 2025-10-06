package io.github.opendonationassistant.config.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.config.ConfigRepository;
import io.github.opendonationassistant.config.values.ConfigValue.Action;
import io.micronaut.core.util.StringUtils;
import io.micronaut.serde.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(InstancioExtension.class)
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
      List.of(),
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
      List.of(),
      repository
    ).getValue();
    assertEquals(savedValues, actual);
  }

  @Test
  public void testAddingNewAction(@Given Action action) {
    var repository = mock(ConfigRepository.class);
    var config = new PaymentPageConfigValue(
      "id",
      "ownerId",
      "url",
      new HashMap<>(),
      List.of(),
      repository
    );
    config.upsertAction(action);
    assertEquals(List.of(action), config.getActions());
  }

  @Test
  public void testUpdatingAction(@Given Action action) {
    var repository = mock(ConfigRepository.class);
    var config = new PaymentPageConfigValue(
      "id",
      "ownerId",
      "url",
      new HashMap<>(),
      List.of(action),
      repository
    );
    assertEquals(List.of(action), config.getActions());

    var updated = new Action(
      action.id(),
      "new name",
      new Amount(100, 0, "RUB"),
      "category",
      "new game"
    );
    config.upsertAction(updated);
    assertEquals(List.of(updated), config.getActions());
  }
}
