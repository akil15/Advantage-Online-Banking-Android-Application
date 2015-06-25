package com.hp.advantage.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hp.advantage.R;

public class HelpFragment extends AdvantageFragment {
    private RelativeLayout rl;
    private Button CallButton;
    private Button EmailButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.fragment_help, container, false);

        try {
            TextView versionNumberTextView = (TextView) rl.findViewById(R.id.versionNumberTextView);
            versionNumberTextView.setText("Version: " + String.valueOf(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        CallButton = (Button) rl.findViewById(R.id.callButton);
        CallButton.setVisibility(isTelephonyEnabled()?View.VISIBLE:View.INVISIBLE);

        EmailButton = (Button) rl.findViewById(R.id.emailButton);
        CallButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isTelephonyEnabled()) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:800-474-6836"));
                    v.getContext().startActivity(callIntent);
                }
            }
        });

        EmailButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, "support@advantage-bank.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Bank support");
                intent.putExtra(Intent.EXTRA_TEXT, "Dear Banker,");

                v.getContext().startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });


        return rl;
    }

    private boolean isTelephonyEnabled()
    {
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY))
        {
            TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            return tm != null && (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_GSM || tm.getSimState() == TelephonyManager.SIM_STATE_READY);
        }
        return false;
    }

    @Override
    public String GetTitle() {
        return "Help";
    }
}
