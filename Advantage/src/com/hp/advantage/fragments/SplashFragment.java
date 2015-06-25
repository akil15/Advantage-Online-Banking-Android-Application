package com.hp.advantage.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hp.advantage.R;
import com.hp.advantage.activities.MainActivity;
import com.hp.advantage.data.User;
import com.hp.advantage.utils.GenericUtils;
import com.hp.advantage.utils.LogManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SplashFragment extends AdvantageFragment {

    private RelativeLayout rl;
    private User CurrentUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getActivity().getActionBar().isShowing()) {
            getActivity().getActionBar().hide();
        }
        rl = (RelativeLayout) inflater.inflate(R.layout.fragment_splash, container, false);

        ImageView imageViewSplashLogo = (ImageView) rl.findViewById(R.id.imageViewSplashLogo);
        imageViewSplashLogo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(SplashFragment.this.getActivity(), "Crashing, on Purpose", Toast.LENGTH_SHORT).show();
                CrashTask crashTask = new CrashTask();
                crashTask.execute();

            }
        });

        // Clear cache for now
        //GenericUtils.Instance(this).GetImageLoader().clearCache();

        CurrentUser = GenericUtils.GetCurrentUser();

        final RegisterUserTask registerUserTask = new RegisterUserTask();
        registerUserTask.execute();

        ImageView imageViewLoadingIcon = (ImageView) rl.findViewById(R.id.imageViewLoadingIcon);
        Animation rotate_continously = AnimationUtils.loadAnimation(rl.getContext(), R.anim.rotate_continously);
        imageViewLoadingIcon.startAnimation(rotate_continously);


        //setting timeout thread for async task
        Thread thread1 = new Thread() {
            public void run() {
                // Sleep for a minimum of 4 seconds
                try
                {
                    Thread.sleep(4000);
                }
                catch (InterruptedException e)
                {

                }

                // If the register task is not finished, give it a few more seconds to complete
                if (registerUserTask.getStatus()== AsyncTask.Status.RUNNING) {
                    try
                    {
                          registerUserTask.get(10, TimeUnit.SECONDS);  //set time to 10 seconds
                    } catch (Exception e) {
                        registerUserTask.cancel(true);
                    }
                }
                SplashFragment.this.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        ((MainActivity) (SplashFragment.this.getActivity())).NavigateTo(LoginFragment.class.getName(), false);
                    }
                });
            }
        };
        thread1.start();

        return rl;
    }

    protected void PopulateList(String jSonData) {
        JSONObject jObj = null;
        if (jSonData == null) {
            return;
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(jSonData);
            JSONObject QueryResult = jObj.getJSONObject("data").getJSONObject("user");

            if (QueryResult != null) {
                if (QueryResult != null) {
                    CurrentUser = new User(QueryResult);
                    GenericUtils.UpdateCurrentUser(CurrentUser);
                }
            }
        } catch (Exception exp) {
            LogManager.Error("PopulateList", exp);
        }
    }

    @Override
    public String GetTitle() {
        return "Splash";
    }

    @Override
    public boolean ShowTitleBar() {
        return false;
    }

    //run
    private class RegisterUserTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            JSONObject jObject = new JSONObject();
            try {
                JSONObject jsonObj = GenericUtils.GetDefaultJSONObj();

                jObject.putOpt("data", jsonObj);
            } catch (JSONException e) {
                e.printStackTrace();
                LogManager.Error("RegisterUserTask", e);
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                LogManager.Error("RegisterUserTask", e);
                return null;
            }

            LogManager.Info(">>>> " + jObject.toString());
            return GenericUtils.HTTPRequest("RegisterUser", jObject
                    .toString());
        }

        @Override
        protected void onPostExecute(String result) {
            LogManager.Info("RegisterUserTask - " + result);
            PopulateList(result);
            //((MainActivity)(SplashFragment.this.getActivity())).NavigateTo(LoginFragment.class.getName(), false);
            LogManager.Info("End RegisterUserTask " + new Date());
        }
    }

    //run
    private class CrashTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                // do nothing
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            LogManager.Info("CrashTask - " + result);
            GenericUtils.Crash("Splash Random Crash");

        }
    }
}
