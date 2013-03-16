package com.modzelewski.nfcgb.controller;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;
import com.modzelewski.nfcgb.R;
import com.modzelewski.nfcgb.model.Group;
import com.modzelewski.nfcgb.model.Person;

import java.util.List;

/**
 * @author Georg
 */
public class GroupAdapter extends BaseExpandableListAdapter implements
        ExpandableListAdapter {
    // public class GroupAdapter extends BaseAdapter implements
    // ExpandableListAdapter {
    private final Context context;
    private final List<Group> groups;
    List<List<Person>> persons;

    // private final LayoutInflater inflater;

    public GroupAdapter(Context context, List<Group> groups) {
        super();
        this.context = context;
        this.groups = groups;
        // this.inflater = (LayoutInflater)
        // this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // public GroupAdapter(Context context, List<GroupData> groups,
    // List<List<PersonData>> persons) {
    // super();
    // this.context = context;
    // this.groups = groups;
    // this.persons = persons;
    // }

    public int getCount() {
        return groups.size();
    }

    public Object getItem(int position) {
        return groups.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // public View getView(int position, View convertView, ViewGroup parent) {
    // TextView text = (TextView) getView()
    // text.setText(groups.get(position).getGroupName());
    // return text;
    // }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).getPerson().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        TextView text = new TextView(context);
        text.setPadding(20, 0, 0, 0); // indent the child element a bit
        Log.i("GROUPADAPTER", "Gruppe an groupPosition\n = " + groups.get(groupPosition).toString());
        Log.i("GROUPADAPTER", "Liste Personen an groupPosition\n = " + groups.get(groupPosition).getPerson().toString());
        Log.i("GROUPADAPTER", "Person an childPosition\n = " + groups.get(groupPosition).getPerson().get(childPosition).getName());


        if (groups.get(groupPosition).getPerson().get(childPosition).getName() != null)
            text.setText(groups.get(groupPosition).getPerson()
                    .get(childPosition).getName());
        return text;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).getPerson().size();
    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        TextView text = new TextView(context);
        text.setPadding(70, 30, 10, 30);
        text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);

        if (isExpanded) {
            text.setTypeface(Typeface.create("serif", Typeface.BOLD));
            text.setBackgroundResource(R.color.AliceBlue);
        } else
            text.setTypeface(Typeface.create("serif", Typeface.NORMAL));

        text.setText(groups.get(groupPosition).getGroupName().toString());
        return text;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
    }

    public void onGroupExpand(int groupPosition) {
        Log.i(getClass().getSimpleName(), "onGroupExpand: " + groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

}
