package com.code44.finance.ui.currencies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.api.currencies.CurrenciesAsyncApi;
import com.code44.finance.api.currencies.CurrencyRequest;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.ui.ModelFragment;
import com.code44.finance.utils.MoneyFormatter;

import de.greenrobot.event.EventBus;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class CurrencyFragment extends ModelFragment<Currency> {
    private SmoothProgressBar loading_SPB;
    private TextView code_TV;
    private TextView format_TV;
    private TextView mainFormat_TV;
    private TextView exchangeRate_TV;

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
        mainFormat_TV = (TextView) view.findViewById(R.id.mainFormat_TV);
        exchangeRate_TV = (TextView) view.findViewById(R.id.exchangeRate_TV);
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
            mainFormat_TV.setVisibility(View.GONE);
        } else {
            final SpannableStringBuilder ssb = new SpannableStringBuilder(currency.getCode() + " \u2192 " + Currency.getDefault().getCode());
            ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_accent)), ssb.length() - 3, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            code_TV.setText(ssb);
            code_TV.setTextColor(getResources().getColor(R.color.text_primary));
            exchangeRate_TV.setText(String.valueOf(currency.getExchangeRate()));
            mainFormat_TV.setText("= " + MoneyFormatter.format(Currency.getDefault(), (long) (100000 * currency.getExchangeRate())));
            mainFormat_TV.setVisibility(View.VISIBLE);
        }
        format_TV.setText(MoneyFormatter.format(currency, 100000));
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
}
