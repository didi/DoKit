package com.example.androidpowercomsumption.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ADB {

    private final Context context;

    // abd的工作目录
    private final String adbPath;

    public ADB(Context context) {
        this.context = context;
        this.adbPath = context.getApplicationInfo().nativeLibraryDir + "/libadb.so";
        String TAG = "Battery";
        Log.d(TAG, this.adbPath);
    }

    /**
     * 启动adb服务
     */
    public void initADBServer() throws InterruptedException, IOException {
        boolean secureSettingsGranted = this.context.checkSelfPermission("android.permission.WRITE_SECURE_SETTINGS") == PackageManager.PERMISSION_GRANTED;
        boolean wirelessDebugEnabled = Settings.Global.getInt(this.context.getContentResolver(), "adb_wifi_enabled", 0) == 1;

        // 开启无线调试功能
        if (!wirelessDebugEnabled) {
            Settings.Global.putInt(
                    this.context.getContentResolver(),
                    "adb_wifi_enabled",
                    1
            );
            Thread.sleep(30000);
        }
        List<String> command = new ArrayList<>();
        // 启动服务
        command.add("start-server");
        this.execCommand(command).waitFor();
        command = new ArrayList<>();
        command.add("wait-for-device");
        Process process = this.execCommand(command);
        process.waitFor(5, TimeUnit.SECONDS);

//        if (!secureSettingsGranted) {
//            String cmd = "shell pm grant com.draco.ladb android.permission.WRITE_SECURE_SETTINGS &> /dev/null";
//            String[] cmdList = cmd.split(" ");
//            this.execCommand(Arrays.asList(cmdList));
//        }

    }

    // 将之前的电量信息清楚，保证结果的正确性
    public void clearBatteryInfo() throws IOException, InterruptedException {
        String cmd = "shell dumpsys batterystats --reset";
        String[] cmdStrs = cmd.split(" ");
        List<String> command = new ArrayList<>();
        for (String str : cmdStrs) {
            command.add(str);
        }
        this.execCommand(command).waitFor();
    }

    /**
     * 获取当前正在运行的app的耗电量
     */
    public void getCurRunAppBatteryInfo() {
        ActivityManager am = (ActivityManager)
                this.context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
        if (runningTasks != null && !runningTasks.isEmpty()) {
            ActivityManager.RunningTaskInfo taskInfo = runningTasks.get(0);
            // 获得当前运行的app的包名
            String curApppkg = taskInfo.topActivity.getPackageName();
            String cmd = "shell dumpsys batterystats " + curApppkg;
            String[] cmdStrs = cmd.split(" ");
            List<String> command = new ArrayList<>();
            for (String str : cmdStrs) {
                command.add(str);
            }
            Process process;
            try {
                process = this.execCommand(command);
                int exitVal = process.waitFor();
                if (exitVal != 0) Log.e("ADBError", "没有获取到当前app的电量信息");
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = input.readLine()) != null) {
                    Log.d("Battery", line);
                }
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取电量信息，并进行分析返回结果
     */
    public void getAllBatteryInfo() throws IOException, InterruptedException {
        String cmd = "shell dumpsys batterystats";
        String[] cmdStrs = cmd.split(" ");
        List<String> command = new ArrayList<>();
        for (String str : cmdStrs) {
            command.add(str);
        }
        Process process = this.execCommand(command);
        int exitVal = process.waitFor();
        if (exitVal != 0) Log.e("ADBError", "没有获取到电量信息");
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = input.readLine()) != null) {
            Log.d("Battery", line);
        }
        input.close();

    }


    /**
     * 与设备匹配
     *
     * @param port     端口
     * @param pairCode 匹配码
     * @return 是否匹配成功
     */
    public boolean pair(String port, String pairCode) throws IOException, InterruptedException {
        // 执行该命令会提示输入pairCode
        List<String> pairCommand = new ArrayList<>();
        pairCommand.add(this.adbPath);
        pairCommand.add("pair");
        pairCommand.add("localhost:" + port);
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(pairCommand);
        // 设置环境
        processBuilder.environment().put("HOME", this.context.getFilesDir().getPath());
        processBuilder.environment().put("TMPDIR", this.context.getCacheDir().getPath());
        Process pairShell = processBuilder.start();
        Thread.sleep(5000);

        // 输入配对码
        PrintStream printStream = new PrintStream(pairShell.getOutputStream());
        printStream.println(pairCode);
        printStream.flush();

        pairShell.waitFor(10, TimeUnit.SECONDS);
        pairShell.destroyForcibly();

        // 返回匹配结果
        return pairShell.exitValue() == 0;

    }


    /**
     * 执行adb命令
     *
     */
    public Process execCommand(List<String> command) throws IOException {
        command.add(0, this.adbPath);
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(command);
        processBuilder.environment().put("HOME", this.context.getFilesDir().getPath());
        processBuilder.environment().put("TMPDIR", this.context.getCacheDir().getPath());
        return processBuilder.start();
    }


}
