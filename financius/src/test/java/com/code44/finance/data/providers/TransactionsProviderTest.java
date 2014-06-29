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
        Account account = insertAccount();
        final Transaction transaction = new Transaction();
        transaction.setAccountFrom(account);
        transaction.setCategory(Category.getExpense());
        transaction.setAmount(42);

        insert(TransactionsProvider.uriTransactions(), transaction);
        account = getAccount(account.getId());

        assertEquals(-42, account.getBalance());
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_throwsIllegalArgumentException() {
        update(TransactionsProvider.uriTransactions(), new ContentValues(), null);
    }

    @Test
    public void delete_updatesAccountsBalances() {
        Account account = insertAccount();
        final Transaction transaction = new Transaction();
        transaction.setAccountFrom(account);
        transaction.setCategory(Category.getExpense());
        transaction.setAmount(42);

        insert(TransactionsProvider.uriTransactions(), transaction);
        delete("delete", TransactionsProvider.uriTransactions(), null);
        account = getAccount(account.getId());

        assertEquals(0, account.getBalance());
    }

    @Test
    public void bulkInsert_updatesAccountsBalances() {
        Account account = insertAccount();
        final Transaction transaction = new Transaction();
        transaction.setAccountTo(account);
        transaction.setCategory(Category.getIncome());
        transaction.setAmount(42);

        bulkInsert(TransactionsProvider.uriTransactions(), transaction.asContentValues());
        account = getAccount(account.getId());

        assertEquals(42, account.getBalance());
    }

    private Account insertAccount() {
        final Account account = new Account();
        account.setTitle("a");
        account.setId(insert(AccountsProvider.uriAccounts(), account));
        return account;
    }

    private Account getAccount(long accountId) {
        final Cursor cursor = Query.create()
                .projectionId(Tables.Accounts.ID)
                .projection(Tables.Accounts.PROJECTION)
                .projection(Tables.Currencies.PROJECTION)
                .from(context, AccountsProvider.uriAccount(accountId))
                .execute();
        assertEquals(1, cursor.getCount());
        final Account account = Account.from(cursor);
        IOUtils.closeQuietly(cursor);
        return account;
    }
}
