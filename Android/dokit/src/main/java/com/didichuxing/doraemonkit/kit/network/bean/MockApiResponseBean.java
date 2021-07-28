package com.didichuxing.doraemonkit.kit.network.bean;

import com.didichuxing.doraemonkit.kit.core.DoKitManager;
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager;

import java.util.List;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-12-20:19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MockApiResponseBean {

    /**
     * code : 200
     * data : {"datalist":[{"_id":"5dc925a3e6173367388831e0","name":"模拟","desc":"模拟dokit","groupId":"","projectId":"5c8e04056144a626ff2542e344","categoryId":"5dc920a56de696654ec9b56b","path":"/dokit1","method":"GET","formatType":"json","params":{"pathParams":[{"name":"api","value":"api.dj.login"}]},"owner":{},"interOwner":{},"curStatus":{},"createDate":"2019-11-11 17:10:59","categoryName":"用户","query":{"api":"api.dj.login"},"sceneList":[{"_id":"5dca79a0cf77257a255a0fda","name":"场景2","desc":"","projectId":"5c8e04056144a626ff2542e344","interfaceId":"5dc925a3e6173367388831e0","createDate":1573550496005},{"_id":"5dca795bb073557a27024a24","name":"测试场景","desc":"","projectId":"5c8e04056144a626ff2542e344","interfaceId":"5dc925a3e6173367388831e0","createDate":1573550427707}]},{"_id":"5dc92585b1b0846733194865","name":"测试接口1","desc":"","groupId":"","projectId":"5c8e04056144a626ff2542e344","categoryId":"5dc920a56de696654ec9b56b","path":"/inter1","method":"POST","formatType":"json","params":{"pathParams":[]},"owner":{"_id":"5c87b096513b3e266f4027ca","name":"卢群","displayName":"卢群(普惠产品技术部)","department":"普惠产品技术部","employeeNumber":"D00252","accountName":"luqun","authority":{"admin":true}},"interOwner":"","curStatus":{"status":"new","date":"2019-11-11 17:10:29"},"createDate":"2019-11-11 17:10:29","categoryName":"用户","query":{},"sceneList":[]}]}
     */

    private int code;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    /**
     * 该对象需要做特殊处理
     */
    public static class DataBean {
        private List<DatalistBean> datalist;

        public List<DatalistBean> getDatalist() {
            return datalist;
        }

        public void setDatalist(List<DatalistBean> datalist) {
            this.datalist = datalist;
        }

        public static class DatalistBean {
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

            private String _id;
            private String name;
            private String desc;
            private String groupId;
            private String projectId;
            private String categoryId;
            private String path;
            private String method;
            private String formatType;
            //private ParamsBean params;
            private OwnerBean owner;
            //private Object interOwner;
            private CurStatusBean curStatus;
            private String createDate;
            private String categoryName;
            //private QueryBean query;
            private List<SceneListBean> sceneList;

            public String get_id() {
                return _id;
            }

            public void set_id(String _id) {
                this._id = _id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public String getGroupId() {
                return groupId;
            }

            public void setGroupId(String groupId) {
                this.groupId = groupId;
            }

            public String getProjectId() {
                return projectId;
            }

            public void setProjectId(String projectId) {
                this.projectId = projectId;
            }

            public String getCategoryId() {
                return categoryId;
            }

            public void setCategoryId(String categoryId) {
                this.categoryId = categoryId;
            }

            /**
             * 拦截一层kop
             *
             * @return path
             */
            public String getPath() {
                if (DoKitManager.isRpcSDK()) {
                    path = DoKitManager.dealDidiPlatformPath(path, DokitDbManager.FROM_SDK_DIDI);
                }
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public String getMethod() {
                return method;
            }

            public void setMethod(String method) {
                this.method = method;
            }

            public String getFormatType() {
                return formatType;
            }

            public void setFormatType(String formatType) {
                this.formatType = formatType;
            }

            public OwnerBean getOwner() {
                return owner;
            }

            public void setOwner(OwnerBean owner) {
                this.owner = owner;
            }

//            public Object getInterOwner() {
//                return interOwner;
//            }
//
//            public void setInterOwner(Object interOwner) {
//                this.interOwner = interOwner;
//            }

            public CurStatusBean getCurStatus() {
                return curStatus;
            }

            public void setCurStatus(CurStatusBean curStatus) {
                this.curStatus = curStatus;
            }

            public String getCreateDate() {
                return createDate;
            }

            public void setCreateDate(String createDate) {
                this.createDate = createDate;
            }

            public String getCategoryName() {
                return categoryName;
            }

            public void setCategoryName(String categoryName) {
                this.categoryName = categoryName;
            }

//            public QueryBean getQuery() {
//                return query;
//            }
//
//            public void setQuery(QueryBean query) {
//                this.query = query;
//            }

            public List<SceneListBean> getSceneList() {
                return sceneList;
            }

            public void setSceneList(List<SceneListBean> sceneList) {
                this.sceneList = sceneList;
            }


            public static class OwnerBean {
                String _id;
                String name;
                String displayName;
                String department;
                String employeeNumber;
                String accountName;


                public String getName() {
                    return name;
                }

                public String getDisplayName() {
                    return displayName;
                }

                public String getDepartment() {
                    return department;
                }

                public String getEmployeeNumber() {
                    return employeeNumber;
                }

                public String getAccountName() {
                    return accountName;
                }
            }

            public static class InterOwnerBean {
            }

            public static class CurStatusBean {

                String status;
                OperatorBean operator;

                public String getStatus() {
                    return status;
                }

                public OperatorBean getOperator() {
                    return operator;
                }

                public static class OperatorBean {
                    String name;
                    String displayName;
                    String department;
                    String employeeNumber;
                    String accountName;

                    public String getName() {
                        return name;
                    }

                    public String getDisplayName() {
                        return displayName;
                    }

                    public String getDepartment() {
                        return department;
                    }

                    public String getEmployeeNumber() {
                        return employeeNumber;
                    }

                    public String getAccountName() {
                        return accountName;
                    }
                }
            }

            public static class QueryBean {
                /**
                 * api : api.dj.login
                 */

                private String api;

                public String getApi() {
                    return api;
                }

                public void setApi(String api) {
                    this.api = api;
                }
            }

            public static class SceneListBean {
                /**
                 * _id : 5dca79a0cf77257a255a0fda
                 * name : 场景2
                 * desc :
                 * projectId : 5c8e04056144a626ff2542e344
                 * interfaceId : 5dc925a3e6173367388831e0
                 * createDate : 1573550496005
                 */

                private String _id;
                private String name;
                private String desc;
                private String projectId;
                private String interfaceId;
                private long createDate;

                public String get_id() {
                    return _id;
                }

                public void set_id(String _id) {
                    this._id = _id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getDesc() {
                    return desc;
                }

                public void setDesc(String desc) {
                    this.desc = desc;
                }

                public String getProjectId() {
                    return projectId;
                }

                public void setProjectId(String projectId) {
                    this.projectId = projectId;
                }

                public String getInterfaceId() {
                    return interfaceId;
                }

                public void setInterfaceId(String interfaceId) {
                    this.interfaceId = interfaceId;
                }

                public long getCreateDate() {
                    return createDate;
                }

                public void setCreateDate(long createDate) {
                    this.createDate = createDate;
                }
            }
        }
    }
}
