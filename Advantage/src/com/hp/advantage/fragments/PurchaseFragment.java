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
import com.hp.advantage.data.Product;
import com.hp.advantage.utils.GenericUtils;
import com.hp.advantage.utils.LogManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class PurchaseFragment extends AdvantageFragment {
    private ArrayList<Account> accountList;
    private RelativeLayout rl;
    private Button PurchaseButton;
    private Spinner purchaseFromSpinner;
    private TextView productNameTextView;
    private ImageView productImageView;
    private TextView productPriceTextView;
    private AccountDropDownAdapter accountDropDownAdapter;
    private ArrayList<String> productIds;
    private ArrayList<Product> productList;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("accountList", (ArrayList<? extends Parcelable>) accountList);
        outState.putParcelableArrayList("productList", (ArrayList<? extends Parcelable>) productList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.fragment_purchase, container, false);

        PurchaseButton = (Button) rl.findViewById(R.id.purchaseButton);
        purchaseFromSpinner = (Spinner) rl.findViewById(R.id.purchaseFromSpinner);
        productNameTextView = (TextView) rl.findViewById(R.id.productNameTextView);
        productImageView = (ImageView) rl.findViewById(R.id.productImageView);
        productPriceTextView = (TextView) rl.findViewById(R.id.productPriceTextView);

        if (savedInstanceState != null) {
            accountList = savedInstanceState.getParcelableArrayList("accountList");
            productList = savedInstanceState.getParcelableArrayList("productList");
            if (productList.size()>0) {
                productNameTextView.setText(productList.get(0).product_name);
                GenericUtils.Instance(this.getActivity()).GetImageLoader().DisplayImageOriginalSize(productList.get(0).product_image, productImageView);
                productPriceTextView.setText("$"+Double.toString(productList.get(0).product_cost));
            }
        }
        else
        {
            accountList = new ArrayList<Account>();
            GetAccountsTask getAccountsTask = new GetAccountsTask();
            getAccountsTask.execute();

            Bundle args = getArguments();
            productList = new ArrayList<Product>();
            if (args!=null && args.containsKey("products"))
            {
                productList = args.getParcelableArrayList("products");
                if (productList.size()>0) {
                    productNameTextView.setText(productList.get(0).product_name);
                    GenericUtils.Instance(this.getActivity()).GetImageLoader().DisplayImageOriginalSize(productList.get(0).product_image, productImageView);
                    productPriceTextView.setText("$"+Double.toString(productList.get(0).product_cost));
                }
            }
            /*
            if (productIds!=null && productIds.size()>0)
            {
                GetProductsTask getItemsTask = new GetProductsTask();
                getItemsTask.execute(productIds.get(0));
            }
            */
            SetLoadingAnimation(true);
        }

        // Set selected Call for instrumentation
        purchaseFromSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

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

        PurchaseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Account fromAccount = (Account) purchaseFromSpinner.getSelectedItem();
                PurchaseTask purchaseTask = new PurchaseTask();
                purchaseTask.execute(Integer.toString(fromAccount.account_id), productList.get(0).product_id,  Double.toString(productList.get(0).product_cost));

                if (productList.get(0).product_cost > 1000) {
                    SetLoadingAnimation(true, "Buying expensive item");
                } else {
                    SetLoadingAnimation(true, "Buying item");
                }


                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(productPriceTextView.getWindowToken(), 0);
            }
        });
        accountDropDownAdapter = new AccountDropDownAdapter(this.getActivity(), android.R.layout.simple_spinner_item, accountList);
        purchaseFromSpinner.setAdapter(accountDropDownAdapter);
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
            purchaseFromSpinner.setSelection(0);
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
        return "Buy Item";
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
    private class PurchaseTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            JSONObject jObject = new JSONObject();
            double cost = 0;
            try {
                JSONObject jsonObj = GenericUtils.GetDefaultJSONObj();
                jsonObj.putOpt("account_from", params[0]);
                jsonObj.putOpt("product_code", params[1]);
                jObject.putOpt("data", jsonObj);
                cost = Double.parseDouble(params[2]);
            } catch (JSONException e) {
                e.printStackTrace();
                LogManager.Error("PurchaseTask", e);
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                LogManager.Error("PurchaseTask", e);
                return null;
            }

            if (cost > 1000) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } // Sleep for 10 seconds before transferring money
            }

            LogManager.Info(">>>> " + jObject.toString());
            return GenericUtils.HTTPRequest("PurchaseProduct", jObject
                    .toString());
        }

        @Override
        protected void onPostExecute(String result) {
            LogManager.Info("PurchaseTask - " + result);
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
                    LogManager.Error("PurchaseTask", exp);
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(PurchaseFragment.this.getActivity(), R.style.AlertDialogCustom));
            builder.setMessage("Item purchased")
                    .setIcon(R.drawable.ic_launcher)
                    .setCancelable(false)
                    .setMessage((IsSuccess == true) ? "Item purchased correctly" : "Item failed to purchase")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            SetLoadingAnimation(false);
            ((MainActivity) PurchaseFragment.this.getActivity()).NavigateTo(AccountsFragment.class.getName(), false, null);
        }
    }
}

