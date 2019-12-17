package com.didichuxing.doraemonkit.kit.methodtrace;

import com.android.tools.perflib.vmtrace.MethodInfo;
import com.android.tools.perflib.vmtrace.TraceAction;
import com.android.tools.perflib.vmtrace.VmTraceHandler;
import com.android.tools.perflib.vmtrace.VmTraceParser;
import com.didichuxing.doraemonkit.util.LogHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Stack;


/**
 * 文件扫描
 */
class TraceScanner {
    private static final String TAG = "TraceScanner";
    private File file;
    private String packageName = "";
    private AnalysisListener listener;

    public TraceScanner(File file) {
        super();
        this.file = file;
        System.out.println("file：" + file);
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName.trim();
    }

    public void setListener(AnalysisListener listener) {
        this.listener = listener;
    }

    public ArrayList<OrderBean> convertFile() {
        ArrayList<String> index = new ArrayList<>();
        final ArrayList<OrderBean> list = new ArrayList<>();
        final HashMap<String, Stack<OrderBean>> stackMap = new HashMap<>();
        final LinkedHashMap<Integer, String> threadInfoMap = new LinkedHashMap<>();
        final LinkedHashMap<Long, MethodInfo> methodInfoMap = new LinkedHashMap<>();
        final long[] count = {1};

        final String[] osName = {System.getProperty("os.name")};

        index.add("PackageName: " + packageName + "\n");
        index.add("开始解析\n");
        final String oName = packageName.replaceAll("[.]", "/");
        if (listener != null) {
            listener.startAnalysis();
        }
        if (!file.exists()) {
            LogHelper.e(TAG, "File does not exist");
        } else {
            VmTraceParser parser = new VmTraceParser(file, new VmTraceHandler() {
                @Override
                public void setVersion(int version) {
                    //System.out.println("version is " + version);
                }

                @Override
                public void setProperty(String key, String value) {
                /*System.out.println("setProperty key is " + key);
                System.out.println("setProperty value is " + value);*/
                }

                @Override
                public void addThread(int id, String name) {
                    threadInfoMap.put(id, name);
                    stackMap.put("" + id, new Stack<OrderBean>());
                }

                @Override
                public void addMethod(long id, MethodInfo info) {
                    methodInfoMap.put(id, info);
                }

                @Override
                public void addMethodAction(int threadId, long methodId, TraceAction methodAction, int threadTime, int globalTime) {
                    OrderBean orderBean = new OrderBean();
                    orderBean.setOrder(count[0]++);
                    String functionName = "";
                    if (methodInfoMap.get(methodId) != null) {
                        functionName = methodInfoMap.get(methodId).toString();
                    }

                    if ((methodAction == TraceAction.METHOD_ENTER || methodAction == TraceAction.METHOD_EXIT)
                            && (functionName.contains(packageName) || functionName.contains(oName))) {
                        orderBean.setTime("" + threadTime);
                        orderBean.setThreadId("" + threadId);
                        orderBean.setThreadName(threadInfoMap.get(threadId));
                        orderBean.setFunctionName(functionName);

                        if (methodAction == TraceAction.METHOD_ENTER) {
                            list.add(orderBean);
                            stackMap.get(orderBean.getThreadId()).push(orderBean);
                        }

                        if (methodAction == TraceAction.METHOD_EXIT) {
                            if (!stackMap.get(orderBean.getThreadId()).isEmpty()) {
                                OrderBean peek = stackMap.get(orderBean.getThreadId()).peek();
                                if (peek.getFunctionName().equals(orderBean.getFunctionName())) {
                                    OrderBean pop = stackMap.get(orderBean.getThreadId()).pop();
                                    String costTime = "" + Math.abs(Long.parseLong(orderBean.getTime()) - Long.parseLong(pop.getTime()));
                                    pop.setCostTime(costTime);
                                }
                            }
                        }
                    }

                   /* if (methodAction == TraceAction.METHOD_EXIT_UNROLL) {
                    }*/
                }

                @Override
                public void setStartTimeUs(long startTimeUs) {
                    //System.out.println("setStartTimeUs is " + startTimeUs);
                }
            });
            try {
                parser.parse();
                Collections.sort(list);
                // if (packageName.equals("")) {
                //outTxt(list, "./appMethodOrderTrace.txt");
                //}
            } catch (Exception e) {
                index.add("解析过程异常\n");
                e.printStackTrace();
            } finally {
                index.add("解析结束\n");
                if (listener != null) {
                    listener.afterAnalysis();
                }
            }
        }
        return list;

    }

    private static void outTxt(ArrayList<OrderBean> list, String fileName) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();

        for (OrderBean orderBean : list) {
            if (orderBean.isXit()) {
                continue;
            }
            stringBuffer.append(orderBean.getThreadId());
            stringBuffer.append("	");
            stringBuffer.append(orderBean.getThreadName());
            stringBuffer.append("	");
            stringBuffer.append(orderBean.getCostTime());
            stringBuffer.append("	");
            stringBuffer.append(orderBean.getFunctionName());
            stringBuffer.append("\n");
        }

        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(fileName);
        fileWriter.write(stringBuffer.toString());
        fileWriter.flush();
        fileWriter.close();
    }

    public interface AnalysisListener {
        void startAnalysis();

        void afterAnalysis();
    }

}
