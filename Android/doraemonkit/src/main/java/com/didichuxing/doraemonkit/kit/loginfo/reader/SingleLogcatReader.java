package com.didichuxing.doraemonkit.kit.loginfo.reader;

import android.text.TextUtils;

import com.didichuxing.doraemonkit.kit.loginfo.helper.LogcatHelper;
import com.didichuxing.doraemonkit.kit.loginfo.helper.RuntimeHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

public class SingleLogcatReader extends AbsLogcatReader {

    private static final String TAG = "SingleLogcatReader";

    private Process logcatProcess;
    private BufferedReader bufferedReader;
    private String logBuffer;
    private String lastLine;

    public SingleLogcatReader(boolean recordingMode, String logBuffer, String lastLine) throws IOException {
        super(recordingMode);
        this.logBuffer = logBuffer;
        this.lastLine = lastLine;
        init();
    }

    private void init() throws IOException {
        // use the "time" log so we can see what time the logs were logged at
        logcatProcess = LogcatHelper.getLogcatProcess(logBuffer);

        bufferedReader = new BufferedReader(new InputStreamReader(logcatProcess
                .getInputStream()), 8192);
    }


    public String getLogBuffer() {
        return logBuffer;
    }


    @Override
    public void killQuietly() {
        if (logcatProcess != null) {
            RuntimeHelper.destroy(logcatProcess);
        }
    }

    @Override
    public String readLine() throws IOException {
        String line = bufferedReader.readLine();

        if (recordingMode && lastLine != null) { // still skipping past the 'last line'
            if (lastLine.equals(line) /*|| isAfterLastTime(line)*/) {
                lastLine = null; // indicates we've passed the last line
            }
        }

        return line;
    }

    private boolean isAfterLastTime(String line) {
        // doing a string comparison is sufficient to determine whether this line is chronologically
        // after the last line, because the format they use is exactly the same and
        // lists larger time period before smaller ones
        return isDatedLogLine(lastLine) && isDatedLogLine(line) && line.compareTo(lastLine) > 0;

    }

    private boolean isDatedLogLine(String line) {
        // 18 is the size of the logcat timestamp
        return (!TextUtils.isEmpty(line) && line.length() >= 18 && Character.isDigit(line.charAt(0)));
    }


    @Override
    public boolean readyToRecord() {
        return recordingMode && lastLine == null;
    }

    @Override
    public List<Process> getProcesses() {
        return Collections.singletonList(logcatProcess);
    }
}
