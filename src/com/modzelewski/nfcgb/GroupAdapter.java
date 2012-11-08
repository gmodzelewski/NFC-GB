package com.modzelewski.nfcgb;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

/**
 * TODO: just a demo adapter
 * 
 * @author Georg
 * 
 */
public class GroupAdapter extends BaseExpandableListAdapter implements ExpandableListAdapter {
//public class GroupAdapter extends BaseAdapter implements ExpandableListAdapter {
	Context context;
	List<GroupData> items;

	public GroupAdapter(Context context, List<GroupData> items) {
		super();
		this.context = context;
		this.items = items;
	}

	public int getCount() {
		return items.size();
	}

	public Object getItem(int position) {
		return items.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TextView text = (TextView) getView(position, convertView, parent);
		text.setText(items.get(position).groupName);
		return text;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return items.get(groupPosition).person.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		TextView text = new TextView(context);
		text.setPadding(20, 0, 0, 0); // indent the child element a bit
		text.setText(items.get(groupPosition).person.get(childPosition).name);
		return text;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return items.get(groupPosition).person.size();
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
		return items.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return items.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		TextView text = new TextView(context);
		text.setPadding(70, 30, 10, 30);
		text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);

		if (isExpanded) {
			text.setTypeface(Typeface.create("serif", Typeface.BOLD));
			text.setBackgroundResource(R.color.AliceBlue);
		} else
			text.setTypeface(Typeface.create("serif", Typeface.NORMAL));

		text.setText(items.get(groupPosition).groupName.toString());
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

	@Override
	public void onGroupExpanded(int groupPosition) {
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

}
