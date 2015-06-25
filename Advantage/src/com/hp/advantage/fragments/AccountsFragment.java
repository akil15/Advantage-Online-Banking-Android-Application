package com.hp.advantage.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hp.advantage.R;
import com.hp.advantage.activities.MainActivity;
import com.hp.advantage.adapters.AccountListAdapter;
import com.hp.advantage.data.Account;
import com.hp.advantage.utils.GenericUtils;
import com.hp.advantage.utils.LogManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class AccountsFragment extends AdvantageFragment {
    private AccountListAdapter accountListAdapter;
    private ArrayList<Account> accountList;
    private RelativeLayout rl;
    private ListView accountsListView;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("accountList", (ArrayList<? extends Parcelable>) accountList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.fragment_accounts, container, false);
        if (savedInstanceState != null) {
            accountList = savedInstanceState.getParcelableArrayList("accountList");
            InitializeListView();
        } else {
            accountList = new ArrayList<Account>();
            InitializeListView();
            GetAccountsTask getAccountsTask = new GetAccountsTask();
            getAccountsTask.execute();
            SetLoadingAnimation(true);
        }
        return rl;
    }

    protected void InitializeListView() {
        accountsListView = (ListView) rl.findViewById(R.id.accountsListView);
        accountListAdapter = new AccountListAdapter(this.getActivity(), accountList);
        accountsListView.setAdapter(accountListAdapter);
        accountListAdapter.notifyDataSetChanged();

        accountsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Account selectedAccount = accountList.get(arg2);
                Bundle args = new Bundle();
                args.putParcelable("account", selectedAccount);
               ((MainActivity) (AccountsFragment.this.getActivity())).NavigateTo(TransactionsFragment.class.getName(), true, args);

            }
        });
    }

    public void PopulateList(String jSonData) {

        JSONObject jObj = null;
        if (jSonData == null) {
            FillWithSampleDate();
            if (accountListAdapter != null) {
                accountListAdapter.notifyDataSetChanged();
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
                        FillWithSampleDate();

                    // Loop the Array
                    for (int i = 0; i < QueryResult.length(); i++) {
                        JSONObject e = QueryResult.getJSONObject(i);
                        if (e != null) {
                            Account account = new Account(e);
                            accountList.add(account);
                        }
                    }
                }
                if (accountListAdapter != null) {
                    accountListAdapter.notifyDataSetChanged();
                }

            } catch (Exception exp) {
                LogManager.Error("PopulateList", exp);
            }
        }
        SetLoadingAnimation(false);

    }

    private void FillWithSampleDate() {
        Random rnd = new Random();

        String[] accountNames = {"Savings", "Checking", "Money Market", "Certificates of Deposit", "Individual Retirement"};
        String[] accountImages = {"savings.png", "checking.png", "money_market.png", "certificates_of_deposit.png", "individual_retirement.png"};
        for (int i = 0; i < 5; i++) {
            Account account = new Account();
            account.account_id = rnd.nextInt();
            account.user_id = GenericUtils.GetCurrentUser().user_id;
            account.bank_id = 1;
            account.branch_id = 1;
            account.account_name = accountNames[i];
            account.balance = Math.floor(rnd.nextDouble() * 100000);
            account.account_image = accountImages[i];
            accountList.add(account);
        }
    }

    protected void SetLoadingAnimation(boolean start) {
        if (rl != null) {
            RelativeLayout loadingRelativeLayout = (RelativeLayout) rl.findViewById(R.id.loadingRelativeLayout);
            ImageView imageViewLoadingIcon = (ImageView) rl.findViewById(R.id.imageViewLoadingIcon);

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
        return "Accounts";
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
}
