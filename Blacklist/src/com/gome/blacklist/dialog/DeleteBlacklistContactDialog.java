package com.gome.blacklist.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.gome.blacklist.R;
import com.hct.gios.widget.AlertDialog;
import com.hct.gios.widget.CheckBoxHCT;

@SuppressLint("NewApi")
public class DeleteBlacklistContactDialog  extends DialogFragment {

    public static final String TAG = "DeleteBlacklistContactDialog";
    private OnClickListener listener;

    // Public no-args constructor needed for fragment re-instantiation
    public DeleteBlacklistContactDialog() {}

    public static DeleteBlacklistContactDialog newInstance() {
        final DeleteBlacklistContactDialog dialog = new DeleteBlacklistContactDialog();
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_delete_blacklist_contact, null);
        if (view == null) return null;
        final CheckBoxHCT check = (CheckBoxHCT) view.findViewById(R.id.delete_blacklist_contact_recovery_checked);
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
        		.setTitle(R.string.blacklist_delete_contact_title)
        		.setNegativeButton(R.string.cancel, null)
        		.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (listener != null) {
							listener.onContactDelete(check.isChecked());
						}
					}
				})
        		.setView(view).create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
    
    public void setOnClickListener(OnClickListener listener) {
    	this.listener = listener;
    }
    
    public interface OnClickListener {
    	void onContactDelete(boolean restoreSms);
    }
}