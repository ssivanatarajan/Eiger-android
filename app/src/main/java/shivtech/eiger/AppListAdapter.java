package shivtech.eiger;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import shivtech.eiger.models.App;

/**
 * Created by Sivanatarajan on 26-01-2017.
 */

public class AppListAdapter extends BaseAdapter implements SectionIndexer {
    HashMap<String, Integer> mapIndex;
    String[] sections;
    List<App> appsList;
    Context mContext;
    ArrayList<String> sectionList;

    public AppListAdapter(Context context, List<App> appList) {
        //super(context, R.layout.fragement_applistitem,appList);
        this.mContext = context;
        this.appsList = appList;
        init();
    }

    public void init() {
        mapIndex = new LinkedHashMap<String, Integer>();

        for (int x = 0; x < appsList.size(); x++) {
            String appname = appsList.get(x).getAppName();
            String ch = appname.substring(0, 1);
            ch = ch.toUpperCase(Locale.US);

            // HashMap will prevent duplicates
            if (mapIndex.get(ch) == null)
                mapIndex.put(ch, x);
        }
        Set<String> sectionLetters = mapIndex.keySet();
        Log.d("SectionLetters", sectionLetters.size() + " " + sectionLetters);
        // create a list from the set to sort
        sectionList = new ArrayList<String>(sectionLetters);

        Log.d("sectionList", sectionList.toString());
        Collections.sort(sectionList);

        sections = new String[sectionList.size()];

        sectionList.toArray(sections);


    }

    public int getPositionForSection(int section) {
        Log.d("section", "" + section);
        return mapIndex.get(sections[section]);
    }

    @Override
    public int getSectionForPosition(int i) {
        return 0;
    }


    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getCount() {
        Log.i("in get count", appsList.size() + "");

        return appsList.size();
    }

    @Override
    public App getItem(int i) {
        return appsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.fragement_applistitem, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.appName = (TextView) convertView.findViewById(R.id.app_name);
            viewHolder.appTower = (TextView) convertView.findViewById(R.id.app_tower);
            viewHolder.appID = (TextView) convertView.findViewById(R.id.app_id);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.appName.setText(appsList.get(position).getAppName());
        viewHolder.appTower.setText(appsList.get(position).getAppTower());
        viewHolder.appID.setText(Integer.toString(appsList.get(position).getAppId()));

        return convertView;
    }

    static class ViewHolder {
        TextView appName;
        TextView appTower;
        TextView appID;
    }

}


