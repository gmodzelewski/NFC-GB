package com.modzelewski.nfcgb.nfc;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.widget.Toast;
import com.modzelewski.nfcgb.R;

public class NfcCheck {
    private final Context context;
    private final NfcAdapter nfcAdapter;

    public NfcCheck(NfcAdapter nfcAdapter, Context context) {
        this.context = context;
        this.nfcAdapter = nfcAdapter;
    }

    public void check() {
        if (nfcAdapter == null) {
            Toast.makeText(context, context.getResources().getString(R.string.nfc_not_available), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.nfc_available), Toast.LENGTH_LONG).show();
        }
    }

}
