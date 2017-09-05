/* Copyright Statement:
 *  newly created for blacklist by guwenxu on 2017/03/24
 */
package com.gome.blacklist.fragment;

import com.gome.blacklist.BlackListManagementActivity;
import com.gome.blacklist.BlacklistData;
import com.gome.blacklist.InterceptionRulesActivity;
import com.gome.blacklist.R;
import com.gome.blacklist.WhiteListManagementActivity;
import com.gome.blacklist.preference.ListPreference;
import com.hct.gios.preference.Preference;
import com.hct.gios.preference.PreferenceFragment;
import com.hct.gios.preference.PreferenceScreen;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class SettingPreferenceFragment extends PreferenceFragment {

    private static final String TAG = "SettingPreferenceFragment";

    private Preference mInterceptionRulesPreference;
    private Preference mBlackListPreference;
    private Preference mWhiteListPreference;

    private static final String KEY_INTERCEPTION_RULES = "intercepetion_rules";
    private static final String KEY_BLACK_LIST = "tel_black_list";
    private static final String KEY_WHITE_LIST = "tel_white_list";
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_setting);

        mInterceptionRulesPreference = (Preference) findPreference(KEY_INTERCEPTION_RULES);
        mBlackListPreference = (Preference) findPreference(KEY_BLACK_LIST);
        mWhiteListPreference = (Preference) findPreference(KEY_WHITE_LIST);

    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(0, null, mBlackListCursorCallbacks);
        getLoaderManager().initLoader(1, null, mWhiteListCursorCallbacks);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        Log.i(TAG, "onPreferenceTreeClick()");
        if (preference == mInterceptionRulesPreference) {
            Intent intent = new Intent(mContext, InterceptionRulesActivity.class);
            startActivity(intent);
            return true;
        }
        if (preference == mBlackListPreference) {
            Intent intent = new Intent(mContext, BlackListManagementActivity.class);
            startActivity(intent);
            return true;
        }
        if (preference == mWhiteListPreference) {
            Intent intent = new Intent(mContext, WhiteListManagementActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private final String[] LIST_PROJECTION = new String[] { BlacklistData.WhitelistTable._ID,
            BlacklistData.WhitelistTable.DISPLAY_NAME, BlacklistData.WhitelistTable.PHONE_NUMBER };
    // black list
    private LoaderCallbacks<Cursor> mBlackListCursorCallbacks = new LoaderCallbacks<Cursor>() {

        @Override
        public void onLoaderReset(Loader<Cursor> arg0) {
        }

        @Override
        public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
            ((ListPreference) mBlackListPreference).setNumber(String.valueOf(arg1.getCount()));
        }

        @Override
        public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
            CursorLoader cursorLoader = new CursorLoader(mContext, BlacklistData.BlacklistTable.CONTENT_URI,
                    LIST_PROJECTION, null, null, BlacklistData.BlacklistTable.DEFAULT_SORT_ORDER);
            return cursorLoader;
        }
    };

    // white list
    private LoaderCallbacks<Cursor> mWhiteListCursorCallbacks = new LoaderCallbacks<Cursor>() {

        @Override
        public void onLoaderReset(Loader<Cursor> arg0) {
        }

        @Override
        public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
            ((ListPreference) mWhiteListPreference).setNumber(String.valueOf(arg1.getCount()));
        }

        @Override
        public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
            CursorLoader cursorLoader = new CursorLoader(mContext, BlacklistData.WhitelistTable.CONTENT_URI,
                    LIST_PROJECTION, null, null, BlacklistData.WhitelistTable.DEFAULT_SORT_ORDER);
            return cursorLoader;
        }
    };
}
