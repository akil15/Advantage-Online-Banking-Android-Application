package com.hp.advantage.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class Account implements Parcelable {
    public static final Parcelable.Creator<Account> CREATOR =
            new Parcelable.Creator<Account>() {
                public Account createFromParcel(Parcel in) {
                    return new Account(in);
                }

                public Account[] newArray(int size) {
                    return new Account[size];
                }
            };
    public int account_id;
    public int user_id;
    public int bank_id;
    public int branch_id;
    public String account_name;
    public double balance;
    public String account_image;

    public Account(JSONObject account) {
        account_id = account.optInt("account_id", 0);
        user_id = account.optInt("user_id", 0);
        bank_id = account.optInt("user_id", 0);
        branch_id = account.optInt("user_id", 0);
        account_name = account.optString("account_name", "");
        balance = account.optDouble("balance", 0);
        account_image = account.optString("account_image", "");
    }

    public Account(Parcel in) {
        account_id = in.readInt();
        user_id = in.readInt();
        bank_id = in.readInt();
        branch_id = in.readInt();
        account_name = in.readString();
        balance = in.readDouble();
        account_image = in.readString();
    }

    public Account() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return account_name;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(account_id);
        out.writeInt(user_id);
        out.writeInt(bank_id);
        out.writeInt(branch_id);
        out.writeString(account_name);
        out.writeDouble(balance);
        out.writeString(account_image);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }
}
