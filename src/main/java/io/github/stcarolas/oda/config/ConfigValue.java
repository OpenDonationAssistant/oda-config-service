package io.github.stcarolas.oda.config;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.model.DataType;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@MappedEntity("config")
public class ConfigValue {

  @Id
  private String id;

  @MappedProperty(value = "name")
  private String name;

  @MappedProperty(value = "owner_id")
  private String ownerId;

  @MappedProperty(type = DataType.JSON)
  private java.util.Map<String, Object> value;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }

  public java.util.Map<String, Object> getValue() {
    return value;
  }

  public void setValue(java.util.Map<String, Object> value) {
    this.value = value;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
