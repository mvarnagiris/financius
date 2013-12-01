package com.code44.finance.db.model;

import android.database.Cursor;
import com.code44.finance.db.Tables;

public class Account extends DBRecord
{
    private Currency currency;
    private String typeResName;
    private String title;
    private String note;
    private double balance;
    private double overdraft;
    private boolean showInTotals;
    private boolean showInSelection;
    private int origin;

    public static Account from(Cursor c)
    {
        final Account account = new Account();
        initBase(account, c, c.getLong(c.getColumnIndex(Tables.Accounts.ID)), Tables.Accounts.TABLE_NAME);

        final int iCurrencyId = c.getColumnIndex(Tables.Accounts.CURRENCY_ID);
        final int iTitle = c.getColumnIndex(Tables.Accounts.TITLE);
        final int iNote = c.getColumnIndex(Tables.Accounts.NOTE);
        final int iBalance = c.getColumnIndex(Tables.Accounts.BALANCE);
        final int iShowInTotals = c.getColumnIndex(Tables.Accounts.SHOW_IN_TOTALS);
        final int iShowInSelection = c.getColumnIndex(Tables.Accounts.SHOW_IN_SELECTION);
        final int iOrigin = c.getColumnIndex(Tables.Accounts.ORIGIN);

        if (iCurrencyId >= 0)
            account.setCurrency(Currency.from(c, Tables.Accounts.CURRENCY_ID));

        if (iTitle >= 0)
            account.setTitle(c.getString(iTitle));

        if (iNote >= 0)
            account.setNote(c.getString(iNote));

        if (iBalance >= 0)
            account.setBalance(c.getDouble(iBalance));

        if (iShowInTotals >= 0)
            account.setShowInTotals(c.getInt(iShowInTotals) != 0);

        if (iShowInSelection >= 0)
            account.setShowInSelection(c.getInt(iShowInSelection) != 0);

        if (iOrigin >= 0)
            account.setOrigin(c.getInt(iOrigin));

        return account;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public double getBalance()
    {
        return balance;
    }

    public void setBalance(double balance)
    {
        this.balance = balance;
    }

    public int getOrigin()
    {
        return origin;
    }

    public Account setOrigin(int origin)
    {
        this.origin = origin;
        return this;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public void setCurrency(Currency currency)
    {
        this.currency = currency;
    }

    public String getTypeResName()
    {
        return typeResName;
    }

    public void setTypeResName(String typeResName)
    {
        this.typeResName = typeResName;
    }

    public String getNote()
    {
        return note;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    public double getOverdraft()
    {
        return overdraft;
    }

    public void setOverdraft(double overdraft)
    {
        this.overdraft = overdraft;
    }

    public boolean isShowInTotals()
    {
        return showInTotals;
    }

    public void setShowInTotals(boolean showInTotals)
    {
        this.showInTotals = showInTotals;
    }

    public boolean isShowInSelection()
    {
        return showInSelection;
    }

    public void setShowInSelection(boolean showInSelection)
    {
        this.showInSelection = showInSelection;
    }
}