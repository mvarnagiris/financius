package com.code44.finance.db.model;

import android.database.Cursor;
import com.code44.finance.db.Tables;
import com.code44.finance.views.reports.PieChartLegendView;

import java.util.*;

public class CategoriesPeriodReport
{
    private static final Comparator<CategoriesPeriodReportItem> comparator = new Comparator<CategoriesPeriodReportItem>()
    {
        @Override
        public int compare(CategoriesPeriodReportItem left, CategoriesPeriodReportItem right)
        {
            if (left.amount > right.amount)
                return -1;
            else if (left.amount < right.amount)
                return 1;
            else
                return 0;
        }
    };
    private final List<CategoriesPeriodReportItem> incomeList = new ArrayList<CategoriesPeriodReportItem>();
    private final List<CategoriesPeriodReportItem> expenseList = new ArrayList<CategoriesPeriodReportItem>();
    private final List<CategoriesPeriodReportItem> transferList = new ArrayList<CategoriesPeriodReportItem>();
    private float totalIncome = 0.0f;
    private float totalExpense = 0.0f;
    private float totalTransfer = 0.0f;
    private int totalIncomeItemsCount = 0;
    private int totalExpenseItemsCount = 0;
    private int totalTransferItemsCount = 0;

    private CategoriesPeriodReport()
    {

    }

    public static CategoriesPeriodReport from(Cursor c)
    {
        // Init
        final CategoriesPeriodReport report = new CategoriesPeriodReport();

        if (c != null && c.moveToFirst())
        {
            // Prepare maps
            final Map<Long, CategoriesPeriodReportItem> incomeMap = new HashMap<Long, CategoriesPeriodReportItem>();
            final Map<Long, CategoriesPeriodReportItem> expenseMap = new HashMap<Long, CategoriesPeriodReportItem>();
            final Map<Long, CategoriesPeriodReportItem> transferMap = new HashMap<Long, CategoriesPeriodReportItem>();
            final Map<Long, Map<Long, CategoriesPeriodReportItem>> subMap = new HashMap<Long, Map<Long, CategoriesPeriodReportItem>>();

            // Find indexes
            final int iCategoryId = 0;
            final int iCategoryParentId = 1;
            final int iType = 2;
            final int iCategoryColor = 3;
            final int iLevel = 4;
            final int iCategoryTitle = 5;
            final int iCategoryParentTitle = 6;
            final int iAmount = 7;

            // Prepare values
            CategoriesPeriodReportItem reportItem;
            Long categoryId;
            Long categoryParentId;
            String categoryTitle;
            String categoryParentTitle;
            int type;
            int level;
            int color;
            float amount;
            Map<Long, CategoriesPeriodReportItem> mapToUse;
            Map<Long, CategoriesPeriodReportItem> subMapToUse;
            List<CategoriesPeriodReportItem> list;
            do
            {
                // Get common values
                type = c.getInt(iType);
                level = c.getInt(iLevel);
                color = c.getInt(iCategoryColor);
                amount = c.getFloat(iAmount);

                // Check type
                switch (type)
                {
                    case Tables.Categories.Type.INCOME:
                        report.totalIncome += amount;
                        mapToUse = incomeMap;
                        break;

                    case Tables.Categories.Type.EXPENSE:
                        report.totalExpense += amount;
                        mapToUse = expenseMap;
                        break;

                    default:
                        report.totalTransfer += amount;
                        mapToUse = transferMap;
                        break;
                }

                // Check level
                if (level == 2)
                {
                    // Sub-category

                    // Get parent category values
                    categoryParentId = c.getLong(iCategoryParentId);
                    categoryParentTitle = c.getString(iCategoryParentTitle);
                    reportItem = mapToUse.get(categoryParentId);
                    if (reportItem == null)
                    {
                        // If we don't have that item in map, create a new one
                        reportItem = new CategoriesPeriodReportItem(categoryParentId, categoryParentTitle, color, 1);
                    }

                    // Update parent category values
                    reportItem.amount += amount;
                    mapToUse.put(categoryParentId, reportItem);

                    // Get sub-category values
                    categoryId = c.getLong(iCategoryId);
                    categoryTitle = c.getString(iCategoryTitle);
                    subMapToUse = subMap.get(categoryParentId);
                    if (subMapToUse == null)
                    {
                        // If sub categories map null, then create a new one
                        subMapToUse = new HashMap<Long, CategoriesPeriodReportItem>();
                    }
                    reportItem = subMapToUse.get(categoryId);
                    if (reportItem == null)
                    {
                        // If we don't have that item in map, create a new one
                        reportItem = new CategoriesPeriodReportItem(categoryId, categoryTitle, color, level);
                    }

                    // Update sub-category values
                    reportItem.amount += amount;
                    subMapToUse.put(categoryId, reportItem);
                    subMap.put(categoryParentId, subMapToUse);
                }
                else
                {
                    // Main category or system (level==0) category

                    // Get parent category values
                    categoryId = c.getLong(iCategoryId);
                    categoryTitle = c.getString(iCategoryTitle);
                    reportItem = mapToUse.get(categoryId);
                    if (reportItem == null)
                    {
                        // If we don't have that item in map, create a new one
                        reportItem = new CategoriesPeriodReportItem(categoryId, categoryTitle, color, level);
                    }

                    // Update parent category values
                    reportItem.amount += amount;
                    mapToUse.put(categoryId, reportItem);
                }
            }
            while (c.moveToNext());

            // Generate income categories list
            for (CategoriesPeriodReportItem item : incomeMap.values())
            {
                item.totalAmountInGroup = report.totalIncome;

                // Generate sub-categories list
                mapToUse = subMap.get(item.id);
                if (mapToUse != null)
                {
                    list = new ArrayList<CategoriesPeriodReportItem>();
                    for (CategoriesPeriodReportItem subItem : mapToUse.values())
                    {
                        subItem.totalAmountInGroup = item.amount;
                        list.add(subItem);
                        report.totalIncomeItemsCount++;
                    }
                    Collections.sort(list, comparator);
                    item.setSubCategoriesList(list);
                }

                report.incomeList.add(item);
                report.totalIncomeItemsCount++;
            }
            Collections.sort(report.incomeList, comparator);

            // Generate expense categories list
            for (CategoriesPeriodReportItem item : expenseMap.values())
            {
                item.totalAmountInGroup = report.totalExpense;

                // Generate sub-categories list
                mapToUse = subMap.get(item.id);
                if (mapToUse != null)
                {
                    list = new ArrayList<CategoriesPeriodReportItem>();
                    for (CategoriesPeriodReportItem subItem : mapToUse.values())
                    {
                        subItem.totalAmountInGroup = item.amount;
                        list.add(subItem);
                        report.totalExpenseItemsCount++;
                    }
                    Collections.sort(list, comparator);
                    item.setSubCategoriesList(list);
                }

                report.expenseList.add(item);
                report.totalExpenseItemsCount++;
            }
            Collections.sort(report.expenseList, comparator);

            // Generate transfer categories list
            for (CategoriesPeriodReportItem item : transferMap.values())
            {
                item.totalAmountInGroup = report.totalTransfer;

                // Generate sub-categories list
                mapToUse = subMap.get(item.id);
                if (mapToUse != null)
                {
                    list = new ArrayList<CategoriesPeriodReportItem>();
                    for (CategoriesPeriodReportItem subItem : mapToUse.values())
                    {
                        subItem.totalAmountInGroup = item.amount;
                        list.add(subItem);
                        report.totalTransferItemsCount++;
                    }
                    Collections.sort(list, comparator);
                    item.setSubCategoriesList(list);
                }

                report.transferList.add(item);
                report.totalTransferItemsCount++;
            }
            Collections.sort(report.transferList, comparator);
        }

        return report;
    }

    public int getTotalIncomeItemsCount()
    {
        return totalIncomeItemsCount;
    }

    public int getTotalExpenseItemsCount()
    {
        return totalExpenseItemsCount;
    }

    public int getTotalTransferItemsCount()
    {
        return totalTransferItemsCount;
    }

    public CategoriesPeriodReportItem getIncomeItem(int position)
    {
        return getItem(incomeList, totalIncomeItemsCount, position);
    }

    public CategoriesPeriodReportItem getExpenseItem(int position)
    {
        return getItem(expenseList, totalExpenseItemsCount, position);
    }

    public CategoriesPeriodReportItem getTransferItem(int position)
    {
        return getItem(transferList, totalTransferItemsCount, position);
    }

    public CategoriesPeriodReportItem getItem(List<CategoriesPeriodReportItem> list, int totalItemsCount, int position)
    {
        if (position >= totalItemsCount || position < 0)
            return null;

        int currentOffset = 0;
        int subItemsCount;
        for (CategoriesPeriodReportItem item : list)
        {
            // Check if requested item is current top level item
            if (position == currentOffset)
                return item;
            currentOffset++;

            // Check if requested item is in current sub categories list
            subItemsCount = item.getSubCategoriesList().size();
            if (position < currentOffset + subItemsCount)
                return item.getSubCategoriesList().get(position - currentOffset);
            currentOffset += subItemsCount;
        }

        return null;
    }

    public List<CategoriesPeriodReportItem> getIncomeList()
    {
        return incomeList;
    }

    public List<CategoriesPeriodReportItem> getExpenseList()
    {
        return expenseList;
    }

    public List<CategoriesPeriodReportItem> getTransferList()
    {
        return transferList;
    }

    public float getTotalIncome()
    {
        return totalIncome;
    }

    public float getTotalExpense()
    {
        return totalExpense;
    }

    public float getTotalTransfer()
    {
        return totalTransfer;
    }

    public static class CategoriesPeriodReportItem implements PieChartLegendView.PieChartLegendItem
    {
        private final long id;
        private final String title;
        private final int color;
        private final int level;
        private final List<CategoriesPeriodReportItem> subCategoriesList = new ArrayList<CategoriesPeriodReportItem>();
        private float amount;
        private float totalAmountInGroup;

        public CategoriesPeriodReportItem(long id, String title, int color, int level)
        {
            this.id = id;
            this.title = title;
            this.color = color;
            this.level = level;
            this.amount = 0.0f;
            this.totalAmountInGroup = 0.0f;
        }

        public int getLevel()
        {
            return level;
        }

        public List<CategoriesPeriodReportItem> getSubCategoriesList()
        {
            return subCategoriesList;
        }

        public void setSubCategoriesList(List<CategoriesPeriodReportItem> subcategories)
        {
            this.subCategoriesList.clear();
            if (subcategories != null)
                this.subCategoriesList.addAll(subcategories);
        }

        public long getId()
        {
            return id;
        }

        public String getTitle()
        {
            return title;
        }

        public float getAmount()
        {
            return amount;
        }

        public int getColor()
        {
            return color;
        }

        @Override
        public int getPieChartColor()
        {
            return getColor();
        }

        @Override
        public float getPieChartFraction()
        {
            return totalAmountInGroup > 0 ? amount / totalAmountInGroup : 0;
        }

        @Override
        public String getPieChartLegendTitle()
        {
            return getTitle();
        }

        @Override
        public float getPieChartLegendValue()
        {
            return getAmount();
        }
    }
}