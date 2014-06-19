package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Account;
import com.code44.finance.data.db.model.BaseModel;
import com.code44.finance.data.db.model.Transaction;
import com.code44.finance.utils.IOUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class AccountsProviderTest extends BaseContentProviderTestCase {
    @Test
    public void insert_createsTransactionToMakeCorrectBalance_whenAccountIsNew() {
        final Account account = new Account();
        account.setTitle("a");
        account.setBalance(42);

        insert(AccountsProvider.uriAccounts(), account);

        final Cursor cursor = query(TransactionsProvider.uriTransactions(), getTransactionsQuery());
        final Transaction transaction = Transaction.from(cursor);
        IOUtils.closeQuietly(cursor);
        assertEquals(42, transaction.getAmount());
    }

    @Test
    public void insert_createsTransactionToMakeCorrectBalance_whenAccountAlreadyExists() {
        final Account account = new Account();
        account.setTitle("a");

        insert(AccountsProvider.uriAccounts(), account);
        account.setBalance(42);
        insert(AccountsProvider.uriAccounts(), account);

        final Cursor cursor = query(TransactionsProvider.uriTransactions(), getTransactionsQuery());
        final Transaction transaction = Transaction.from(cursor);
        IOUtils.closeQuietly(cursor);
        assertEquals(42, transaction.getAmount());
    }

    @Test
    public void insert_doesNotCreateTransaction_whenAccountIsNewAndBalanceIsZero() {
        final Account account = new Account();
        account.setTitle("a");

        insert(AccountsProvider.uriAccounts(), account);

        assertQuerySize(TransactionsProvider.uriTransactions(), getTransactionsQuery(), 0);
    }

    @Test
    public void insert_doesNotCreateTransaction_whenAccountAlreadyExistsAndBalanceIsUnchanged() {
        final Account account = new Account();
        account.setTitle("a");
        account.setBalance(42);

        insert(AccountsProvider.uriAccounts(), account);
        insert(AccountsProvider.uriAccounts(), account);

        assertQuerySize(TransactionsProvider.uriTransactions(), getTransactionsQuery(), 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_throwsIllegalArgumentException() {
        update(AccountsProvider.uriAccounts(), new ContentValues(), null);
    }

    @Test
    public void delete_setsTheSameItemStateForTransactionsWithAffectedAccounts() {
        // Insert account
        final Account account = new Account();
        account.setTitle("a");
        account.setId(insert(AccountsProvider.uriAccounts(), account));

        // Insert transaction with this account
        final Transaction transaction = new Transaction();
        transaction.setAccountFrom(account);
        transaction.setId(insert(TransactionsProvider.uriTransactions(), transaction));

        // Delete account
        final Uri uri = uriWithDeleteMode(AccountsProvider.uriAccounts(), "delete");
        delete(uri, Tables.Accounts.ID.getNameWithTable() + "=?", String.valueOf(account.getId()));

        // Assert
        final Cursor cursor = query(TransactionsProvider.uriTransactions(), getTransactionsQuery());
        final Transaction transactionFromDB = Transaction.from(cursor);
        IOUtils.closeQuietly(cursor);
        assertEquals(BaseModel.ItemState.DELETED_UNDO, transactionFromDB.getItemState());
    }

    private Query getTransactionsQuery() {
        return Query.get()
                .projectionId(Tables.Transactions.ID)
                .projection(Tables.Transactions.PROJECTION);
    }
}
