package com.gome.blacklist.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.gome.blacklist.R;
import com.hct.gios.widget.AlertDialog;

@SuppressLint("NewApi")
public class DeleteWhitelistContactDialog  extends DialogFragment {

    public static final String TAG = "DeleteWhitelistContactDialog";
    private OnClickListener listener;

    // Public no-args constructor needed for fragment re-instantiation
    public DeleteWhitelistContactDialog() {}

    public static DeleteWhitelistContactDialog newInstance() {
        final DeleteWhitelistContactDialog dialog = new DeleteWhitelistContactDialog();
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
        		.setTitle(R.string.whitelist_delete_contact_title)
        		.setNegativeButton(R.string.cancel, null)
        		.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (listener != null) {
							listener.onContactDelete();
						}
					}
				})
        		.setMessage(R.string.whitelist_delete_contact_summary).create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
    
    public void setOnClickListener(OnClickListener listener) {
    	this.listener = listener;
    }
    
    public interface OnClickListener {
    	void onContactDelete();
    }
}