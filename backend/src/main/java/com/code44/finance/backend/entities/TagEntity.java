package com.code44.finance.backend.entities;

import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.annotation.Entity;

import static com.code44.finance.backend.OfyService.ofy;

@Entity
public class TagEntity extends BaseUserEntity {
    @ApiResourceProperty(name = "title") private String title;

    public static TagEntity find(String id) {
        return ofy().load().type(TagEntity.class).id(id).now();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
