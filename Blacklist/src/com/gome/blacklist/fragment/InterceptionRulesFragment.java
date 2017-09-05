/* Copyright Statement:
 *  newly created for blacklist by guwenxu on 2017/03/24
 */
package com.gome.blacklist.fragment;

import java.util.List;

import com.gome.blacklist.R;
import com.gome.blacklist.preference.InterceptionAllContactsPreference;
import com.gome.blacklist.preference.InterceptionOnlyWhitePreference;
import com.gome.blacklist.preference.InterceptionStrangerPreference;
import com.gome.blacklist.preference.SimSwitchPreference;
import com.hct.gios.preference.CheckBoxPreference;
import com.hct.gios.preference.Preference;
import com.hct.gios.preference.Preference.OnPreferenceClickListener;
import com.hct.gios.preference.Preference.OnPreferenceChangeListener;
import com.hct.gios.preference.PreferenceFragment;
import com.hct.gios.preference.PreferenceScreen;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
//import android.preference.Preference;
//import android.preference.Preference.OnPreferenceClickListener;
import android.telephony.SubscriptionInfo;

public class InterceptionRulesFragment extends PreferenceFragment implements OnPreferenceClickListener, OnPreferenceChangeListener {

    private final static String TAG = "InterceptionRulesFragment";

    private PreferenceScreen mSimSetGroup;
    private CheckBoxPreference mSimSet;
    // private PreferenceScreen mSimSwitch;
    private SimSwitchPreference mSimSwitch;

    private InterceptionStrangerPreference mStrangerPreference;
    private InterceptionOnlyWhitePreference mOnlyAllowWhitePreference;
    private InterceptionAllContactsPreference mAllContactsPreference;

    private final String KEY_SIM_SET = "sim_set";
    private final String KEY_SIM_SWITCH = "sim_switch";
    private final String KEY_SIM_SET_GROUP = "sim_set_group";

    private final String KEY_Stranger = "rules_stranger";
    private final String KEY_ONLY_ALLOW_WHITE = "rules_only_allow_white";
    private final String KEY_ALL_CONTACTS = "rules_all_contacts";

    private boolean mIsSetDoubleSim = false;
    private boolean mIsHasSim1 = false;// sim1 exists
    private boolean mIsHasSim2 = false;// sim2 exists
    private List<SubscriptionInfo> mSubscriptionInfos;

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_interception_rules);

        init();
        setListener();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getListView().setDividerHeight(1);
        super.onActivityCreated(savedInstanceState);
    }

    private void init() {
        mContext = getActivity();

        mSimSetGroup = (PreferenceScreen) findPreference(KEY_SIM_SET_GROUP);
        mSimSet = (CheckBoxPreference) findPreference(KEY_SIM_SET);
        mSimSwitch = (SimSwitchPreference) findPreference(KEY_SIM_SWITCH);

        // rules content
        mStrangerPreference = (InterceptionStrangerPreference) findPreference(KEY_Stranger);
        mOnlyAllowWhitePreference = (InterceptionOnlyWhitePreference) findPreference(KEY_ONLY_ALLOW_WHITE);
        mAllContactsPreference = (InterceptionAllContactsPreference) findPreference(KEY_ALL_CONTACTS);

        // init status
        // SimSet
        if (mSimSet.isChecked() == true) {
            mSimSetGroup.addPreference(mSimSwitch);
            mIsSetDoubleSim = true;
        } else {
            mIsSetDoubleSim = false;
            mSimSetGroup.removePreference(mSimSwitch);
        }
    }

    private void setListener() {
        mSimSet.setOnPreferenceClickListener(this);
		mAllContactsPreference.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference == mSimSet) {
            Log.i(TAG, "onPreferenceClick() mSimSet click");
            if (mSimSet.isChecked() == true) {
                mIsSetDoubleSim = true;
                mSimSetGroup.addPreference(mSimSwitch);
            } else {
                mIsSetDoubleSim = false;
                mSimSetGroup.removePreference(mSimSwitch);
            }
            reLoadData();
        }

        if (preference == mSimSwitch) {
            Log.i(TAG, "onPreferenceClick() mSimSwitch click");
            // reLoadData();
        }
        return false;
    }

	@Override
    public boolean onPreferenceChange(Preference pref, Object objValue) {
        if (pref == mAllContactsPreference) {
			boolean isAllChecked = (Boolean) objValue;
            mStrangerPreference.updateStatus(isAllChecked);
            mOnlyAllowWhitePreference.updateStatus(isAllChecked);
            return true;
        }
        return false;
    }

    private void reLoadData() {
        mStrangerPreference.prepare();
        mOnlyAllowWhitePreference.prepare();
        mAllContactsPreference.prepare();
    }
}
