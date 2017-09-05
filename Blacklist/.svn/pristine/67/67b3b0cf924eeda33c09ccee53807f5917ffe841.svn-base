package com.gome.blacklist;

import com.gome.blacklist.BlacklistData.RecordlistTable;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

public class Record implements Parcelable {
	public int _id;
    public int type;
	public String phoneNumber;
    public String displayName;
    public String body;
    public byte[] pdu;
    public String format;
    public String subId;
    public String location;
    public String time;

    public Record() {}

    public Record(Parcel in) {
    	_id = in.readInt();
    	type = in.readInt();
    	phoneNumber = in.readString();
    	displayName = in.readString();
    	body = in.readString();
    	pdu = in.readBlob();
    	format = in.readString();
    	subId = in.readString();
    	location = in.readString();
    	time = in.readString();
    }

    public Record(Cursor cursor) {
        _id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
        type = cursor.getInt(cursor.getColumnIndex(RecordlistTable.TYPE));
        phoneNumber = cursor.getString(cursor.getColumnIndex(RecordlistTable.PHONE_NUMBER));
        displayName = cursor.getString(cursor.getColumnIndex(RecordlistTable.DISPLAY_NAME));
        body = cursor.getString(cursor.getColumnIndex(RecordlistTable.BODY));
        pdu = cursor.getBlob(cursor.getColumnIndex(RecordlistTable.PDU));
        format = cursor.getString(cursor.getColumnIndex(RecordlistTable.FORMAT));
        subId = cursor.getString(cursor.getColumnIndex(RecordlistTable.SUBID));
        location = cursor.getString(cursor.getColumnIndex(RecordlistTable.LOCATION));
        time = cursor.getString(cursor.getColumnIndex(RecordlistTable.TIME));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeInt(_id);
        out.writeInt(type);
        out.writeString(phoneNumber);
        out.writeString(displayName);
        out.writeString(body);
        out.writeBlob(pdu);
        out.writeString(format);
        out.writeString(subId);
        out.writeString(location);
        out.writeString(time);
    }

    public static final Parcelable.Creator<Record> CREATOR = new Creator<Record>() {
        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }

        @Override
        public Record createFromParcel(Parcel in) {
            return new Record(in);
        }
    };
}
