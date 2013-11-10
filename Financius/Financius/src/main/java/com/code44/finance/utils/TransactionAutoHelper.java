package com.code44.finance.utils;

import android.database.Cursor;
import android.text.format.DateUtils;
import com.code44.finance.db.Tables;
import com.code44.finance.db.model.Account;
import com.code44.finance.db.model.Category;
import com.code44.finance.db.model.Transaction;

import java.util.*;

public class TransactionAutoHelper
{
    private final Map<Long, Account> accountMap = new HashMap<Long, Account>();
    private final Map<Long, Category> categoryMap = new HashMap<Long, Category>();
    private final Map<Long, Transaction> transactionMap = new HashMap<Long, Transaction>();
    // -----------------------------------------------------------------------------------------------------------------
    private boolean categoryTypeReady = false;
    private boolean accountsReady = false;
    private boolean categoriesReady = false;
    private boolean transactionsReady = false;
    // -----------------------------------------------------------------------------------------------------------------
    private long date = 0;
    private long accountFromId = -1;
    private long accountToId = -1;
    private long categoryId = -1;
    private int categoryType = -1;

    public TransactionAutoHelper()
    {
    }

    public static TransactionAutoHelper getInstance()
    {
        return new TransactionAutoHelper();
    }

    public boolean isDataOk()
    {
        return date > 0 && accountFromId >= 0 && accountToId >= 0 && categoryId >= 0 && categoryTypeReady && accountsReady && categoriesReady && transactionsReady;
    }

    public void setDate(long date)
    {
        this.date = date;
    }

    public void setCategoryId(long categoryId)
    {
        this.categoryId = categoryId;
    }

    public void setAccountFromId(long accountFromId)
    {
        this.accountFromId = accountFromId;
    }

    public void setAccountToId(long accountToId)
    {
        this.accountToId = accountToId;
    }

    public void setCategoryType(int categoryType)
    {
        this.categoryType = categoryType;
        categoryTypeReady = categoryType == Tables.Categories.Type.EXPENSE || categoryType == Tables.Categories.Type.INCOME || categoryType == Tables.Categories.Type.TRANSFER;
    }

    public void setAccounts(Cursor c)
    {
        accountMap.clear();
        if (c != null && c.moveToFirst())
        {
            Account account;
            do
            {
                account = Account.from(c);
                accountMap.put(account.getId(), account);
            }
            while (c.moveToNext());
        }

        accountsReady = true;
    }

    public void setCategories(Cursor c)
    {
        categoryMap.clear();
        if (c != null && c.moveToFirst())
        {
            Category category;
            do
            {
                category = Category.from(c);
                categoryMap.put(category.getId(), category);
            }
            while (c.moveToNext());
        }

        categoriesReady = true;
    }

    public void setTransactions(Cursor c)
    {
        transactionMap.clear();
        if (c != null && c.moveToFirst())
        {
            Transaction transaction;
            do
            {
                transaction = Transaction.from(c);
                transactionMap.put(transaction.getId(), transaction);
            }
            while (c.moveToNext());
        }

        transactionsReady = true;
    }

    public Account getAccount(AccountType accountType)
    {
        // Check if we have any accounts
        if (accountMap.size() == 0)
            return null;

        // Check, maybe we are asking for system account
        if (accountType == AccountType.FROM && categoryType == Tables.Categories.Type.INCOME)
        {
            // If this is income, then accountFrom is always 'expense account'.
            return null;
        }
        else if (accountType == AccountType.TO && categoryType == Tables.Categories.Type.EXPENSE)
        {
            // If this is expense, then accountTo is always 'income account'.
            return null;
        }

        // If we have only 1 account, then it will always be that account, unless it's the account to avoid.
        if (accountMap.size() == 1)
        {
            final Account account = accountMap.values().iterator().next();

            if (account.getId() == accountFromId || account.getId() == accountToId)
                return null;

            return account;
        }

        // Check for transactions
        if (transactionMap.size() == 0)
        {
            // We don't have transactions, just use latest created account
            Account latestAccount = null;
            for (Account account : accountMap.values())
            {
                if (account == null)
                    continue;

                if ((account.getId() != accountFromId && account.getId() != accountToId) && (latestAccount == null || account.getTimestamp() == null && latestAccount.getTimestamp() == null || account.getTimestamp().after(latestAccount.getTimestamp())))
                    latestAccount = account;
            }
            return latestAccount;
        }

        // Prepare accounts probabilitiesMap map
        final Map<Long, Probability> probabilitiesMap = new HashMap<Long, Probability>();
        for (Account account : accountMap.values())
        {
            if (account.getId() != accountFromId && account.getId() != accountToId)
                probabilitiesMap.put(account.getId(), new Probability());
        }

        // Check again how many accounts do we have, because avoidIdList might have reduced that to 1 or 0
        if (probabilitiesMap.size() == 1)
            return accountMap.get(probabilitiesMap.keySet().iterator().next());
        else if (probabilitiesMap.size() == 0)
            return null;

        // Iterate through all transactions and update probabilities for accounts
        Probability probability;
        Long accountFromId;
        Long accountToId;
        for (Transaction transaction : transactionMap.values())
        {
            // Skip transactions that are of different type
            if (transaction.getCategory().getType() != categoryType)
                continue;

            accountFromId = null;
            accountToId = null;

            switch (categoryType)
            {
                case Tables.Categories.Type.EXPENSE:
                    accountFromId = transaction.getAccountFrom().getId();
                    break;

                case Tables.Categories.Type.INCOME:
                    accountToId = transaction.getAccountTo().getId();
                    break;

                case Tables.Categories.Type.TRANSFER:
                    accountFromId = transaction.getAccountFrom().getId();
                    accountToId = transaction.getAccountTo().getId();
                    break;
            }

            if (accountFromId != null && probabilitiesMap.get(accountFromId) != null)
            {
                probability = probabilitiesMap.get(accountFromId);
                probability.addTime(date, transaction.getDate().getTime());
                probability.addSameCategoryType(categoryType, accountType);
                if (categoryType != Tables.Categories.Type.TRANSFER)
                    probability.addSameCategory(categoryId, transaction.getCategory().getId());
                else if (accountType == AccountType.FROM)
                    probability.addSameCategory(Tables.Categories.IDs.TRANSFER_ID, Tables.Categories.IDs.TRANSFER_ID);
            }

            if (accountToId != null && probabilitiesMap.get(accountToId) != null)
            {
                probability = probabilitiesMap.get(accountToId);
                probability.addTime(date, transaction.getDate().getTime());
                probability.addSameCategoryType(categoryType, accountType);
                if (categoryType != Tables.Categories.Type.TRANSFER)
                    probability.addSameCategory(categoryId, transaction.getCategory().getId());
                else if (accountType == AccountType.TO)
                    probability.addSameCategory(Tables.Categories.IDs.TRANSFER_ID, Tables.Categories.IDs.TRANSFER_ID);
            }
        }

        // Return the most probable account.
        Account probableAccount = null;
        Float bestProbability = 0.0f;
        Float probabilityValue;
        for (Long accountId : probabilitiesMap.keySet())
        {
            probabilityValue = probabilitiesMap.get(accountId).getAccountProbability();
            if (probableAccount == null || probabilityValue >= bestProbability)
            {
                probableAccount = accountMap.get(accountId);
                bestProbability = probabilityValue;
            }
        }

        return probableAccount;
    }

    public Category getCategory()
    {
        if (categoryMap.size() == 0)
            return null;

        // If this is for transfer or transactions list is empty, then we don't need to find category
        if (categoryType == Tables.Categories.Type.TRANSFER || transactionMap.size() == 0)
            return null;

        // Prepare categories probabilitiesMap map
        final Map<Long, Probability> probabilitiesMap = new HashMap<Long, Probability>();
        for (Category category : categoryMap.values())
        {
            if (category.getType() == categoryType)
                probabilitiesMap.put(category.getId(), new Probability());
        }

        // Iterate through all transactions and update probabilities for accounts
        Probability probability;
        Long transactionCategoryId = 0L;
        for (Transaction transaction : transactionMap.values())
        {
            // Skip transactions that are of different type
            if (transaction.getCategory().getType() != categoryType)
                continue;

            transactionCategoryId = transaction.getCategory().getId();

            if (probabilitiesMap.get(transactionCategoryId) != null)
            {
                probability = probabilitiesMap.get(transactionCategoryId);
                probability.addTime(date, transaction.getDate().getTime());
                probability.addSameAccount(categoryType == Tables.Categories.Type.EXPENSE ? accountFromId : accountToId, categoryType == Tables.Categories.Type.EXPENSE ? transaction.getAccountFrom().getId() : transaction.getAccountTo().getId());
            }
        }

        // If no transactions were found, return null
        if (transactionCategoryId == 0)
            return null;

        // Return the most probable account.
        Category probableCategory = null;
        Float bestProbability = 0.0f;
        Float probabilityValue;
        for (Long categoryId : probabilitiesMap.keySet())
        {
            probabilityValue = probabilitiesMap.get(categoryId).getCategoryProbability();
            if (probableCategory == null || probabilityValue >= bestProbability)
            {
                probableCategory = categoryMap.get(categoryId);
                bestProbability = probabilityValue;
            }
        }

        return probableCategory;
    }

    public enum AccountType
    {
        FROM, TO
    }

    private static class Probability
    {
        private static final long MAX_TIME_OF_DAY_DEVIATION = DateUtils.HOUR_IN_MILLIS;
        private static final long MAX_RECENCY_DEVIATION = DateUtils.DAY_IN_MILLIS * 7;
        // -------------------------------------------------------------------------------------------------------------
        private static final float SAME_CATEGORY_FACTOR = 0.3f;
        private static final float SAME_CATEGORY_TYPE_FACTOR = 0.2f;
        private static final float DAY_OF_WEEK_FACTOR = 0.2f;
        private static final float DAY_OF_MONTH_FACTOR = 0.2f;
        // -------------------------------------------------------------------------------------------------------------
        private final List<Float> timeOfDayList = new ArrayList<Float>();
        private Float recencyProbability = 0.0f;
        private int dayOfWeek = 0;
        private int dayOfMonth = 0;
        private int usedForSameCategoryType = 0;
        private int usedForSameCategory = 0;
        private int usedForSameAccount = 0;

        public Float getAccountProbability()
        {
            return getRecencyProbability() + getAverage(timeOfDayList) + getProbability(DAY_OF_WEEK_FACTOR, dayOfWeek) + getProbability(DAY_OF_MONTH_FACTOR, dayOfMonth) + getProbability(SAME_CATEGORY_TYPE_FACTOR, usedForSameCategoryType) + getProbability(SAME_CATEGORY_FACTOR, usedForSameCategory);
        }

        public Float getCategoryProbability()
        {
            return getRecencyProbability() + getAverage(timeOfDayList) + getProbability(DAY_OF_WEEK_FACTOR, dayOfWeek) + getProbability(DAY_OF_MONTH_FACTOR, dayOfMonth) + getProbability(SAME_CATEGORY_FACTOR, usedForSameAccount);
        }

        public void addTime(long date, long transactionDate)
        {
            addRecency(date, transactionDate);
            addTimeOfDay(date, transactionDate);
            addDayOfWeek(date, transactionDate);
            addDayOfMonth(date, transactionDate);
        }

        public void addSameCategoryType(int categoryType, AccountType accountType)
        {
            if ((accountType == AccountType.FROM && (categoryType == Tables.Categories.Type.EXPENSE || categoryType == Tables.Categories.Type.TRANSFER)) || (accountType == AccountType.TO && (categoryType == Tables.Categories.Type.INCOME || categoryType == Tables.Categories.Type.TRANSFER)))
                usedForSameCategoryType++;
        }

        public void addSameCategory(long categoryId, long transactionCategoryId)
        {
            if (categoryId > 0 && transactionCategoryId > 0 && categoryId == transactionCategoryId)
                usedForSameCategory++;
        }

        public void addSameAccount(long accountId, long transactionAccountId)
        {
            if (accountId > 0 && transactionAccountId > 0 && accountId == transactionAccountId)
                usedForSameAccount++;
        }

        private void addRecency(long date, long transactionDate)
        {
            final long deviation = Math.abs(date - transactionDate);
            if (deviation < MAX_RECENCY_DEVIATION)
            {
                final Float probability = 1 - ((float) deviation / MAX_RECENCY_DEVIATION);
                if (probability > recencyProbability)
                    recencyProbability = probability;
            }
        }

        private void addTimeOfDay(long date, long transactionDate)
        {
            final long deviation = Math.abs(date - transactionDate);
            if (deviation < MAX_TIME_OF_DAY_DEVIATION)
                timeOfDayList.add(1 - ((float) deviation / MAX_TIME_OF_DAY_DEVIATION));
        }

        private void addDayOfWeek(long date, long transactionDate)
        {
            final Calendar calDate = Calendar.getInstance();
            calDate.setTimeInMillis(date);

            final Calendar calTransactionDate = Calendar.getInstance();
            calTransactionDate.setTimeInMillis(transactionDate);

            if (calTransactionDate.get(Calendar.DAY_OF_WEEK) == calTransactionDate.get(Calendar.DAY_OF_WEEK))
                dayOfWeek++;
        }

        private void addDayOfMonth(long date, long transactionDate)
        {
            final Calendar calDate = Calendar.getInstance();
            calDate.setTimeInMillis(date);

            final Calendar calTransactionDate = Calendar.getInstance();
            calTransactionDate.setTimeInMillis(transactionDate);

            if (calTransactionDate.get(Calendar.DAY_OF_MONTH) == calTransactionDate.get(Calendar.DAY_OF_MONTH))
                dayOfMonth++;
        }

        private Float getRecencyProbability()
        {
            return recencyProbability;
        }

        private Float getAverage(List<Float> list)
        {
            Float average = 0.0f;
            for (int i = 0; i < list.size(); i++)
                average += list.get(i);

            return list.size() == 0 ? 0.0f : average / list.size();
        }

        /**
         * Calculates probability that approaches 1.0
         *
         * @param factor The bigger the factor, the quicker it will approach 1
         * @param count
         * @return
         */
        private Float getProbability(float factor, int count)
        {
            return count == 0 ? 0.0f : (float) -Math.exp(-factor * count) + 1;
        }
    }
}