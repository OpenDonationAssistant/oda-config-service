package io.github.stcarolas.oda.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
          "remoteplayer": "/topic/testuserremoteplayer",
          "reel": "/topic/testuserreel",
          "goal": "/topic/testusergoal",
          "donaterstoplist": "/topic/testuserdonaterstoplist"
        },
        "testkey":"testvalue",
        "loglevel":"error"
      }
      """, Map.class);
    HttpResponse<ConfigValue> result = controller.get(
      "widgets",
      "testuser",
      auth()
    );
    ConfigValue response = result.getBody().get();
    assertNotNull(response);
    Map<String, Object> factValue = response.getValue();

    assertNotNull(factValue);
    assertEquals("widgets", response.getName());
    assertEquals("testuser", response.getOwnerId());
    assertEquals(expectedValue, response.getValue());
  }

  private Authentication auth() {
    Authentication auth = mock(Authentication.class);
    when(auth.getAttributes())
      .thenReturn(Map.of("preferred_username", "testuser"));
    return auth;
  }
}
