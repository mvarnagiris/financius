package com.code44.finance.modules;

import com.code44.finance.ui.CalculatorActivity;
import com.code44.finance.ui.CalculatorFragment;
import com.code44.finance.ui.SplashActivity;
import com.code44.finance.ui.accounts.AccountActivity;
import com.code44.finance.ui.accounts.AccountEditActivity;
import com.code44.finance.ui.accounts.AccountsActivity;
import com.code44.finance.ui.categories.CategoriesActivity;
import com.code44.finance.ui.categories.CategoryActivity;
import com.code44.finance.ui.categories.CategoryEditActivity;
import com.code44.finance.ui.common.navigation.NavigationFragment;
import com.code44.finance.ui.currencies.CurrenciesActivity;
import com.code44.finance.ui.currencies.CurrencyActivity;
import com.code44.finance.ui.currencies.CurrencyEditActivity;
import com.code44.finance.ui.dialogs.DatePickerDialog;
import com.code44.finance.ui.dialogs.DeleteDialogFragment;
import com.code44.finance.ui.dialogs.ListDialogFragment;
import com.code44.finance.ui.dialogs.TimePickerDialog;
import com.code44.finance.ui.overview.OverviewActivity;
import com.code44.finance.ui.playservices.GoogleApiFragment;
import com.code44.finance.ui.reports.ReportsFragment;
import com.code44.finance.ui.reports.categories.CategoriesReportActivity;
import com.code44.finance.ui.settings.SettingsActivity;
import com.code44.finance.ui.settings.about.AboutActivity;
import com.code44.finance.ui.settings.data.DataActivity;
import com.code44.finance.ui.settings.data.DataFragment;
import com.code44.finance.ui.settings.data.ExportActivity;
import com.code44.finance.ui.settings.data.ImportActivity;
import com.code44.finance.ui.settings.security.LockActivity;
import com.code44.finance.ui.settings.security.UnlockActivity;
import com.code44.finance.ui.tags.TagActivity;
import com.code44.finance.ui.tags.TagEditActivity;
import com.code44.finance.ui.tags.TagsActivityV2;
import com.code44.finance.ui.transactions.TransactionActivity;
import com.code44.finance.ui.transactions.TransactionEditActivity;
import com.code44.finance.ui.transactions.TransactionsActivity;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {
                SplashActivity.class,
                OverviewActivity.class,
                CurrenciesActivity.class,
                CurrencyActivity.class,
                CurrencyEditActivity.class,
                AccountsActivity.class,
                AccountActivity.class,
                AccountEditActivity.class,
                TransactionsActivity.class,
                TransactionActivity.class,
                TransactionEditActivity.class,
                CategoriesActivity.class,
                CategoryActivity.class,
                CategoryEditActivity.class,
                TagsActivityV2.class,
                TagActivity.class,
                TagEditActivity.class,
                SettingsActivity.class,
                DataActivity.class,
                CalculatorActivity.class,
                ExportActivity.class,
                ImportActivity.class,
                AboutActivity.class,
                CategoriesReportActivity.class,
                UnlockActivity.class,
                LockActivity.class,

                NavigationFragment.class,
                DeleteDialogFragment.class,
                ListDialogFragment.class,
                CalculatorFragment.class,
                GoogleApiFragment.class,
                DataFragment.class,
                ReportsFragment.class,
                DatePickerDialog.class,
                TimePickerDialog.class
        }
)
public class UIModule {
}
