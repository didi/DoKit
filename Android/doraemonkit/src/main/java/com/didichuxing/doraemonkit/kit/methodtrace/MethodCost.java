package com.didichuxing.doraemonkit.kit.methodtrace;

import android.app.Application;
import android.os.Build;
import android.os.Debug;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ThreadUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-10-09-17:25
 * 描    述：函数耗时工具类 由于通过系统收集函数调用栈存在一定的性能损耗，得到的结果不真实 只能作为大致的参考
 * startMethodTracing(startMethodTracingSampling) 和stopMethodTracingAndPrintLog 需要配对使用
 * 其中startMethodTracing是不采样的 所以在操作会产生大量的数据 导致缓存区会一下就满了 所以该方法建议该方法针对在一个方法中调用 类似demo
 * startMethodTracingSampling采用的采样的模式 可以再整个activity的生命周期中使用
 * <p>
 * 生成的trace文件位于getExternalFilesDir() 下 **.trace
 * 修订历史：  该功能已经废弃 请使用插件的函数耗时统计 使用手册:http://xingyun.xiaojukeji.com/docs/dokit/#/TimeProfiler
 * ================================================
 *
 */
@Deprecated
public class MethodCost {
    private static final String TAG = "MethodTrace";
    /**
     * storage/sdcard/Android/data/com.didichuxing.doraemondemo/files/  高版本的.trace文件路径
     */
    private static final String ROOT_APP_PATH = PathUtils.getExternalAppFilesPath() + File.separator;
    /**
     * storage/sdcard/ 低版本的.trace文件路径
     */
    private static final String ROOT_STORAGE_PATH = PathUtils.getExternalStoragePath() + File.separator;

    private static final String packageName = AppUtils.getAppPackageName();

    /**
     * @param traceFileName Path to the trace log file to create. If {@code null},
     *                      this will default to "dmtrace.trace". If the file already
     *                      exists, it will be truncated. If the path given does not end
     *                      in ".trace", it will be appended for you.
     * @param bufferSize    The maximum amount of trace data we gather. If not
     *                      given, it defaults to 16MB.
     */
    public static void startMethodTracing(final String traceFileName, final int bufferSize) {
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                Debug.startMethodTracing(traceFileName, bufferSize, 0);
                return null;
            }

            @Override
            public void onSuccess(Object result) {

            }
        });


    }

    public static Application APPLICATION;


    /**
     * @param traceFileName Path to the trace log file to create. If {@code null},
     *                      this will default to "dmtrace.trace". If the file already
     *                      exists, it will be truncated. If the path given does not end
     *                      in ".trace", it will be appended for you.
     */
    public static void startMethodTracing(String traceFileName) {
        startMethodTracing(traceFileName, 32 * 1024 * 1024);
    }


    /**
     * @param traceFileName Path to the trace log file to create. If {@code null},
     *                      this will default to "dmtrace.trace". If the file already
     *                      exists, it will be truncated. If the path given does not end
     *                      in ".trace", it will be appended for you.
     * @param bufferSize    The maximum amount of trace data we gather. If not
     *                      given, it defaults to 16MB.
     * @param intervalUs    The amount of time between each sample in microseconds.
     */
    public static void startMethodTracingSampling(String traceFileName, int bufferSize, int intervalUs) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Debug.startMethodTracingSampling(traceFileName, bufferSize, intervalUs);
        } else {
            Log.e(TAG, "current api need OS Api level 21");
        }
    }

    /**
     * @param traceFileName Path to the trace log file to create. If {@code null},
     *                      this will default to "dmtrace.trace". If the file already
     *                      exists, it will be truncated. If the path given does not end
     *                      in ".trace", it will be appended for you.
     */
    public static void startMethodTracingSampling(String traceFileName) {
        startMethodTracingSampling(traceFileName, 16 * 1024 * 1024, 10);
    }


    /**
     * 结束方法耗时并打印日志
     *
     * @param traceFileName .trace 的文件名 不需要后缀
     * @param needIndent    是否需要缩进
     */
    public static void stopMethodTracingAndPrintLog(String traceFileName, boolean needIndent, MethodCostCallback appStartCallback) {
        Debug.stopMethodTracing();
        printLog(traceFileName, needIndent, appStartCallback);
    }


    /**
     * 结束方法耗时并打印日志
     *
     * @param traceFileName .trace 的文件名 不需要后缀
     */
    public static void stopMethodTracingAndPrintLog(String traceFileName) {
        stopMethodTracingAndPrintLog(traceFileName, true, null);
    }


    /**
     * 结束方法耗时并打印日志
     *
     * @param traceFileName .trace 的文件名 不需要后缀
     */
    public static void stopMethodTracingAndPrintLog(String traceFileName, MethodCostCallback appStartCallback) {
        stopMethodTracingAndPrintLog(traceFileName, true, appStartCallback);
    }

    static String filePath;

    /**
     * @param traceFileName
     */
    private static void printLog(final String traceFileName, final boolean needIndent, final MethodCostCallback appStartCallback) {

        ThreadUtils.executeByCachedWithDelay(new ThreadUtils.Task<ArrayList<OrderBean>>() {
            @Override
            public ArrayList<OrderBean> doInBackground() throws Throwable {
                if (APPLICATION != null) {
                    filePath = APPLICATION.getExternalFilesDir(null) + File.separator + traceFileName + ".trace";
                } else {
                    filePath = ROOT_APP_PATH + traceFileName + ".trace";
                }
                //假如不存在改文件 则用低版本的路径再次尝试
                if (!FileUtils.isFileExists(filePath)) {
                    filePath = ROOT_STORAGE_PATH + traceFileName + ".trace";
                }
                if (!FileUtils.isFileExists(filePath) || FileUtils.getFileLength(filePath) == 0) {
                    filePath = PathUtils.getInternalAppFilesPath() + File.separator + "appStart.trace";
                }
                File file = new File(filePath);
                if (!FileUtils.isFileExists(filePath)) {
                    Log.i(TAG, "filePath==>" + filePath + "  not found");
                    appStartCallback.onError("no matched file", filePath);
                    return null;
                }
                //LogHelper.i(TAG, "file size===>" + FileUtils.getFileSize(file));
                TraceScanner scanner = new TraceScanner(file);
                scanner.setPackageName(packageName);
                return scanner.convertFile();
            }

            @Override
            public void onSuccess(ArrayList<OrderBean> orderBeans) {
                if (orderBeans == null || orderBeans.size() == 0) {
                    Log.i(TAG, "filePath==>" + filePath + "  not found");
                    if (appStartCallback != null) {
                        appStartCallback.onError("no match method", filePath);
                    }
                    return;
                } else {
                    if (traceFileName.equals("appStart")) {
                        String destFilePath = PathUtils.getInternalAppFilesPath() + File.separator + "appStart.trace";
                        FileUtils.copyFile(filePath, destFilePath);
                    }
                }

                if (appStartCallback != null) {
                    appStartCallback.onCall(filePath, orderBeans);
                }


                Log.i(TAG, "-------" + orderBeans.get(0).getFunctionName() + " Call Chain-----------------");

                for (int index = 0; index < orderBeans.size(); index++) {
                    OrderBean orderBean = orderBeans.get(index);
                    if (needIndent) {
                        Log.i(TAG, strMultiply("*", index + 1) + orderBean.getFunctionName() + "  cost time: " + orderBean.getCostTime() + "μs");
                    } else {
                        Log.i(TAG, "**" + orderBean.getFunctionName() + "  cost time: " + orderBean.getCostTime() + "μs");
                    }
                }

                //FileUtils.delete(filePath);

            }

            @Override
            public void onCancel() {
                //FileUtils.delete(filePath);
                Log.i(TAG, "--------onCancel---------");
            }

            @Override
            public void onFail(Throwable t) {
                //FileUtils.delete(filePath);
                Log.e(TAG, "throwable: " + t.getMessage());
            }
        }, 2, TimeUnit.SECONDS);
    }

    private static String strMultiply(String singleStr, int count) {
        String str = singleStr + singleStr;
        for (int i = 0; i < count * 2; i++) {
            str = str + singleStr;
        }
        return str;
    }
}
