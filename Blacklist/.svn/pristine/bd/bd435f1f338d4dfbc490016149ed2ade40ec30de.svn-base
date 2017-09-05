/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to MediaTek Inc. and/or its licensors.
 * Without the prior written permission of MediaTek inc. and/or its licensors,
 * any reproduction, modification, use or disclosure of MediaTek Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 /* Copyright Statement:
  *  newly created for blacklist ops by wenpd on 2017/03/20 
  */

package com.gome.blacklist;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * BlacklistData.
 */
public class BlacklistData {
    public static String AUTHORITY = "com.gome.blacklist";
    public static Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    /**
     * BlacklistTable.
     */
    public static final class BlacklistTable implements BaseColumns {
        public static final String TABLE_NAME = "black_list";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);

        public static final int RECORDS_NUMBER_MAX = 500;

        public static final int CATEGORY = 0x1001;

        /**
         * MIMe type.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/contact";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/contact";

        /**
         * Type: TEXT.
         */
        public static final String PHONE_NUMBER = "PHONE_NUMBER";

        /**
         * Type: TEXT.
         */
        public static final String DISPLAY_NAME = "NAME";

		
        /**
         * Type: INTEGER.
         */
        public static final String USER_MODE = "USER";

        /**
         * Sort order .
         */
        public static final String DEFAULT_SORT_ORDER = BaseColumns._ID + " ASC";

    }

	/**
     * WhitelistTable.
     */
    public static final class WhitelistTable implements BaseColumns {
        public static final String TABLE_NAME = "white_list";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);

        public static final int RECORDS_NUMBER_MAX = 500;

        public static final int CATEGORY = 0x1002;

        /**
         * MIMe type.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/contact";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/contact";

        /**
         * Type: TEXT.
         */
        public static final String PHONE_NUMBER = "PHONE_NUMBER";

        /**
         * Type: TEXT.
         */
        public static final String DISPLAY_NAME = "NAME";

        /**
         * Type: INTEGER.
         */
        public static final String USER_MODE = "USER";

        /**
         * Sort order .
         */
        public static final String DEFAULT_SORT_ORDER = BaseColumns._ID + " ASC";

    }

	/**
     * RecordlistTable.
     */
    public static final class RecordlistTable implements BaseColumns {
        public static final String TABLE_NAME = "record_list";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);

        public static final int RECORDS_NUMBER_MAX = 5000;
        
        public static final int TYPE_SMS = 1;
        public static final int TYPE_CALL = 2;
        
        /**
         * Type: TEXT.
         */
        public static final String PHONE_NUMBER = "PHONE_NUMBER";

		/**
         * Type: TEXT.
         */
        public static final String DISPLAY_NAME = "NAME";

        /**
         * Type: INTEGER.
         */
        public static final String TYPE = "TYPE";

         /**
         * Type: TEXT.
         */
        public static final String BODY = "BODY";

		  /**
         * Type: TEXT.
         */
        public static final String PDU = "PDU";


         /**
         * Type: TEXT.
         */
        public static final String FORMAT = "FORMAT";

		
         /**
         * Type: TEXT.
         */
        public static final String SUBID = "SUBID";


         /**
         * Type: TEXT.
         */
        public static final String TIME = "TIME";

		  /**
         * Type: INTEGER.
         */
        public static final String READ = "READ";

         /**
         * Type: TEXT.
         */
        public static final String LOCATION = "LOCATION";

		  /**
         * Type: INTEGER.
         */
        public static final String USER_MODE = "USER";

        /**
         * Sort order .
         */
        public static final String DEFAULT_SORT_ORDER = " TIME DESC";

    }
}
