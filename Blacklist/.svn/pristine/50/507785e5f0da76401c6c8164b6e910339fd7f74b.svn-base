package com.gome.blacklist;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gome.blacklist.BlacklistData.BlacklistTable;
import com.gome.blacklist.utils.ToolUtils;
import com.hct.gios.widget.CheckBoxHCT;

public class BlackListAdapter extends RecyclerViewCursorAdapter<RecyclerViewCursorViewHolder> {

	public static final String[] PROJECTION = new String[] {
			BaseColumns._ID,
			BlacklistTable.PHONE_NUMBER,
			BlacklistTable.DISPLAY_NAME
    };

    /**
     * Constructor
     *
     * @param context The Context the Adapter is displayed in.
     */
    public BlackListAdapter(Context context) {
        super(context);
        setupCursorAdapter(null, 0, R.layout.contact_list_item, false);
    }

    @Override
    public RecyclerViewCursorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactViewHolder(mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent),
        		(BlackListManagementActivity) mContext, (BlackListManagementActivity) mContext);
    }

    @Override
    public void onBindViewHolder(RecyclerViewCursorViewHolder holder, int position) {
        // Move cursor to the this position
        mCursorAdapter.getCursor().moveToPosition(position);

        // Set the ViewHolder
        setViewHolder(holder);

        // Bind this view
        mCursorAdapter.bindView(null, mContext, mCursorAdapter.getCursor());
    }
    
    public Contact getContactByPosstion(int position) {
        Cursor cursor = getCursor();
        int savedPos = cursor.getPosition();
        if (cursor.moveToPosition(position)) {
            Contact contact = new Contact(cursor);
            cursor.moveToPosition(savedPos);
            return contact;
        }
        throw new IllegalArgumentException("Could not move to position(" + position + ")");
    }

    @Override
    public void swapCursor(Cursor cursor) {
        if (null == cursor) {
            return;
        }
        super.swapCursor(cursor);
    }
    
    protected boolean inActionMode() {
    	return ((BlackListManagementActivity) mContext).inActionMode();
    }
    
    protected boolean isContactSelected(Contact contact) {
    	return ((BlackListManagementActivity) mContext).isContactSelected(contact);
    }

    public class ContactViewHolder extends RecyclerViewCursorViewHolder {

        private final TextView mNameNumber;
        private final TextView mInterceptDetail;
        private final CheckBoxHCT mCheckable;

        /**
         * Constructor
         *
         * @param view The root view of the ViewHolder
         */
        public ContactViewHolder(View view, OnViewHolderClickListener listener, OnViewHolderLongClickListener longListener) {
            super(view, listener, longListener);
            mNameNumber = (TextView) view.findViewById(R.id.contact_name_number);
            mInterceptDetail = (TextView) view.findViewById(R.id.contact_intercept_detail);
            mCheckable = (CheckBoxHCT) view.findViewById(R.id.contact_checkable);
        }

        @Override
        public void bindCursor(Cursor cursor) {
        	Contact contact = new Contact(cursor);
    	    mNameNumber.setText(ToolUtils.getContactNameNumber(contact.displayName, contact.phoneNumber));
			int count[] = BlacklistUtils.getCountByNumber(mContext, contact.phoneNumber);
			final String detail = mContext.getResources().getString(R.string.intercept_count_detail,
                    count[1], count[0]);
        	mInterceptDetail.setText(detail);
        	mCheckable.setChecked(isContactSelected(contact));
        	mCheckable.setVisibility(inActionMode() ? View.VISIBLE : View.GONE);
        	mInterceptDetail.setVisibility(inActionMode() ? View.VISIBLE : View.GONE);
        }
    }
}
