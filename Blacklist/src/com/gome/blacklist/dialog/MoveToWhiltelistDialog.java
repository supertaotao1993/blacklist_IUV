package com.gome.blacklist.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.gome.blacklist.BlacklistUtils;
import com.gome.blacklist.R;
import com.gome.blacklist.Record;
import com.gome.blacklist.utils.ToolUtils;
import com.hct.gios.widget.AlertDialog;
import com.hct.gios.widget.CheckBoxHCT;

@SuppressLint("NewApi")
public class MoveToWhiltelistDialog  extends DialogFragment {

    public static final String TAG = "MoveToWhiltelistDialog";
    private static final String KEY_RECORD = "key-record";
    private Record mCurrentRecord;

    // Public no-args constructor needed for fragment re-instantiation
    public MoveToWhiltelistDialog() {}

    public static MoveToWhiltelistDialog newInstance(Record record) {
        final MoveToWhiltelistDialog dialog = new MoveToWhiltelistDialog();
        final Bundle bundle = new Bundle(1);
        bundle.putParcelable(KEY_RECORD, record);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mCurrentRecord = getArguments().getParcelable(KEY_RECORD);
		View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_move_to_whitelist, null);
		
		final TextView msgTextView = (TextView) view.findViewById(R.id.dialog_message);
		if(false == BlacklistUtils.checkNumberExist(getActivity(), mCurrentRecord.phoneNumber, 1)) {
			msgTextView.setText(R.string.dialog_move_to_whitelist_summary2);
		}
		if (view == null || mCurrentRecord == null) return null;
		final CheckBoxHCT check = (CheckBoxHCT) view.findViewById(R.id.dialog_move_to_whitelist_checked);
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
        		.setTitle(ToolUtils.getContactNameNumber(mCurrentRecord.displayName, mCurrentRecord.phoneNumber))
        		.setView(view)
        		.setNegativeButton(R.string.cancel, null)
        		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						BlacklistUtils.deleteNumber(getActivity(), mCurrentRecord.phoneNumber, 1, false);
						BlacklistUtils.insertNumber(getActivity(), null, mCurrentRecord.phoneNumber, 2, true);
						if (check.isChecked()) {
				    		BlacklistUtils.restoreItemByNumber(getActivity(), mCurrentRecord.phoneNumber);
						}
					}
				}).create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}