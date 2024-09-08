package com.dev.profile.utility;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GenericConverter {

    // Generic method to convert an entity to a DTO
    public static <E, D> D convertToDto(E entity, Function<E, D> entityToDtoMapper) {
        return entityToDtoMapper.apply(entity);
    }

    // Generic method to convert a DTO to an entity
    public static <D, E> E convertToEntity(D dto, Function<D, E> dtoToEntityMapper) {
        return dtoToEntityMapper.apply(dto);
    }

    // Generic method to convert a Set of entities to a Set of DTOs
    public static <E, D> Set<D> convertToDtoSet(Set<E> entities, Function<E, D> entityToDtoMapper) {
        return entities.stream().map(entityToDtoMapper).collect(Collectors.toSet());
    }

    // Generic method to convert a Set of DTOs to a Set of entities
    public static <D, E> Set<E> convertToEntitySet(Set<D> dtos, Function<D, E> dtoToEntityMapper) {
        return dtos.stream().map(dtoToEntityMapper).collect(Collectors.toSet());
    }

}
