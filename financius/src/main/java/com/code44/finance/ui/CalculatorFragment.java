package com.code44.finance.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.utils.Calculator;

import javax.inject.Inject;

public class CalculatorFragment extends BaseFragment implements View.OnClickListener, View.OnLongClickListener {
    private static final String ARG_VALUE = "ARG_VALUE";

    @Inject Calculator calculator;

    private ViewGroup resultContainer_V;
    private TextView result_TV;
    private View resultClearer_V;

    private CalculatorListener listener;

    public static CalculatorFragment newInstance(long value) {
        final Bundle args = new Bundle();
        args.putLong(ARG_VALUE, value);

        final CalculatorFragment fragment = new CalculatorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof CalculatorListener) {
            listener = (CalculatorListener) activity;
        } else {
            throw new IllegalArgumentException("Activity " + activity.getClass().getSimpleName() + " must implement " + CalculatorListener.class.getSimpleName());
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calculator, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        resultContainer_V = (ViewGroup) view.findViewById(R.id.resultContainer_V);
        result_TV = (TextView) view.findViewById(R.id.result_TV);
        resultClearer_V = view.findViewById(R.id.resultClearer_V);
        final Button equals_B = (Button) view.findViewById(R.id.equals_B);
        final Button delete_B = (Button) view.findViewById(R.id.delete_B);
        final Button divide_B = (Button) view.findViewById(R.id.divide_B);
        final Button multiply_B = (Button) view.findViewById(R.id.multiply_B);
        final Button minus_B = (Button) view.findViewById(R.id.minus_B);
        final Button plus_B = (Button) view.findViewById(R.id.plus_B);
        final Button dot_B = (Button) view.findViewById(R.id.dot_B);
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

        if (savedInstanceState == null) {
            final long value = getArguments().getLong(ARG_VALUE, 0);
            if (value != 0) {
                calculator.setValue(value / 100.0);
            }
        }

        updateResult();

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

                onLayoutFinished();
            }
        });
    }

    @Override public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_B:
                calculator.delete();
                updateResult();
                break;

            case R.id.equals_B:
                if (calculator.hasExpression()) {
                    calculator.calculate();
                    updateResult();
                } else {
                    listener.onCalculatorResult(calculator.getResult());
                }
                break;

            case R.id.divide_B:
                calculator.divide();
                updateResult();
                break;

            case R.id.multiply_B:
                calculator.multiply();
                updateResult();
                break;

            case R.id.minus_B:
                calculator.minus();
                updateResult();
                break;

            case R.id.plus_B:
                calculator.plus();
                updateResult();
                break;

            case R.id.dot_B:
                calculator.decimal();
                updateResult();
                break;

            case R.id.number0_B:
                calculator.number(0);
                updateResult();
                break;

            case R.id.number1_B:
                calculator.number(1);
                updateResult();
                break;

            case R.id.number2_B:
                calculator.number(2);
                updateResult();
                break;

            case R.id.number3_B:
                calculator.number(3);
                updateResult();
                break;

            case R.id.number4_B:
                calculator.number(4);
                updateResult();
                break;

            case R.id.number5_B:
                calculator.number(5);
                updateResult();
                break;

            case R.id.number6_B:
                calculator.number(6);
                updateResult();
                break;

            case R.id.number7_B:
                calculator.number(7);
                updateResult();
                break;

            case R.id.number8_B:
                calculator.number(8);
                updateResult();
                break;

            case R.id.number9_B:
                calculator.number(9);
                updateResult();
                break;
        }
    }

    @Override public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.delete_B:
                clear();
                return true;
        }
        return false;
    }

    public void updateResult() {
        result_TV.setText(calculator.getFormattedExpression());
    }

    private void onLayoutFinished() {
        final ViewGroup.LayoutParams params = resultClearer_V.getLayoutParams();
        params.width = resultContainer_V.getWidth();
        params.height = resultContainer_V.getHeight();
    }

    private void clear() {
        int cx = resultClearer_V.getRight();
        int cy = resultClearer_V.getBottom();
        float radius = Math.max(resultClearer_V.getWidth(), resultClearer_V.getHeight()) * 2.0f;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (resultClearer_V.getVisibility() != View.VISIBLE) {
                Animator reveal = ViewAnimationUtils.createCircularReveal(resultClearer_V, cx, cy, 0, radius);
                reveal.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        resultClearer_V.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        calculator.clear();
                        updateResult();
                        resultClearer_V.animate().alpha(0.0f).setDuration(200).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                resultClearer_V.setVisibility(View.INVISIBLE);
                                resultClearer_V.setAlpha(1.0f);
                            }
                        }).start();
                    }
                });
                reveal.setDuration(600);
                reveal.start();
            }
        } else {
            calculator.clear();
            updateResult();
        }
    }

    static interface CalculatorListener {
        public void onCalculatorResult(long result);
    }
}
