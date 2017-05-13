package com.code44.finance.api.endpoints.requests.tags;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.code44.finance.api.endpoints.EndpointFactory;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.api.endpoints.requests.GetEntitiesRequest;
import com.code44.finance.backend.financius.model.TagEntity;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.providers.TagsProvider;
import com.code44.finance.utils.EventBus;

import java.util.List;

public class GetTagsRequest extends GetEntitiesRequest<Tag, TagEntity> {
    public GetTagsRequest(@NonNull EventBus eventBus, @NonNull EndpointFactory endpointFactory, @NonNull Context context, @NonNull User user) {
        super(eventBus, endpointFactory, context, user);
    }

    @Override protected long getLastUpdateTimestamp(User user) {
        return user.getLastTagsUpdateTimestamp();
    }

    @Override protected List<TagEntity> performRequest(long timestamp) throws Exception {
        return getEndpoint().listTags(timestamp).execute().getItems();
    }

    @Override protected Tag getModelFrom(TagEntity entity) {
        return Tag.from(entity);
    }

    @Override protected Uri getSaveUri() {
        return TagsProvider.uriTags();
    }

    @Override protected void saveLastUpdateTimestamp(User user, long lastUpdateTimestamp) {
        user.setLastTagsUpdateTimestamp(lastUpdateTimestamp);
    }
}