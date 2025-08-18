package com.example.androidpowercomsumption.utils.monitor;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;

public class LogFileWriter {

    public static void write(String str) {
        String filePath = "/data/data/com.example.androidpowercomsumption/files/data.txt";
        try {
            FileWriter fileWriter = new FileWriter(filePath, true);
            fileWriter.write(str + "\n");
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
