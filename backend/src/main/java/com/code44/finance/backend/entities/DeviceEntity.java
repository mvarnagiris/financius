package com.code44.finance.backend.entities;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

import static com.code44.finance.backend.OfyService.ofy;

@Entity
public class DeviceEntity extends BaseEntity {
    @Index @ApiResourceProperty(ignored = AnnotationBoolean.TRUE) private Key<UserEntity> userEntity;

    @Index @ApiResourceProperty(name = "registration_id") private String registrationId;

    @ApiResourceProperty(name = "device_name") private String deviceName;

    public static DeviceEntity find(String id) {
        return ofy().load().type(DeviceEntity.class).id(id).now();
    }

    public static DeviceEntity findWithRegistrationId(String registrationId) {
        return ofy().load().type(DeviceEntity.class).filter("registrationId", registrationId).first().now();
    }

    public Key<UserEntity> getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(Key<UserEntity> userEntity) {
        this.userEntity = userEntity;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
