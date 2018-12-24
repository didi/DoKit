package com.didichuxing.doraemonkit.adb;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.cgutman.adblib.AdbBase64;
import com.cgutman.adblib.AdbConnection;
import com.cgutman.adblib.AdbCrypto;
import com.cgutman.adblib.AdbStream;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by yangmenglin on 2018/11/1.
 */

public class AdbConnector {
    private static final String TAG = "AdbConnector";
    private AdbConnection connection;
    private AdbStream stream;

    private AdbBase64 getBase64Impl() {
        return new AdbBase64() {
            @Override
            public String encodeToString(byte[] data) {
                return Base64.encodeToString(data, Base64.DEFAULT);
            }
        };
    }

    // This function loads a keypair from the specified files if one exists, and if not,
    // it creates a new keypair and saves it in the specified files
    private AdbCrypto setupCrypto(String pubKeyFile, String privKeyFile)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        File pub = new File(pubKeyFile);
        File priv = new File(privKeyFile);
        AdbCrypto c = null;

        // Try to load a key pair from the files
        if (pub.exists() && priv.exists()) {
            try {
                c = AdbCrypto.loadAdbKeyPair(getBase64Impl(), priv, pub);
            } catch (Exception e) {
                // Failed to read from file
                c = null;
            }
        }

        if (c == null) {
            // We couldn't load a key, so let's generate a new one
            c = AdbCrypto.generateAdbKeyPair(getBase64Impl());

            // Save it
            c.saveAdbKeyPair(priv, pub);
            Log.d(TAG, "Generated new keypair");
        } else {
            Log.d(TAG, "Loaded existing keypair");
        }

        return c;
    }


    private AdbConnection connection(Context context) throws Exception {

        // Setup the crypto object required for the AdbConnection
        String path = context.getCacheDir().getAbsolutePath();
        Log.d(TAG, "connection path " + path);
        AdbCrypto crypto = setupCrypto(path + File.separatorChar + "pub.key",
                path + File.separatorChar + "priv.key");

        Log.e(TAG, "Socket connecting...");
        Socket sock = new Socket(AdbConstant.HOST, AdbConstant.PORT);
        // Connect the socket to the remote host

        Log.e(TAG, "Socket connected");

        // Construct the AdbConnection object

        AdbConnection adb = AdbConnection.create(sock, crypto);

        // Start the application layer connection process
        Log.e(TAG, "ADB connecting...");

        adb.connect();

        Log.e(TAG, "ADB connected");


        return adb;
    }


    public String openShell(Context context,String cmd) throws Exception {
        if (connection == null) {
            connection = connection(context);
        }
        stream = connection.open(cmd);
        StringBuilder stringBuilder = new StringBuilder();
        byte[] bytes = stream.read();
        stringBuilder.append(new String(bytes));
        while (bytes != null) {
            stringBuilder.append(new String(bytes));
            try {
                bytes = stream.read();
            } catch (Exception e) {
                bytes = null;
                Log.d("morning",   e.getMessage());
            }
        }
        Log.d("morning", "length is " + stringBuilder.length());
        return stringBuilder.toString();
    }
}
