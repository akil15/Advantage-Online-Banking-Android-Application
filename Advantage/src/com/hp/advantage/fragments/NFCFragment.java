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
import com.hp.advantage.data.Product;
import com.hp.advantage.utils.GenericUtils;
import com.hp.advantage.utils.LogManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import org.ndeftools.Message;
import org.ndeftools.wellknown.TextRecord;


public class NFCFragment extends AdvantageFragment {
    private RelativeLayout rl;
    private ArrayList<Product> productList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.fragment_nfc, container, false);


        if (savedInstanceState != null)
        {
            productList = savedInstanceState.getParcelableArrayList("productList");
        }
        else
        {
            productList = new ArrayList<Product>();
            Bundle args = getArguments();
            if (args!=null && args.containsKey("nfc_message")) {
                Message nfc_message = (Message) (args.getSerializable("nfc_message"));

                if (nfc_message != null) {
                    GetProductsTask getProductsTask = new GetProductsTask();
                    String productId = ((TextRecord) (nfc_message.get(0))).getText();

                    getProductsTask.execute(productId);
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
        return "Reading NFC";
    }


    //run
    private class GetProductsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            JSONObject jObject = new JSONObject();
            try {
                JSONObject jsonObj = GenericUtils.GetDefaultJSONObj();
                jsonObj.putOpt("product_code", params[0]);
                jObject.putOpt("data", jsonObj);
            } catch (JSONException e) {
                e.printStackTrace();
                LogManager.Error("GetProductsTask", e);
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                LogManager.Error("GetProductsTask", e);
                return null;
            }

            LogManager.Info(">>>> " + jObject.toString());
            return GenericUtils.HTTPRequest("GetProduct", jObject
                    .toString());
        }

        @Override
        protected void onPostExecute(String result) {
            LogManager.Info("GetProductsTask - " + result);
            PopulateProducts(result);
            LogManager.Info("End GetProductsTask " + new Date());
        }
    }


    public void PopulateProducts(String jSonData)
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
                JSONObject e = jObj.getJSONObject("product");
                if (e != null) {
                    Product product = new Product(e);
                    productList.add(product);
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
                ((MainActivity) (NFCFragment.this.getActivity())).NavigateTo(PurchaseFragment.class.getName(), false, args);
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(NFCFragment.this.getActivity(), R.style.AlertDialogCustom));
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
                ((MainActivity) (NFCFragment.this.getActivity())).NavigateTo(AccountsFragment.class.getName(), false, null);
            }
        }
        catch (Exception e)
        {
            LogManager.Error(e);
        }
        SetLoadingAnimation(false);

    }
}

