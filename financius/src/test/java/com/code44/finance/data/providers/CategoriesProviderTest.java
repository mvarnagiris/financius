package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.database.Cursor;

import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.BaseModel;
import com.code44.finance.data.db.model.Category;
import com.code44.finance.data.db.model.Transaction;
import com.code44.finance.utils.IOUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class CategoriesProviderTest extends BaseContentProviderTestCase {
    @Test(expected = IllegalArgumentException.class)
    public void update_throwsIllegalArgumentException() {
        update(CategoriesProvider.uriCategories(), new ContentValues(), null);
    }

    @Test
    public void deleteDelete_setsItemStateDeletedUndoForTransactions() {
        final Category category = insertCategory();
        insertTransaction(category);

        deleteCategory(category);
        final Cursor cursor = queryTransactionsCursor();

        assertEquals(1, cursor.getCount());
        assertEquals(BaseModel.ItemState.DELETED_UNDO, Transaction.from(cursor).getItemState());
        IOUtils.closeQuietly(cursor);
    }

    private Category insertCategory() {
        final Category category = new Category();
        category.setTitle("a");
        category.setId(insert(CategoriesProvider.uriCategories(), category));
        return category;
    }

    private int deleteCategory(Category category) {
        return delete("delete", CategoriesProvider.uriCategories(), Tables.Categories.ID + "=?", String.valueOf(category.getId()));
    }

    private Transaction insertTransaction(Category category) {
        final Transaction transaction = new Transaction();

        if (category != null) {
            transaction.setCategory(category);
        }

        transaction.setId(insert(TransactionsProvider.uriTransactions(), transaction));
        return transaction;
    }

    private Cursor queryTransactionsCursor() {
        final Query query = Query.get()
                .projectionId(Tables.Transactions.ID)
                .projection(Tables.Transactions.PROJECTION)
                .projection(Tables.Categories.PROJECTION);

        return query(TransactionsProvider.uriTransactions(), query);
    }
}
