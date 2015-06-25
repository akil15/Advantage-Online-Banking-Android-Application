package com.hp.advantage.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hp.advantage.R;
import com.hp.advantage.data.SideMenuItem;
import com.hp.advantage.utils.GenericUtils;
import com.hp.advantage.utils.LogManager;

import java.util.List;

public class DrawerMenuListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Activity activity;
    private List<SideMenuItem> MenuItemList;
    private GenericUtils GU;

    public DrawerMenuListAdapter(Activity actvt, List<SideMenuItem> menuItemList) {
        activity = actvt;
        MenuItemList = menuItemList;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        GU = GenericUtils.Instance(activity);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            ViewHolder holder;

            if (convertView == null || convertView.getTag() == null) {
                convertView = inflater
                        .inflate(R.layout.drawer_menu_item, null);
                holder = new ViewHolder();
                holder.layoutRelativeLayout = (RelativeLayout) convertView
                        .findViewById(R.id.RelativeLayout1);
                holder.menuItemImageView = (ImageView) convertView
                        .findViewById(R.id.menuItemImageView);
                holder.menuItemTextView = (TextView) convertView
                        .findViewById(R.id.menuItemTextView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            GU.GetImageLoader().DisplayImageOriginalSize((MenuItemList.get(position)).Image, holder.menuItemImageView);
            holder.menuItemTextView.setText((MenuItemList.get(position)).Title);
            return convertView;
        } catch (Exception exp) {

            LogManager.Error("DrawerMenuListAdapter.getView ", exp);

            return null;
        }
    }

    public int getCount() {
        return MenuItemList.size();
    }

    public Object getItem(int position) {
        return MenuItemList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        protected RelativeLayout layoutRelativeLayout;
        protected ImageView menuItemImageView;
        protected TextView menuItemTextView;
    }
}
