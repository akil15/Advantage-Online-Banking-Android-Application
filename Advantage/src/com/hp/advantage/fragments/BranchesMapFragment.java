package com.hp.advantage.fragments;


import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.hp.advantage.R;
import com.hp.advantage.utils.GenericUtils;
import com.hp.advantage.utils.LogManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Random;

public class BranchesMapFragment extends AdvantageFragment implements LocationListener {


    private WebView webView;
    private Location mostRecentLocation;
    private LinearLayout ll;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ll = (LinearLayout) inflater.inflate(R.layout.fragment_branches, container, false);
        webView = (WebView) ll.findViewById(R.id.webview);
        getLocation();
        if (mostRecentLocation != null) {
            CenterMapOnLocation();
            //GetBranchesTask getBranchesTask = new GetBranchesTask();
            //getBranchesTask.execute(mostRecentLocation.getLatitude(), mostRecentLocation.getLongitude() );
        }
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(GenericUtils.ADVANTAGE_URL + "Web/map.html");
        return ll;
    }

    /**
     * Sets up the WebView object and loads the URL of the page *
     */
    private void CenterMapOnLocation() {

        final String centerURL = "javascript:centerAt(" +
                mostRecentLocation.getLatitude() + "," +
                mostRecentLocation.getLongitude() + ")";


        //Wait for the page to load then send the location information
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl(centerURL);
            }
        });
    }

    /**
     * The Location Manager manages location providers. This code searches
     * for the best provider of data (GPS, WiFi/cell phone tower lookup,
     * some other mechanism) and finds the last known location.
     */
    private void getLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = locationManager.getBestProvider(criteria, true);

        //In order to make sure the device is getting location, request updates.        locationManager.requestLocationUpdates(provider, 1, 0, this);
        if (provider!=null)
        {
            mostRecentLocation = locationManager.getLastKnownLocation(provider);
        }
    }

    /**
     * Sets the mostRecentLocation object to the current location of the device *
     */
    @Override
    public void onLocationChanged(Location location) {
        mostRecentLocation = location;
    }

    /**
     * The following methods are only necessary because WebMapActivity implements LocationListener *
     */
    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public String GetTitle() {
        return "Branches";
    }

    //run
    private class GetBranchesTask extends AsyncTask<Double, Void, String> {
        @Override
        protected String doInBackground(Double... params) {
            JSONObject jObject = new JSONObject();
            try {
                JSONObject jsonObj = GenericUtils.GetDefaultJSONObj();
                //jsonObj.putOpt("lat", params[0]);
                //jsonObj.putOpt("lon", params[1]);
                jObject.putOpt("data", jsonObj);
            } catch (JSONException e) {
                e.printStackTrace();
                LogManager.Error("GetBranchesTask", e);
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                LogManager.Error("GetBranchesTask", e);
                return null;
            }

            LogManager.Info(">>>> " + jObject.toString());
            return GenericUtils.HTTPRequest("GetBranches", jObject
                    .toString());
        }

        @Override
        protected void onPostExecute(String result) {
            // Add randrom branches
            Random rnd = new Random();
            LogManager.Info("GetBranchesTask - " + result);
            for (int i = 0; i < 5; i++) {
                String placeMarker = "javascript:placeMarker(" + (mostRecentLocation.getLatitude() + rnd.nextDouble()) + "," + (mostRecentLocation.getLongitude() + rnd.nextDouble()) + ", 'Place' )";
                webView.loadUrl(placeMarker);
            }
            LogManager.Info("End GetBranchesTask " + new Date());
        }
    }
}