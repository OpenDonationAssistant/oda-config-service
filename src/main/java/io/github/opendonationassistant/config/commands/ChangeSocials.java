package io.github.opendonationassistant.config.commands;

import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.events.config.ConfigCommand.PutKeyValue;
import io.github.opendonationassistant.events.config.ConfigCommandSender;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;

@Controller
public class ChangeSocials extends BaseController {

  private final ConfigCommandSender configCommandSender;

  @Inject
  public ChangeSocials(ConfigCommandSender configCommandSender) {
    this.configCommandSender = configCommandSender;
  }

  @Post("/config/commands/change-socials")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<Void> changeAgreement(
    Authentication auth,
    @Body ChangeSocialsCommand command
  ) {
    var owner = getOwnerId(auth);
    if (owner.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    configCommandSender.send(
      new PutKeyValue(
        owner.get(),
        "paymentpage",
        "url",
        List.of(command.socials())
      )
    );
    return HttpResponse.ok();
  }

  @Serdeable
  public static record ChangeSocialsCommand(
    String id,
    Map<String, String> socials
  ) {}
}
