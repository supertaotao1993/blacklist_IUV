package com.gome.blacklist.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.gome.blacklist.R;
import com.gome.blacklist.Record;
import com.hct.gios.widget.AlertDialog;

@SuppressLint("NewApi")
public class AddToContactDialog  extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "AddToContactDialog";
    private static final String KEY_RECORD = "key-record";
    private Record mCurrentRecord;

    // Public no-args constructor needed for fragment re-instantiation
    public AddToContactDialog() {}

    public static AddToContactDialog newInstance(Record record) {
        final AddToContactDialog dialog = new AddToContactDialog();
        final Bundle bundle = new Bundle(1);
        bundle.putParcelable(KEY_RECORD, record);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_to_contact, null);
        mCurrentRecord = getArguments().getParcelable(KEY_RECORD);
        if (mCurrentRecord != null && view != null) {
    		TextView addNew = (TextView) view.findViewById(R.id.add_new_contact);
    		TextView update = (TextView) view.findViewById(R.id.update_exsit_contact);
    		addNew.setOnClickListener(this);
    		update.setOnClickListener(this);
        }

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
        		.setTitle(R.string.add_to_contacts)
        		.setView(view).create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
    
    @Override
    public void onClick(View view) {
    	switch (view.getId()) {
	    	case R.id.add_new_contact:
	    		Intent addIntent = new Intent(Intent.ACTION_INSERT, Uri.withAppendedPath(Uri.parse("content://com.android.contacts"), "contacts"));
	            addIntent.setType("vnd.android.cursor.dir/person");
	            addIntent.setType("vnd.android.cursor.dir/contact");
	            addIntent.setType("vnd.android.cursor.dir/raw_contact");
	            addIntent.putExtra(android.provider.ContactsContract.Intents.Insert.PHONE, mCurrentRecord.phoneNumber);
	            startActivity(addIntent);
	    		this.dismiss();
	    		break;
	    	case R.id.update_exsit_contact:
	    		Intent oldConstantIntent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
	            oldConstantIntent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
	            oldConstantIntent.putExtra(ContactsContract.Intents.Insert.PHONE, mCurrentRecord.phoneNumber);
	            oldConstantIntent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, 3);
	            startActivity(oldConstantIntent);
	    		this.dismiss();
	    		break;
    	}
    }
}