package com.code44.finance.data.providers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class AccountsProviderTest extends BaseContentProviderTestCase {
    @Test
    public void insert_createsTransactionToMakeCorrectBalance_whenAccountIsNew() {
        fail();
    }

    @Test
    public void insert_createsTransactionToMakeCorrectBalance_whenAccountAlreadyExists() {
        fail();
    }

    @Test
    public void insert_doesNotCreateTransaction_whenAccountIsNewAndBalanceIsZero() {
        fail();
    }

    @Test
    public void insert_doesNotCreateTransaction_whenAccountAlreadyExistsAndBalanceIsUnchanged() {
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_throwsIllegalArgumentException() {
    }

    @Test
    public void delete_setsTheSameItemStateForTransactionsWithAffectedAccounts() {
        fail();
    }
}
