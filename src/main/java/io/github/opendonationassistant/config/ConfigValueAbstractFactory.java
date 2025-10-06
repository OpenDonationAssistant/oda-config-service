package io.github.opendonationassistant.config;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.config.values.ConfigValue;
import io.github.opendonationassistant.config.values.PaymentPageConfigValue;
import io.github.opendonationassistant.config.values.WidgetsConfigValue;
import jakarta.annotation.Nonnull;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Singleton
public class ConfigValueAbstractFactory {

  private final ConfigRepository repository;

  @Inject
  public ConfigValueAbstractFactory(@Nonnull ConfigRepository repository) {
    Objects.requireNonNull(repository, "ConfigRepository should be configured");
    this.repository = repository;
  }

  public @Nonnull Optional<ConfigValue> findExisting(
    @Nonnull String ownerId,
    @Nonnull String name
  ) {
    return repository.find(ownerId, name).map(this::construct);
  }

  public @Nonnull Optional<ConfigValue> findByUrl(
    @Nonnull String name,
    @Nonnull String url
  ) {
    return repository.findByNameAndUrl(name, url).map(this::construct);
  }

  public @Nonnull ConfigValue create(
    @Nonnull String name,
    @Nonnull String ownerId,
    @Nonnull String url,
    @Nonnull Map<String, Object> value
  ) {
    var id = Generators.timeBasedEpochGenerator().generate().toString();
    return switch (name) {
      case "paymentpage" -> new PaymentPageConfigValue(
        id,
        ownerId,
        url.isBlank() ? "%s.oda.digital".formatted(ownerId) : url,
        value,
        List.of(),
        repository
      );
      case "widgets" -> new WidgetsConfigValue(
        id,
        ownerId,
        url,
        value,
        repository
      );
      default -> new ConfigValue(id, name, ownerId, url, value, List.of());
    };
  }

  private ConfigValue construct(ConfigValue widget) {
    switch (widget.getName()) {
      case "widgets":
        return new WidgetsConfigValue(
          widget.getId(),
          widget.getOwnerId(),
          widget.getUrl(),
          widget.getValue(),
          repository
        );
      case "paymentpage":
        return new PaymentPageConfigValue(
          widget.getId(),
          widget.getOwnerId(),
          widget.getUrl(),
          widget.getValue(),
          widget.getActions(),
          repository
        );
      default:
        return widget;
    }
  }
}
