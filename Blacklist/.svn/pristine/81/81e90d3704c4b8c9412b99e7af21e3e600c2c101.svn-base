/* Copyright Statement:
 *  newly created for blacklist by guwenxu on 2017/03/24
 */
package com.gome.blacklist.preference;

import com.gome.blacklist.utils.PreferenceUtils;

import android.content.Context;
import android.provider.Settings.System;
import android.util.AttributeSet;

public class InterceptionSimSetPreference extends com.hct.gios.preference.CheckBoxPreference {

    private static final String TAG = "InterceptionCheckPreference";
    private Context mContext;

    public InterceptionSimSetPreference(Context context) {
        super(context);
        prepare();
    }

    public InterceptionSimSetPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        prepare();
    }

    private void prepare() {
        boolean isSimSet = (1 == System.getIntForUser(mContext.getContentResolver(),PreferenceUtils.KEY_SIM_SET, 0, 0));
        setChecked(isSimSet);
    }

    @Override
    protected void onClick() {
        super.onClick();
        boolean isCheckedStatus = isChecked();

        if (isCheckedStatus) {
			System.putIntForUser(mContext.getContentResolver(),PreferenceUtils.KEY_SIM_SET, 1, 0);
            notifyChanged();

        } else {
            System.putIntForUser(mContext.getContentResolver(),PreferenceUtils.KEY_SIM_SET, 0, 0);
            notifyChanged();
        }
    }

}
