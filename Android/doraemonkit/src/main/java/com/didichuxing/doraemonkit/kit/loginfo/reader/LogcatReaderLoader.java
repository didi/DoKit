package com.didichuxing.doraemonkit.kit.loginfo.reader;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.didichuxing.doraemonkit.kit.loginfo.helper.LogcatHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * https://github.com/plusCubed/matlog
 */
public class LogcatReaderLoader implements Parcelable {

    public static final Parcelable.Creator<LogcatReaderLoader> CREATOR = new Parcelable.Creator<LogcatReaderLoader>() {
        @Override
        public LogcatReaderLoader createFromParcel(Parcel in) {
            return new LogcatReaderLoader(in);
        }

        @Override
        public LogcatReaderLoader[] newArray(int size) {
            return new LogcatReaderLoader[size];
        }
    };
    private Map<String, String> lastLines = new HashMap<>();
    private boolean recordingMode;
    private boolean multiple;

    private LogcatReaderLoader(Parcel in) {
        this.recordingMode = in.readInt() == 1;
        this.multiple = in.readInt() == 1;
        Bundle bundle = in.readBundle();
        for (String key : bundle.keySet()) {
            lastLines.put(key, bundle.getString(key));
        }
    }

    private LogcatReaderLoader(List<String> buffers, boolean recordingMode) {
        this.recordingMode = recordingMode;
        this.multiple = buffers.size() > 1;
        for (String buffer : buffers) {
            // no need to grab the last line if this isn't recording mode
            String lastLine = recordingMode ? LogcatHelper.getLastLogLine(buffer) : null;
            lastLines.put(buffer, lastLine);
        }
    }

    public static LogcatReaderLoader create(boolean recordingMode) {
        List<String> buffers = new ArrayList<>();
        buffers.add("main");
        return new LogcatReaderLoader(buffers, recordingMode);
    }

    public LogcatReader loadReader() throws IOException {
        LogcatReader reader;
        // single reader
        String buffer = lastLines.keySet().iterator().next();
        String lastLine = lastLines.values().iterator().next();
        reader = new SingleLogcatReader(recordingMode, buffer, lastLine);

        return reader;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(recordingMode ? 1 : 0);
        dest.writeInt(multiple ? 1 : 0);
        Bundle bundle = new Bundle();
        for (Entry<String, String> entry : lastLines.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }
        dest.writeBundle(bundle);
    }
}
