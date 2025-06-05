package com.hivetech.kanban.util;

public interface DTOMapperUtil<T, E> {
    public T toDTO(E e);
}
