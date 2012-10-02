package com.melitta.nfcgb;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

/**
 * TODO: just a demo adapter
 * @author Georg
 *
 */
public class GroupAdapter extends BaseAdapter implements ExpandableListAdapter {
	Context context;
	List<GroupData> items;

	public GroupAdapter(Context context, List<GroupData> items) {
		super();
		this.context = context;
		this.items = items;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView text = new TextView(context);
		text.setText(items.get(position).toString());
		return text;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return items.get(groupPosition * 3 + childPosition + 1);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return groupPosition * 3 + childPosition + 1;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		TextView text = new TextView(context);
		text.setText("  child: "+items.get(groupPosition * 3 + childPosition + 1));
		return text;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (groupPosition < items.size() / 3)
			return 2;
		int count = items.size() % 3 - 1;
		Log.e("GA", ""+count);
		count = Math.max(0, count);
		Log.e("GA2", ""+count);
		return count;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGroupCount() {
		return (items.size()+2)/3;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		TextView text = new TextView(context);
		text.setText("group: "+items.get(groupPosition*3));
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

}
