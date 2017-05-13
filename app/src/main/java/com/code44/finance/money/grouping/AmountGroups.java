package com.code44.finance.money.grouping;

import android.database.Cursor;

import com.code44.finance.data.model.Transaction;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AmountGroups<AG extends AmountGroups.AmountGroup> {
    public List<AG> getGroups(Cursor cursor) {
        if (cursor == null || !cursor.moveToFirst()) {
            return Collections.emptyList();
        }

        final Map<Long, AG> amountGroupMap = new HashMap<>();
        do {
            final Transaction transaction = Transaction.from(cursor);
            for (int i = 0, groupCount = getGroupCount(transaction); i < groupCount; i++) {
                final Long groupId = getGroupId(transaction, i);
                if (groupId == null) {
                    continue;
                }

                AG amountGroup = amountGroupMap.get(groupId);
                if (amountGroup == null) {
                    amountGroup = createAmountGroup(transaction, i);
                    amountGroupMap.put(groupId, amountGroup);
                }
                amountGroup.setValue(amountGroup.getValue() + getAmount(amountGroup, transaction));
            }
        } while (cursor.moveToNext());

        return getGroups(amountGroupMap.values());
    }

    protected abstract int getGroupCount(Transaction transaction);

    protected abstract Long getGroupId(Transaction transaction, int groupPosition);

    protected abstract AG createAmountGroup(Transaction transaction, int groupPosition);

    protected abstract long getAmount(AG amountGroup, Transaction transaction);

    protected abstract List<AG> getGroups(Collection<AG> groups);

    public static class AmountGroup {
        private long value;

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }
    }
}
