package com.gome.blacklist.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.gome.blacklist.R;
import com.gome.blacklist.Record;
import com.hct.gios.widget.AlertDialog;
import com.gome.blacklist.BlacklistData.RecordlistTable;

@SuppressLint("NewApi")
public class DeleteRecordDialog  extends DialogFragment {

    public static final String TAG = "DeleteRecordDialog";
    private static final String KEY_RECORD = "key-record";
    private Record mCurrentRecord;

    // Public no-args constructor needed for fragment re-instantiation
    public DeleteRecordDialog() {}

    public static DeleteRecordDialog newInstance(Record record) {
        final DeleteRecordDialog dialog = new DeleteRecordDialog();
        final Bundle bundle = new Bundle(1);
        bundle.putParcelable(KEY_RECORD, record);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mCurrentRecord = getArguments().getParcelable(KEY_RECORD);
		int resId = mCurrentRecord.type == RecordlistTable.TYPE_CALL ?
				R.string.delete_message_intercept_call : R.string.delete_message_intercept_sms;
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
        		.setMessage(resId)
        		.setNegativeButton(R.string.cancel, null)
        		.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
			        	String selection = RecordlistTable._ID + " = " + mCurrentRecord._id;
			        	getContext().getContentResolver().delete(RecordlistTable.CONTENT_URI, selection, null);
					}
				}).create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}