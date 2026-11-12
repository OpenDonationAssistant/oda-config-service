package io.github.opendonationassistant.config.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import io.github.opendonationassistant.config.ConfigRepository;
import java.util.Map;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NullAway")
public class WidgetsConfigValueTest {

  private final ConfigRepository repository = mock(ConfigRepository.class);

  @Test
  public void testFillingDefaultTopics() {
    var config = new WidgetsConfigValue(
      "id",
      "testuser",
      "url",
      Map.of(),
      repository
    );

    var value = config.getValue();
    var topic = asMap(value.get("topic"));

    assertEquals("error", value.get("loglevel"));
    assertEquals("/topic/testuseralerts", topic.get("alerts"));
    assertEquals("/topic/testuserplayer", topic.get("player"));
    assertEquals("/topic/testuser.events", topic.get("events"));
    assertEquals("/topic/testuser.streamelements", topic.get("streamelements"));
    assertEquals(17, topic.size());
  }

  @Test
  public void testKeepingSavedTopicValues() {
    var saved = Map.<String, Object>of(
      "topic",
      Map.of("alerts", "custom"),
      "loglevel",
      "info"
    );

    var config = new WidgetsConfigValue(
      "id",
      "testuser",
      "url",
      saved,
      repository
    );

    var topic = asMap(config.getValue().get("topic"));

    assertEquals("custom", topic.get("alerts"));
    assertEquals("/topic/testuserplayer", topic.get("player"));
    assertEquals("info", config.getValue().get("loglevel"));
  }

  @Test
  public void testHandlingNullValues() {
    var config = new WidgetsConfigValue(
      "id",
      "testuser",
      "url",
      null,
      repository
    );

    assertNotNull(config.getValue());
    assertEquals(
      "/topic/testuseralerts",
      asMap(config.getValue().get("topic")).get("alerts")
    );
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> asMap(Object value) {
    return (Map<String, Object>) value;
  }
}
