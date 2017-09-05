package com.gome.blacklist.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import com.gome.blacklist.Contact;

public class ToolUtils {

    private static Toast mToast;
    private static Handler sMainThreadHandler;

    /**
     * @return a {@link Handler} tied to the main thread.
     */
    public static Handler getMainThreadHandler() {
        if (sMainThreadHandler == null) {
            // No need to synchronize -- it's okay to create an extra Handler, which will be used
            // only once and then thrown away.
            sMainThreadHandler = new Handler(Looper.getMainLooper());
        }
        return sMainThreadHandler;
    }

    /**
     * Check if in main thread.
     * @return
     */
    public static boolean inMainThread() {
        return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
    }

    /**
     * A thread safe way to show a Toast.  Can be called from any thread.
     *
     * @param context context
     * @param resId Resource ID of the message string.
     */
    public static void showToast(Context context, int resId) {
        showToast(context, context.getResources().getString(resId));
    }

    /**
     * A thread safe way to show a Toast.  Can be called from any thread.
     *
     * @param context context
     * @param message Message to show.
     */
    public static void showToast(final Context context, final String message) {
        getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                if (mToast == null) {
                    mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                } else {
                    mToast.setText(message);
                }
                mToast.show();
            }
        });
    }

    public static String getTimerInSecond(int sec) {
        return String.format("%02d:%02d:%02d", sec / 3600, (sec % 3600) / 60, sec % 60);
    }

    public static String getTimerInMilliSecond(int milliSec) {
        int sec = milliSec / 1000;
        return String.format("%02d:%02d:%02d", sec / 3600, (sec % 3600) / 60, sec % 60);
    }

    public static <E> ArrayList<E> newArrayList(Iterable<? extends E> elements) {
        checkNotNull(elements); // for GWT
        // Let ArrayList's sizing logic work, if possible
        return (elements instanceof Collection)
                ? new ArrayList<E>(cast(elements))
                : newArrayList(elements.iterator());
    }

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    static <T> Collection<T> cast(Iterable<T> iterable) {
        return (Collection<T>) iterable;
    }

    public static <E> ArrayList<E> newArrayList(Iterator<? extends E> elements) {
        checkNotNull(elements); // for GWT
        ArrayList<E> list = newArrayList();
        while (elements.hasNext()) {
            list.add(elements.next());
        }
        return list;
    }

    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<E>();
    }
    
    public static String getContactNameNumber(String name, String number) {
    	if (TextUtils.isEmpty(name) || number.equals(name.replaceAll(" ",""))) {
    		return number;
		} else {
    	    return name + " " + number;
		}
    }

    public static String formatDate(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(year, month, day);

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        return format.format(cal.getTime());
    }

    public static String formatTime(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        cal.set(year, month, day, hour, minute, second);

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return format.format(cal.getTime());
    }

    public static String formatTimeWithHourAndMinute(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        cal.set(year, month, day, hour, minute, second);

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(cal.getTime());
    }

    public static String formatTimeAsSystemLocale(long time) {
        return DateFormat.getDateTimeInstance().format(new Date(time));
    }

    /**
     * Format a plural string.
     *
     * @param resource The identity of the resource, which must be a R.plurals
     * @param count The number of items.
     */
    public static String formatPlural(Context context, int resource, int count) {
        final CharSequence formatString = context.getResources().getQuantityText(resource, count);
        return String.format(formatString.toString(), count);
    }

    public static Bitmap decodeBitmap(Context context, int resId) {
        return decodeBitmap(context.getResources(), resId);
    }

    public static Bitmap decodeBitmap(Resources res, int resId) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inInputShareable = true;
        InputStream inputStream = res.openRawResource(resId);
        return BitmapFactory.decodeStream(inputStream, null, options);
    }

    public static Bitmap decodeBitmapFromPath(String path) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(path, options);
    }
}
