package com.hp.advantage.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class Product implements Parcelable {
    public static final Creator<Product> CREATOR =
            new Creator<Product>() {
                public Product createFromParcel(Parcel in) {
                    return new Product(in);
                }

                public Product[] newArray(int size) {
                    return new Product[size];
                }
            };
    public String product_id;
    public String product_name;
    public double product_cost;
    public String product_description;
    public String product_image;
    public String product_url;

    public Product(JSONObject product) {
        product_id = product.optString("product_id", "");
        product_name = product.optString("product_name", "");
        product_cost = product.optDouble("product_cost", 0);
        product_description = product.optString("product_description", "");
        product_image = product.optString("product_image", "");
        product_url = product.optString("product_url", "");
    }

    public Product() {
        // TODO Auto-generated constructor stub
    }

    private Product(Parcel in) {
        product_id = in.readString();
        product_name = in.readString();
        product_cost = in.readDouble();
        product_description = in.readString();
        product_image = in.readString();
        product_url = in.readString();
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(product_id);
        out.writeString(product_name);
        out.writeDouble(product_cost);
        out.writeString(product_description);
        out.writeString(product_image);
        out.writeString(product_url);
    }
}