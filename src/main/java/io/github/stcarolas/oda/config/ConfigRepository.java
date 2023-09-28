package io.github.stcarolas.oda.config;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ConfigRepository extends CrudRepository<ConfigValue, String> {
  abstract ConfigValue find(String ownerId, String name);
}
