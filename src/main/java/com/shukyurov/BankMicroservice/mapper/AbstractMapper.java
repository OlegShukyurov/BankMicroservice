package com.shukyurov.BankMicroservice.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractMapper <E, D> {

    @Autowired
    private ModelMapper modelMapper;

    private final Class<E> entityClass;
    private final Class<D> dtoClass;

    public AbstractMapper(Class<E> entityClass, Class<D> dtoClass) {
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }

    public abstract D toDto(E entity);

    public abstract E toEntity(D dto);

    protected abstract Class<E> getEntityClass();

    protected abstract Class<D> getDtoClass();

    protected TypeMap<E, D> createTypeMap() {
        return modelMapper.createTypeMap(getEntityClass(), getDtoClass());
    }

    protected TypeMap<D, E> createReverseTypeMap() {
        return modelMapper.createTypeMap(getDtoClass(), getEntityClass());
    }

    public TypeMap<E, D> getTypeMap() {
        TypeMap<E, D> typeMap = modelMapper.getTypeMap(entityClass, dtoClass);
        if (typeMap == null) {
            typeMap = modelMapper.createTypeMap(entityClass, dtoClass);
            configureTypeMap(typeMap);
        }
        return typeMap;
    }

    public TypeMap<D, E> getReverseTypeMap() {
        TypeMap<D, E> reverseTypeMap = modelMapper.getTypeMap(dtoClass, entityClass);
        if (reverseTypeMap == null) {
            reverseTypeMap = modelMapper.createTypeMap(dtoClass, entityClass);
            configureReverseTypeMap(reverseTypeMap);
        }
        return reverseTypeMap;
    }

    protected void configureTypeMap(TypeMap<E, D> typeMap) {}

    protected void configureReverseTypeMap(TypeMap<D, E> reverseTypeMap) {}

}
