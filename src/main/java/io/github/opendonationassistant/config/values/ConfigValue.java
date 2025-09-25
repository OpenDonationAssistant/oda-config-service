package io.github.opendonationassistant.config.values;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.model.DataType;
import io.micronaut.serde.annotation.Serdeable;
import java.util.Map;

@Serdeable
@MappedEntity("config")
public class ConfigValue {

  @Id
  private String id;

  private String name;

  @MappedProperty(value = "owner_id")
  private String ownerId;

  private String url;

  @MappedProperty(type = DataType.JSON)
  private java.util.Map<String, Object> value;

  public ConfigValue(
    String id,
    String name,
    String ownerId,
    String url,
    Map<String, Object> value
  ) {
    this.id = id;
    this.name = name;
    this.ownerId = ownerId;
    this.url = url;
    this.value = value;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

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

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public java.util.Map<String, Object> getValue() {
    return value;
  }

  public void setValue(java.util.Map<String, Object> value) {
    this.value = value;
  }
}
