package com.code44.finance.ui.common.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.money.Calculator;

import javax.inject.Inject;

public class CalculatorActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener {
    private static final String EXTRA_VALUE = "EXTRA_VALUE";
    private static final String EXTRA_RAW_VALUE = "EXTRA_RAW_VALUE";

    private static final String RESULT_EXTRA_VALUE = "RESULT_EXTRA_VALUE";
    private static final String RESULT_EXTRA_RAW_VALUE = "RESULT_EXTRA_RAW_VALUE";

    private static final String STATE_EXPRESSION = "STATE_EXPRESSION";

    @Inject Calculator calculator;

    private ViewGroup resultContainerView;
    private TextView resultTextView;
    private View resultClearerView;

    public static void start(Activity activity, int requestCode, long value) {
        ActivityStarter.begin(activity, CalculatorActivity.class).extra(EXTRA_VALUE, value).startForResult(requestCode);
    }

    public static void start(Activity activity, int requestCode, double value) {
        ActivityStarter.begin(activity, CalculatorActivity.class).extra(EXTRA_VALUE, value).startForResult(requestCode);
    }

    public static long getResultValue(Intent data) {
        return data.getLongExtra(RESULT_EXTRA_VALUE, 0);
    }

    public static double getResultRawValue(Intent data, double defaultValue) {
        return data.getDoubleExtra(RESULT_EXTRA_RAW_VALUE, defaultValue);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        // Get extras
        final long value = getIntent().getLongExtra(EXTRA_VALUE, 0);
        final double rawValue = getIntent().getDoubleExtra(EXTRA_RAW_VALUE, 0);

        // Get views
        resultContainerView = (ViewGroup) findViewById(R.id.resultContainerView);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        resultClearerView = findViewById(R.id.resultClearerView);
        final Button equalsButton = (Button) findViewById(R.id.equalsButton);
        final Button deleteButton = (Button) findViewById(R.id.deleteButton);
        final Button divideButton = (Button) findViewById(R.id.divideButton);
        final Button multiplyButton = (Button) findViewById(R.id.multiplyButton);
        final Button minusButton = (Button) findViewById(R.id.minusButton);
        final Button plusButton = (Button) findViewById(R.id.plusButton);
        final Button dotButton = (Button) findViewById(R.id.dotButton);
        final Button number0Button = (Button) findViewById(R.id.number0Button);
        final Button number1Button = (Button) findViewById(R.id.number1Button);
        final Button number2Button = (Button) findViewById(R.id.number2Button);
        final Button number3Button = (Button) findViewById(R.id.number3Button);
        final Button number4Button = (Button) findViewById(R.id.number4Button);
        final Button number5Button = (Button) findViewById(R.id.number5Button);
        final Button number6Button = (Button) findViewById(R.id.number6Button);
        final Button number7Button = (Button) findViewById(R.id.number7Button);
        final Button number8Button = (Button) findViewById(R.id.number8Button);
        final Button number9Button = (Button) findViewById(R.id.number9Button);

        // Setup
        deleteButton.setOnLongClickListener(this);
        deleteButton.setOnClickListener(this);
        divideButton.setOnClickListener(this);
        multiplyButton.setOnClickListener(this);
        minusButton.setOnClickListener(this);
        plusButton.setOnClickListener(this);
        dotButton.setOnClickListener(this);
        equalsButton.setOnClickListener(this);
        number0Button.setOnClickListener(this);
        number1Button.setOnClickListener(this);
        number2Button.setOnClickListener(this);
        number3Button.setOnClickListener(this);
        number4Button.setOnClickListener(this);
        number5Button.setOnClickListener(this);
        number6Button.setOnClickListener(this);
        number7Button.setOnClickListener(this);
        number8Button.setOnClickListener(this);
        number9Button.setOnClickListener(this);

        if (savedInstanceState == null) {
            final double realValue;
            if (value != 0) {
                realValue = value / 100.0;
            } else if (Double.compare(rawValue, 0) != 0) {
                realValue = rawValue;
            } else {
                realValue = 0;
            }

            if (Double.compare(realValue, 0) != 0) {
                calculator.setValue(realValue);
            }
        } else {
            if (savedInstanceState.containsKey(STATE_EXPRESSION)) {
                String exp = savedInstanceState.getString(STATE_EXPRESSION, null);
                if (exp != null) {
                    calculator.setExpression(exp);
                }
            }
        }

        updateResult();

        resultContainerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
                resultContainerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                onLayoutFinished();
            }
        });
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_EXPRESSION, calculator.getExpression());
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deleteButton:
                calculator.delete();
                updateResult();
                break;

            case R.id.equalsButton:
                if (calculator.hasExpression()) {
                    calculator.calculate();
                    updateResult();
                } else {
                    onCalculatorResult(calculator.getResult(), calculator.getResultRaw());
                }
                break;

            case R.id.divideButton:
                calculator.divide();
                updateResult();
                break;

            case R.id.multiplyButton:
                calculator.multiply();
                updateResult();
                break;

            case R.id.minusButton:
                calculator.minus();
                updateResult();
                break;

            case R.id.plusButton:
                calculator.plus();
                updateResult();
                break;

            case R.id.dotButton:
                calculator.decimal();
                updateResult();
                break;

            case R.id.number0Button:
                calculator.number(0);
                updateResult();
                break;

            case R.id.number1Button:
                calculator.number(1);
                updateResult();
                break;

            case R.id.number2Button:
                calculator.number(2);
                updateResult();
                break;

            case R.id.number3Button:
                calculator.number(3);
                updateResult();
                break;

            case R.id.number4Button:
                calculator.number(4);
                updateResult();
                break;

            case R.id.number5Button:
                calculator.number(5);
                updateResult();
                break;

            case R.id.number6Button:
                calculator.number(6);
                updateResult();
                break;

            case R.id.number7Button:
                calculator.number(7);
                updateResult();
                break;

            case R.id.number8Button:
                calculator.number(8);
                updateResult();
                break;

            case R.id.number9Button:
                calculator.number(9);
                updateResult();
                break;
        }
    }

    @Override public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.deleteButton:
                clear();
                return true;
        }
        return false;
    }

    private void onCalculatorResult(long result, double rawResult) {
        final Intent data = new Intent();
        data.putExtra(RESULT_EXTRA_VALUE, result);
        data.putExtra(RESULT_EXTRA_RAW_VALUE, rawResult);
        setResult(RESULT_OK, data);
        finish();
    }

    private void updateResult() {
        resultTextView.setText(calculator.getFormattedExpression());
    }

    private void onLayoutFinished() {
        final ViewGroup.LayoutParams params = resultClearerView.getLayoutParams();
        params.width = resultContainerView.getWidth();
        params.height = resultContainerView.getHeight();
    }

    private void clear() {
        int cx = resultClearerView.getRight();
        int cy = resultClearerView.getBottom();
        float radius = Math.max(resultClearerView.getWidth(), resultClearerView.getHeight()) * 2.0f;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (resultClearerView.getVisibility() != View.VISIBLE) {
                Animator reveal = ViewAnimationUtils.createCircularReveal(resultClearerView, cx, cy, 0, radius);
                reveal.addListener(new AnimatorListenerAdapter() {
                    @Override public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        resultClearerView.setVisibility(View.VISIBLE);
                    }

                    @Override public void onAnimationEnd(Animator animation) {
                        calculator.clear();
                        updateResult();
                        resultClearerView.animate().alpha(0.0f).setDuration(200).withEndAction(new Runnable() {
                            @Override public void run() {
                                resultClearerView.setVisibility(View.INVISIBLE);
                                resultClearerView.setAlpha(1.0f);
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
}
