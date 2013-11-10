package com.code44.finance.ui.currencies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.code44.finance.API;
import com.code44.finance.R;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.CurrenciesProvider;
import com.code44.finance.services.AbstractService;
import com.code44.finance.services.CurrenciesRestService;
import com.code44.finance.ui.ItemEditFragment;
import com.code44.finance.utils.CurrenciesHelper;
import de.greenrobot.event.EventBus;

import java.util.Currency;

public class CurrencyFormatFragment extends ItemEditFragment implements LoaderManager.LoaderCallbacks<Cursor>, RadioGroup.OnCheckedChangeListener, View.OnClickListener
{
    private static final String ARG_CODE = CurrencyFormatFragment.class.getName() + ".ARG_CODE";
    // -----------------------------------------------------------------------------------------------------------------
    private static final String STATE_CODE = "STATE_CODE";
    private static final String STATE_IS_CURRENT_MAIN_CURRENCY = "STATE_IS_CURRENT_MAIN_CURRENCY";
    private static final String STATE_GROUP_SEPARATOR = "STATE_GROUP_SEPARATOR";
    private static final String STATE_DECIMAL_SEPARATOR = "STATE_DECIMAL_SEPARATOR";
    private static final String STATE_DECIMALS = "STATE_DECIMALS";
    private static final String STATE_SYMBOL = "STATE_SYMBOL";
    private static final String STATE_SYMBOL_FORMAT = "STATE_SYMBOL_FORMAT";
    private static final String STATE_EXCHANGE_RATE = "STATE_EXCHANGE_RATE";
    // -----------------------------------------------------------------------------------------------------------------
    private static final int LOADER_DEFAULT_CURRENCY = 2;
    // -----------------------------------------------------------------------------------------------------------------
    private TextView code_TV;
    private TextView format_TV;
    private CheckBox default_CB;
    private TextView currentCurrency_TV;
    private RadioGroup groupSeparator_RG;
    private RadioGroup decimalSeparator_RG;
    private RadioGroup decimalsCount_RG;
    private EditText symbol_ET;
    private RadioGroup format_RG;
    private View exchangeRateContainer_V;
    private EditText exchangeRate_ET;
    // -----------------------------------------------------------------------------------------------------------------
    private String code;
    private String defaultCurrencyCode = "";
    private boolean isCurrentMainCurrency;

    public static CurrencyFormatFragment newInstance(long itemId, String code)
    {
        final Bundle args = makeArgs(itemId);
        args.putString(ARG_CODE, code);

        final CurrencyFormatFragment f = new CurrencyFormatFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get arguments
        final Bundle args = getArguments();
        code = args.getString(ARG_CODE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_currency_format, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        code_TV = (TextView) view.findViewById(R.id.code_TV);
        format_TV = (TextView) view.findViewById(R.id.format_TV);
        default_CB = (CheckBox) view.findViewById(R.id.default_CB);
        currentCurrency_TV = (TextView) view.findViewById(R.id.currentCurrency_TV);
        groupSeparator_RG = (RadioGroup) view.findViewById(R.id.groupSeparator_RG);
        decimalSeparator_RG = (RadioGroup) view.findViewById(R.id.decimalSeparator_RG);
        decimalsCount_RG = (RadioGroup) view.findViewById(R.id.decimalsCount_RG);
        symbol_ET = (EditText) view.findViewById(R.id.symbol_ET);
        format_RG = (RadioGroup) view.findViewById(R.id.format_RG);
        exchangeRateContainer_V = view.findViewById(R.id.exchangeRateContainer_V);
        exchangeRate_ET = (EditText) view.findViewById(R.id.exchangeRate_ET);
        final Button refreshRate_B = (Button) view.findViewById(R.id.refreshRate_B);

        // Setup
        groupSeparator_RG.setOnCheckedChangeListener(this);
        decimalSeparator_RG.setOnCheckedChangeListener(this);
        decimalsCount_RG.setOnCheckedChangeListener(this);
        format_RG.setOnCheckedChangeListener(this);
        symbol_ET.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                updateFormat();
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
        default_CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isCurrentMainCurrency && !isChecked)
                {
                    buttonView.setChecked(true);
                    Toast.makeText(getActivity(), R.string.l_cannot_disable_main_currency, Toast.LENGTH_LONG).show();
                }
                updateDefaultCurrency();
            }
        });
        refreshRate_B.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Loader
        getLoaderManager().initLoader(LOADER_DEFAULT_CURRENCY, null, this);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Register events
        EventBus.getDefault().register(this, CurrenciesRestService.GetExchangeRateEvent.class);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        // Unregister events
        EventBus.getDefault().unregister(this, CurrenciesRestService.GetExchangeRateEvent.class);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putString(STATE_CODE, getCurrencyCode());
        outState.putBoolean(STATE_IS_CURRENT_MAIN_CURRENCY, isCurrentMainCurrency());
        outState.putString(STATE_GROUP_SEPARATOR, getGroupSeparator());
        outState.putString(STATE_DECIMAL_SEPARATOR, getDecimalSeparator());
        outState.putInt(STATE_DECIMALS, getDecimals());
        outState.putString(STATE_SYMBOL, getSymbol());
        outState.putString(STATE_SYMBOL_FORMAT, getSymbolFormat());
        outState.putDouble(STATE_EXCHANGE_RATE, getExchangeRate());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle)
    {
        Uri uri;
        String[] projection;
        String selection;
        String[] selectionArgs;
        String sortOrder = null;

        switch (id)
        {
            case LOADER_DEFAULT_CURRENCY:
            {
                uri = CurrenciesProvider.uriCurrencies(getActivity());
                projection = new String[]{Tables.Currencies.CODE};
                selection = Tables.Currencies.IS_DEFAULT + "=?";
                selectionArgs = new String[]{"1"};
                //noinspection ConstantConditions
                return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
            }
        }

        return super.onCreateLoader(id, bundle);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        switch (cursorLoader.getId())
        {
            case LOADER_DEFAULT_CURRENCY:
                bindDefaultCurrency(cursor);
                break;
        }
        super.onLoadFinished(cursorLoader, cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        super.onLoaderReset(cursorLoader);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.refreshRate_B:
                refreshExchangeRate();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {
        updateFormat();
    }

    public String getCurrencyCode()
    {
        //noinspection ConstantConditions
        return code_TV.getText().toString();
    }

    private void setCurrencyCode(String code)
    {
        code_TV.setText(code);
    }

    public boolean isMainCurrency()
    {
        return default_CB.isChecked();
    }

    @SuppressWarnings("ConstantConditions")
    public String getGroupSeparator()
    {
        if (((RadioButton) groupSeparator_RG.getChildAt(1)).isChecked())
            return ".";
        else if (((RadioButton) groupSeparator_RG.getChildAt(2)).isChecked())
            return " ";
        else if (((RadioButton) groupSeparator_RG.getChildAt(3)).isChecked())
            return "";
        else
            return ",";
    }

    @SuppressWarnings("ConstantConditions")
    private void setGroupSeparator(String groupSeparator)
    {
        if (groupSeparator.equalsIgnoreCase(","))
            ((RadioButton) groupSeparator_RG.getChildAt(0)).setChecked(true);
        else if (groupSeparator.equalsIgnoreCase("."))
            ((RadioButton) groupSeparator_RG.getChildAt(1)).setChecked(true);
        else if (groupSeparator.equalsIgnoreCase(" "))
            ((RadioButton) groupSeparator_RG.getChildAt(2)).setChecked(true);
        else
            ((RadioButton) groupSeparator_RG.getChildAt(3)).setChecked(true);
    }

    @SuppressWarnings("ConstantConditions")
    public String getDecimalSeparator()
    {
        if (((RadioButton) decimalSeparator_RG.getChildAt(1)).isChecked())
            return ",";
        else if (((RadioButton) decimalSeparator_RG.getChildAt(2)).isChecked())
            return " ";
        else
            return ".";
    }

    @SuppressWarnings("ConstantConditions")
    private void setDecimalSeparator(String decimalSeparator)
    {
        if (decimalSeparator.equalsIgnoreCase("."))
            ((RadioButton) decimalSeparator_RG.getChildAt(0)).setChecked(true);
        else if (decimalSeparator.equalsIgnoreCase(","))
            ((RadioButton) decimalSeparator_RG.getChildAt(1)).setChecked(true);
        else
            ((RadioButton) decimalSeparator_RG.getChildAt(2)).setChecked(true);
    }

    @SuppressWarnings("ConstantConditions")
    public int getDecimals()
    {
        if (((RadioButton) decimalsCount_RG.getChildAt(1)).isChecked())
            return 1;
        else if (((RadioButton) decimalsCount_RG.getChildAt(2)).isChecked())
            return 0;
        else
            return 2;
    }

    @SuppressWarnings("ConstantConditions")
    private void setDecimals(int decimals)
    {
        if (decimals == 2)
            ((RadioButton) decimalsCount_RG.getChildAt(0)).setChecked(true);
        else if (decimals == 1)
            ((RadioButton) decimalsCount_RG.getChildAt(1)).setChecked(true);
        else
            ((RadioButton) decimalsCount_RG.getChildAt(2)).setChecked(true);
    }

    public String getSymbol()
    {
        //noinspection ConstantConditions
        return symbol_ET.getText().toString();
    }

    private void setSymbol(String symbol)
    {
        symbol_ET.setText(symbol);
    }

    @SuppressWarnings("ConstantConditions")
    public String getSymbolFormat()
    {
        if (((RadioButton) format_RG.getChildAt(1)).isChecked())
            return Tables.Currencies.SymbolFormat.RIGHT_CLOSE;
        else if (((RadioButton) format_RG.getChildAt(2)).isChecked())
            return Tables.Currencies.SymbolFormat.LEFT_FAR;
        else if (((RadioButton) format_RG.getChildAt(3)).isChecked())
            return Tables.Currencies.SymbolFormat.LEFT_CLOSE;
        else
            return Tables.Currencies.SymbolFormat.RIGHT_FAR;
    }

    @SuppressWarnings("ConstantConditions")
    private void setSymbolFormat(String symbolFormat)
    {
        if (symbolFormat.equalsIgnoreCase(Tables.Currencies.SymbolFormat.RIGHT_CLOSE))
            ((RadioButton) format_RG.getChildAt(1)).setChecked(true);
        else if (symbolFormat.equalsIgnoreCase(Tables.Currencies.SymbolFormat.LEFT_FAR))
            ((RadioButton) format_RG.getChildAt(2)).setChecked(true);
        else if (symbolFormat.equalsIgnoreCase(Tables.Currencies.SymbolFormat.LEFT_CLOSE))
            ((RadioButton) format_RG.getChildAt(3)).setChecked(true);
        else
            ((RadioButton) format_RG.getChildAt(0)).setChecked(true);
    }

    public double getExchangeRate()
    {
        if (isCurrentMainCurrency())
            return 1.0;
        else
            //noinspection ConstantConditions
            return Double.parseDouble(exchangeRate_ET.getText().toString());
    }

    private void setExchangeRate(double exchangeRate)
    {
        exchangeRate_ET.setText(String.valueOf(exchangeRate));
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(CurrenciesRestService.GetExchangeRateEvent event)
    {
        if (event.getState() == AbstractService.ServiceEvent.State.SUCCEEDED && event.getFromCode().equalsIgnoreCase(getCurrencyCode()) && event.getToCode().equalsIgnoreCase(CurrenciesHelper.getDefault(getActivity()).getMainCurrencyCode()))
            setExchangeRate(event.getExchangeRate());
    }

    @Override
    protected boolean bindItem(Cursor c, boolean isDataLoaded)
    {
        if (!isDataLoaded && c != null && c.moveToFirst())
        {
            final int iCode = c.getColumnIndex(Tables.Currencies.CODE);
            final int iIsDefault = c.getColumnIndex(Tables.Currencies.IS_DEFAULT);
            final int iGroupSeparator = c.getColumnIndex(Tables.Currencies.GROUP_SEPARATOR);
            final int iDecimalSeparator = c.getColumnIndex(Tables.Currencies.DECIMAL_SEPARATOR);
            final int iDecimals = c.getColumnIndex(Tables.Currencies.DECIMALS);
            final int iSymbol = c.getColumnIndex(Tables.Currencies.SYMBOL);
            final int iSymbolFormat = c.getColumnIndex(Tables.Currencies.SYMBOL_FORMAT);
            final int iExchangeRate = c.getColumnIndex(Tables.Currencies.EXCHANGE_RATE);

            setCurrencyCode(c.getString(iCode));
            setIsMainCurrency(c.getInt(iIsDefault) != 0);
            setIsCurrentMainCurrency(c.getInt(iIsDefault) != 0);
            setGroupSeparator(c.getString(iGroupSeparator));
            setDecimalSeparator(c.getString(iDecimalSeparator));
            setDecimals(c.getInt(iDecimals));
            setSymbol(c.getString(iSymbol));
            setSymbolFormat(c.getString(iSymbolFormat));
            setExchangeRate(c.getDouble(iExchangeRate));

            updateFormat();
            updateDefaultCurrency();

            refreshExchangeRate();
            return true;
        }
        return isDataLoaded;
    }

    @Override
    protected void restoreOrInit(long itemId, Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            setCurrencyCode(savedInstanceState.getString(STATE_CODE));
            setIsCurrentMainCurrency(savedInstanceState.getBoolean(STATE_IS_CURRENT_MAIN_CURRENCY));
            setGroupSeparator(savedInstanceState.getString(STATE_GROUP_SEPARATOR));
            setDecimalSeparator(savedInstanceState.getString(STATE_DECIMAL_SEPARATOR));
            setDecimals(savedInstanceState.getInt(STATE_DECIMALS));
            setSymbol(savedInstanceState.getString(STATE_SYMBOL));
            setSymbolFormat(savedInstanceState.getString(STATE_SYMBOL_FORMAT));
            setExchangeRate(savedInstanceState.getDouble(STATE_EXCHANGE_RATE));
        }
        else if (itemId == 0)
        {
            setCurrencyCode(code);
            setIsCurrentMainCurrency(false);
            setGroupSeparator(",");
            setDecimalSeparator(".");
            setDecimals(2);
            String symbol = "$";
            try
            {
                Currency currency = Currency.getInstance(code);
                symbol = currency.getSymbol();
            }
            catch (Exception e)
            {
                // Ignore. This is not essential.
            }
            setSymbol(symbol);
            setSymbolFormat(Tables.Currencies.SymbolFormat.RIGHT_FAR);
            setExchangeRate(1.0);

            refreshExchangeRate();
        }
        else
        {
            refreshExchangeRate();
        }
    }

    @Override
    protected boolean onSave(Context context, long itemId)
    {
        // Ignore this
        return false;
    }

    @Override
    protected boolean onDiscard()
    {
        // Ignore this
        return false;
    }

    @Override
    protected Loader<Cursor> createItemLoader(Context context, long itemId)
    {
        Uri uri = CurrenciesProvider.uriCurrency(getActivity(), itemId);
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
    }

    private void refreshExchangeRate()
    {
        API.getExchangeRate(getActivity(), getCurrencyCode());
    }

    private void bindDefaultCurrency(Cursor c)
    {
        if (c != null && c.moveToFirst())
        {
            final int iCode = c.getColumnIndex(Tables.Currencies.CODE);

            defaultCurrencyCode = c.getString(iCode);
            updateDefaultCurrency();
        }
    }

    private void setIsMainCurrency(boolean isMainCurrency)
    {
        default_CB.setChecked(isMainCurrency);
    }

    private boolean isCurrentMainCurrency()
    {
        return isCurrentMainCurrency;
    }

    private void setIsCurrentMainCurrency(boolean isCurrentMainCurrency)
    {
        this.isCurrentMainCurrency = isCurrentMainCurrency;
        if (isCurrentMainCurrency)
        {
            currentCurrency_TV.setVisibility(View.GONE);
            exchangeRateContainer_V.setVisibility(View.GONE);
        }
        else
        {
            currentCurrency_TV.setVisibility(View.VISIBLE);
            exchangeRateContainer_V.setVisibility(View.VISIBLE);
        }
    }

    private void updateFormat()
    {
        final SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append("1").append(getGroupSeparator()).append("000");
        if (getDecimals() > 0)
            sb.append(getDecimalSeparator());

        if (getDecimals() == 2)
            sb.append("00");
        else if (getDecimals() == 1)
            sb.append("0");

        final SpannableStringBuilder ssb = new SpannableStringBuilder(getSymbol());
        ssb.setSpan(new TypefaceSpan("sans-serif-light"), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        final String symbolFormat = getSymbolFormat();
        if (symbolFormat.equalsIgnoreCase(Tables.Currencies.SymbolFormat.RIGHT_CLOSE))
            sb.append(ssb);
        else if (symbolFormat.equalsIgnoreCase(Tables.Currencies.SymbolFormat.LEFT_FAR))
            sb.insert(0, " ").insert(0, ssb);
        else if (symbolFormat.equalsIgnoreCase(Tables.Currencies.SymbolFormat.LEFT_CLOSE))
            sb.insert(0, ssb);
        else
            sb.append(" ").append(ssb);

        format_TV.setText(sb);
    }

    private void updateDefaultCurrency()
    {
        if (!getCurrencyCode().equalsIgnoreCase(defaultCurrencyCode) && isMainCurrency())
        {
            currentCurrency_TV.setTextColor(getResources().getColor(R.color.text_red));
            currentCurrency_TV.setText(getString(R.string.f_x_will_change_y_main_currency, getCurrencyCode(), defaultCurrencyCode));
        }
        else
        {
            currentCurrency_TV.setTextColor(getResources().getColor(R.color.text_secondary));
            currentCurrency_TV.setText(getString(R.string.f_current_main_currency_x, defaultCurrencyCode));
        }
    }
}