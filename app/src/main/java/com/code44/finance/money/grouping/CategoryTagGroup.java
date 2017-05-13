package com.code44.finance.money.grouping;

import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryTagGroup extends CategoryGroup {
    private final Map<Tag, TagGroup> tagGroups = new HashMap<>();

    public CategoryTagGroup(Category category) {
        super(category);
    }

    public void addTagsAmount(Transaction transaction, long amount) {
        final List<Tag> tags = transaction.getTags();
        if (tags == null || tags.size() == 0) {
            return;
        }

        for (Tag tag : tags) {
            TagGroup tagGroup = tagGroups.get(tag);
            if (tagGroup == null) {
                tagGroup = new TagGroup(tag);
                tagGroups.put(tag, tagGroup);
            }
            tagGroup.setValue(tagGroup.getValue() + amount);
        }
    }

    public Collection<TagGroup> getTagGroups() {
        return tagGroups.values();
    }
}
