package io.github.opendonationassistant.config.commands;

import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.config.ConfigValueAbstractFactory;
import io.github.opendonationassistant.config.values.PaymentPageConfigValue;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;

@Controller
public class CreateConfig extends BaseController {

  private ConfigValueAbstractFactory factory;

  @Inject
  public CreateConfig(ConfigValueAbstractFactory factory) {
    this.factory = factory;
  }

  @Post("/config/commands/create-config")
  @Secured(SecurityRule.IS_ANONYMOUS)
  public HttpResponse<Void> createConfig(@Body CreateConfigCommand command) {
    ((PaymentPageConfigValue) factory.create(
        "paymentpage",
        command.recipientId(),
        command.url(),
        Map.of(
          "nickname",
          command.displayName(),
          "url",
          command
            .socialLinks()
            .stream()
            .map(link -> Map.of(link.platform(), link.url()))
            .toList()
        )
      )).save();
    return HttpResponse.ok();
  }

  @Serdeable
  public record CreateConfigCommand(
    String recipientId,
    String url,
    String displayName,
    List<SocialLink> socialLinks
  ) {}

  @Serdeable
  public record SocialLink(String platform, String url) {}
}
