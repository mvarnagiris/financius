package com.code44.finance.backend.endpoint.body;

import com.code44.finance.backend.entity.TagEntity;

import java.util.List;

public class TagsBody extends EntitiesBody<TagEntity> {
    private final List<TagEntity> tags;

    public TagsBody(List<TagEntity> tags, String deviceRegId) {
        super(deviceRegId);
        this.tags = tags;
    }

    public List<TagEntity> getTags() {
        return tags;
    }
}
