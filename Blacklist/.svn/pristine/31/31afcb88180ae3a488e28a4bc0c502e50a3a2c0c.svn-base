package com.gome.blacklist.dialog;

import com.gome.blacklist.BlacklistUtils;
import com.gome.blacklist.R;
import com.gome.blacklist.Record;
import com.gome.blacklist.utils.ToolUtils;
import com.hct.gios.widget.AlertDialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecordOptionsDialog extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "RecordOptionsDialog";
    private static final String KEY_RECORD = "key-record";
    private AlertDialog mDialog;
    private Record mCurrentRecord;

    // Public no-args constructor needed for fragment re-instantiation
    public RecordOptionsDialog() {}

    public static RecordOptionsDialog newInstance(Record record) {
        final RecordOptionsDialog fragment = new RecordOptionsDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_RECORD, record);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_record_options, null);
        mCurrentRecord = getArguments().getParcelable(KEY_RECORD);
        if (view != null && mCurrentRecord != null) {
            LinearLayout smsContent = (LinearLayout) view.findViewById(R.id.options_sms_content);
            LinearLayout addToContacts = (LinearLayout) view.findViewById(R.id.options_add_to_contacts);
			LinearLayout addToWhiteList = (LinearLayout) view.findViewById(R.id.options_add_to_white_list);
            LinearLayout callNumber = (LinearLayout) view.findViewById(R.id.options_call_number);
            LinearLayout recoverySms = (LinearLayout) view.findViewById(R.id.options_recovery_sms);
            TextView delete = (TextView) view.findViewById(R.id.options_delete);
            TextView content = (TextView) view.findViewById(R.id.sms_content);
            addToContacts.setOnClickListener(this);
            callNumber.setOnClickListener(this);
            recoverySms.setOnClickListener(this);
            addToWhiteList.setOnClickListener(this);
            delete.setOnClickListener(this);
            content.setText(mCurrentRecord.body);
            
            boolean showAddToContact = mCurrentRecord.type == 2 && TextUtils.isEmpty(mCurrentRecord.displayName);
			boolean showAddWhiteList = (false == BlacklistUtils.checkNumberExist(getActivity(), mCurrentRecord.phoneNumber, 2));
            smsContent.setVisibility(mCurrentRecord.type == 1 ? View.VISIBLE : View.GONE);
            recoverySms.setVisibility(mCurrentRecord.type == 1 ? View.VISIBLE : View.GONE);
            addToContacts.setVisibility(showAddToContact ? View.VISIBLE : View.GONE);
			addToWhiteList.setVisibility(showAddWhiteList ? View.VISIBLE : View.GONE);
            callNumber.setVisibility(mCurrentRecord.type == 2 ? View.VISIBLE : View.GONE);

            mDialog = new AlertDialog.Builder(getActivity())
            		.setTitle(ToolUtils.getContactNameNumber(mCurrentRecord.displayName, mCurrentRecord.phoneNumber))
            		.setView(view).create();
//            mDialog.setCanceledOnTouchOutside(false);
        }

        return mDialog;
    }
    
    @Override
    public void onClick(View view) {
    	switch (view.getId()) {
	    	case R.id.options_add_to_white_list:
	    		MoveToWhiltelistDialog whitelistDialog = MoveToWhiltelistDialog.newInstance(mCurrentRecord);
	            FragmentManager manager = getActivity().getFragmentManager();
	            whitelistDialog.show(manager, DeleteRecordDialog.TAG);
				this.dismiss();
	    		break;
	    	case R.id.options_add_to_contacts:
	    		AddToContactDialog addDialog = AddToContactDialog.newInstance(mCurrentRecord);
	            FragmentManager manager1 = getActivity().getFragmentManager();
	            addDialog.show(manager1, AddToContactDialog.TAG);
	    		this.dismiss();
	    		break;
	    	case R.id.options_call_number:
	    		Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
	    		phoneIntent.setData(Uri.parse("tel:" + mCurrentRecord.phoneNumber));
	    		phoneIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    		getActivity().startActivity(phoneIntent);
				this.dismiss();
	    		break;
	    	case R.id.options_recovery_sms:
	    		BlacklistUtils.restoreItemById(getActivity(), mCurrentRecord._id, true);
	    		this.dismiss();
	    		break;
	    	case R.id.options_delete:
	    		DeleteRecordDialog deleteDialog = DeleteRecordDialog.newInstance(mCurrentRecord);
	            FragmentManager manager2 = getActivity().getFragmentManager();
	            deleteDialog.show(manager2, DeleteRecordDialog.TAG);
	    		this.dismiss();
	    		break;
    	}
    }
}