package io.github.opendonationassistant.config;

import io.github.opendonationassistant.config.values.ConfigValue;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ConfigRepository extends CrudRepository<ConfigValue, String> {
  abstract Optional<ConfigValue> find(String ownerId, String name);
  abstract Optional<ConfigValue> findByNameAndUrl(String name, String url);
}
