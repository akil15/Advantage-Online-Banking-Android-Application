package com.hp.advantage.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hp.advantage.R;
import com.hp.advantage.activities.MainActivity;
import com.hp.advantage.adapters.AccountDropDownAdapter;
import com.hp.advantage.data.Account;
import com.hp.advantage.data.Bill;
import com.hp.advantage.utils.GenericUtils;
import com.hp.advantage.utils.LogManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class PayBillFragment extends AdvantageFragment {
    private ArrayList<Account> accountList;
    private RelativeLayout rl;
    private Spinner payFromFromSpinner;
    private AccountDropDownAdapter accountDropDownAdapter;
    private ArrayList<Bill> billList;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("accountList", (ArrayList<? extends Parcelable>) accountList);
        outState.putParcelableArrayList("billList", (ArrayList<? extends Parcelable>) billList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.fragment_pay_bill, container, false);

        Button payBillButton = (Button) rl.findViewById(R.id.payBillButton);
        payFromFromSpinner = (Spinner) rl.findViewById(R.id.payFromFromSpinner);
        TextView billNameTextView = (TextView) rl.findViewById(R.id.billNameTextView);
        ImageView billImageView = (ImageView) rl.findViewById(R.id.billImageView);
        ImageView billPayeeImageView = (ImageView) rl.findViewById(R.id.billPayeeImageView);
        TextView billAmountTextView = (TextView) rl.findViewById(R.id.billAmountTextView);

        if (savedInstanceState != null) {
            accountList = savedInstanceState.getParcelableArrayList("accountList");
            billList = savedInstanceState.getParcelableArrayList("billList");
            if (billList.size()>0) {
                billNameTextView.setText(billList.get(0).bill_name + " for the " + billList.get(0).bill_payee_name);
                GenericUtils.Instance(this.getActivity()).GetImageLoader().DisplayImageOriginalSize(billList.get(0).bill_image, billImageView);
                GenericUtils.Instance(this.getActivity()).GetImageLoader().DisplayImageOriginalSize(billList.get(0).bill_payee_image, billPayeeImageView);

                billAmountTextView.setText("$"+Double.toString(billList.get(0).bill_amount));
            }
        }
        else
        {
            accountList = new ArrayList<Account>();
            GetAccountsTask getAccountsTask = new GetAccountsTask();
            getAccountsTask.execute();

            Bundle args = getArguments();
            billList = new ArrayList<Bill>();
            if (args!=null && args.containsKey("bills"))
            {
                billList = args.getParcelableArrayList("bills");
                if (billList.size()>0) {
                    billNameTextView.setText(billList.get(0).bill_name + " for the " + billList.get(0).bill_payee_name);
                    GenericUtils.Instance(this.getActivity()).GetImageLoader().DisplayImageOriginalSize(billList.get(0).bill_image, billImageView);
                    GenericUtils.Instance(this.getActivity()).GetImageLoader().DisplayImageOriginalSize(billList.get(0).bill_payee_image, billPayeeImageView);
                    billAmountTextView.setText("$"+Double.toString(billList.get(0).bill_amount));
                }
            }

            SetLoadingAnimation(true);
        }

        // Set selected Call for instrumentation
        payFromFromSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        payBillButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Account fromAccount = (Account) payFromFromSpinner.getSelectedItem();
                PayBillTask payBillTask = new PayBillTask();
                payBillTask.execute(Integer.toString(fromAccount.account_id), billList.get(0).bill_id, Double.toString(billList.get(0).bill_amount));

                if (billList.get(0).bill_amount > 1000) {
                    SetLoadingAnimation(true, "Paying expensive bill");
                } else {
                    SetLoadingAnimation(true, "Paying item");
                }
            }
        });
        accountDropDownAdapter = new AccountDropDownAdapter(this.getActivity(), android.R.layout.simple_spinner_item, accountList);
        payFromFromSpinner.setAdapter(accountDropDownAdapter);
        return rl;
    }

    public void Refresh(List<Account> _accounts) {
        accountList.clear();
        for (Account account : _accounts) {
            accountList.add(account);
        }

        // Update
        accountDropDownAdapter.notifyDataSetChanged();
    }

    public void PopulateList(String jSonData) {
        JSONObject jObj = null;
        if (jSonData == null) {
            FillWithSampleData();
            if (accountDropDownAdapter != null) {
                accountDropDownAdapter.notifyDataSetChanged();
            }
        } else {
            // try parse the string to a JSON object
            try {
                jObj = new JSONObject(jSonData);
                // Get the element that holds the items ( JSONArray )
                accountList.clear();
                JSONArray QueryResult = jObj.getJSONArray("accounts");
                if (QueryResult != null) {
                    if (QueryResult.length() == 0)
                        FillWithSampleData();

                    // Loop the Array
                    for (int i = 0; i < QueryResult.length(); i++) {
                        JSONObject e = QueryResult.getJSONObject(i);
                        if (e != null) {
                            Account account = new Account(e);
                            accountList.add(account);
                        }
                    }
                }
                if (accountDropDownAdapter != null) {
                    accountDropDownAdapter.notifyDataSetChanged();
                }

            } catch (Exception exp) {
                LogManager.Error("PopulateList", exp);
            }
        }

        if (accountList.size() > 1) {
            payFromFromSpinner.setSelection(0);
        }
        SetLoadingAnimation(false);

    }

    private void FillWithSampleData() {
        Random rnd = new Random();
        for (int i = 0; i < 10; i++) {
            Account account = new Account();
            account.account_id = rnd.nextInt();
            account.user_id = i;
            account.bank_id = rnd.nextInt();
            account.branch_id = i;
            account.account_name = "Account " + account.account_id;
            account.balance = rnd.nextLong();
            accountList.add(account);
        }
    }

    protected void SetLoadingAnimation(boolean start) {
        SetLoadingAnimation(start, null);
    }

    protected void SetLoadingAnimation(boolean start, String loadingText) {
        if (rl != null) {
            RelativeLayout loadingRelativeLayout = (RelativeLayout) rl.findViewById(R.id.loadingRelativeLayout);
            ImageView imageViewLoadingIcon = (ImageView) rl.findViewById(R.id.imageViewLoadingIcon);
            if (loadingText != null) {
                TextView imageViewLoadingTitle2 = (TextView) rl.findViewById(R.id.imageViewLoadingTitle2);
                imageViewLoadingTitle2.setText(loadingText);
            }

            if (start == true) {
                Animation rotate_continously = AnimationUtils.loadAnimation(rl.getContext(), R.anim.rotate_continously);
                imageViewLoadingIcon.startAnimation(rotate_continously);

                loadingRelativeLayout.setVisibility(View.VISIBLE);
            } else {
                loadingRelativeLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public String GetTitle() {
        return "Pay Bill";
    }

    //run
    private class GetAccountsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            JSONObject jObject = new JSONObject();
            try {
                JSONObject jsonObj = GenericUtils.GetDefaultJSONObj();
                jObject.putOpt("data", jsonObj);
            } catch (JSONException e) {
                e.printStackTrace();
                LogManager.Error("GetAccountsTask", e);
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                LogManager.Error("GetAccountsTask", e);
                return null;
            }

            LogManager.Info(">>>> " + jObject.toString());
            return GenericUtils.HTTPRequest("GetAccounts", jObject
                    .toString());
        }

        @Override
        protected void onPostExecute(String result) {
            LogManager.Info("GetAccountsTask - " + result);
            PopulateList(result);
            LogManager.Info("End GetAccountsTask " + new Date());
        }
    }

    //run
    private class PayBillTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            JSONObject jObject = new JSONObject();
            double bill_amount = 0;
            try {
                JSONObject jsonObj = GenericUtils.GetDefaultJSONObj();
                jsonObj.putOpt("account_from", params[0]);
                jsonObj.putOpt("bill_id", params[1]);
                jObject.putOpt("data", jsonObj);
                bill_amount = Double.parseDouble(params[2]);
            } catch (JSONException e) {
                e.printStackTrace();
                LogManager.Error("PayBillTask", e);
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                LogManager.Error("PayBillTask", e);
                return null;
            }

            if (bill_amount > 1000) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } // Sleep for 10 seconds before transferring money
            }

            LogManager.Info(">>>> " + jObject.toString());
            return GenericUtils.HTTPRequest("PayBill", jObject
                    .toString());
        }

        @Override
        protected void onPostExecute(String result) {
            LogManager.Info("PayBillTask - " + result);
            JSONObject jObj = null;
            boolean IsSuccess = false;
            if (result != null) {
                // try parse the string to a JSON object
                try {
                    jObj = new JSONObject(result);
                    String status = jObj.optString("status", "failed");
                    if (status.equals("success")) {
                        IsSuccess = true;
                     }
                } catch (Exception exp) {
                    LogManager.Error("PayBillTask", exp);
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(PayBillFragment.this.getActivity(), R.style.AlertDialogCustom));
            builder.setMessage("Bill paid")
                    .setIcon(R.drawable.ic_launcher)
                    .setCancelable(false)
                    .setMessage((IsSuccess == true) ? "Bill paid correctly" : "Bill failed to be paid")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            SetLoadingAnimation(false);
            ((MainActivity) PayBillFragment.this.getActivity()).NavigateTo(AccountsFragment.class.getName(), false, null);
        }
    }
}

