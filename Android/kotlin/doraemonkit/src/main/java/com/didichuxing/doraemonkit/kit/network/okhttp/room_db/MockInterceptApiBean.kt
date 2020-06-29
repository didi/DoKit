package com.didichuxing.doraemonkit.kit.network.okhttp.room_db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.didichuxing.doraemonkit.kit.network.okhttp.bean.MockApiResponseBean.DataBean.DatalistBean.SceneListBean

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
class MockInterceptApiBean : AbsMockApiBean {
    /**
     * /设置主键 并且组建不为null
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    public override var id = ""

    @ColumnInfo(name = "mock_api_name")
    var mockApiName: String? = null

    @ColumnInfo(name = "path")
    public override var path: String = ""

    @ColumnInfo(name = "method")
    public var method: String? = null

    @ColumnInfo(name = "query")
    public override var query: String? = null

    @ColumnInfo(name = "body")
    public override var body: String? = null

    @ColumnInfo(name = "fromType")
    public var fromType: String? = null

    @ColumnInfo(name = "selected_scene_name")
    public var selectedSceneName: String? = null

    @ColumnInfo(name = "selected_scene_id")
    public override var selectedSceneId: String = ""

    /**
     * 接口是否被打开
     */
    @ColumnInfo(name = "is_open")
    public override var isOpen = false

    @Ignore
    var group: String? = null

    @Ignore
    var createPerson: String? = null

    @Ignore
    var modifyPerson: String? = null

    @Ignore
    var sceneList: MutableList<SceneListBean>? = null

    constructor()
    @Ignore
    constructor(id: String, mockApiName: String?, path: String?, method: String?, fromType: String?, query: String?, body: String?, group: String?, createPerson: String?, modifyPerson: String?, sceneList: MutableList<SceneListBean>?) {
        this.id = id
        this.mockApiName = mockApiName
        this.path = path!!
        this.method = method
        this.fromType = fromType
        this.query = query
        this.body = body
        this.group = group
        this.createPerson = createPerson
        this.modifyPerson = modifyPerson
        this.sceneList = sceneList
    }

}