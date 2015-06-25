package com.hp.advantage.fragments;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hp.advantage.R;
import com.hp.advantage.adapters.TransactionListAdapter;
import com.hp.advantage.data.Transaction;
import com.hp.advantage.data.Account;
import com.hp.advantage.utils.GenericUtils;
import com.hp.advantage.utils.LogManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;


public class TransactionsFragment extends AdvantageFragment implements OnGestureListener {
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private TransactionListAdapter transactionListAdapter;
    private ArrayList<Transaction> transactionList;
    private RelativeLayout rl;
    private ListView transactionsListView;
    private Account account;

    private GestureDetector gestureScanner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.fragment_transactions, container, false);
        Bundle args = getArguments();
        if (args.containsKey("account"))
        {
            account = (args.getParcelable("account"));
        }

        transactionsListView = (ListView) rl.findViewById(R.id.transactionsListView);
        if (savedInstanceState != null) {
            transactionList = (savedInstanceState.getParcelableArrayList("transactions"));
        } else {
            transactionList = new ArrayList<Transaction>();
            GetTransactionsTask getTransactionsTask = new GetTransactionsTask();
            getTransactionsTask.execute(String.valueOf(account.account_id));
            SetLoadingAnimation(true);
        }
        transactionListAdapter = new TransactionListAdapter(this.getActivity(), transactionList);
        transactionsListView.setAdapter(transactionListAdapter);
        transactionListAdapter.notifyDataSetChanged();

        gestureScanner = new GestureDetector(this.getActivity(), this);
        transactionsListView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureScanner.onTouchEvent(event);
            }
        });
        return rl;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("transactions", (ArrayList<? extends Parcelable>) transactionList);
    }

    public void PopulateList(String jSonData) {

        JSONObject jObj = null;
        if (jSonData == null) {
            FillWithSampleDate();
            if (transactionListAdapter != null) {
                transactionListAdapter.notifyDataSetChanged();
            }
        } else {
            // try parse the string to a JSON object
            try {
                jObj = new JSONObject(jSonData);
                // Get the element that holds the items ( JSONArray )
                transactionList.clear();
                JSONArray QueryResult = jObj.getJSONArray("transactions");
                if (QueryResult != null) {
                    if (QueryResult.length() == 0)
                        FillWithSampleDate();
                    // Loop the Array
                    for (int i = 0; i < QueryResult.length(); i++) {
                        JSONObject e = QueryResult.getJSONObject(i);
                        if (e != null) {
                            Transaction transaction = new Transaction(e);
                            transactionList.add(transaction);
                        }
                    }
                }
                if (transactionListAdapter != null) {
                    transactionListAdapter.notifyDataSetChanged();
                }

            } catch (Exception exp) {
                LogManager.Error("PopulateList", exp);
            }
        }
        SetLoadingAnimation(false);

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

    private void FillWithSampleDate() {
        Random rnd = new Random();

        String[] descriptions = {"ATM", "Charge", "Check", "Deposit", "Online", "POS", "Transfer", "Withdrawal"};
        String[] transactionImages = {"atm.png", "charge.png", "check.png", "deposit.png", "online.png", "pos.png", "transfer.png", "withdrawal.png"};
        for (int i = 0; i < 8; i++) {
            Transaction transaction = new Transaction();
            transaction.account_id = rnd.nextInt(4);
            transaction.transaction_id = i;
            transaction.transaction_date = GenericUtils.GetDateFormatter().format(new Date());
            transaction.description = descriptions[i];
            transaction.amount = Math.floor(rnd.nextDouble() * 1000);
            transaction.transaction_image = transactionImages[i];
            transactionList.add(transaction);
        }
    }

    @Override
    public String GetTitle()
    {
        if (account==null)
        {
            Bundle args = getArguments();
            if (args!=null && args.containsKey("account"))
            {
                account = (args.getParcelable("account"));
            }
            else
            {
                return "";
            }
        }
        return account.account_name;
    }

    @Override
    public boolean onDown(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {

                return false;
            }
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // Left Swipe
                int pos = transactionsListView.pointToPosition((int) e1.getX(), (int) e1.getY());
                // Transaction swipedTransaction = (Transaction) transactionsListView.getAdapter().getItem((pos));
                View listItemView = getViewByPosition(pos, transactionsListView);
                listItemView.setBackgroundColor(Color.RED);
                if (listItemView != null) {
                    Animation animation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.swipe_left);
                    listItemView.startAnimation(animation);
                }
                return true;

            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // Right Swipe
                int pos = transactionsListView.pointToPosition((int) e1.getX(), (int) e1.getY());
                //Transaction swipedTransaction = (Transaction) transactionsListView.getAdapter().getItem((pos));
                View listItemView = getViewByPosition(pos, transactionsListView);
                listItemView.setBackgroundColor(Color.GREEN);
                if (listItemView != null) {
                    Animation animation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.swipe_right);
                    listItemView.startAnimation(animation);
                }
                return true;
            }
        } catch (Exception e) {
            LogManager.Error(e);
        }

        return true;
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    //run
    private class GetTransactionsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            JSONObject jObject = new JSONObject();
            try {
                JSONObject jsonObj = GenericUtils.GetDefaultJSONObj();
                jsonObj.putOpt("account_id", params[0]);
                jObject.putOpt("data", jsonObj);
            } catch (JSONException e) {
                e.printStackTrace();
                LogManager.Error("GetTransactionsTask", e);
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                LogManager.Error("GetTransactionsTask", e);
                return null;
            }

            LogManager.Info(">>>> " + jObject.toString());
            return GenericUtils.HTTPRequest("GetTransactions", jObject
                    .toString());
        }

        @Override
        protected void onPostExecute(String result) {
            LogManager.Info("GetTransactionsTask - " + result);
            PopulateList(result);
            LogManager.Info("End GetTransactionsTask " + new Date());
        }
    }

}
