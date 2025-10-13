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

@Controller
public class ChangeAgreement extends BaseController {

  private final ConfigCommandSender configCommandSender;

  @Inject
  public ChangeAgreement(ConfigCommandSender configCommandSender) {
    this.configCommandSender = configCommandSender;
  }

  @Post("/config/commands/change-agreement")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<Void> changeAgreement(
    Authentication auth,
    @Body ChangeAgreementCommand command
  ) {
    var ownerId = getOwnerId(auth);
    if (ownerId.isEmpty()) {
      return HttpResponse.unauthorized();
    }
    configCommandSender.send(
      new PutKeyValue(
        ownerId.get(),
        "paymentpage",
        "gateway",
        command.gateway()
      )
    );
    return HttpResponse.ok();
  }

  @Serdeable
  public static record ChangeAgreementCommand(String gateway) {}
}
