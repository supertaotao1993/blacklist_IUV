package com.gome.blacklist;

import com.gome.blacklist.BlacklistData.RecordlistTable;
import com.gome.blacklist.RecyclerViewCursorViewHolder.OnViewHolderClickListener;
import com.hct.gios.widget.ActivityHCT;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class InterceptMultiPickActivity extends ActivityHCT implements View.OnClickListener,
			OnViewHolderClickListener, LoaderManager.LoaderCallbacks<Cursor>, RecordSetObserver {

    private final int LOADER_INTERCEPT_MULTI_PICK = 0x1000;
    public final static String INTERCEPT_PICK_EXTRA = "intercept_pick_extra";
    public final static String INTERCEPT_PICK_TYPE = "intercept_pick_type";
    public final static String INTERCEPT_PICK_RESULT_NUMBER = "intercept_pick_result_number";

    private RecyclerView mInterceptList;
    private InterceptRecordAdapter mInterceptAdapter;
    private View mEmptyView;
    private View mLoadingView;
    private Button mCancel;
    private Button mAdd;

    /**
     * Selected records, if any.
     */
    private final RecordSelectionSet mSelectedSet = new RecordSelectionSet();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_intercept_multi_pick);
        initializeActionBar();

        mSelectedSet.addObserver(this);
        mInterceptList = (RecyclerView) findViewById(R.id.intercept_list_view);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        mInterceptList.setLayoutManager(lm);
        mInterceptAdapter = new InterceptRecordAdapter(this, true, this, null);
        mInterceptList.setAdapter(mInterceptAdapter);

        Bundle bundle = getIntent().getBundleExtra(INTERCEPT_PICK_EXTRA);
		int type = bundle.getInt(INTERCEPT_PICK_TYPE);
		int textId = type == RecordlistTable.TYPE_CALL ? R.string.empty_tips_intercept_call : R.string.empty_tips_intercept_mms;
		int srcId = type == RecordlistTable.TYPE_CALL ? R.drawable.ic_empty_intercept_call : R.drawable.ic_empty_intercept_sms;

        mEmptyView = findViewById(R.id.layout_file_list_empty);
        TextView emptyTips = (TextView) mEmptyView.findViewById(R.id.empty_view_tips);
        emptyTips.setText(textId);
        ImageView emptySrc = (ImageView) mEmptyView.findViewById(R.id.empty_view_tag);
        emptySrc.setBackgroundResource(srcId);
        mLoadingView = findViewById(R.id.loading_layout);
        findViewById(R.id.operate_button_container).setVisibility(View.VISIBLE);
        mCancel = (Button) findViewById(R.id.border_button_negative);
        mAdd = (Button) findViewById(R.id.border_button_positive);
        mAdd.setText(R.string.add);
        mCancel.setOnClickListener(this);
        mAdd.setOnClickListener(this);

        getLoaderManager().initLoader(LOADER_INTERCEPT_MULTI_PICK, bundle, this);
	}

	@Override
    protected void onDestroy() {
    	mSelectedSet.clear();
		mSelectedSet.removeObserver(this);
        getLoaderManager().destroyLoader(LOADER_INTERCEPT_MULTI_PICK);
        super.onDestroy();
    }

    @SuppressLint("NewApi")
	private void initializeActionBar() {
        final ActionBar actionBar = getActionBar();
        if (actionBar == null) return;

		actionBar.setDisplayOptions(
				ActionBar.DISPLAY_HOME_AS_UP
						/* |ActionBar.DISPLAY_SHOW_HOME */ | ActionBar.DISPLAY_SHOW_TITLE,
				ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setDisplayShowHomeEnabled(false);
		setIndicatorColor(getResources().getColor(R.color.contacts_actb_bg, null));
    }
    
    public boolean isRecordSelected(Record record) {
    	return mSelectedSet.contains(record);
    }

	@Override
	public void onViewHolderClick(ViewHolder holder, int position, View view) {
    	mSelectedSet.toggle(mInterceptAdapter.getRecordByPosition(position));
	}
    
    @Override
    public void onClick(View view) {
    	switch (view.getId()) {
	    	case R.id.border_button_negative:
		    	finish();
	    		break;
	    	case R.id.border_button_positive:
	    		if (mSelectedSet.size() <= 0) return;
    			StringBuilder numbers = new StringBuilder();
    			for (Record record : mSelectedSet.values()) {
    				numbers.append(record.phoneNumber);
    				numbers.append(",");
    			}
				if (numbers.length() > 1) {
					numbers.deleteCharAt(numbers.length() - 1);
				}
				android.util.Log.e("zxMail", "setResult()... numbers:" + numbers.toString());
				Intent intent = new Intent();
				intent.putExtra(INTERCEPT_PICK_RESULT_NUMBER, numbers.toString());
				setResult(RESULT_OK, intent);
				finish();
	    		break;
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.intercept_multi_pick_menu, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	if (mSelectedSet.isEmpty()) {
	        menu.findItem(R.id.intercept_pick_select_all).setVisible(true);
	        menu.findItem(R.id.intercept_pick_unselect_all).setVisible(false);
		} else if (mInterceptAdapter.getItemCount() == mSelectedSet.size()) {
	        menu.findItem(R.id.intercept_pick_select_all).setVisible(false);
	        menu.findItem(R.id.intercept_pick_unselect_all).setVisible(true);
		} else {
			menu.findItem(R.id.intercept_pick_select_all).setVisible(true);
	        menu.findItem(R.id.intercept_pick_unselect_all).setVisible(false);
		}
    	return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		    case android.R.id.home:
		    	finish();
		        break;
		    case R.id.intercept_pick_select_all:
            	mSelectedSet.putAll(mInterceptAdapter.getCursor());
		        break;
		    case R.id.intercept_pick_unselect_all:
            	mSelectedSet.clear();
		        break;
    	}
    	return super.onOptionsItemSelected(item);
    }

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		int type = bundle.getInt(INTERCEPT_PICK_TYPE);
    	String selection = RecordlistTable.TYPE + " = " + type;
        return new CursorLoader(this, RecordlistTable.CONTENT_URI,
        		InterceptRecordAdapter.PROJECTION, selection, null, RecordlistTable.DEFAULT_SORT_ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		boolean isEmpty = (data == null || data.getCount() == 0);
        mLoadingView.setVisibility(View.GONE);
        mAdd.setEnabled(!isEmpty && (mSelectedSet.size() > 0));
        mEmptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        mInterceptList.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        mInterceptAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mInterceptAdapter.swapCursor(null);
	}

	@Override
	public void onSetEmpty() {
		mAdd.setEnabled(false);
	}

	@Override
	public void onSetPopulated(RecordSelectionSet set) {
		mAdd.setEnabled(true);
	}

	@Override
	public void onSetChanged(RecordSelectionSet set) {
		String title = getString(R.string.select_mode_zero);
		if (set.size() > 0) {
			title = getString(R.string.select_mode_other, set.size());
		}
		getActionBar().setTitle(title);
		invalidateOptionsMenu();
		mInterceptAdapter.notifyDataSetChanged();
	}
}