/* Copyright Statement:
 *  newly created for blacklist by guwenxu on 2017/03/24
 */
package com.gome.blacklist.preference;

import com.gome.blacklist.utils.PreferenceUtils;
import com.hct.gios.preference.CheckBoxPreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings.System;
import android.util.AttributeSet;

public class InterceptionAllContactsPreference extends CheckBoxPreference {

    private Context mContext;
    private final static String TAG = "InterceptionAllContactsPreference";
    private SharedPreferences mPrenferences;

    public InterceptionAllContactsPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        prepare();
    }

    public void prepare() {
        if (mContext != null) {
            if (mPrenferences == null) {
                mPrenferences = (SharedPreferences) mContext.getSharedPreferences(PreferenceUtils.INTERCEPTION_RULES,
                        Context.MODE_PRIVATE);
            }
            String currentKey = getCurrentKey();
            boolean isChecked = (1 == System.getIntForUser(mContext.getContentResolver(),currentKey, 0, 0));
            setChecked(isChecked);
        }
    }

    private String getCurrentKey() {
        String currentKey = PreferenceUtils.KEY_ALL_CONTACTS_ALL;
        if (mPrenferences == null) {
            return currentKey;
        }
        boolean simSet = (1 == System.getIntForUser(mContext.getContentResolver(), PreferenceUtils.KEY_SIM_SET, 0, 0));
        int currentSim = mPrenferences.getInt(PreferenceUtils.KEY_SIM_SWITCH, 1);

        if (simSet == true) {
            // SIM 1
            if (currentSim == 1) {
                currentKey = PreferenceUtils.KEY_ALL_CONTACTS_SIM1;
            } else {
                // SIM 2
                currentKey = PreferenceUtils.KEY_ALL_CONTACTS_SIM2;
            }
        } else {
            // simSet checked state is false
            currentKey = PreferenceUtils.KEY_ALL_CONTACTS_ALL;
        }
        return currentKey;
    }

    @Override
    protected void onClick() {
        super.onClick();
        boolean isCheckedStatus = isChecked();
        String currentKey = getCurrentKey();

        if (isCheckedStatus) {
            System.putIntForUser(mContext.getContentResolver(),currentKey, 1, 0);
            notifyChanged();

        } else {
            System.putIntForUser(mContext.getContentResolver(),currentKey, 0, 0);
            notifyChanged();
        }
    }
}
