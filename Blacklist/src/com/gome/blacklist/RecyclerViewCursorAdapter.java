package com.gome.blacklist;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Adapter class that uses a CursorAdapter to display data in a RecyclerView.
 */
public abstract class RecyclerViewCursorAdapter<T extends RecyclerViewCursorViewHolder> extends RecyclerView.Adapter<T> {

    /**
     * The Context of the adapter
     */
    protected final Context mContext;

    /**
     * The CursorAdapter to display data with
     */
    protected CursorAdapter mCursorAdapter;

    /**
     * ViewHolder object to bind Cursor data to. A class level variable is created to pass the
     * ViewHolder between RecyclerView.Adapter.bindView() and CursorAdapter.bindView()
     */
    private T mViewHolder;

    /**
     * Constructor
     * @param context The Context the Adapter is displayed in.
     */
    protected RecyclerViewCursorAdapter(Context context) {
        mContext = context;
    }

    /**
     * Default implementation of the CursorAdapter for the scenario when only one view type is used.
     * @param cursor The Cursor from which to get the data.
     * @param flags Flags used to determine the behavior of the adapter.
     * @param resource Resource ID for an XML layout to be inflated.
     * @param attachToRoot Whether the inflated layout should be attached to the root view.
     */
    protected void setupCursorAdapter(Cursor cursor, int flags, final int resource, final boolean attachToRoot) {
        mCursorAdapter = new CursorAdapter(mContext, cursor, flags) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                return LayoutInflater.from(context).inflate(resource, viewGroup, attachToRoot);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                // Bind cursor to our ViewHolder
                mViewHolder.bindCursor(cursor);
            }
        };
    }

    /**
     * Swap the Cursor of the CursorAdapter and notify the RecyclerView.Adapter that data has
     * changed.
     * @param cursor The new Cursor representation of the data to be displayed.
     */
    public void swapCursor(Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
        notifyDataSetChanged();
    }
    
    public Cursor getCursor() {
    	return mCursorAdapter.getCursor();
    }
    
    /**
     * The number of elements in the adapter is the number of elements in the Cursor Adapter.
     */
    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }

    /**
     * Sets the ViewHolder object.
     * @param viewHolder The ViewHolder we will be binding data to.
     */
    protected void setViewHolder(T viewHolder) {
        mViewHolder = viewHolder;
    }
}
