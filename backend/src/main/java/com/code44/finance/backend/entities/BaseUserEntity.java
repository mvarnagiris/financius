package com.code44.finance.backend.entities;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Index;

public class BaseUserEntity extends BaseEntity {
    @Index @ApiResourceProperty(ignored = AnnotationBoolean.TRUE) private Key<UserEntity> userEntity;

    public Key<UserEntity> getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(Key<UserEntity> userEntity) {
        this.userEntity = userEntity;
    }
}
