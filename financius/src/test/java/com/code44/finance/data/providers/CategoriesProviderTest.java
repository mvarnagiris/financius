package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

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
    public void delete_setsTheSameItemStateForTransactionsWithAffectedCategories() {
        // Insert category
        final Category category = new Category();
        category.setTitle("a");
        category.setId(insert(CategoriesProvider.uriCategories(), category));

        // Insert transactions with this Category
        final Transaction transaction = new Transaction();
        transaction.setCategory(category);
        insert(TransactionsProvider.uriTransactions(), transaction);

        // Delete category
        final Uri uri = uriWithDeleteMode(CategoriesProvider.uriCategories(), "delete");
        delete(uri, Tables.Categories.ID.getNameWithTable() + "=?", String.valueOf(category.getId()));

        // Assert
        final Cursor cursor = query(TransactionsProvider.uriTransactions(), getTransactionsQuery());
        assertEquals(1, cursor.getCount());
        final Transaction transactionFromDB = Transaction.from(cursor);
        IOUtils.closeQuietly(cursor);
        assertEquals(BaseModel.ItemState.DELETED_UNDO, transactionFromDB.getItemState());
    }

    private Query getTransactionsQuery() {
        return Query.get()
                .projectionId(Tables.Transactions.ID)
                .projection(Tables.Transactions.PROJECTION)
                .projection(Tables.Categories.PROJECTION);
    }
}
