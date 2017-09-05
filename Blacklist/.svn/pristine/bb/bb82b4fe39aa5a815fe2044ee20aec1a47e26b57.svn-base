package com.gome.blacklist.widget;

import com.gome.blacklist.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Outline;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Lightweight implementation of ViewPager tabs. This looks similar to traditional actionBar tabs,
 * but allows for the view containing the tabs to be placed anywhere on screen. Text-related
 * attributes can also be assigned in XML - these will get propogated to the child TextViews
 * automatically.
 */
@SuppressLint("NewApi")
public class ViewPagerTabs extends HorizontalScrollView implements ViewPager.OnPageChangeListener {

    ViewPager mPager;
    private ViewPagerTabStrip mTabStrip;
    

    /**
     * Linearlayout that will contain the TextViews serving as tabs. This is the only child
     * of the parent HorizontalScrollView.
     */
    private boolean mShowUnderline = false;
    private int mSelectedTextColor = Color.WHITE;
    private int mUnselectedTextColor = Color.WHITE;
    private int mTextSize;
    private boolean mTextAllCaps;
    int mPrevSelected = -1;
    int mSidePadding;

	@SuppressLint("NewApi")
	private static final ViewOutlineProvider VIEW_BOUNDS_OUTLINE_PROVIDER =
            new ViewOutlineProvider() {
		@SuppressLint("NewApi")
		@Override
        public void getOutline(View view, Outline outline) {
            outline.setRect(0, 0, view.getWidth(), view.getHeight());
        }
    };

    private static final int TAB_SIDE_PADDING_IN_DPS = 10;

    /**
     * Simulates actionbar tab behavior by showing a toast with the tab title when long clicked.
     */
    private class OnTabLongClickListener implements OnLongClickListener {
        final int mPosition;

        public OnTabLongClickListener(int position) {
            mPosition = position;
        }

        @Override
        public boolean onLongClick(View v) {
            final int[] screenPos = new int[2];
            getLocationOnScreen(screenPos);

            final Context context = getContext();
            final int width = getWidth();
            final int height = getHeight();
            final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;

            Toast toast = Toast.makeText(context, mPager.getAdapter().getPageTitle(mPosition),
                    Toast.LENGTH_SHORT);

            // Show the toast under the tab
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL,
                    (screenPos[0] + width / 2) - screenWidth / 2, screenPos[1] + height);

            toast.show();
            return true;
        }
    }

    public ViewPagerTabs(Context context) {
        this(context, null);
    }

    public ViewPagerTabs(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerTabs(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFillViewport(true);

        mSidePadding = (int) (getResources().getDisplayMetrics().density * TAB_SIDE_PADDING_IN_DPS);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerTabs);
        mSelectedTextColor = a.getColor(R.styleable.ViewPagerTabs_tab_selected_text_color, mSelectedTextColor);
        mUnselectedTextColor = a.getColor(R.styleable.ViewPagerTabs_tab_unselected_text_color, mUnselectedTextColor);
        mShowUnderline = a.getBoolean(R.styleable.ViewPagerTabs_tab_underline_visiable, false);
        mTextSize = (int) a.getDimension(R.styleable.ViewPagerTabs_android_textSize, mTextSize);
        mTextAllCaps = a.getBoolean(R.styleable.ViewPagerTabs_android_textAllCaps, mTextAllCaps);

        mTabStrip = new ViewPagerTabStrip(context);
        addView(mTabStrip, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        a.recycle();

        // enable shadow casting from view bounds
        setOutlineProvider(VIEW_BOUNDS_OUTLINE_PROVIDER);
    }

    public void setViewPager(ViewPager viewPager, int selectedTab) {
        mPager = viewPager;
        addTabs(mPager.getAdapter(), selectedTab);
    }

    private void addTabs(PagerAdapter adapter, int selectedTab) {
        mTabStrip.removeAllViews();

        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            addTab(adapter.getPageTitle(i), i, selectedTab);
        }
    }

	private void addTab(CharSequence tabTitle, final int position, int selectedTab) {
        final TextView textView = new TextView(getContext());

        // Default to the first child being selected
        if (position == selectedTab) {
            mPrevSelected = selectedTab;
            textView.setSelected(true);
        }

        textView.setText(tabTitle);
        textView.setGravity(Gravity.CENTER);
        textView.setOnLongClickListener(new OnTabLongClickListener(position));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        textView.setAllCaps(mTextAllCaps);
        textView.setPadding(mSidePadding, 0, mSidePadding, 0);
        if (textView.isSelected()) {
            textView.setTextColor(mSelectedTextColor);
        } else {
            textView.setTextColor(mUnselectedTextColor);
        }

        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(getRtlPosition(position));
            }
        });

        mTabStrip.addView(textView, new LinearLayout.
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        position = getRtlPosition(position);
        int tabStripChildCount = mTabStrip.getChildCount();
        if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
            return;
        }

        mTabStrip.onPageScrolled(position, positionOffset, positionOffsetPixels, mShowUnderline);
    }

    @Override
    public void onPageSelected(int position) {
        position = getRtlPosition(position);
        if (mPrevSelected >= 0) {
            final View unselectedChild = mTabStrip.getChildAt(mPrevSelected);
            if (unselectedChild instanceof TextView) {
                ((TextView) unselectedChild).setTextColor(mUnselectedTextColor);
            }
            unselectedChild.setSelected(false);
        }
        final View selectedChild = mTabStrip.getChildAt(position);
        if (selectedChild instanceof TextView) {
            ((TextView) selectedChild).setTextColor(mSelectedTextColor);
        }
        selectedChild.setSelected(true);

        // Update scroll position
        final int scrollPos = selectedChild.getLeft() - (getWidth() - selectedChild.getWidth()) / 2;
        smoothScrollTo(scrollPos, 0);
        mPrevSelected = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    private int getRtlPosition(int position) {
        if (getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            return mTabStrip.getChildCount() - 1 - position;
        }
        return position;
    }

    public int getCurrentTabIndex() {
        return mPrevSelected;
    }
}

