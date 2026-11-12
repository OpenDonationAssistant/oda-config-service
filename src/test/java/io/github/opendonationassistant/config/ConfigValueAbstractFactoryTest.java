package io.github.opendonationassistant.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.config.values.ConfigValue;
import io.github.opendonationassistant.config.values.PaymentPageConfigValue;
import io.github.opendonationassistant.config.values.WidgetsConfigValue;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ConfigValueAbstractFactoryTest {

  ConfigRepository mockRepository = mock(ConfigRepository.class);

  @Test
  public void testReturnStoredValue() {
    var config = new ConfigValue(
      "id",
      "testname",
      "testuser",
      "url",
      Map.of("testkey", "testvalue"),
      List.of()
    );
    when(mockRepository.find(Mockito.any(), Mockito.any())).thenReturn(
      Optional.of(config)
    );
    var factory = new ConfigValueAbstractFactory(mockRepository);
    var expected = Optional.of(config);
    assertEquals(expected, factory.findExisting("testuser", "testname"));
    verify(mockRepository).find("testuser", "testname");
  }

  @Test
  public void testReturnPaymentPageConfigValue() {
    var config = new ConfigValue(
      "id",
      "paymentpage",
      "owner",
      "url",
      Map.of(),
      List.of()
    );
    when(mockRepository.find(Mockito.any(), Mockito.any())).thenReturn(
      Optional.of(config)
    );
    var factory = new ConfigValueAbstractFactory(mockRepository);
    Optional<ConfigValue> actual = factory.findExisting(
      "testuser",
      "paymentpage"
    );
    assertTrue(actual.isPresent());
    assertEquals(PaymentPageConfigValue.class, actual.get().getClass());
  }

  @Test
  public void testReturnEmptyIfNotFound() {
    when(mockRepository.find(Mockito.any(), Mockito.any())).thenReturn(
      Optional.empty()
    );
    var factory = new ConfigValueAbstractFactory(mockRepository);
    Optional<ConfigValue> actual = factory.findExisting(
      "testuser",
      "paymentpage"
    );
    assertTrue(actual.isEmpty());
  }

  @Test
  public void testCreatingConfig() {
    var factory = new ConfigValueAbstractFactory(mockRepository);
    var widgetConfig = factory.create("widgets", "testuser", "", Map.of());
    assertTrue(widgetConfig instanceof WidgetsConfigValue);
    assertEquals("", widgetConfig.getUrl());
    var paymentConfig = factory.create("paymentpage", "testuser", "", Map.of());
    assertTrue(paymentConfig instanceof PaymentPageConfigValue);
    assertEquals("testuser.oda.digital", paymentConfig.getUrl());
    var otherConfig = factory.create("other", "testuser", "", Map.of());
    assertTrue(otherConfig instanceof ConfigValue);
  }

  @Test
  public void testCreatingPaymentPageWithoutCasting() {
    var factory = new ConfigValueAbstractFactory(mockRepository);

    PaymentPageConfigValue paymentPage = factory.createPaymentPage(
      "testuser",
      "",
      Map.of()
    );

    assertEquals("testuser.oda.digital", paymentPage.getUrl());
    assertEquals("paymentpage", paymentPage.getName());
    assertEquals("testuser", paymentPage.getOwnerId());
    assertTrue(paymentPage.getId() != null && !paymentPage.getId().isBlank());
  }

  @Test
  public void testMergingSavedWidgetConfigWithDefaultValues() {
    Map<String, Object> configValues = Map.of(
      "topic",
      Map.of("alerts", "sometestvalue")
    );
    when(mockRepository.find(Mockito.any(), Mockito.any())).thenReturn(
      Optional.of(
        new ConfigValue(
          "id",
          "widgets",
          "testuser",
          "url",
          configValues,
          List.of()
        )
      )
    );
    var factory = new ConfigValueAbstractFactory(mockRepository);

    Optional<ConfigValue> config = factory.findExisting("testuser", "widgets");

    assertTrue(config.isPresent());
    ConfigValue fact = config.get();
    assertEquals(
      "sometestvalue",
      ((Map<String, Object>) fact
          .getValue()
          .getOrDefault("topic", Map.<String, Object>of())).get("alerts")
    );
  }

  @Test
  public void testUsingStoredLoglevel() {
    Map<String, Object> configValues = Map.of("loglevel", "info");
    when(mockRepository.find(Mockito.any(), Mockito.any())).thenReturn(
      Optional.of(
        new ConfigValue(
          "id",
          "widgets",
          "testuser",
          "url",
          configValues,
          List.of()
        )
      )
    );
    var factory = new ConfigValueAbstractFactory(mockRepository);

    Optional<ConfigValue> config = factory.findExisting("testuser", "widgets");

    assertTrue(config.isPresent());
    ConfigValue fact = config.get();
    assertEquals("info", fact.getValue().get("loglevel"));
  }
}
