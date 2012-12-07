package com.modzelewski.nfcgb;

import android.content.ClipDescription;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class DragEventListener implements OnDragListener {

	@Override
	public boolean onDrag(View v, DragEvent event) {
		final int action = event.getAction();

		switch (action) {
		case DragEvent.ACTION_DRAG_STARTED:
			if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
				Log.i(getClass().getSimpleName(), "ACTION DRAG STARTED accept");
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
	        // Dropped, reassign View to ViewGroup
	        View view = (View) event.getLocalState();
	        ViewGroup owner = (ViewGroup) view.getParent();
	        owner.removeView(view);
	        LinearLayout container = (LinearLayout) v;
	        container.addView(view);
	        view.setVisibility(View.VISIBLE);        
	        return false;
	        //return true; if drop accepted 
		}
		return true;
	}
}
