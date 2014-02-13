package com.code44.finance.views.cards;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import com.code44.finance.R;
import com.code44.finance.db.Tables;
import com.code44.finance.views.AutoResizeTextView;

@SuppressWarnings("UnusedDeclaration")
public class CategoryCardView extends CardViewV2
{
    private final AutoResizeTextView category_TV;
    // -----------------------------------------------------------------------------------------------------------------
    private long categoryId;
    private String categoryTitle;
    private int categoryColor;

    public CategoryCardView(Context context)
    {
        this(context, null);
    }

    public CategoryCardView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public CategoryCardView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        // Setup
        //noinspection ConstantConditions
        int padding = getResources().getDimensionPixelSize(R.dimen.space_normal);
        container_V.setPadding(0, container_V.getPaddingTop(), padding, container_V.getPaddingBottom());
        category_TV = new AutoResizeTextView(context);
        category_TV.setGravity(Gravity.CENTER_VERTICAL);
//        category_TV.setMaxLines(1);
        category_TV.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        //noinspection ConstantConditions
        category_TV.setTextColor(getResources().getColor(R.color.text_primary));
        category_TV.setTextSize(getResources().getDimension(R.dimen.text_xxxlarge));
        category_TV.setMinTextSize(getResources().getDimension(R.dimen.text_xsmall));
        category_TV.setMinHeight(getResources().getDimensionPixelSize(R.dimen.recommended_touch_size) - container_V.getPaddingTop() - container_V.getPaddingBottom());
        setContentView(category_TV);


        if (isInEditMode())
            setCategory(1, "Groceries", 0xffb4c833);
        else
            setCategory(0, null, 0);
    }

    public void setCategory(long categoryId, String categoryTitle, int categoryColor)
    {
        this.categoryId = categoryId;
        this.categoryTitle = categoryTitle;
        this.categoryColor = categoryColor;

        if (categoryId == 0 || categoryId == Tables.Categories.IDs.EXPENSE_ID || categoryId == Tables.Categories.IDs.INCOME_ID || categoryId == Tables.Categories.IDs.TRANSFER_ID)
        {
            //noinspection ConstantConditions
            category_TV.setText(getResources().getString(R.string.category));
            category_TV.setTextColor(getResources().getColor(R.color.text_secondary));
        }
        else
        {
            category_TV.setText(categoryTitle);
            //noinspection ConstantConditions
            category_TV.setTextColor(getResources().getColor(R.color.text_primary));
        }

        final int size = getResources().getDimensionPixelSize(R.dimen.recommended_touch_size) - container_V.getPaddingTop() - container_V.getPaddingBottom();
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setSize(size, size);
        drawable.setColor(categoryColor == 0 ? getResources().getColor(R.color.text_secondary) : categoryColor);
        setIcon(drawable);
    }

    public long getCategoryId()
    {
        return categoryId;
    }

    public String getCategoryTitle()
    {
        return categoryTitle;
    }

    public int getCategoryColor()
    {
        return categoryColor;
    }
}
