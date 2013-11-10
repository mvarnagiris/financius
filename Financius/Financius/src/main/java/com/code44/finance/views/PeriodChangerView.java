package com.code44.finance.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.code44.finance.R;
import com.code44.finance.utils.PeriodHelper;

/**
 * Created by Mantas on 09/06/13.
 */
public class PeriodChangerView extends LinearLayout implements View.OnClickListener
{
    private final ImageButton previous_B;
    private final ImageButton next_B;
    private final TextView period_TV;
    private PeriodHelper periodHelper;
    private PeriodChangerListener listener;

    public PeriodChangerView(Context context)
    {
        this(context, null);
    }

    public PeriodChangerView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public PeriodChangerView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        inflate(context, R.layout.v_period_changer, this);

        // Init
        if (!isInEditMode())
            periodHelper = PeriodHelper.getDefault(context);

        // Get views
        previous_B = (ImageButton) findViewById(R.id.previous_B);
        next_B = (ImageButton) findViewById(R.id.next_B);
        period_TV = (TextView) findViewById(R.id.period_TV);

        // Setup
        previous_B.setOnClickListener(this);
        next_B.setOnClickListener(this);
        updateViews();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.previous_B:
                periodHelper.previousActive();
                if (listener != null)
                    listener.onPeriodChanged();
                updateViews();
                break;

            case R.id.next_B:
                periodHelper.nextActive();
                if (listener != null)
                    listener.onPeriodChanged();
                updateViews();
                break;
        }
    }

    public PeriodChangerListener getListener()
    {
        return listener;
    }

    public void setListener(PeriodChangerListener listener)
    {
        this.listener = listener;
    }

    public void updateViews()
    {
        if (!isInEditMode())
            period_TV.setText(periodHelper.getActivePeriodShortTitle());
        else
            period_TV.setText("August");
    }

    public static interface PeriodChangerListener
    {
        public void onPeriodChanged();
    }
}
