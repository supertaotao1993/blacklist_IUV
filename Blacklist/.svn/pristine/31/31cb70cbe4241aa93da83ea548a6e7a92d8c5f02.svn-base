package com.gome.blacklist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import com.gome.blacklist.utils.ToolUtils;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class ContactSelectionSet implements Parcelable {

    private final Object mLock = new Object();
    /** Map of contact ID to contact objects. Every selected contact is here. */
    private final LinkedHashMap<Integer, Contact> mInternalMap = new LinkedHashMap<Integer, Contact>();
    /** All objects that are interested in changes to the selected set. */
    final Set<ContactSetObserver> mObservers = new HashSet<ContactSetObserver>();
    


    /** @see java.util.HashMap#values */
    public Collection<Contact> values() {
        synchronized (mLock) {
            return mInternalMap.values();
        }
    }

    /** @see java.util.HashMap#keySet() */
    public Set<Integer> keySet() {
        synchronized (mLock) {
            return mInternalMap.keySet();
        }
    }

    /**
     * Returns the number of contacts that are currently selected
     * @return the number of selected contacts.
     */
    public int size() {
        synchronized (mLock) {
            return mInternalMap.size();
        }
    }

    /**
     * Registers an observer to listen for interesting changes on this set.
     *
     * @param observer the observer to register.
     */
    public void addObserver(ContactSetObserver observer) {
        synchronized (mLock) {
            mObservers.add(observer);
        }
    }
    
    /**
     * Unregisters an observer for change events.
     *
     * @param observer the observer to unregister.
     */
    public void removeObserver(ContactSetObserver observer) {
        synchronized (mLock) {
            mObservers.remove(observer);
        }
    }

    /**
     * Toggles the existence of the given contact in the selection set. If the contact is
     * currently selected, it is deselected. If it doesn't exist in the selection set, then it is
     * selected.
     * @param contact
     */
    public void toggle(Contact contact) {
        final int conversationId = contact._id;
        if (containsKey(conversationId)) {
            // We must not do anything with view here.
            remove(conversationId);
        } else {
            put(conversationId, contact);
        }
    }

    /**
     * Clear the selected set entirely.
     */
    public void clear() {
        synchronized (mLock) {
//            boolean initiallyNotEmpty = !mInternalMap.isEmpty();
            mInternalMap.clear();

//            if (mInternalMap.isEmpty() && initiallyNotEmpty) {
            if (mInternalMap.isEmpty()) {
                ArrayList<ContactSetObserver> observersCopy = ToolUtils.newArrayList(mObservers);
            	dispatchOnChange(observersCopy);
                dispatchOnEmpty(observersCopy);
            }
        }
    }

    /**
     * put all contact in black list to the selected set.
     * @param cursor : black list cursor which contains all the contacts
     */
    public void putAll(Cursor cursor) {
        synchronized (mLock) {
            final boolean initiallyEmpty = mInternalMap.isEmpty();
            int savedPos = cursor.getPosition();
            if (cursor.moveToFirst()) {
                do {
                    Contact contact = new Contact(cursor);
                    final int id = contact._id;
                    if (!mInternalMap.containsKey(id)) {
                        mInternalMap.put(id, contact);
                    }
                } while(cursor.moveToNext());
                cursor.moveToPosition(savedPos);

                final ArrayList<ContactSetObserver> observersCopy = ToolUtils.newArrayList(mObservers);
                dispatchOnChange(observersCopy);
                if (initiallyEmpty) {
                    dispatchOnBecomeUnempty(observersCopy);
                }
            }
        }
    }

    /**
     * Is this contact set empty?
     * @return true if the contact selection set is empty. False otherwise.
     */
    public boolean isEmpty() {
        synchronized (mLock) {
            return mInternalMap.isEmpty();
        }
    }

    /**
     * Returns true if the given conversation is stored in the selection set.
     * @param conversation
     * @return true if the conversation exists in the selected set.
     */
    public boolean contains(Contact contact) {
        synchronized (mLock) {
            return containsKey(contact._id);
        }
    }

    private void put(Integer id, Contact info) {
        synchronized (mLock) {
            final boolean initiallyEmpty = mInternalMap.isEmpty();
            mInternalMap.put(id, info);

            final ArrayList<ContactSetObserver> observersCopy = ToolUtils.newArrayList(mObservers);
            dispatchOnChange(observersCopy);
            if (initiallyEmpty) {
                dispatchOnBecomeUnempty(observersCopy);
            }
        }
    }

    /** @see java.util.HashMap#remove */
    private void remove(Integer id) {
        synchronized (mLock) {
            removeAll(Collections.singleton(id));
        }
    }

    private void removeAll(Collection<Integer> ids) {
        synchronized (mLock) {
            final boolean initiallyNotEmpty = !mInternalMap.isEmpty();

            for (Integer id : ids) {
                mInternalMap.remove(id);
            }

            ArrayList<ContactSetObserver> observersCopy = ToolUtils.newArrayList(mObservers);
            dispatchOnChange(observersCopy);
            if (mInternalMap.isEmpty() && initiallyNotEmpty) {
                dispatchOnEmpty(observersCopy);
            }
        }
    }

    /**
     * Returns true if the given key exists in the contact selection set. This assumes
     * the internal representation holds contact.id values.
     * @param key the id of the contact
     * @return true if the key exists in this selected set.
     */
    private boolean containsKey(Integer key) {
        synchronized (mLock) {
            return mInternalMap.containsKey(key);
        }
    }

    private void dispatchOnBecomeUnempty(ArrayList<ContactSetObserver> observers) {
        synchronized (mLock) {
            for (ContactSetObserver observer : observers) {
                observer.onSetPopulated(this);
            }
        }
    }

    private void dispatchOnChange(ArrayList<ContactSetObserver> observers) {
        synchronized (mLock) {
            // Copy observers so that they may unregister themselves as listeners on
            // event handling.
            for (ContactSetObserver observer : observers) {
                observer.onSetChanged(this);
            }
        }
    }

    private void dispatchOnEmpty(ArrayList<ContactSetObserver> observers) {
        synchronized (mLock) {
            for (ContactSetObserver observer : observers) {
                observer.onSetEmpty();
            }
        }
    }
    


    /**
     * Create a new object,
     */
    public ContactSelectionSet() {
        // Do nothing.
    }

    private ContactSelectionSet(Parcel source, ClassLoader loader) {
        Parcelable[] conversations = source.readParcelableArray(loader);
        for (Parcelable parceled : conversations) {
            Contact contact = (Contact) parceled;
            put(contact._id, contact);
        }
    }
    
    
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
        Contact[] values = values().toArray(new Contact[size()]);
        dest.writeParcelableArray(values, flags);
	}
	
    public static final ClassLoaderCreator<ContactSelectionSet> CREATOR =
            new ClassLoaderCreator<ContactSelectionSet>() {

        @Override
        public ContactSelectionSet createFromParcel(Parcel source) {
            return new ContactSelectionSet(source, null);
        }

        @Override
        public ContactSelectionSet createFromParcel(Parcel source, ClassLoader loader) {
            return new ContactSelectionSet(source, loader);
        }

        @Override
        public ContactSelectionSet[] newArray(int size) {
            return new ContactSelectionSet[size];
        }

    };

}
