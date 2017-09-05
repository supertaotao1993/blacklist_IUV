/* Copyright Statement:
 *  newly created for blacklist ops by wenpd on 2017/03/20 
 */

package com.gome.blacklist;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SqliteWrapper;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemProperties;
import android.os.UserManager;
import android.provider.BaseColumns;
import android.provider.CallLog;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.Settings;
import android.provider.Telephony.Sms;
import android.provider.Telephony.Sms.Inbox;
import android.provider.Telephony.Threads;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.gome.blacklist.BlacklistData.BlacklistTable;
import com.gome.blacklist.BlacklistData.WhitelistTable;
import com.gome.blacklist.utils.PreferenceUtils;
import com.mediatek.geocoding.GeoCodingQuery;
import com.gome.blacklist.BlacklistData.RecordlistTable;

import java.util.ArrayList;
/**
 * BlacklistUtils.
 */
public class BlacklistUtils {

    private static final String TAG = "BlacklistUtils";

    private static final Uri BLACKLIST_URI = BlacklistTable.CONTENT_URI;
	private static final Uri WHITELIST_URI = WhitelistTable.CONTENT_URI;
	private static final Uri RECORDLIST_URI = RecordlistTable.CONTENT_URI;
    private static final Uri CONTACTS_URI = Data.CONTENT_URI;
    private static final String[] BLACKLIST_PROJECTION = {
            BaseColumns._ID,
            BlacklistTable.DISPLAY_NAME,
            BlacklistTable.PHONE_NUMBER
    };
    private static final String[] WHITELIST_PROJECTION = {
            BaseColumns._ID,
            WhitelistTable.DISPLAY_NAME,
            WhitelistTable.PHONE_NUMBER
    };

    // regular expression for removing '.' '-' and ' '
    private static final String SPLIT_CHARS_EXP = new String("[.-]");
    private static final String WHITESPACE_CHAR_EXP = new String("\\s");

    private static final String[] CONTACTS_PROJECTION = new String[] {
        Phone.NUMBER,
        Phone.DISPLAY_NAME
    };

    private final static ArrayList<SyncWithContactsCallback> mSyncCallbacks =
                                new ArrayList<SyncWithContactsCallback>();
    private static SyncWithContactsTask sSyncTask = null;

	public static final int BLOCK_TYPE_CONTACT = 1;
	public static final int BLOCK_TYPE_WHITE = 2;
	public static final int BLOCK_TYPE_ALL = 3;

	/**
	* type : 1-blacklist, 2-white-list
	*/
    public static Uri insertNumber(Context context, String name, String number, int type, boolean needToast) {
        ContentValues values = new ContentValues();

		number = number.replaceAll(" ", "");
        values.put(BlacklistTable.PHONE_NUMBER, number);

        if (name != null && !name.isEmpty()) {
            values.put(BlacklistTable.DISPLAY_NAME, name);
        }
		if(getCurrentUserId(context) != 0) {
			values.put(BlacklistTable.USER_MODE, 1);
		}

		ContentResolver resolver = context.getContentResolver();
		Uri uri = null;
        if(type == 1) {
            uri = resolver.insert(BLACKLIST_URI, values);
        } else {
            uri = resolver.insert(WHITELIST_URI, values);
        }
        log("insertNumber: " + name + ", " + number);
		if(uri != null && needToast) {
			if(type == 1) {
			    Toast.makeText(context, R.string.add_black_number_success, Toast.LENGTH_SHORT).show();
			} else {
			    Toast.makeText(context, R.string.add_white_number_success, Toast.LENGTH_SHORT).show();
			}
		} else if(uri == null && needToast){
			if(type == 1) {
			    Toast.makeText(context, R.string.add_black_number_fail, Toast.LENGTH_SHORT).show();
			} else {
			    Toast.makeText(context, R.string.add_white_number_fail, Toast.LENGTH_SHORT).show();
			}
		}
		return uri;
    }

	/**
	* type : 1-blacklist, 2-white-list
	*/
    public static int deleteNumber(Context context, String number, int type, boolean needToast) {
        number = number.replaceAll(" ", "");
        String where = BlacklistTable.PHONE_NUMBER + " = " + number;

		ContentResolver resolver = context.getContentResolver();
		int ret = 0;
        if(type == 1) {
            ret = resolver.delete(BLACKLIST_URI, where, null);
        } else {
            ret = resolver.delete(WHITELIST_URI, where, null);
        }
		if(ret > 0 && needToast) {
			if(type == 1) {
			    Toast.makeText(context, R.string.del_black_number_success, Toast.LENGTH_SHORT).show();
			} else {
			    Toast.makeText(context, R.string.del_white_number_success, Toast.LENGTH_SHORT).show();
			}
		} else if(ret < 1 && needToast){
			if(type == 1) {
			    Toast.makeText(context, R.string.del_black_number_fail, Toast.LENGTH_SHORT).show();
			} else {
			    Toast.makeText(context, R.string.del_white_number_fail, Toast.LENGTH_SHORT).show();
			}
		}
		return ret;
    }

    protected static void importFromContacts(ContentResolver resolver, final long[] ids) {
        if (ids == null || ids.length <= 0) {
            return;
        }

        StringBuilder selection = new StringBuilder(Phone._ID + " in (");
        for (long id : ids) {
            selection.append(Long.toString(id));
            selection.append(',');
        }
        selection.deleteCharAt(selection.length() - 1);
        selection.append(')');

        log(selection.toString());
        Cursor cursorContact = resolver.query(CONTACTS_URI, CONTACTS_PROJECTION,
                                        selection.toString(), null, null);
        if (cursorContact == null) {
            return;
        }

        try {
            cursorContact.moveToFirst();
            while (!cursorContact.isAfterLast()) {
                String number = cursorContact
                        .getString(cursorContact.getColumnIndexOrThrow(CONTACTS_PROJECTION[0]));
                String name = cursorContact
                        .getString(cursorContact.getColumnIndexOrThrow(CONTACTS_PROJECTION[1]));
                if (number == null || number.isEmpty()) {
                    log("cursor is null or empty !");
                } else {
                    ContentValues values = new ContentValues();
                    values.put(BlacklistTable.PHONE_NUMBER, number);
                    if (name != null && !name.isEmpty()) {
                        values.put(BlacklistTable.DISPLAY_NAME, name);
                    }

                    resolver.insert(BLACKLIST_URI, values);
                }
                cursorContact.moveToNext();
           }
        } finally {
            cursorContact.close();
        }
    }

	/**
	* type : 1-blacklist, 2-white-list, 3-record-list
	*/
    public static int deleteMembers(ContentResolver resolver, String where, int type) {
    	if(type == 1) {
           return resolver.delete(BLACKLIST_URI, where, null);
    	} else if(type == 2) {
           return resolver.delete(WHITELIST_URI, where, null);
    	} else {
           return resolver.delete(RECORDLIST_URI, where, null);
    	}
    }

	public static void deleteMembers(ContentResolver resolver, ArrayList<Long> ids, int type) {
		if(ids == null || ids.size() == 0) {
			return;
		}
		StringBuilder idBuilder = new StringBuilder();
		for (long id : ids) {
			idBuilder.append(id);
			idBuilder.append(",");
		}
		if (idBuilder.length() > 1) {
			idBuilder.deleteCharAt(idBuilder.length() - 1);
		}
        deleteMembers(resolver, "_id in (" + idBuilder.toString() + ")", type);
    }

    /**
     * SyncWithContactsCallback.
     */
    public interface SyncWithContactsCallback {
        /**
         * onUpdatedWithContacts.
         * @param result boolean
         */
        void onUpdatedWithContacts(boolean result);
    }

    protected static void startSyncWithContacts(ContentResolver resolver,
                                                SyncWithContactsCallback cb) {
        if (sSyncTask != null && sSyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            sSyncTask.cancel(true);
        }

        if (!mSyncCallbacks.contains(cb)) {
            log("add sync callback");
            if (mSyncCallbacks.size() > 0) {
                log("remove all sync callback");
                mSyncCallbacks.clear();
            }

            mSyncCallbacks.add(cb);
        }

        if (sSyncTask == null || sSyncTask.getStatus() == AsyncTask.Status.FINISHED) {
            sSyncTask = new SyncWithContactsTask();

            log("Start to sync with Contacts");
            sSyncTask.execute(resolver);
        }
    }

    protected static void cancelSyncWithContacts(SyncWithContactsCallback cb) {
        if (mSyncCallbacks.contains(cb)) {
            log("cancel to sync with Contacts, remove callback");
            mSyncCallbacks.remove(cb);
        }

        if (mSyncCallbacks.size() == 0) {
            if (sSyncTask != null && sSyncTask.getStatus() == AsyncTask.Status.RUNNING) {
                sSyncTask.cancel(true);
            }
        }
    }

    /**
     * SyncWithContactsTask.
     * used to sync with contacts
     */
    public static class SyncWithContactsTask extends AsyncTask<ContentResolver, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(ContentResolver... params) {
            log("SyncWithContactsTask doInBackground");
            Integer ret = 0;
            if (syncwithContacts(params[0])) {
                ret = 1;
            }

            return ret;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (!this.isCancelled()) {
                log("sync contacts done: " + result);

                for (SyncWithContactsCallback cb : mSyncCallbacks) {
                    cb.onUpdatedWithContacts(result == 1 ? true : false);
                }
            }
        }
    }

    private static boolean syncwithContacts(ContentResolver resolver) {
        boolean ret = false;
        ArrayList<String> contactsNames = new ArrayList<String>();

        Cursor blackListCusror = resolver.query(BLACKLIST_URI, BLACKLIST_PROJECTION,
                                            null, null, null);

        log("syncwithContacts ++");

        try {
            if (blackListCusror == null || blackListCusror.getCount() == 0) {
                log("blacklist is empty");
                return ret;
            }

            //blackListCusror.moveToFirst();
            while (blackListCusror.moveToNext()) {
                String number = blackListCusror
                        .getString(blackListCusror.getColumnIndexOrThrow(BLACKLIST_PROJECTION[2]));
                String name = blackListCusror
                        .getString(blackListCusror.getColumnIndexOrThrow(BLACKLIST_PROJECTION[1]));
                String id = blackListCusror
                        .getString(blackListCusror.getColumnIndexOrThrow(BLACKLIST_PROJECTION[0]));

                Cursor contactsCursor = resolver.query(
                        Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number)),
                                new String[] {PhoneLookup.DISPLAY_NAME}, null, null, null);
                try {
                    if (contactsCursor == null || contactsCursor.getCount() == 0) {
                        log("Contacts does not contain the number: " + number);
                        continue;
                    }

                    contactsCursor.moveToFirst();

                    contactsNames.clear();
                    while (!contactsCursor.isAfterLast()) {
                        String contactName = contactsCursor.getString(0);
                        contactsNames.add(contactName);
                        log("contacts name: " + contactName + ", " + number);
                        contactsCursor.moveToNext();
                    }

                    if (!contactsNames.contains(name)) {
                        name = contactsNames.get(0);
                        ContentValues values = new ContentValues();
                        values.put(BLACKLIST_PROJECTION[1], name);
                        log("contacts name: " + name + ", " + number + ",  " + id);

                        resolver.update(Uri.withAppendedPath(BLACKLIST_URI, Uri.encode(id)),
                                        values, null, null);
                        ret = true;
                    }
                } finally {
                    contactsCursor.close();
                }

            }
        } finally {
            blackListCusror.close();
            log("syncwithContacts --");
        }

        return ret;
    }

    /**
     * buildQueryNubmer.
     * @param number String
     * @return String
     */
    public static String buildQueryNubmer(String number) {
        StringBuilder sb = new StringBuilder();

        sb.append("\'");
		number = number.replaceAll(" ", "");
        sb.append(number);
        sb.append("\'");

        log("buildQueryNubmer:" + number);

        return sb.toString();
    }

    /**
     * removeSpeicalChars.
     * @param number String
     * @return String
     */
    public static String removeSpeicalChars(String number) {
        log("removeSpeicalChars, befor:" + number);

        String ret = number.replaceAll(WHITESPACE_CHAR_EXP, "");
        ret = ret.replaceAll(SPLIT_CHARS_EXP, "");
        log("removeSpeicalChars, after:" + ret);

        return ret;
    }

	public static boolean isInBlackList(Context context, String number) {
		return checkNumberExist(context,number, 1);
	}

    public static boolean isInWhiteList(Context context, String number) {
        return checkNumberExist(context,number, 2);
    }

	/**
	* to check if it is number's in Contacts
	* @param context the context used to get the SystemService
	* @result  true if number is in database, otherwise false
	*/
	public static boolean isInContacts(Context context, String number) {
		Cursor cursor = context.getContentResolver().query(
				Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number)),
                new String[]{ PhoneLookup.NUMBER}, null, null, null);
		boolean exist = false;
		if(cursor != null) {
			try {
				if (cursor.moveToNext()) {
					exist = true;
				}
			}finally{
				cursor.close();
			}
		}
		log(number + " exist:" + exist);
		return exist;
	}

	/**
	* get Blacklist rule:
	* 1--only block number not in contacts 
	* 2--block number not in white-list 
	* 3--block all
	*/
	public static boolean getBlacklistRule(final Context context, int type, int subid) {
		int simSet = Settings.System.getIntForUser(context.getContentResolver(),PreferenceUtils.KEY_SIM_SET, 0, 0);
		String settingName = null;
		if(simSet == 0) {
			settingName = PreferenceUtils.KEY_STRANGER_ALL;
			if(type == BLOCK_TYPE_WHITE) {
				settingName = PreferenceUtils.KEY_ONLY_WHITE_ALL;
			} else if(type == BLOCK_TYPE_ALL) {
				settingName = PreferenceUtils.KEY_ALL_CONTACTS_ALL;
			}
		} else {
			int slotid = SubscriptionManager.getSlotId(subid);
			if(slotid == 0) {
				settingName = PreferenceUtils.KEY_STRANGER_SIM1;
				if(type == BLOCK_TYPE_WHITE) {
					settingName = PreferenceUtils.KEY_ONLY_WHITE_SIM1;
				} else if(type == BLOCK_TYPE_ALL) {
					settingName = PreferenceUtils.KEY_ALL_CONTACTS_SIM1;
				}
			} else if(slotid == 1) {
				settingName = PreferenceUtils.KEY_STRANGER_SIM2;
				if(type == BLOCK_TYPE_WHITE) {
					settingName = PreferenceUtils.KEY_ONLY_WHITE_SIM2;
				} else if(type == BLOCK_TYPE_ALL) {
					settingName = PreferenceUtils.KEY_ALL_CONTACTS_SIM2;
				}
			}
		}
		if(settingName == null) {
			log("getBlacklistRule-settingName is null");
			return false;
		}
		log("getBlacklistRule-settingName:" + settingName);
		return (1 == Settings.System.getIntForUser(context.getContentResolver(), settingName, 0, 0));
	}

	/**
	* to record the blocked SMS to database
	* @param context the context used to get the ContentResolver
	* @param intent, Intent of the blocked SMS
	*/
	@TargetApi(23)
	public static void recordSms(Context context, Intent intent) {
		String format = intent.getStringExtra("format");
		int subId = intent.getIntExtra("subid", SubscriptionManager.getDefaultSmsSubId());
		Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
		int pduCount = messages.length;
		SmsMessage[] msgs = new SmsMessage[pduCount];
		ContentValues values = new ContentValues();
		values.put(RecordlistTable.TYPE, 1);
		values.put(RecordlistTable.FORMAT, format);
		values.put(RecordlistTable.SUBID, subId);
		StringBuilder body = new StringBuilder();
		for (int i = 0; i < pduCount; i++) {
			byte[] pdu = (byte[]) messages[i];
			if(i == 0) values.put(RecordlistTable.PDU, pdu);
			msgs[i] = SmsMessage.createFromPdu(pdu, format);
			body.append(msgs[i].getMessageBody());
		}
		String address = msgs[0].getOriginatingAddress();
		values.put(RecordlistTable.PHONE_NUMBER, address);
		values.put(RecordlistTable.TIME, msgs[0].getTimestampMillis());
		values.put(RecordlistTable.BODY, body.toString());
		String cityName = getGeoDescription(context, address);
		if(!TextUtils.isEmpty(cityName)) {
			values.put(RecordlistTable.LOCATION, cityName);
		}
		if(context.getContentResolver().isSecreted(address)) {
			values.put(RecordlistTable.USER_MODE, 1);
		}
		Uri uri= context.getContentResolver().insert(RECORDLIST_URI, values);
		log("recordSms-Uri:" + uri);
		//testRestoreItem(context, 1);
	}
	
	/**
	* to record the blocked call to database
	* @param context the context used to get the ContentResolver
	* @param intent, Intent of the blocked call
	*/
	public static void recordCall(Context context, Intent intent) {
		ContentValues values = new ContentValues();
		String number  = intent.getStringExtra("Number");
		values.put(RecordlistTable.TYPE, 2);
		values.put(RecordlistTable.SUBID, intent.getIntExtra("subid", SubscriptionManager.getDefaultSmsSubId()));
		values.put(RecordlistTable.PHONE_NUMBER, number);
		values.put(RecordlistTable.TIME, System.currentTimeMillis());
		values.put(RecordlistTable.FORMAT, intent.getStringExtra("format"));
		String cityName = getGeoDescription(context, number);
		if(!TextUtils.isEmpty(cityName)) {
			values.put(RecordlistTable.LOCATION, cityName);
		}
		if(context.getContentResolver().isSecreted(number)) {
			values.put(RecordlistTable.USER_MODE, 1);
		}
		Uri uri= context.getContentResolver().insert(RECORDLIST_URI, values);
		log("recordCall-Uri:" + uri);
		//testRestoreItem(context, 2);
	}

	/**
	* to restore a item in record list according the id
	* @param context the context used to get the ContentResolver
	* @param id, id in database of record
	* @param showToast, whether show the toast
	* @result  The URI of the restored item in database of SMS
	*/
	public static Uri restoreItemById(Context context, int id, boolean showToast) {
		String selection = BaseColumns._ID + " = " + id;
		Cursor cursor= context.getContentResolver().query(RECORDLIST_URI, null, selection, null, null);
		if(cursor != null) {
			try {
				if(cursor.moveToNext()) {
					return restoreRecordItem(context, cursor, showToast);
				}
			} finally {
				cursor.close();
			}
		}
		return null;
	}
	
	/**
	* to restore a item in record list according the number
	* @param context the context used to get the ContentResolver
	* @param number, the phone number
	* @param showToast, whether show the toast
	* @result  The URI of the restored item in database of SMS
	*/
	public static int restoreItemByNumber(Context context, String number) {
		Uri queryUri = Uri.withAppendedPath(RECORDLIST_URI, number);
		String selection = RecordlistTable.TYPE + " = " + RecordlistTable.TYPE_SMS;
		Cursor cursor = context.getContentResolver().query(queryUri, null, selection, null, null);
		int count = 0;
		if(cursor != null) {
			try {
				while(cursor.moveToNext()) {
					if(null != restoreRecordItem(context, cursor, false)) {
						++count;
					}
				}
			} finally {
				cursor.close();
			}
		}
		return count;
	}

	/**
	* to restore a item in record list
	* @param context the context used to get the ContentResolver
	* @param cursor, Cursor from adapter of record-list
	* @param showToast, whether show the toast
	* @result  The URI of the restored item in database of SMS
	*/
	public static Uri restoreRecordItem(Context context, Cursor cursor, boolean showToast) {
		int type = 0;
		long id = 0;
		Uri retUri = null;
		try{
			type = cursor.getInt(cursor.getColumnIndexOrThrow(RecordlistTable.TYPE));
			id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID));
		} catch(Exception ex) {
			log("get TYPE ERROR:" + ex);
			return null;
		}
		if(type == 1) {
			retUri = restoreToSms(context, cursor);
		} else if(type == 2) {
			//retUri = restoreToCallLog(context, cursor);
			return null;//no need to restore call items
		}
		if(retUri != null) {
			ArrayList<Long> ids = new ArrayList<Long>();
			ids.add(id);
			deleteMembers(context.getContentResolver(), ids, 3);
			if(showToast) {
				Toast.makeText(context, R.string.sms_recovery_success, Toast.LENGTH_SHORT).show();
			}
		} else {
			if(showToast) {
				Toast.makeText(context, R.string.sms_recovery_fail, Toast.LENGTH_SHORT).show();
			}
		}
			    		
		return retUri;
    }

	/**
	* to restore a item to SMS
	* @param context the context used to get the ContentResolver
	* @param cursor, Cursor from adapter of record-list
	* @result  The URI of the restored item in database of SMS
	*/
	@TargetApi(23)
	private static Uri restoreToSms(Context context, Cursor cursor) {
		if (!SmsWriteOpUtils.isWriteEnabled(context)) {
            SmsWriteOpUtils.setWriteEnabled(context, true);
		}
		//String address = null;
		String body = null;
		String format = null;
		int subid = 0;
		//long time = 0;
		byte[] smsPdu = null;
		try{
			//address = cursor.getString(cursor.getColumnIndexOrThrow(RecordlistTable.PHONE_NUMBER));
			body = cursor.getString(cursor.getColumnIndexOrThrow(RecordlistTable.BODY));
			format = cursor.getString(cursor.getColumnIndexOrThrow(RecordlistTable.FORMAT));
			subid = cursor.getInt(cursor.getColumnIndexOrThrow(RecordlistTable.SUBID));
			//time = cursor.getLong(cursor.getColumnIndexOrThrow(RecordlistTable.TIME));
			smsPdu = cursor.getBlob(cursor.getColumnIndex(RecordlistTable.PDU));
		}catch(Exception ex) {
			log("get cursor ERROR:" + ex);
			return null;
		}
		SmsMessage sms = SmsMessage.createFromPdu(smsPdu, format);
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = extractContentValues(sms);
        values.put(Sms.ERROR_CODE, 0);
		values.put("sub_id", subid);
		values.put(Inbox.BODY, body);
		values.put("sms_pdu", sms.getPdu());
		values.put(Sms.REPLY_PATH_PRESENT, 0);
		Uri restoredUri = SqliteWrapper.insert(context, resolver, Inbox.CONTENT_URI, values);
		log("restoreToSms-Uri:" + restoredUri);
		return restoredUri;
	}

	/**
     * Extract all the content values except the body from an SMS
     * message.
     */
    @SuppressLint("UseValueOf")
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private static ContentValues extractContentValues(SmsMessage sms) {
        ContentValues values = new ContentValues();
        values.put(Inbox.ADDRESS, sms.getOriginatingAddress());
        values.put(Inbox.DATE_SENT, Long.valueOf(sms.getTimestampMillis()));
        values.put(Inbox.DATE, new Long(System.currentTimeMillis()));
        values.put(Inbox.PROTOCOL, sms.getProtocolIdentifier());
        values.put(Inbox.READ, 0);
        values.put(Inbox.SEEN, 0);
        if (sms.getPseudoSubject().length() > 0) {
            values.put(Inbox.SUBJECT, sms.getPseudoSubject());
        }
        values.put(Inbox.REPLY_PATH_PRESENT, sms.isReplyPathPresent() ? 1 : 0);
        values.put(Inbox.SERVICE_CENTER, sms.getServiceCenterAddress());
        return values;
    }

	/**
	* to get unread count
	* @param context the context used to get the ContentResolver
	* @param type, 1:SMS, 2:call
	* @result  count of unread records
	*/
	public static int getUnreadNumber(Context context, int type) {
		int retCount = 0;
		String selection = RecordlistTable.READ + " = 0 AND TYPE = " + type;
		Cursor cursor = context.getContentResolver().query(RECORDLIST_URI, null, selection, null, null);
		if(cursor != null) {
			try {
				retCount = cursor.getCount();
			} finally {
				cursor.close();
			}
		}
		return retCount;
	}

	/**
	* to mark all item as read when activity exit
	* @param context the context used to get the ContentResolver
	* @param type, 1:SMS, 2:call
	*/
	public static void markAllAsRead(Context context, int type) {
		ContentValues values = new ContentValues();
        values.put(RecordlistTable.READ, 1);
		String where = " TYPE = " + type;
		context.getContentResolver().update(RECORDLIST_URI, values, where, null);
	}

	/**
	* to get unread count
	* @param context the context used to get the ContentResolver
	* @param number, phone number
	* @result  count array of number's record count, 0-SMS, 1-call
	*/
	public static int[] getCountByNumber(Context context, String number) {
		int count[] = {0, 0};
		Uri queryUri = Uri.withAppendedPath(RECORDLIST_URI, number);
		Cursor cursor = context.getContentResolver().query(queryUri, new String[]{RecordlistTable.TYPE}, null, null, null);
		int type = 0;
		if(cursor != null) {
			try {
				while(cursor.moveToNext()) {
					type = cursor.getInt(0);
					if(type == 1) {
						++count[0];
					} else if(type == 2) {
						++count[1];
					}
				}
			} finally {
				cursor.close();
			}
		}
		return count;
	}

	/**
	* to get unread count
	* @param context the context used to get the ContentResolver
	* @param type, 1-blacklist, 2-whitelist, 3-recordlist
	* @result  count of rows
	*/
	public static int getDbCount(Context context, int type) {
		int count = 0;
		Uri queryUri = null;
		if(type == 1) {
			queryUri = BLACKLIST_URI;
		} else if(type == 2){
			queryUri = WHITELIST_URI;
		} else {
			queryUri = RECORDLIST_URI;
		}
		Cursor cursor = context.getContentResolver().query(queryUri, null, null, null, null);
		if(cursor != null) {
			try {
				count = cursor.getCount();
			} finally {
				cursor.close();
			}
		}
		return count;
	}

	    /**
     * @return a geographical description string for the specified number.
     * @see com.android.i18n.phonenumbers.PhoneNumberOfflineGeocoder
     */
    /*
     * Google code:
    private static String getGeoDescription(Context context, String number) {
    */
    private static String getGeoDescription(Context context, String number) {
        if (TextUtils.isEmpty(number)) {
            return null;
        }

        if (SystemProperties.get("ro.mtk_phone_number_geo").equals("1")) {
            GeoCodingQuery geoCodingQuery = GeoCodingQuery.getInstance(context);
            String cityName = geoCodingQuery.queryByNumber(number);
            Log.v(TAG, "[GeoCodingQuery] cityName = " + cityName);
            if (!TextUtils.isEmpty(cityName)) {
                return cityName;
            }
        }

        return null;
    }

    /**
     * Get the number's category, maybe in whitelist, maybe in blacklist, maybe both not.
     * 
     * @param context, the context used to get the ContentResolver
     * @param number, phone number
     * @return the category for this number
     */
    public static int getNumberCategory(Context context, String number) {
        int category = 0;
        if (isInBlackList(context, number)) {
            category = BlacklistData.BlacklistTable.CATEGORY;
        } else if (isInWhiteList(context, number)) {
            category = BlacklistData.WhitelistTable.CATEGORY;
        }
        return category;
    }

	/**
	* to check a number if exist in white or black list
	* @param context the context used to get the ContentResolver
	* @param number, phone number
	* @param type, 1:SMS, 2:call
	* @result  true if exist
	*/
	public static boolean checkNumberExist(Context context, String number, int type) {
		Uri queryUri = null;
		if(type == 1) {
			queryUri = Uri.withAppendedPath(BLACKLIST_URI, number);
		} else {
			queryUri = Uri.withAppendedPath(WHITELIST_URI, number);
		}
		Cursor cursor = context.getContentResolver().query(queryUri, null, null, null, null);
		boolean exist = false;
		if(cursor != null) {
			try {
				if(cursor.moveToNext()) {
					exist = true;
				}
			} catch (Exception e) {
				Log.w(TAG, "no GomeBlacklist exit: " + e);;
			} finally {
				cursor.close();
			}
		}
		return exist;
	}

	public static int getCurrentUserId(Context context) {
		UserManager userManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
		if ("TYJW".equals(android.os.SystemProperties.get("persist.gome.branch"))) {
			if(userManager.getSwitchedUserId() == 0) {//for tyjw 0 is secure
				return 10;
			}
			return 0;
		}
        return userManager.getSwitchedUserId();
	}

	public static boolean hasCallLog(final Context context) {
		Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, 
				null, null, "date DESC limit 1");
		if(cursor != null) {
			try {
				if(cursor.moveToNext()) {
					return true;
				}
			} finally {
				cursor.close();
			}
		}
		return false;
	}

	public static boolean hasMessage(final Context context) {
        final Uri allThreadsUri = Threads.CONTENT_URI.buildUpon().appendQueryParameter("simple", "true").build();
		Cursor cursor = context.getContentResolver().query(allThreadsUri, null, 
				null, null, "date DESC limit 1");
		if(cursor != null) {
			try {
				if(cursor.moveToNext()) {
					return true;
				}
			} finally {
				cursor.close();
			}
		}
		return false;
	}
		
    private static void log(String message) {
        Log.d(TAG, "[BlacklistUtils] " + message);
    }
}
