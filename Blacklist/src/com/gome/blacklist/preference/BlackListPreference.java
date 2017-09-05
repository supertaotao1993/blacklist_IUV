/* Copyright Statement:
 *  newly created for blacklist by guwenxu on 2017/03/24
 */
package com.gome.blacklist.preference;

import com.gome.blacklist.R;
import com.hct.gios.preference.Preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class BlackListPreference extends Preference {

	public BlackListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWidgetLayoutResource(R.layout.list_preference);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		return super.onCreateView(parent);
	}
}
