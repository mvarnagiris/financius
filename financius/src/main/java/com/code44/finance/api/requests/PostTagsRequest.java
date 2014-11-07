package com.code44.finance.api.requests;

import com.code44.finance.api.GcmRegistration;
import com.code44.finance.backend.endpoint.tags.Tags;
import com.code44.finance.backend.endpoint.tags.model.TagEntity;
import com.code44.finance.backend.endpoint.tags.model.TagsBody;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.model.Tag;

import java.util.ArrayList;
import java.util.List;

public class PostTagsRequest extends PostRequest<TagsBody> {
    private final Tags tagsService;
    private final List<Tag> tags;

    public PostTagsRequest(GcmRegistration gcmRegistration, Tags tagsService, List<Tag> tags) {
        super(null, gcmRegistration);
        Preconditions.notNull(tagsService, "Tags service cannot be null.");
        Preconditions.notNull(tags, "Tags list cannot be null.");

        this.tagsService = tagsService;
        this.tags = tags;
    }

    @Override protected TagsBody createBody() {
        return new TagsBody();
    }

    @Override protected void onAddPostData(TagsBody body) {
        final List<TagEntity> entities = new ArrayList<>();
        for (Tag tag : tags) {
//            entities.add(tag.asEntity());
        }
        body.setTags(entities);
    }

    @Override protected boolean isPostDataEmpty(TagsBody body) {
        return body.getTags().isEmpty();
    }

    @Override protected void performRequest(TagsBody body) throws Exception {
        tagsService.save(body);
    }
}
