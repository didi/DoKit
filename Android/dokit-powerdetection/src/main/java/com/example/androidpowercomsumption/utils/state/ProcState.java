package com.example.androidpowercomsumption.utils.state;

public class ProcState {
    public int id;
    public String comm = "";
    public String stat = "";

    public long utime = -1;
    public long stime = -1;

    public long cutime = -1;

    public long cstime = -1;

    public int numThreads = 0; //进程下线程的数量

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }


    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public long getStime() {
        return stime;
    }

    public void setStime(long stime) {
        this.stime = stime;
    }

    public long getCstime() {
        return cstime;
    }

    public void setCstime(long cstime) {
        this.cstime = cstime;
    }

    public long getUtime() {
        return utime;
    }

    public void setUtime(long utime) {
        this.utime = utime;
    }


    public long getJiffies() {
        return utime + stime + cutime + cstime;
    }

    public String getComm() {
        return comm;
    }

    public void setComm(String comm) {
        this.comm = comm;
    }

    public long getCutime() {
        return cutime;
    }


    public void setCutime(long cutime) {
        this.cutime = cutime;
    }

    @Override
    public String toString() {
        return "ProcState{" +
                "id=" + id +
                ", comm='" + comm + '\'' +
                ", stat='" + stat + '\'' +
                ", utime=" + utime +
                ", stime=" + stime +
                ", cutime=" + cutime +
                ", cstime=" + cstime +
                ", numThreads=" + numThreads +
                '}';
    }
}