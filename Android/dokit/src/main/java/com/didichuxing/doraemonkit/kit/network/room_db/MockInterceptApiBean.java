package com.didichuxing.doraemonkit.kit.network.room_db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.didichuxing.doraemonkit.kit.network.bean.MockApiResponseBean;
import com.didichuxing.doraemonkit.kit.network.ui.InterceptMockAdapter;

import java.util.List;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-15-15:44
 * 描    述：mock 拦截数据
 * 修订历史：
 * ================================================
 */
@Entity(tableName = "mock_intercept_api")
public class MockInterceptApiBean extends AbsMockApiBean {
    /**
     * /设置主键 并且组建不为null
     */
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id = "";
    @ColumnInfo(name = "mock_api_name")
    private String mockApiName;
    @ColumnInfo(name = "path")
    private String path;
    @ColumnInfo(name = "method")
    private String method;
    @ColumnInfo(name = "query")
    private String query;
    @ColumnInfo(name = "body")
    private String body;
    @ColumnInfo(name = "fromType")
    private String fromType;
    @ColumnInfo(name = "selected_scene_name")
    private String selectedSceneName;
    @ColumnInfo(name = "selected_scene_id")
    private String selectedSceneId;
    /**
     * 接口是否被打开
     */
    @ColumnInfo(name = "is_open")
    private boolean isOpen;

    @Ignore
    private String group;
    @Ignore
    private String createPerson;
    @Ignore
    private String modifyPerson;
    @Ignore
    private List<MockApiResponseBean.DataBean.DatalistBean.SceneListBean> sceneList;


    public MockInterceptApiBean() {
    }

    @Ignore
    public MockInterceptApiBean(@NonNull String id, String mockApiName, String path, String method, String fromType, String query, String body, String group, String createPerson, String modifyPerson, List<MockApiResponseBean.DataBean.DatalistBean.SceneListBean> sceneList) {
        this.id = id;
        this.mockApiName = mockApiName;
        this.path = path;
        this.method = method;
        this.fromType = fromType;
        this.query = query;
        this.body = body;
        this.group = group;
        this.createPerson = createPerson;
        this.modifyPerson = modifyPerson;
        this.sceneList = sceneList;
    }

    @Override
    public String getId() {
        return id;
    }


    public String getMockApiName() {
        return mockApiName;
    }

    public void setMockApiName(String mockApiName) {
        this.mockApiName = mockApiName;
    }

    @Override
    public String getPath() {
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

    @Override
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFromType() {
        return fromType;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    public String getSelectedSceneName() {
        return selectedSceneName;
    }

    public void setSelectedSceneName(String selectedSceneName) {
        this.selectedSceneName = selectedSceneName;
    }

    @Override
    public String getSelectedSceneId() {
        return selectedSceneId;
    }

    public void setSelectedSceneId(String selectedSceneId) {
        this.selectedSceneId = selectedSceneId;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getCreatePerson() {
        return createPerson;
    }

    public void setCreatePerson(String createPerson) {
        this.createPerson = createPerson;
    }

    public String getModifyPerson() {
        return modifyPerson;
    }

    public void setModifyPerson(String modifyPerson) {
        this.modifyPerson = modifyPerson;
    }

    public List<MockApiResponseBean.DataBean.DatalistBean.SceneListBean> getSceneList() {
        return sceneList;
    }

    public void setSceneList(List<MockApiResponseBean.DataBean.DatalistBean.SceneListBean> sceneList) {
        this.sceneList = sceneList;
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void setOpen(boolean open) {
        this.isOpen = open;
    }


}
