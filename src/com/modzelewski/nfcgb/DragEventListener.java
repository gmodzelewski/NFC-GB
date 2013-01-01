package com.modzelewski.nfcgb;

import android.content.ClipData;
import android.content.ClipDescription;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.ExpandableListView;

public class DragEventListener implements OnDragListener {
	
	BackgroundModel model;
	
	public DragEventListener(BackgroundModel model) {
		this.model = model;		
	}
	
	@Override
	public boolean onDrag(View v, DragEvent event) {
		final int action = event.getAction();
		switch (action) {
		case DragEvent.ACTION_DRAG_STARTED:
			if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
				Log.i(getClass().getSimpleName(), "ACTION DRAG STARTED accept");
				v.invalidate();
				return true;
			} else {
				Log.i(getClass().getSimpleName(), "ACTION DRAG STARTED rejected");
				return false;
			}
		
		
		case MotionEvent.ACTION_DOWN:
			if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
				Log.i(getClass().getSimpleName(), "ACTION DRAG STARTED accept");
				return true;
			} else {
				Log.i(getClass().getSimpleName(), "ACTION DRAG STARTED rejected");
				return false;
			}
		

		case DragEvent.ACTION_DROP:
	        ClipData.Item i = event.getClipData().getItemAt(0);
	        int personId = Integer.parseInt((String) i.getText()) -1; //Location ist anscheinend PersonID -1 -> Nochmal checken
	        PersonData person = model.persons.get(personId);
	        Log.i(getClass().getSimpleName(), person.toString()); // Person ist gegeben

	        //Ab hier: Hole dir die richtige Gruppe
	        Log.i(getClass().getSimpleName(), "String.valueOf(v.getTag()) " + String.valueOf(v.getTag()));
//	        GroupData group = model.groups.get(2);
//	        Item current = (Item) v.getTag();
	        
	        Log.i(getClass().getSimpleName(), String.valueOf(v.getId()));
	        Log.i(getClass().getSimpleName(), String.valueOf(v.getContentDescription()));
	        	
	        
	        //TODO: Hole Gruppenobjekt group irgendwie (GroupData group = (GroupData) v;)
	        //TODO: person der Gruppe zuweisen
	        
//	        ExpandableListView elv = (ExpandableListView) v;
//	        Log.i(getClass().getSimpleName(), String.valueOf(elv.);
//	        model.groups.add(group);
	      
//	        owner.removeView(view);
//	        LinearLayout container = (LinearLayout) v;
//	        container.addView(view);
//	        view.setVisibility(View.VISIBLE);        
//	        return false;
	        v.invalidate();
	        return true; //if drop accepted 
		
		}	        
		return true;
	}
}
