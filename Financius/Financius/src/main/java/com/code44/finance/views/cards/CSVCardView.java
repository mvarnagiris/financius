package com.code44.finance.views.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.code44.finance.R;
import org.joda.time.format.DateTimeFormat;

@SuppressWarnings("UnusedDeclaration")
public class CSVCardView extends BigTextCardView implements View.OnClickListener
{
    private static final int REQUEST_FROM = 49641;
    private static final int REQUEST_TO = 56145;
    // -----------------------------------------------------------------------------------------------------------------
    private final View exportCSV_V;
    private final Button dateFrom_B;
    private final Button dateTo_B;
    private final ImageButton clearDateFrom_B;
    private final ImageButton clearDateTo_B;
    private final Button export_B;
    // -----------------------------------------------------------------------------------------------------------------
    private Callback callback;
    private long dateFrom;
    private long dateTo;

    public CSVCardView(Context context)
    {
        this(context, null);
    }

    public CSVCardView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public CSVCardView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        // Get views
        exportCSV_V = LayoutInflater.from(context).inflate(R.layout.v_export_csv, this, false);
        //noinspection ConstantConditions
        dateFrom_B = (Button) exportCSV_V.findViewById(R.id.dateFrom_B);
        dateTo_B = (Button) exportCSV_V.findViewById(R.id.dateTo_B);
        clearDateFrom_B = (ImageButton) exportCSV_V.findViewById(R.id.clearDateFrom_B);
        clearDateTo_B = (ImageButton) exportCSV_V.findViewById(R.id.clearDateTo_B);
        export_B = (Button) exportCSV_V.findViewById(R.id.export_B);

        // Add views
        addView(exportCSV_V);

        // Setup
        setCardInfo(new CSVCardInfo(context));
        dateFrom_B.setOnClickListener(this);
        dateTo_B.setOnClickListener(this);
        clearDateFrom_B.setOnClickListener(this);
        clearDateTo_B.setOnClickListener(this);
        export_B.setOnClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int extraHeight = 0;

        // Balance container
        if (exportCSV_V.getVisibility() != GONE)
        {
            LayoutParams params = (LayoutParams) exportCSV_V.getLayoutParams();
            //noinspection ConstantConditions
            final int widthMS = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight() - params.leftMargin - params.rightMargin, MeasureSpec.EXACTLY);
            final int heightMS = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            exportCSV_V.measure(widthMS, heightMS);
            extraHeight += exportCSV_V.getMeasuredHeight() + params.topMargin + params.bottomMargin;
        }

        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + extraHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);

        int childTop = getContentTop();

        // Container
        LayoutParams params = (LayoutParams) exportCSV_V.getLayoutParams();
        //noinspection ConstantConditions
        childTop += params.topMargin;
        int childLeft = getPaddingLeft() + params.leftMargin;
        exportCSV_V.layout(childLeft, childTop, childLeft + exportCSV_V.getMeasuredWidth(), childTop + exportCSV_V.getMeasuredHeight());
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.dateFrom_B:
                if (callback != null)
                    callback.onRequestCSVPeriod(REQUEST_FROM, dateFrom > 0 ? dateFrom : System.currentTimeMillis());
                break;

            case R.id.dateTo_B:
                if (callback != null)
                    callback.onRequestCSVPeriod(REQUEST_TO, dateTo > 0 ? dateTo : System.currentTimeMillis());
                break;

            case R.id.clearDateFrom_B:
                setDateFrom(0);
                break;

            case R.id.clearDateTo_B:
                setDateTo(0);
                break;

            case R.id.export_B:
                if (callback != null)
                    callback.onExportCSV(dateFrom, dateTo == 0 ? Long.MAX_VALUE : dateTo);
                break;
        }
    }

    @Override
    public void setCardInfo(CardInfo cardInfo)
    {
        super.setCardInfo(cardInfo);
        final CSVCardInfo info = (CSVCardInfo) cardInfo;
        setDateFrom(info.getDateFrom());
        setDateTo(info.getDateTo());
    }

    public long getDateFrom()
    {
        return dateFrom;
    }

    public void setDateFrom(long dateFrom)
    {
        this.dateFrom = dateFrom;
        if (dateFrom == 0)
        {
            dateFrom_B.setText(R.string.not_set);
            clearDateFrom_B.setVisibility(INVISIBLE);
        }
        else
        {
            dateFrom_B.setText(DateTimeFormat.mediumDate().print(dateFrom));
            clearDateFrom_B.setVisibility(VISIBLE);
        }
    }

    public long getDateTo()
    {
        return dateTo;
    }

    public void setDateTo(long dateTo)
    {
        this.dateTo = dateTo;
        if (dateTo == 0)
        {
            dateTo_B.setText(R.string.not_set);
            clearDateTo_B.setVisibility(INVISIBLE);
        }
        else
        {
            dateTo_B.setText(DateTimeFormat.mediumDate().print(dateTo));
            clearDateTo_B.setVisibility(VISIBLE);
        }
    }

    public void setDate(int requestCode, long date)
    {
        if (requestCode == REQUEST_FROM)
            setDateFrom(date);
        else if (requestCode == REQUEST_TO)
            setDateTo(date);
    }

    public Callback getCallback()
    {
        return callback;
    }

    public void setCallback(Callback callback)
    {
        this.callback = callback;
    }

    public static interface Callback
    {
        public void onRequestCSVPeriod(int requestCode, long date);

        public void onExportCSV(long dateFrom, long dateTo);
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class CSVCardInfo extends BigTextCardInfo
    {
        private long dateFrom;
        private long dateTo;

        public CSVCardInfo(Context context)
        {
            super(0);
            setTitle(context.getString(R.string.export_csv));
            dateFrom = 0;
            dateTo = 0;
        }

        public long getDateFrom()
        {
            return dateFrom;
        }

        public CSVCardInfo setDateFrom(long dateFrom)
        {
            this.dateFrom = dateFrom;
            return this;
        }

        public long getDateTo()
        {
            return dateTo;
        }

        public CSVCardInfo setDateTo(long dateTo)
        {
            this.dateTo = dateTo;
            return this;
        }
    }
}