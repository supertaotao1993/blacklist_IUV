package com.gome.blacklist;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gome.blacklist.BlacklistData.BlacklistTable;
import com.gome.blacklist.BlacklistData.RecordlistTable;
import com.gome.blacklist.BlacklistData.WhitelistTable;
import com.gome.blacklist.RecyclerViewCursorViewHolder.OnViewHolderClickListener;
import com.gome.blacklist.RecyclerViewCursorViewHolder.OnViewHolderLongClickListener;
import com.gome.blacklist.utils.ToolUtils;
import com.hct.gios.widget.CheckBoxHCT;

public class InterceptRecordAdapter extends RecyclerViewCursorAdapter<RecyclerViewCursorViewHolder> {

	public static final String[] PROJECTION = new String[] {
			BaseColumns._ID,
			RecordlistTable.PHONE_NUMBER,
			RecordlistTable.DISPLAY_NAME,
			RecordlistTable.TYPE,
			RecordlistTable.BODY,
			RecordlistTable.PDU,
			RecordlistTable.FORMAT,
			RecordlistTable.SUBID,
			RecordlistTable.READ,
			RecordlistTable.LOCATION,
			RecordlistTable.TIME
    };

	private boolean isPickMode = false;
	private OnViewHolderClickListener mClickListener;
	private OnViewHolderLongClickListener mLongClickListener;

    /**
     * Constructor
     *
     * @param context The Context the Adapter is displayed in.
     */
    public InterceptRecordAdapter(Context context, boolean pickMode, OnViewHolderClickListener clickListener, OnViewHolderLongClickListener longClickListener) {
        super(context);
        this.isPickMode = pickMode;
        this.mClickListener = clickListener;
        this.mLongClickListener = longClickListener;
        setupCursorAdapter(null, 0, R.layout.intercept_record_list_item, false);
    }

    @Override
    public RecyclerViewCursorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecordViewHolder(mCursorAdapter
                .newView(mContext, mCursorAdapter.getCursor(), parent), mClickListener, mLongClickListener);
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

    public Record getRecordByPosition(int position) {
        Cursor cursor = mCursorAdapter.getCursor();
        int savedPos = cursor.getPosition();
        if (cursor.moveToPosition(position)) {
            Record detail = new Record(cursor);
            cursor.moveToPosition(savedPos);
            return detail;
        }
        throw new IllegalArgumentException("Could not move to position(" + position + ")");
    }

    private boolean isRecordSelected(Record record) {
    	if (isPickMode) {
    		return ((InterceptMultiPickActivity) mContext).isRecordSelected(record);
    	}
    	return false;
    }

    @Override
    public void swapCursor(Cursor cursor) {
        if (null == cursor) {
            return;
        }
        super.swapCursor(cursor);
    }

    public class RecordViewHolder extends RecyclerViewCursorViewHolder {

        private final TextView mNameNumber;
        private final TextView mTime;
        private final TextView mSmsContent;
        private final TextView mCategory;
        private final TextView mAttribute;
        private final CheckBoxHCT mCheckbox;

        /**
         * Constructor
         *
         * @param view The root view of the ViewHolder
         */
        public RecordViewHolder(View view, OnViewHolderClickListener listener, OnViewHolderLongClickListener longListener) {
            super(view, listener, longListener);
            mNameNumber = (TextView) view.findViewById(R.id.record_name_number);
            mTime = (TextView) view.findViewById(R.id.record_time);
            mSmsContent = (TextView) view.findViewById(R.id.record_sms_content);
            mCategory = (TextView) view.findViewById(R.id.record_category);
            mAttribute = (TextView) view.findViewById(R.id.record_attribute);
            mCheckbox = (CheckBoxHCT) view.findViewById(R.id.intercept_record_checkable);
        }

        @Override
        public void bindCursor(Cursor cursor) {
            Record record = new Record(cursor);
            mCheckbox.setChecked(isRecordSelected(record));
            mCheckbox.setVisibility(isPickMode ? View.VISIBLE : View.GONE);
            mNameNumber.setText(ToolUtils.getContactNameNumber(record.displayName, record.phoneNumber));
            String time = ToolUtils.formatTimeWithHourAndMinute(Long.parseLong(record.time));
            mTime.setText(time);
            if (record.type == 1) { // SMS
                mSmsContent.setVisibility(View.VISIBLE);
                mSmsContent.setText(record.body);
            } else { // CALL
                mSmsContent.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(record.location)) {
                mAttribute.setText(R.string.unknown_location);
            } else {
                mAttribute.setText(record.location);
            }
            int category = BlacklistUtils.getNumberCategory(mContext, record.phoneNumber);
            if (category == BlacklistTable.CATEGORY) {
                mCategory.setText(R.string.phone_number_category_blacklist);
            } else if (category == WhitelistTable.CATEGORY) {
                mCategory.setText(R.string.phone_number_category_whitelist);
            } else {
                mCategory.setText(null);
            }
        }
    }
}
