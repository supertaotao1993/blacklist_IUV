package com.gome.blacklist.fragment;

import com.gome.blacklist.InterceptManagementActivity;
import com.gome.blacklist.InterceptRecordAdapter;
import com.gome.blacklist.R;
import com.gome.blacklist.Record;
import com.gome.blacklist.RecyclerViewCursorViewHolder.OnViewHolderClickListener;
import com.gome.blacklist.dialog.RecordOptionsDialog;
import com.gome.blacklist.BlacklistData.RecordlistTable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class InterceptCallFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnViewHolderClickListener {

    private Context mContext;
    private View mRootView;
    private RecyclerView mCallList;
    private InterceptRecordAdapter mCallAdapter;
    private View mEmptyView;
    private View mLoadingView;

    private final int LOADER_INTERCEPT_CALL = 101;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_list_panel, null);
        mCallList = (RecyclerView) mRootView.findViewById(R.id.panel_list);
        LinearLayoutManager lm = new LinearLayoutManager(mContext);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        mCallList.setLayoutManager(lm);
        mCallAdapter = new InterceptRecordAdapter(mContext, false, this, null);
        mCallList.setAdapter(mCallAdapter);

        mEmptyView = mRootView.findViewById(R.id.layout_file_list_empty);
        TextView emptyTips = (TextView) mEmptyView.findViewById(R.id.empty_view_tips);
        emptyTips.setText(R.string.empty_tips_intercept_call);
        ImageView emptySrc = (ImageView) mEmptyView.findViewById(R.id.empty_view_tag);
        emptySrc.setBackgroundResource(R.drawable.ic_empty_intercept_call);
        mLoadingView = mRootView.findViewById(R.id.loading_layout);
        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(LOADER_INTERCEPT_CALL, null, this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext =context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onDestroyView() {
        getLoaderManager().destroyLoader(LOADER_INTERCEPT_CALL);
        super.onDestroyView();
    }

    @Override
    public void onViewHolderClick(RecyclerView.ViewHolder holder, final int position, View view) {
        Record record = mCallAdapter.getRecordByPosition(position);
        RecordOptionsDialog dialog = RecordOptionsDialog.newInstance(record);

        FragmentManager manager = ((Activity) mContext).getFragmentManager();
        dialog.show(manager, RecordOptionsDialog.TAG);
    }

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
    	String smsSelection = RecordlistTable.TYPE + " = 2 ";
        return new CursorLoader(mContext, RecordlistTable.CONTENT_URI,
        		InterceptRecordAdapter.PROJECTION, smsSelection, null, RecordlistTable.DEFAULT_SORT_ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
		boolean isEmpty = (data == null || data.getCount() == 0);
        mLoadingView.setVisibility(View.GONE);
        mEmptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        mCallList.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        mCallAdapter.swapCursor(data);
        // Add for bug 92441 guwenxu on 2017/04/10 begin
        ((InterceptManagementActivity)getActivity()).setClearEnable();
        // Add for bug 92441 guwenxu on 2017/04/10 end
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mCallAdapter.swapCursor(null);
	}

    // Add for bug 92441 guwenxu on 2017/04/10 begin
    public boolean isEmpty() {
        return mCallAdapter.getItemCount() == 0;
    }
    // Add for bug 92441 guwenxu on 2017/04/10 end
}
