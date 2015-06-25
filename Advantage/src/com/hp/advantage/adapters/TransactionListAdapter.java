package com.hp.advantage.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hp.advantage.R;
import com.hp.advantage.data.Transaction;
import com.hp.advantage.utils.GenericUtils;
import com.hp.advantage.utils.LogManager;

import java.util.List;

public class TransactionListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Activity activity;
    private List<Transaction> TransactionList;
    private GenericUtils GU;

    public TransactionListAdapter(Activity bidsActivity, List<Transaction> transactionList) {
        activity = bidsActivity;
        TransactionList = transactionList;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        GU = GenericUtils.Instance(activity);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            ViewHolder holder;

            if (convertView == null || convertView.getTag() == null) {
                convertView = inflater
                        .inflate(R.layout.transaction_list_item, null);
                holder = new ViewHolder();
                holder.transactionImageView = (ImageView) convertView
                        .findViewById(R.id.transactionImageView);
                holder.transactionNameTextView = (TextView) convertView
                        .findViewById(R.id.transactionNameTextView);
                holder.transactionBalanceTextView = (TextView) convertView
                        .findViewById(R.id.transactionBalanceTextView);
                holder.transactionDateTextView = (TextView) convertView
                        .findViewById(R.id.transactionDateTextView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Transaction curentItem = TransactionList.get(position);
            GU.GetImageLoader().DisplayImageOriginalSize(curentItem.transaction_image, holder.transactionImageView);
            holder.transactionNameTextView.setText(curentItem.description);
            holder.transactionBalanceTextView.setText(String.format("%.2f$", curentItem.amount));

            if (curentItem.amount < 0) {
                holder.transactionBalanceTextView.setTextColor(activity.getResources().getColor(R.color.red));
            } else {
                holder.transactionBalanceTextView.setTextColor(activity.getResources().getColor(R.color.green));
            }
            holder.transactionDateTextView.setText(curentItem.transaction_date);
            return convertView;
        } catch (Exception exp) {

            LogManager.Error("transactionListAdapter.getView ", exp);

            return null;
        }
    }

    public int getCount() {
        return TransactionList.size();
    }

    public Object getItem(int position) {
        return TransactionList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {

        protected ImageView transactionImageView;
        protected TextView transactionNameTextView;
        protected TextView transactionBalanceTextView;
        protected TextView transactionDateTextView;
    }
}
