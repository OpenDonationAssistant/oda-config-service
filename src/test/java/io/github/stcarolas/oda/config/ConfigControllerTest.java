package io.github.stcarolas.oda.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.micronaut.http.HttpResponse;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MicronautTest(environments = "allinone")
public class ConfigControllerTest {

  Logger log = LoggerFactory.getLogger(ConfigControllerTest.class);

  @Test
  public void testPutAndGetConfigValue(ConfigController controller) throws IOException {
    ConfigValue config = new ConfigValue();
    config.setName("widgets");
    config.setValue(Map.of("testkey", "testvalue"));
    controller.put(config, auth());
    var expectedValue = ObjectMapper.getDefault().readValue("""
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
          "remoteplayer": "/topic/testuserremoteplayer"
        },
        "testkey":"testvalue"
      }
      """, Map.class);
    HttpResponse<ConfigValue> result = controller.get(
      "widgets",
      "testuser",
      auth()
    );
    assertEquals("widgets", result.getBody().get().getName());
    assertEquals("testuser", result.getBody().get().getOwnerId());
    assertEquals(expectedValue, result.getBody().get().getValue());
  }

  private Authentication auth() {
    Authentication auth = mock(Authentication.class);
    when(auth.getAttributes())
      .thenReturn(Map.of("preferred_username", "testuser"));
    return auth;
  }
}
