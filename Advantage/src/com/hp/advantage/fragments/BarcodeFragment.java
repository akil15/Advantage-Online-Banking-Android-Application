package com.hp.advantage.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hp.advantage.R;
import com.hp.advantage.activities.MainActivity;
import com.hp.advantage.data.Bill;
import com.hp.advantage.data.Product;
import com.hp.advantage.utils.GenericUtils;
import com.hp.advantage.utils.LogManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.ndeftools.Message;
import org.ndeftools.wellknown.TextRecord;

import java.util.ArrayList;
import java.util.Date;


public class BarcodeFragment extends AdvantageFragment {
    private RelativeLayout rl;
    private ArrayList<Product> productList;
    private ArrayList<Bill> billList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.fragment_barcode, container, false);


        if (savedInstanceState != null)
        {
            productList = savedInstanceState.getParcelableArrayList("productList");
            billList = savedInstanceState.getParcelableArrayList("billList");
        }
        else
        {
            productList = new ArrayList<Product>();
            billList = new ArrayList<Bill>();
            Bundle args = getArguments();
            if (args!=null)
            {
                if (args.containsKey("barcode_scan_result"))
                {
                    String barcode_scan_result = args.getString("barcode_scan_result");
                    GetItemForBarcodeTask getItemForBarcodeTask = new GetItemForBarcodeTask();
                    getItemForBarcodeTask.execute(barcode_scan_result);
                    SetLoadingAnimation(true);
                }
                else
                {
                    GetItemForBarcodeTask getItemForBarcodeTask = new GetItemForBarcodeTask();
                    getItemForBarcodeTask.execute("");
                    SetLoadingAnimation(true);
                }
            }
        }
        return rl;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelableArrayList("productList", (ArrayList<? extends Parcelable>) productList);
        outState.putParcelableArrayList("billList", (ArrayList<? extends Parcelable>) billList);

        super.onSaveInstanceState(outState);
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
        return "Reading Barcode";
    }


    //run
    private class GetItemForBarcodeTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            JSONObject jObject = new JSONObject();
            try {
                JSONObject jsonObj = GenericUtils.GetDefaultJSONObj();
                jsonObj.putOpt("barcode", params[0]);
                jObject.putOpt("data", jsonObj);
            } catch (JSONException e) {
                e.printStackTrace();
                LogManager.Error("GetItemForBarcodeTask", e);
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                LogManager.Error("GetItemForBarcodeTask", e);
                return null;
            }

            LogManager.Info(">>>> " + jObject.toString());
            return GenericUtils.HTTPRequest("GetItemForBarcode", jObject
                    .toString());
        }

        @Override
        protected void onPostExecute(String result) {
            LogManager.Info("GetItemForBarcodeTask - " + result);
            PopulateItemsForBarcode(result);
            LogManager.Info("End GetItemForBarcodeTask " + new Date());
        }
    }


    public void PopulateItemsForBarcode(String jSonData)
    {
        JSONObject jObj = null;
        if (jSonData == null) {
            //FillWithSampleDate();
            //if (accountDropDownAdapter != null) {
            //     accountDropDownAdapter.notifyDataSetChanged();
            // }
        } else {
            // try parse the string to a JSON object
            try {
                jObj = new JSONObject(jSonData);
                // Get the element that holds the items ( JSONArray )
                productList.clear();
                if (jObj.has("product")) {
                    JSONObject product = jObj.getJSONObject("product");
                    if (product != null) {
                        productList.add(new Product(product));
                    }
                }
                billList.clear();
                if (jObj.has("bill")) {
                    JSONObject bill = jObj.getJSONObject("bill");
                    if (bill != null) {
                        billList.add(new Bill(bill));
                    }
                }
            } catch (Exception exp) {
                LogManager.Error("PopulateList", exp);
            }
        }

        try
        {
            if (productList.size() > 0)
            {
                Bundle args = new Bundle();
                args.putParcelableArrayList("products", productList);
                ((MainActivity) BarcodeFragment.this.getActivity()).NavigateTo(PurchaseFragment.class.getName(), false, args);
            }
            else if (billList.size() > 0)
            {
                Bundle args = new Bundle();
                args.putParcelableArrayList("bills", billList);
                ((MainActivity) BarcodeFragment.this.getActivity()).NavigateTo(PayBillFragment.class.getName(), false, args);
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(BarcodeFragment.this.getActivity(), R.style.AlertDialogCustom));
                builder.setMessage("Item Not found")
                        .setIcon(R.drawable.ic_launcher)
                        .setCancelable(false)
                        .setMessage("Item not found")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

                SetLoadingAnimation(false);
                ((MainActivity) BarcodeFragment.this.getActivity()).NavigateTo(AccountsFragment.class.getName(), false, null);
            }
        }
        catch (Exception e)
        {
            LogManager.Error(e);
        }
        SetLoadingAnimation(false);
    }
}

