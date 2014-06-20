package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Account;
import com.code44.finance.data.db.model.BaseModel;
import com.code44.finance.data.db.model.Category;
import com.code44.finance.data.db.model.Transaction;
import com.code44.finance.utils.IOUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class AccountsProviderTest extends BaseContentProviderTestCase {
    @Test
    public void insert_createsIncomeTransactionToMakeCorrectBalance_whenAccountIsNewAndBalanceIsPositive() {
        final Account account = new Account();
        account.setTitle("a");
        account.setBalance(42);

        final Account accountFromDB = getAccountFromDB(insert(AccountsProvider.uriAccounts(), account));
        assertEquals(42, accountFromDB.getBalance());

        final Cursor cursor = query(TransactionsProvider.uriTransactions(), getTransactionsQuery());
        final Transaction transaction = Transaction.from(cursor);
        assertEquals(1, cursor.getCount());
        assertEquals(42, transaction.getAmount());
        assertEquals(Category.Type.INCOME, transaction.getCategory().getType());
        IOUtils.closeQuietly(cursor);
    }

    @Test
    public void insert_createsExpenseTransactionToMakeCorrectBalance_whenAccountIsNewAndBalanceIsExpense() {
        final Account account = new Account();
        account.setTitle("a");
        account.setBalance(-42);

        final Account accountFromDB = getAccountFromDB(insert(AccountsProvider.uriAccounts(), account));
        assertEquals(-42, accountFromDB.getBalance());

        final Cursor cursor = query(TransactionsProvider.uriTransactions(), getTransactionsQuery());
        final Transaction transaction = Transaction.from(cursor);
        assertEquals(1, cursor.getCount());
        assertEquals(42, transaction.getAmount());
        assertEquals(Category.Type.EXPENSE, transaction.getCategory().getType());
        IOUtils.closeQuietly(cursor);
    }

    @Test
    public void insert_createsTransactionToMakeCorrectBalance_whenAccountAlreadyExists() {
        final Account account = new Account();
        account.setTitle("a");

        Account accountFromDB = getAccountFromDB(insert(AccountsProvider.uriAccounts(), account));
        assertEquals(0, accountFromDB.getBalance());
        accountFromDB.setBalance(42);
        accountFromDB = getAccountFromDB(insert(AccountsProvider.uriAccounts(), accountFromDB));
        assertEquals(42, accountFromDB.getBalance());

        final Cursor cursor = query(TransactionsProvider.uriTransactions(), getTransactionsQuery());
        final Transaction transaction = Transaction.from(cursor);
        assertEquals(1, cursor.getCount());
        assertEquals(42, transaction.getAmount());
        IOUtils.closeQuietly(cursor);
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

        account.setId(insert(AccountsProvider.uriAccounts(), account));
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

        // Insert transactions with this account
        Transaction transaction = new Transaction();
        transaction.setAccountFrom(account);
        insert(TransactionsProvider.uriTransactions(), transaction);

        transaction = new Transaction();
        transaction.setAccountTo(account);
        insert(TransactionsProvider.uriTransactions(), transaction);

        // Delete account
        final Uri uri = uriWithDeleteMode(AccountsProvider.uriAccounts(), "delete");
        delete(uri, Tables.Accounts.ID.getNameWithTable() + "=?", String.valueOf(account.getId()));

        // Assert
        final Cursor cursor = query(TransactionsProvider.uriTransactions(), getTransactionsQuery());
        assertEquals(2, cursor.getCount());
        final Transaction transaction1FromDB = Transaction.from(cursor);
        cursor.moveToNext();
        final Transaction transaction2FromDB = Transaction.from(cursor);
        IOUtils.closeQuietly(cursor);
        assertEquals(BaseModel.ItemState.DELETED_UNDO, transaction1FromDB.getItemState());
        assertEquals(BaseModel.ItemState.DELETED_UNDO, transaction2FromDB.getItemState());
    }

    private Query getTransactionsQuery() {
        return Query.get()
                .projectionId(Tables.Transactions.ID)
                .projection(Tables.Transactions.PROJECTION);
    }

    private Account getAccountFromDB(long accountId) {
        final Cursor cursor = Query.get()
                .projectionId(Tables.Accounts.ID)
                .projection(Tables.Accounts.PROJECTION)
                .projection(Tables.Currencies.PROJECTION)
                .asCursor(Robolectric.getShadowApplication().getApplicationContext(), AccountsProvider.uriAccount(accountId));
        final Account account = Account.from(cursor);
        IOUtils.closeQuietly(cursor);
        return account;
    }
}
