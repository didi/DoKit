package com.didichuxing.doraemonkit.kit.network.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.constant.NetWorkConstant
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.network.okhttp.bean.MockApiResponseBean
import com.didichuxing.doraemonkit.kit.network.okhttp.bean.MockInterceptTitleBean
import com.didichuxing.doraemonkit.kit.network.okhttp.bean.MockTemplateTitleBean
import com.didichuxing.doraemonkit.kit.network.okhttp.room_db.DokitDbManager
import com.didichuxing.doraemonkit.kit.network.okhttp.room_db.MockInterceptApiBean
import com.didichuxing.doraemonkit.kit.network.okhttp.room_db.MockTemplateApiBean
import com.didichuxing.doraemonkit.okgo.DokitOkGo
import com.didichuxing.doraemonkit.okgo.callback.StringCallback
import com.didichuxing.doraemonkit.okgo.model.Response
import com.didichuxing.doraemonkit.util.DokitUtil
import com.didichuxing.doraemonkit.widget.bravh.entity.node.BaseNode
import com.didichuxing.doraemonkit.widget.bravh.module.BaseLoadMoreModule
import com.didichuxing.doraemonkit.widget.dropdown.DkDropDownMenu
import com.didichuxing.doraemonkit.widget.easyrefresh.EasyRefreshLayout
import com.didichuxing.doraemonkit.widget.easyrefresh.EasyRefreshLayout.EasyEvent
import com.didichuxing.doraemonkit.widget.easyrefresh.LoadModel
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar
import kotlinx.android.synthetic.main.dk_fragment_net_mock.*
import org.json.JSONObject
import java.util.*

/**
 * 数据mock 相关设置 详情页
 *
 * @author jintai
 */
class NetWorkMockFragment : BaseFragment() {
    private val projectId = DokitConstant.PRODUCT_ID
    private val pageSize = 100
    private val mFormatApiUrl = NetWorkConstant.MOCK_DOMAIN + "/api/app/interface?projectId=%s&isfull=1&curPage=%s&pageSize=%s"
    private var mInterceptRefreshLayout: EasyRefreshLayout? = null
    private var mTemplateRefreshLayout: EasyRefreshLayout? = null
    private var mInterceptApiAdapter: InterceptMockAdapter? = null
    private var mTemplateApiAdapter: TemplateMockAdapter? = null
    private var mInterceptLoadMoreModule: BaseLoadMoreModule? = null
    private var mTemplateLoadMoreModule: BaseLoadMoreModule? = null
    private val mMenuHeaders = arrayOf(DokitUtil.getString(R.string.dk_data_mock_group),
            DokitUtil.getString(R.string.dk_data_mock_switch_status))


    private var mRvWrap: FrameLayout? = null
    private var mRvIntercept: RecyclerView? = null
    private var mRvTemplate: RecyclerView? = null
    /**
     * drop down 分组adapter
     */
    private var mGroupMenuAdapter: ListDropDownAdapter? = null
    private var mSwitchMenuAdapter: ListDropDownAdapter? = null
    private val mSwitchMenus = arrayOf(DokitUtil.getString(R.string.dk_data_mock_switch_all),
            DokitUtil.getString(R.string.dk_data_mock_switch_opened),
            DokitUtil.getString(R.string.dk_data_mock_switch_closed))
    private val popupViews: MutableList<View> = ArrayList()
    private var mInterceptFilterBean: FilterConditionBean? = null
    private var mTemplateFilterBean: FilterConditionBean? = null
    private var mSelectedTableIndex = BOTTOM_TAB_INDEX_0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_net_mock
    }

    private fun initView() {
        if (activity == null) {
            return
        }
        title_bar.setListener(object : HomeTitleBar.OnTitleBarClickListener {
            override fun onRightClick() {
                finish()
            }
        })
        if (TextUtils.isEmpty(projectId)) {
            ToastUtils.showLong(DokitUtil.getString(R.string.dk_data_mock_plugin_toast))
            return
        }


        tv_search.setOnClickListener {
            if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                mInterceptFilterBean!!.filterText = edittext.text.toString()
            } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                mTemplateFilterBean!!.filterText = edittext.text.toString()
            }
            filterAndNotifyData()
        }

        ll_bottom_tab_mock.setOnClickListener { switchBottomTabStatus(BOTTOM_TAB_INDEX_0) }
        ll_bottom_tab_template.setOnClickListener { switchBottomTabStatus(BOTTOM_TAB_INDEX_1) }



        mRvWrap = FrameLayout(activity!!)
        //mock
        mInterceptRefreshLayout = EasyRefreshLayout(activity!!)
        mInterceptRefreshLayout?.apply {
            setBackgroundColor(resources.getColor(R.color.dk_color_FFFFFF))
            mRvIntercept = RecyclerView(activity!!)
            addView(mRvIntercept)
            loadMoreModel = LoadModel.NONE
            //关闭下拉刷新
            isEnablePullToRefresh = false
            addEasyEvent(object : EasyEvent {
                override fun onLoadMore() {}
                override fun onRefreshing() {
                    initResponseApis()
                }
            })
        }


        //template
        mTemplateRefreshLayout = EasyRefreshLayout(activity!!)
        mTemplateRefreshLayout?.apply {
            setBackgroundColor(resources.getColor(R.color.dk_color_FFFFFF))
            mRvTemplate = RecyclerView(activity!!)
            addView(mRvTemplate)
            loadMoreModel = LoadModel.NONE
            //关闭下拉刷新
            isEnablePullToRefresh = false
            addEasyEvent(object : EasyEvent {
                override fun onLoadMore() {}
                override fun onRefreshing() {
                    initResponseApis()
                }
            })
        }

        mRvWrap?.apply {
            setBackgroundColor(resources.getColor(R.color.dk_color_F5F6F7))
            setPadding(0, ConvertUtils.dp2px(4f), 0, 0)
            addView(mInterceptRefreshLayout)
            addView(mTemplateRefreshLayout)
        }


        mRvIntercept!!.layoutManager = LinearLayoutManager(activity)
        mRvTemplate!!.layoutManager = LinearLayoutManager(activity)
        //请求接口列表
        initResponseApis()
    }

    /**
     * 分组筛选
     */
    private var mStrInterceptGroup = ""
    private var mStrTemplateGroup = ""

    /**
     * 0:所有
     * 1:打开
     * 2:关闭
     */
    private var mInterceptOpenStatus = 0
    private var mTemplateOpenStatus = 0

    /**
     * 全局的列表数据  主要用于筛选
     */
    private val mInterceptTitleBeans: MutableList<MockInterceptTitleBean<*>> = ArrayList()
    private val mTemplateTitleBeans: MutableList<MockTemplateTitleBean<*>> = ArrayList()

    /**
     * 根据删选条件更新数据
     */
    private fun filterAndNotifyData() {
        val strFilter = edittext.text.toString()
        if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
            val interceptTitleBeans: MutableList<MockInterceptTitleBean<*>> = ArrayList()
            for (interceptTitleBean in mInterceptTitleBeans) {
                val interceptApiBean = interceptTitleBean.childNode!!.get(0) as MockInterceptApiBean
                //分组信息是否匹配
                var boolGroupMatched: Boolean
                boolGroupMatched = if (TextUtils.isEmpty(mStrInterceptGroup)) {
                    true
                } else {
                    interceptApiBean.group == mStrInterceptGroup
                }
                //接口开关是否匹配
                var boolSwitchOpenMatched: Boolean
                boolSwitchOpenMatched = if (mInterceptOpenStatus == 0) {
                    true
                } else if (mInterceptOpenStatus == 1) {
                    interceptApiBean.isOpen
                } else if (mInterceptOpenStatus == 2) {
                    !interceptApiBean.isOpen
                } else {
                    false
                }

                //手动过滤信息是否匹配
                var boolStrFilterMatched: Boolean
                boolStrFilterMatched = if (TextUtils.isEmpty(strFilter)) {
                    true
                } else {
                    interceptApiBean.mockApiName!!.contains(strFilter)
                }
                if (boolGroupMatched && boolSwitchOpenMatched && boolStrFilterMatched) {
                    interceptTitleBeans.add(interceptTitleBean)
                }
            }
            mInterceptApiAdapter!!.setNewInstance(interceptTitleBeans as MutableList<BaseNode>)
            mInterceptLoadMoreModule!!.loadMoreEnd()
            if (interceptTitleBeans.isEmpty()) {
                mInterceptApiAdapter!!.setEmptyView(R.layout.dk_rv_empty_layout2)
            }
        } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
            val templateTitleBeans: MutableList<MockTemplateTitleBean<*>> = ArrayList()
            for (templateTitleBean in mTemplateTitleBeans) {
                val templateApiBean = templateTitleBean.childNode!!.get(0) as MockTemplateApiBean
                //分组信息是否匹配
                var boolGroupMatched: Boolean
                boolGroupMatched = if (TextUtils.isEmpty(mStrTemplateGroup)) {
                    true
                } else {
                    templateApiBean.group == mStrTemplateGroup
                }
                //接口开关是否匹配
                var boolSwitchOpenMatched: Boolean
                boolSwitchOpenMatched = if (mTemplateOpenStatus == 0) {
                    true
                } else if (mTemplateOpenStatus == 1) {
                    templateApiBean.isOpen
                } else if (mTemplateOpenStatus == 2) {
                    !templateApiBean.isOpen
                } else {
                    false
                }

                //手动过滤信息是否匹配
                var boolStrFilterMatched: Boolean
                boolStrFilterMatched = if (TextUtils.isEmpty(strFilter)) {
                    true
                } else {
                    templateApiBean.mockApiName!!.contains(strFilter)
                }
                if (boolGroupMatched && boolSwitchOpenMatched && boolStrFilterMatched) {
                    templateTitleBeans.add(templateTitleBean)
                }
            }
            mTemplateApiAdapter!!.setNewInstance(templateTitleBeans as MutableList<BaseNode>)
            mTemplateLoadMoreModule!!.loadMoreEnd()
            if (templateTitleBeans.isEmpty()) {
                mTemplateApiAdapter!!.setEmptyView(R.layout.dk_rv_empty_layout2)
            }
        }
    }

    /**
     * 将数据绑定到intercept RecycleView上
     *
     * @param mockTitleBeans
     */
    private fun attachInterceptRv(mockTitleBeans: List<MockInterceptTitleBean<*>>) {
        //全局保存列表数据
        mInterceptTitleBeans.addAll(mockTitleBeans)
        mInterceptRefreshLayout!!.refreshComplete()
        if (mInterceptApiAdapter == null) {
            mInterceptApiAdapter = InterceptMockAdapter(null)
            mRvIntercept!!.adapter = mInterceptApiAdapter
            mInterceptLoadMoreModule = mInterceptApiAdapter!!.loadMoreModule
            mInterceptLoadMoreModule?.apply {
                //关闭加载更多
                isEnableLoadMore = false
                setOnLoadMoreListener { loadMoreResponseApis() }
                isEnableLoadMoreIfNotFullPage = false
            }

        }
        if (mockTitleBeans.isEmpty()) {
            mInterceptApiAdapter!!.setEmptyView(R.layout.dk_rv_empty_layout)
            return
        }
        mInterceptApiAdapter!!.setNewInstance(mockTitleBeans as MutableList<BaseNode>)
        if (mockTitleBeans.size < pageSize) {
            mInterceptLoadMoreModule!!.loadMoreEnd()
        }
    }

    /**
     * 将数据绑定到template RecycleView上
     *
     * @param mockTitleBeans
     */
    private fun attachTemplateRv(mockTitleBeans: List<MockTemplateTitleBean<*>>) {
        //全局保存列表数据
        mTemplateTitleBeans.addAll(mockTitleBeans)
        mTemplateRefreshLayout!!.refreshComplete()
        if (mTemplateApiAdapter == null) {
            //template
            mTemplateApiAdapter = TemplateMockAdapter(null)
            mRvTemplate!!.adapter = mTemplateApiAdapter
            mTemplateLoadMoreModule = mTemplateApiAdapter!!.loadMoreModule
            //关闭加载更多
            mTemplateLoadMoreModule?.apply {
                isEnableLoadMore = false
                setOnLoadMoreListener { loadMoreResponseApis() }
                isEnableLoadMoreIfNotFullPage = false
            }

        }
        if (mockTitleBeans.isEmpty()) {
            mTemplateApiAdapter!!.setEmptyView(R.layout.dk_rv_empty_layout)
            return
        }
        mTemplateApiAdapter!!.setNewInstance(mockTitleBeans as MutableList<BaseNode>)
        if (mockTitleBeans.size < pageSize) {
            mTemplateLoadMoreModule!!.loadMoreEnd()
        }
    }

    /**
     * 加载更多intercept 更新rv
     *
     * @param mockTitleBeans
     */
    private fun loadMoreInterceptDates(mockTitleBeans: List<MockInterceptTitleBean<*>>) {
        mInterceptApiAdapter!!.addData(mockTitleBeans)
        if (mockTitleBeans.size < pageSize) {
            mInterceptLoadMoreModule!!.loadMoreEnd()
        } else {
            mInterceptLoadMoreModule!!.loadMoreComplete()
        }
    }

    /**
     * 加载更多template 更新rv
     *
     * @param mockTitleBeans
     */
    private fun loadMoreTemplateDates(mockTitleBeans: List<MockTemplateTitleBean<*>>) {
        mTemplateApiAdapter!!.addData(mockTitleBeans)
        if (mockTitleBeans.size < pageSize) {
            mTemplateLoadMoreModule!!.loadMoreEnd()
        } else {
            mTemplateLoadMoreModule!!.loadMoreComplete()
        }
    }

    /**
     * 初始化mock 接口列表
     */
    private fun loadMoreResponseApis() {
        var curPage = 1
        if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
            curPage = mInterceptApiAdapter!!.data.size / pageSize + 1
        } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
            curPage = mTemplateApiAdapter!!.data.size / pageSize + 1
        }
        val apiUrl = String.format(mFormatApiUrl, projectId, curPage, pageSize)
        DokitOkGo.get<String>(apiUrl).tag(this)
                .execute(object : StringCallback() {
                    override fun onSuccess(response: Response<String>) {
                        try {
                            if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                                val mockInterceptTitleBeans = dealInterceptResponseData(response.body())
                                //插入拦截接口
                                loadMoreInterceptDates(mockInterceptTitleBeans)
                            } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                                val mockTemplateTitleBeans = dealTemplateResponseData(response.body())
                                loadMoreTemplateDates(mockTemplateTitleBeans)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                                mInterceptLoadMoreModule!!.loadMoreEnd()
                            } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                                mTemplateLoadMoreModule!!.loadMoreEnd()
                            }
                        }
                    }

                    override fun onError(response: Response<String>) {
                        super.onError(response)
                        if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                            mInterceptLoadMoreModule!!.loadMoreEnd()
                        } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                            mTemplateLoadMoreModule!!.loadMoreEnd()
                        }
                    }
                })
    }

    /**
     * 初始化顶部筛选状态
     */
    private fun initMenus(mockInterceptTitleBeans: List<MockInterceptTitleBean<*>>) {
        val groups: MutableList<String> = ArrayList()
        groups.add(DokitUtil.getString(R.string.dk_data_mock_group))
        for (mockInterceptTitleBean in mockInterceptTitleBeans) {
            val mockInterceptApiBean = mockInterceptTitleBean.childNode!!.get(0) as MockInterceptApiBean
            if (!groups.contains(mockInterceptApiBean.group)) {
                mockInterceptApiBean.group?.let {
                    groups.add(it)
                }
            }
        }
        //init group menu
        val mGroupListView = ListView(activity)
        mGroupListView.dividerHeight = 0
        mGroupMenuAdapter = ListDropDownAdapter(activity!!, groups)
        mGroupListView.adapter = mGroupMenuAdapter
        mGroupListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            mGroupMenuAdapter!!.setCheckItem(position)

            drop_down_menu.setTabText(groups[position])
            drop_down_menu.closeMenu()
            //保存删选状态
            if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                mInterceptFilterBean!!.groupIndex = position
                mStrInterceptGroup = if (groups[position] == DokitUtil.getString(R.string.dk_data_mock_group)) "" else groups[position]
            } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                mTemplateFilterBean!!.groupIndex = position
                mStrTemplateGroup = if (groups[position] == DokitUtil.getString(R.string.dk_data_mock_group)) "" else groups[position]
            }
            filterAndNotifyData()
        }
        //init switch menu
        val mSwitchListView = ListView(activity)
        mSwitchListView.dividerHeight = 0
        mSwitchMenuAdapter = ListDropDownAdapter(activity!!, Arrays.asList(*mSwitchMenus))
        mSwitchListView.adapter = mSwitchMenuAdapter
        mSwitchListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            mSwitchMenuAdapter!!.setCheckItem(position)
            drop_down_menu.setTabText(mSwitchMenus[position])
            drop_down_menu.closeMenu()
            //保存删选状态
            if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                mInterceptFilterBean!!.switchIndex = position
                mInterceptOpenStatus = position
            } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                mTemplateFilterBean!!.switchIndex = position
                mTemplateOpenStatus = position
            }
            filterAndNotifyData()
        }
        popupViews.add(mGroupListView)
        popupViews.add(mSwitchListView)
        drop_down_menu.setDropDownMenu(Arrays.asList(*mMenuHeaders), popupViews, mRvWrap!!)
        mInterceptFilterBean = FilterConditionBean()
        mInterceptFilterBean?.apply {
            filterText = ""
            groupIndex = 0
            switchIndex = 0
        }

        mTemplateFilterBean = FilterConditionBean()
        mTemplateFilterBean?.apply {
            filterText = ""
            groupIndex = 0
            switchIndex = 0
        }


        //初始化tab状态
        switchBottomTabStatus(BOTTOM_TAB_INDEX_0)
    }

    /**
     * 初始化mock 接口列表
     */
    private fun initResponseApis() {
        val apiUrl = String.format(mFormatApiUrl, projectId, 1, pageSize)
//        LogHelper.i(TAG, "apiUrl===>" + apiUrl);
        DokitOkGo.get<String>(apiUrl).tag(this)
                .execute(object : StringCallback() {
                    override fun onSuccess(response: Response<String>) {
                        try {
                            if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                                val mockInterceptTitleBeans = dealInterceptResponseData(response.body())
                                initMenus(mockInterceptTitleBeans)
                                attachInterceptRv(mockInterceptTitleBeans)
                                //测试空数据
                                //attachInterceptRv(null);
                            } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                                val mockTemplateTitleBeans = dealTemplateResponseData(response.body())
                                attachTemplateRv(mockTemplateTitleBeans)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                                mInterceptRefreshLayout!!.refreshComplete()
                            } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                                mTemplateRefreshLayout!!.refreshComplete()
                            }
                        }
                    }

                    override fun onError(response: Response<String>) {
                        super.onError(response)
                        //LogHelper.e(TAG, "error====>" + response.getException().getMessage());
                        ToastUtils.showShort(response.exception.message)
                        if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                            mInterceptRefreshLayout!!.refreshComplete()
                        } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                            mTemplateRefreshLayout!!.refreshComplete()
                        }
                    }
                })
    }
    //private int rvTypeIntercept = 0;
    //private int rvTypeTemplate = 1;
    /**
     * @param strResponse 返回的数据
     * @return
     */
    @Throws(Exception::class)
    private fun dealInterceptResponseData(strResponse: String): List<MockInterceptTitleBean<*>> {
        val responseJsonObject = JSONObject(strResponse)
        val jsonArray = responseJsonObject.getJSONObject("data").getJSONArray("datalist")
        val mockApiResponseBean = GsonUtils.fromJson(strResponse, MockApiResponseBean::class.java)
        val lists = mockApiResponseBean.data?.datalist
        val mockInterceptTitleBeans = ArrayList<MockInterceptTitleBean<*>>()
        if (lists != null) {
            for (index in lists.indices) {
                val datalistBean = lists[index]
                var queryJsonObject: JSONObject
                var bodyJsonObject: JSONObject
                val mockJsonObject = jsonArray.getJSONObject(index)
                queryJsonObject = if (mockJsonObject.has("query")) {
                    mockJsonObject.getJSONObject("query")
                } else {
                    JSONObject()
                }
                bodyJsonObject = if (mockJsonObject.has("body")) {
                    mockJsonObject.getJSONObject("body")
                } else {
                    JSONObject()
                }
                var modifyName: String? = "null"
                if (datalistBean.curStatus != null && datalistBean.curStatus!!.operator != null) {
                    modifyName = datalistBean.curStatus!!.operator?.name
                }
                //新建 intercept
                val mockInterceptApiBeans: MutableList<MockInterceptApiBean> = ArrayList()
                datalistBean?.let {
                    mockInterceptApiBeans.add(MockInterceptApiBean(datalistBean._id!!, datalistBean.name, datalistBean.path
                            , datalistBean.method, datalistBean.formatType,
                            queryJsonObject.toString(), bodyJsonObject.toString(),
                            datalistBean.categoryName, datalistBean.owner?.name,
                            modifyName, datalistBean.sceneList))
                }


                val mockInterceptTitleBean: MockInterceptTitleBean<*> = MockInterceptTitleBean<MockInterceptApiBean>(datalistBean.name!!, mockInterceptApiBeans)
                mockInterceptTitleBeans.add(mockInterceptTitleBean)
            }
        }
        //插入拦截接口
        insertAllInterceptApis(mockInterceptTitleBeans)
        return mockInterceptTitleBeans
    }

    /**
     * @param strResponse 返回的数据
     * @return
     */
    @Throws(Exception::class)
    private fun dealTemplateResponseData(strResponse: String): List<MockTemplateTitleBean<*>> {
        val responseJsonObject = JSONObject(strResponse)
        val jsonArray = responseJsonObject.getJSONObject("data").getJSONArray("datalist")
        val mockApiResponseBean = GsonUtils.fromJson(strResponse, MockApiResponseBean::class.java)
        val lists = mockApiResponseBean.data?.datalist
        val mockTemplateTitleBeans = ArrayList<MockTemplateTitleBean<*>>()
        lists?.let {


            for (index in lists.indices) {
                val datalistBean = lists?.get(index)
                var queryJsonObject: JSONObject
                var bodyJsonObject: JSONObject
                val mockJsonObject = jsonArray.getJSONObject(index)
                queryJsonObject = if (mockJsonObject.has("query")) {
                    mockJsonObject.getJSONObject("query")
                } else {
                    JSONObject()
                }
                bodyJsonObject = if (mockJsonObject.has("body")) {
                    mockJsonObject.getJSONObject("body")
                } else {
                    JSONObject()
                }
                var modifyName: String? = "null"
                if (datalistBean != null) {
                    if (datalistBean.curStatus != null && datalistBean.curStatus!!.operator != null) {
                        modifyName = datalistBean.curStatus!!.operator?.name
                    }
                }
                //新建 template
                val mockTemplateApiBeans: MutableList<MockTemplateApiBean> = ArrayList()
                datalistBean?.let {
                    mockTemplateApiBeans.add(MockTemplateApiBean(datalistBean._id!!, datalistBean.name,
                            datalistBean.path, datalistBean.method,
                            datalistBean.formatType, queryJsonObject.toString(),
                            bodyJsonObject.toString(), datalistBean.categoryName, datalistBean.owner?.name,
                            modifyName, datalistBean.projectId))
                }

                val mockTemplateTitleBean: MockTemplateTitleBean<*> = MockTemplateTitleBean<MockTemplateApiBean>(datalistBean?.name!!, mockTemplateApiBeans)
                mockTemplateTitleBeans.add(mockTemplateTitleBean)
            }
        }
        //插入模板接口
        insertAllTemplateApis(mockTemplateTitleBeans)
        return mockTemplateTitleBeans
    }

    /**
     * 插入intercept数据
     *
     * @param mockTitleBeans
     */
    private fun insertAllInterceptApis(mockTitleBeans: ArrayList<MockInterceptTitleBean<*>>) {
        val mockApis: MutableList<MockInterceptApiBean> = ArrayList()
        for (mockInterceptTitleBean in mockTitleBeans) {
            val mockApi = mockInterceptTitleBean.childNode!!.get(0) as MockInterceptApiBean
            if (!hasInterceptApiInDb(mockApi.path, mockApi.id)) {
                mockApis.add(mockApi)
            } else {
                updateInterceptApi(mockApi)
            }
        }
        DokitDbManager.instance.insertAllInterceptApi(mockApis)
    }

    /**
     * 插入template数据
     *
     * @param mockTitleBeans
     */
    private fun insertAllTemplateApis(mockTitleBeans: ArrayList<MockTemplateTitleBean<*>>) {
        val mockApis: MutableList<MockTemplateApiBean> = ArrayList()
        for (mockTemplateTitleBean in mockTitleBeans) {
            val mockApi = mockTemplateTitleBean.childNode!!.get(0) as MockTemplateApiBean
            if (!hasTemplateApiInDb(mockApi.path, mockApi.id)) {
                mockApis.add(mockApi)
            } else {
                updateTemplateApi(mockApi)
            }
        }
        DokitDbManager.instance.insertAllTemplateApi(mockApis)
    }

    /**
     * 更新本地数据到新的数据列表中
     * @param mockApi
     * @return
     */
    private fun updateInterceptApi(mockApi: MockInterceptApiBean) {
        val localInterceptApis = DokitDbManager.instance.globalInterceptApiMaps?.get(mockApi.path) as List<MockInterceptApiBean>?
                ?: return
        for (localMockApi in localInterceptApis) {
            if (localMockApi.id == mockApi.id) {
                mockApi.isOpen = localMockApi.isOpen
                mockApi.selectedSceneId = localMockApi.selectedSceneId
                mockApi.selectedSceneName = localMockApi.selectedSceneName
                break
            }
        }
    }

    /**
     * 更新本地数据到新的数据列表中
     * @param mockApi
     * @return
     */
    private fun updateTemplateApi(mockApi: MockTemplateApiBean) {
        val localTemplateApis = DokitDbManager.instance.globalTemplateApiMaps?.get(mockApi.path) as List<MockTemplateApiBean>?
                ?: return
        for (localMockApi in localTemplateApis) {
            if (localMockApi.id == mockApi.id) {
                mockApi.isOpen = localMockApi.isOpen
                mockApi.responseFrom = localMockApi.responseFrom
                mockApi.strResponse = localMockApi.strResponse
                break
            }
        }
    }

    /**
     * 查找本地数据是否已经存在该条数据
     *
     * @param id
     * @return
     */
    private fun hasInterceptApiInDb(path: String, id: String): Boolean {
        val mockInterceptApi = DokitDbManager.instance.getInterceptApiByIdInMap(path, id, DokitDbManager.FROM_SDK_OTHER) as MockInterceptApiBean?
        return mockInterceptApi != null
    }

    /**
     * 查找本地数据是否已经存在该条数据
     *
     * @param id
     * @return
     */
    private fun hasTemplateApiInDb(path: String, id: String): Boolean {
        val mockTemplateApi = DokitDbManager.instance.getTemplateApiByIdInMap(path, id, DokitDbManager.FROM_SDK_OTHER) as MockTemplateApiBean?
        return mockTemplateApi != null
    }

    /**
     * 切换底部tabbar 状态
     *
     * @param tabIndex
     */
    private fun switchBottomTabStatus(tabIndex: Int) {
        when (tabIndex) {
            0 -> {
                tv_mock!!.setTextColor(resources.getColor(R.color.dk_color_337CC4))
                tv_template.setTextColor(resources.getColor(R.color.dk_color_333333))
                iv_mock.setImageResource(R.mipmap.dk_mock_highlight)
                iv_template.setImageResource(R.mipmap.dk_template_normal)
                mInterceptRefreshLayout!!.visibility = View.VISIBLE
                mTemplateRefreshLayout!!.visibility = View.GONE
                mSelectedTableIndex = BOTTOM_TAB_INDEX_0
            }
            1 -> {
                tv_mock.setTextColor(resources.getColor(R.color.dk_color_333333))
                tv_template.setTextColor(resources.getColor(R.color.dk_color_337CC4))
                iv_mock.setImageResource(R.mipmap.dk_mock_normal)
                iv_template.setImageResource(R.mipmap.dk_template_highlight)
                mInterceptRefreshLayout!!.visibility = View.GONE
                mTemplateRefreshLayout!!.visibility = View.VISIBLE
                mSelectedTableIndex = BOTTOM_TAB_INDEX_1
                if (mTemplateApiAdapter == null) {
                    initResponseApis()
                }
            }
            else -> {
            }
        }
        resetMenuStatus()
    }

    /**
     * 重置删选按钮的状态
     */
    private fun resetMenuStatus() {
        if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
            if (mInterceptFilterBean != null) {
                mGroupMenuAdapter!!.setCheckItem(mInterceptFilterBean!!.groupIndex)
                mSwitchMenuAdapter!!.setCheckItem(mInterceptFilterBean!!.switchIndex)
                drop_down_menu.resetTabText(arrayOf(mGroupMenuAdapter!!.list[mInterceptFilterBean!!.groupIndex], mSwitchMenuAdapter!!.list[mInterceptFilterBean!!.switchIndex]))
                edittext.setText("" + mInterceptFilterBean!!.filterText)
            }
        } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
            if (mTemplateFilterBean != null) {
                mGroupMenuAdapter!!.setCheckItem(mTemplateFilterBean!!.groupIndex)
                mSwitchMenuAdapter!!.setCheckItem(mTemplateFilterBean!!.switchIndex)
                drop_down_menu.resetTabText(arrayOf(mGroupMenuAdapter!!.list[mTemplateFilterBean!!.groupIndex], mSwitchMenuAdapter!!.list[mTemplateFilterBean!!.switchIndex]))
                edittext.setText("" + mTemplateFilterBean!!.filterText)
            }
        }
        drop_down_menu.closeMenu()
    }

    /**
     * 删选条件保存的状态
     */
    private class FilterConditionBean {
        var groupIndex = 0
        var switchIndex = 0
        var filterText: String? = null

        override fun toString(): String {
            return "FilterConditionBean{" +
                    "groupIndex=" + groupIndex +
                    ", switchIndex=" + switchIndex +
                    ", filterText='" + filterText + '\'' +
                    '}'
        }
    }

    companion object {
        private const val BOTTOM_TAB_INDEX_0 = 0
        private const val BOTTOM_TAB_INDEX_1 = 1
    }
}