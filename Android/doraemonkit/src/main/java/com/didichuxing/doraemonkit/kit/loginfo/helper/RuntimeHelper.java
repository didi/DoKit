package com.didichuxing.doraemonkit.kit.loginfo.helper;

import com.didichuxing.doraemonkit.kit.loginfo.util.ArrayUtil;

import java.io.IOException;
import java.util.List;

/**
 * Helper functions for running processes.
 *
 * @author nolan
 */
public class RuntimeHelper {

    /**
     * Exec the arguments, using root if necessary.
     *
     * @param args
     */
    public static Process exec(List<String> args) throws IOException {
        return Runtime.getRuntime().exec(ArrayUtil.toArray(args, String.class));
    }

    public static void destroy(Process process) {
        // if we're in JellyBean, then we need to kill the process as root, which requires all this
        // extra UnixProcess logic
            process.destroy();
    }

}