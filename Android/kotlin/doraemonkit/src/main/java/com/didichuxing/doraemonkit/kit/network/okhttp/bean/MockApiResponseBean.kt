package com.didichuxing.doraemonkit.kit.network.okhttp.bean

import com.didichuxing.doraemonkit.constant.DokitConstant.dealDidiPlatformPath
import com.didichuxing.doraemonkit.constant.DokitConstant.isRpcSDK
import com.didichuxing.doraemonkit.kit.network.okhttp.room_db.DokitDbManager

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-12-20:19
 * 描    述：
 * 修订历史：
 * ================================================
 */
class MockApiResponseBean {
    /**
     * code : 200
     * data : {"datalist":[{"_id":"5dc925a3e6173367388831e0","name":"模拟","desc":"模拟dokit","groupId":"","projectId":"5c8e04056144a626ff2542e344","categoryId":"5dc920a56de696654ec9b56b","path":"/dokit1","method":"GET","formatType":"json","params":{"pathParams":[{"name":"api","value":"api.dj.login"}]},"owner":{},"interOwner":{},"curStatus":{},"createDate":"2019-11-11 17:10:59","categoryName":"用户","query":{"api":"api.dj.login"},"sceneList":[{"_id":"5dca79a0cf77257a255a0fda","name":"场景2","desc":"","projectId":"5c8e04056144a626ff2542e344","interfaceId":"5dc925a3e6173367388831e0","createDate":1573550496005},{"_id":"5dca795bb073557a27024a24","name":"测试场景","desc":"","projectId":"5c8e04056144a626ff2542e344","interfaceId":"5dc925a3e6173367388831e0","createDate":1573550427707}]},{"_id":"5dc92585b1b0846733194865","name":"测试接口1","desc":"","groupId":"","projectId":"5c8e04056144a626ff2542e344","categoryId":"5dc920a56de696654ec9b56b","path":"/inter1","method":"POST","formatType":"json","params":{"pathParams":[]},"owner":{"_id":"5c87b096513b3e266f4027ca","name":"卢群","displayName":"卢群(普惠产品技术部)","department":"普惠产品技术部","employeeNumber":"D00252","accountName":"luqun","authority":{"admin":true}},"interOwner":"","curStatus":{"status":"new","date":"2019-11-11 17:10:29"},"createDate":"2019-11-11 17:10:29","categoryName":"用户","query":{},"sceneList":[]}]}
     */
    var code = 0
    var data: DataBean? = null

    /**
     * 该对象需要做特殊处理
     */
    class DataBean {
        var datalist: MutableList<DatalistBean>? = null

        class DatalistBean {
            /**
             * _id : 5dc925a3e6173367388831e0
             * name : 模拟
             * desc : 模拟dokit
             * groupId :
             * projectId : 5c8e04056144a626ff2542e344
             * categoryId : 5dc920a56de696654ec9b56b
             * path : /dokit1
             * method : GET
             * formatType : json
             * params : {"pathParams":[{"name":"api","value":"api.dj.login"}]}
             * owner : {}
             * interOwner : {}
             * curStatus : {}
             * createDate : 2019-11-11 17:10:59
             * categoryName : 用户
             * query : {"api":"api.dj.login"}
             * sceneList : [{"_id":"5dca79a0cf77257a255a0fda","name":"场景2","desc":"","projectId":"5c8e04056144a626ff2542e344","interfaceId":"5dc925a3e6173367388831e0","createDate":1573550496005},{"_id":"5dca795bb073557a27024a24","name":"测试场景","desc":"","projectId":"5c8e04056144a626ff2542e344","interfaceId":"5dc925a3e6173367388831e0","createDate":1573550427707}]
             */
            var _id: String? = ""
            var name: String? = null
            var desc: String? = null
            var groupId: String? = null
            var projectId: String? = null
            var categoryId: String? = null

            /**
             * 拦截一层kop
             *
             * @return path
             */
            var path: String? = null
                get() {
                    if (isRpcSDK) {
                        field = dealDidiPlatformPath(field!!, DokitDbManager.FROM_SDK_DIDI)
                    }
                    return field
                }
            var method: String? = null
            var formatType: String? = null

            //private ParamsBean params;
            var owner: OwnerBean? = null

            //            public Object getInterOwner() {
            //                return interOwner;
            //            }
            //
            //            public void setInterOwner(Object interOwner) {
            //                this.interOwner = interOwner;
            //            }
            //private Object interOwner;
            var curStatus: CurStatusBean? = null
            var createDate: String? = null
            var categoryName: String? = null

            //            public QueryBean getQuery() {
            //                return query;
            //            }
            //
            //            public void setQuery(QueryBean query) {
            //                this.query = query;
            //            }
            //private QueryBean query;
            var sceneList: MutableList<SceneListBean>? = null


            class OwnerBean {
                var _id: String? = null
                var name: String? = null
                var displayName: String? = null
                var department: String? = null
                var employeeNumber: String? = null
                var accountName: String? = null

            }

            class InterOwnerBean
            class CurStatusBean {
                var status: String? = null
                var operator: OperatorBean? = null

                class OperatorBean {
                    var name: String? = null
                    var displayName: String? = null
                    var department: String? = null
                    var employeeNumber: String? = null
                    var accountName: String? = null

                }
            }

            class QueryBean {
                /**
                 * api : api.dj.login
                 */
                var api: String? = null

            }

            class SceneListBean {
                /**
                 * _id : 5dca79a0cf77257a255a0fda
                 * name : 场景2
                 * desc :
                 * projectId : 5c8e04056144a626ff2542e344
                 * interfaceId : 5dc925a3e6173367388831e0
                 * createDate : 1573550496005
                 */
                public var _id: String? = null
                var name: String? = null
                var desc: String? = null
                var projectId: String? = null
                var interfaceId: String? = null
                var createDate: Long = 0



            }
        }
    }
}