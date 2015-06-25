package com.hp.advantage.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class Transaction implements Parcelable {
    public static final Parcelable.Creator<Transaction> CREATOR =
            new Parcelable.Creator<Transaction>() {
                public Transaction createFromParcel(Parcel in) {
                    return new Transaction(in);
                }

                public Transaction[] newArray(int size) {
                    return new Transaction[size];
                }
            };
    public int transaction_id;
    public int account_id;
    public String transaction_date;
    public double amount;
    public String description;
    public String transaction_image;

    public Transaction(JSONObject transaction) {
        transaction_id = transaction.optInt("transaction_id", 0);
        account_id = transaction.optInt("account_id", 0);
        transaction_date = transaction.optString("transaction_date", "");
        amount = transaction.optDouble("amount", 0);
        description = transaction.optString("description", "");
        transaction_image = transaction.optString("transaction_image", "");
    }

    public Transaction() {
        // TODO Auto-generated constructor stub
    }

    private Transaction(Parcel in) {
        transaction_id = in.readInt();
        account_id = in.readInt();
        transaction_date = in.readString();
        amount = in.readDouble();
        description = in.readString();
        transaction_image = in.readString();
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(transaction_id);
        out.writeInt(account_id);
        out.writeString(transaction_date);
        out.writeDouble(amount);
        out.writeString(description);
        out.writeString(transaction_image);
    }
}