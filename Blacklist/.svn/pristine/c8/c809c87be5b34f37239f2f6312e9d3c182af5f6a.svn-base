/* Copyright Statement:
 *  newly created for blacklist ops by wenpd on 2017/03/20 
 */

package com.gome.blacklist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.provider.CallLog;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;

/**
* service user to listen to calllog
*/
public class BlacklistService extends Service   {  
	private static final String TAG = "BlacklistService";  
	private static final int MSG_SHOW_HARASSMENT_DIALOG = 0x123;
	private static final int MSG_SHOW_ADD_TO_DIALOG = 0x124;
	private static final long MIN_CALLS_DURING = 30;
	private static final String CALL_ACCOUNT = "com.android.services.telephony";
	private TelephonyManager mTelManager;
	private String mCurNumber;
	private AlertDialog mAddToDialog;
	private AlertDialog mHarassmentDialog;
	private static final String[] CALLS_PROJECTION = {
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION,
            CallLog.Calls.PHONE_ACCOUNT_COMPONENT_NAME
    };
	
    @SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
				case MSG_SHOW_HARASSMENT_DIALOG:{
					String number = (String)msg.obj;
					showHarassmentDialog(number); 
					}
                    break;
				case MSG_SHOW_ADD_TO_DIALOG:{
					String number = (String)msg.obj;
					showAddToBlacklistConfirm(number); 
					}
                    break;
            }
        }
    }; 

	private PhoneStateListener listener = new PhoneStateListener() {
	 	 public void onCallStateChanged(int state, String incomingNumber) {
        	 switch (state) {
        	 case TelephonyManager.CALL_STATE_RINGING:
				 mCurNumber = incomingNumber;
				 dismisDialogs();
				 break;
        	 default:
				 break;
        	 }
	 	 }
	 };

	private ContentObserver mCallsLogObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			if(mCurNumber != null && (mTelManager.getCallState() == TelephonyManager.CALL_STATE_IDLE)) {
				getNewestCallLog();
			}
		}
	};
      
    @Override  
    public void onCreate()   
    {  
        super.onCreate();  
        Log.i(TAG, "onCreate");  
		mTelManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        mTelManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		this.getContentResolver().registerContentObserver(
                CallLog.Calls.CONTENT_URI, true, mCallsLogObserver);
    } 

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	// TODO Auto-generated method stub
    	return super.onStartCommand(intent, flags, startId);
    }
  
      
    @Override  
    public void onDestroy() {  
        Log.i(TAG, "onDestroy");   
		this.getContentResolver().unregisterContentObserver(mCallsLogObserver);
		mTelManager.listen(listener, PhoneStateListener.LISTEN_NONE);
        super.onDestroy(); 

    }

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	} 
	
	/**
	* to get newest calllog to check if it is a harassment call
	*/
	private void getNewestCallLog() {
		Cursor cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI, CALLS_PROJECTION, 
				null, null, "date DESC limit 1");
		if(cursor != null) {
			try{
				if(cursor.moveToNext()) {
					final String number = cursor.getString(0);
					int type = cursor.getInt(1);
					int during = cursor.getInt(3);
					String account = cursor.getString(4);
					if(PhoneNumberUtils.compare(number, mCurNumber)) {
						mCurNumber = null;
					} else {
						return ;
					}
					Log.w(TAG, "type:" + type + ";during:" + during + ";account:" + account);
					if(account.contains(CALL_ACCOUNT) && type == CallLog.Calls.INCOMING_TYPE && MIN_CALLS_DURING > during) {
						Log.w(TAG, "Newest number:" + number);
						if(BlacklistUtils.isInContacts(this, number) || hasOtherCalls(number)) {
							return ;
						}
						Message msg = new Message();
						msg.what = MSG_SHOW_HARASSMENT_DIALOG; 
						msg.obj = (Object)number;
						mHandler.sendMessage(msg);
					}
				}
			} finally {
				cursor.close();
			}
		}
	}

	/**
	* to check if it is number's first call
	* @param context the context used to get the SystemService
	* @result  true if not first call, otherwise false
	*/
	private boolean hasOtherCalls(final String number) {
		String selection = CallLog.Calls.NUMBER + " = \"" + number + "\"";
		Cursor cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI, CALLS_PROJECTION, 
				selection, null, CallLog.Calls.DEFAULT_SORT_ORDER);
		if(cursor != null) {
			try{
				if(cursor.getCount() > 1) {
					Log.w(TAG, "number:" + number + " has other calls.");
					return true;
				}
			} finally {
				cursor.close();
			}
		}

		return false;
	}

	/**
	* to show the dialog whether to add the number to blacklist
	* @param context the context used to get the SystemService
	*/
	private void lightScreen(Context context) {
        PowerManager powerManager = (PowerManager) (context.getSystemService(Context.POWER_SERVICE));
        PowerManager.WakeLock wakeLock = null;
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP 
			| PowerManager.ON_AFTER_RELEASE, "bls_wake_lock");
        long wakeUpTime = 10000;
        wakeLock.acquire(wakeUpTime);               
    }

	/**
	* to show the dialog whether to add the number to blacklist
	*@param number, the phone number
	*/
	private void showHarassmentDialog(final String number) {
		lightScreen(this);
		mHarassmentDialog = new AlertDialog.Builder(this)
         .setTitle(R.string.tips) // Tips
         .setMessage(R.string.harassment_call_confrim)			
         .setNegativeButton(R.string.cancel, null)
         .setPositiveButton(R.string.button_add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               Message msg = new Message();  
               msg.what = MSG_SHOW_ADD_TO_DIALOG;
			   msg.obj = (Object)number;
               mHandler.sendMessage(msg);
            }
         }).create();
		mHarassmentDialog.setCanceledOnTouchOutside(false);
		mHarassmentDialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
		mHarassmentDialog.show();
	}
	
	/**
	* to show the dialog whether to add the number to blacklist
	*@param number, the phone number
	*/
	private void showAddToBlacklistConfirm(final String number) {
		mAddToDialog = new AlertDialog.Builder(this)
         .setTitle(R.string.add_to_blacklist_title)
         .setMessage(R.string.add_to_blacklist_confrim)			
         .setNegativeButton(R.string.cancel, null)
         .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               BlacklistUtils.insertNumber(BlacklistService.this, null, number, 1, true);
            }
         }).create();
		mAddToDialog.setCanceledOnTouchOutside(false);
		mAddToDialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
		mAddToDialog.show();
	}

	/**
	* to close dialog when new call coming
	*/
	private void dismisDialogs(){
		if(mHarassmentDialog != null && mHarassmentDialog.isShowing()) {
			mHarassmentDialog.dismiss();
		}
		if(mAddToDialog != null && mAddToDialog.isShowing()) {
			mAddToDialog.dismiss();
		}
	}
}  
