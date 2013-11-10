package com.code44.finance.db.model;

import android.database.Cursor;
import com.code44.finance.db.Tables;

import java.util.Date;

public class Transaction extends DBRecord
{
    private Date date;
    private Account accountFrom;
    private Account accountTo;
    private Category category;
    private double amount;
    private String note;
    private int state;
    private double exchangeRate;
    private boolean showInTotals;

    public static Transaction from(Cursor c)
    {
        final Transaction transaction = new Transaction();
        initBase(transaction, c, c.getLong(c.getColumnIndex(Tables.Transactions.ID)), Tables.Transactions.TABLE_NAME);

        final int iDate = c.getColumnIndex(Tables.Transactions.DATE);
        final int iAccountFromId = c.getColumnIndex(Tables.Transactions.ACCOUNT_FROM_ID);
        final int iAccountToId = c.getColumnIndex(Tables.Transactions.ACCOUNT_TO_ID);
        final int iCategoryId = c.getColumnIndex(Tables.Transactions.CATEGORY_ID);
        final int iAmount = c.getColumnIndex(Tables.Transactions.AMOUNT);
        final int iNote = c.getColumnIndex(Tables.Transactions.NOTE);
        final int iState = c.getColumnIndex(Tables.Transactions.STATE);
        final int iExchangeRate = c.getColumnIndex(Tables.Transactions.EXCHANGE_RATE);
        final int iShowInTotals = c.getColumnIndex(Tables.Transactions.SHOW_IN_TOTALS);
        final int iCategoryType = c.getColumnIndex(Tables.Categories.CategoriesChild.TYPE);

        if (iDate >= 0)
            transaction.setDate(new Date(c.getLong(iDate)));

        if (iAccountFromId >= 0)
        {
            final Account account = new Account();
            account.setId(c.getLong(iAccountFromId));
            transaction.setAccountFrom(account);
        }

        if (iAccountToId >= 0)
        {
            final Account account = new Account();
            account.setId(c.getLong(iAccountToId));
            transaction.setAccountTo(account);
        }

        if (iCategoryId >= 0)
        {
            final Category category = Category.from(c, Tables.Transactions.CATEGORY_ID);
            if (iCategoryType >= 0)
                category.setType(c.getInt(iCategoryType));
            transaction.setCategory(category);
        }

        if (iAmount >= 0)
            transaction.setAmount(c.getDouble(iAmount));

        if (iNote >= 0)
            transaction.setNote(c.getString(iNote));

        if (iState >= 0)
            transaction.setState(c.getInt(iState));

        if (iExchangeRate >= 0)
            transaction.setExchangeRate(c.getDouble(iExchangeRate));

        if (iShowInTotals >= 0)
            transaction.setShowInTotals(c.getInt(iShowInTotals) != 0);

        return transaction;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public Account getAccountFrom()
    {
        return accountFrom;
    }

    public void setAccountFrom(Account accountFrom)
    {
        this.accountFrom = accountFrom;
    }

    public Account getAccountTo()
    {
        return accountTo;
    }

    public void setAccountTo(Account accountTo)
    {
        this.accountTo = accountTo;
    }

    public Category getCategory()
    {
        return category;
    }

    public void setCategory(Category category)
    {
        this.category = category;
    }

    public double getAmount()
    {
        return amount;
    }

    public void setAmount(double amount)
    {
        this.amount = amount;
    }

    public String getNote()
    {
        return note;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    public int getState()
    {
        return state;
    }

    public void setState(int state)
    {
        this.state = state;
    }

    public double getExchangeRate()
    {
        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate)
    {
        this.exchangeRate = exchangeRate;
    }

    public boolean isShowInTotals()
    {
        return showInTotals;
    }

    public void setShowInTotals(boolean showInTotals)
    {
        this.showInTotals = showInTotals;
    }
}
