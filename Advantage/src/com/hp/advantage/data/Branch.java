package com.hp.advantage.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class Branch implements Parcelable {
    public static final Parcelable.Creator<Branch> CREATOR =
            new Parcelable.Creator<Branch>() {
                public Branch createFromParcel(Parcel in) {
                    return new Branch(in);
                }

                public Branch[] newArray(int size) {
                    return new Branch[size];
                }
            };
    public int id;
    public double lat;
    public double lon;
    public String name;
    public String icon_image_url;
    public String logo_image_url;
    public String image_url;
    public String phone_number;

    public Branch(JSONObject branch) {
        id = branch.optInt("id", 0);
        lat = branch.optDouble("lat", 0.0);
        lon = branch.optDouble("lon", 0.0);
        name = branch.optString("name", "");
        icon_image_url = branch.optString("icon_image_url", "");
        logo_image_url = branch.optString("logo_image_url", "");
        image_url = branch.optString("image_url", "");
        phone_number = branch.optString("phone_number", "");
    }

    public Branch(Parcel in) {
        id = in.readInt();
        lat = in.readDouble();
        lon = in.readDouble();
        name = in.readString();
        icon_image_url = in.readString();
        logo_image_url = in.readString();
        image_url = in.readString();
        phone_number = in.readString();
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeDouble(lat);
        out.writeDouble(lon);
        out.writeString(name);
        out.writeString(icon_image_url);
        out.writeString(logo_image_url);
        out.writeString(image_url);
        out.writeString(phone_number);
    }

}
