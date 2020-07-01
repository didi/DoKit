package com.didichuxing.doraemonkit.kit.health

import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.TimeUtils
import com.didichuxing.doraemonkit.BuildConfig
import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.kit.health.model.AppHealthInfo
import com.didichuxing.doraemonkit.kit.health.model.AppHealthInfo.BaseInfoBean
import com.didichuxing.doraemonkit.kit.health.model.AppHealthInfo.DataBean.*
import com.didichuxing.doraemonkit.kit.health.model.AppHealthInfo.DataBean.AppStartBean.LoadFuncBean
import com.didichuxing.doraemonkit.kit.health.model.AppHealthInfo.DataBean.PerformanceBean.ValuesBean
import java.util.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020-01-02-16:42
 * 描    述：app 健康体检工具类
 * 修订历史：
 * ================================================
 */
class AppHealthInfoUtil {
    private var mAppHealthInfo: AppHealthInfo? = AppHealthInfo()

    /**
     * 静态内部类单例
     */
    private object Holder {
        val INSTANCE = AppHealthInfoUtil()
    }

    /**
     * 设置基本信息
     *
     * @param caseName   用例名称
     * @param testPerson 测试人员名字
     */
    fun setBaseInfo(caseName: String?, testPerson: String?) {
        val baseInfoBean = BaseInfoBean()
        baseInfoBean.testPerson = testPerson
        baseInfoBean.caseName = caseName
        baseInfoBean.appName = AppUtils.getAppName()
        baseInfoBean.appVersion = AppUtils.getAppVersionName()
        baseInfoBean.dokitVersion = BuildConfig.DOKIT_VERSION
        baseInfoBean.platform = "Android"
        baseInfoBean.phoneMode = DeviceUtils.getModel()
        baseInfoBean.time = TimeUtils.getNowString()
        baseInfoBean.systemVersion = DeviceUtils.getSDKVersionName()
        baseInfoBean.setpId("" + DokitConstant.PRODUCT_ID)
        mAppHealthInfo!!.baseInfo = baseInfoBean
    }

    /**
     * 设置app启动耗时的具体信息
     *
     * @param costTime
     * @param costDetail
     */
    fun setAppStartInfo(costTime: Long, costDetail: String?, loadFunc: MutableList<LoadFuncBean>?) {
        val appStartBean = AppStartBean()
        appStartBean.costTime = costTime
        appStartBean.costDetail = costDetail
        appStartBean.loadFunc = loadFunc
        data!!.appStart = appStartBean
    }

    /**
     * 添加cpu信息
     *
     * @param cpuBean
     */
    fun addCPUInfo(cpuBean: PerformanceBean?) {
        var cpus: MutableList<PerformanceBean>? = data!!.cpu
        if (cpus == null) {
            cpus = ArrayList()
            data!!.cpu = cpus
        }
        //不过滤最大最小值
        //cpuBean.setValues(sortValue(cpuBean.getValues()));
        cpuBean?.let {
            cpus.add(it)
        }

    }

    /**
     * 添加memory信息
     *
     * @param memoryBean
     */
    fun addMemoryInfo(memoryBean: PerformanceBean?) {
        var memories: MutableList<PerformanceBean>? = data!!.memory
        if (memories == null) {
            memories = ArrayList()
            data!!.memory = memories
        }
        //不过滤最大最小值
        //memoryBean.setValues(sortValue(memoryBean.getValues()));
        memoryBean?.let {
            memories.add(it)
        }

    }

    /**
     * 添加fps信息
     *
     * @param fpsBean
     */
    fun addFPSInfo(fpsBean: PerformanceBean?) {
        var fpsBeans: MutableList<PerformanceBean>? = data!!.fps
        if (fpsBeans == null) {
            fpsBeans = ArrayList()
            data!!.fps = fpsBeans
        }
        //不过滤最大最小值
        //fpsBean.setValues(sortValue(fpsBean.getValues()));
        fpsBean?.let {
            fpsBeans.add(it)
        }

    }

    /**
     * 添加网络信息
     *
     * @param networkBean
     */
    fun addNetWorkInfo(networkBean: NetworkBean?) {
        var networks: MutableList<NetworkBean>? = data!!.network
        if (networks == null) {
            networks = ArrayList()
            data!!.network = networks
        }
        networkBean?.let {
            networks.add(it)
        }

    }

    /**
     * 获取指定的NetworkBean
     *
     * @param activityName
     */
    fun getNetWorkInfo(activityName: String): NetworkBean? {
        val networks = data!!.network
        if (networks == null || networks.size == 0) {
            return null
        }
        var networkBean: NetworkBean? = null
        for (traverseNetworkBean in networks) {
            if (traverseNetworkBean.page == activityName) {
                networkBean = traverseNetworkBean
                break
            }
        }
        return networkBean
    }

    /**
     * 添加卡顿信息
     *
     * @param blockBean
     */
    fun addBlockInfo(blockBean: BlockBean?) {
        var blocks: MutableList<BlockBean>? = data!!.block
        if (blocks == null) {
            blocks = ArrayList()
            data!!.block = blocks
        }
        blockBean?.let {
            blocks.add(it)
        }

    }

    /**
     * 添加页面层级信息
     *
     * @param uiLevelBean
     */
    fun addUiLevelInfo(uiLevelBean: UiLevelBean?) {
        var uiLevels: MutableList<UiLevelBean>? = data!!.uiLevel
        if (uiLevels == null) {
            uiLevels = ArrayList()
            data!!.uiLevel = uiLevels
        }
        uiLevelBean?.let {
            uiLevels.add(it)
        }

    }

    /**
     * 添加内存泄漏信息
     *
     * @param leakBean
     */
    fun addLeakInfo(leakBean: LeakBean?) {
        var leaks: MutableList<LeakBean>? = data!!.leak
        if (leaks == null) {
            leaks = ArrayList()
            data!!.leak = leaks
        }
        leakBean?.let {
            leaks.add(it)
        }

    }

    /**
     * 添加页面加载耗时信息
     *
     * @param pageLoadBean
     */
    fun addPageLoadInfo(pageLoadBean: PageLoadBean?) {
        var pageloads: MutableList<PageLoadBean>? = data!!.pageLoad
        if (pageloads == null) {
            pageloads = ArrayList()
            data!!.pageLoad = pageloads
        }
        pageLoadBean?.let {
            pageloads.add(it)
        }

    }

    /**
     * 添加页面加载耗时信息
     *
     * @param bigFileBean
     */
    fun addBigFilrInfo(bigFileBean: BigFileBean?) {
        var bigFiles: MutableList<BigFileBean>? = data!!.bigFile
        if (bigFiles == null) {
            bigFiles = ArrayList()
            data!!.bigFile = bigFiles
        }
        bigFileBean?.let {
            bigFiles.add(it)
        }

    }

    /**
     * 获取data对象
     *
     * @return
     */
    private val data: AppHealthInfo.DataBean?
        private get() {
            if (mAppHealthInfo!!.data == null) {
                val dataBean = AppHealthInfo.DataBean()
                dataBean.cpu = ArrayList()
                dataBean.memory = ArrayList()
                dataBean.fps = ArrayList()
                dataBean.network = ArrayList()
                dataBean.block = ArrayList()
                dataBean.uiLevel = ArrayList()
                dataBean.leak = ArrayList()
                dataBean.pageLoad = ArrayList()
                dataBean.bigFile = ArrayList()
                dataBean.subThreadUI = ArrayList()
                mAppHealthInfo!!.data = dataBean
            }
            return mAppHealthInfo!!.data
        }

    /**
     * 开启健康体检监控
     */
    fun start() {}

    /**
     * 结束健康体检监控
     */
    fun stop() {}

    /**
     * list 去掉最大值和最小值 并重新 排序
     */
    private fun sortValue(valuesBeans: List<ValuesBean>): List<ValuesBean> {
        val newValuesBeans: MutableList<ValuesBean> = ArrayList(valuesBeans)
        Collections.sort(newValuesBeans) { pre, next ->
            val preValue = pre.value.toFloat()
            val nextValue = next.value.toFloat()
            if (preValue < nextValue) {
                -1
            } else {
                1
            }
        }
        newValuesBeans.removeAt(0)
        newValuesBeans.removeAt(newValuesBeans.size - 1)
        Collections.sort(newValuesBeans) { pre, next ->
            val preValue = pre.time.toLong()
            val nextValue = next.time.toLong()
            if (preValue < nextValue) {
                -1
            } else {
                1
            }
        }
        return newValuesBeans
    }

    /**
     * 内存释放
     */
    fun release() {
        if (mAppHealthInfo != null) {
            mAppHealthInfo = null
        }
    }

    companion object {
        private const val TAG = "AppHealthInfoUtil"
        val instance: AppHealthInfoUtil
            get() = Holder.INSTANCE
    }
}