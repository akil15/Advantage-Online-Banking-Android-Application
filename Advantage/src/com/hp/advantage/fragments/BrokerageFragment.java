package com.hp.advantage.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hp.advantage.R;
import com.hp.advantage.adapters.StockListAdapter;
import com.hp.advantage.data.Stock;
import com.hp.advantage.utils.GenericUtils;
import com.hp.advantage.utils.LogManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class BrokerageFragment extends AdvantageFragment implements OnGestureListener {
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private RelativeLayout rl;
    private Button GetQoutesButton;
    private EditText stocksEditText;
    private ArrayList<Stock> stockList;
    private ListView stockssListView;
    private StockListAdapter stockListAdapter;
    private GestureDetector gestureScanner;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("stockList", (ArrayList<? extends Parcelable>) stockList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.fragment_brokerage, container, false);
        if (savedInstanceState != null) {
            stockList = savedInstanceState.getParcelableArrayList("stockList");
        } else {
            stockList = new ArrayList<Stock>();
        }

        gestureScanner = new GestureDetector(this.getActivity(), this);

        GetQoutesButton = (Button) rl.findViewById(R.id.getQoutesButton);
        stocksEditText = (EditText) rl.findViewById(R.id.stocksEditText);
        RadioButton nasdaqRadioButton = (RadioButton) rl.findViewById(R.id.nasdaqRadioButton);
        RadioButton dowRadioButton = (RadioButton) rl.findViewById(R.id.dowRadioButton);
        RadioButton sp500RadioButton = (RadioButton) rl.findViewById(R.id.sp500RadioButton);
        nasdaqRadioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub

            }
        });
        dowRadioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub

            }
        });
        sp500RadioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub

            }
        });

        GetQoutesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String stock_symbols = stocksEditText.getText().toString();
                if (stock_symbols.toLowerCase(Locale.US).contains("crsh")) {
                    // Simulate crash
                    GenericUtils.Crash("Brokerage CRSH");
                }

                GetStockQuotesTask getStockQuotesTask = new GetStockQuotesTask();
                getStockQuotesTask.execute(stock_symbols);
                SetLoadingAnimation(true, "Retrieving Stocks");

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(stocksEditText.getWindowToken(), 0);
            }
        });

        stockssListView = (ListView) rl.findViewById(R.id.stocksListView);
        InitializeListView();

        return rl;
    }

    protected void InitializeListView() {
        stockListAdapter = new StockListAdapter(this.getActivity(), stockList);
        stockssListView.setAdapter(stockListAdapter);
        stockListAdapter.notifyDataSetChanged();

        stockssListView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureScanner.onTouchEvent(event);
            }
        });
    }

    public void PopulateList(String jSonData) {

        JSONObject jObj = null;
        if (jSonData == null) {
            FillWithSampleDate();
            if (stockListAdapter != null) {
                stockListAdapter.notifyDataSetChanged();
            }
        } else {
            // try parse the string to a JSON object
            try {
                jObj = new JSONObject(jSonData);
                // Get the element that holds the items ( JSONArray )
                stockList.clear();
                JSONArray QueryResult = jObj.getJSONArray("stocks");
                if (QueryResult != null) {
                    if (QueryResult.length() == 0)
                        FillWithSampleDate();
                    // Loop the Array
                    for (int i = 0; i < QueryResult.length(); i++) {
                        JSONObject e = QueryResult.getJSONObject(i);
                        if (e != null) {
                            Stock stock = new Stock(e);
                            stockList.add(stock);
                        }
                    }
                }
                if (stockListAdapter != null) {
                    stockListAdapter.notifyDataSetChanged();
                }

            } catch (Exception exp) {
                LogManager.Error("PopulateList", exp);
            }
        }
        SetLoadingAnimation(false, null);

    }

    private void FillWithSampleDate() {
        Random rnd = new Random();
        String[] exchanges = {"Nasdaq", "Dow", "SP500"};
        for (int i = 0; i < 10; i++) {
            Stock stock = new Stock();
            stock.symbol = String.valueOf(Character.toChars((rnd.nextInt(26) + 65))) + String.valueOf(Character.toChars((rnd.nextInt(26) + 65))) + String.valueOf(Character.toChars((rnd.nextInt(26) + 65))) + String.valueOf(Character.toChars((rnd.nextInt(26) + 65)));
            stock.name = String.valueOf(Character.toChars((rnd.nextInt(26) + 65))) + String.valueOf(Character.toChars((rnd.nextInt(26) + 65))) + String.valueOf(Character.toChars((rnd.nextInt(26) + 65))) + String.valueOf(Character.toChars((rnd.nextInt(26) + 65))) + String.valueOf(Character.toChars((rnd.nextInt(26) + 65))) + String.valueOf(Character.toChars((rnd.nextInt(26) + 65))) + String.valueOf(Character.toChars((rnd.nextInt(26) + 65))) + String.valueOf(Character.toChars((rnd.nextInt(26) + 65)));

            stock.bid = rnd.nextDouble() * 1000;
            stock.open = rnd.nextDouble() * 1000;
            stock.change = (rnd.nextDouble() * 100 - 50);
            stock.stock_exchange = exchanges[rnd.nextInt(3)];
            stock.last_trade_date = (new Date()).toString();
            stock.percent_change = (rnd.nextDouble() * 100 - 50) + "%";
            stockList.add(stock);
        }

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
        return "Brokerage";
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
                int pos = stockssListView.pointToPosition((int) e1.getX(), (int) e1.getY());
                Stock swipedStock = (Stock) stockssListView.getAdapter().getItem((pos));
                View listItemView = getViewByPosition(pos, stockssListView);
                if (listItemView != null) {
                    Animation animation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.swipe_left);
                    listItemView.startAnimation(animation);
                }
                return true;

            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // Right Swipe
                int pos = stockssListView.pointToPosition((int) e1.getX(), (int) e1.getY());
                Stock swipedStock = (Stock) stockssListView.getAdapter().getItem((pos));
                View listItemView = getViewByPosition(pos, stockssListView);
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
    private class GetStockQuotesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            JSONObject jObject = new JSONObject();
            try {
                JSONObject jsonObj = GenericUtils.GetDefaultJSONObj();
                jsonObj.putOpt("stock_symbols", params[0]);
                jObject.putOpt("data", jsonObj);
            } catch (JSONException e) {
                e.printStackTrace();
                LogManager.Error("GetStockQuotesTask", e);
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                LogManager.Error("GetStockQuotesTask", e);
                return null;
            }

            LogManager.Info(">>>> " + jObject.toString());
            return GenericUtils.HTTPRequest("GetStockQuotes", jObject
                    .toString());
        }

        @Override
        protected void onPostExecute(String result) {
            LogManager.Info("GetStockQuotesTask - " + result);
            PopulateList(result);
            LogManager.Info("End GetStockQuotesTask " + new Date());
        }
    }

}
