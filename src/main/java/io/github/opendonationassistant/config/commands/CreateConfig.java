package io.github.opendonationassistant.config.commands;

import io.github.opendonationassistant.commons.micronaut.BaseController;
import io.github.opendonationassistant.config.ConfigValueAbstractFactory;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
  public HttpResponse<Void> createConfig(
    @Valid @Body CreateConfigCommand command
  ) {
    factory
      .createPaymentPage(
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
      )
      .save();
    return HttpResponse.ok();
  }

  @Serdeable
  public record CreateConfigCommand(
    @NotBlank String recipientId,
    @NotBlank String url,
    @NotBlank String displayName,
    @NotNull List<SocialLink> socialLinks
  ) {}

  @Serdeable
  public record SocialLink(@NotBlank String platform, @NotBlank String url) {}
}
