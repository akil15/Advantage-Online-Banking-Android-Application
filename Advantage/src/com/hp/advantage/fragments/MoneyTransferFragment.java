package com.hp.advantage.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.advantage.R;
import com.hp.advantage.activities.MainActivity;
import com.hp.advantage.adapters.AccountDropDownAdapter;
import com.hp.advantage.data.Account;
import com.hp.advantage.utils.GenericUtils;
import com.hp.advantage.utils.LogManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MoneyTransferFragment extends AdvantageFragment  implements DatePickerDialog.OnDateSetListener  {
    private ArrayList<Account> accountList;
    private RelativeLayout rl;
    private ListView accountsListView;
    private Button TransferButton;
    private Spinner transferFromSpinner;
    private Spinner transferToSpinner;
    private TextView transferTransferDateTextView;
    private EditText transferSumEditText;
    private AccountDropDownAdapter accountDropDownAdapter;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("accountList", (ArrayList<? extends Parcelable>) accountList);
        outState.putString("transferDate", transferTransferDateTextView.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.fragment_money_transfer, container, false);
        TransferButton = (Button) rl.findViewById(R.id.transferButton);
        transferFromSpinner = (Spinner) rl.findViewById(R.id.transferFromSpinner);
        transferToSpinner = (Spinner) rl.findViewById(R.id.transferToSpinner);
        transferSumEditText = (EditText) rl.findViewById(R.id.transferSumEditText);
        transferTransferDateTextView = (TextView) rl.findViewById(R.id.transferTransferDateTextView);

        if (savedInstanceState != null) {
            accountList = savedInstanceState.getParcelableArrayList("accountList");
            transferTransferDateTextView.setText(savedInstanceState.getString("transferDate"));
        } else {
            transferTransferDateTextView.setText(GenericUtils.GetDateFormatter().format(new Date()));
            accountList = new ArrayList<Account>();
            GetAccountsTask getAccountsTask = new GetAccountsTask();
            getAccountsTask.execute();
            SetLoadingAnimation(true);
        }


        transferTransferDateTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Date currentSelectedDate = new Date();
                try {
                    currentSelectedDate = GenericUtils.GetDateFormatter().parse(transferTransferDateTextView.getText().toString());
                }
                catch (Exception e)
                {

                }

                // Use the current date as the default date in the picker
                final Calendar c = Calendar.getInstance();
                c.setTime(currentSelectedDate);
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Create a new instance of DatePickerDialog and return it
                (new DatePickerDialog(MoneyTransferFragment.this.getActivity(), MoneyTransferFragment.this, year, month, day)).show();
            }
        });
        // Set selected Call for instrumentation
        transferFromSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

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
        transferToSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

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

        TransferButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Account fromAccount = (Account) transferFromSpinner.getSelectedItem();
                Account toAccount = (Account) transferToSpinner.getSelectedItem();
                String transferDate = transferTransferDateTextView.getText().toString();
                String sum = transferSumEditText.getText().toString();

                try {
                    // Simulate crashes
                    int sumInt = (Integer.parseInt(sum)); // Throws number format exception for characters

                    TransferMoneyTask transferMoneyTask = new TransferMoneyTask();
                    transferMoneyTask.execute(Integer.toString(fromAccount.account_id), Integer.toString(toAccount.account_id), transferDate, sum);

                    if (sumInt > 1000) {
                        SetLoadingAnimation(true, "Transferring large funds");
                    } else {
                        SetLoadingAnimation(true, "Transferring funds");
                    }


                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(transferSumEditText.getWindowToken(), 0);
                }
                catch (NumberFormatException nfe)
                {
                    Toast.makeText(getActivity(), "Invalid funds", Toast.LENGTH_SHORT).show();

                }
            }
        });
        accountDropDownAdapter = new AccountDropDownAdapter(this.getActivity(), android.R.layout.simple_spinner_item, accountList);
        transferFromSpinner.setAdapter(accountDropDownAdapter);
        transferToSpinner.setAdapter(accountDropDownAdapter);
        return rl;
    }

    public void onDateSet(DatePicker view, int year, int month, int day)
    {

        try {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date currentSelectedDate =  cal.getTime();
            transferTransferDateTextView.setText(GenericUtils.GetDateFormatter().format(currentSelectedDate));
        }
        catch (Exception e)
        {

        }
    }

    public void Refresh(List<Account> _accounts) {
        accountList.clear();
        for (Account account : _accounts) {
            accountList.add(account);
        }

        // Save ListView state
        Parcelable state = accountsListView.onSaveInstanceState();

        // Update
        accountDropDownAdapter.notifyDataSetChanged();

        // Restore previous state (including selected item index and scroll position)
        accountsListView.onRestoreInstanceState(state);
    }

    public void PopulateList(String jSonData) {
        JSONObject jObj = null;
        if (jSonData == null) {
            FillWithSampleDate();
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
                if (accountDropDownAdapter != null) {
                    accountDropDownAdapter.notifyDataSetChanged();
                }

            } catch (Exception exp) {
                LogManager.Error("PopulateList", exp);
            }
        }

        if (accountList.size() > 1) {
            transferFromSpinner.setSelection(0);
            transferToSpinner.setSelection(1);
        }
        SetLoadingAnimation(false);

    }

    private void FillWithSampleDate() {
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
        return "Money Transfer";
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
    private class TransferMoneyTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            JSONObject jObject = new JSONObject();
            int sumInt = 0;
            try {
                JSONObject jsonObj = GenericUtils.GetDefaultJSONObj();
                jsonObj.putOpt("account_from", params[0]);
                jsonObj.putOpt("account_to", params[1]);
                jsonObj.putOpt("transfer_date", params[2]);
                jsonObj.putOpt("sum", params[3]);
                jObject.putOpt("data", jsonObj);
                sumInt = Integer.parseInt(params[3]);
            } catch (JSONException e) {
                e.printStackTrace();
                LogManager.Error("TransferMoneyTask", e);
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                LogManager.Error("TransferMoneyTask", e);
                return null;
            }

            if (sumInt > 1000) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } // Sleep for 10 seconds before transferring money
            }

            LogManager.Info(">>>> " + jObject.toString());
            return GenericUtils.HTTPRequest("TransferMoney", jObject
                    .toString());
        }

        @Override
        protected void onPostExecute(String result) {
            LogManager.Info("TransferMoneyTask - " + result);
            JSONObject jObj = null;
            boolean IsSuccess = false;
            if (result != null) {
                // try parse the string to a JSON object
                try {
                    jObj = new JSONObject(result);
                    String status = jObj.optString("status", "failed");
                    if (status.equals("success")) {
                        IsSuccess = true;

                        //((MainActivity)(MoneyTransferFragment.this.getActivity())).NavigateTo(OperationStatusFragment.class.getName(), true, "success");
                    }
                    /*
					else
					{
						((MainActivity)(MoneyTransferFragment.this.getActivity())).NavigateTo(OperationStatusFragment.class.getName(), true, "failed");
					}
					*/
                } catch (Exception exp) {
                    LogManager.Error("PopulateList", exp);
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MoneyTransferFragment.this.getActivity(), R.style.AlertDialogCustom));
            builder.setMessage("Money Transfer")
                    .setIcon(R.drawable.ic_launcher)
                    .setCancelable(false)
                    .setMessage((IsSuccess == true) ? "Money transferred correctly" : "Money failed to transfer")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

            if (IsSuccess)
            {
                ((MainActivity) MoneyTransferFragment.this.getActivity()).NavigateTo(AccountsFragment.class.getName(), false, null);
            }
            SetLoadingAnimation(false);
        }
    }
}
