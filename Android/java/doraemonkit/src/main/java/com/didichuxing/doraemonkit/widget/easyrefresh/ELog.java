package com.didichuxing.doraemonkit.widget.easyrefresh;

import android.text.TextUtils;


public final class ELog {

    /**
     * Priority constant for the println method; use Log.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use Log.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use Log.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; use Log.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; use Log.e.
     */
    public static final int ERROR = 6;

    /**
     * Priority constant for the println method.
     */
    public static final int ASSERT = 7;
    
    /**
     * log是否可用
     */
    private static boolean isEnabled = true; 


    private ELog() {
    }
    
    /**
     * 设置log是否可用
     * @param enableBoolean
     */
    public static void setEnable(boolean enableBoolean) {
    	isEnabled  = enableBoolean;
    }
    
    /**
     * 检查log是否可用
     * @return boolean isEnabled
     */
    public static boolean isEnable() {
    	return isEnabled;
    }
    
    private static void log(LEVEL level, String tag, String msg, Throwable thr) {
    	if(!isEnabled ) {
    		return ;
    	}
    	
    	log2console(level, tag, msg, thr);
    }
    
    private static void log2console(LEVEL level, String tag, String msg, Throwable thr) {
    	boolean isFilter = false;

        
        if(!isFilter) {
        	switch (level) {
            case VERBOSE:
                if (thr == null) {
                    android.util.Log.v(tag, msg);
                } else {
                	android.util.Log.v(tag, msg, thr);
                }
                break;
            case DEBUG:
                if (thr == null) {
                	android.util.Log.d(tag, msg);
                } else {
                	android.util.Log.d(tag, msg, thr);
                }
                break;
            case INFO:
                if (thr == null) {
                	android.util.Log.i(tag, msg);
                } else {
                	android.util.Log.i(tag, msg, thr);
                }
                break;
            case WARN:
                if (thr == null) {
                	android.util.Log.w(tag, msg);
                } else if (TextUtils.isEmpty(msg)) {
                	android.util.Log.w(tag, thr);
                } else {
                	android.util.Log.w(tag, msg, thr);
                }
                break;
            case ERROR:
                if (thr == null) {
                	android.util.Log.e(tag, msg);
                } else {
                	android.util.Log.e(tag, msg, thr);
                }
                break;
            case ASSERT:
                if (thr == null) {
                	android.util.Log.wtf(tag, msg);
                } else if (TextUtils.isEmpty(msg)) {
                	android.util.Log.wtf(tag, thr);
                } else {
                	android.util.Log.wtf(tag, msg, thr);
                }
                break;
            default:
            	break;
        	}
        } else {
        	return;
        }
    }

    public static void v(String tag, String msg) {
    	log(LEVEL.VERBOSE, tag, msg, null);
    }

    public static void v(String tag, String msg, Throwable thr) {
    	log(LEVEL.VERBOSE, tag, msg, thr);
    }

    public static void d(String tag, String msg) {
    	log(LEVEL.DEBUG, tag, msg, null);
    }

    public static void d(String tag, String msg, Throwable thr) {
    	log(LEVEL.DEBUG, tag, msg, thr);
    }

    public static void i(String tag, String msg) {
    	log(LEVEL.INFO, tag, msg, null);
    }

    public static void i(String tag, String msg, Throwable thr) {
    	log(LEVEL.INFO, tag, msg, thr);   	
    }

    public static void w(String tag, String msg) {
    	log(LEVEL.WARN, tag, msg, null);   	
    }

    public static void w(String tag, String msg, Throwable thr) {
    	log(LEVEL.WARN, tag, msg, thr);  	
    }

    public static void w(String tag, Throwable thr) {
    	log(LEVEL.WARN, tag, "", thr);      
    }

    public static void e(String tag, String msg) {
    	log(LEVEL.ERROR, tag, msg, null);      
    }

    public static void e(String tag, String msg, Throwable thr) {
    	log(LEVEL.ERROR, tag, msg, thr);       
    }
    
    /**
     * 等级枚举，对应android原生log的等级
     * @author jjzheng
     *
     */
    public enum LEVEL {
        VERBOSE(2, "V"),
        DEBUG(3, "D"),
        INFO(4, "I"),
        WARN(5, "W"),
        ERROR(6, "E"),
        ASSERT(7, "A");

        final String levelString;
        final int level;

        //Supress default constructor for noninstantiability
        private LEVEL() {
            throw new AssertionError();
        }

        private LEVEL(int level, String levelString) {
            this.level = level;
            this.levelString = levelString;
        }

        public String getLevelString() {
            return this.levelString;
        }

        public int getLevel() {
            return this.level;
        }
    }


    


}
