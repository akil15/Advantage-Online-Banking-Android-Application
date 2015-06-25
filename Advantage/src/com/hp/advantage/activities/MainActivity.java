package com.hp.advantage.activities;

//Insert comments here
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import com.hp.advantage.R;
import com.hp.advantage.R.anim;
import com.hp.advantage.adapters.DrawerMenuListAdapter;
import com.hp.advantage.data.SideMenuItem;
import com.hp.advantage.fragments.AccountsFragment;
import com.hp.advantage.fragments.AdvantageFragment;
import com.hp.advantage.fragments.BranchesMapFragment;
import com.hp.advantage.fragments.BrokerageFragment;
import com.hp.advantage.fragments.FragmentFactory;
import com.hp.advantage.fragments.HelpFragment;
import com.hp.advantage.fragments.LoginFragment;
import com.hp.advantage.fragments.MoneyTransferFragment;
import com.hp.advantage.fragments.NFCFragment;
import com.hp.advantage.fragments.BarcodeFragment;
import com.hp.advantage.fragments.ScanCheckFragment;
import com.hp.advantage.fragments.SplashFragment;
import com.hp.advantage.utils.GenericUtils;
import com.hp.advantage.utils.NfcReaderActivity;
import com.hp.advantage.utils.IntentIntegrator;
import com.hp.advantage.utils.IntentResult;

import org.ndeftools.Message;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends NfcReaderActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private List<SideMenuItem> mMenuTitles;
    private String TopTitle;
    private AdvantageFragment mCurrentFragment;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);

        TopTitle = "";
        // Initialize the singleton
        GenericUtils.Instance(this);
        if (savedInstanceState == null) {
            getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
            getActionBar().hide();
        } else {
            mCurrentFragment = savedInstanceState.getParcelable("currentFragment");
        }

        setContentView(R.layout.activity_main);

        InitializeDrawer();

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setIcon(R.drawable.ic_headerlogo);

        //FragmentManager fm = getFragmentManager();
        //AdvantageFragment mCurrentFragment = (AdvantageFragment) fm.findFragmentByTag(mCurrentFragmentTag);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        //if (mCurrentFragment == null) 
        //{
        //	NavigateTo(SplashFragment.class.getName(), false);
        //}

        HandleNFC();


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("currentFragment", mCurrentFragment);
        super.onSaveInstanceState(outState);
    }


    private void HandleNFC() {
        // lets start detecting NDEF message using foreground mode
        setDetecting(true);
        /*
        PackageManager pm = getPackageManager();
    	if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) 
        {
        	return;
        }
    	
    	mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
	     if (mNfcAdapter == null) 
	     {
	         return;
	     }

        handle(get());
        */

    }

    @SuppressLint("NewApi")
    private void InitializeDrawer() {
        PackageManager pm = getPackageManager();
        mMenuTitles = new ArrayList<SideMenuItem>();
        mMenuTitles.add(new SideMenuItem("Accounts", AccountsFragment.class.getName(), GenericUtils.ADVANTAGE_URL+"/Images/accounts.png"));
        mMenuTitles.add(new SideMenuItem("Money transfer", MoneyTransferFragment.class.getName(), GenericUtils.ADVANTAGE_URL+"/Images/moneytransfer.png"));
        mMenuTitles.add(new SideMenuItem("Brokerage", BrokerageFragment.class.getName(), GenericUtils.ADVANTAGE_URL+"/Images/brokerage.png"));

        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) && pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
            mMenuTitles.add(new SideMenuItem("Pay Bills", "BarcodeScan", GenericUtils.ADVANTAGE_URL+"/Images/bills.png"));
            mMenuTitles.add(new SideMenuItem("Check Deposit", ScanCheckFragment.class.getName(), GenericUtils.ADVANTAGE_URL+"/Images/checkdeposit.png"));
        }
        /*
        if (pm.hasSystemFeature(PackageManager.FEATURE_NFC)) 
        {
        	mMenuTitles.add(new SideMenuItem("NFC", NFCFragment.class.getName(), "nfc.png"));
        }
        */
        mMenuTitles.add(new SideMenuItem("Help", HelpFragment.class.getName(), GenericUtils.ADVANTAGE_URL+"/Images/help.png"));
        mMenuTitles.add(new SideMenuItem("Branches", BranchesMapFragment.class.getName(), GenericUtils.ADVANTAGE_URL+"/Images/branches.png"));
        mMenuTitles.add(new SideMenuItem("Logout", LoginFragment.class.getName(), GenericUtils.ADVANTAGE_URL+"/Images/logout.png"));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new DrawerMenuListAdapter(this, mMenuTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                setActionBarArrowDependingOnFragmentsBackStack();
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                setActionBarArrowDependingOnFragmentsBackStack();
            }
        });
    }

    private void setActionBarArrowDependingOnFragmentsBackStack() {
        int backStackEntryCount = getFragmentManager().getBackStackEntryCount();
        boolean shouldEnableDrawerIndicator = backStackEntryCount == 0;
        mDrawerToggle.setDrawerIndicatorEnabled(shouldEnableDrawerIndicator);
        mDrawerLayout.setDrawerLockMode(shouldEnableDrawerIndicator ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle("Closing Advantage")
                    .setMessage("Are you sure you want to close Advantage?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            setTitle(TopTitle);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.isDrawerIndicatorEnabled() && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (item.getItemId() == android.R.id.home && getFragmentManager().popBackStackImmediate()) {
            return true;
        }
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_accounts:
                NavigateTo(AccountsFragment.class.getName(), false);
                return true;
            case R.id.menu_money_transfer:
                NavigateTo(MoneyTransferFragment.class.getName(), false);
                return true;
            case R.id.menu_brokerage:
                NavigateTo(BrokerageFragment.class.getName(), false);
                return true;
            case R.id.menu_pay_bills:
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.initiateScan();
                return true;
            case R.id.menu_checkdeposit:
                NavigateTo(ScanCheckFragment.class.getName(), false);
                return true;
            case R.id.menu_help:
                NavigateTo(HelpFragment.class.getName(), false);
                return true;
            case R.id.menu_branches:
                NavigateTo(BranchesMapFragment.class.getName(), false);
                return true;
            case R.id.menu_logout:
                Logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void Logout() {
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setIcon(R.drawable.ic_launcher)
                .setTitle("Logout from Advantage")
                .setMessage("Are you sure you want to log out from Advantage?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavigateTo(LoginFragment.class.getName(), false);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void NavigateTo(String fragmentName, boolean AddToBackStack) {
        NavigateTo(fragmentName, AddToBackStack, null);
    }

    @SuppressLint("NewApi")
    public void NavigateTo(String fragmentName, boolean AddToBackStack, Bundle params) {
        // update the main content by replacing fragments
        mCurrentFragment = FragmentFactory.Create(this, fragmentName);
        mCurrentFragment.setArguments(params);
        FragmentManager fragmentManager = getFragmentManager();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            if (AddToBackStack) {
                fragmentManager.beginTransaction().setCustomAnimations(anim.slide_in_right, anim.slide_out_left, anim.slide_in_left, anim.slide_out_right).replace(R.id.content_frame, mCurrentFragment, mCurrentFragment.getFragmentID()).addToBackStack(mCurrentFragment.GetTitle()).commitAllowingStateLoss();
            } else {
                TopTitle = mCurrentFragment.GetTitle();
                fragmentManager.beginTransaction().setCustomAnimations(anim.slide_in_right, anim.slide_out_left, anim.slide_in_left, anim.slide_out_right).replace(R.id.content_frame, mCurrentFragment, mCurrentFragment.getFragmentID()).commitAllowingStateLoss();
            }
        } else {
            if (AddToBackStack) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, mCurrentFragment, mCurrentFragment.getFragmentID()).addToBackStack(mCurrentFragment.GetTitle()).commitAllowingStateLoss();

            } else {
                TopTitle = mCurrentFragment.GetTitle();
                fragmentManager.beginTransaction().replace(R.id.content_frame, mCurrentFragment, mCurrentFragment.getFragmentID()).commitAllowingStateLoss();
            }
        }
        setTitle(mCurrentFragment.GetTitle());
        if (mCurrentFragment.ShowTitleBar())
            getActionBar().show();
        else
            getActionBar().hide();
    }

    @Override
    protected void readNdefMessage(Message message)
    {
        Bundle args = new Bundle();
        args.putSerializable("nfc_message", message);

        NavigateTo(NFCFragment.class.getName(), false, args);
    }

    @Override
    protected void readEmptyNdefMessage()
    {
        NavigateTo(NFCFragment.class.getName(), false, null);
    }

    @Override
    protected void readNonNdefMessage() {

        NavigateTo(NFCFragment.class.getName(), false, null);
    }

    @Override
    protected void onNfcStateEnabled() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onNfcStateDisabled() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onNfcStateChange(boolean enabled) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onNfcFeatureNotFound() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onTagLost() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void nonNfcIntentDetected(Intent intent, String action) {
        if (Intent.ACTION_MAIN.equals(action)) {
            if (mCurrentFragment == null) {
                NavigateTo(SplashFragment.class.getName(), false);
            }
        }

    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mDrawerLayout.closeDrawer(mDrawerList);
            if (mMenuTitles.get(position).FragmentName.equals(LoginFragment.class.getName())) {
                Logout();
            }
            else if (mMenuTitles.get(position).FragmentName.equals("BarcodeScan"))
            {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.initiateScan();
            }
            else
            {
                NavigateTo(mMenuTitles.get(position).FragmentName, false);
                // Highlight the selected item, update the title, and close the drawer
                mDrawerList.setItemChecked(position, true);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null)
        {
                Bundle args = new Bundle();
                args.putString("barcode_scan_result", scanResult.getContents());
                args.putString("barcode_scan_result_format", scanResult.getFormatName());
                NavigateTo(BarcodeFragment.class.getName(), false, args);
        }
    }
}