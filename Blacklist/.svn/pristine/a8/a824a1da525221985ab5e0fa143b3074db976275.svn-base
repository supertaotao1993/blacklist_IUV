/* Copyright Statement:
 *  newly created for blacklist ops by wenpd on 2017/03/20 
 */

package com.gome.blacklist;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;

import com.gome.blacklist.BlacklistData.BlacklistTable;
import com.gome.blacklist.BlacklistData.WhitelistTable;
import com.gome.blacklist.BlacklistData.RecordlistTable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * BlacklistProvider.
 */
public class BlacklistProvider extends ContentProvider {

    private static final String TAG = "Blacklist";

    private static final int URL_BLACK_CONTACTS = 1;
    private static final int URL_BLACK_CONTACT_NUMBER = 2;
	private static final int URL_WHITE_CONTACTS = 3;
    private static final int URL_WHITE_CONTACT_NUMBER = 4;
	private static final int URL_RECORDS = 5;
	private static final int URL_RECORDS_NUMBER = 6;
	private static final int URL_CHECK_NUMBER = 7;

    private static final UriMatcher sMATCHER;
    private static final HashMap<String, String> sBlacklistProjection;
	private static final HashMap<String, String> sWhitelistProjection;
	private static final HashMap<String, String> sRecordlistProjection;
    private BlacklistDatabseHelper mDbHelper = null;

    static {
        sMATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        sMATCHER.addURI(BlacklistData.AUTHORITY, "/black_list", URL_BLACK_CONTACTS);
        sMATCHER.addURI(BlacklistData.AUTHORITY, "/black_list/#", URL_BLACK_CONTACT_NUMBER);
        sMATCHER.addURI(BlacklistData.AUTHORITY, "/black_list/*", URL_BLACK_CONTACT_NUMBER);
		sMATCHER.addURI(BlacklistData.AUTHORITY, "/white_list", URL_WHITE_CONTACTS);
        sMATCHER.addURI(BlacklistData.AUTHORITY, "/white_list/#", URL_WHITE_CONTACT_NUMBER);
        sMATCHER.addURI(BlacklistData.AUTHORITY, "/white_list/*", URL_WHITE_CONTACT_NUMBER);
		sMATCHER.addURI(BlacklistData.AUTHORITY, "/record_list", URL_RECORDS);
		sMATCHER.addURI(BlacklistData.AUTHORITY, "/record_list/#", URL_RECORDS_NUMBER);
		sMATCHER.addURI(BlacklistData.AUTHORITY, "/record_list/*", URL_RECORDS_NUMBER);
		sMATCHER.addURI(BlacklistData.AUTHORITY, "/check/#", URL_CHECK_NUMBER);
		sMATCHER.addURI(BlacklistData.AUTHORITY, "/check/*", URL_CHECK_NUMBER);

        sBlacklistProjection = new HashMap<String, String>();
        sBlacklistProjection.put(BaseColumns._ID, BaseColumns._ID);
        sBlacklistProjection.put(BlacklistTable.PHONE_NUMBER, BlacklistTable.PHONE_NUMBER);
        sBlacklistProjection.put(BlacklistTable.DISPLAY_NAME, BlacklistTable.DISPLAY_NAME);

		sWhitelistProjection = new HashMap<String, String>();
        sWhitelistProjection.put(BaseColumns._ID, BaseColumns._ID);
        sWhitelistProjection.put(WhitelistTable.PHONE_NUMBER, WhitelistTable.PHONE_NUMBER);
        sWhitelistProjection.put(WhitelistTable.DISPLAY_NAME, WhitelistTable.DISPLAY_NAME);

		sRecordlistProjection = new HashMap<String, String>();
		sRecordlistProjection.put(BaseColumns._ID, BaseColumns._ID);
		sRecordlistProjection.put(RecordlistTable.PHONE_NUMBER, RecordlistTable.PHONE_NUMBER);
        sRecordlistProjection.put(RecordlistTable.DISPLAY_NAME, RecordlistTable.DISPLAY_NAME);
        sRecordlistProjection.put(RecordlistTable.TYPE, RecordlistTable.TYPE);
        sRecordlistProjection.put(RecordlistTable.PDU, RecordlistTable.PDU);
		sRecordlistProjection.put(RecordlistTable.FORMAT, RecordlistTable.FORMAT);
		sRecordlistProjection.put(RecordlistTable.SUBID, RecordlistTable.SUBID);
		sRecordlistProjection.put(RecordlistTable.BODY, RecordlistTable.BODY);
		sRecordlistProjection.put(RecordlistTable.READ, RecordlistTable.READ);
        sRecordlistProjection.put(RecordlistTable.TIME, RecordlistTable.TIME);
		sRecordlistProjection.put(RecordlistTable.LOCATION, RecordlistTable.LOCATION);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = BlacklistDatabseHelper.getInstance(getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String finalWhere = where;
        String number = null;
        ArrayList<String> numberList = new ArrayList<String>();
        int ret = -1;
        Boolean useOrignalSql = true;
		String table = getTableName(uri);
		if(RecordlistTable.TABLE_NAME.equals(table)) {
			ret = deleteRecord(db, where, whereArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return ret;
		}

        log("[delete]++ uri:" + uri.toString());
        if (where == null && uri.getPathSegments().size() > 0) {
            number = Uri.decode(uri.getLastPathSegment());
            log("[delete] delete by number:" + number);

            //number = BlacklistUtils.buildQueryNubmer(number);
            //finalWhere = BlacklistTable.PHONE_NUMBER + " = " + number;
            numberList.add(number);
            useOrignalSql = false;
        } else if (where != null && !where.isEmpty()) {
            log("[delete] delete by where:" + where + ", " + whereArgs);
            if (where.contains(BlacklistTable.PHONE_NUMBER)) {
                //Case, delete by number
                useOrignalSql = false;
                if (where.contains("=")) {
                    //one number case
                    StringBuilder num = new StringBuilder();
                    if (whereArgs == null) {
                        int i = where.indexOf("=");
                        for (; i < where.length(); i++) {
                            char c = where.charAt(i);
                            if (c == '+' || (c >= '0' && c <= '9')) {
                                num.append(c);
                            }
                        }
                        number = num.toString();
                    } else {
                        int size = whereArgs.length;
                        log("[delete] one number case, whereArgs.size " + size);
                        if (size > 0) {
                            number = whereArgs[0];
                        }
                    }
                    log("[delete] one number case, number:" + number);
                    numberList.add(number);
                } else if (where.contains("in") || where.contains("IN")) {
                    //many numbers case
                    String numbers = null;
                    int i = 0;
                    //StringBuilder num = new StringBuilder();

                    if (whereArgs == null) {
                        i = where.indexOf("(");
                        numbers = where.substring(i + 1, where.length() - 1);
                    } else {
                        int size = whereArgs.length;
                        log("[delete] many numbers case, whereArgs.size " + size);
                        if (size > 0) {
                            numbers = whereArgs[0];
                        }
                    }

                    log("[delete] many numbers case, numbers:" + numbers);
                    if (numbers != null) {
                        String[] sp = numbers.split(",");
                        numberList.addAll(java.util.Arrays.asList(sp));
                        log("[delete] many numbers case, numbers is:" + numberList.toString());
                    } else {
                        useOrignalSql = true;
                    }
                }
            } else {
                /* delete by _id
                 * parameter is illegal
                 */
                log("[delete] maybe delete by _id or parameter is illegal!, just try it");
            }
        }

        /* query data from DB and compare number with PhoneNumberUtils
         * handle internal number (+) case
         * eg. ALPS02113937
         */
        if (useOrignalSql) {
            log("[delete] delete where: " + finalWhere);
            ret = db.delete(table, finalWhere, whereArgs);
        } else {
            Uri checkUri = BlacklistTable.CONTENT_URI;
            if(WhitelistTable.TABLE_NAME.equals(table)) {
                checkUri = WhitelistTable.CONTENT_URI;
            }
            StringBuilder idBuilder = new StringBuilder();
            Cursor blackCursor = query(checkUri, new String[] {BaseColumns._ID,
                                        BlacklistTable.PHONE_NUMBER}, null, null, null);

            try {
                for (String delNumber : numberList) {
                    if (!delNumber.isEmpty()) {
                        while (blackCursor.moveToNext()) {
                            String rawId = blackCursor.getString(0);
                            String rawNumber = blackCursor.getString(1);
                            if (PhoneNumberUtils.compare(delNumber, rawNumber)) {
                                idBuilder.append(rawId);
                                idBuilder.append(",");
                                log("[delete] will delete number:" + rawNumber);
                            }
                        }
                        blackCursor.moveToFirst();
                    }
                }
                if (idBuilder.length() > 1) {
                    idBuilder.deleteCharAt(idBuilder.length() - 1);
                }
            } finally {
                blackCursor.close();
            }

            log("[delete] delete id(s):" + idBuilder.toString());
            ret = db.delete(table, "_id in (" + idBuilder.toString() + ")", null);
        }

        log("[delete]-- ret = " + ret);
		getContext().getContentResolver().notifyChange(uri, null);
        return ret;
    }


    public int deleteRecord(SQLiteDatabase db, String where, String[] whereArgs) {
		log("[delete] delete where: " + where);
		if(BlacklistUtils.getCurrentUserId(getContext())!= 0) {
			if(where != null) {
				where += " AND " + RecordlistTable.USER_MODE + " = 1";
			} else {
				where = RecordlistTable.USER_MODE + " = 1";
			}
		} else {
			if(where != null) {
				where += " AND " + RecordlistTable.USER_MODE + " = 0";
			} else {
				where = RecordlistTable.USER_MODE + " = 0";
			}
		}
        return db.delete(RecordlistTable.TABLE_NAME, where, whereArgs);
    }

    @Override
    public String getType(Uri uri) {
        String type = null;
        final int match = sMATCHER.match(uri);

        switch(match) {
            case URL_BLACK_CONTACTS:
            case URL_WHITE_CONTACTS:
                type = BlacklistTable.CONTENT_TYPE;
                break;

            case URL_BLACK_CONTACT_NUMBER:
            case URL_WHITE_CONTACT_NUMBER:
                type = BlacklistTable.CONTENT_ITEM_TYPE;
                break;

            default:
        }

        return type;
    }

	private String getTableName(Uri uri) {
        String table = null;
        final int match = sMATCHER.match(uri);

        switch(match) {
            case URL_BLACK_CONTACTS:
            case URL_BLACK_CONTACT_NUMBER:
                table = BlacklistTable.TABLE_NAME;
                break;

            case URL_WHITE_CONTACTS:
            case URL_WHITE_CONTACT_NUMBER:
            	table = WhitelistTable.TABLE_NAME;
                break;

            case URL_RECORDS:
            case URL_RECORDS_NUMBER:
            	table = RecordlistTable.TABLE_NAME;
                break;

            default:
        }

        return table;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String savedNumber = "";

        if (!values.containsKey(BlacklistTable.PHONE_NUMBER)) {
                throw new IllegalArgumentException("values inserted doesn't contain Phone_number");
        } else {
            savedNumber = values.getAsString(BlacklistTable.PHONE_NUMBER);
        }

        log("insert number is " + savedNumber);
        if (savedNumber == null || savedNumber.isEmpty()) {
            throw new IllegalArgumentException("Phone_number is null or empty");
        }

		String table = getTableName(uri);
        // chec number if already exist in blacklist
        log("insert number, check blacklist db +");

		Uri checkUri = BlacklistTable.CONTENT_URI;
		boolean checkExist = true;
		int maxCount = BlacklistTable.RECORDS_NUMBER_MAX;
		if(WhitelistTable.TABLE_NAME.equals(table)) {
			checkUri = WhitelistTable.CONTENT_URI;
			maxCount = WhitelistTable.RECORDS_NUMBER_MAX;
		} else if(RecordlistTable.TABLE_NAME.equals(table)) {
			checkUri = RecordlistTable.CONTENT_URI;
			checkExist = false;
			maxCount = RecordlistTable.RECORDS_NUMBER_MAX;
		}

        Cursor blackCursor = query(checkUri, new String[] {BaseColumns._ID,
                                    BlacklistTable.PHONE_NUMBER}, null, null, null);
        try {
            if (blackCursor != null) {
                int count = blackCursor.getCount();
                if (count >= maxCount) {
                    log("records number in db reached to: " + count + " cannot be added.");
                    return null;
                }

                while (checkExist && blackCursor.moveToNext()) {
                    String existId = blackCursor.getString(0);
                    String existNumber = blackCursor.getString(1);
                    if (PhoneNumberUtils.compare(savedNumber, existNumber)) {
                        log("It already exists in db, id: " + existId +
                            " , number is: " + existNumber);
                        return null;
                    }
                }
            }
        } finally {
            blackCursor.close();
            log("insert number, check blacklist db -");
        }

        // check name if exists in contacts
        log("insert number, query contacts db +");
        if (!values.containsKey(BlacklistTable.DISPLAY_NAME)) {
            log("query display name from contacts ");
            Cursor contactsCursor = getContext().getContentResolver().query(
                Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(savedNumber)),
                new String[] {BaseColumns._ID, PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME},
                null, null,
                BaseColumns._ID + " DESC");

            try {
                if (contactsCursor == null || contactsCursor.getCount() == 0) {
                    log("Contacts does not contain the number: " + savedNumber);
                } else {
                    while (contactsCursor.moveToNext()) {
                        String contactNumber = contactsCursor.getString(1);
                        String contactName = contactsCursor.getString(2);
                        if (PhoneNumberUtils.compare(savedNumber, contactNumber)) {
                            log("contacts number matched: " + contactNumber);
                            values.put(BlacklistTable.DISPLAY_NAME, contactName);
                        }
                    }
                }
            } finally {
                contactsCursor.close();
                log("insert number, query contacts db -");
            }
        }

        long rowId = db.insert(table, null, values);
        log("insert db row id: " + rowId);

        if (rowId > 0) {
             Uri resultUri = ContentUris.withAppendedId(checkUri, rowId);
             getContext().getContentResolver().notifyChange(resultUri, null);
             return resultUri;
        }

        return null;
    }

	private void setProjectMap(SQLiteQueryBuilder qb, Uri uri) {
		switch (sMATCHER.match(uri)) {
			case URL_BLACK_CONTACTS:
			case URL_BLACK_CONTACT_NUMBER:
				qb.setProjectionMap(sBlacklistProjection);
				break;
				
            case URL_WHITE_CONTACTS:
			case URL_WHITE_CONTACT_NUMBER:
				qb.setProjectionMap(sWhitelistProjection);
				break;
				
			case URL_RECORDS:
			case URL_RECORDS_NUMBER:
                qb.setProjectionMap(sRecordlistProjection);
				break;

            default:
                break;
        }
	}

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                            String sortOrder) {

        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String table = getTableName(uri);
        qb.setTables(table);
		String trueSelection = null;
		if(!"NONE".equals(selection)) {
			if(BlacklistUtils.getCurrentUserId(getContext())!= 0) {
				trueSelection = BlacklistTable.USER_MODE + " = 1";
			} else {
				trueSelection = BlacklistTable.USER_MODE + " = 0";
			}
			if(!TextUtils.isEmpty(selection)) {
				trueSelection += " AND " + selection;
			}
		}

        if (projection == null) {
            //qb.setProjectionMap(sBlacklistProjection);
            setProjectMap(qb, uri);
        }

        switch (sMATCHER.match(uri)) {
			case URL_BLACK_CONTACTS:
            case URL_WHITE_CONTACTS:
            case URL_RECORDS:
                break;

            case URL_BLACK_CONTACT_NUMBER:
            case URL_WHITE_CONTACT_NUMBER:
                if (uri.getPathSegments().size() > 1) {
                    String number = Uri.decode(uri.getLastPathSegment());
                    log("query by number: " + number);

                    //number = BlacklistUtils.buildQueryNubmer(number);
                    //qb.appendWhere(BlacklistTable.PHONE_NUMBER + "=" + number);
                    Uri idQueryUri = BlacklistTable.CONTENT_URI;
                    if(WhitelistTable.TABLE_NAME.equals(table)) {
						idQueryUri = WhitelistTable.CONTENT_URI;
                    }
					StringBuilder idBuilder = getQueryIdStringBuilder(idQueryUri, number, false);
					if(trueSelection != null) {
						trueSelection += " AND ";
					}
					trueSelection += "_id in (" + idBuilder.toString() + ")";
                }
                break;
				
            case URL_CHECK_NUMBER:
				if (uri.getPathSegments().size() > 1) {
                    String number = Uri.decode(uri.getLastPathSegment());
					int subid = 1;
					if(selection != null) {
						subid = Integer.parseInt(selection);
					}
                    log("check by number: " + number + "; subid=" + selection);
					return checkNumber(number, subid);
                }
                return null;

          case URL_RECORDS_NUMBER:
                if (uri.getPathSegments().size() > 1) {
					String queryNum = Uri.decode(uri.getLastPathSegment());
					log("queryNum: " + queryNum);
					StringBuilder idBuilder = getQueryIdStringBuilder(RecordlistTable.CONTENT_URI, queryNum, true);
					if(trueSelection != null) {
						trueSelection += " AND ";
					}
					trueSelection += "_id in (" + idBuilder.toString() + ")";
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

		String order = BlacklistTable.DEFAULT_SORT_ORDER;
		if(RecordlistTable.TABLE_NAME.equals(table)) {
			order = RecordlistTable.DEFAULT_SORT_ORDER;
		}

		log("query-trueSelection: " + trueSelection);
        Cursor c = qb.query(db, projection, trueSelection, selectionArgs, null, null, order);
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    /**
    * check a number if it need to be blocked by our rules
    * @param number, phone number
    * @param subid, SIM card index
    * @result  null, if not need block, else a MatrixCursor object
    */
    private Cursor checkNumber(String number, int subid) {
		boolean needBlock = false;
		boolean needCheckBlacklst = true;
		if(BlacklistUtils.getBlacklistRule(getContext(), BlacklistUtils.BLOCK_TYPE_ALL, subid)){
			needBlock = true;
			needCheckBlacklst = false;
		} else if(BlacklistUtils.getBlacklistRule(getContext(), BlacklistUtils.BLOCK_TYPE_WHITE, subid)) {
			Cursor cursor = query(WhitelistTable.CONTENT_URI, new String[] {WhitelistTable.PHONE_NUMBER}, "NONE", null, null);
			boolean exist = false;
			if(cursor != null) {
				try {
					while (cursor.moveToNext()) {
						String existNumber = cursor.getString(0);
						 if (PhoneNumberUtils.compare(number, existNumber)) {
						 	exist = true;
							break;
						 }
					}
				}finally{
					cursor.close();
				}
			}
			needBlock = !exist;
			needCheckBlacklst = false;
		} else if(BlacklistUtils.getBlacklistRule(getContext(), BlacklistUtils.BLOCK_TYPE_CONTACT, subid)) {
			Cursor cursor = getContext().getContentResolver().query(
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
			needBlock = !exist;
			if(needBlock) {
				needCheckBlacklst = false;
			}
		} 
		if(needCheckBlacklst) {
			Cursor cursor = query(BlacklistTable.CONTENT_URI, new String[] {BlacklistTable.PHONE_NUMBER}, "NONE", null, null);
			boolean exist = false;
			if(cursor != null) {
				try {
					while (cursor.moveToNext()) {
						String existNumber = cursor.getString(0);
						if(existNumber.endsWith("*")) {
						    if(isSpecailEqual(existNumber, number)){
							    exist = true;
							    break;
						    }
						}else if (PhoneNumberUtils.compare(number, existNumber)) {
						 	exist = true;
							break;
						 }
					}
				}finally{
					cursor.close();
				}
			}
			needBlock = exist;
		}

		if(!needBlock) {
			log("number: " + number + " not need block.");
			return null;
		}

		log("number: " + number + " need block.");
		final String[] COLUMN_NAME = { "exist" };  
		MatrixCursor matrixCursor = new MatrixCursor(COLUMN_NAME, 1); 
		matrixCursor.addRow(new Object[] {true});  

		return matrixCursor;
	 }

	/**
	* to get id array for a number's in database
	* @param uri, the query uri
	* @param number, the query number
	* @param useMark, whether to mach special number as 1234*
	* @result  a StringBuilder such as 2,3,4
	*/
	private synchronized StringBuilder getQueryIdStringBuilder(Uri uri, String number, boolean useMark) {
		StringBuilder idBuilder = new StringBuilder();
		Cursor idCursor = query(uri, new String[] {BaseColumns._ID,
		                BlacklistTable.PHONE_NUMBER}, null, null, null);
		number = number.replaceAll(" ", "");
		if(idCursor != null) {
			try{
				while(idCursor.moveToNext()) {
					String rawId = idCursor.getString(0);
					String rawNumber = idCursor.getString(1);
					if(useMark && number.endsWith("*")) {
						 if(isSpecailEqual(number, rawNumber)){
						     idBuilder.append(rawId);
						     idBuilder.append(",");
						 }
					} else if (PhoneNumberUtils.compare(number, rawNumber)) {
						 idBuilder.append(rawId);
						 idBuilder.append(",");
					}
				}
			} finally {
				idCursor.close();
			}
			if (idBuilder.length() > 1) {
				idBuilder.deleteCharAt(idBuilder.length() - 1);
			}
		}

		return idBuilder;
	}

    /**
    * check if a number is start with another number
    * @param starString, number same as 138*
    * @param phoneNumber, checked number
    * @result  true if starString is first part of phoneNumber, else false
    */
	private boolean isSpecailEqual(String starString, String phoneNumber) {
		phoneNumber = phoneNumber.replaceAll(" ", "");
		starString = starString.substring(0, starString.length() - 1);
		return phoneNumber.startsWith(starString);
	}

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String finalWhere = selection;
        log("[update] uri: " + uri.toString());
        if (selection == null && uri.getPathSegments().size() > 0) {
            finalWhere = BaseColumns._ID + " = " + Uri.decode(uri.getLastPathSegment());
        }

		if(BlacklistUtils.getCurrentUserId(getContext())!= 0) {
			if(finalWhere != null) {
				finalWhere += " AND " + RecordlistTable.USER_MODE + " = 1";
			} else {
				finalWhere = RecordlistTable.USER_MODE + " = 1";
			}
		} else {
			if(finalWhere != null) {
				finalWhere += " AND " + RecordlistTable.USER_MODE + " = 0";
			} else {
				finalWhere = RecordlistTable.USER_MODE + " = 0";
			}
		}

        int count = db.update(getTableName(uri), values, finalWhere, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    private void log(String message) {
        Log.d(TAG, "[" + getClass().getSimpleName() + "] " + message);
    }
}
