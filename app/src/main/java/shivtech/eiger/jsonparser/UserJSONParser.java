package shivtech.eiger.jsonparser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import shivtech.eiger.models.User;
import shivtech.eiger.utils.Constants;

/**
 * Created by Sivanatarajan on 29-01-2017.
 */

public class UserJSONParser {
    String json;

    public UserJSONParser(String json) {
        this.json = json;
    }

    public ArrayList<User> parseUserJSON() {
        ArrayList<User> usersList = new ArrayList<User>();
        Log.e("user json", json);

        try {
            JSONArray jsonArray = new JSONArray(json);
            Log.e("user json length", jsonArray.length() + "");
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    Log.e("User json index", "" + i);
                    JSONObject userJSON = jsonArray.getJSONObject(i);
                    User user = new User();
                    //int empid=userJSON.getInt(Constants.empId);
                    String name = userJSON.getString(Constants.username);
                    String mobile = userJSON.getString(Constants.mobile);
                    String tcsEmail = userJSON.getString(Constants.tcsEmail);
                    String projEmail = userJSON.getString(Constants.projectEmail);
                    int empId = userJSON.getInt(Constants.empId);
                    String bloodGroup = userJSON.getString(Constants.bloodGroup);
                    String summary = userJSON.getString(Constants.summary);
                    String tools = userJSON.getString(Constants.tools);
                    String programming_langs = userJSON.getString(Constants.programming_langs);
                    String hobbies = userJSON.getString(Constants.hobbies);
                    String lastmodified = userJSON.getString(Constants.lastModified);
                    String userRole = userJSON.getString(Constants.userRole);
                    int tower = userJSON.getInt(Constants.tower);
                    String dob = userJSON.getString(Constants.dob);
                    if (dob != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date DOB = null;
                        try {
                            DOB = dateFormat.parse(dob);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.e("Date parse exp", e.toString());
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                        dob = sdf.format(DOB);
                    }
                    Log.e("user details", name + " " + mobile + " " + tcsEmail + " " + projEmail + " " + empId + " " + dob);
                    //user.setUserId(userid);
                    user.setUserName(name);
                    user.setHobbies(hobbies);
                    user.setLastModified(lastmodified);
                    user.setBloodGroup(bloodGroup);
                    user.setUserMobile(mobile);
                    user.setEmpID(empId);
                    user.setTcsEmail(tcsEmail);
                    user.setDob(dob);
                    user.setUserTowerID(tower);
                    user.setProjectEmail(projEmail);
                    user.setSummary(summary);
                    user.setTools(tools);
                    user.setUserRole(userRole);
                    user.setProgramming_langs(programming_langs);
                    usersList.add(user);
                } catch (JSONException exp) {
                    Log.e("user json exp", i + " " + exp.toString());
                }
            }
            Log.e("userjson list size", usersList.size() + "");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("user json exp", e.toString());
        }
        return usersList;
    }
}
