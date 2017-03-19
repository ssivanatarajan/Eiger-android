package shivtech.eiger;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import shivtech.eiger.models.User;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserProfile_about.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserProfile_about#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfile_about extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView mobile, tcsEmail, projEmail, dob, bloodgroup, prgrm_langs, tools, hobbies;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public UserProfile_about() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfile_about.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfile_about newInstance(String param1, String param2) {
        UserProfile_about fragment = new UserProfile_about();
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
        View view = inflater.inflate(R.layout.fragment_user_profile_about, container, false);
        User user = UserProfile.user;
        mobile = (TextView) view.findViewById(R.id.UPT_user_mobile);
        tcsEmail = (TextView) view.findViewById(R.id.UPT_user_tcs_email);
        projEmail = (TextView) view.findViewById(R.id.UPT_user_proj_email);
        dob = (TextView) view.findViewById(R.id.UPT_user_dob);
        bloodgroup = (TextView) view.findViewById(R.id.UPT_user_bloodgrp);
        prgrm_langs = (TextView) view.findViewById(R.id.UPT_prgm_langs);
        hobbies = (TextView) view.findViewById(R.id.UPT_user_hobbies);
        tools = (TextView) view.findViewById(R.id.UPT_tools);
        mobile.setText(user.getUserMobile());
        tcsEmail.setText(user.getTcsEmail());
        projEmail.setText(user.getProjectEmail());
        hobbies.setText(user.getHobbies());
        bloodgroup.setText(user.getBloodGroup());
        dob.setText(user.getDob());
        /*if(user.getDob()!=null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            Date DOB = null;
            try {
                DOB = dateFormat.parse(user.getDob());
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("Date parse exp", e.toString());
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            dob.setText(sdf.format(DOB));
        }*/
        String prgrmlngs = user.getProgramming_langs();

        Drawable d = getResources().getDrawable(R.drawable.spanablbg);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(getContext(), R.drawable.spanablbg);
        prgrm_langs.setTransformationMethod(null);
        tools.setTransformationMethod(null);
        if (prgrmlngs != null && prgrmlngs.trim().length() > 0) {
            prgrmlngs = " " + prgrmlngs + " ";
            prgrmlngs.replace(",", "  ,  ");

            SpannableString spanablePrgmLngs = new SpannableString(prgrmlngs);
            Log.e("spanable prgm langs", spanablePrgmLngs.toString());
            int i = 0;

            while (i < spanablePrgmLngs.length()) {
                int indx = prgrmlngs.indexOf(',', i);
                Log.e("prgmspanindx", i + " " + indx);

                if (indx > 0) {
                    Log.e("prgmspanindx", i + " " + indx);

                    spanablePrgmLngs.setSpan(new BackgroundColorSpan(R.color.textlinkcolor), i, indx, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                    i += indx + 1;
                } else {
                    spanablePrgmLngs.setSpan(new BackgroundColorSpan(R.color.textlinkcolor), i, spanablePrgmLngs.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    break;
                }
            }
            prgrm_langs.setText(spanablePrgmLngs);
        }
        String toolsTxt = user.getTools();
        if (toolsTxt != null && toolsTxt.trim().length() > 0) {
            toolsTxt = " " + toolsTxt + " ";
            toolsTxt.replace(",", " , ");
            SpannableString spanableTools = new SpannableString(toolsTxt);
            int i = 0;
            while (i < spanableTools.length()) {
                int indx = toolsTxt.indexOf(',', i);
                Log.e("toolsspanindx", i + " " + indx);
                if (indx > 0) {
                    spanableTools.setSpan(span, i, indx, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                    i += indx + 1;
                } else {
                    spanableTools.setSpan(span, i, spanableTools.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    break;
                }

            }
            tools.setText(spanableTools);

        }
        return view;

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
