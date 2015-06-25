package com.hp.advantage.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hp.advantage.R;
import com.hp.advantage.data.Stock;
import com.hp.advantage.utils.LogManager;

import java.util.List;

public class StockListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Activity activity;
    private List<Stock> StockList;

    public StockListAdapter(Activity _activity, List<Stock> stockList) {
        activity = _activity;
        StockList = stockList;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            ViewHolder holder;

            if (convertView == null || convertView.getTag() == null) {
                convertView = inflater
                        .inflate(R.layout.stock_list_item, null);
                holder = new ViewHolder();
                holder.stockNameTextView = (TextView) convertView
                        .findViewById(R.id.stockNameTextView);
                holder.stockBidTextView = (TextView) convertView
                        .findViewById(R.id.stockBidTextView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Stock curentItem = (Stock) (StockList.get(position));
            holder.stockNameTextView.setText(curentItem.name);
            holder.stockBidTextView.setText(String.format("%.2f", curentItem.bid));
            return convertView;
        } catch (Exception exp) {

            LogManager.Error("StockListAdapter.getView ", exp);

            return null;
        }
    }

    public int getCount() {
        return StockList.size();
    }

    public Object getItem(int position) {
        return StockList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        protected TextView stockNameTextView;
        protected TextView stockBidTextView;
    }
}
