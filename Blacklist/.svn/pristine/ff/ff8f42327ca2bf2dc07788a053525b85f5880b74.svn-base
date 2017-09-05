package com.gome.blacklist;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class RecyclerViewCursorViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener {

    public interface OnViewHolderClickListener {
        void onViewHolderClick(RecyclerView.ViewHolder holder, int position, View view);
    }

    public interface OnViewHolderLongClickListener {
        void onViewHolderLongClick(RecyclerView.ViewHolder holder, int position, View view);
    }

    private OnViewHolderClickListener mClickListener;
    private OnViewHolderLongClickListener mLongClickListener;

    /**
     * Constructor
     * @param view The root view of the ViewHolder
     */
    public RecyclerViewCursorViewHolder(View view, OnViewHolderClickListener listener,
                                        OnViewHolderLongClickListener longListener) {
        super(view);
        mClickListener = listener;
        mLongClickListener = longListener;
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (mClickListener != null) {
//            mClickListener.onViewHolderClick(this, getAdapterPosition(), view);
            mClickListener.onViewHolderClick(this, getPosition(), view);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (mLongClickListener != null) {
//            mLongClickListener.onViewHolderLongClick(this, getAdapterPosition(), view);
            mLongClickListener.onViewHolderLongClick(this, getPosition(), view);
        }
        return true;
    }

    /**
     * Binds the information from a Cursor to the various UI elements of the ViewHolder.
     * @param cursor A Cursor representation of the data to be displayed.
     */
    public abstract void bindCursor(Cursor cursor);

}
