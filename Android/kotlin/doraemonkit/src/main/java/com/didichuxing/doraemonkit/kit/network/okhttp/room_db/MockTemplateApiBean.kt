package com.didichuxing.doraemonkit.kit.network.okhttp.room_db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-15-15:44
 * 描    述：mock 上传数据
 * 修订历史：
 * ================================================
 */
@Entity(tableName = "mock_template_api")
class MockTemplateApiBean : AbsMockApiBean, Serializable {
    /**
     * /设置主键 并且组建不为null
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    public override var id = ""

    @ColumnInfo(name = "mock_api_name")
    public var mockApiName: String? = null

    @ColumnInfo(name = "path")
    public override var path: String = ""

    @ColumnInfo(name = "method")
    public var method: String? = null

    @ColumnInfo(name = "query")
    public override var query: String? = null

    @ColumnInfo(name = "body")
    public override var body: String? = null

    @ColumnInfo(name = "fromType")
    var fromType: String? = null

    /**
     * 接口是否被打开
     */
    @ColumnInfo(name = "is_open")
    public override var isOpen = false

    /**
     * path 接口域名mock出来的数据
     */
    @ColumnInfo(name = "str_response")
    var strResponse: String? = null

    /**
     * mockResponse的来源 0:mock 1:真实的网络数据
     */
    @ColumnInfo(name = "response_from")
    var responseFrom = 0

    @Ignore
    var group: String? = null

    @Ignore
    var createPerson: String? = null

    @Ignore
    var modifyPerson: String? = null

    @Ignore
    var projectId: String? = null
        private set

    constructor()

    constructor(id: String, mockApiName: String?, path: String?, method: String?, fromType: String?, query: String?, body: String?, group: String?, createPerson: String?, modifyPerson: String?, projectId: String?) {
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
        this.projectId = projectId
    }

    companion object {
        /**
         * 来自真实接口返回数据
         */
        const val RESPONSE_FROM_REAL = 1

        /**
         * 来自mock接口返回的数据
         */
        const val RESPONSE_FROM_MOCK = 0
    }
}