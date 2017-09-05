/* Copyright Statement:
 *  newly created for blacklist by guwenxu on 2017/03/24
 */
package com.gome.blacklist.preference;

import com.gome.blacklist.R;
import com.hct.gios.preference.Preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ListPreference extends Preference {

    private String mNumber;
    private TextView mNumberTv;
    private TextView mTitleTv;
    private TextView mSummaryTv;

    public ListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWidgetLayoutResource(R.layout.list_preference_end_widget);
        mNumber = "0";
    }

    public void setNumber(String number) {
        mNumber = number;
        notifyChanged();
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        // View view =
        // LayoutInflater.from(getContext()).inflate(R.layout.list_preference,
        // null);
        // mNumberTv = (TextView) view.findViewById(R.id.number);
        // mTitleTv = (TextView) view.findViewById(R.id.title);
        // mSummaryTv = (TextView) view.findViewById(R.id.summary);
        // mNumberTv.setText(mNumber +
        // getContext().getResources().getString(R.string.list_num));
        // mTitleTv.setText(getTitle());
        // mSummaryTv.setText(getSummary());
        // return view;
        return super.onCreateView(parent);
    }

    @Override
    protected void onBindView(View view) {
        mNumberTv = (TextView) view.findViewById(R.id.number);
        mNumberTv.setText(mNumber + getContext().getResources().getString(R.string.list_num));
        super.onBindView(view);
    }
}
