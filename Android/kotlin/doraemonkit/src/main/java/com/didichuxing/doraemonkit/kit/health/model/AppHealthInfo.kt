package com.didichuxing.doraemonkit.kit.health.model

import com.google.gson.annotations.Expose

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020-01-02-16:29
 * 描    述：app 健康体检上传服务器数据
 * 修订历史：
 * ================================================
 */
class AppHealthInfo {
    /**
     * baseInfo : {"caseName":"iOS5.0版本性能测试","testPerson":"易小翔","platfom":"iOS","time":"2019-12-12 : 11:12:30","phoneMode":"iphone6S","systemVersion":"13","appNmae":"Dokit","appVersion":"1.0.0","dokitVersion":"2.0.0"}
     * data : {"appStart":{"costTime":"3200","costDetail":"代码耗时字符串","loadFunc":[{"className":"ClassA","costTime":"15"},{"className":"ClassB","costTime":"30"}]},"cpu":[{"page":"HomeViewController","values":[{"time":"时间戳","value":"0.5"},{"time":"时间戳","value":"0.8"}]},{"page":"MapViewController","values":[{"time":"时间戳","value":"0.5"},{"time":"时间戳","value":"0.8"}]}],"memory":[{"page":"HomeViewController","values":[{"time":"时间戳","value":"80"},{"time":"时间戳","value":"81"}]},{"page":"MapViewController","values":[{"time":"时间戳","value":"90"},{"time":"时间戳","value":"91"}]}],"fps":[{"page":"HomeViewController","values":[{"time":"时间戳","value":"60"},{"time":"时间戳","value":"59"}]},{"page":"MapViewController","values":[{"time":"时间戳","value":"50"},{"time":"时间戳","value":"60"}]}],"network":[{"page":"HomeViewController","values":[{"time":"时间戳","url":"http://www.baidu.com","up":"100","down":"200","code":"200","method":"Get"},{"time":"时间戳","url":"http://www.taobao.com","up":"100","down":"200","code":"200","method":"Post"}]},{"page":"MapViewController","values":[{"time":"时间戳","url":"http://www.baidu.com","up":"100","down":"200","code":"200","method":"Get"},{"time":"时间戳","url":"http://www.taobao.com","up":"100","down":"200","code":"200","method":"Post"}]}],"block":[{"page":"HomeViewController","blockTime":"4.2","detail":"卡顿堆栈"},{"page":"MapViewController","blockTime":"5.2","detail":"卡顿堆栈"}],"subThreadUI":[{"page":"HomeViewController","detail":"代码堆栈"},{"page":"MapViewController","detail":"代码堆栈"}],"uiLevel":[{"page":"HomeViewController","level":"10","detail":"层级引用链"},{"page":"MapViewController","level":"10","detail":"层级引用链"}],"leak":[{"page":"HomeViewController","detail":"内存泄漏详情"},{"page":"MapViewController","detail":"内存泄漏详情"}],"pageLoad":[{"page":"HomeViewController","time":"120"},{"page":"MapViewController","time":"120"}],"bigFile":[{"fileName":"fileName1","fileSize":"30M","filePath":"/data/json/fileName1"}]}
     */
    var baseInfo: BaseInfoBean? = null
    var data: DataBean? = null

    class BaseInfoBean {
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
        var caseName: String? = null
        var testPerson: String? = null
        var platform: String? = null
        var time: String? = null
        var phoneMode: String? = null
        var systemVersion: String? = null
        var appName: String? = null
        var appVersion: String? = null
        var dokitVersion: String? = null
        private var pId: String? = null
        fun getpId(): String? {
            return pId
        }

        fun setpId(pId: String?) {
            this.pId = pId
        }

    }

    class DataBean {
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
        var appStart: AppStartBean? = null
        var cpu: MutableList<PerformanceBean>? = null
        var memory: MutableList<PerformanceBean>? = null
        var fps: MutableList<PerformanceBean>? = null
        var network: MutableList<NetworkBean>? = null
        var block: MutableList<BlockBean>? = null
        var subThreadUI: MutableList<SubThreadUIBean>? = null
        var uiLevel: MutableList<UiLevelBean>? = null
        var leak: MutableList<LeakBean>? = null
        var pageLoad: MutableList<PageLoadBean>? = null
        var bigFile: MutableList<BigFileBean>? = null

        class AppStartBean {
            /**
             * costTime : 3200
             * costDetail : 代码耗时字符串
             * loadFunc : [{"className":"ClassA","costTime":"15"},{"className":"ClassB","costTime":"30"}]
             */
            var costTime: Long = 0
            var costDetail: String? = null
            var loadFunc: List<LoadFuncBean>? = null

            class LoadFuncBean {
                /**
                 * className : ClassA
                 * costTime : 15
                 */
                var className: String? = null
                var costTime: String? = null

            }
        }

        /**
         * cpu、内存、fps 共享的Bean
         */
        class PerformanceBean {
            /**
             * page : HomeViewController
             * values : [{"time":"时间戳","value":"0.5"},{"time":"时间戳","value":"0.8"}]
             */
            @Expose
            var pageKey: String? = null
            var page: String? = null
            var values: MutableList<ValuesBean>? = null

            /**
             * cpu、内存、fps 共享的ValueBean
             */
            class ValuesBean(
                    /**
                     * time : 时间戳
                     * value : 0.5
                     */
                    var time: String, var value: String)
        }

        class NetworkBean {
            /**
             * page : HomeViewController
             * values : [{"time":"时间戳","url":"http://www.baidu.com","up":"100","down":"200","code":"200","method":"Get"},{"time":"时间戳","url":"http://www.taobao.com","up":"100","down":"200","code":"200","method":"Post"}]
             */
            var page: String? = null
            var values: MutableList<NetworkValuesBean>? = null

            class NetworkValuesBean {
                /**
                 * time : 时间戳
                 * url : http://www.baidu.com
                 * up : 100
                 * down : 200
                 * code : 200
                 * method : Get
                 */
                var time: String? = null
                var url: String? = null
                var up: String? = null
                var down: String? = null
                var code: String? = null
                var method: String? = null

            }
        }

        class BlockBean {
            /**
             * page : HomeViewController
             * blockTime : 4.2
             * detail : 卡顿堆栈
             */
            var page: String? = null
            var blockTime: Long = 0
            var detail: String? = null

        }

        class SubThreadUIBean {
            /**
             * page : HomeViewController
             * detail : 代码堆栈
             */
            var page: String? = null
            var detail: String? = null

        }

        class UiLevelBean {
            /**
             * page : HomeViewController
             * level : 10
             * detail : 层级引用链
             */
            var page: String? = null
            var level: String? = null
            var detail: String? = null

        }

        class LeakBean {
            /**
             * page : HomeViewController
             * detail : 内存泄漏详情
             */
            var page: String? = null
            var detail: String? = null

        }

        class PageLoadBean {
            /**
             * page : HomeViewController
             * time : 120
             */
            var page: String? = null
            var time: String? = null
            var trace: String? = null

        }

        class BigFileBean {
            /**
             * fileName : fileName1
             * fileSize : 30M
             * filePath : /data/json/fileName1
             */
            var fileName: String? = null
            var fileSize: String? = null
            var filePath: String? = null

        }
    }
}