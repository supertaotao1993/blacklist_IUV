package com.gome.blacklist.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.gome.blacklist.R;
import com.hct.gios.widget.AlertDialog;
import com.gome.blacklist.BlacklistData.RecordlistTable;

@SuppressLint("NewApi")
public class ClearRecordDialog  extends DialogFragment {

    public static final String TAG = "ClearRecordDialog";
    public static final String KEY_TYPE = "key_type";
    private int mType;

    // Public no-args constructor needed for fragment re-instantiation
    public ClearRecordDialog() {}

    public static ClearRecordDialog newInstance(int type) {
        final ClearRecordDialog dialog = new ClearRecordDialog();
        final Bundle bundle = new Bundle(1);
        bundle.putInt(KEY_TYPE, type);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	mType = getArguments().getInt(KEY_TYPE);
    	int messageId = mType == RecordlistTable.TYPE_CALL ?
    			R.string.intercept_clear_summary_call : R.string.intercept_clear_summary_sms;
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
        		.setTitle(R.string.tips)
        		.setMessage(messageId)
        		.setNegativeButton(R.string.cancel, null)
        		.setPositiveButton(R.string.delete_all, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
			        	String selection = RecordlistTable.TYPE + " = " + mType;
			        	getContext().getContentResolver().delete(RecordlistTable.CONTENT_URI, selection, null);
					}
				}).create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}