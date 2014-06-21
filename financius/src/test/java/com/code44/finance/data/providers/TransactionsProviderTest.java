package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.database.Cursor;

import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Account;
import com.code44.finance.data.db.model.Category;
import com.code44.finance.data.db.model.Transaction;
import com.code44.finance.utils.IOUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class TransactionsProviderTest extends BaseContentProviderTestCase {
    @Test
    public void insert_updatesAccountsBalances() {
        final Transaction transaction = new Transaction();
        transaction.setAccountFrom(insertAccount());
        transaction.setCategory(Category.getExpense());
        transaction.setAmount(42);

        insert(TransactionsProvider.uriTransactions(), transaction);
        final Account account = getAccount();

        assertEquals(42, account.getBalance());
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_throwsIllegalArgumentException() {
        update(TransactionsProvider.uriTransactions(), new ContentValues(), null);
    }

    @Test
    public void delete_updatesAccountsBalances() {
        final Transaction transaction = new Transaction();
        transaction.setAccountFrom(insertAccount());
        transaction.setCategory(Category.getExpense());
        transaction.setAmount(42);

        insert(TransactionsProvider.uriTransactions(), transaction);
        delete(TransactionsProvider.uriTransactions(), null);
        final Account account = getAccount();

        assertEquals(0, account.getBalance());
    }

    @Test
    public void bulkInsert_updatesAccountsBalances() {
        final Transaction transaction = new Transaction();
        transaction.setAccountFrom(insertAccount());
        transaction.setCategory(Category.getExpense());
        transaction.setAmount(42);

        contentResolver.bulkInsert(TransactionsProvider.uriTransactions(), new ContentValues[]{transaction.asContentValues()});
        final Account account = getAccount();

        assertEquals(42, account.getBalance());
    }

    private Account insertAccount() {
        final Account account = new Account();
        account.setTitle("a");
        account.setId(insert(AccountsProvider.uriAccounts(), account));
        return account;
    }

    private Account getAccount() {
        final Cursor cursor = Query.get()
                .projectionId(Tables.Accounts.ID)
                .projection(Tables.Accounts.PROJECTION)
                .projection(Tables.Currencies.PROJECTION)
                .asCursor(context, AccountsProvider.uriAccounts());
        final Account account = Account.from(cursor);
        IOUtils.closeQuietly(cursor);
        return account;
    }
}
