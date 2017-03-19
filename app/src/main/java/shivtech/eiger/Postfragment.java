package shivtech.eiger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shivtech.eiger.db.DBHandler;
import shivtech.eiger.jsonparser.PostJSONParser;
import shivtech.eiger.models.Post;
import shivtech.eiger.utils.Constants;

import static shivtech.eiger.Postfragment.PostAdapter.*;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Postfragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Postfragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Postfragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RequestQueue requestQueue;
    SharedPreferences sharedPreferences;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    EditText postContent;

    Button postBtn;
    RecyclerView postRecyclerView;
    List<Post> postList;
    PostAdapter postAdapter;
    int currentuserempid;
    String currentUserName;
    private OnFragmentInteractionListener mListener;

    public Postfragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Postfragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Postfragment newInstance(String param1, String param2) {
        Postfragment fragment = new Postfragment();
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
        View postView=inflater.inflate(R.layout.fragment_post, container, false);
        requestQueue = Volley.newRequestQueue(getContext());
        sharedPreferences = getContext().getSharedPreferences(Constants.shared_prefs, Context.MODE_PRIVATE);
        currentUserName=sharedPreferences.getString(Constants.sp_cur_user_name," ");
        currentuserempid=sharedPreferences.getInt(Constants.sp_cur_user_empId,-1);
        postList=new ArrayList<Post>();
        postContent=(EditText)postView.findViewById(R.id.post_msg);
        postBtn=(Button)postView.findViewById(R.id.post_btn);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String postbody=postContent.getText().toString();
                if(postbody.trim().length()>0)
                    addPost(postbody);
            }
        });
        postRecyclerView=(RecyclerView)postView.findViewById(R.id.posts_recyclerview);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        postRecyclerView.setLayoutManager(llm);
        fetchPosts();
        postAdapter=new PostAdapter(postList);
        postRecyclerView.setAdapter(postAdapter);
        postRecyclerView.setItemAnimator(new DefaultItemAnimator());
        postRecyclerView.setHasFixedSize(true);

        return postView;
    }
    private void fetchPosts()
    {

        String fecthPosts_url = "https://eigerapp.herokuapp.com/api/posts/";
        final JsonObjectRequest addPostReq = new JsonObjectRequest(Request.Method.GET, fecthPosts_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Fetching post", "" + response.toString());
                PostJSONParser postJSONParser=new PostJSONParser(response.toString());
                postList=postJSONParser.parsePosts();
                postAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("unable to add Post", error.toString());
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void addPost(final String postbody)
    {
        JSONObject params = new JSONObject();
        try {

            params.put("userid", currentuserempid);
            params.put("body", postbody);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = params.toString();
        Log.e("add Post request body", requestBody);
        String add_post_url = "https://eigerapp.herokuapp.com/api/posts/";
        final JsonObjectRequest addPostReq = new JsonObjectRequest(Request.Method.POST, add_post_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Adding post", "" + response.toString());
                try {
                    if (response.getString("msg").equals("Post successfully Added")) {
                        Post post=new Post();
                        post.setPostContent(postbody);
                        post.setPostedBy(currentUserName);
                        post.setPostID(response.getInt("postid"));
                        postList.add(0,post);
                        postContent.setText("");
                        postAdapter.notifyDataSetChanged();
                        postRecyclerView.invalidate();
                        Log.e("Adding Post", "Added" + response.toString()+" "+postList.size());
                        Toast.makeText(getContext(), " Successfully posted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Unable to add post ", Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Add post err",e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("unable to add Post", error.toString());
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();

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
        requestQueue.add(addPostReq);


    }

    private void initPosts()
    {

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

    public  class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
       private List<Post> postsList;
        public PostAdapter(List<Post> postList)
       {
        postsList=postList;
       }

        public class ViewHolder extends RecyclerView.ViewHolder {
           private TextView postedby,postContent,likestats;
           ToggleButton likeToggle,commentToggle;
            RelativeLayout commentsLayout;

           public ViewHolder(View view)
           {
               super(view);
               postedby=(TextView)view.findViewById(R.id.postedBy);
               postContent=(TextView)view.findViewById(R.id.postcontent);
               likeToggle=(ToggleButton)view.findViewById(R.id.liketoggle);
               likestats=(TextView)view.findViewById(R.id.likeStats);
               commentToggle=(ToggleButton)view.findViewById(R.id.commenttoggle);
               commentsLayout=(RelativeLayout)view.findViewById(R.id.commentsLayout);
           }

       }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.postitem, parent, false);

            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Post post=postsList.get(position);
            holder.postedby.setText(post.getPostedBy());
            holder.postContent.setText(post.getPostContent());
            holder.likeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b)
                        compoundButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                    else
                        compoundButton.setTextColor(getResources().getColor(R.color.unliketextcolor));

                }
            });
            holder.commentToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b)
                        holder.commentsLayout.setVisibility(View.VISIBLE);
                    else
                        holder.commentsLayout.setVisibility(View.GONE);

                }
            });
            int likeCount=post.getLikeCount();
            if(likeCount>0)
            {
                if(!post.islikedByYou())
                    holder.likestats.setText(post.getLikeCount()+" likes");
                else
                    holder.likestats.setText("You and "+post.getLikeCount()+" other liked");
            }

        }

        @Override
        public int getItemCount() {
            return postsList.size();
        }
    }
}
