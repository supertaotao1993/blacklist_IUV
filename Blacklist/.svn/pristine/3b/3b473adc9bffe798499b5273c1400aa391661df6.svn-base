/* Copyright Statement:
 *  newly created for blacklist by guwenxu on 2017/03/24
 */
package com.gome.blacklist;

import com.gome.blacklist.fragment.SettingPreferenceFragment;
import com.hct.gios.widget.PreferenceActivityHCT;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class BlackWhiteListSettingActivity extends PreferenceActivityHCT {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingPreferenceFragment()).commit();

        // init actionbar setting
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(
                    ActionBar.DISPLAY_HOME_AS_UP
                            /* |ActionBar.DISPLAY_SHOW_HOME */ | ActionBar.DISPLAY_SHOW_TITLE,
                    ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setDisplayShowHomeEnabled(false);
        }
        setIndicatorColor(getApplicationContext().getResources().getColor(R.color.contacts_actb_bg));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home: {
            finish();
            return true;
        }
        }
        return false;
    }
}
