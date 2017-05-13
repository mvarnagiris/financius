package com.code44.finance.money.grouping;

import com.code44.finance.data.model.Tag;

public class TagGroup extends AmountGroups.AmountGroup {
    private final Tag tag;

    public TagGroup(Tag tag) {
        this.tag = tag;
    }

    public Tag getTag() {
        return tag;
    }
}
