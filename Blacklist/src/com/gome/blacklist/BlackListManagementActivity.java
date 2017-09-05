package com.gome.blacklist;

import com.gome.blacklist.R;
import com.gome.blacklist.RecyclerViewCursorViewHolder.OnViewHolderClickListener;
import com.gome.blacklist.RecyclerViewCursorViewHolder.OnViewHolderLongClickListener;
import com.gome.blacklist.dialog.AddContactDialog;
import com.gome.blacklist.dialog.DeleteBlacklistContactDialog;

import java.util.ArrayList;
import java.util.List;

import com.gome.blacklist.BlacklistData.BlacklistTable;
import com.hct.gios.widget.ActivityHCT;
import com.hct.gios.widget.ProgressDialog;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Intents;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BlackListManagementActivity extends ActivityHCT implements LoaderManager.LoaderCallbacks<Cursor>,
			OnViewHolderLongClickListener, OnViewHolderClickListener, View.OnClickListener, ActionMode.Callback,
			DeleteBlacklistContactDialog.OnClickListener, ContactSetObserver {
	
	private static final String TAG = "BlackListManagementActivity";
    private RecyclerView mBlackList;
    private BlackListAdapter mBlackListAdapter;
    private View mEmptyView;
    private View mLoadingView;
    
    private Button mCancel;
    private Button mRemove;
    private LinearLayout operateContanier;

    private ActionMode mActionMode;
    /**
     * Selected contacts, if any.
     */
    private final ContactSelectionSet mSelectedSet = new ContactSelectionSet();

    private final int LOADER_BLACKLIST = 102;

	private static final String ACTION_GETNUMBER = "com.gome.blacklist.GETNUMBER";
	private static final int REQUEST_CODE_ADD_FROM_CONTACT = 121;
	private static final int REQUEST_CODE_ADD_FROM_CALLLOG = 122;
	private static final int REQUEST_CODE_ADD_FROM_MMS = 123;
	private static final String CONTACT_SELECTION =
    Data.MIMETYPE + " IN ('"
    + Phone.CONTENT_ITEM_TYPE + "')"
    + " AND ";

    private static final String CONTACT_ORDER = Data.CONTACT_ID + " DESC";

    private static final String[] CONTACT_PROJECTION = {
        Data.CONTACT_ID,
        Data.MIMETYPE,
        Data.DATA1,
        Data.DISPLAY_NAME
    };
	private static final int ID = 0;
    private static final int MIMETYPE = 1;
    private static final int DATA1 = 2;
    private static final int DISPLAY_NAME = 3;

	private static final int REMOVE_CONFIRM_MESSAGE = 2017;
	private List<String> mInsertNumList = new ArrayList<String>();
	private int mAddedNumber = 0;
	private int mRealAddedNumber = 0;
	private boolean mHasCallLog = true;
	private boolean mHasSms = true;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.w(TAG, "Message=" + msg.what);
            switch (msg.what) {
                case REMOVE_CONFIRM_MESSAGE:
					int index = msg.arg1;
					removeConfirm(index);
                    break;				
                default:
                    Log.wtf(TAG, "Message not expected: " + msg.what);
                    break;
            }
        }
	};
	private MenuItem mMenuItemSms;
    private MenuItem mMenuItemCall;
	private ProgressDialog mAddProgress;
	private ProgressDialog mDelProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.management_blacklist);
        initializeActionBar();

        operateContanier = (LinearLayout) findViewById(R.id.operate_button_container);
        mCancel = (Button) findViewById(R.id.border_button_negative);
        mRemove = (Button) findViewById(R.id.border_button_positive);
        mCancel.setOnClickListener(this);
        mRemove.setOnClickListener(this);
        
        mSelectedSet.addObserver(this);
        mBlackList = (RecyclerView) findViewById(R.id.black_list_view);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        mBlackList.setLayoutManager(lm);
        mBlackListAdapter = new BlackListAdapter(this);
        mBlackList.setAdapter(mBlackListAdapter);

        mEmptyView = findViewById(R.id.layout_file_list_empty);
        TextView emptyTips = (TextView) mEmptyView.findViewById(R.id.empty_view_tips);
        emptyTips.setText(R.string.blacklist_management_empty);
        ImageView emptySrc = (ImageView) mEmptyView.findViewById(R.id.empty_view_tag);
        emptySrc.setBackgroundResource(R.drawable.ic_empty_blacklist);
        mLoadingView = findViewById(R.id.loading_layout);

        getLoaderManager().initLoader(LOADER_BLACKLIST, null, this);
		(new GetCallLogAndSmsTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
    
    @Override
    protected void onDestroy() {
    	mSelectedSet.removeObserver(this);
        getLoaderManager().destroyLoader(LOADER_BLACKLIST);
		mHandler.removeMessages(REMOVE_CONFIRM_MESSAGE);
		dismissDialogs();
    	super.onDestroy();
    }

    private void initializeActionBar() {
        final ActionBar actionBar = getActionBar();
        if (actionBar == null) return;

		actionBar.setDisplayOptions(
				ActionBar.DISPLAY_HOME_AS_UP
						/* |ActionBar.DISPLAY_SHOW_HOME */ | ActionBar.DISPLAY_SHOW_TITLE,
				ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setDisplayShowHomeEnabled(false);
		setIndicatorColor(getResources().getColor(R.color.contacts_actb_bg));
    }
    
    @Override
    public void onClick(View view) {
    	switch (view.getId()) {
	    	case R.id.border_button_negative:
	    		if (mActionMode != null) mActionMode.finish();
	    		break;
	    	case R.id.border_button_positive:
	    		DeleteBlacklistContactDialog dialog = DeleteBlacklistContactDialog.newInstance();
	    		dialog.setOnClickListener(this);
	    		dialog.show(getFragmentManager(), DeleteBlacklistContactDialog.TAG);
	    		break;
    	}
    }
    
    @Override
    public void onContactDelete(boolean restoreSms) {
		/*for (int contactId : mSelectedSet.keySet()) {
        	String selection = BlacklistTable._ID + " = " + contactId;
        	int count = getContentResolver().delete(BlacklistTable.CONTENT_URI, selection, null);
        	if (count > 0 && restoreSms) {
	    		BlacklistUtils.restoreItemById(this, contactId, false);
        	}
		}
		mSelectedSet.clear();*/
		(new DeleteAndRestoreTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, restoreSms);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.blacklist_management_menu, menu);
		mMenuItemSms = menu.findItem(R.id.menu_blacklist_add_from_sms);
		mMenuItemCall = menu.findItem(R.id.menu_blacklist_add_from_call_log);
    	return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        if(mHasCallLog){
            mMenuItemCall.setVisible(true);
        } else {
            mMenuItemCall.setVisible(false);
        }
        if(mHasSms){
             mMenuItemSms.setVisible(true);
        } else {
            mMenuItemSms.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		    case android.R.id.home:
		    	finish();
		        break;
		    case R.id.menu_blacklist_add_manual:
				AddContactDialog manualDialog = AddContactDialog.newInstance(AddContactDialog.DIALOG_TYPE_MANUAL, AddContactDialog.CONTACT_TYPE_BLACKLIST);
				manualDialog.show(getFragmentManager(), AddContactDialog.TAG);
		        break;
		    case R.id.menu_blacklist_add_number_prefix:
				AddContactDialog prefixDialog = AddContactDialog.newInstance(AddContactDialog.DIALOG_TYPE_PREFIX_NUMBER, AddContactDialog.CONTACT_TYPE_BLACKLIST);
				prefixDialog.show(getFragmentManager(), AddContactDialog.TAG);
		        break;
		    case R.id.menu_blacklist_add_from_contacts:{
				final Intent pickerIntent = new Intent("com.android.contacts.action.GET_MULTIPLE_CONTACTS");
				ComponentName componentName = new ComponentName("com.android.contacts","com.hct.contacts.multiSelect.ContactMultiSelectionActivity");
				pickerIntent.setComponent(componentName);
				pickerIntent.setType(Contacts.CONTENT_ITEM_TYPE);
				startActivityForResult(pickerIntent, REQUEST_CODE_ADD_FROM_CONTACT);
		    	}
		        break;
		    case R.id.menu_blacklist_add_from_call_log:{
				Intent intent = new Intent(ACTION_GETNUMBER);
				ComponentName componentName = new ComponentName("com.android.contacts","com.hct.contacts.multiSelect.CallLogMultiSelectionActivity");
				intent.setComponent(componentName);
				intent.setType("vnd.android.cursor.dir/calls");
				intent.putExtra("selectType", 0);
				startActivityForResult(intent, REQUEST_CODE_ADD_FROM_CALLLOG);
				}
		        break;
		    case R.id.menu_blacklist_add_from_sms:{
				Intent intent = new Intent(ACTION_GETNUMBER);
				ComponentName componentName = new ComponentName("com.android.mms","com.hct.mms.ui.conversation.ConversationSelectItem");
				intent.setComponent(componentName);
				intent.setType("vnd.android-dir/mms-sms");
				intent.putExtra("useMode", 2);
				startActivityForResult(intent, REQUEST_CODE_ADD_FROM_MMS);
				}
		        break;
    	}
    	return super.onOptionsItemSelected(item);
    }

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        return new CursorLoader(this, BlacklistTable.CONTENT_URI,
        		BlackListAdapter.PROJECTION, null, null, BlacklistTable.DEFAULT_SORT_ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
    	boolean isEmpty = (data == null || data.getCount() == 0);
        mLoadingView.setVisibility(View.GONE);
        mEmptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        mBlackList.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        mBlackListAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mBlackListAdapter.swapCursor(null);
	}

    @Override
    public void onViewHolderClick(RecyclerView.ViewHolder holder, final int position, View view) {
    	if (mBlackListAdapter == null || mActionMode == null) return;
        	mSelectedSet.toggle(mBlackListAdapter.getContactByPosstion(position));
    }

	@Override
	public void onViewHolderLongClick(ViewHolder holder, int position, View view) {
		if (mBlackListAdapter == null || mActionMode != null) return;
		mActionMode = startActionMode(this);
		operateContanier.setVisibility(View.VISIBLE);
      	mSelectedSet.toggle(mBlackListAdapter.getContactByPosstion(position));
	}

	@Override
	public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        if (mBlackListAdapter != null) {
            switch (menuItem.getItemId()) {
                case R.id.blacklist_management_unselect_all:
                	mSelectedSet.clear();
                    break;
                case R.id.blacklist_management_select_all:
                	mSelectedSet.putAll(mBlackListAdapter.getCursor());
                    break;
            }
        }
		return false;
	}

	@Override
	public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.blacklist_management_action_mode_menu, menu);
		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode actionMode) {
		mActionMode = null;
		mSelectedSet.clear();
        operateContanier.setVisibility(View.GONE);
	}

	@Override
	public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
		if (mSelectedSet.isEmpty()) {
            menu.findItem(R.id.blacklist_management_select_all).setVisible(true);
            menu.findItem(R.id.blacklist_management_unselect_all).setVisible(false);
		} else if (mBlackListAdapter.getItemCount() == mSelectedSet.size()) {
            menu.findItem(R.id.blacklist_management_select_all).setVisible(false);
            menu.findItem(R.id.blacklist_management_unselect_all).setVisible(true);
		} else {
			menu.findItem(R.id.blacklist_management_select_all).setVisible(true);
            menu.findItem(R.id.blacklist_management_unselect_all).setVisible(false);
		}
		return false;
	}

	@Override
	public void onSetEmpty() {
		mRemove.setEnabled(false);
	}

	@Override
	public void onSetPopulated(ContactSelectionSet set) {
		mRemove.setEnabled(true);
	}

	@Override
	public void onSetChanged(ContactSelectionSet set) {
		if (mActionMode != null) {
			String title = getString(R.string.select_mode_zero);
			if (mSelectedSet.size() > 0) {
				title = getString(R.string.select_mode_other, mSelectedSet.size());
			}
	        mActionMode.setTitle(title);
	        mActionMode.invalidate();
		}
        mBlackListAdapter.notifyDataSetChanged();
	}
	
	public boolean inActionMode() {
		return mActionMode != null;
	}
	
	public boolean isContactSelected(Contact contact) {
		return mSelectedSet.contains(contact);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode != RESULT_OK){
			return;
    	}

		switch(requestCode) {
            case REQUEST_CODE_ADD_FROM_MMS:{
				Bundle bundle = data.getExtras();
				String addresses = bundle.getString("Addresses");
				Log.w(TAG, "Addresses:" + addresses);
				addNumberToBlackList(addresses);
				//(new InsertNumberTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, addresses);
            }
			break;

            case REQUEST_CODE_ADD_FROM_CALLLOG:{
				Bundle bundle = data.getExtras();
				String addresses = bundle.getString("Addresses");
				Log.w(TAG, "Addresses:" + addresses);
				addNumberToBlackList(addresses);
				//.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, addresses);
            }
            break;
			
            case REQUEST_CODE_ADD_FROM_CONTACT:{
				if(null != data) {
					Parcelable[] uris =data.getParcelableArrayExtra(Intents.EXTRA_PHONE_URIS);
					int count = (uris!= null ? uris.length : 0);
					StringBuilder numberBuilder = new StringBuilder();
					if(count > 0){
					   String[] idStrings = new String[count];
					   int i = 0;
					   for (Parcelable p : uris) {
					      Uri uri = (Uri) p;
					      long contactId = ContentUris.parseId(uri);
					      idStrings[i] = Long.toString(contactId);
					      i ++;
					   }
					   numberBuilder.append(getContactNumber(Data.CONTACT_ID, idStrings));
					}
					if (numberBuilder.length() > 1) {
					   numberBuilder.deleteCharAt(numberBuilder.length() - 1);
					   Log.w(TAG, "Addresses:" + numberBuilder);
					   addNumberToBlackList(numberBuilder.toString());
					} else {
					   Toast.makeText(BlackListManagementActivity.this, R.string.no_valid_number, Toast.LENGTH_SHORT).show();
					}
					//(new InsertNumberTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, nmumberBuilder.toString());
				}	
            }
			break;

			default:
				break;
		}
    }

	 /**
	 * get contact's numbers
	 */
	 private String getContactNumber(String selection, String[] keys){
        StringBuilder builder = new StringBuilder();
		StringBuilder contactBuilder = new StringBuilder();
        for (String key : keys) {
            builder.append("\'");
            builder.append(key);
            builder.append("\',");
        }
        String keyString = builder.toString();
        if (!TextUtils.isEmpty(keyString) && keyString.endsWith(",")) {
            keyString = keyString.substring(0, keyString.length() - 1);
        }
        Cursor cursor = getContentResolver().query(Data.CONTENT_URI,
                CONTACT_PROJECTION, CONTACT_SELECTION + selection + " IN ( " + keyString + " )", null, CONTACT_ORDER);
        if (cursor != null) {
            try {
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    //get contact info
                    String mimetype = cursor.getString(MIMETYPE);
                    if (Phone.CONTENT_ITEM_TYPE.equals(mimetype)) {
                         String data = cursor.getString(DATA1);
                        if (!TextUtils.isEmpty(data)){
							contactBuilder.append(data);
							contactBuilder.append(",");
                        }
                    } 
                }
            } finally {
                cursor.close();
            }
        }

		return contactBuilder.toString();
    }

   /**
   * to add all number in list to black list
   */
   public synchronized void addNumberToBlackList(String addressList) {
      String[] numbers = addressList.split(",");
	  if(mInsertNumList != null) {
         mInsertNumList.clear();
	  }
	  for(String number:numbers) {
         mInsertNumList.add(number);
	  }
	  mAddedNumber = 0;
	  mRealAddedNumber = 0;
	  (new InsertNumberTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);
   }
   
   /**
   * to show a confrim dialog if number is in white list
   */
   private void removeConfirm(final int index) {
      final String number = mInsertNumList.get(index);
	  Log.w(TAG, "removeConfirm-number" + number);
      AlertDialog dialog = new AlertDialog.Builder(this)
         .setTitle(number) // Tips
         .setMessage(R.string.dialog_move_to_blacklist_summary)			
         .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               (new InsertNumberTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, index+1);
            }
         })
         .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               BlacklistUtils.deleteNumber(BlackListManagementActivity.this, number, 2, false);
               BlacklistUtils.insertNumber(BlackListManagementActivity.this, null, number, 1, false);
			   ++mAddedNumber;
			   ++mRealAddedNumber;
               (new InsertNumberTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, index+1);
            }
         }).create();
      dialog.setCanceledOnTouchOutside(false);
	  dialog.show();
   }

   /**
   * sent message to handler to show the confrim dialog
   */
   private void sendConfrimMessgage(int index) {
		Message msg = mHandler.obtainMessage(REMOVE_CONFIRM_MESSAGE);
		msg.arg1 = index;
        mHandler.sendMessage(msg);
   }

    /**
    * simple task to insert number to databases
    */
    class InsertNumberTask extends AsyncTask<Integer, String, Integer> {
        @Override
        protected void onPreExecute() {
            if (mAddProgress == null) {
                mAddProgress = new ProgressDialog(BlackListManagementActivity.this);
                mAddProgress.setMessage(getResources().getString(R.string.processing));
                mAddProgress.setCanceledOnTouchOutside(false);
				mAddProgress.setCancelable(true);
				mAddProgress.setOnCancelListener(new OnCancelListener() { 
                    @Override 
                    public void onCancel(DialogInterface dialog) { 
                        InsertNumberTask.this.cancel(true);
                    } 
                }); 
            }
            if (!BlackListManagementActivity.this.isDestroyed()) {
                mAddProgress.show();
            }
            super.onPreExecute();
        }

		@Override
		protected Integer doInBackground(Integer... params) {
			int index = params[0];
			for(int i=index; i<mInsertNumList.size(); ++i) {
				if(BlacklistUtils.checkNumberExist(BlackListManagementActivity.this, mInsertNumList.get(i), 2)) {
				   return i;
				}
				String noSpaceNumber = mInsertNumList.get(i).replaceAll(" ", "");
				Uri uri = BlacklistUtils.insertNumber(BlackListManagementActivity.this, null, noSpaceNumber, 1, false);
				++mAddedNumber;
				publishProgress((i+1) + "/" + mInsertNumList.size());
				if(uri != null) {
					++mRealAddedNumber;
				} else if(uri == null && BlacklistUtils.getDbCount(BlackListManagementActivity.this, 1) >= BlacklistTable.RECORDS_NUMBER_MAX) {
					return -1;
				}
			}
			return mInsertNumList.size();
		}

		protected void onProgressUpdate(String... progress) {
			String msg = BlackListManagementActivity.this.getString(R.string.processing_add_to_blacklist);
			msg += progress[0];
			if(mAddProgress != null) {
				mAddProgress.setMessage(msg);
			}
            //Log.w(TAG, progress[0]);
        }

		@Override
		protected void onPostExecute(Integer param) {
            if (isCancelled()) {
				mInsertNumList.clear();
				return;
			}
            if (mAddProgress != null && mAddProgress.isShowing()) {
                mAddProgress.dismiss();
            }
			int index = param;
			if(index < 0) {
				mInsertNumList.clear();
				Toast.makeText(BlackListManagementActivity.this, R.string.blacklist_reach_max_tip, Toast.LENGTH_LONG).show();
			} else if(index < mInsertNumList.size()) {
				sendConfrimMessgage(index);
			} else if(mAddedNumber != 0){
				mInsertNumList.clear();
				mAddedNumber = 0;
				if(mRealAddedNumber == 0) {
					Toast.makeText(BlackListManagementActivity.this, R.string.toast_exist_number_blacklist, Toast.LENGTH_SHORT).show();
				} else {
					mRealAddedNumber = 0;
					Toast.makeText(BlackListManagementActivity.this, R.string.add_black_number_success, Toast.LENGTH_SHORT).show();
				}
			}
		}
    }

    /**
    * simple task to delete number from databases, and restore records
    */
    class DeleteAndRestoreTask extends AsyncTask<Boolean, String, String> {
        @Override
        protected void onPreExecute() {
            if (mDelProgress == null) {
                mDelProgress = new ProgressDialog(BlackListManagementActivity.this);
                mDelProgress.setMessage(getResources().getString(R.string.processing));
                mDelProgress.setCanceledOnTouchOutside(false);
				mDelProgress.setCancelable(false);
            }
            if (!BlackListManagementActivity.this.isDestroyed()) {
                mDelProgress.show();
            }
            super.onPreExecute();
        }

		@Override
		protected String doInBackground(Boolean... params) {
			boolean restoreSms = params[0];
			ArrayList<Long> ids = new ArrayList<Long>();
			String msg = BlackListManagementActivity.this.getString(R.string.processing_remove_from_blacklist_restore);
			publishProgress(msg);
			for (Contact contact : mSelectedSet.values()) {
				if(restoreSms) {
					BlacklistUtils.restoreItemByNumber(BlackListManagementActivity.this, contact.phoneNumber);
				}
				ids.add((long)contact._id);
			}
			msg = BlackListManagementActivity.this.getString(R.string.processing_remove_from_blacklist);
			publishProgress(msg);
			BlacklistUtils.deleteMembers(BlackListManagementActivity.this.getContentResolver(), ids, 1);
			return null;
		}

		protected void onProgressUpdate(String... progress) {
			if(mDelProgress != null) {
				mDelProgress.setMessage(progress[0]);
			}
            //Log.w(TAG, progress[0]);
        }

		@Override
		protected void onPostExecute(String param) {
			mSelectedSet.clear();
            if (mDelProgress != null && mDelProgress.isShowing()) {
                mDelProgress.dismiss();
            }
			if (mActionMode != null) mActionMode.finish();
			Toast.makeText(BlackListManagementActivity.this, R.string.del_black_number_success, Toast.LENGTH_SHORT).show();
		}
    }

    /**
    * simple task to get info of mms and calllog
    */
    class GetCallLogAndSmsTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			mHasCallLog = BlacklistUtils.hasCallLog(BlackListManagementActivity.this);
			mHasSms = BlacklistUtils.hasMessage(BlackListManagementActivity.this);
			return null;
		}

		@Override
		protected void onPostExecute(Void param) {
			BlackListManagementActivity.this.invalidateOptionsMenu();
		}
    }

	private void dismissDialogs() {
		if (mAddProgress != null) {
			if(mAddProgress.isShowing()) {
				mAddProgress.dismiss();
			}
			mAddProgress = null;
		}
		if (mDelProgress != null) {
			if(mDelProgress.isShowing()) {
				mDelProgress.dismiss();
			}
			mDelProgress = null;
		}
	}
}
