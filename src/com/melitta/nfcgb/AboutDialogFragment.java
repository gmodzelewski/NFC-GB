package com.melitta.nfcgb;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutDialogFragment extends DialogFragment {
	static AboutDialogFragment newInstance() {
		return new AboutDialogFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.about_fragment_dialog, container,
				false);
		return v;
	}
}