package com.didichuxing.doraemonkit.kit.network.room_db;

import android.text.TextUtils;

import com.didichuxing.doraemonkit.util.ThreadUtils;
import com.didichuxing.doraemonkit.kit.core.DoKitManager;
import com.didichuxing.doraemonkit.kit.core.DoKitViewManager;
import com.didichuxing.doraemonkit.util.LogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-15-16:12
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DokitDbManager<T extends AbsMockApiBean> {
    public final static String IS_NOT_NORMAL_QUERY_PARAMS = "is not normal query parmas";
    public final static String IS_NOT_NORMAL_BODY_PARAMS = "is not normal body parmas";

    public final static String MEDIA_TYPE_FORM = "application/x-www-form-urlencoded";
    public final static String MEDIA_TYPE_JSON = "application/json";
    public final static String MEDIA_TYPE_PLAIN = "text/plain";

    private static final String TAG = "DokitDbManager";

    /**
     * key 为path 可能存在path是一样的 所以value为List
     */
    private Map<String, List<T>> mGlobalInterceptApiMaps = new HashMap<>();
    /**
     * key 为path 可能存在path是一样的 所以value为List
     */
    private Map<String, List<T>> mGlobalTemplateApiMaps = new HashMap<>();


    private MockTemplateApiBean mGlobalTemplateApiBean;

    public Map<String, List<T>> getGlobalInterceptApiMaps() {
        return mGlobalInterceptApiMaps;
    }


    public Map<String, List<T>> getGlobalTemplateApiMaps() {
        return mGlobalTemplateApiMaps;
    }


    public MockTemplateApiBean getGlobalTemplateApiBean() {
        return mGlobalTemplateApiBean;
    }

    public void setGlobalTemplateApiBean(MockTemplateApiBean mGlobalTemplateApiBean) {
        this.mGlobalTemplateApiBean = mGlobalTemplateApiBean;
    }

    /**
     * 静态内部类单例
     */
    private static class Holder {
        private static DokitDbManager INSTANCE = new DokitDbManager();
    }

    public static DokitDbManager getInstance() {
        return DokitDbManager.Holder.INSTANCE;
    }

    /**
     * 获取所有的mock intercept apis
     */
    public void getAllInterceptApis() {
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<List<T>>() {
            @Override
            public List<T> doInBackground() throws Throwable {
                DokitDatabase db = DoKitViewManager.getINSTANCE().getDb();
                if (db.mockApiDao() != null) {
                    return (List<T>) db.mockApiDao().getAllInterceptApi();
                } else {
                    throw new NullPointerException("mDb == null || mDb.mockApiDao()");
                }
            }

            @Override
            public void onSuccess(List<T> result) {
                list2mapByIntercept(result);
            }

            @Override
            public void onFail(Throwable t) {
                super.onFail(t);
                LogHelper.e(TAG, "error====>" + t.getMessage());
            }
        });
    }


    /**
     * 获取所有的mock template apis
     */
    public void getAllTemplateApis() {
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<List<T>>() {
            @Override
            public List<T> doInBackground() throws Throwable {
                DokitDatabase db = DoKitViewManager.getINSTANCE().getDb();
                if (db.mockApiDao() != null) {
                    return (List<T>) db.mockApiDao().getAllTemplateApi();
                } else {
                    throw new NullPointerException("mDb == null || mDb.mockApiDao()");
                }

            }

            @Override
            public void onSuccess(List<T> result) {
                list2mapByTemplate(result);
            }

            @Override
            public void onFail(Throwable t) {
                super.onFail(t);
                LogHelper.e(TAG, "error====>" + t.getMessage());
            }
        });
    }


    /**
     * 数据库中获取指定的 template api
     */
    public T getTemplateApiByIdInDb(String id) {
        return (T) DoKitViewManager.getINSTANCE().getDb().mockApiDao().findTemplateApiById(id);
    }


    /**
     * 数据库中获取指定的mock intercept api
     */
    public T getInterceptApiByIdInDb(String id) {
        return (T) DoKitViewManager.getINSTANCE().getDb().mockApiDao().findInterceptApiById(id);
    }

    /**
     * 内存中中获取指定的mock intercept api
     */
    public T getInterceptApiByIdInMap(String path, String id, int fromSDK) {

        if (mGlobalInterceptApiMaps == null) {
            return null;
        }
        //先进行全匹配
        List<T> mGlobalInterceptApis = mGlobalInterceptApiMaps.get(path);
        if (mGlobalInterceptApis == null) {
            path = DoKitManager.dealDidiPlatformPath(path, fromSDK);
            mGlobalInterceptApis = mGlobalInterceptApiMaps.get(path);
        }

        //再进行滴滴内部匹配
        if (mGlobalInterceptApis == null) {
            return null;
        }

        T selectedMockApi = null;
        for (T mockApi : mGlobalInterceptApis) {
            if (mockApi.getId().equals(id)) {
                selectedMockApi = mockApi;
                break;
            }
        }

        return selectedMockApi;
    }

    /**
     * 内存中获取指定的 template api
     */
    public T getTemplateApiByIdInMap(String path, String id, int fromSDK) {
        if (mGlobalTemplateApiMaps == null) {
            return null;
        }
        List<T> mGlobalTemplateApis = mGlobalTemplateApiMaps.get(path);
        //先进行全匹配
        if (mGlobalTemplateApis == null) {
            path = DoKitManager.dealDidiPlatformPath(path, fromSDK);
            mGlobalTemplateApis = mGlobalTemplateApiMaps.get(path);
        }
        //再进行滴滴内部匹配
        if (mGlobalTemplateApis == null) {
            return null;
        }

        T selectedMockApi = null;
        for (T mockApi : mGlobalTemplateApis) {
            if (mockApi.getId().equals(id)) {
                selectedMockApi = mockApi;
                break;
            }
        }

        return selectedMockApi;
    }

    /**
     * 插入所有的mock intercept  Api 数据
     *
     * @param mockApis
     */
    public void insertAllInterceptApi(final List<MockInterceptApiBean> mockApis) {
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                DoKitViewManager.getINSTANCE().getDb().mockApiDao().insertAllInterceptApi(mockApis);
                //更新本地数据
                getAllInterceptApis();
                return null;
            }

            @Override
            public void onSuccess(Object result) {

            }
        });
    }


    /**
     * 插入所有的mock template  Api 数据
     *
     * @param mockApis
     */
    public void insertAllTemplateApi(final List<MockTemplateApiBean> mockApis) {
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Void>() {
            @Override
            public Void doInBackground() throws Throwable {
                DoKitViewManager.getINSTANCE().getDb().mockApiDao().insertAllTemplateApi(mockApis);
                return null;
            }

            @Override
            public void onSuccess(Void result) {
                //更新本地数据
                getAllTemplateApis();
            }
        });
    }


    /**
     * 更新某个intercept Api
     *
     * @param mockApi
     */
    public void updateInterceptApi(final MockInterceptApiBean mockApi) {

        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Void>() {
            @Override
            public Void doInBackground() throws Throwable {
                DoKitViewManager.getINSTANCE().getDb().mockApiDao().updateInterceptApi(mockApi);

                return null;
            }

            @Override
            public void onSuccess(Void result) {
                //更新本地数据
                getAllInterceptApis();
            }
        });

    }


    /**
     * 更新某个template Api
     *
     * @param mockApi
     */
    public void updateTemplateApi(final MockTemplateApiBean mockApi) {

        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                DoKitViewManager.getINSTANCE().getDb().mockApiDao().updateTemplateApi(mockApi);
                //更新本地数据
                getAllTemplateApis();
                return null;
            }

            @Override
            public void onSuccess(Object result) {

            }
        });

    }

    /**
     * 返回选中的场景id
     *
     * @param path
     * @param id
     * @return
     */
    public String getMockInterceptSelectedSceneIdByPathAndId(String path, String id) {
        if (mGlobalInterceptApiMaps.get(path) == null) {
            return "";
        }

        String selectedSceneId = "";
        for (T mockApi : mGlobalInterceptApiMaps.get(path)) {
            if (mockApi.getId().equals(id)) {
                selectedSceneId = mockApi.getSelectedSceneId();
                break;
            }
        }

        return selectedSceneId;
    }


    public static final String CONTENT_TYPE = "application/json";


    /**
     * 拦截
     */
    public static final int MOCK_API_INTERCEPT = 1;
    /**
     * 模板
     */
    public static final int MOCK_API_TEMPLATE = 2;

    /**
     * 来自滴滴内部SDK
     */
    public static int FROM_SDK_DIDI = 100;
    /**
     * 来自外部SDK
     */
    public static int FROM_SDK_OTHER = 101;

    /**
     * 返回命中的id
     *
     * @param path
     * @param jsonQuery
     * @param operateType
     * @return
     */
    public String isMockMatched(String path, String jsonQuery, String jsonRequestBody, int operateType, int fromSDK) {
        //如果是非字符串类型的请求体 直接不匹配
        if (!TextUtils.isEmpty(jsonQuery) && jsonQuery.equals(IS_NOT_NORMAL_QUERY_PARAMS)) {
            return "";
        }

        if (!TextUtils.isEmpty(jsonRequestBody) && jsonRequestBody.equals(IS_NOT_NORMAL_BODY_PARAMS)) {
            return "";
        }

        T mockApi = mockMatched(path, jsonQuery, jsonRequestBody, operateType, fromSDK);
        if (mockApi == null) {
            return "";
        }
        return mockApi.getId();
    }


    /**
     * 通过path和query查询指定的对象
     *
     * @param path
     * @param jsonQuery
     * @param operateType 1:代表拦截 2：代表模板
     * @return
     */
    private T mockMatched(String path, String jsonQuery, String jsonRequestBody, int operateType, int fromSDK) {
        List<T> mockApis = null;
        if (operateType == DokitDbManager.MOCK_API_INTERCEPT) {
            //先进行一次全匹配
            mockApis = mGlobalInterceptApiMaps.get(path);
            //滴滴内部sdk匹配
            if (mockApis == null) {
                path = DoKitManager.dealDidiPlatformPath(path, fromSDK);
                mockApis = mGlobalInterceptApiMaps.get(path);
            }
        } else if (operateType == DokitDbManager.MOCK_API_TEMPLATE) {
            //先进行一次全匹配
            mockApis = mGlobalTemplateApiMaps.get(path);
            //滴滴内部sdk匹配
            if (mockApis == null) {
                path = DoKitManager.dealDidiPlatformPath(path, fromSDK);
                mockApis = mGlobalTemplateApiMaps.get(path);
            }
        }
        if (mockApis == null) {
            return null;
        }
        T matchedMockApi = null;
        for (T mockApi : mockApis) {
            if (mockApi.isOpen() && queriesMatched(jsonQuery, mockApi) && bodyMatched(jsonRequestBody, mockApi)) {
                matchedMockApi = mockApi;
                break;
            }

        }

        return matchedMockApi;
    }

    /**
     * queries 是否命中
     *
     * @param jsonQuery
     * @param mockApi
     */
    private boolean queriesMatched(String jsonQuery, T mockApi) {
        //{}代表没有配置query
        String mockQuery = mockApi.getQuery();
        boolean mockQueryIsEmpty = TextUtils.isEmpty(mockQuery) || "{}".equals(mockQuery);
        //平台没有配置query参数且本地也没有query参数
        if (mockQueryIsEmpty && TextUtils.isEmpty(jsonQuery)) {
            return true;
        }

        //本地有参数 平台没有配置参数
        if (!TextUtils.isEmpty(jsonQuery) && mockQueryIsEmpty) {
            return true;
        }

        //本地没有参数 但是平台有参数
        if (TextUtils.isEmpty(jsonQuery) && !mockQueryIsEmpty) {
            return false;
        }

        //匹配query
        if (!TextUtils.isEmpty(jsonQuery) && !mockQueryIsEmpty) {
            try {
                JSONObject jsonQueryLocal = new JSONObject(jsonQuery);
                JSONObject jsonQueryMock = new JSONObject(mockQuery);
                List<String> keys = new ArrayList<>();
                //通过平台端的来主动匹配
                Iterator<String> iterator = jsonQueryMock.keys();
                while (iterator.hasNext()) {
                    keys.add(iterator.next());
                }
                int count = 0;

                for (int index = 0; index < keys.size(); index++) {
                    String key = keys.get(index);
                    if (jsonQueryLocal.has(key) && jsonQueryMock.getString(key).equals(jsonQueryLocal.get(key))) {
                        count++;
                    }
                }

                if (count == keys.size()) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    /**
     * body 是否命中
     *
     * @param jsonRequestBody
     * @param mockApi
     */
    private boolean bodyMatched(String jsonRequestBody, T mockApi) {
        //{}代表没有配置query
        String mockBody = mockApi.getBody();
        boolean mockQueryIsEmpty = TextUtils.isEmpty(mockBody) || "{}".equals(mockBody);
        //平台没有配置query参数且本地也没有query参数
        if (mockQueryIsEmpty && TextUtils.isEmpty(jsonRequestBody)) {
            return true;
        }

        //本地有参数 平台没有配置参数
        if (!TextUtils.isEmpty(jsonRequestBody) && mockQueryIsEmpty) {
            return true;
        }

        //本地没有参数 但是平台有参数
        if (TextUtils.isEmpty(jsonRequestBody) && !mockQueryIsEmpty) {
            return false;
        }

        //匹配body
        if (!TextUtils.isEmpty(jsonRequestBody) && !mockQueryIsEmpty) {
            try {
                JSONObject jsonBodyLocal = new JSONObject(jsonRequestBody);
                JSONObject jsonBodyMock = new JSONObject(mockBody);

                List<String> keys = new ArrayList<>();
                Iterator<String> iterator = jsonBodyMock.keys();
                while (iterator.hasNext()) {
                    keys.add(iterator.next());
                }
                int count = 0;
                for (int index = 0; index < keys.size(); index++) {
                    String key = keys.get(index);
                    if (jsonBodyLocal.has(key) && jsonBodyMock.getString(key).equals(jsonBodyLocal.get(key))) {
                        count++;
                    }
                }
                if (count == keys.size()) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }


    private void list2mapByIntercept(List<T> interceptApiBeans) {
        mGlobalInterceptApiMaps.clear();
        for (T mockApi : interceptApiBeans) {
            if (mGlobalInterceptApiMaps.get(mockApi.getPath()) == null) {
                List<T> mockInterceptApiBeans = new ArrayList<>();
                mockInterceptApiBeans.add(mockApi);
                mGlobalInterceptApiMaps.put(mockApi.getPath(), mockInterceptApiBeans);
            } else {
                mGlobalInterceptApiMaps.get(mockApi.getPath()).add(mockApi);
            }

        }
    }


    private void list2mapByTemplate(List<T> templateApiBeans) {
        mGlobalTemplateApiMaps.clear();
        for (T mockApi : templateApiBeans) {
            if (mGlobalTemplateApiMaps.get(mockApi.getPath()) == null) {
                List<T> mockTemplateApiBeans = new ArrayList<>();
                mockTemplateApiBeans.add(mockApi);
                mGlobalTemplateApiMaps.put(mockApi.getPath(), mockTemplateApiBeans);
            } else {
                mGlobalTemplateApiMaps.get(mockApi.getPath()).add(mockApi);
            }
        }
    }


}
