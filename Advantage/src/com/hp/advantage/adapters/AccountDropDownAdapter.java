package com.hp.advantage.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hp.advantage.R;
import com.hp.advantage.data.Account;
import com.hp.advantage.utils.GenericUtils;

import java.util.ArrayList;
import java.util.List;

public class AccountDropDownAdapter extends ArrayAdapter<Account> {

    private Activity context;
    private List<Account> AccountList;

    public AccountDropDownAdapter(Activity context, int resource, ArrayList<Account> accountList) {
        super(context, resource, accountList);
        this.context = context;
        this.AccountList = accountList;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {   // This view starts when we click the spinner.
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.account_spinner_layout, parent, false);
        }

        Account item = AccountList.get(position);

        if (item != null) {   // Parse the data from each object and set it.
            ImageView imageIcon = (ImageView) row.findViewById(R.id.imageIcon);
            TextView accountName = (TextView) row.findViewById(R.id.accountName);
            if (accountName != null)
                accountName.setText(item.account_name);
            if (imageIcon != null)
                GenericUtils.Instance().GetImageLoader().DisplayImageOriginalSize(item.account_image, imageIcon);
        }

        return row;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // This view starts when we click the spinner.
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.account_spinner_layout, parent, false);
        }

        Account item = AccountList.get(position);

        if (item != null) {   // Parse the data from each object and set it.
            ImageView imageIcon = (ImageView) row.findViewById(R.id.imageIcon);
            TextView accountName = (TextView) row.findViewById(R.id.accountName);
            if (accountName != null)
                accountName.setText(item.account_name);
            if (imageIcon != null)
                GenericUtils.Instance().GetImageLoader().DisplayImageOriginalSize(item.account_image, imageIcon);

        }

        return row;
    }
}
