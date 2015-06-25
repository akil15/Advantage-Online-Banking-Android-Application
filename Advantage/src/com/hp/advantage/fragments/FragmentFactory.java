package com.hp.advantage.fragments;

import android.app.Activity;
import android.app.Fragment;

public class FragmentFactory {

    public static Fragment Create(Activity activity, int id) {
        Fragment retFragment = null;
        switch (id) {
            case 0:
                retFragment = new AccountsFragment();
                break;
            case 1:
                retFragment = new BranchesMapFragment();
                break;
            default:
                retFragment = new AccountsFragment();
                break;
        }
        return retFragment;
    }

    public static AdvantageFragment Create(Activity activity, String fragmentName) {
        AdvantageFragment retFragment = null;
        if (fragmentName == SplashFragment.class.getName()) {
            retFragment = new SplashFragment();
        }
        if (fragmentName == BranchesMapFragment.class.getName()) {
            retFragment = new BranchesMapFragment();
        }
        if (fragmentName == AccountsFragment.class.getName()) {
            retFragment = new AccountsFragment();
        }
        if (fragmentName == HelpFragment.class.getName()) {
            retFragment = new HelpFragment();
        }
        if (fragmentName == LoginFragment.class.getName()) {
            retFragment = new LoginFragment();
        }
        if (fragmentName == TransactionsFragment.class.getName()) {
            retFragment = new TransactionsFragment();
        }
        if (fragmentName == MoneyTransferFragment.class.getName()) {
            retFragment = new MoneyTransferFragment();
        }
        if (fragmentName == ScanCheckFragment.class.getName()) {
            retFragment = new ScanCheckFragment();
        }
        if (fragmentName == BrokerageFragment.class.getName()) {
            retFragment = new BrokerageFragment();
        }
        if (fragmentName == NFCFragment.class.getName()) {
            retFragment = new NFCFragment();
        }
        if (fragmentName == BarcodeFragment.class.getName()) {
            retFragment = new BarcodeFragment();
        }
        if (fragmentName == PurchaseFragment.class.getName()) {
            retFragment = new PurchaseFragment();
        }
        if (fragmentName == PayBillFragment.class.getName()) {
            retFragment = new PayBillFragment();
        }
        return retFragment;
    }
}
