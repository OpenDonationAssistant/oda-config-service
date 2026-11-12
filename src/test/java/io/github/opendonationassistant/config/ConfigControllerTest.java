package io.github.opendonationassistant.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.config.values.ConfigValue;
import io.github.opendonationassistant.config.values.WidgetsConfigValue;
import io.micronaut.http.HttpStatus;
import io.micronaut.security.authentication.Authentication;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NullAway")
public class ConfigControllerTest {

  private final ConfigRepository repository = mock(ConfigRepository.class);
  private final ConfigValueAbstractFactory factory = mock(
    ConfigValueAbstractFactory.class
  );

  private ConfigController controller;
  private Authentication alice;
  private Authentication withoutOwner;

  @BeforeEach
  void setUp() {
    controller = new ConfigController(repository, factory);
    alice = mock(Authentication.class);
    when(alice.getAttributes()).thenReturn(
      Map.of("preferred_username", "alice")
    );
    withoutOwner = mock(Authentication.class);
    when(withoutOwner.getAttributes()).thenReturn(Map.of());
  }

  private ConfigValue config(String id, String name, String ownerId) {
    return new ConfigValue(id, name, ownerId, "url", Map.of(), List.of());
  }

  // --- get ---

  @Test
  public void testGetRequiresAuthForNonPaymentPage() {
    var response = controller.get("widgets", "", "", null);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
  }

  @Test
  public void testGetReturnsExistingConfigForOwner() {
    var existing = config("id", "paymentpage", "alice");
    when(factory.findExisting("alice", "paymentpage")).thenReturn(
      Optional.of(existing)
    );

    var response = controller.get("paymentpage", "", "", alice);

    assertEquals(HttpStatus.OK, response.getStatus());
    assertEquals(existing, response.body());
  }

  @Test
  public void testGetFallsBackToUrlLookupForAnonymous() {
    var existing = config("id", "paymentpage", "bob");
    when(factory.findExisting(any(), any())).thenReturn(Optional.empty());
    when(factory.findByUrl("paymentpage", "https://x")).thenReturn(
      Optional.of(existing)
    );

    var response = controller.get("paymentpage", "", "https://x", null);

    assertEquals(HttpStatus.OK, response.getStatus());
    assertEquals(existing, response.body());
  }

  @Test
  public void testGetReturnsNotFoundWhenNothingMatches() {
    when(factory.findExisting(any(), any())).thenReturn(Optional.empty());
    when(factory.findByUrl(any(), any())).thenReturn(Optional.empty());

    var response = controller.get("paymentpage", "", "", null);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
  }

  @Test
  public void testGetWidgetsReturnsSyntheticConfigForOwner() {
    var response = controller.get("widgets", "", "", alice);

    assertEquals(HttpStatus.OK, response.getStatus());
    assertTrue(response.body() instanceof WidgetsConfigValue);
    assertEquals("alice", response.body().getOwnerId());
  }

  // --- put ---

  @Test
  public void testPutAssignsAuthenticatedOwnerAndSaves() {
    var body = config("ignored", "paymentpage", "someone-else");

    var response = controller.put(body, alice);

    assertEquals(HttpStatus.OK, response.getStatus());
    assertEquals("alice", body.getOwnerId());
    assertNotNull(body.getId());
    verify(repository).save(body);
  }

  @Test
  public void testPutUnauthorizedWhenOwnerCannotBeResolved() {
    var response = controller.put(
      config("id", "paymentpage", "x"),
      withoutOwner
    );

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    verify(repository, never()).save(any());
  }

  // --- update ---

  @Test
  public void testUpdateRejectsConfigOwnedByAnotherUser() {
    when(repository.findById("id")).thenReturn(
      Optional.of(config("id", "paymentpage", "bob"))
    );

    var response = controller.update(
      "id",
      config("id", "paymentpage", "alice"),
      alice
    );

    assertEquals(HttpStatus.FORBIDDEN, response.getStatus());
    verify(repository, never()).update(any());
    verify(repository, never()).save(any());
  }

  @Test
  public void testUpdateIgnoresBodyOwnerForOwnedConfig() {
    when(repository.findById("id")).thenReturn(
      Optional.of(config("id", "paymentpage", "alice"))
    );
    var body = config("id", "paymentpage", "bogus");

    var response = controller.update("id", body, alice);

    assertEquals(HttpStatus.OK, response.getStatus());
    assertEquals("alice", body.getOwnerId());
    verify(repository).update(body);
    verify(repository, never()).save(any());
  }

  @Test
  public void testUpdateCreatesConfigWhenMissing() {
    when(repository.findById("id")).thenReturn(Optional.empty());
    var body = config("id", "paymentpage", "bogus");

    var response = controller.update("id", body, alice);

    assertEquals(HttpStatus.OK, response.getStatus());
    assertEquals("alice", body.getOwnerId());
    verify(repository).save(body);
    verify(repository, never()).update(any());
  }

  @Test
  public void testUpdateUnauthorizedWhenOwnerCannotBeResolved() {
    var response = controller.update(
      "id",
      config("id", "paymentpage", "x"),
      withoutOwner
    );

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    verify(repository, never()).findById(any());
  }
}
