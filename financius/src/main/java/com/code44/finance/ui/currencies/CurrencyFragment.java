package com.code44.finance.ui.currencies;

import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.adapters.CurrencyAccountsAdapter;
import com.code44.finance.api.currencies.CurrenciesAsyncApi;
import com.code44.finance.api.currencies.CurrencyRequest;
import com.code44.finance.common.model.AccountOwner;
import com.code44.finance.common.model.ModelState;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.ui.ModelFragment;
import com.code44.finance.utils.MoneyFormatter;
import com.code44.finance.views.FabImageButton;

import de.greenrobot.event.EventBus;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class CurrencyFragment extends ModelFragment<Currency> implements View.OnClickListener {
    private static final int LOADER_ACCOUNTS = 1;

    private TextView code_TV;
    private TextView format_TV;
    private TextView exchangeRate_TV;
    private SmoothProgressBar loading_SPB;
    private FabImageButton refreshRate_IB;

    private CurrencyAccountsAdapter adapter;

    public static CurrencyFragment newInstance(long currencyId) {
        final Bundle args = makeArgs(currencyId);

        final CurrencyFragment fragment = new CurrencyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_currency, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        code_TV = (TextView) view.findViewById(R.id.code_TV);
        format_TV = (TextView) view.findViewById(R.id.format_TV);
        exchangeRate_TV = (TextView) view.findViewById(R.id.exchangeRate_TV);
        loading_SPB = (SmoothProgressBar) view.findViewById(R.id.loading_SPB);
        refreshRate_IB = (FabImageButton) view.findViewById(R.id.refreshRate_IB);
        final ListView list_V = (ListView) view.findViewById(R.id.list_V);

        // Setup
        adapter = new CurrencyAccountsAdapter(getActivity());
        list_V.setAdapter(adapter);
        refreshRate_IB.setOnClickListener(this);
        //noinspection ConstantConditions
        getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    //noinspection deprecation
                    getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                ((ViewGroup.MarginLayoutParams) refreshRate_IB.getLayoutParams()).bottomMargin = -refreshRate_IB.getHeight() / 2;
            }
        });
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
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //noinspection ConstantConditions
        menu.findItem(R.id.action_delete).setVisible(model != null && !model.isDefault());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ACCOUNTS) {
            return Query.create()
                    .projectionId(Tables.Accounts.ID)
                    .projection(Tables.Accounts.PROJECTION)
                    .projection(Tables.Currencies.PROJECTION)
                    .selection(Tables.Accounts.OWNER + "=?", String.valueOf(AccountOwner.USER.asInt()))
                    .selection(" and " + Tables.Accounts.MODEL_STATE + "=?", String.valueOf(ModelState.NORMAL.asInt()))
                    .asCursorLoader(getActivity(), AccountsProvider.uriAccounts());
        }

        return super.onCreateLoader(id, args);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_ACCOUNTS) {
            adapter.swapCursor(data);
            return;
        }
        super.onLoadFinished(loader, data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_ACCOUNTS) {
            adapter.swapCursor(null);
            return;
        }
        super.onLoaderReset(loader);
    }

    @Override
    protected Uri getUri(long modelId) {
        return CurrenciesProvider.uriCurrency(modelId);
    }

    @Override
    protected Query getQuery() {
        return Query.create()
                .projectionId(Tables.Currencies.ID)
                .projection(Tables.Currencies.PROJECTION);
    }

    @Override
    protected Currency getModelFrom(Cursor cursor) {
        return Currency.from(cursor);
    }

    @Override
    protected void onModelLoaded(Currency currency) {
        adapter.setCurrency(currency);
        code_TV.setText(currency.getCode());
        if (currency.isDefault()) {
            exchangeRate_TV.setText(R.string.main_currency);
        } else {
            exchangeRate_TV.setText(String.valueOf(currency.getExchangeRate()));
        }
        format_TV.setText(MoneyFormatter.format(currency, 100000));
        refreshRate_IB.setVisibility(model != null && !model.isDefault() ? View.VISIBLE : View.GONE);

        getActivity().invalidateOptionsMenu();

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refreshRate_IB:
                refreshRate();
                break;
        }
    }
}
