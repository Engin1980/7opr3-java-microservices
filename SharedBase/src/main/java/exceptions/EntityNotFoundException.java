package exceptions;

import lombok.Getter;

public class EntityNotFoundException extends RuntimeException {
  @Getter
  private final Class<?> entityType;
  @Getter
  private final int id;

  public <T> EntityNotFoundException(Class<T> entityType, int id) {
    super("Entity of type " + entityType.getName() + " with id " + id + " not found");

    this.entityType = entityType;
    this.id = id;
  }
}
