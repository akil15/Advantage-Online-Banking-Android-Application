package com.hp.advantage.data;

import android.os.Parcel;
import android.os.Parcelable;

public class SideMenuItem implements Parcelable {
    public static final Parcelable.Creator<SideMenuItem> CREATOR =
            new Parcelable.Creator<SideMenuItem>() {
                public SideMenuItem createFromParcel(Parcel in) {
                    return new SideMenuItem(in);
                }

                public SideMenuItem[] newArray(int size) {
                    return new SideMenuItem[size];
                }
            };
    public String Title;
    public String FragmentName;
    public String Image;

    public SideMenuItem(String title, String fragmentName, String image) {

        Title = title;
        FragmentName = fragmentName;
        Image = image;
    }


    public SideMenuItem(Parcel in) {
        Title = in.readString();
        FragmentName = in.readString();
        Image = in.readString();
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return Title;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(Title);
        out.writeString(FragmentName);
        out.writeString(Image);
    }
}
