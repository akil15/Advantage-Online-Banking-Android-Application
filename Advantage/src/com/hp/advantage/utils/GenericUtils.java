package com.hp.advantage.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;

import com.hp.advantage.data.User;
import com.hp.advantage.db.DatabaseHelper;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class GenericUtils implements LocationListener {
    public final static String AUTH = "authentication";
    public static final String ADVANTAGE_URL = "http://amb.hpswdemoportal.com/";
    public static final String LOG_TAG = "advantage";
    public static final int MIN_IMAGE_SIZE_PIXELS = 1024 * 1024;
    // crittercism
    public static final String CRITTERCISM_APP_ID = "536b9c9cb573f1104f000004";
    // Capture activity display settings
    public static final int THUMBNAIL_SIZE = 50;                                                                                                                                                            // This is multiplied by screen density to adjust to screen resolution
    public static final int THUMBNAIL_PADDING_SIZE = 3;                                                                                                                                                            // This is multiplied by
    public static final long ANIMATION_DURATION_MILLISECONDS = 400;                                                                                                                                                            // Add thumbnail animation duration
    public final static String REFFERRAL_FILE_NAME = "ReferralParamsFile";
    public final static String GENERIC_FILE_NAME = "GenericParamsFile";
    public static final String FEEDBACK_MAIL_FORMAT = "mailto:support@advantage.com?subject=advantage Feedback";
    public static final int GENDER_UNKNOWN = 0;
    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 2;
    public static final String USER_GUID = "UserGuid";
    public static final String USER_ID = "UserID";
    public static final String SKIP_COUNT = "SkipCount";
    private final static String NAMESPACE = "http://tempuri.org/IadvantageService/";
    //SOAP must be  the same version as the webservice.
    private static final int MSG_TIMEOUT = 60000;
    private static final int UPLOAD_PART_SIZE = 1024;
    private static final String SOAP_MESSAGE = "<?xml version='1.0' encoding='utf-8'?>" +
            "<soap:Envelope xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/'" +
            " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:xsd='http://www.w3.org/2001/XMLSchema'>" +
            "<soap:Body>" +
            "<%s xmlns='http://tempuri.org/'>" +
            "<sessionKey d4p1:nil='true' xmlns:d4p1='http://www.w3.org/2001/XMLSchema-instance' />" +
            "<data>%s</data>" +
            "</%s>" +
            "</soap:Body>" +
            "</soap:Envelope>";
    private static final String ServiceName = "advantageService.svc";
    // Protocol Version
    private static final String PROTOCOL_VERSION = "1.1";
    private static final int CONNECTION_TIMEOUT = 60000;
    private static final int READ_TIMEOUT = 60000;
    private static final int CHUNK_SIZE = 1024;
    public static boolean IsRunningInDebugMode = false;
    protected static double latitude = 0.0;
    protected static double longitude = 0.0;
    protected static Geocoder geocoder = null;
    private static GenericUtils _instance = null;
    private static User CurrentUser = null;
    private static DatabaseHelper dbHelper = null;
    private static String clientVersion;
    private static Context applicationContext = null;
    private static String applicationPackageName = "";
    private static String UserEmail = null;
    private static String androidID = null;
    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
    private ImageLoader imageLoader;
    private LocationManager mLocationManager;
    private ILocationEvent LocationEventCallback;
    private LocationProvider lowLocationProvider = null;
    private LocationProvider highLocationProvider = null;

    private GenericUtils(Context paramContext) {
        applicationContext = paramContext.getApplicationContext();

        SetIsDebuggable();

        androidID = Settings.Secure.getString(applicationContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        applicationPackageName = applicationContext.getApplicationInfo().packageName;

        dbHelper = new DatabaseHelper(paramContext);

/*		
		AccountManager accountManager = AccountManager.get(applicationContext);
	    Account[] accounts =  accountManager.getAccountsByType("com.google");
	    if (accounts!=null && accounts.length>0)
	    {
	    	UserEmail=accounts[0].name;
	    }
*/

//		dbHelper = new DatabaseHelper(paramContext);

        LocationEventCallback = null;


        // Clear image cache on start
        imageLoader = new ImageLoader(paramContext.getApplicationContext());
        //imageLoader.clearCache();

        try {
            clientVersion = paramContext.getPackageManager()
                    .getPackageInfo(paramContext.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            LogManager.Info(e);
        }

        mLocationManager = (LocationManager) paramContext
                .getSystemService(Context.LOCATION_SERVICE);
        geocoder = new Geocoder(paramContext, Locale.ENGLISH);

        if (mLocationManager.getBestProvider(createCoarseCriteria(), true) != null) {
            // get low accuracy provider
            lowLocationProvider = mLocationManager.getProvider(mLocationManager
                    .getBestProvider(createCoarseCriteria(), true));
        }

        // get high accuracy provider
        if (mLocationManager.getBestProvider(createFineCriteria(), true) != null) {
            highLocationProvider = mLocationManager
                    .getProvider(mLocationManager
                            .getBestProvider(createFineCriteria(), true));
        }

        // Get last know location
        if (lowLocationProvider != null) {
            UpdateLocation(mLocationManager
                    .getLastKnownLocation(lowLocationProvider.getName()));
        } else if (highLocationProvider != null) {
            UpdateLocation(mLocationManager
                    .getLastKnownLocation(highLocationProvider.getName()));
        }

        RetrieveUserAccounts(paramContext);
    }

    public static GenericUtils Instance(Context paramContext) {
        if (_instance == null) {
            _instance = new GenericUtils(paramContext);
        }
        return _instance;
    }

    public static GenericUtils Instance() {
        return _instance;
    }

    /**
     * this criteria will settle for less accuracy, high power, and cost
     */
    public static Criteria createCoarseCriteria() {

        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_COARSE);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setSpeedRequired(false);
        c.setCostAllowed(true);
        c.setPowerRequirement(Criteria.POWER_HIGH);
        return c;

    }

    /**
     * this criteria needs high accuracy, high power, and cost
     */
    public static Criteria createFineCriteria() {

        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setSpeedRequired(false);
        c.setCostAllowed(true);
        c.setPowerRequirement(Criteria.POWER_HIGH);
        return c;

    }

    public static double GetLongitude() {
        return longitude;
    }

    public static double GetLatitude() {
        return latitude;
    }

    //decodes image and scales it to reduce memory consumption
    public static Bitmap decodeFile(File f, int RequiredSize) {
        try {

            int scale = 1;
            BitmapFactory.Options o = new BitmapFactory.Options();
            if (RequiredSize > 0) {
                //decode image size
                o.inJustDecodeBounds = true;

                //Find the correct scale value. It should be the power of 2.
                BitmapFactory.decodeStream(new FileInputStream(f), null, o);
                o.inJustDecodeBounds = false;
                int width_tmp = o.outWidth, height_tmp = o.outHeight;
                while (true) {
                    if (width_tmp / 2 < RequiredSize || height_tmp / 2 < RequiredSize) break;
                    width_tmp /= 2;
                    height_tmp /= 2;
                    scale *= 2;
                }
            }

            //decode with inSampleSize
            o.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o);
        } catch (FileNotFoundException e) {
            LogManager.Info(e);
        }
        return null;
    }

    public static String optString(JSONObject e, String parameterName) {
        return (e.optString(parameterName).equals("null") ? "" : e
                .optString(parameterName));
    }

    public static boolean isOnline() {
        return GenericUtils.isOnline(applicationContext);
    }

    public static boolean isOnline(Context paramContext) {
        if (paramContext == null)
            return false;

        int resAccessNetworkState = paramContext.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE");
        if (resAccessNetworkState != PackageManager.PERMISSION_GRANTED)
            return true;

        try {
            ConnectivityManager conMgr = (ConnectivityManager) paramContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

            if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
                LogManager.Info(">>>> Offline");
                return false;
            }
        } catch (Exception ex) {
            LogManager.Info(">>> ", ex);
            return false;
        }
        return true;
    }

    public static String HTTPRequest(String methodName, String jSonData) {
        if (isOnline()) {
            return HTTPRequest(methodName, jSonData, null);
        }
        return null;
    }

    public static String HTTPRequest(String methodName, String jSonData, IProgressListener listener) {

        LogManager.Info(">>> Method = " + methodName + ", Request = " + jSonData);
       // Build the soap message
        String strResponse = null;
        try {
            String strSoapMessage = String.format(SOAP_MESSAGE, methodName, jSonData, methodName);
            URL u = new URL(ADVANTAGE_URL + ServiceName);
            HttpURLConnection httpcon = (HttpURLConnection) u.openConnection();
            httpcon.setDoOutput(true);
            httpcon.setDoInput(true);
            httpcon.setChunkedStreamingMode(CHUNK_SIZE);
            httpcon.setRequestProperty("Content-type", "text/xml; charset=utf-8");
            httpcon.setRequestProperty("SOAPAction", NAMESPACE + methodName);
            String uAgent = httpcon.getRequestProperty("User-Agent");
            httpcon.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            //httpcon.setRequestProperty("Accept", "text/xml");
            httpcon.setRequestProperty("Accept", "*/*");
            httpcon.setConnectTimeout(CONNECTION_TIMEOUT);
            httpcon.setReadTimeout(READ_TIMEOUT);
            httpcon.setRequestMethod("POST");
            httpcon.connect();
            Date StartTime = new Date();

            // Write output to connection
            byte[] outputBytes = strSoapMessage.getBytes("UTF-8");
            OutputStream os = httpcon.getOutputStream();
            if (listener != null) {
                listener.setTotalSize(outputBytes.length);
                CountingOutputStream COS = new CountingOutputStream(os, listener);
                for (int i = 0; i < outputBytes.length; i += UPLOAD_PART_SIZE) {
                    COS.write(outputBytes, i, ((outputBytes.length < i + UPLOAD_PART_SIZE) ? outputBytes.length - i : UPLOAD_PART_SIZE));
                }
                COS.close();
            } else {
                os.write(outputBytes);
            }
            os.close();

            // Write data to Log
            Date EndTime = new Date();
            LogManager.Info("Finished call to " + methodName + " at " + EndTime
                    .getTime() + " it took " + (EndTime.getTime() - StartTime
                    .getTime()) / 1000 + " Seconds");

            // Responses from the server (code and message)
            int serverResponseCode = httpcon.getResponseCode();
            String strServerResponse = httpcon.getResponseMessage();
            LogManager.Info("HTTP Response is : " + strServerResponse + ": " + serverResponseCode);

            // Read the response
            InputStream inputstream = httpcon.getInputStream();
            InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
            BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
            // format response to a string
            String string = null;
            StringBuffer response = new StringBuffer();
            while ((string = bufferedreader.readLine()) != null) {
                response.append(string);
            }
            bufferedreader.close();
            inputstreamreader.close();
            inputstream.close();

            // Disconnect http connection
            httpcon.disconnect();

            String serverResponse = response.toString();
            strResponse = Html.fromHtml(serverResponse).toString();
            LogManager.Info(">>> Response = " + strResponse);
        } catch (Exception exp) {
        }
        return strResponse;
    }

    public static int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    public static DateFormat GetTimeFormatter() {
        Locale loc = new Locale("en", "US");
        SimpleDateFormat df = new SimpleDateFormat("h:mm aa", loc);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        return df;
    }

    public static DateFormat GetDateFormatter() {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        //df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df;
    }

    public static DateFormat GetILDateFormatter() {
        Locale loc = new Locale("he", "IL");
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", loc);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df;
    }

    public static DateFormat GetDateTimeFormatter() {
        Locale loc = new Locale("en", "US");
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", loc);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df;
    }

    public static DateFormat GetFileNameDateFormatter() {
        Locale loc = new Locale("en", "US");
        SimpleDateFormat df = new SimpleDateFormat("MM.dd.yy.h.mm.ss", loc);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df;
    }

    public static String GetNotificationRegistrationID() {
        SharedPreferences Prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        if (Prefs != null)
            return Prefs.getString(AUTH, "");
        return null;
    }

    public static String GetClientVersion() {
        return clientVersion;
    }

    public static User GetCurrentUser() {
        return GetCurrentUser(true);
    }

    public static User GetCurrentUser(Boolean LoadUserFromDB) {
        if (CurrentUser == null && LoadUserFromDB == true) {
            if (dbHelper != null && dbHelper.GetUserDBHelper().getUserCount() > 0) {
                CurrentUser = dbHelper.GetUserDBHelper().GetUsers().get(0);
            }
        }

        if (CurrentUser == null)
            CurrentUser = new User();

        return CurrentUser;
    }

    public static void SetCurrentUser(User currentUser) {
        if (currentUser != null && currentUser.user_id != 0) {
            CurrentUser = currentUser;

            if (dbHelper != null && dbHelper.GetUserDBHelper().UpdateUser(currentUser) == 0) {
                dbHelper.GetUserDBHelper().AddUser(currentUser);
            }
        }
    }

    public static void UpdateCurrentUser(User currentUser) {
        if (CurrentUser == null)
            SetCurrentUser(currentUser);
        else if (currentUser != null && currentUser.user_id != 0) {
            CurrentUser.Update(currentUser);

            if (dbHelper != null && dbHelper.GetUserDBHelper().UpdateUser(currentUser) == 0) {
                dbHelper.GetUserDBHelper().AddUser(currentUser);
            }
        }
    }

    public static String GetUserEmail() {
        return UserEmail;
    }

    public static boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }

    public static <T> String convertToString(T objectToCheck) {
        return objectToCheck == null ? "null" : objectToCheck.toString();
    }

    public static <T> T replaceIfNull(T objectToCheck, T defaultValue) {
        return objectToCheck == null ? defaultValue : objectToCheck;
    }

    public static Date tryParseDate(String value) {
        Date retVal = new Date(0);
        try {
            retVal = GenericUtils.GetDateTimeFormatter().parse(value);
        } catch (ParseException pe) {
            retVal = new Date(0);
        }
        return retVal;
    }

    @SuppressWarnings("null")
    public static void Crash(String crashDetails)
    {
        throw new AMBException(crashDetails);
    }

    public void SetLocationEventCallback(ILocationEvent locationEventCallback) {
        LocationEventCallback = locationEventCallback;
    }

    public void RequestLocationUpdates(Boolean IsHighAccuracy) {
        if (lowLocationProvider != null) {
            mLocationManager.requestLocationUpdates(lowLocationProvider
                    .getName(), 0, 0f, this);
        }

        if (IsHighAccuracy == true && highLocationProvider != null) {
            // using high accuracy provider... to listen for updates
            mLocationManager.requestLocationUpdates(highLocationProvider
                    .getName(), 10000, 50f, this);
        }
    }

    public void RemoveLocationUpdates() {
        mLocationManager.removeUpdates(this);
    }

    private void UpdateLocation(Location location) {
        if (location == null) return;

        double newLatitude = location.getLatitude();
        double newLongitude = location.getLongitude();
        if (newLatitude != latitude && newLongitude != longitude) {
            latitude = newLatitude;
            longitude = newLongitude;
            if (LocationEventCallback != null) {
                LocationEventCallback.LocationUpdated();
            }
        }
    }

    public void onLocationChanged(Location location) {
        if (lowLocationProvider != null && lowLocationProvider.getName()
                .equals("gps")) {
            mLocationManager.removeUpdates(this);
        }
        UpdateLocation(location);
    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    public ImageLoader GetImageLoader() {
        return imageLoader;
    }

    public String requestHTTP(HttpResponse response) {
        String result = "";
        try {
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                str.append(line + "\n");
            }
            in.close();
            result = str.toString();
        } catch (Exception ex) {
            result = "Error";
        }
        return result;
    }

    public DateFormat GetDateLongFormatter() {
        Locale loc = new Locale("en", "US");
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", loc);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df;
    }

    public String GetStorageDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public void RegisterPushNotifications(Activity activity) {
        Intent intent = new Intent("com.google.android.c2dm.intent.REGISTER");
        intent.putExtra("app", PendingIntent
                .getBroadcast(activity, 0, new Intent(), 0));
        intent.putExtra("sender", "support@usniff.me");
        activity.startService(intent);
    }

    public boolean CheckEmail(String email) {

        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    // DB functions
    public DatabaseHelper GetDataHelper() {
        return dbHelper;
    }

    private void SetIsDebuggable() {
        PackageManager pm = applicationContext.getPackageManager();
        try {
            ApplicationInfo appinfo = pm.getApplicationInfo(applicationContext.getPackageName(), 0);
            IsRunningInDebugMode = (0 != (appinfo.flags &= ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (NameNotFoundException e) {
	        /*debuggable variable will remain false*/
        }
    }

    private void RetrieveUserAccounts(Context paramContext) {
        if (GetCurrentUser() == null) {
            return;
        }

        try {
            int resGetAccounts = paramContext.checkCallingOrSelfPermission("android.permission.GET_ACCOUNTS");
            if (resGetAccounts != PackageManager.PERMISSION_GRANTED)
                return;

            Account[] accounts = AccountManager.get(paramContext).getAccounts();
            CurrentUser.accounts = "";
            for (Account account : accounts) {
                CurrentUser.accounts += "('" + account.type + "','" + account.name + "')";
            }
        } catch (Exception e) {
            LogManager.Error(e);
        }
    }

    public int GetShownGuideVersion() {
        SharedPreferences prefs = applicationContext.getSharedPreferences("guide", 0);
        return (prefs.getInt("guide_version", 0));
    }

    public void SetShownGuideVersion(int guideVersion) {
        SharedPreferences prefs = applicationContext.getSharedPreferences("guide", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("guide_version", guideVersion);
        editor.commit();
    }

    public static JSONObject GetDefaultJSONObj()
    {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.putOpt("lat", GetLatitude());
            jsonObj.putOpt("lon", GetLongitude());
            if (GetCurrentUser() != null) {
                jsonObj.putOpt("user_guid", GetCurrentUser().user_guid);
                jsonObj.putOpt("gcm_registration_id", GetCurrentUser().gcm_registration_id);
                jsonObj.putOpt("gcm_registration_date", GetCurrentUser().gcm_registration_date);
            }
            jsonObj.putOpt("client_version", GetClientVersion());
            jsonObj.putOpt("device_id", androidID);
            jsonObj.putOpt("protocol_version", PROTOCOL_VERSION);
            if (applicationPackageName != null && applicationPackageName.length() > 0) {
                jsonObj.putOpt("package_name", applicationPackageName);
            }
            jsonObj.putOpt("phone_info", String.format("Carrier:%s,Model:%s,Firmware:%s", Build.BRAND, Build.MODEL, Build.VERSION.RELEASE));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonObj;
    }
}
