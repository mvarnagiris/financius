package com.code44.finance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.code44.finance.BuildConfig;
import com.code44.finance.R;
import com.code44.finance.ui.settings.security.Security;
import com.code44.finance.utils.BaseInterval;

import java.util.ArrayList;
import java.util.List;

public class SettingsAdapter extends BaseAdapter {
    public static final int ID_CURRENCIES = 1;
    public static final int ID_CATEGORIES = 2;
    public static final int ID_TAGS = 3;
    public static final int ID_PERIOD = 4;
    public static final int ID_SECURITY = 5;
    public static final int ID_DATA = 6;
    public static final int ID_ABOUT = 7;

    private final Context context;
    private final List<SettingsItem> settingsItems;

    public SettingsAdapter(Context context) {
        this.context = context;
        settingsItems = new ArrayList<>();
        settingsItems.add(new SettingsItem(ID_CURRENCIES, ViewType.SettingsItem, context.getString(R.string.currencies_other)));
        settingsItems.add(new SettingsItem(ID_CATEGORIES, ViewType.SettingsItem, context.getString(R.string.categories_other)));
        settingsItems.add(new SettingsItem(ID_TAGS, ViewType.SettingsItem, context.getString(R.string.tags_other)));
        settingsItems.add(new SettingsSubtitleItem(ID_PERIOD, ViewType.SettingsSubtitleItem, context.getString(R.string.period), ""));
        settingsItems.add(new SettingsSubtitleItem(ID_SECURITY, ViewType.SettingsSubtitleItem, context.getString(R.string.security), ""));
        settingsItems.add(new SettingsItem(ID_DATA, ViewType.SettingsItem, context.getString(R.string.your_data)));
        settingsItems.add(new SettingsSubtitleItem(ID_ABOUT, ViewType.SettingsSubtitleItem, context.getString(R.string.about), BuildConfig.VERSION_NAME));
    }

    @Override public int getCount() {
        return settingsItems.size();
    }

    @Override public int getViewTypeCount() {
        return ViewType.values().length;
    }

    @Override public Object getItem(int position) {
        return settingsItems.get(position);
    }

    @Override public long getItemId(int position) {
        return settingsItems.get(position).getId();
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        final SettingsItem settingsItem = settingsItems.get(position);
        final ViewType viewType = settingsItem.getViewType();
        final SettingsItemViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(viewType.getLayoutId(), parent, false);
            viewHolder = viewType.createViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SettingsItemViewHolder) convertView.getTag();
        }
        viewHolder.bind(settingsItem);

        return convertView;
    }

    public void setInterval(BaseInterval interval) {
        ((SettingsSubtitleItem) settingsItems.get(ID_PERIOD - 1)).setSubtitle(interval.getTypeTitle());
        notifyDataSetChanged();
    }

    public void setSecurity(Security security) {

    }

    private static enum ViewType {
        SettingsItem(R.layout.li_settings) {
            @Override
            public SettingsItemViewHolder createViewHolder(View itemView) {
                return new SettingsItemViewHolder(itemView);
            }
        },
        SettingsSubtitleItem(R.layout.li_settings_subtitle) {
            @Override
            public SettingsItemViewHolder createViewHolder(View itemView) {
                return new SettingsSubtitleItemViewHolder(itemView);
            }
        };

        private final int layoutId;

        private ViewType(int layoutId) {
            this.layoutId = layoutId;
        }

        public int getLayoutId() {
            return layoutId;
        }

        public abstract SettingsItemViewHolder createViewHolder(View itemView);
    }

    private static class SettingsItem {
        private final long id;
        private final ViewType viewType;
        private final String title;

        protected SettingsItem(long id, ViewType viewType, String title) {
            this.id = id;
            this.viewType = viewType;
            this.title = title;
        }

        public long getId() {
            return id;
        }

        public ViewType getViewType() {
            return viewType;
        }

        public String getTitle() {
            return title;
        }
    }

    private static class SettingsSubtitleItem extends SettingsItem {
        private String subtitle;

        protected SettingsSubtitleItem(long id, ViewType viewType, String title, String subtitle) {
            super(id, viewType, title);
            this.subtitle = subtitle;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }
    }

    private static class SettingsItemViewHolder {
        private final TextView title_TV;

        private SettingsItemViewHolder(View itemView) {
            title_TV = (TextView) itemView.findViewById(R.id.titleTextView);
        }

        protected void bind(SettingsItem settingsItem) {
            title_TV.setText(settingsItem.getTitle());
        }
    }

    private static class SettingsSubtitleItemViewHolder extends SettingsItemViewHolder {
        private final TextView subtitle_TV;

        private SettingsSubtitleItemViewHolder(View itemView) {
            super(itemView);
            subtitle_TV = (TextView) itemView.findViewById(R.id.subtitleTextView);
        }

        @Override protected void bind(SettingsItem settingsItem) {
            super.bind(settingsItem);
            subtitle_TV.setText(((SettingsSubtitleItem) settingsItem).getSubtitle());
        }
    }
}
