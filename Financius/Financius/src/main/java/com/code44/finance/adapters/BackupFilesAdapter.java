package com.code44.finance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.code44.finance.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BackupFilesAdapter extends BaseAdapter implements Comparator<File>
{
    private final Context context;
    private final View.OnClickListener overflowClickListener;
    private final List<File> list = new ArrayList<File>();


    public BackupFilesAdapter(Context context, View.OnClickListener overflowClickListener)
    {
        this.context = context;
        this.overflowClickListener = overflowClickListener;
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup root)
    {
        final ViewHolder holder;
        if (view == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.li_backup_file, root, false);
            holder = new ViewHolder();
            holder.date_TV = (TextView) view.findViewById(R.id.date_TV);
            holder.deviceName_TV = (TextView) view.findViewById(R.id.deviceName_TV);
            holder.overflow_B = (ImageButton) view.findViewById(R.id.overflow_B);
            holder.overflow_B.setOnClickListener(overflowClickListener);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }

//        final File file = list.get(position);
//        holder.date_TV.setText(DateUtils.formatDateTime(context, file.getModifiedDate().getValue(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME));
//        final double size = file.getFileSize();
//        final String sizeStr = size >= 1000000 ? String.format("%.1f MB", size / 1000000.0) : String.format("%.0f KB", size / 1000.0);
//        holder.deviceName_TV.setText(file.getDescription() + ", " + sizeStr);
//        holder.overflow_B.setTag(file.getId());

        return view;
    }

    public void setList(List<File> fileList)
    {
        this.list.clear();
        if (fileList != null)
            this.list.addAll(fileList);
        Collections.sort(this.list, this);
        notifyDataSetChanged();
    }

    public void addItem(File file)
    {
        list.add(file);
        Collections.sort(this.list, this);
        notifyDataSetChanged();
    }

    @Override
    public int compare(File file, File file2)
    {
//        if (file.getModifiedDate().getValue() < file2.getModifiedDate().getValue())
//            return 1;
//        if (file.getModifiedDate().getValue() > file2.getModifiedDate().getValue())
//            return -1;
        return 0;
    }

    private static class ViewHolder
    {
        private TextView deviceName_TV;
        private TextView date_TV;
        private ImageButton overflow_B;
    }
}
