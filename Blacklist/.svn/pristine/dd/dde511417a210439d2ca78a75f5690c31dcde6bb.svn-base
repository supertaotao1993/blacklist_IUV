package com.gome.blacklist;

import java.util.Locale;

import com.gome.blacklist.BlacklistData.RecordlistTable;
import com.gome.blacklist.dialog.ClearRecordDialog;
import com.gome.blacklist.fragment.InterceptCallFragment;
import com.gome.blacklist.fragment.InterceptSmsFragment;
import com.gome.blacklist.widget.ScrollableViewPager;
import com.gome.blacklist.widget.ViewPagerTabs;
import com.hct.gios.widget.ActivityHCT;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class InterceptManagementActivity extends ActivityHCT implements View.OnClickListener {

    private String[] mTabsTitle = new String[2];
    private static final int INDEX_MMS = 0;
    private static final int INDEX_CALL = 1;

    private InterceptSmsFragment mSourceFragment;
    private InterceptCallFragment mExportFragment;
    private Button mClear;

    private ScrollableViewPager mViewPager;
    private TabPagerAdapter mTabPagerAdapter;
    private ViewPagerTabs mViewPagerTabs;
    private final TabPagerListener mTabPagerListener = new TabPagerListener();
    
    private ContentObserver mRecordObserver = new ContentObserver(new Handler()) {
    	public void onChange(boolean selfChange, android.net.Uri uri) {
            initializeTabTitle();
            mViewPagerTabs.setViewPager(mViewPager, mTabPagerAdapter.getCurrentTabIndex());
    	};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.intercept_main);
        initializeActionBar();
        initializeTabTitle();

        mClear = (Button) findViewById(R.id.intercept_clear);
        mClear.setOnClickListener(this);
        mViewPager = (ScrollableViewPager) findViewById(R.id.files_tab_pager);
        mTabPagerAdapter = new TabPagerAdapter();
        mViewPager.setAdapter(mTabPagerAdapter);
        mViewPager.setOnPageChangeListener(mTabPagerListener);

        mViewPagerTabs = (ViewPagerTabs) findViewById(R.id.files_pager_header);
        if (mViewPagerTabs != null) {
            mViewPagerTabs.setVisibility(View.VISIBLE);
            mViewPagerTabs.setViewPager(mViewPager, 0);
        }

        mSourceFragment = new InterceptSmsFragment();
        mExportFragment = new InterceptCallFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.files_tab_pager, mSourceFragment);
        transaction.add(R.id.files_tab_pager, mExportFragment);
        transaction.commitAllowingStateLoss();
        getContentResolver().registerContentObserver(RecordlistTable.CONTENT_URI, true, mRecordObserver);
	}

    @Override
    protected void onNewIntent(Intent intent) {
        // Handle intents that occur after the activity has already been created.
        int preTab = getIntent().getIntExtra("preTab", -1);
		if (preTab == 1) {
			mViewPager.setCurrentItem(0);
		} else if (preTab == 2) {
			mViewPager.setCurrentItem(1);
		}
    }

	@Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mRecordObserver); 
		BlacklistUtils.markAllAsRead(this, RecordlistTable.TYPE_SMS);
		BlacklistUtils.markAllAsRead(this, RecordlistTable.TYPE_CALL);
    }

    private void initializeActionBar() {
        final ActionBar actionBar = getActionBar();
        if (actionBar == null) return;

		actionBar.setDisplayOptions(
				ActionBar.DISPLAY_HOME_AS_UP
						/* |ActionBar.DISPLAY_SHOW_HOME */ | ActionBar.DISPLAY_SHOW_TITLE,
				ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setDisplayShowHomeEnabled(false);
		setIndicatorColor(getResources().getColor(R.color.contacts_actb_bg));
    }
    
    private void initializeTabTitle() {
        int unreadSms = BlacklistUtils.getUnreadNumber(this, RecordlistTable.TYPE_SMS);
        int unreadCall = BlacklistUtils.getUnreadNumber(this, RecordlistTable.TYPE_CALL);
		mTabsTitle[INDEX_MMS] = getString(R.string.tab_title_intercept_mms_zero);
		if (unreadSms > 0) {
			mTabsTitle[INDEX_MMS] = getString(R.string.tab_title_intercept_mms_other, unreadSms);
		}
		mTabsTitle[INDEX_CALL] = getString(R.string.tab_title_intercept_call_zero);
		if (unreadCall > 0) {
			mTabsTitle[INDEX_CALL] = getString(R.string.tab_title_intercept_call_other, unreadCall);
		}
    }
    
    @Override
    public void onClick(View view) {
    	switch (view.getId()) {
	    	case R.id.intercept_clear:
	    		ClearRecordDialog dialog = ClearRecordDialog.newInstance(getCurrentRecordType());
	    		dialog.show(getFragmentManager(), ClearRecordDialog.TAG);
	    		break;
    	}
    }
    
    private int getCurrentRecordType() {
		int position = mTabPagerAdapter.getCurrentTabIndex();
		int type = 0;
		if (getTabPositionForTextDirection(position) == INDEX_CALL) {
			type = RecordlistTable.TYPE_CALL;
		} else if (getTabPositionForTextDirection(position) == INDEX_MMS) {
			type = RecordlistTable.TYPE_SMS;
		}
		
		return type;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.intercept_main_menu, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		    case android.R.id.home:
		    	finish();
		        break;
		    case R.id.menu_settings:
		    	// Go to Settings activity.
		    	Intent intent = new Intent(this, BlackWhiteListSettingActivity.class);
		    	startActivity(intent);
		        break;
    	}
    	return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NewApi")
	private boolean isRTL() {
        final Locale locale = Locale.getDefault();
        return TextUtils.getLayoutDirectionFromLocale(locale) == View.LAYOUT_DIRECTION_RTL;
    }

    private int getTabPositionForTextDirection(int position) {
        if (isRTL()) {
            return 1 - position;
        }
        return position;
    }

    private class TabPagerAdapter extends PagerAdapter {
        private final FragmentManager mFragmentManager;
        private FragmentTransaction mCurTransaction = null;
        private Fragment mCurrentPrimaryItem;

        public TabPagerAdapter() {
            mFragmentManager = getFragmentManager();
        }

        @Override
        public int getCount() {
            return mTabsTitle.length;
        }

        private Fragment getFragment(int position) {
            position = getTabPositionForTextDirection(position);
            if (position == INDEX_MMS) {
                return mSourceFragment;
            } else if (position == INDEX_CALL) {
                return mExportFragment;
            }
            throw new IllegalArgumentException("position: " + position);
        }

        @Override
        public int getItemPosition(Object object) {
            if (object == mSourceFragment) {
                return getTabPositionForTextDirection(INDEX_MMS);
            }
            if (object == mExportFragment) {
                return getTabPositionForTextDirection(INDEX_CALL);
            }
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            Fragment f = getFragment(position);
            mCurTransaction.show(f);

            // Non primary pages are not visible.
            f.setUserVisibleHint(f == mCurrentPrimaryItem);
            return f;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return ((Fragment) object).getView() == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabsTitle[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            mCurTransaction.hide((Fragment) object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            if (mCurTransaction != null) {
                mCurTransaction.commitAllowingStateLoss();
                mCurTransaction = null;
                mFragmentManager.executePendingTransactions();
            }
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            Fragment fragment = (Fragment) object;
            if (mCurrentPrimaryItem != fragment) {
                if (mCurrentPrimaryItem != null) {
                    mCurrentPrimaryItem.setUserVisibleHint(false);
                }
                if (fragment != null) {
                    fragment.setUserVisibleHint(true);
                }
                mCurrentPrimaryItem = fragment;
            }
        }
        
        public int getCurrentTabIndex() {
    		return getItemPosition(mCurrentPrimaryItem);
        }
    }

    private class TabPagerListener implements ViewPager.OnPageChangeListener {

        public TabPagerListener() {}

        @Override
        public void onPageScrollStateChanged(int state) {
            mViewPagerTabs.onPageScrollStateChanged(state);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            mViewPagerTabs.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            mViewPagerTabs.onPageSelected(position);
            invalidateOptionsMenu();
            // Add for bug 92441 guwenxu on 2017/04/10 begin
            setClearEnable();
            // Add for bug 92441 guwenxu on 2017/04/10 end
        }
	}

    // Add for bug 92441 guwenxu on 2017/04/10 begin
    @Override
    protected void onResume() {
        super.onResume();
        setClearEnable();
    }

    public void setClearEnable() {
        int position = mViewPager.getCurrentItem();
        if (position == INDEX_MMS) {
            if (mSourceFragment.isEmpty()) {
                mClear.setEnabled(false);
            } else {
                mClear.setEnabled(true);
            }
        } else if (position == INDEX_CALL) {
            if (mExportFragment.isEmpty()) {
                mClear.setEnabled(false);
            } else {
                mClear.setEnabled(true);
            }
        } else {
            // POSITION_NONE
            mClear.setEnabled(false);
        }
		if(BlacklistUtils.getCurrentUserId(this) != 0) {
            initializeTabTitle();
            mViewPagerTabs.setViewPager(mViewPager, mTabPagerAdapter.getCurrentTabIndex());
		}
    }
    // Add for bug 92441 guwenxu on 2017/04/10 end
}