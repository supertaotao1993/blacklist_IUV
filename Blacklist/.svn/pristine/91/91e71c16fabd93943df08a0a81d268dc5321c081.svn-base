/* Copyright Statement:
 *  newly created for blacklist ops by wenpd on 2017/03/20 
 */

package com.gome.blacklist;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Boot completed receiver. used to reset the app install state every time the
 * device boots.
 *
 */
public class RecordsReceiver extends BroadcastReceiver {
    private static final String TAG = "RecordsReceiver";
	public static final String ACTION_INSERT = "com.gome.action.INSERT_NUMBER";
	public static final String ACTION_DELETE = "com.gome.action.DELETE_NUMBER";
	public static final String ACTION_RECORD = "com.gome.action.INSERT_RECORD";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
		Log.w(TAG, "blacklist-action:" + action);

        // insert number to black-list or white-list
        if (ACTION_INSERT.equals(action)) {
			//int type = intent.getIntExtra("Type", 1);
			String number = intent.getStringExtra("Number");
			String name = intent.getStringExtra("Name");
			boolean needToast = intent.getBooleanExtra("needToast", false);
			if(TextUtils.isEmpty(number)) {
				Toast.makeText(context, R.string.no_valid_number, Toast.LENGTH_SHORT).show();
			} else if(number.indexOf(",") >= 0) {
				String numbers[] = number.split(",");
				addBlacklistWithCheck(context, 0, numbers, name, needToast, 0, 0);
			} else {
			    String numbers[] = {number};
			    addBlacklistWithCheck(context, 0, numbers, name, needToast, 0, 0);
			}
        } else if(ACTION_DELETE.equals(action)) {
			int type = intent.getIntExtra("Type", 1);
			String number = intent.getStringExtra("Number");
			boolean needToast = intent.getBooleanExtra("needToast", false);
			if(type == 1) {
				if(number.indexOf(",") >= 0) {
					String numbers[] = number.split(",");
					boolean success = false;
					for(int i=0; i<numbers.length; ++i) {
						if(0 < BlacklistUtils.deleteNumber(context, numbers[i], 1, false)) {
							success = true;
						}
					}
				    if(needToast) {
					    if(success) {
						    Toast.makeText(context, R.string.del_black_number_success, Toast.LENGTH_SHORT).show();
					    } else {
						    Toast.makeText(context, R.string.del_black_number_fail, Toast.LENGTH_SHORT).show();
					    }
				    }
				} else {
					BlacklistUtils.deleteNumber(context, number, 1, needToast);
				}
			}
        } else if(ACTION_RECORD.equals(action)) {
			int type = intent.getIntExtra("Type", 1);
			if(type == 1) {
				BlacklistUtils.recordSms(context, intent);
			} else if(type == 2) {
				BlacklistUtils.recordCall(context, intent);
			}
        } else if(Intent.ACTION_BOOT_COMPLETED.equals(action)) {
			Intent serviceIntent = new Intent(context, BlacklistService.class);
			context.startService(serviceIntent);
        }
    }

    private void addBlacklistWithCheck(final Context context, final int index, final String numbers[], 
    		final String name, final boolean needToast, final int suc, final int fail) {
        if(index < numbers.length) {
			Log.w(TAG, "index:" + index + ";numbers:" + numbers[index]);
            if(BlacklistUtils.checkNumberExist(context, numbers[index], 2)) {
                AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle(numbers[index]) // Tips
                    .setMessage(R.string.dialog_move_to_blacklist_summary)			
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addBlacklistWithCheck(context, index+1, numbers, name, needToast, suc, fail);
                        }
                    })
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            BlacklistUtils.deleteNumber(context, numbers[index], 2, false);
                            if( null != BlacklistUtils.insertNumber(context, null, numbers[index], 1, false)) {
                            	addBlacklistWithCheck(context, index+1, numbers, name, needToast, suc + 1, fail);
                            } else {
                            	addBlacklistWithCheck(context, index+1, numbers, name, needToast, suc, fail + 1);
                            }
                        }
                    }).create();
                dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }else {
            	if( null != BlacklistUtils.insertNumber(context, null, numbers[index], 1, false)) {
					addBlacklistWithCheck(context, index+1, numbers, name, needToast, suc + 1, fail);
            	} else {
					addBlacklistWithCheck(context, index+1, numbers, name, needToast, suc, fail + 1);
            	}
            }
        } else {
            if(needToast && suc > 0) {
				Toast.makeText(context, R.string.add_black_number_success, Toast.LENGTH_SHORT).show();
        	} else if(needToast && fail > 0) {
				Toast.makeText(context, R.string.add_black_number_fail, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
