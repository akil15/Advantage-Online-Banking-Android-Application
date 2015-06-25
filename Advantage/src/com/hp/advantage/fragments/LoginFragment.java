package com.hp.advantage.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.hp.advantage.R;
import com.hp.advantage.activities.MainActivity;

public class LoginFragment extends AdvantageFragment {
    private RelativeLayout rl;
    private ImageButton BranchesButton;
    private ImageButton HelpButton;
    private Button LoginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getActivity().getActionBar().isShowing()) {
            getActivity().getActionBar().hide();
        }
        rl = (RelativeLayout) inflater.inflate(R.layout.fragment_login, container, false);
        BranchesButton = (ImageButton) rl.findViewById(R.id.branchesButton);
        HelpButton = (ImageButton) rl.findViewById(R.id.helpButton);
        LoginButton = (Button) rl.findViewById(R.id.loginButton);

        LoginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MainActivity) (LoginFragment.this.getActivity())).NavigateTo(AccountsFragment.class.getName(), false);
            }
        });

        BranchesButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MainActivity) (LoginFragment.this.getActivity())).NavigateTo(BranchesMapFragment.class.getName(), true);

            }
        });

        HelpButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MainActivity) (LoginFragment.this.getActivity())).NavigateTo(HelpFragment.class.getName(), true);

            }
        });

        return rl;
    }


    @Override
    public String GetTitle() {
        return "Login";
    }

    @Override
    public boolean ShowTitleBar() {
        return false;
    }
}
