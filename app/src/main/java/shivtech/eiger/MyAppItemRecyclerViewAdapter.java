package shivtech.eiger;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import shivtech.eiger.AppItemFragment.OnListFragmentInteractionListener;
import shivtech.eiger.dummy.DummyContent.DummyItem;
import shivtech.eiger.models.App;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyAppItemRecyclerViewAdapter extends RecyclerView.Adapter<MyAppItemRecyclerViewAdapter.ViewHolder>  {
    public Context mcontext;
    private final List<App> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyAppItemRecyclerViewAdapter(List<App> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_appitem, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mAppNameView.setText(mValues.get(position).getAppName());
        holder.mAppTowerView.setText(mValues.get(position).getAppTower());
        holder.mAppIdView.setText(Integer.toString(mValues.get(position).getAppId()));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
                mcontext=holder.mView.getContext();

                        final Intent appProfIntent=new Intent(mcontext,AppProfile.class);
                        int appId=Integer.parseInt(holder.mAppIdView.getText().toString());
                        appProfIntent.putExtra("AppID",appId);
                        Log.i("itemclick","itemclick");
                        mcontext.startActivity(appProfIntent);
                    }


            });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mAppNameView;
        public final TextView mAppTowerView;
        public final TextView mAppIdView;
        public App mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mcontext=view.getContext();
            mAppIdView=(TextView)view.findViewById(R.id.app_id);
            mAppNameView = (TextView) view.findViewById(R.id.app_name);
            mAppTowerView = (TextView) view.findViewById(R.id.app_tower);

        }

        @Override
        public String toString() {
            return super.toString() + mAppIdView.getText()+" '" +mAppNameView.getText()+ " "+ mAppTowerView.getText() + "'";
        }
    }
}
