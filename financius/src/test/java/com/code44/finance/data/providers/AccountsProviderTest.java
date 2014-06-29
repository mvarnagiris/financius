package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.database.Cursor;

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
        final Account account = queryAccount(insertAccount(42).getId());
        assertEquals(42, account.getBalance());

        final Cursor cursor = queryTransactionsCursor();
        assertEquals(1, cursor.getCount());

        final Transaction transaction = Transaction.from(cursor);
        assertEquals(42, transaction.getAmount());
        assertEquals(Category.Type.INCOME, transaction.getCategory().getType());
        IOUtils.closeQuietly(cursor);
    }

    @Test
    public void insert_createsExpenseTransactionToMakeCorrectBalance_whenAccountIsNewAndBalanceIsExpense() {
        final Account account = queryAccount(insertAccount(-42).getId());
        assertEquals(-42, account.getBalance());

        final Cursor cursor = queryTransactionsCursor();
        assertEquals(1, cursor.getCount());

        final Transaction transaction = Transaction.from(cursor);
        assertEquals(42, transaction.getAmount());
        assertEquals(Category.Type.EXPENSE, transaction.getCategory().getType());
        IOUtils.closeQuietly(cursor);
    }

    @Test
    public void insert_createsTransactionToMakeCorrectBalance_whenAccountAlreadyExists() {
        Account account = queryAccount(insertAccount(0).getId());
        assertEquals(0, account.getBalance());

        account.setBalance(42);
        account = queryAccount(insertAccount(account).getId());
        assertEquals(42, account.getBalance());

        final Cursor cursor = queryTransactionsCursor();
        assertEquals(1, cursor.getCount());

        final Transaction transaction = Transaction.from(cursor);
        assertEquals(42, transaction.getAmount());
        IOUtils.closeQuietly(cursor);
    }

    @Test
    public void insert_doesNotCreateTransaction_whenAccountIsNewAndBalanceIsZero() {
        insertAccount(0);

        final Cursor cursor = queryTransactionsCursor();
        assertEquals(0, cursor.getCount());
        IOUtils.closeQuietly(cursor);
    }

    @Test
    public void insert_doesNotCreateTransaction_whenAccountAlreadyExistsAndBalanceIsUnchanged() {
        insertAccount(insertAccount(42));

        final Cursor cursor = queryTransactionsCursor();
        assertEquals(1, cursor.getCount());
        IOUtils.closeQuietly(cursor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_throwsIllegalArgumentException() {
        update(AccountsProvider.uriAccounts(), new ContentValues(), null);
    }

    @Test
    public void deleteDelete_setsItemStateDeletedUndoForTransactions() {
        final Account account = insertAccount(0);
        insertTransaction(account, null);
        insertTransaction(null, account);

        deleteAccount(account);
        final Cursor cursor = queryTransactionsCursor();

        assertEquals(2, cursor.getCount());
        assertEquals(BaseModel.ItemState.DELETED_UNDO, Transaction.from(cursor).getItemState());
        cursor.moveToNext();
        assertEquals(BaseModel.ItemState.DELETED_UNDO, Transaction.from(cursor).getItemState());
        IOUtils.closeQuietly(cursor);
    }

    private Account queryAccount(long accountId) {
        final Cursor cursor = Query.create()
                .projectionId(Tables.Accounts.ID)
                .projection(Tables.Accounts.PROJECTION)
                .projection(Tables.Currencies.PROJECTION)
                .from(Robolectric.getShadowApplication().getApplicationContext(), AccountsProvider.uriAccount(accountId))
                .execute();
        final Account account = Account.from(cursor);
        IOUtils.closeQuietly(cursor);
        return account;
    }

    private Account insertAccount(long balance) {
        final Account account = new Account();
        account.setTitle("a");
        account.setBalance(balance);

        return insertAccount(account);
    }

    private Account insertAccount(Account account) {
        account.setId(insert(AccountsProvider.uriAccounts(), account));
        return account;
    }

    private int deleteAccount(Account account) {
        return delete("delete", AccountsProvider.uriAccounts(), Tables.Accounts.ID + "=?", String.valueOf(account.getId()));
    }

    private Cursor queryTransactionsCursor() {
        final Query query = Query.create()
                .projectionId(Tables.Transactions.ID)
                .projection(Tables.Transactions.PROJECTION)
                .projection(Tables.Categories.PROJECTION);

        return query(TransactionsProvider.uriTransactions(), query);
    }

    private Transaction insertTransaction(Account accountFrom, Account accountTo) {
        final Transaction transaction = new Transaction();

        if (accountFrom != null) {
            transaction.setAccountFrom(accountFrom);
        }

        if (accountTo != null) {
            transaction.setAccountTo(accountTo);
        }

        transaction.setId(insert(TransactionsProvider.uriTransactions(), transaction));
        return transaction;
    }
}
