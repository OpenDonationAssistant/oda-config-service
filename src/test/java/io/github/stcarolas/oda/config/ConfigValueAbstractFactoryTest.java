package io.github.stcarolas.oda.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.stcarolas.oda.config.values.PaymentPageConfigValue;
import io.micronaut.serde.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ConfigValueAbstractFactoryTest {

  ConfigRepository mockRepository = mock(ConfigRepository.class);

  @Test
  public void testNotAcceptingNullAsRepository() {
    assertThrowsExactly(
      NullPointerException.class,
      () -> new ConfigValueAbstractFactory(null),
      "ConfigRepository should be configured"
    );
  }

  @Test
  public void testThrowExceptionForIncorrectArguments() {
    var factory = new ConfigValueAbstractFactory(mockRepository);
    assertThrowsExactly(
      NullPointerException.class,
      () -> factory.findExisting(null, "name"),
      "Missing ownerId to search for config"
    );
    assertThrowsExactly(
      NullPointerException.class,
      () -> factory.findExisting("ownerId", null),
      "Missing config's name to search for it"
    );
  }

  @Test
  public void testReturnStoredValue() {
    var config = new ConfigValue(
      "testname",
      "testuser",
      Map.of("testkey", "testvalue")
    );
    when(mockRepository.find(Mockito.any(), Mockito.any()))
      .thenReturn(Optional.of(config));
    var factory = new ConfigValueAbstractFactory(mockRepository);
    var expected = Optional.of(config);
    assertEquals(expected, factory.findExisting("testuser", "testname"));
    verify(mockRepository).find("testuser", "testname");
  }

  @Test
  public void testReturnPaymentPageConfigValue() {
    var config = new ConfigValue("paymentpage", "owner", Map.of());
    when(mockRepository.find(Mockito.any(), Mockito.any()))
      .thenReturn(Optional.of(config));
    var factory = new ConfigValueAbstractFactory(mockRepository);
    Optional<ConfigValue> actual = factory.findExisting(
      "testuser",
      "paymentpage"
    );
    assertTrue(actual.isPresent());
    assertEquals(PaymentPageConfigValue.class, actual.get().getClass());
  }

  @Test
  public void testReturnDefaultPaymentPageConfig() {
    when(mockRepository.find(Mockito.any(), Mockito.any()))
      .thenReturn(Optional.empty());
    var factory = new ConfigValueAbstractFactory(mockRepository);
    Optional<ConfigValue> actual = factory.findExisting(
      "testuser",
      "paymentpage"
    );
    assertTrue(actual.isPresent());
    assertEquals(PaymentPageConfigValue.class, actual.get().getClass());
  }

  // todo rewrite tests for widgets
  @Test
  public void testReturnDefaultValuesForWidgetsIfMissing() throws IOException {
    when(mockRepository.find(Mockito.any(), Mockito.any()))
      .thenReturn(Optional.empty());
    var factory = new ConfigValueAbstractFactory(mockRepository);

    Optional<ConfigValue> config = factory.findExisting("testuser", "widgets");

    assertTrue(config.isPresent());
    assertEquals("widgets", config.get().getName());
    assertEquals("testuser", config.get().getOwnerId());
    var expectedValue = ObjectMapper
      .getDefault()
      .readValue(
        """
        {
          "topic": {
            "alerts": "/topic/testuseralerts",
            "alertStatus": "/topic/testuseralertStatus",
            "alertWidgetCommans": "/topic/testuseralertWidgetCommands",
            "player": "/topic/testuserplayer",
            "playerCommands": "/topic/testuserplayerCommands",
            "media": "/topic/testusermedia",
            "paymentWidgetCommands": "/topic/testuserpaymentWidgetCommands",
            "mediaWidgetCommands": "/topic/testusermediaWidgetCommands",
            "remoteplayerfeedback": "/topic/testuserremoteplayerfeedback",
            "remoteplayer": "/topic/testuserremoteplayer",
            "reel": "/topic/testuserreel",
            "goal": "/topic/testusergoal",
            "donaterstoplist": "/topic/testuserdonaterstoplist"
          },
          "loglevel":"error"
        }
        """,
        Map.class
      );
    assertEquals(expectedValue, config.get().getValue());
  }

  @Test
  public void testMergingSavedWidgetConfigWithDefaultValues() {
    Map<String, Object> configValues = Map.of(
      "topic",
      Map.of("alerts", "sometestvalue")
    );
    when(mockRepository.find(Mockito.any(), Mockito.any()))
      .thenReturn(
        Optional.of(new ConfigValue("id", "widgets", "testuser", configValues))
      );
    var factory = new ConfigValueAbstractFactory(mockRepository);

    Optional<ConfigValue> config = factory.findExisting("testuser", "widgets");

    assertTrue(config.isPresent());
    ConfigValue fact = config.get();
    assertEquals(
      "sometestvalue",
      ((Map<String, Object>) fact.getValue().get("topic")).get("alerts")
    );
  }

  @Test
  public void testUsingStoredLoglevel() {
    Map<String, Object> configValues = Map.of("loglevel", "info");
    when(mockRepository.find(Mockito.any(), Mockito.any()))
      .thenReturn(
        Optional.of(new ConfigValue("id", "widgets", "testuser", configValues))
      );
    var factory = new ConfigValueAbstractFactory(mockRepository);

    Optional<ConfigValue> config = factory.findExisting("testuser", "widgets");

    assertTrue(config.isPresent());
    ConfigValue fact = config.get();
    assertEquals("info", fact.getValue().get("loglevel"));
  }
}
