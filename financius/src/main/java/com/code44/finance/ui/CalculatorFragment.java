package com.code44.finance.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.code44.finance.R;

public class CalculatorFragment extends BaseFragment {
    private TextView result_TV;
    private Button divide_B;
    private Button multiply_B;
    private Button minus_B;
    private Button plus_B;
    private Button number0_B;
    private Button number1_B;
    private Button number2_B;
    private Button number3_B;
    private Button number4_B;
    private Button number5_B;
    private Button number6_B;
    private Button number7_B;
    private Button number8_B;
    private Button number9_B;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calculator, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        result_TV = (TextView) view.findViewById(R.id.result_TV);
        divide_B = (Button) view.findViewById(R.id.divide_B);
        multiply_B = (Button) view.findViewById(R.id.multiply_B);
        minus_B = (Button) view.findViewById(R.id.minus_B);
        plus_B = (Button) view.findViewById(R.id.plus_B);
        number0_B = (Button) view.findViewById(R.id.number0_B);
        number1_B = (Button) view.findViewById(R.id.number1_B);
        number2_B = (Button) view.findViewById(R.id.number2_B);
        number3_B = (Button) view.findViewById(R.id.number3_B);
        number4_B = (Button) view.findViewById(R.id.number4_B);
        number5_B = (Button) view.findViewById(R.id.number5_B);
        number6_B = (Button) view.findViewById(R.id.number6_B);
        number7_B = (Button) view.findViewById(R.id.number7_B);
        number8_B = (Button) view.findViewById(R.id.number8_B);
        number9_B = (Button) view.findViewById(R.id.number9_B);
    }
}
