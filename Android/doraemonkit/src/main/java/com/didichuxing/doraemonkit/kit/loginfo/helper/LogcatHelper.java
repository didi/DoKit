package com.didichuxing.doraemonkit.kit.loginfo.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogcatHelper {
    private static final String TAG = "LogcatHelper";
    public static final String BUFFER_MAIN = "main";
    public static final String BUFFER_EVENTS = "events";
    public static final String BUFFER_RADIO = "radio";


    public static Process getLogcatProcess(String buffer) throws IOException {

        List<String> args = getLogcatArgs(buffer);

        return RuntimeHelper.exec(args);
    }

    private static List<String> getLogcatArgs(String buffer) {
        List<String> args = new ArrayList<>(Arrays.asList("logcat", "-v", "time"));

        // for some reason, adding -b main excludes log output from AndroidRuntime runtime exceptions,
        // whereas just leaving it blank keeps them in.  So do not specify the buffer if it is "main"
        if (!buffer.equals(BUFFER_MAIN)) {
            args.add("-b");
            args.add(buffer);
        }

        return args;
    }

    public static String getLastLogLine(String buffer) {
        Process dumpLogcatProcess = null;
        BufferedReader reader = null;
        String result = null;
        try {

            List<String> args = getLogcatArgs(buffer);
            args.add("-d"); // -d just dumps the whole thing

            dumpLogcatProcess = RuntimeHelper.exec(args);
            reader = new BufferedReader(new InputStreamReader(dumpLogcatProcess
                    .getInputStream()), 8192);

            String line;
            while ((line = reader.readLine()) != null) {
                result = line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dumpLogcatProcess != null) {
                RuntimeHelper.destroy(dumpLogcatProcess);
                // LogHelper.d(TAG,"destroyed 1 dump logcat process");
            }

        }

        return result;
    }
}
