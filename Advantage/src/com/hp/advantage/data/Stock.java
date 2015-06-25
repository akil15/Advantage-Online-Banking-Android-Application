package com.hp.advantage.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class Stock implements Parcelable {
    public static final Parcelable.Creator<Stock> CREATOR =
            new Parcelable.Creator<Stock>() {
                public Stock createFromParcel(Parcel in) {
                    return new Stock(in);
                }

                public Stock[] newArray(int size) {
                    return new Stock[size];
                }
            };
    public String symbol;
    public String name;
    public double bid;
    public double open;
    public double change;
    public String stock_exchange;
    public String last_trade_date;
    public String percent_change;

    public Stock(JSONObject branch) {
        symbol = branch.optString("symbol", "");
        name = branch.optString("name", "");
        bid = branch.optDouble("bid", 0.0);
        open = branch.optDouble("open", 0.0);
        change = branch.optDouble("change", 0.0);
        stock_exchange = branch.optString("stock_exchange", "");
        last_trade_date = branch.optString("last_trade_date", "");
        percent_change = branch.optString("percent_change", "");
    }


    public Stock() {
        // TODO Auto-generated constructor stub
    }

    public Stock(Parcel in) {
        symbol = in.readString();
        name = in.readString();
        bid = in.readDouble();
        open = in.readDouble();
        change = in.readDouble();
        stock_exchange = in.readString();
        last_trade_date = in.readString();
        percent_change = in.readString();
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(symbol);
        out.writeString(name);
        out.writeDouble(bid);
        out.writeDouble(open);
        out.writeDouble(change);
        out.writeString(stock_exchange);
        out.writeString(last_trade_date);
        out.writeString(percent_change);
    }
}