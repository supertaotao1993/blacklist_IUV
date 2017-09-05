package com.gome.blacklist;

import com.gome.blacklist.BlacklistData.RecordlistTable;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

public class Contact implements Parcelable {
	public int _id;
	public String phoneNumber;
    public String displayName;

    public Contact() {}

    public Contact(Parcel in) {
    	_id = in.readInt();
    	phoneNumber = in.readString();
    	displayName = in.readString();
    }
    
    public Contact(Cursor cursor) {
        _id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
        phoneNumber = cursor.getString(cursor.getColumnIndex(RecordlistTable.PHONE_NUMBER));
        displayName = cursor.getString(cursor.getColumnIndex(RecordlistTable.DISPLAY_NAME));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeInt(_id);
        out.writeString(phoneNumber);
        out.writeString(displayName);
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }

        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }
    };
}
