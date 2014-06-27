package com.code44.finance.ui.currencies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.api.currencies.CurrenciesAsyncApi;
import com.code44.finance.api.currencies.CurrencyRequest;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Account;
import com.code44.finance.data.db.model.BaseModel;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.ui.ModelFragment;
import com.code44.finance.utils.MoneyFormatter;

import de.greenrobot.event.EventBus;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class CurrencyFragment extends ModelFragment<Currency> {
    private static final int LOADER_ACCOUNTS = 1;

    private SmoothProgressBar loading_SPB;
    private TextView code_TV;
    private TextView format_TV;
    private TextView exchangeRate_TV;
    private LinearLayout accountsContainer_V;

    private int accountsContainerStaticViewCount;

    public static CurrencyFragment newInstance(long currencyId) {
        final Bundle args = makeArgs(currencyId);

        final CurrencyFragment fragment = new CurrencyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_currency, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        loading_SPB = (SmoothProgressBar) view.findViewById(R.id.loading_SPB);
        code_TV = (TextView) view.findViewById(R.id.code_TV);
        format_TV = (TextView) view.findViewById(R.id.format_TV);
        exchangeRate_TV = (TextView) view.findViewById(R.id.exchangeRate_TV);
        accountsContainer_V = (LinearLayout) view.findViewById(R.id.accountsContainer_V);

        // Setup
        accountsContainerStaticViewCount = accountsContainer_V.getChildCount();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.currency, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //noinspection ConstantConditions
        menu.findItem(R.id.action_refresh_rate).setVisible(model != null && !model.isDefault());
        //noinspection ConstantConditions
        menu.findItem(R.id.action_delete).setVisible(model != null && !model.isDefault());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh_rate:
                refreshRate();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ACCOUNTS) {
            return Query.get()
                    .projectionId(Tables.Accounts.ID)
                    .projection(Tables.Accounts.PROJECTION)
                    .projection(Tables.Currencies.PROJECTION)
                    .selection(Tables.Accounts.OWNER + "=?", String.valueOf(Account.Owner.USER.asInt()))
                    .selection(" and " + Tables.Accounts.ITEM_STATE + "=?", String.valueOf(BaseModel.ItemState.NORMAL.asInt()))
                    .asCursorLoader(getActivity(), AccountsProvider.uriAccounts());
        }

        return super.onCreateLoader(id, args);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_ACCOUNTS) {
            onAccountsLoaded(data);
            return;
        }
        super.onLoadFinished(loader, data);
    }

    @Override
    protected Uri getUri(long modelId) {
        return CurrenciesProvider.uriCurrency(modelId);
    }

    @Override
    protected Currency getModelFrom(Cursor cursor) {
        return Currency.from(cursor);
    }

    @Override
    protected void onModelLoaded(Currency currency) {
        getActivity().supportInvalidateOptionsMenu();
        if (currency.isDefault()) {
            code_TV.setText(currency.getCode());
            code_TV.setTextColor(getResources().getColor(R.color.text_accent));
            exchangeRate_TV.setText(R.string.main_currency);
        } else {
            final SpannableStringBuilder ssb = new SpannableStringBuilder(currency.getCode() + " \u2192 " + Currency.getDefault().getCode());
            ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_secondary)), ssb.length() - 3, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            code_TV.setText(ssb);
            code_TV.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
            exchangeRate_TV.setText(String.valueOf(currency.getExchangeRate()));
        }
        format_TV.setText(MoneyFormatter.format(currency, 100000));

        // Loader
        getLoaderManager().restartLoader(LOADER_ACCOUNTS, null, this);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(CurrencyRequest.CurrencyRequestEvent event) {
        updateRefreshView();
    }

    private void updateRefreshView() {
        loading_SPB.setVisibility(EventBus.getDefault().getStickyEvent(CurrencyRequest.CurrencyRequestEvent.class) != null ? View.VISIBLE : View.INVISIBLE);
    }

    private void refreshRate() {
        CurrenciesAsyncApi.get().updateExchangeRate(model.getCode());
    }

    private void onAccountsLoaded(Cursor cursor) {
        if (accountsContainer_V.getChildCount() > accountsContainerStaticViewCount) {
            accountsContainer_V.removeViews(accountsContainerStaticViewCount, accountsContainer_V.getChildCount() - accountsContainerStaticViewCount);
        }

        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        if (cursor.moveToFirst()) {
            do {
                final Account account = Account.from(cursor);
                final View view = inflater.inflate(R.layout.li_currency_account, accountsContainer_V, false);
                ((TextView) view.findViewById(R.id.title_TV)).setText(account.getTitle() + ", " + account.getCurrency().getCode());
                final Button currency_B = (Button) view.findViewById(R.id.currency_B);
                if (account.getCurrency().getId() != model.getId()) {
                    currency_B.setText(getString(R.string.f_change_to_x, model.getCode()));
                    currency_B.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            account.setCurrency(model);
                            DataStore.insert().model(account).into(AccountsProvider.uriAccounts());
                        }
                    });
                } else {
                    currency_B.setVisibility(View.GONE);
                }
                accountsContainer_V.addView(view);
            } while (cursor.moveToNext());
        }
    }
}
