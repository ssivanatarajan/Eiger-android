package shivtech.eiger;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import shivtech.eiger.models.App;

/**
 * Created by Sivanatarajan on 24-02-2017.
 */

public class PriSecAppsAdapter extends BaseAdapter {
    ArrayList<App> AppsList;
    Context mContext;

    PriSecAppsAdapter(Context context, ArrayList<App> appsList) {
        this.AppsList = appsList;
        this.mContext = context;
        Log.e("pri sec app", appsList.size() + "");
    }

    @Override
    public int getCount() {
        Log.e("Appslist size", AppsList.size() + "");
        if (AppsList != null)
            return AppsList.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int i) {
        return AppsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        Log.e(" prisecgetview position", position + "");
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.pri_sec_apps_listitem, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.appName = (TextView) convertView.findViewById(R.id.app_name);
            viewHolder.appID = (TextView) convertView.findViewById(R.id.app_id);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.appName.setText(AppsList.get(position).getAppName());
        viewHolder.appID.setText(Integer.toString(AppsList.get(position).getAppId()));
        viewHolder.appName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent appProfileIntent = new Intent(mContext, AppProfile.class);
                int appid = Integer.valueOf(viewHolder.appID.getText().toString());
                appProfileIntent.putExtra("AppID", appid);
                mContext.startActivity(appProfileIntent);
            }
        });
        return convertView;

    }

    static class ViewHolder {
        TextView appName;

        TextView appID;
    }
}
