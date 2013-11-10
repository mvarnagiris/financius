package com.code44.finance.views.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import com.code44.finance.R;
import com.code44.finance.db.model.CategoriesPeriodReport;
import com.code44.finance.utils.CardViewUtils;
import com.code44.finance.utils.PeriodHelper;
import com.code44.finance.views.reports.PieChartLegendView;
import com.code44.finance.views.reports.PieChartView;

@SuppressWarnings("ConstantConditions")
public class CategoriesReportCardView extends PeriodCardView
{
    public static final long UNIQUE_CARD_ID = CardViewUtils.ID_CATEGORIES_REPORT;
    // -----------------------------------------------------------------------------------------------------------------
    private static final float PIE_CHART_RATIO = 0.3f;
    // -----------------------------------------------------------------------------------------------------------------
    private final PieChartView pieChart_V;
    private final PieChartLegendView legend_V;

    public CategoriesReportCardView(Context context)
    {
        this(context, null);
    }

    public CategoriesReportCardView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public CategoriesReportCardView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        // Init
        pieChart_V = new PieChartView(context);
        legend_V = new PieChartLegendView(context);

        // Setup
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //noinspection ConstantConditions
        lp.rightMargin = getResources().getDimensionPixelSize(R.dimen.space_small);
        lp.topMargin = getResources().getDimensionPixelSize(R.dimen.space_normal);
        pieChart_V.setLayoutParams(lp);

        lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.leftMargin = getResources().getDimensionPixelSize(R.dimen.space_small);
        lp.topMargin = getResources().getDimensionPixelSize(R.dimen.space_normal);
        legend_V.setLayoutParams(lp);
        legend_V.setPieChartView(pieChart_V);

        // Add views
        addView(pieChart_V);
        addView(legend_V);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int maxWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int extraHeight = 0;

        // Pie chart
        LayoutParams lp = (LayoutParams) pieChart_V.getLayoutParams();
        final int pieChartWidth = (int) (maxWidth * PIE_CHART_RATIO - lp.rightMargin - lp.leftMargin);
        pieChart_V.measure(MeasureSpec.makeMeasureSpec(pieChartWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        extraHeight += lp.topMargin + pieChart_V.getMeasuredHeight() + lp.bottomMargin;

        // Legend
        lp = (LayoutParams) legend_V.getLayoutParams();
        final int legendWidth = (int) (maxWidth - (maxWidth * PIE_CHART_RATIO) - lp.leftMargin - lp.rightMargin);
        legend_V.measure(MeasureSpec.makeMeasureSpec(legendWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(pieChart_V.getMeasuredHeight(), MeasureSpec.EXACTLY));

        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + extraHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);

        final int childTop = getContentTop();

        // Pie chart
        LayoutParams lp = (LayoutParams) pieChart_V.getLayoutParams();
        final int pieChartTop = childTop + lp.topMargin;
        final int pieChartLeft = getPaddingLeft() + lp.leftMargin;
        pieChart_V.layout(pieChartLeft, pieChartTop, pieChartLeft + pieChart_V.getMeasuredWidth(), pieChartTop + pieChart_V.getMeasuredHeight());

        // Legend
        lp = (LayoutParams) legend_V.getLayoutParams();
        final int legendTop = childTop + lp.topMargin;
        final int legendRight = getMeasuredWidth() - getPaddingRight() - lp.rightMargin;
        legend_V.layout(legendRight - legend_V.getMeasuredWidth(), legendTop, legendRight, legendTop + legend_V.getMeasuredHeight());
    }

    @Override
    public void setCardInfo(CardInfo cardInfo)
    {
        super.setCardInfo(cardInfo);

        setCategoriesReport(((CategoriesReportCardInfo) cardInfo).getReport());
    }

    public void setCategoriesReport(CategoriesPeriodReport report)
    {
        legend_V.bind(report.getExpenseList());
    }

    public static class CategoriesReportCardInfo extends PeriodCardInfo
    {
        private CategoriesPeriodReport report;

        public CategoriesReportCardInfo(Context context)
        {
            super(context, UNIQUE_CARD_ID);
            setTitle(context.getString(R.string.categories_report));
            final PeriodHelper periodHelper = PeriodHelper.getDefault(context);
            setPeriod(periodHelper.getType(), periodHelper.getCurrentStart(), periodHelper.getCurrentEnd());
        }

        public CategoriesPeriodReport getReport()
        {
            return report;
        }

        public CategoriesReportCardInfo setReport(CategoriesPeriodReport report)
        {
            this.report = report;
            return this;
        }
    }
}
