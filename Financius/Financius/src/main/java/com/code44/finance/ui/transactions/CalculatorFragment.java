package com.code44.finance.ui.transactions;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.code44.finance.R;
import com.code44.finance.ui.BaseFragment;
import com.code44.finance.utils.AnimUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.Locale;

import bsh.EvalError;
import bsh.Interpreter;

public class CalculatorFragment extends BaseFragment implements OnClickListener, View.OnLongClickListener
{
    private static final String ARG_AMOUNT = "ARG_AMOUNT";
    private static final String ARG_ALLOW_NEGATIVE = "ARG_ALLOW_NEGATIVE";
    private static final String ARG_RETURN_TWO_DECIMALS = "ARG_RETURN_TWO_DECIMALS";
    // -----------------------------------------------------------------------------------------------------------------
    private static final String STATE_PARTS = "STATE_PARTS";
    private static final String STATE_NEGATIVE_ERROR_COUNT = "STATE_NEGATIVE_ERROR_COUNT";
    // -----------------------------------------------------------------------------------------------------------------
    private TextView amount_TV;
    private ImageButton delete_B;
    private Button tl_B;
    private Button tm_B;
    private Button tr_B;
    private Button ml_B;
    private Button mm_B;
    private Button mr_B;
    private Button bl_B;
    private Button bm_B;
    private Button br_B;
    private Button zero_B;
    private Button done_B;
    private Button decimal_B;
    private Button div_B;
    private Button mul_B;
    private Button sub_B;
    private Button add_B;
    private CalculatorListener listener;
    private LinkedList<CalcPart> calcPartList = new LinkedList<CalcPart>();
    private CalcPart currentPart;
    private boolean isNumbersBottomToUp = true;
    private boolean allowNegative;
    private boolean returnTwoDecimals;
    private int negativeErrorCount = 0;

    public static CalculatorFragment newInstance(double amount, boolean allowNegative, boolean returnTwoDecimals)
    {
        final Bundle args = new Bundle();
        args.putDouble(ARG_AMOUNT, amount);
        args.putBoolean(ARG_ALLOW_NEGATIVE, allowNegative);
        args.putBoolean(ARG_RETURN_TWO_DECIMALS, returnTwoDecimals);

        final CalculatorFragment f = new CalculatorFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        if (activity instanceof CalculatorListener)
            listener = (CalculatorListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // Get arguments
        final Bundle args = getArguments();
        allowNegative = args.getBoolean(ARG_ALLOW_NEGATIVE);
        returnTwoDecimals = args.getBoolean(ARG_RETURN_TWO_DECIMALS);

        // Only when state is null, apply passed amount
        if (savedInstanceState == null)
        {
            // Only initialise when we don't have saved state. If we have saved state, everything will be initialized in onActivityCreated method.
            final double amount = args.getDouble(ARG_AMOUNT, 0);
            currentPart = new CalcPart(getActivity(), CalcPart.Type.NUMBER, allowNegative ? amount : Math.max(amount, 0));
        }
        else
        {
            // Restore state
            negativeErrorCount = savedInstanceState.getInt(STATE_NEGATIVE_ERROR_COUNT, 0);
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_calculator, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        amount_TV = (TextView) view.findViewById(R.id.amount_TV);
        tl_B = (Button) view.findViewById(R.id.tl_B);
        tm_B = (Button) view.findViewById(R.id.tm_B);
        tr_B = (Button) view.findViewById(R.id.tr_B);
        ml_B = (Button) view.findViewById(R.id.ml_B);
        mm_B = (Button) view.findViewById(R.id.mm_B);
        mr_B = (Button) view.findViewById(R.id.mr_B);
        bl_B = (Button) view.findViewById(R.id.bl_B);
        bm_B = (Button) view.findViewById(R.id.bm_B);
        br_B = (Button) view.findViewById(R.id.br_B);
        zero_B = (Button) view.findViewById(R.id.zero_B);
        done_B = (Button) view.findViewById(R.id.done_B);
        decimal_B = (Button) view.findViewById(R.id.decimal_B);
        delete_B = (ImageButton) view.findViewById(R.id.delete_B);
        div_B = (Button) view.findViewById(R.id.div_B);
        mul_B = (Button) view.findViewById(R.id.mul_B);
        sub_B = (Button) view.findViewById(R.id.sub_B);
        add_B = (Button) view.findViewById(R.id.add_B);

        // Setup
        tl_B.setOnClickListener(this);
        tm_B.setOnClickListener(this);
        tr_B.setOnClickListener(this);
        ml_B.setOnClickListener(this);
        mm_B.setOnClickListener(this);
        mr_B.setOnClickListener(this);
        bl_B.setOnClickListener(this);
        bm_B.setOnClickListener(this);
        br_B.setOnClickListener(this);
        zero_B.setOnClickListener(this);
        done_B.setOnClickListener(this);
        decimal_B.setOnClickListener(this);
        delete_B.setOnClickListener(this);
        delete_B.setOnLongClickListener(this);
        div_B.setOnClickListener(this);
        mul_B.setOnClickListener(this);
        sub_B.setOnClickListener(this);
        add_B.setOnClickListener(this);
        setupViews();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Restore parts if necessary
        if (currentPart == null)
        {
            if (savedInstanceState == null)
            {
                currentPart = new CalcPart(getActivity(), CalcPart.Type.NUMBER);
            }
            else
            {
                String[] parts = savedInstanceState.getStringArray(STATE_PARTS);
                for (int i = 0; i < parts.length - 1; i++)
                {
                    calcPartList.add(CalcPart.fromExpression(getActivity(), parts[i]));
                }
                currentPart = CalcPart.fromExpression(getActivity(), parts[parts.length - 1]);
            }
        }

        updateViews();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        String[] parts = new String[calcPartList.size() + 1];
        CalcPart part;
        for (int i = 0; i < calcPartList.size(); i++)
        {
            part = calcPartList.get(i);
            parts[i] = part.getExpression();
        }
        parts[calcPartList.size()] = currentPart.getExpression();
        outState.putStringArray(STATE_PARTS, parts);
        outState.putInt(STATE_NEGATIVE_ERROR_COUNT, negativeErrorCount);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tl_B:
                onNumberClick(isNumbersBottomToUp ? 7 : 1);
                break;

            case R.id.tm_B:
                onNumberClick(isNumbersBottomToUp ? 8 : 2);
                break;

            case R.id.tr_B:
                onNumberClick(isNumbersBottomToUp ? 9 : 3);
                break;

            case R.id.ml_B:
                onNumberClick(4);
                break;

            case R.id.mm_B:
                onNumberClick(5);
                break;

            case R.id.mr_B:
                onNumberClick(6);
                break;

            case R.id.bl_B:
                onNumberClick(isNumbersBottomToUp ? 1 : 7);
                break;

            case R.id.bm_B:
                onNumberClick(isNumbersBottomToUp ? 2 : 8);
                break;

            case R.id.br_B:
                onNumberClick(isNumbersBottomToUp ? 3 : 9);
                break;

            case R.id.zero_B:
                onNumberClick(0);
                break;

            case R.id.decimal_B:
                onDecimalClick();
                break;

            case R.id.done_B:
                onDoneClick();
                break;

            case R.id.delete_B:
                onDeleteClick();
                break;

            case R.id.div_B:
                onActionClick(CalcPart.Type.DIV);
                break;

            case R.id.mul_B:
                onActionClick(CalcPart.Type.MUL);
                break;

            case R.id.sub_B:
                onActionClick(CalcPart.Type.SUB);
                break;

            case R.id.add_B:
                onActionClick(CalcPart.Type.ADD);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v)
    {
        switch (v.getId())
        {
            case R.id.delete_B:
                calcPartList.clear();
                currentPart = new CalcPart(getActivity(), CalcPart.Type.NUMBER, 0.0);
                updateViews();
                break;
        }
        return false;
    }

    private void setupViews()
    {
        zero_B.setText(getString(R.string.calc_0));
        tl_B.setText(getString(isNumbersBottomToUp ? R.string.calc_7 : R.string.calc_1));
        tm_B.setText(getString(isNumbersBottomToUp ? R.string.calc_8 : R.string.calc_2));
        tr_B.setText(getString(isNumbersBottomToUp ? R.string.calc_9 : R.string.calc_3));
        ml_B.setText(getString(R.string.calc_4));
        mm_B.setText(getString(R.string.calc_5));
        mr_B.setText(getString(R.string.calc_6));
        bl_B.setText(getString(isNumbersBottomToUp ? R.string.calc_1 : R.string.calc_7));
        bm_B.setText(getString(isNumbersBottomToUp ? R.string.calc_2 : R.string.calc_8));
        br_B.setText(getString(isNumbersBottomToUp ? R.string.calc_3 : R.string.calc_9));
    }

    private void updateViews()
    {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        for (CalcPart part : calcPartList)
        {
            ssb.append(part.getFormattedExpression());
        }
        ssb.append(currentPart.getFormattedExpression());
        amount_TV.setText(ssb);

        done_B.setText(calcPartList.size() == 0 || (calcPartList.size() == 1 && calcPartList.getFirst().getType() == CalcPart.Type.SUB) ? R.string.calc_done
                : R.string.calc_equals);
    }

    private void onDoneClick()
    {
        if (calcPartList.size() == 0 || (calcPartList.size() == 1 && calcPartList.getFirst().getType() == CalcPart.Type.SUB))
        {
            // We are in normal entry mode
            double result = (calcPartList.size() == 1 && calcPartList.getFirst().getType() == CalcPart.Type.SUB) ? -1 * currentPart.getNumber(returnTwoDecimals) : currentPart.getNumber(returnTwoDecimals);

            // Return result
            if (listener != null)
                listener.onAmountSet(result == Double.POSITIVE_INFINITY || result == Double.NEGATIVE_INFINITY ? 0 : result);
        }
        else
        {
            // We are in calculation mode
            final StringBuilder sb = new StringBuilder();
            for (CalcPart part : calcPartList)
            {
                sb.append(part.getExpression());
            }
            sb.append(currentPart.getExpression());

            final Interpreter interpreter = new Interpreter();
            Double result = null;
            try
            {
                result = (Double) interpreter.eval(sb.toString());
            }
            catch (EvalError e)
            {
                e.printStackTrace();
                result = null;
            }

            currentPart = new CalcPart(getActivity(), CalcPart.Type.NUMBER, result);
            calcPartList.clear();
            updateViews();
        }
    }

    private void onDecimalClick()
    {
        if (currentPart.getType() == CalcPart.Type.NUMBER)
        {
            // Current part is a number
            currentPart.addDecimal();
        }
        else
        {
            // Current part is an action
            calcPartList.add(currentPart);
            currentPart = new CalcPart(getActivity(), CalcPart.Type.NUMBER);
            currentPart.addDecimal();
        }
        updateViews();
    }

    private void onDeleteClick()
    {
        if (currentPart.getType() == CalcPart.Type.NUMBER)
        {
            // Current part is a number
            currentPart.removeDigit();

            // Assign new current part if necessary
            if (currentPart.isEmpty() && calcPartList.size() > 0)
                currentPart = calcPartList.removeLast();
        }
        else
        {
            // Current part is an action
            if (calcPartList.size() > 0)
                currentPart = calcPartList.removeLast();
            currentPart = new CalcPart(getActivity(), CalcPart.Type.NUMBER, 0.0);
        }
        updateViews();
    }

    private void onNumberClick(int number)
    {
        if (currentPart.getType() == CalcPart.Type.NUMBER)
        {
            // Current part is a number
            currentPart.addDigit(number);
        }
        else
        {
            // Current part is an action
            calcPartList.add(currentPart);
            currentPart = new CalcPart(getActivity(), CalcPart.Type.NUMBER);
            currentPart.addDigit(number);
        }
        updateViews();
    }

    private void onActionClick(CalcPart.Type type)
    {
        switch (type)
        {
            case DIV:
            case MUL:
            case ADD:
            case SUB:
            {
                if (currentPart.getType() == CalcPart.Type.NUMBER)
                {
                    // Current part is a number
                    if (currentPart.getNumber(false) != 0.0)
                    {
                        // Number is entered
                        calcPartList.add(currentPart);
                        currentPart = new CalcPart(getActivity(), type);
                    }
                    else
                    {
                        // Number is not entered
                        if (type == CalcPart.Type.SUB && calcPartList.size() == 0)
                        {
                            // Action is subtraction and nothing has been entered at all
                            calcPartList.add(new CalcPart(getActivity(), type));
                        }
                    }
                }
                else
                {
                    // Current part is an action
                    if (type == CalcPart.Type.SUB && (currentPart.getType() == CalcPart.Type.DIV || currentPart.getType() == CalcPart.Type.MUL))
                    {
                        // This is a subtraction and previous action was division or multiplication.
                        calcPartList.add(currentPart);
                        currentPart = new CalcPart(getActivity(), type);
                    }
                    else
                    {
                        currentPart = new CalcPart(getActivity(), type);
                        if (calcPartList.size() > 0 && calcPartList.getLast().getType() != CalcPart.Type.NUMBER)
                        {
                            // Was two actions in a row. This might happen for example when we have 5.45÷-
                            calcPartList.removeLast();
                        }
                    }
                }
                break;
            }

            default:
                break;
        }
        updateViews();
    }

    public static interface CalculatorListener
    {
        public void onAmountSet(double amount);
    }

    private static class CalcPart
    {
        private static final int MAX_FRACTION = 10;
        private final Type type;
        private final CharSequence div;
        private final CharSequence mul;
        private final CharSequence sub;
        private final CharSequence add;
        private final TypefaceSpan integerTypefaceSpan;
        private final TypefaceSpan fractionTypefaceSpan;
        private final ForegroundColorSpan decimalOnColorSpan;
        private String number = null;

        public CalcPart(Context context, Type type)
        {
            this(context, type, null);
        }

        public CalcPart(Context context, Type type, Double value)
        {
            this.type = type;
            final ForegroundColorSpan span = new ForegroundColorSpan(context.getResources().getColor(R.color.text_yellow));
            SpannableStringBuilder ssb;

            ssb = new SpannableStringBuilder(context.getString(R.string.calc_div));
            ssb.setSpan(span, 0, ssb.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            div = ssb;

            ssb = new SpannableStringBuilder(context.getString(R.string.calc_mul));
            ssb.setSpan(span, 0, ssb.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            mul = ssb;

            ssb = new SpannableStringBuilder(context.getString(R.string.calc_sub));
            ssb.setSpan(span, 0, ssb.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            sub = ssb;

            ssb = new SpannableStringBuilder(context.getString(R.string.calc_add));
            ssb.setSpan(span, 0, ssb.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            add = ssb;

            integerTypefaceSpan = new TypefaceSpan("sans-serif");
            fractionTypefaceSpan = new TypefaceSpan("sans-serif-light");
            decimalOnColorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.text_yellow));

            if (value != null && value != 0.0)
            {
                final NumberFormat format = NumberFormat.getInstance(Locale.US);
                format.setGroupingUsed(false);
                format.setMaximumFractionDigits(MAX_FRACTION);
                number = format.format(value).replace(",", ".");
            }

            if (!TextUtils.isEmpty(number) && (number.startsWith("0") || number.contains(".")))
            {
                // Remove trailing '0' and '.'
                int cutCount = 0;
                for (int i = number.length() - 1; i >= 0; i--)
                {
                    if (number.charAt(i) == '0')
                        cutCount++;
                    else if (number.charAt(i) == '.')
                    {
                        cutCount++;
                        break;
                    }
                    else
                        break;
                }
                if (cutCount > 0)
                    number = number.substring(0, number.length() - cutCount);

                // Remove trailing fraction to MAX_FRACTION digits count
                if (number.contains(".") && number.split("\\.").length == 2 && number.split("\\.")[1].length() > MAX_FRACTION)
                    number.substring(0, number.length() - (number.split("\\.")[1].length() - MAX_FRACTION));

                if (TextUtils.isEmpty(number))
                    number = null;
            }
        }

        public static CalcPart fromExpression(Context context, String expression)
        {
            if (expression.equalsIgnoreCase("/"))
                return new CalcPart(context, Type.DIV);
            if (expression.equalsIgnoreCase("*"))
                return new CalcPart(context, Type.MUL);
            if (expression.equalsIgnoreCase("-"))
                return new CalcPart(context, Type.SUB);
            if (expression.equalsIgnoreCase("+"))
                return new CalcPart(context, Type.ADD);
            else
                return new CalcPart(context, Type.NUMBER, Double.parseDouble(expression));
        }

        public Type getType()
        {
            return type;
        }

        public double getNumber(boolean removeTrailingFraction)
        {
            String actualNumber = number;

            // Check if number is empty
            if (TextUtils.isEmpty(actualNumber) || actualNumber.equalsIgnoreCase(".") || actualNumber.equalsIgnoreCase("-"))
            {
                // Number is empty or contains only '.'
                actualNumber = "0";
            }

            // Check if number is just symbols
            if (actualNumber.contains("-"))
            {
                // Number contains '-'
                if (actualNumber.length() == 2 && actualNumber.endsWith("."))
                {
                    actualNumber = "0";
                }
            }

            // Check if number contains '.'
            if (actualNumber.contains("."))
            {
                // Number contains '.'

                if (actualNumber.startsWith("."))
                    actualNumber = "0" + actualNumber;

                if (actualNumber.endsWith("."))
                    actualNumber = actualNumber + "0";

                String[] split = actualNumber.split("\\.");
                if (removeTrailingFraction && split.length == 2 && split[1].length() > 2)
                    actualNumber = actualNumber.substring(0, actualNumber.length() - split[1].length() + 2);
            }

            double result = 0;
            try
            {
                result = NumberFormat.getInstance(Locale.US).parse(actualNumber).doubleValue();
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            return result;
        }

        public void addDigit(int digit)
        {
            if (TextUtils.isEmpty(number))
                number = String.valueOf(digit);
            else if (!(number.equals("0") && digit == 0)
                    && (!number.contains(".") || number.endsWith(".") || number.length() - number.indexOf(".") - 1 < MAX_FRACTION))
                number = number + String.valueOf(digit);
        }

        public void removeDigit()
        {
            if (!TextUtils.isEmpty(number))
                number = number.substring(0, number.length() - 1);

            if (number != null && number.length() == 0)
                number = null;
        }

        public void addDecimal()
        {
            if (TextUtils.isEmpty(number))
                number = ".";
            else if (!number.contains("."))
                number = number + ".";
        }

        public void clearValue()
        {
            number = null;
        }

        public boolean hasDecimalSeparator()
        {
            return !TextUtils.isEmpty(number) && number.contains(".");
        }

        public boolean isEmpty()
        {
            return TextUtils.isEmpty(number);
        }

        public CharSequence getFormattedExpression()
        {
            switch (type)
            {
                case DIV:
                    return div;
                case MUL:
                    return mul;
                case SUB:
                    return sub;
                case ADD:
                    return add;
                default:
                {
                    // Get the number of current part
                    final double amount = getNumber(false);

                    // Prepare NumberFormat and format number
                    final DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance();
                    decimalFormat.setMinimumFractionDigits(Math.min(MAX_FRACTION, !isEmpty() && hasDecimalSeparator() && number.split("\\.").length == 2 ? number.split("\\.")[1].length() : 0));
                    decimalFormat.setMaximumFractionDigits(MAX_FRACTION);
                    final String formattedNumber = decimalFormat.format(amount);

                    // Find the index of decimal
                    int decimalIndex = formattedNumber.indexOf(decimalFormat.getDecimalFormatSymbols().getDecimalSeparator());

                    // Set styles on formatted number
                    final SpannableStringBuilder ssb = new SpannableStringBuilder(decimalFormat.format(amount));
                    if (decimalIndex == -1 && hasDecimalSeparator())
                    {
                        ssb.append(decimalFormat.getDecimalFormatSymbols().getDecimalSeparator());
                        decimalIndex = ssb.length() - 1;
                    }
                    ssb.setSpan(integerTypefaceSpan, 0, decimalIndex > 0 ? decimalIndex : ssb.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (decimalIndex > 0)
                    {
                        ssb.setSpan(decimalOnColorSpan, decimalIndex, decimalIndex + 1, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.setSpan(fractionTypefaceSpan, decimalIndex + 1, ssb.length(),
                                SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    return ssb;
                }
            }
        }

        public String getExpression()
        {
            switch (type)
            {
                case DIV:
                    return "/";
                case MUL:
                    return "*";
                case SUB:
                    return "-";
                case ADD:
                    return "+";
                default:
                    return String.valueOf(getNumber(false));
            }
        }

        public enum Type
        {
            DIV, MUL, SUB, ADD, NUMBER
        }
    }
}