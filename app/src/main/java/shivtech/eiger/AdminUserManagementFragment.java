package shivtech.eiger;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdminUserManagementFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdminUserManagementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminUserManagementFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int ADD_USER=1;
    private static final int REMOVE_USER=0;
    EditText addEmpid, removeEmpid;
    Button addUserBtn, removeUserBtn;
    ProgressDialog progressDialog;
    boolean isvalid = false;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public AdminUserManagementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminUserManagementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminUserManagementFragment newInstance(String param1, String param2) {
        AdminUserManagementFragment fragment = new AdminUserManagementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View admin_userView = inflater.inflate(R.layout.fragment_admin_user_management, container, false);
        addEmpid = (EditText) admin_userView.findViewById(R.id.ad_add_user_empid);
        removeEmpid = (EditText) admin_userView.findViewById(R.id.ad_remove_user_empid);
        addUserBtn = (Button) admin_userView.findViewById(R.id.ad_add_user_btn);
        removeUserBtn = (Button) admin_userView.findViewById(R.id.ad_remove_user_btn);
        progressDialog = new ProgressDialog(getContext());
        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               final String empId = addEmpid.getText().toString();
                if (empId.trim().length() < 6)
                    addEmpid.setError("Invalid Empid");
                else {
                    showDialog(empId,ADD_USER); // 1 for remove user
                }

            }
        });
        removeUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String empId = removeEmpid.getText().toString();
                if (empId.trim().length() < 6)
                    removeEmpid.setError("Invalid Empid");
                else
                   showDialog(empId,REMOVE_USER); // 0 for remove user
            }
        });


        return admin_userView;

    }
    /*
    type 1 for add user
    type 0 for remove user
     */
    private void showDialog(final String empId,final int type)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final AlertDialog dialog = builder.create();
        if(type==ADD_USER)
            dialog.setMessage("Do you want to add this User?");
        else if(type==REMOVE_USER)
            dialog.setMessage("Do you want to Remove this User?");
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(type==ADD_USER)
                    addUser(empId);
                else if(type==REMOVE_USER)
                    removeUser(empId);
                dialog.dismiss();
            }
        });

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
    dialog.show();
    }

    private void removeUser(final String empid) {
        progressDialog.setMessage(" Checking Emp ID" + " ...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        String user_delete_url = "https://eigerapp.herokuapp.com/api/users/" + empid;
        final JsonObjectRequest deleteUserReq = new JsonObjectRequest(Request.Method.DELETE, user_delete_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Deleting user", "" + response.toString());
                try {
                    if (response.getString("msg").equals("User removed successfully")) {
                        Log.e("Deleting user", "Removed" + response.toString());
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "User Successfully Removed", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Unable to remove user ", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Unable to delete user", error.toString());
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();

                progressDialog.dismiss();

            }
        });

        checkEmpID(deleteUserReq, empid, 0);
    }

    public void addUser(final String empid) {
        progressDialog.setMessage(" Checking Emp ID" + " ...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        String sign_up_url = "https://eigerapp.herokuapp.com/api/users/" + empid;
        final JsonObjectRequest addUserReq = new JsonObjectRequest(Request.Method.PUT, sign_up_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Adding user", "" + response.toString());
                try {
                    if (response.getString("msg").equals("User successfully Added")) {
                        progressDialog.dismiss();
                        Log.e("Adding user", "Added" + response.toString());
                        Toast.makeText(getContext(), "User Successfully added", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Unable to add user ", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("unable to delete user", error.toString());
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();

            }
        });

        checkEmpID(addUserReq, empid, 1);
    }

    /*
    addOrRemove- flag for add or Remove

     */
    private void checkEmpID(final JsonObjectRequest request, String empid, final int addOrRemove) {


        String empId_check_url = "https://eigerapp.herokuapp.com/api/users/";
        empId_check_url += "empId?value=" + empid;

        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest empId_check_req = new JsonObjectRequest(Request.Method.GET, empId_check_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject responseObj) {
                if (responseObj.length() > 0) {
                    try {
                        Log.e("emp id check res", responseObj.toString());

                        Log.e("res type", responseObj.getString("type"));
                        if (responseObj.getString("type").equals("empId")) {
                            if (responseObj.getBoolean("empIdRegistered")) {

                                if (addOrRemove == 1) {
                                    addEmpid.requestFocus();
                                    addEmpid.setError("Emp ID is already added");
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Emp ID is already added", Toast.LENGTH_LONG).show();
                                }
                                if (addOrRemove == 0) {
                                    progressDialog.setMessage("Removing user ...");
                                    progressDialog.show();
                                    requestQueue.add(request);
                                }

                            } else {
                                if (addOrRemove == 1) {
                                    progressDialog.setMessage("Adding user ...");
                                    progressDialog.show();
                                    requestQueue.add(request);
                                }
                                if (addOrRemove == 0) {
                                    removeEmpid.requestFocus();
                                    removeEmpid.setError("Emp ID is not found");
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Emp ID is not found ", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        Log.e("json exp", e.toString());

                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                Log.e("emp id check err", error.toString());
                progressDialog.dismiss();
            }
        });
        requestQueue.add(empId_check_req);
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
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
