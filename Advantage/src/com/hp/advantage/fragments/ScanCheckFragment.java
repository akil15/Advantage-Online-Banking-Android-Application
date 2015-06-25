package com.hp.advantage.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.hp.advantage.R;
import com.hp.advantage.utils.GenericUtils;
import com.hp.advantage.utils.ShowCamera;

public class ScanCheckFragment extends AdvantageFragment {

    //private Camera cameraObject;
    private ShowCamera showCamera;
    private RelativeLayout rl;
    private Button ScanButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rl = (RelativeLayout) inflater.inflate(R.layout.fragment_scan_check, container, false);
        showCamera = new ShowCamera(this.getActivity());
        FrameLayout preview = (FrameLayout) rl.findViewById(R.id.camera_preview);
        preview.addView(showCamera);

        ScanButton = (Button) rl.findViewById(R.id.scanButton);
        ScanButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //showCamera.takePicture();
                GenericUtils.Crash("Scan check crash");
            }
        });

        return rl;
    }

    @Override
    public String GetTitle() {
        return "Scan check";
    }
}