package com.gome.blacklist;

public interface RecordSetObserver {

    /**
     * Called when the selection set becomes empty.
     */
    void onSetEmpty();

    /**
     * Handle when the selection set is populated with some items. The observer should not make any
     * modifications to the set while handling this event.
     */
    void onSetPopulated(RecordSelectionSet set);

    /**
     * Handle when the selection set gets an element added or removed. The observer should not
     * make any modifications to the set while handling this event.
     */
    void onSetChanged(RecordSelectionSet set);
}
