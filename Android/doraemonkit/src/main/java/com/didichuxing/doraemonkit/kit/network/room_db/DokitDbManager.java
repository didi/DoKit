package com.didichuxing.doraemonkit.kit.network.room_db;

import android.text.TextUtils;

import com.blankj.utilcode.util.ThreadUtils;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;

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


    private final DokitDatabase mDb = DokitViewManager.getInstance().getDb();
    private Map<String, List<T>> mGlobalInterceptApiMaps = new HashMap<>();

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
                return (List<T>) mDb.mockApiDao().getAllInterceptApi();
            }

            @Override
            public void onSuccess(List<T> result) {
                list2mapByIntercept(result);
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
                return (List<T>) mDb.mockApiDao().getAllTemplateApi();
            }

            @Override
            public void onSuccess(List<T> result) {
                list2mapByTemplate(result);
            }
        });
    }


    /**
     * 数据库中获取指定的 template api
     */
    public T getTemplateApiByIdInDb(String id) {
        return (T) mDb.mockApiDao().findTemplateApiById(id);
    }


    /**
     * 内存中获取指定的 template api
     */
    public T getTemplateApiByIdInMap(String path, String id) {
        if (mGlobalTemplateApiMaps.get(path) == null) {
            return null;
        }

        T selectedMockApi = null;
        for (T mockApi : mGlobalTemplateApiMaps.get(path)) {
            if (mockApi.getId().equals(id)) {
                selectedMockApi = mockApi;
                break;
            }
        }

        return selectedMockApi;
    }


    /**
     * 数据库中获取指定的mock intercept api
     */
    public T getInterceptApiByIdInDb(String id) {
        return (T) mDb.mockApiDao().findInterceptApiById(id);
    }

    /**
     * 内存中中获取指定的mock intercept api
     */
    public T getInterceptApiByIdInMap(String path, String id) {
        if (mGlobalInterceptApiMaps.get(path) == null) {
            return null;
        }

        T selectedMockApi = null;
        for (T mockApi : mGlobalInterceptApiMaps.get(path)) {
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
                mDb.mockApiDao().insertAllInterceptApi(mockApis);
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
                mDb.mockApiDao().insertAllTemplateApi(mockApis);
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
                mDb.mockApiDao().updateInterceptApi(mockApi);

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
                mDb.mockApiDao().updateTemplateApi(mockApi);
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
     * 返回命中的id
     *
     * @param path
     * @param strLocalQuery
     * @param operateType
     * @return
     */
    public String isMockMatched(String path, String strLocalQuery, int operateType) {
        T mockApi = mockMatched(path, strLocalQuery, operateType);
        if (mockApi == null) {
            return "";
        }
        return mockApi.getId();
    }


    /**
     * 通过path和query查询指定的对象
     *
     * @param path
     * @param strLocalQuery
     * @param strLocalQuery 1:代表拦截 2：代表模板
     * @return
     */
    private T mockMatched(String path, String strLocalQuery, int operateType) {
        List<T> mockApis;
        if (operateType == 1) {
            mockApis = mGlobalInterceptApiMaps.get(path);
        } else {
            mockApis = mGlobalTemplateApiMaps.get(path);

        }
        if (mockApis == null) {
            return null;
        }
        T matchedMockApi = null;
        for (T mockApi : mockApis) {
            if (mockApi.isOpen() && queriesMatched(strLocalQuery, mockApi)) {
                matchedMockApi = mockApi;
                break;
            }

        }

        return matchedMockApi;
    }

    /**
     * queries 是否命中
     *
     * @param strLocalQuery
     * @param mockApi
     */
    private boolean queriesMatched(String strLocalQuery, T mockApi) {
        String mockQuery = mockApi.getQuery();
        //没有配置query参数
        if (TextUtils.isEmpty(mockQuery) && TextUtils.isEmpty(strLocalQuery)) {
            return true;
        }

        if (TextUtils.isEmpty(strLocalQuery) && !TextUtils.isEmpty(mockQuery)) {
            return false;
        }

        if (!TextUtils.isEmpty(strLocalQuery) && TextUtils.isEmpty(mockQuery)) {
            return false;
        }


        //匹配query
        if (!TextUtils.isEmpty(strLocalQuery) && !TextUtils.isEmpty(mockQuery)) {
            try {
                JSONObject mockQueryObject = new JSONObject(mockQuery);
                List<String> keys = new ArrayList<>();
                Iterator<String> iterator = mockQueryObject.keys();
                while (iterator.hasNext()) {
                    keys.add(iterator.next());
                }
                int count = 0;
                for (int index = 0; index < keys.size(); index++) {
                    String param = keys.get(index) + "=" + mockQueryObject.getString(keys.get(index));
                    //判断本地是否包含配置的query
                    if (strLocalQuery.contains(param)) {
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
