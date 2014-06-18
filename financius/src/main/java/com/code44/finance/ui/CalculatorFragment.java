package com.code44.finance.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.utils.Calculator;

public class CalculatorFragment extends BaseFragment implements View.OnClickListener, View.OnLongClickListener {
    private static final String ARG_VALUE = "ARG_VALUE";

    private final Calculator calculator = new Calculator();

    private TextView result_TV;

    private CalculatorListener listener;

    public static CalculatorFragment newInstance(long value) {
        final Bundle args = new Bundle();
        args.putLong(ARG_VALUE, value);

        final CalculatorFragment fragment = new CalculatorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof CalculatorListener) {
            listener = (CalculatorListener) activity;
        } else {
            throw new IllegalArgumentException("Activity " + activity.getClass().getSimpleName() + " must implement " + CalculatorListener.class.getSimpleName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calculator, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        result_TV = (TextView) view.findViewById(R.id.result_TV);
        final ImageButton delete_B = (ImageButton) view.findViewById(R.id.delete_B);
        final Button divide_B = (Button) view.findViewById(R.id.divide_B);
        final Button multiply_B = (Button) view.findViewById(R.id.multiply_B);
        final Button minus_B = (Button) view.findViewById(R.id.minus_B);
        final Button plus_B = (Button) view.findViewById(R.id.plus_B);
        final Button dot_B = (Button) view.findViewById(R.id.dot_B);
        final Button equals_B = (Button) view.findViewById(R.id.equals_B);
        final Button number0_B = (Button) view.findViewById(R.id.number0_B);
        final Button number1_B = (Button) view.findViewById(R.id.number1_B);
        final Button number2_B = (Button) view.findViewById(R.id.number2_B);
        final Button number3_B = (Button) view.findViewById(R.id.number3_B);
        final Button number4_B = (Button) view.findViewById(R.id.number4_B);
        final Button number5_B = (Button) view.findViewById(R.id.number5_B);
        final Button number6_B = (Button) view.findViewById(R.id.number6_B);
        final Button number7_B = (Button) view.findViewById(R.id.number7_B);
        final Button number8_B = (Button) view.findViewById(R.id.number8_B);
        final Button number9_B = (Button) view.findViewById(R.id.number9_B);

        // Setup
        delete_B.setOnLongClickListener(this);
        delete_B.setOnClickListener(this);
        divide_B.setOnClickListener(this);
        multiply_B.setOnClickListener(this);
        minus_B.setOnClickListener(this);
        plus_B.setOnClickListener(this);
        dot_B.setOnClickListener(this);
        equals_B.setOnClickListener(this);
        number0_B.setOnClickListener(this);
        number1_B.setOnClickListener(this);
        number2_B.setOnClickListener(this);
        number3_B.setOnClickListener(this);
        number4_B.setOnClickListener(this);
        number5_B.setOnClickListener(this);
        number6_B.setOnClickListener(this);
        number7_B.setOnClickListener(this);
        number8_B.setOnClickListener(this);
        number9_B.setOnClickListener(this);

        divide_B.setTag("÷");
        multiply_B.setTag("×");
        minus_B.setTag("-");
        plus_B.setTag("+");
        dot_B.setTag(".");
        number0_B.setTag("0");
        number1_B.setTag("1");
        number2_B.setTag("2");
        number3_B.setTag("3");
        number4_B.setTag("4");
        number5_B.setTag("5");
        number6_B.setTag("6");
        number7_B.setTag("7");
        number8_B.setTag("8");
        number9_B.setTag("9");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_B:
                delete();
                break;

            case R.id.equals_B:
                calculate();
                break;

            case R.id.divide_B:
            case R.id.multiply_B:
            case R.id.minus_B:
            case R.id.plus_B:
            case R.id.dot_B:
            case R.id.number0_B:
            case R.id.number1_B:
            case R.id.number2_B:
            case R.id.number3_B:
            case R.id.number4_B:
            case R.id.number5_B:
            case R.id.number6_B:
            case R.id.number7_B:
            case R.id.number8_B:
            case R.id.number9_B:
                appendExpression((String) v.getTag());
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.delete_B:
                clear();
                return true;
        }
        return false;
    }

    private void appendExpression(String value) {
        calculator.append(value);
        updateResult();
    }

    private void delete() {
        calculator.delete();
        updateResult();
    }

    private void clear() {
        calculator.clear();
        updateResult();
    }

    private void calculate() {
        calculator.calculate();
        updateResult();
    }

    public void updateResult() {
        result_TV.setText(calculator.getExpression());
    }

    static interface CalculatorListener {
        public void onCalculatorResult(long result);
    }
}
