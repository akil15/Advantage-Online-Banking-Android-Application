package com.hp.advantage.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressLint("ValidFragment")
public class AdvantageFragment extends Fragment implements Parcelable {
    public static final Parcelable.Creator<AdvantageFragment> CREATOR
            = new Parcelable.Creator<AdvantageFragment>() {
        public AdvantageFragment createFromParcel(Parcel in) {
            return new AdvantageFragment(in);
        }

        public AdvantageFragment[] newArray(int size) {
            return new AdvantageFragment[size];
        }
    };

    public AdvantageFragment(Parcel source) {

    }

    public AdvantageFragment() {

    }

    public String GetTitle() {
        return "";
    }

    public boolean ShowTitleBar() {
        return true;
    }

    public String getFragmentID() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub

    }

}
