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
import com.hp.advantage.data.Account;
import com.hp.advantage.utils.GenericUtils;
import com.hp.advantage.utils.LogManager;

import java.util.List;

public class AccountListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Activity activity;
    private List<Account> AccountList;
    private GenericUtils GU;

    public AccountListAdapter(Activity bidsActivity, List<Account> accountList) {
        activity = bidsActivity;
        AccountList = accountList;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        GU = GenericUtils.Instance(activity);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            ViewHolder holder;

            if (convertView == null || convertView.getTag() == null) {
                convertView = inflater
                        .inflate(R.layout.account_list_item, null);
                holder = new ViewHolder();
                holder.layoutRelativeLayout = (RelativeLayout) convertView
                        .findViewById(R.id.RelativeLayout1);
                holder.accountImageView = (ImageView) convertView
                        .findViewById(R.id.accountImageView);
                holder.accountNameTextView = (TextView) convertView
                        .findViewById(R.id.accountNameTextView);
                holder.accountBalanceTextView = (TextView) convertView
                        .findViewById(R.id.accountBalanceTextView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Account curentItem = (Account) (AccountList.get(position));
            GU.GetImageLoader().DisplayImageOriginalSize(curentItem.account_image, holder.accountImageView);

            holder.accountNameTextView.setText(curentItem.account_name);
            holder.accountBalanceTextView.setText(String.format("%.2f$", curentItem.balance));

            if (curentItem.balance < 0) {
                holder.accountBalanceTextView.setTextColor(activity.getResources().getColor(R.color.red));
            } else {
                holder.accountBalanceTextView.setTextColor(activity.getResources().getColor(R.color.green));
            }
            return convertView;
        } catch (Exception exp) {

            LogManager.Error("AccountListAdapter.getView ", exp);

            return null;
        }
    }

    public int getCount() {
        return AccountList.size();
    }

    public Object getItem(int position) {
        return AccountList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        protected RelativeLayout layoutRelativeLayout;
        protected ImageView accountImageView;
        protected TextView accountNameTextView;
        protected TextView accountBalanceTextView;
    }
}
