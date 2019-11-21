package com.didichuxing.doraemonkit.kit.methodtrace;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-10-09-17:25
 * 描    述：函数耗时工具类 由于通过系统收集函数调用栈存在一定的性能损耗，得到的结果不真实 只能作为大致的参考
 * startMethodTracing(startMethodTracingSampling) 和stopMethodTracingAndPrintLog 需要配对使用
 * 其中startMethodTracing是不采样的 所以在操作会产生大量的数据 导致缓存区会一下就满了 所以该方法建议该方法针对在一个方法中调用 类似demo
 * startMethodTracingSampling采用的采样的模式 可以再整个activity的生命周期中使用
 * 修订历史：
 * ================================================
 */
public class MethodCost {

    /**
     * @param traceFileName Path to the trace log file to create. If {@code null},
     *                      this will default to "dmtrace.trace". If the file already
     *                      exists, it will be truncated. If the path given does not end
     *                      in ".trace", it will be appended for you.
     * @param bufferSize    The maximum amount of trace data we gather. If not
     *                      given, it defaults to 16MB.
     */
    public static void startMethodTracing(String traceFileName, int bufferSize) {

    }

    /**
     * @param traceFileName Path to the trace log file to create. If {@code null},
     *                      this will default to "dmtrace.trace". If the file already
     *                      exists, it will be truncated. If the path given does not end
     *                      in ".trace", it will be appended for you.
     */
    public static void startMethodTracing(String traceFileName) {

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

    }

    /**
     * @param traceFileName Path to the trace log file to create. If {@code null},
     *                      this will default to "dmtrace.trace". If the file already
     *                      exists, it will be truncated. If the path given does not end
     *                      in ".trace", it will be appended for you.
     */
    public static void startMethodTracingSampling(String traceFileName) {

    }


    /**
     * 结束方法耗时并打印日志
     *
     * @param traceFileName .trace 的文件名 不需要后缀
     * @param needIndent    是否需要缩进
     */
    public static void stopMethodTracingAndPrintLog(String traceFileName, boolean needIndent) {

    }


    /**
     * 结束方法耗时并打印日志
     *
     * @param traceFileName .trace 的文件名 不需要后缀
     */
    public static void stopMethodTracingAndPrintLog(String traceFileName) {

    }


}
