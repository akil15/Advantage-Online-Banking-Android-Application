package com.hp.advantage.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class Bill implements Parcelable {
    public static final Creator<Bill> CREATOR =
            new Creator<Bill>() {
                public Bill createFromParcel(Parcel in) {
                    return new Bill(in);
                }

                public Bill[] newArray(int size) {
                    return new Bill[size];
                }
            };
    public String bill_id;
    public String bill_name;
    public double bill_amount;
    public String bill_date;
    public String bill_description;
    public String bill_image;
    public String bill_payee_name;
    public String bill_payee_image;
    public String bill_url;

    public Bill(JSONObject bill) {
        bill_id = bill.optString("bill_id", "");
        bill_name = bill.optString("bill_name", "");
        bill_amount = bill.optDouble("bill_amount", 0);
        bill_date = bill.optString("bill_date", "");
        bill_description = bill.optString("bill_description", "");
        bill_image = bill.optString("bill_image", "");
        bill_payee_name = bill.optString("bill_payee_name", "");
        bill_payee_image = bill.optString("bill_payee_image", "");
        bill_url = bill.optString("bill_url", "");
    }

    public Bill() {
        // TODO Auto-generated constructor stub
    }

    private Bill(Parcel in) {
        bill_id = in.readString();
        bill_name = in.readString();
        bill_amount = in.readDouble();
        bill_date = in.readString();
        bill_description = in.readString();
        bill_image = in.readString();
        bill_payee_name = in.readString();
        bill_payee_image = in.readString();
        bill_url = in.readString();
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(bill_id);
        out.writeString(bill_name);
        out.writeDouble(bill_amount);
        out.writeString(bill_date);
        out.writeString(bill_description);
        out.writeString(bill_image);
        out.writeString(bill_payee_name);
        out.writeString(bill_payee_image);
        out.writeString(bill_url);
    }
}