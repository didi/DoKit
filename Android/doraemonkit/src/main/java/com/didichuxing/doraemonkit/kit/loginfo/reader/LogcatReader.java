package com.didichuxing.doraemonkit.kit.loginfo.reader;

import java.io.IOException;
import java.util.List;

public interface LogcatReader {

    /**
     * Read a single log line, ala BufferedReader.readLine().
     *
     * @return
     * @throws IOException
     */
    String readLine() throws IOException;

    /**
     * Kill the reader and close all resources without throwing any exceptions.
     */
    void killQuietly();

    boolean readyToRecord();

    List<Process> getProcesses();

}
