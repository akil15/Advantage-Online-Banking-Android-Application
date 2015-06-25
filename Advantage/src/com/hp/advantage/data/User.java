package com.hp.advantage.data;

import com.hp.advantage.utils.GenericUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class User {

    public int user_id;
    public String first_name;
    public String last_name;
    public String email;
    public int gender;
    public Date create_date;
    public Date last_login;
    public String phone_unique_id;
    public String facebook_id;
    public String facebook_user_name;
    public Date birthday;
    public String zipcode;
    public String phone;
    public String city_id;
    public String state_id;
    public String country_id;
    public String user_guid;
    public long facebook_expires_in;
    public String facebook_access_token;
    public String gcm_registration_id;
    public Date gcm_registration_date;
    public String plus_account_name;
    public int can_place_bids;
    public String accounts;

    public User() {
    }

    public User(JSONObject usr) {
        user_id = usr.optInt("user_id", 0);
        first_name = usr.optString("first_name", "");
        last_name = usr.optString("last_name", "");
        email = usr.optString("email", "");
        gender = usr.optInt("gender", 0);
        create_date = GenericUtils.tryParseDate(usr.optString("create_date"));
        last_login = GenericUtils.tryParseDate(usr.optString("last_login"));
        phone_unique_id = usr.optString("phone_unique_id", "");
        facebook_id = usr.optString("facebook_id", "");
        facebook_user_name = usr.optString("facebook_user_name", "");
        birthday = GenericUtils.tryParseDate(usr.optString("birthday"));

        zipcode = usr.optString("zipcode", "");
        phone = usr.optString("phone", "");
        city_id = usr.optString("city_id", "");
        state_id = usr.optString("state_id", "");
        country_id = usr.optString("country_id", "");
        user_guid = usr.optString("user_guid", "");
        facebook_expires_in = usr.optLong("facebook_expires_in", 0);
        facebook_access_token = usr.optString("facebook_access_token", "");
        gcm_registration_id = usr.optString("gcm_registration_id", "");
        gcm_registration_date = GenericUtils.tryParseDate(usr.optString("gcm_registration_date"));
        plus_account_name = usr.optString("plus_account_name", "");
        can_place_bids = usr.optInt("can_place_bids", 0);
        accounts = "";
    }

    public void Update(User currentUser) {
        user_id = currentUser.user_id;
        first_name = (currentUser.first_name.length() == 0) ? first_name : currentUser.first_name;
        last_name = (currentUser.last_name.length() == 0) ? last_name : currentUser.last_name;
        email = (currentUser.email.length() == 0) ? email : currentUser.email;
        gender = (currentUser.gender == 0) ? gender : currentUser.gender;
        phone_unique_id = (currentUser.phone_unique_id.length() == 0) ? phone_unique_id : currentUser.phone_unique_id;
        facebook_id = (currentUser.facebook_id.length() == 0) ? facebook_id : currentUser.facebook_id;
        facebook_user_name = (currentUser.facebook_user_name.length() == 0) ? facebook_user_name : currentUser.facebook_user_name;
        zipcode = (currentUser.zipcode.length() == 0) ? zipcode : currentUser.zipcode;
        phone = (currentUser.phone.length() == 0) ? phone : currentUser.phone;
        city_id = (currentUser.city_id.length() == 0) ? city_id : currentUser.city_id;
        state_id = (currentUser.state_id.length() == 0) ? state_id : currentUser.state_id;
        country_id = (currentUser.country_id.length() == 0) ? country_id : currentUser.country_id;
        user_guid = (currentUser.user_guid.length() == 0) ? user_guid : currentUser.user_guid;

        gcm_registration_id = (currentUser.gcm_registration_id.length() == 0) ? gcm_registration_id : currentUser.gcm_registration_id;
        //gcm_registration_date
        plus_account_name = (currentUser.plus_account_name.length() == 0) ? plus_account_name : currentUser.plus_account_name;
        can_place_bids = currentUser.can_place_bids;
        accounts = currentUser.accounts;
    }

    public void SetJsonParameters(JSONObject jsonObj) {

        try {
            //jsonObj.putOpt("email", email);
           // jsonObj.putOpt("facebook_access_token", facebook_access_token);
           // jsonObj.putOpt("facebook_expires_in", facebook_expires_in);
           // jsonObj.putOpt("first_name", first_name);
           // jsonObj.putOpt("last_name", last_name);
           // jsonObj.putOpt("facebook_user_name", facebook_user_name);
           // jsonObj.putOpt("birthday", birthday);
           // jsonObj.putOpt("gender", gender);
           // jsonObj.putOpt("plus_account_name", plus_account_name);
            jsonObj.putOpt("accounts", accounts);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}