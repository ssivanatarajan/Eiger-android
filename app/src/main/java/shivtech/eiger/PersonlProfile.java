package shivtech.eiger;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import shivtech.eiger.db.DBHandler;
import shivtech.eiger.models.User;
import shivtech.eiger.utils.Constants;
import shivtech.eiger.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PersonlProfile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PersonlProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonlProfile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String currentUser;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;
    EditText name, mobile, tcsEmail, projEmail, dob, hobbies, bloodGroup, summary;
    Calendar myCalendar;
    DBHandler dbHandler;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public PersonlProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonlProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonlProfile newInstance(String param1, String param2) {
        PersonlProfile fragment = new PersonlProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHandler = new DBHandler(getContext());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personl_profile, container, false);
        sharedPreferences = getContext().getSharedPreferences(Constants.shared_prefs, Context.MODE_PRIVATE);
        int empid = sharedPreferences.getInt(Constants.sp_cur_user_empId, 0);
        currentUser = String.valueOf(empid);
        DBHandler dbHandler = new DBHandler(getContext());
        Log.e("surrent user empid", empid + "");
        User user = dbHandler.getUserdetails(empid);

        name = (EditText) view.findViewById(R.id.name);
        mobile = (EditText) view.findViewById(R.id.mobile);
        tcsEmail = (EditText) view.findViewById(R.id.tcsEmail);
        projEmail = (EditText) view.findViewById(R.id.projectEmail);
        bloodGroup = (EditText) view.findViewById(R.id.bloodgroup);
        dob = (EditText) view.findViewById(R.id.dob);
        hobbies = (EditText) view.findViewById(R.id.hobbies);
        summary = (EditText) view.findViewById(R.id.summary);
        Log.e("user details", user.getUserName() + "" + user.getUserMobile());
        name.setText(user.getUserName());
        mobile.setText(user.getUserMobile());
        tcsEmail.setText(user.getTcsEmail());
        projEmail.setText(user.getProjectEmail());
        bloodGroup.setText(user.getBloodGroup());
        dob.setText(user.getDob());
        hobbies.setText(user.getHobbies());
        summary.setText(user.getSummary());

        myCalendar = Calendar.getInstance();
        Button save = (Button) view.findViewById(R.id.save);
        progressDialog = new ProgressDialog(getActivity());
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             /*   if (!Utils.checkInternetConnection()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                            .setMessage("No Internet Connection, please check your connectivity")
                            .setIcon(android.R.drawable.stat_sys_warning)
                            .setPositiveButton("Ok", null);

                    AlertDialog dialog = builder.show();


                } else {*/
                save();
                //}
            }
        });
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDOB();
            }

        };
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        return view;
    }

    private void updateDOB() {

        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dob.setText(sdf.format(myCalendar.getTime()));
    }

    private void save() {
        String profile_save_url = "https://eigerapp.herokuapp.com/api/users/";
        boolean isValid = true;
        final String str_name, str_tcsEmail, str_projEmail, str_hobbies, str_mobile, str_dob, str_bloodGroup, str_summary;
        str_summary = summary.getText().toString();
        str_name = name.getText().toString();
        str_tcsEmail = tcsEmail.getText().toString();
        str_projEmail = projEmail.getText().toString();
        str_mobile = mobile.getText().toString();
        str_hobbies = hobbies.getText().toString();
        str_dob = dob.getText().toString();
        str_bloodGroup = bloodGroup.getText().toString();

        String projEmailPattern = "[a-zA-Z0-9._-]+@vodafone.com";
        if (str_name.trim().length() == 0) {
            isValid = false;
            name.requestFocus();
            name.setError("Name is required");
        }
        if (str_mobile.trim().length() == 0) {
            isValid = false;
            mobile.requestFocus();
            mobile.setError("Mobile no is required");
        }
        if (str_tcsEmail.trim().length() == 0) {
            isValid = false;
            tcsEmail.requestFocus();
            tcsEmail.setError("TCS Email is required");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(str_tcsEmail.trim()).matches()) {
            isValid = false;
            tcsEmail.requestFocus();
            tcsEmail.setError(" Invalid TCS Email");
        }
        if (str_projEmail.trim().length() == 0) {
            isValid = false;
            projEmail.requestFocus();
            projEmail.setError("Client Email is required");
        } else if (!str_projEmail.matches(projEmailPattern)) {
            isValid = false;
            projEmail.requestFocus();
            projEmail.setError("Invalid Client Email");
        }
        if (str_dob.trim().length() == 0) {
            isValid = false;
            dob.requestFocus();
            dob.setError("DOB is required");
        }

        if (str_bloodGroup.trim().length() == 0) {
            isValid = false;
            bloodGroup.requestFocus();
            bloodGroup.setError("BloodGroup is required");
        }
        if (isValid) {
            long res = dbHandler.updatePersonalProfile(currentUser, str_name, str_summary, str_tcsEmail, str_projEmail, str_mobile, str_hobbies, str_dob, str_bloodGroup);
            if (res == 1)
                Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_LONG).show();

            if (Utils.checkInternetConnection()) {
                progressDialog.setMessage("Saving data..");
                progressDialog.show();

                Log.e("isvalid", "true");
                final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                JSONObject params = new JSONObject();
                try {
                    params.put("empId", currentUser);
                    params.put("name", str_name);
                    params.put("bloodGroup", str_bloodGroup);
                    params.put("mobile", str_mobile);
                    params.put("tcsEmail", str_tcsEmail);
                    params.put("projEmail", str_projEmail);
                    params.put("dob", str_dob);
                    params.put("hobbies", str_hobbies);
                    params.put("summary", str_summary);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final String requestBody = params.toString();
                final JsonObjectRequest update_profile_request = new JsonObjectRequest(Request.Method.POST, profile_save_url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e("save res", response.toString());
                                try {
                                    if (response.getString("msg").equals("updated successfully"))
                                        Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_LONG).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                progressDialog.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("save err", error.toString());
                        Toast.makeText(getContext(), "Error occurs " + error.toString(), Toast.LENGTH_LONG).show();
                        dbHandler.makeDirty(Constants.userTable, 1, currentUser);
                        progressDialog.setMessage("Error occurs");
                        progressDialog.dismiss();

                    }
                }) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }


                    @Override
                    public byte[] getBody() {
                        try {
                            return requestBody == null ? null : requestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                    requestBody, "utf-8");
                            return null;
                        }
                    }
                };
                requestQueue.add(update_profile_request);
            } else {
                dbHandler.makeDirty(Constants.userTable, 1, currentUser);

            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
