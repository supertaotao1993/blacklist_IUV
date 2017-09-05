package com.gome.blacklist.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gome.blacklist.R;
import com.hct.gios.widget.AlertDialog;
import com.gome.blacklist.BlacklistData.BlacklistTable;
import com.gome.blacklist.BlacklistData.WhitelistTable;
import com.gome.blacklist.BlacklistUtils;

@SuppressLint("NewApi")
public class AddContactDialog  extends DialogFragment implements TextWatcher {

    public static final String TAG = "AddContactDialog";
    private static final String KEY_DIALOG_TYPE = "key_dialog_type";
    private static final String KEY_CONTACT_TYPE = "key_contact_type";

    public static final int DIALOG_TYPE_MANUAL = 0x1001;
    public static final int DIALOG_TYPE_PREFIX_NUMBER = 0x1002;
    public static final int CONTACT_TYPE_BLACKLIST = 0x1003;
    public static final int CONTACT_TYPE_WHITELIST = 0x1004;
    
    private EditText mInputNumber;
    private AlertDialog mDialog;
    
    private int mDialogType;
    private int mContactType;

    // Public no-args constructor needed for fragment re-instantiation
    public AddContactDialog() {}

    public static AddContactDialog newInstance(int dialogType, int contactType) {
        final AddContactDialog dialog = new AddContactDialog();
        final Bundle bundle = new Bundle(2);
        bundle.putInt(KEY_DIALOG_TYPE, dialogType);
        bundle.putInt(KEY_CONTACT_TYPE, contactType);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_contact_add, null);
        mDialogType = getArguments().getInt(KEY_DIALOG_TYPE);
        mContactType = getArguments().getInt(KEY_CONTACT_TYPE);
        if (view != null && mDialogType > 0 && mContactType > 0) {
        	TextView summary = (TextView) view.findViewById(R.id.blacklist_add_number_prefix_summary);
        	summary.setVisibility(mDialogType == DIALOG_TYPE_PREFIX_NUMBER ? View.VISIBLE : View.GONE);
        	mInputNumber = (EditText) view.findViewById(R.id.contact_add_input);
        	mInputNumber.addTextChangedListener(this);
        }
        
        int titleId = mDialogType == DIALOG_TYPE_PREFIX_NUMBER ?
        		R.string.blacklist_add_number_prefix : R.string.blacklist_add_manual;
        mDialog = new AlertDialog.Builder(getActivity())
        		.setTitle(titleId)
        		.setNegativeButton(R.string.cancel, null)
        		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (mContactType <= 0) return;
			        	String number = mDialogType == DIALOG_TYPE_PREFIX_NUMBER ?
			        			mInputNumber.getText().toString() + "*" : mInputNumber.getText().toString();
						int type = (mContactType == CONTACT_TYPE_BLACKLIST)? 1:2;
						int checkType = (mContactType == CONTACT_TYPE_BLACKLIST)? 2:1;
						if(BlacklistUtils.checkNumberExist(getContext(), number, type)) {
							int toastId = R.string.toast_exist_number_blacklist;
							if(type == 2) {
								toastId = R.string.toast_exist_number_whitelist;
							}
							Toast.makeText(getContext(), toastId, Toast.LENGTH_SHORT).show();
						}else if(mDialogType != DIALOG_TYPE_PREFIX_NUMBER && BlacklistUtils.checkNumberExist(getContext(), number, checkType)) {
							removeConfirm(getContext(), type, checkType, number);
						} else {
							BlacklistUtils.insertNumber(getContext(), null, number, type, true);
						}
					}
				})
        		.setView(view).create();
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                setPositiveEnable(TextUtils.isEmpty(mInputNumber.getText().toString().trim()) ? false : true);
            }
        });
        mDialog.setCanceledOnTouchOutside(false);

        return mDialog;
    }

	   /**
   * to show a confrim dialog if number is in white list
   */
   private void removeConfirm(final Context context, final int type, final int checkType, final String number) {
	  int msgId = R.string.dialog_move_to_blacklist_summary;
	  if(type == 2) {
	  	msgId = R.string.dialog_move_to_whitelist_summary;
	  }
      AlertDialog dialog = new AlertDialog.Builder(context)
         .setTitle(number) // Tips
         .setMessage(msgId)			
         .setNegativeButton(R.string.cancel, null)
         .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               BlacklistUtils.deleteNumber(context, number, checkType, false);
               BlacklistUtils.insertNumber(context, null, number, type, false);
            }
         }).create();
      dialog.setCanceledOnTouchOutside(false);
	  dialog.show();
   }

    public void setPositiveEnable(boolean enable) {
        if (mDialog == null) return;
        Button positive = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (positive != null) {
            positive.setEnabled(enable);
        }
    }

	@Override
	public void afterTextChanged(Editable arg0) {
        if (mInputNumber == null) return;
        setPositiveEnable(TextUtils.isEmpty(mInputNumber.getText().toString().trim()) ? false : true);
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
}