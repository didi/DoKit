package com.didichuxing.doraemonkit.kit.health.model;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020-01-02-16:29
 * 描    述：app 健康体检上传服务器数据
 * 修订历史：
 * ================================================
 */
public class AppHealthInfo {
    /**
     * baseInfo : {"caseName":"iOS5.0版本性能测试","testPerson":"易小翔","platfom":"iOS","time":"2019-12-12 : 11:12:30","phoneMode":"iphone6S","systemVersion":"13","appNmae":"Dokit","appVersion":"1.0.0","dokitVersion":"2.0.0"}
     * data : {"appStart":{"costTime":"3200","costDetail":"代码耗时字符串","loadFunc":[{"className":"ClassA","costTime":"15"},{"className":"ClassB","costTime":"30"}]},"cpu":[{"page":"HomeViewController","values":[{"time":"时间戳","value":"0.5"},{"time":"时间戳","value":"0.8"}]},{"page":"MapViewController","values":[{"time":"时间戳","value":"0.5"},{"time":"时间戳","value":"0.8"}]}],"memory":[{"page":"HomeViewController","values":[{"time":"时间戳","value":"80"},{"time":"时间戳","value":"81"}]},{"page":"MapViewController","values":[{"time":"时间戳","value":"90"},{"time":"时间戳","value":"91"}]}],"fps":[{"page":"HomeViewController","values":[{"time":"时间戳","value":"60"},{"time":"时间戳","value":"59"}]},{"page":"MapViewController","values":[{"time":"时间戳","value":"50"},{"time":"时间戳","value":"60"}]}],"network":[{"page":"HomeViewController","values":[{"time":"时间戳","url":"http://www.baidu.com","up":"100","down":"200","code":"200","method":"Get"},{"time":"时间戳","url":"http://www.taobao.com","up":"100","down":"200","code":"200","method":"Post"}]},{"page":"MapViewController","values":[{"time":"时间戳","url":"http://www.baidu.com","up":"100","down":"200","code":"200","method":"Get"},{"time":"时间戳","url":"http://www.taobao.com","up":"100","down":"200","code":"200","method":"Post"}]}],"block":[{"page":"HomeViewController","blockTime":"4.2","detail":"卡顿堆栈"},{"page":"MapViewController","blockTime":"5.2","detail":"卡顿堆栈"}],"subThreadUI":[{"page":"HomeViewController","detail":"代码堆栈"},{"page":"MapViewController","detail":"代码堆栈"}],"uiLevel":[{"page":"HomeViewController","level":"10","detail":"层级引用链"},{"page":"MapViewController","level":"10","detail":"层级引用链"}],"leak":[{"page":"HomeViewController","detail":"内存泄漏详情"},{"page":"MapViewController","detail":"内存泄漏详情"}],"pageLoad":[{"page":"HomeViewController","time":"120"},{"page":"MapViewController","time":"120"}],"bigFile":[{"fileName":"fileName1","fileSize":"30M","filePath":"/data/json/fileName1"}]}
     */

    private BaseInfoBean baseInfo;
    private DataBean data;

    public BaseInfoBean getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(BaseInfoBean baseInfo) {
        this.baseInfo = baseInfo;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class BaseInfoBean {
        /**
         * caseName : iOS5.0版本性能测试
         * testPerson : 易小翔
         * platfom : iOS
         * time : 2019-12-12 : 11:12:30
         * phoneMode : iphone6S
         * systemVersion : 13
         * appNmae : Dokit
         * appVersion : 1.0.0
         * dokitVersion : 2.0.0
         */

        private String caseName;
        private String testPerson;
        private String platform;
        private String time;
        private String phoneMode;
        private String systemVersion;
        private String appName;
        private String appVersion;
        private String dokitVersion;
        private String pId;

        public String getpId() {
            return pId;
        }

        public void setpId(String pId) {
            this.pId = pId;
        }

        public String getCaseName() {
            return caseName;
        }

        public void setCaseName(String caseName) {
            this.caseName = caseName;
        }

        public String getTestPerson() {
            return testPerson;
        }

        public void setTestPerson(String testPerson) {
            this.testPerson = testPerson;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getPhoneMode() {
            return phoneMode;
        }

        public void setPhoneMode(String phoneMode) {
            this.phoneMode = phoneMode;
        }

        public String getSystemVersion() {
            return systemVersion;
        }

        public void setSystemVersion(String systemVersion) {
            this.systemVersion = systemVersion;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getAppVersion() {
            return appVersion;
        }

        public void setAppVersion(String appVersion) {
            this.appVersion = appVersion;
        }

        public String getDokitVersion() {
            return dokitVersion;
        }

        public void setDokitVersion(String dokitVersion) {
            this.dokitVersion = dokitVersion;
        }
    }

    public static class DataBean {
        /**
         * appStart : {"costTime":"3200","costDetail":"代码耗时字符串","loadFunc":[{"className":"ClassA","costTime":"15"},{"className":"ClassB","costTime":"30"}]}
         * cpu : [{"page":"HomeViewController","values":[{"time":"时间戳","value":"0.5"},{"time":"时间戳","value":"0.8"}]},{"page":"MapViewController","values":[{"time":"时间戳","value":"0.5"},{"time":"时间戳","value":"0.8"}]}]
         * memory : [{"page":"HomeViewController","values":[{"time":"时间戳","value":"80"},{"time":"时间戳","value":"81"}]},{"page":"MapViewController","values":[{"time":"时间戳","value":"90"},{"time":"时间戳","value":"91"}]}]
         * fps : [{"page":"HomeViewController","values":[{"time":"时间戳","value":"60"},{"time":"时间戳","value":"59"}]},{"page":"MapViewController","values":[{"time":"时间戳","value":"50"},{"time":"时间戳","value":"60"}]}]
         * network : [{"page":"HomeViewController","values":[{"time":"时间戳","url":"http://www.baidu.com","up":"100","down":"200","code":"200","method":"Get"},{"time":"时间戳","url":"http://www.taobao.com","up":"100","down":"200","code":"200","method":"Post"}]},{"page":"MapViewController","values":[{"time":"时间戳","url":"http://www.baidu.com","up":"100","down":"200","code":"200","method":"Get"},{"time":"时间戳","url":"http://www.taobao.com","up":"100","down":"200","code":"200","method":"Post"}]}]
         * block : [{"page":"HomeViewController","blockTime":"4.2","detail":"卡顿堆栈"},{"page":"MapViewController","blockTime":"5.2","detail":"卡顿堆栈"}]
         * subThreadUI : [{"page":"HomeViewController","detail":"代码堆栈"},{"page":"MapViewController","detail":"代码堆栈"}]
         * uiLevel : [{"page":"HomeViewController","level":"10","detail":"层级引用链"},{"page":"MapViewController","level":"10","detail":"层级引用链"}]
         * leak : [{"page":"HomeViewController","detail":"内存泄漏详情"},{"page":"MapViewController","detail":"内存泄漏详情"}]
         * pageLoad : [{"page":"HomeViewController","time":"120"},{"page":"MapViewController","time":"120"}]
         * bigFile : [{"fileName":"fileName1","fileSize":"30M","filePath":"/data/json/fileName1"}]
         */

        private AppStartBean appStart;
        private List<PerformanceBean> cpu;
        private List<PerformanceBean> memory;
        private List<PerformanceBean> fps;
        private List<NetworkBean> network;
        private List<BlockBean> block;
        private List<SubThreadUIBean> subThreadUI;
        private List<UiLevelBean> uiLevel;
        private List<LeakBean> leak;
        private List<PageLoadBean> pageLoad;
        private List<BigFileBean> bigFile;

        public AppStartBean getAppStart() {
            return appStart;
        }

        public void setAppStart(AppStartBean appStart) {
            this.appStart = appStart;
        }

        public List<PerformanceBean> getCpu() {
            return cpu;
        }

        public void setCpu(List<PerformanceBean> cpu) {
            this.cpu = cpu;
        }

        public List<PerformanceBean> getMemory() {
            return memory;
        }

        public void setMemory(List<PerformanceBean> memory) {
            this.memory = memory;
        }

        public List<PerformanceBean> getFps() {
            return fps;
        }

        public void setFps(List<PerformanceBean> fps) {
            this.fps = fps;
        }

        public List<NetworkBean> getNetwork() {
            return network;
        }

        public void setNetwork(List<NetworkBean> network) {
            this.network = network;
        }

        public List<BlockBean> getBlock() {
            return block;
        }

        public void setBlock(List<BlockBean> block) {
            this.block = block;
        }

        public List<SubThreadUIBean> getSubThreadUI() {
            return subThreadUI;
        }

        public void setSubThreadUI(List<SubThreadUIBean> subThreadUI) {
            this.subThreadUI = subThreadUI;
        }

        public List<UiLevelBean> getUiLevel() {
            return uiLevel;
        }

        public void setUiLevel(List<UiLevelBean> uiLevel) {
            this.uiLevel = uiLevel;
        }

        public List<LeakBean> getLeak() {
            return leak;
        }

        public void setLeak(List<LeakBean> leak) {
            this.leak = leak;
        }

        public List<PageLoadBean> getPageLoad() {
            return pageLoad;
        }

        public void setPageLoad(List<PageLoadBean> pageLoad) {
            this.pageLoad = pageLoad;
        }

        public List<BigFileBean> getBigFile() {
            return bigFile;
        }

        public void setBigFile(List<BigFileBean> bigFile) {
            this.bigFile = bigFile;
        }

        public static class AppStartBean {
            /**
             * costTime : 3200
             * costDetail : 代码耗时字符串
             * loadFunc : [{"className":"ClassA","costTime":"15"},{"className":"ClassB","costTime":"30"}]
             */

            private long costTime;
            private String costDetail;
            private List<LoadFuncBean> loadFunc;

            public long getCostTime() {
                return costTime;
            }

            public void setCostTime(long costTime) {
                this.costTime = costTime;
            }

            public String getCostDetail() {
                return costDetail;
            }

            public void setCostDetail(String costDetail) {
                this.costDetail = costDetail;
            }

            public List<LoadFuncBean> getLoadFunc() {
                return loadFunc;
            }

            public void setLoadFunc(List<LoadFuncBean> loadFunc) {
                this.loadFunc = loadFunc;
            }

            public static class LoadFuncBean {
                /**
                 * className : ClassA
                 * costTime : 15
                 */

                private String className;
                private String costTime;

                public String getClassName() {
                    return className;
                }

                public void setClassName(String className) {
                    this.className = className;
                }

                public String getCostTime() {
                    return costTime;
                }

                public void setCostTime(String costTime) {
                    this.costTime = costTime;
                }
            }
        }

        /**
         * cpu、内存、fps 共享的Bean
         */
        public static class PerformanceBean {
            /**
             * page : HomeViewController
             * values : [{"time":"时间戳","value":"0.5"},{"time":"时间戳","value":"0.8"}]
             */
            @Expose
            private String pageKey;
            private String page;
            private List<ValuesBean> values;

            public String getPageKey() {
                return pageKey;
            }

            public void setPageKey(String pageKey) {
                this.pageKey = pageKey;
            }

            public String getPage() {
                return page;
            }

            public void setPage(String page) {
                this.page = page;
            }

            public List<ValuesBean> getValues() {
                return values;
            }

            public void setValues(List<ValuesBean> values) {
                this.values = values;
            }

            /**
             * cpu、内存、fps 共享的ValueBean
             */
            public static class ValuesBean {
                /**
                 * time : 时间戳
                 * value : 0.5
                 */

                private String time;
                private String value;

                public ValuesBean(String time, String value) {
                    this.time = time;
                    this.value = value;
                }

                public String getTime() {
                    return time;
                }

                public void setTime(String time) {
                    this.time = time;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }
        }


        public static class NetworkBean {
            /**
             * page : HomeViewController
             * values : [{"time":"时间戳","url":"http://www.baidu.com","up":"100","down":"200","code":"200","method":"Get"},{"time":"时间戳","url":"http://www.taobao.com","up":"100","down":"200","code":"200","method":"Post"}]
             */

            private String page;
            private List<NetworkValuesBean> values;

            public String getPage() {
                return page;
            }

            public void setPage(String page) {
                this.page = page;
            }

            public List<NetworkValuesBean> getValues() {
                return values;
            }

            public void setValues(List<NetworkValuesBean> values) {
                this.values = values;
            }

            public static class NetworkValuesBean {
                /**
                 * time : 时间戳
                 * url : http://www.baidu.com
                 * up : 100
                 * down : 200
                 * code : 200
                 * method : Get
                 */

                private String time;
                private String url;
                private String up;
                private String down;
                private String code;
                private String method;

                public String getTime() {
                    return time;
                }

                public void setTime(String time) {
                    this.time = time;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public String getUp() {
                    return up;
                }

                public void setUp(String up) {
                    this.up = up;
                }

                public String getDown() {
                    return down;
                }

                public void setDown(String down) {
                    this.down = down;
                }

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
                }

                public String getMethod() {
                    return method;
                }

                public void setMethod(String method) {
                    this.method = method;
                }
            }
        }

        public static class BlockBean {
            /**
             * page : HomeViewController
             * blockTime : 4.2
             * detail : 卡顿堆栈
             */

            private String page;
            private long blockTime;
            private String detail;

            public String getPage() {
                return page;
            }

            public void setPage(String page) {
                this.page = page;
            }

            public long getBlockTime() {
                return blockTime;
            }

            public void setBlockTime(long blockTime) {
                this.blockTime = blockTime;
            }

            public String getDetail() {
                return detail;
            }

            public void setDetail(String detail) {
                this.detail = detail;
            }
        }

        public static class SubThreadUIBean {
            /**
             * page : HomeViewController
             * detail : 代码堆栈
             */

            private String page;
            private String detail;

            public String getPage() {
                return page;
            }

            public void setPage(String page) {
                this.page = page;
            }

            public String getDetail() {
                return detail;
            }

            public void setDetail(String detail) {
                this.detail = detail;
            }
        }

        public static class UiLevelBean {
            /**
             * page : HomeViewController
             * level : 10
             * detail : 层级引用链
             */

            private String page;
            private String level;
            private String detail;

            public String getPage() {
                return page;
            }

            public void setPage(String page) {
                this.page = page;
            }

            public String getLevel() {
                return level;
            }

            public void setLevel(String level) {
                this.level = level;
            }

            public String getDetail() {
                return detail;
            }

            public void setDetail(String detail) {
                this.detail = detail;
            }
        }

        public static class LeakBean {
            /**
             * page : HomeViewController
             * detail : 内存泄漏详情
             */

            private String page;
            private String detail;

            public String getPage() {
                return page;
            }

            public void setPage(String page) {
                this.page = page;
            }

            public String getDetail() {
                return detail;
            }

            public void setDetail(String detail) {
                this.detail = detail;
            }
        }

        public static class PageLoadBean {
            /**
             * page : HomeViewController
             * time : 120
             */

            private String page;
            private String time;
            private String trace;

            public String getPage() {
                return page;
            }

            public void setPage(String page) {
                this.page = page;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }


            public String getTrace() {
                return trace;
            }

            public void setTrace(String trace) {
                this.trace = trace;
            }
        }

        public static class BigFileBean {
            /**
             * fileName : fileName1
             * fileSize : 30M
             * filePath : /data/json/fileName1
             */

            private String fileName;
            private String fileSize;
            private String filePath;

            public String getFileName() {
                return fileName;
            }

            public void setFileName(String fileName) {
                this.fileName = fileName;
            }

            public String getFileSize() {
                return fileSize;
            }

            public void setFileSize(String fileSize) {
                this.fileSize = fileSize;
            }

            public String getFilePath() {
                return filePath;
            }

            public void setFilePath(String filePath) {
                this.filePath = filePath;
            }
        }
    }
}
