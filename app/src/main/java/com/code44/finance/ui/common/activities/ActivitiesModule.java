package com.code44.finance.ui.common.activities;

import com.code44.finance.ui.accounts.detail.AccountActivity;
import com.code44.finance.ui.accounts.edit.AccountEditActivity;
import com.code44.finance.ui.accounts.list.AccountsActivity;
import com.code44.finance.ui.categories.detail.CategoryActivity;
import com.code44.finance.ui.categories.edit.CategoryEditActivity;
import com.code44.finance.ui.categories.list.CategoriesActivity;
import com.code44.finance.ui.currencies.detail.CurrencyActivity;
import com.code44.finance.ui.currencies.edit.CurrencyEditActivity;
import com.code44.finance.ui.currencies.list.CurrenciesActivity;
import com.code44.finance.ui.overview.OverviewActivity;
import com.code44.finance.ui.reports.categories.CategoriesReportActivity;
import com.code44.finance.ui.settings.SettingsActivity;
import com.code44.finance.ui.settings.about.AboutActivity;
import com.code44.finance.ui.settings.data.DataActivity;
import com.code44.finance.ui.settings.data.DriveExportActivity;
import com.code44.finance.ui.settings.data.DriveImportActivity;
import com.code44.finance.ui.settings.data.FileExportActivity;
import com.code44.finance.ui.settings.data.FileImportActivity;
import com.code44.finance.ui.settings.security.LockActivity;
import com.code44.finance.ui.settings.security.UnlockActivity;
import com.code44.finance.ui.tags.detail.TagActivity;
import com.code44.finance.ui.tags.edit.TagEditActivity;
import com.code44.finance.ui.tags.list.TagsActivity;
import com.code44.finance.ui.transactions.detail.TransactionActivity;
import com.code44.finance.ui.transactions.edit.TransactionEditActivity;
import com.code44.finance.ui.transactions.list.TransactionsActivity;
import com.code44.finance.ui.user.LoginActivity;
import com.code44.finance.ui.user.ProfileActivity;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {SplashActivity.class, OverviewActivity.class, CurrenciesActivity.class, CurrencyActivity.class,
                   CurrencyEditActivity.class, AccountsActivity.class, AccountActivity.class, AccountEditActivity.class,
                   TransactionsActivity.class, TransactionActivity.class, TransactionEditActivity.class, CategoriesActivity.class,
                   CategoryActivity.class, CategoryEditActivity.class, TagsActivity.class, TagActivity.class, TagEditActivity.class,
                   SettingsActivity.class, DataActivity.class, CalculatorActivity.class, FileExportActivity.class,
                   DriveExportActivity.class, FileImportActivity.class, DriveImportActivity.class, AboutActivity.class,
                   CategoriesReportActivity.class, UnlockActivity.class, LockActivity.class, LoginActivity.class, ProfileActivity.class})
public class ActivitiesModule {
}
