package com.example.androidpowercomsumption.utils.state;

import android.os.Process;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * app 进程即线程状态，通过读 /proc/pid/stat 和  /proc/pid/task/tid/stat 文件里面的信息，
 * 主要是得到 app 运行的进程、线程名，app 运行时和 app 内线程的用户时间、系统时间、等待的系统时间和用户时间
 * <p>
 * /proc/pid/stat:
 * 4218 (owercomsumption) R 301 301 0 0 -1 4194624 8707 0 0 0 32 34 0 0 10
 * -10 18 0 195290 1306812416 30324 18446744073709551615 1504755712 1504782416
 * 4289779952 0 0 0 4612 1 1073775864 0 0 0 17 1 0 0 0 0 0 1504791360 1504791360
 * 1522069504 4289787537 4289787613 4289787613 4289789920 0
 * 字段:
 * - pid:  进程ID.
 * - comm: task_struct结构体的进程名
 * - state: 进程状态, 此处为R
 * - ppid: 父进程ID （父进程是指通过fork方式, 通过clone并非父进程）
 * - pgrp: 进程组ID
 * - session: 进程会话组ID
 * - tty_nr: 当前进程的tty终点设备号
 * - tpgid: 控制进程终端的前台进程号
 * - flags: 进程标识位, 定义在include/linux/sched.h中的PF_*, 此处等于1077952832
 * - minflt:  次要缺页中断的次数, 即无需从磁盘加载内存页. 比如COW和匿名页
 * - cminflt: 当前进程等待子进程的minflt
 * - majflt: 主要缺页中断的次数, 需要从磁盘加载内存页. 比如map文件
 * - majflt: 当前进程等待子进程的majflt
 * - utime: 该进程处于用户态的时间, 单位jiffies, 此处等于166114
 * - stime: 该进程处于内核态的时间, 单位jiffies, 此处等于129684
 * - cutime: 当前进程等待子进程的utime
 * - cstime: 当前进程等待子进程的utime
 * - priority: 进程优先级, 此次等于10.
 * - nice: nice值, 取值范围[19, -20], 此处等于-10
 * - num_threads: 线程个数, 此处等于221
 * - itrealvalue: 该字段已废弃, 恒等于0
 * - starttime: 自系统启动后的进程创建时间, 单位jiffies, 此处等于2284
 * - vsize: 进程的虚拟内存大小, 单位为bytes
 * - rss: 进程独占内存+共享库, 单位pages, 此处等于93087
 * - rsslim: rss大小上限
 * <p>
 * 说明:
 * 第10~17行主要是随着时间而改变的量；
 * 内核时间单位, sysconf(_SC_CLK_TCK)一般地定义为jiffies(一般地等于10ms)
 * starttime: 此值单位为jiffies, 结合/proc/stat的btime, 可知道每一个线程启动的时间点
 * 1500827856 + 2284/100 = 1500827856, 转换成北京时间为2017/7/24 0:37:58
 * 第四行数据很少使用,只说一下该行第7至9个数的含义:
 * signal: 即将要处理的信号, 十进制, 此处等于6660
 * blocked: 阻塞的信号, 十进制
 * sigignore: 被忽略的信号, 十进制, 此处等于36088
 */
public class ProcStateUtil {
    private static final String TAG = "AppStateApplication";

    /**
     * 根据pid/tid得到对应文件中的信息
     *
     * @param pid 进程号
     * @param tid 线程号，等于-1即为不存在
     * @return
     */
    public ProcState splicePath(int pid, int tid) {
        if (tid == -1) {
            return getInfoFromProcFile("/proc/" + pid + "/stat"); // 查看进程详细信息
        } else {
            return getInfoFromProcFile("/proc/" + pid + "/task/" + tid + "/stat"); // 查看线程详细信息
        }
    }

    /**
     * 读取 /proc/pid/stat 和  /proc/pid/task/tid/stat 文件里面的信息，
     *
     * @param path
     */
    public ProcState getInfoFromProcFile(String path) {
        ProcState procState = new ProcState();
        try {
            RandomAccessFile reader = new RandomAccessFile(path, "r");
            String procStr = reader.readLine();
            // 找出线程的名称，需要特殊处理
            int beginIndex = procStr.indexOf("(");
            int lastIndex = procStr.indexOf(")");
            String comm = procStr.substring(beginIndex + 1, lastIndex);
            procStr = procStr.substring(0, beginIndex) + procStr.substring(lastIndex + 2);
//            Log.d(TAG, procStr);
            String[] procStateInfo = procStr.split(" ");
//            Log.d(TAG, String.valueOf(procStateInfo.length));
//            for(String str:procStateInfo){
//                Log.d(TAG, str);
//            }
            procState.setId(Integer.parseInt(procStateInfo[0]));
            procState.setComm(comm);
            procState.setStat(procStateInfo[1]);
            procState.setUtime(Long.parseLong(procStateInfo[12]));
            procState.setStime(Long.parseLong(procStateInfo[13]));
            procState.setCutime(Long.parseLong(procStateInfo[14]));
            procState.setCstime(Long.parseLong(procStateInfo[15]));
            procState.setNumThreads(Integer.parseInt(procStateInfo[18]));
            Log.d(TAG, procState.toString());
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return procState;

    }

    /**
     * 获取进程下所有线程的线程信息
     */
    public List<ProcState> getAllThreadInfo() {
        String rootPath = "/proc/" + Process.myPid() + "/task/";
        List<ProcState> threadList = new ArrayList<>();
        File taskDir = new File(rootPath);
        try {
            if (taskDir.isDirectory()) {
                File[] subDirs = taskDir.listFiles();

                for (File file : subDirs) {
                    if (!file.isDirectory()) {
                        continue;
                    }
                    try {
                        ProcState threadState = splicePath(Process.myPid(), Integer.parseInt(file.getName()));
                        threadList.add(threadState);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return threadList;
    }

    // 获得CPU的运行时间
    public long getCPUStatus() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat/", "r");
            String procStr = reader.readLine();
            String[] cpuInfo = procStr.split(" ");
            long cpuTime = 0;
            for (int i = 1; i <= 7; i++) {
                cpuTime += Long.parseLong(cpuInfo[i]);
            }
            reader.close();
            return cpuTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
