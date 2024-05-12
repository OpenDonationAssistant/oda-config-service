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

  @Test
  public void testCreatingWithDefaultValues() throws IOException {
    var repository = mock(ConfigRepository.class);
    var expected = ObjectMapper
      .getDefault()
      .readValue(
        """
        {
          "fio":"Иванов Иван Иванович",
          "inn":"1122334455",
          "email":"test@mail.com",
          "nickname":"testuser",
          "media.requests.cost": 100,
          "media.requests.enabled": false,
          "media.requests.disabled.permanently": true,
          "minimalAmount":40
        }
        """,
        Map.class
      );
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
      .readValue(
        """
        {
          "fio":"Смирнов Лебедь Александрович",
          "inn":"333",
          "email":"someemail",
          "nickname":"saved",
          "media.requests.cost": 200,
          "media.requests.enabled": true,
          "media.requests.disabled.permanently": false,
          "minimalAmount":100
        }
        """,
        Map.class
      );
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
