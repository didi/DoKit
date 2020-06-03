package com.didichuxing.doraemonkit.constant

import com.didichuxing.doraemonkit.BuildConfig

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/5/29-14:26
 * 描    述：
 * 修订历史：
 * ================================================
 */
object NetWorkConstant {
    /**
     * ########以下为数据mock 的相关网络接口#########
     */
    const val MOCK_SCHEME_HTTP = "http://"
    const val MOCK_SCHEME_HTTPS = "https://"

    //    private static final String MOCK_HOST_DEBUG = "xyrd.intra.xiaojukeji.com";
    private const val MOCK_HOST_DEBUG = "mock.dokit.cn"
    private const val MOCK_HOST_RELEASE = "mock.dokit.cn"
    private const val MOCK_DEBUG_DOMAIN = MOCK_SCHEME_HTTPS + MOCK_HOST_DEBUG
    private const val MOCK_RELEASE_DOMAIN = MOCK_SCHEME_HTTPS + MOCK_HOST_RELEASE
    val MOCK_DOMAIN = if (BuildConfig.DEBUG) MOCK_DEBUG_DOMAIN else MOCK_RELEASE_DOMAIN
    val MOCK_HOST = if (BuildConfig.DEBUG) MOCK_HOST_DEBUG else MOCK_HOST_RELEASE
    /**
     * ########以上为数据mock 的相关网络接口#########
     */


    /**
     * ########以上为数据mock 的相关网络接口#########
     */
    /**
     * ########app健康体检相关接口 的相关网络接口#########
     * 线上地址：https://www.dokit.cn/healthCheck/addCheckData
     * 测试环境地址:http://dokit-test.intra.xiaojukeji.com/healthCheck/addCheckData
     */
    const val APP_HEALTH_URL = "https://www.dokit.cn/healthCheck/addCheckData"

    /**
     * ########业务埋点的网络接口#########
     */
    const val APP_DATA_PICK_URL = "https://www.dokit.cn/pointData/addPointData"
    //public static final String APP_DATA_PICK_URL = "http://dokit-test.intra.xiaojukeji.com/pointData/addPointData";
    //public static final String APP_DATA_PICK_URL = "http://dokit-test.intra.xiaojukeji.com/pointData/addPointData";
    /**
     * 慢函数操作文档
     */
    const val APP_DOCUMENT_URL = "http://xingyun.xiaojukeji.com/docs/dokit/#/TimeProfiler"

    /**
     * app 启动数据埋点
     */
    const val APP_START_DATA_PICK_URL = "https://doraemon.xiaojukeji.com/uploadAppData"

}