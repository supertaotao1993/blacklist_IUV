/* Copyright Statement:
 *  newly created for blacklist by guwenxu on 2017/03/24
 */
package com.gome.blacklist.preference;

import java.util.ArrayList;
import java.util.List;

import com.gome.blacklist.R;
import com.gome.blacklist.utils.PreferenceUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SimSwitchPreference extends com.hct.gios.preference.Preference {

    private final static String TAG = "SimSwitchPreference";
    private SharedPreferences mPrenferences;
    private int mCurrentSim = 1;// sim1: 1; sim2: 2

    public SimSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mSubscriptionInfos = new ArrayList<SubscriptionInfo>();
        prepare();
        getSimStatus();
    }

    private void prepare() {
        if (mContext != null) {
            mPrenferences = (SharedPreferences) mContext.getSharedPreferences(PreferenceUtils.INTERCEPTION_RULES,
                    Context.MODE_PRIVATE);
            mCurrentSim = mPrenferences.getInt(PreferenceUtils.KEY_SIM_SWITCH, 1);

        }
    }

    private Context mContext;
    private TextView mSim1Btn;
    private TextView mSim2Btn;

    public int getCurrentSim() {
        return mCurrentSim;
    }

    public void setFirstSimDisplayName(String displayName) {
        mSim1Btn.setText(displayName);
        notifyChanged();
    }

    public void setSecondSimDisplayName(String displayName) {
        mSim2Btn.setText(displayName);
        notifyChanged();
    }

    public void setSimEnabled(int simNum) {
        if (simNum == 1) {
            mSim1Btn.setEnabled(true);
        } else if (simNum == 2) {
            mSim2Btn.setEnabled(true);
        }
        notifyChanged();
    }

    public void setSimDisabled(int simNum) {
        if (simNum == 1) {
            mSim1Btn.setEnabled(false);
        } else if (simNum == 2) {
            mSim2Btn.setEnabled(false);
        }
        notifyChanged();
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        Log.i(TAG, "onCreateView() simSwitch");
        View view = LayoutInflater.from(mContext).inflate(R.layout.sim_switch, null);
        mSim1Btn = (TextView) view.findViewById(R.id.sim1_tv);
        mSim2Btn = (TextView) view.findViewById(R.id.sim2_tv);

        setListener();

        if (mCurrentSim == 1) {
            mSim1Btn.setTextColor(mContext.getResources().getColor(R.color.sim_switch_text_color_selected, null));
            mSim2Btn.setTextColor(mContext.getResources().getColor(R.color.sim_switch_text_color_unselected, null));
        } else {
            mSim1Btn.setTextColor(mContext.getResources().getColor(R.color.sim_switch_text_color_unselected, null));
            mSim2Btn.setTextColor(mContext.getResources().getColor(R.color.sim_switch_text_color_selected, null));
        }
        initData();
        return view;
    }

    private List<SubscriptionInfo> mSubscriptionInfos;
    private SubscriptionManager mSubscriptionManager;
    private boolean mIsHasSim1 = false;// sim1 exists
    private boolean mIsHasSim2 = false;// sim2 exists

    private void getSimStatus() {
        mSubscriptionManager = SubscriptionManager.from(mContext);
        SubscriptionInfo sir1 = mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(0);
        if (sir1 == null) {
            mIsHasSim1 = false;
        } else {
            mIsHasSim1 = true;
            mSubscriptionInfos.add(sir1);
        }

        SubscriptionInfo sir2 = mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(1);
        if (sir2 == null) {
            mIsHasSim2 = false;
        } else {
            mIsHasSim2 = true;
            mSubscriptionInfos.add(sir2);
        }
    }

    private void initData() {
        // SimSwitch
        // sim 1
        if (mIsHasSim1 == true) {
            /// sim card is inserted
            String tempStr = mSubscriptionInfos.get(0).getDisplayName().toString();
            String displayName = mContext.getResources().getString(R.string.sim1, tempStr);
            setFirstSimDisplayName(displayName);
        } else {
            // sim card is not inserted
            String tempStr = mContext.getResources().getString(R.string.sim_lack);
            String displayName = mContext.getResources().getString(R.string.sim1, tempStr);
            setFirstSimDisplayName(displayName);
        }
        // sim 2
        if (mIsHasSim2 == true) {
            /// sim card is inserted
            String tempStr = mSubscriptionInfos.get(0).getDisplayName().toString();
			if (mIsHasSim1 == true) {
				tempStr = mSubscriptionInfos.get(1).getDisplayName().toString();
			}
            String displayName = mContext.getResources().getString(R.string.sim2, tempStr);
            setSecondSimDisplayName(displayName);
        } else {
            // sim card is not inserted
            String tempStr = mContext.getResources().getString(R.string.sim_lack);
            String displayName = mContext.getResources().getString(R.string.sim2, tempStr);
            setSecondSimDisplayName(displayName);
        }
    }

    private void setListener() {
        mSim1Btn.setOnClickListener(new SimSwitchListener());
        mSim2Btn.setOnClickListener(new SimSwitchListener());
    }

    private class SimSwitchListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick()");

            if (mPrenferences == null) {
                return;
            }
			if ((v == mSim1Btn && mIsHasSim1 == false) || (v == mSim2Btn && mIsHasSim2 == false)) {
				return ;
			}
            SharedPreferences.Editor editor = mPrenferences.edit();
            if (v == mSim1Btn) {
                mSim1Btn.setTextColor(mContext.getResources().getColor(R.color.sim_switch_text_color_selected, null));
                mSim2Btn.setTextColor(mContext.getResources().getColor(R.color.sim_switch_text_color_unselected, null));
                mCurrentSim = 1;
                editor.putInt(PreferenceUtils.KEY_SIM_SWITCH, 1);
                notifyChanged();
            }
            if (v == mSim2Btn) {
                mSim1Btn.setTextColor(mContext.getResources().getColor(R.color.sim_switch_text_color_unselected, null));
                mSim2Btn.setTextColor(mContext.getResources().getColor(R.color.sim_switch_text_color_selected, null));
                mCurrentSim = 2;
                editor.putInt(PreferenceUtils.KEY_SIM_SWITCH, 2);
                notifyChanged();
            }
            editor.apply();

            // reLoad
            ((InterceptionStrangerPreference) getPreferenceManager().findPreference("rules_stranger")).prepare();
            ((InterceptionOnlyWhitePreference) getPreferenceManager().findPreference("rules_only_allow_white"))
                    .prepare();
            ((InterceptionAllContactsPreference) getPreferenceManager().findPreference("rules_all_contacts")).prepare();
        }
    }
}
