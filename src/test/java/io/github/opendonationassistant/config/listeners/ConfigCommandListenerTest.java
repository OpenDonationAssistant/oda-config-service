package io.github.opendonationassistant.config.listeners;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.config.ConfigRepository;
import io.github.opendonationassistant.config.ConfigValueAbstractFactory;
import io.github.opendonationassistant.config.values.ConfigValue.Action;
import io.github.opendonationassistant.config.values.PaymentPageConfigValue;
import io.github.opendonationassistant.events.config.ConfigCommand;
import io.micronaut.serde.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigCommandListenerTest {

  private final ObjectMapper mapper = ObjectMapper.getDefault();
  private final ConfigValueAbstractFactory factory = mock(
    ConfigValueAbstractFactory.class
  );
  private final ConfigRepository repository = mock(ConfigRepository.class);

  private ConfigCommandListener listener;

  @BeforeEach
  void setUp() {
    listener = new ConfigCommandListener(factory, repository);
  }

  private PaymentPageConfigValue existingPaymentPage() {
    return new PaymentPageConfigValue(
      "id",
      "owner",
      "url",
      new HashMap<>(),
      List.of(),
      repository
    );
  }

  @Test
  public void testPutKeyValueWithTypeHeader() throws IOException {
    var config = existingPaymentPage();
    when(factory.findExisting("owner", "paymentpage")).thenReturn(
      Optional.of(config)
    );

    listener.listen(
      mapper.writeValueAsBytes(
        new ConfigCommand.PutKeyValue(
          "owner",
          "paymentpage",
          "gateway",
          "sberbank"
        )
      ),
      "PutKeyValue"
    );

    assertEquals("sberbank", config.getValue().get("gateway"));
    verify(repository).update(config);
  }

  @Test
  public void testPutKeyValueWithoutTypeHeader() throws IOException {
    var config = existingPaymentPage();
    when(factory.findExisting("owner", "paymentpage")).thenReturn(
      Optional.of(config)
    );

    listener.listen(
      mapper.writeValueAsBytes(
        new ConfigCommand.PutKeyValue(
          "owner",
          "paymentpage",
          "customKey",
          "customValue"
        )
      ),
      null
    );

    assertEquals("customValue", config.getValue().get("customKey"));
    verify(repository).update(config);
  }

  @Test
  public void testUnknownTypeFallsBackToPutKeyValue() throws IOException {
    var config = existingPaymentPage();
    when(factory.findExisting("owner", "paymentpage")).thenReturn(
      Optional.of(config)
    );

    listener.listen(
      mapper.writeValueAsBytes(
        new ConfigCommand.PutKeyValue(
          "owner",
          "paymentpage",
          "customKey",
          "customValue"
        )
      ),
      "SomeFutureCommand"
    );

    assertEquals("customValue", config.getValue().get("customKey"));
    verify(repository).update(config);
  }

  @Test
  public void testPutKeyValueForMissingConfigIsIgnored() throws IOException {
    when(factory.findExisting(any(), any())).thenReturn(Optional.empty());

    listener.listen(
      mapper.writeValueAsBytes(
        new ConfigCommand.PutKeyValue("owner", "paymentpage", "k", "v")
      ),
      "PutKeyValue"
    );

    verify(repository, never()).update(any());
  }

  @Test
  public void testUpsertAction() throws IOException {
    var config = existingPaymentPage();
    when(factory.findExisting("owner", "paymentpage")).thenReturn(
      Optional.of(config)
    );

    listener.listen(
      mapper.writeValueAsBytes(
        new ConfigCommand.UpsertAction(
          "owner",
          "actionId",
          "actionName",
          new Amount(100, 0, "RUB"),
          "category",
          "game",
          Map.of()
        )
      ),
      "UpsertAction"
    );

    assertEquals(1, config.getActions().size());
    assertEquals("actionId", config.getActions().get(0).id());
    verify(repository).save(config);
  }

  @Test
  public void testDeleteAction() throws IOException {
    var action = new Action(
      "actionId",
      "actionName",
      new Amount(100, 0, "RUB"),
      "category",
      "game"
    );
    var config = new PaymentPageConfigValue(
      "id",
      "owner",
      "url",
      new HashMap<>(),
      List.of(action),
      repository
    );
    when(factory.findExisting("owner", "paymentpage")).thenReturn(
      Optional.of(config)
    );

    listener.listen(
      mapper.writeValueAsBytes(
        new ConfigCommand.DeleteAction("owner", "actionId")
      ),
      "DeleteAction"
    );

    assertTrue(config.getActions().isEmpty());
    verify(repository).save(config);
  }
}
