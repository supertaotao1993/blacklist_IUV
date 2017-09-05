package com.gome.blacklist;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.pm.IPackageManager;
import android.os.IBinder;
import android.util.Log;


public class ReflectUtils {
    
    public static final String TAG="ReflectUtils";

   
    public static Object LoadMethod(String cName,Object obj, String MethodName, Object[] params, Class [] ArgsClass ) throws Exception {

        Object retObject = null;

        try {
            Class cls = null;
            if (obj != null)
            {
                cls = obj.getClass();          
            }
            else
            {
                cls = Class.forName(cName);    
            }

            if(params != null){
                //Class [] ArgsClass = getArgsClass(params);
                Method meth = cls.getMethod(MethodName, ArgsClass);
                //meth.setAccessible(true);
                retObject = meth.invoke(obj, params);
            }
            else{
                Method meth = cls.getMethod(MethodName);
                //meth.setAccessible(true);
                retObject = meth.invoke(obj);
            }

        } catch (InvocationTargetException e) {
            Log.e(TAG,e.getTargetException().toString());
            throw e;
        } catch (Exception e) {
            System.err.println(e);
            throw e;
        }

        return retObject;
    }

    
    // get parameter list
     public static Class[] getArgsClass(Object[] params) {
        Class[] argClass =  new Class[params.length];
    
         if (params != null){
             for(int i =0;i<params.length;i++){
                 Log.d(TAG,"paramTypes[" +i + "]="  + argClass[i].toString());
                 argClass[i]=params[i].getClass();
             }
         }
         return argClass;
     }

    public static  Object get_instance_var(Object info, String var) {
        try {
            Class localClass1 = info.getClass();
            Field f = localClass1.getDeclaredField(var);
            f.setAccessible(true);
            Object ret = f.get(info);
            return ret;
        } catch (Exception e) {
            Log.e(TAG,"get_instance_var  error:"+e);
            e.printStackTrace();

        }
        return 0;
    }

    public static  Object get_class_var(String clsName, String var) {
        try {
            Class cls = Class.forName(clsName);
            Field fld = cls.getDeclaredField(var);
            fld.setAccessible(true);
            Object ret = fld.get(cls);
            return ret;
        } catch (Exception e) {
            Log.e(TAG,"get_class_var  error:"+e);
            e.printStackTrace();
        }
        return 0;
    }

    public static  IPackageManager get_IPackageManager() {
        try {
            Class cls = Class.forName("android.os.ServiceManager");
            Method method = ((Class) cls).getMethod("getService", String.class);
            return IPackageManager.Stub.asInterface((IBinder) ((Method) method).invoke(null, "package"));
        } catch (Exception e) {
            Log.i("PreferredActivities", "get_IPackageManager: " + e);
        }
        return null;

    }
    
    public static boolean checkClassExists(String clsName){
    	 boolean ret = false;
    	 try {
             Class cls = Class.forName(clsName);
             ret = true;
         } catch (Exception e) {
             Log.e("PreferredActivities", "checkClassExists class: " + clsName + ",not exist");
             Log.e("PreferredActivities", "checkClassExists class e: " + e);
         }
    	 Log.e("PreferredActivities", "checkClassExists  ret:" + ret + ",clsName:" + clsName);
    	 return ret;
    }
}
