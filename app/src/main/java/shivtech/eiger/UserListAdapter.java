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

import shivtech.eiger.models.User;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class UserListAdapter extends BaseAdapter implements SectionIndexer {

    HashMap<String, Integer> mapIndex;
    String[] sections;
    List<User> UsersList;
    Context mContext;
    ArrayList<String> sectionList;

    //private final List<User> mValues;


    public UserListAdapter(Context context, List<User> items) {
        UsersList = items;
        mContext = context;
        init();
    }

    public void init() {
        mapIndex = new LinkedHashMap<String, Integer>();

        for (int x = 0; x < UsersList.size(); x++) {
            String username = UsersList.get(x).getUserName();
            String ch = username.substring(0, 1);
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
        Log.i("in get count", UsersList.size() + "");

        return UsersList.size();
    }

    @Override
    public User getItem(int i) {
        return UsersList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        UserListAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_useritem, viewGroup, false);
            viewHolder = new UserListAdapter.ViewHolder();
            viewHolder.userName = (TextView) convertView.findViewById(R.id.user_name);
            viewHolder.userMobile = (TextView) convertView.findViewById(R.id.user_mobile);
            viewHolder.EmpID = (TextView) convertView.findViewById(R.id.user_id);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (UserListAdapter.ViewHolder) convertView.getTag();
        viewHolder.userName.setText(UsersList.get(position).getUserName());
        viewHolder.userMobile.setText(UsersList.get(position).getUserMobile());
        viewHolder.EmpID.setText(Integer.toString(UsersList.get(position).getEmpID()));

        return convertView;
    }

    static class ViewHolder {
        TextView userName;
        TextView userMobile;
        TextView EmpID;
    }
}
