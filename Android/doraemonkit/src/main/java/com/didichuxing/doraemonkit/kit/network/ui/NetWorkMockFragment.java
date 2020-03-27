package com.didichuxing.doraemonkit.kit.network.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ajguan.library.EasyRefreshLayout;
import com.ajguan.library.LoadModel;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.DokitConstant;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.network.bean.MockApiResponseBean;
import com.didichuxing.doraemonkit.kit.network.bean.MockInterceptTitleBean;
import com.didichuxing.doraemonkit.kit.network.bean.MockTemplateTitleBean;
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager;
import com.didichuxing.doraemonkit.kit.network.room_db.MockInterceptApiBean;
import com.didichuxing.doraemonkit.kit.network.room_db.MockTemplateApiBean;
import com.didichuxing.doraemonkit.okgo.DokitOkGo;
import com.didichuxing.doraemonkit.okgo.callback.StringCallback;
import com.didichuxing.doraemonkit.okgo.model.Response;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.widget.titlebar.HomeTitleBar;
import com.didichuxing.doraemonkit.util.DokitUtil;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.view.DkDropDownMenu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数据mock 相关设置 详情页
 */
public class NetWorkMockFragment extends BaseFragment {
    private String projectId = DokitConstant.PRODUCT_ID;
    private int pageSize = 100;
    private String mFormatApiUrl = NetworkManager.MOCK_DOMAIN + "/api/app/interface?projectId=%s&isfull=1&curPage=%s&pageSize=%s";
    private EditText mEditText;
    private TextView mTvSearch;
    private EasyRefreshLayout mInterceptRefreshLayout, mTemplateRefreshLayout;
    private InterceptMockAdapter<MockInterceptTitleBean> mInterceptApiAdapter;
    private TemplateMockAdapter<MockTemplateTitleBean> mTemplateApiAdapter;
    private RecyclerView mRvIntercept;
    private RecyclerView mRvTemplate;
    private FrameLayout mRvWrap;
    private LinearLayout mLlBottomInterceptWrap, mLlBottomTemplateWrap;
    private TextView mTvMock, mTvTemplate;
    private ImageView mIvMock, mIvTemplate;
    private String mMenuHeaders[] = {DokitUtil.getString(R.string.dk_data_mock_group),
            DokitUtil.getString(R.string.dk_data_mock_switch_status)};


    ListView mGroupListView;
    ListView mSwitchListView;
    private DkDropDownMenu mDropDownMenu;
    /**
     * drop down 分组adapter
     */
    private ListDropDownAdapter mGroupMenuAdapter, mSwitchMenuAdapter;


    private String[] mSwitchMenus = {DokitUtil.getString(R.string.dk_data_mock_switch_all),
            DokitUtil.getString(R.string.dk_data_mock_switch_opened),
            DokitUtil.getString(R.string.dk_data_mock_switch_closed)};
    private List<View> popupViews = new ArrayList<>();

    FilterConditionBean mInterceptFilterBean, mTemplateFilterBean;
    private static int BOTTOM_TAB_INDEX_0 = 0;
    private static int BOTTOM_TAB_INDEX_1 = 1;
    private int mSelectedTableIndex = BOTTOM_TAB_INDEX_0;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_net_mock;
    }


    private void initView() {
        if (getActivity() == null) {
            return;
        }
        HomeTitleBar homeTitleBar = findViewById(R.id.title_bar);
        homeTitleBar.setListener(new HomeTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                getActivity().finish();
            }
        });
        if (TextUtils.isEmpty(projectId)) {
            ToastUtils.showLong("请先到www.dokit.cn申请projectId,并参考接入手册");
            return;
        }
        mEditText = findViewById(R.id.edittext);
        mTvSearch = findViewById(R.id.tv_search);
        mTvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                    mInterceptFilterBean.setFilterText(mEditText.getText().toString());
                } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                    mTemplateFilterBean.setFilterText(mEditText.getText().toString());
                }
                filterAndNotifyData();
            }
        });
        mLlBottomInterceptWrap = findViewById(R.id.ll_bottom_tab_mock);
        mLlBottomInterceptWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchBottomTabStatus(BOTTOM_TAB_INDEX_0);
            }
        });
        mLlBottomTemplateWrap = findViewById(R.id.ll_bottom_tab_template);
        mLlBottomTemplateWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchBottomTabStatus(BOTTOM_TAB_INDEX_1);
            }
        });
        mTvMock = findViewById(R.id.tv_mock);
        mTvTemplate = findViewById(R.id.tv_template);
        mIvMock = findViewById(R.id.iv_mock);
        mIvTemplate = findViewById(R.id.iv_template);

        mDropDownMenu = findViewById(R.id.drop_down_menu);
        mRvWrap = new FrameLayout(getActivity());
        //mock
        mInterceptRefreshLayout = new EasyRefreshLayout(getActivity());
        mInterceptRefreshLayout.setBackgroundColor(getResources().getColor(R.color.dk_color_FFFFFF));
        mRvIntercept = new RecyclerView(getActivity());
        mInterceptRefreshLayout.addView(mRvIntercept);
        mInterceptRefreshLayout.setLoadMoreModel(LoadModel.NONE);
        //关闭下拉刷新
        mInterceptRefreshLayout.setEnablePullToRefresh(false);
        mInterceptRefreshLayout.addEasyEvent(new EasyRefreshLayout.EasyEvent() {
            @Override
            public void onLoadMore() {
            }

            @Override
            public void onRefreshing() {
                initResponseApis();
            }
        });
        //template
        mTemplateRefreshLayout = new EasyRefreshLayout(getActivity());
        mTemplateRefreshLayout.setBackgroundColor(getResources().getColor(R.color.dk_color_FFFFFF));
        mRvTemplate = new RecyclerView(getActivity());
        mTemplateRefreshLayout.addView(mRvTemplate);
        mTemplateRefreshLayout.setLoadMoreModel(LoadModel.NONE);
        //关闭下拉刷新
        mTemplateRefreshLayout.setEnablePullToRefresh(false);
        mTemplateRefreshLayout.addEasyEvent(new EasyRefreshLayout.EasyEvent() {
            @Override
            public void onLoadMore() {
            }

            @Override
            public void onRefreshing() {
                initResponseApis();
            }
        });
        mRvWrap.setBackgroundColor(getResources().getColor(R.color.dk_color_F5F6F7));
        mRvWrap.setPadding(0, ConvertUtils.dp2px(4), 0, 0);
        mRvWrap.addView(mInterceptRefreshLayout);
        mRvWrap.addView(mTemplateRefreshLayout);


        mRvIntercept.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRvTemplate.setLayoutManager(new LinearLayoutManager(getActivity()));
        //请求接口列表
        initResponseApis();
    }

    /**
     * 分组筛选
     */
    private String mStrInterceptGroup = "", mStrTemplateGroup = "";

    /**
     * 0:所有
     * 1:打开
     * 2:关闭
     */
    private int mInterceptOpenStatus = 0, mTemplateOpenStatus = 0;

    /**
     * 全局的列表数据  主要用于筛选
     */
    private List<MockInterceptTitleBean> mInterceptTitleBeans = new ArrayList<>();
    private List<MockTemplateTitleBean> mTemplateTitleBeans = new ArrayList<>();

    /**
     * 根据删选条件更新数据
     */
    private void filterAndNotifyData() {
        String strFilter = mEditText.getText().toString();
        if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
            List<MockInterceptTitleBean> interceptTitleBeans = new ArrayList<>();
            for (MockInterceptTitleBean interceptTitleBean : mInterceptTitleBeans) {
                MockInterceptApiBean interceptApiBean = interceptTitleBean.getSubItem(0);
                //分组信息是否匹配
                boolean boolGroupMatched;
                if (TextUtils.isEmpty(mStrInterceptGroup)) {
                    boolGroupMatched = true;
                } else {
                    boolGroupMatched = interceptApiBean.getGroup().equals(mStrInterceptGroup);
                }
                //接口开关是否匹配
                boolean boolSwitchOpenMatched;

                if (mInterceptOpenStatus == 0) {
                    boolSwitchOpenMatched = true;
                } else if (mInterceptOpenStatus == 1) {
                    if (interceptApiBean.isOpen()) {
                        boolSwitchOpenMatched = true;
                    } else {
                        boolSwitchOpenMatched = false;
                    }
                } else if (mInterceptOpenStatus == 2) {
                    if (interceptApiBean.isOpen()) {
                        boolSwitchOpenMatched = false;
                    } else {
                        boolSwitchOpenMatched = true;
                    }
                } else {
                    boolSwitchOpenMatched = false;
                }

                //手动过滤信息是否匹配
                boolean boolStrFilterMatched;
                if (TextUtils.isEmpty(strFilter)) {
                    boolStrFilterMatched = true;
                } else {
                    if (interceptApiBean.getMockApiName().contains(strFilter)) {
                        boolStrFilterMatched = true;
                    } else {
                        boolStrFilterMatched = false;
                    }
                }


                if (boolGroupMatched && boolSwitchOpenMatched && boolStrFilterMatched) {
                    interceptTitleBeans.add(interceptTitleBean);
                }
            }
            mInterceptApiAdapter.setNewData(interceptTitleBeans);
            mInterceptApiAdapter.loadMoreEnd();
            if (interceptTitleBeans.isEmpty()) {
                mInterceptApiAdapter.setEmptyView(R.layout.dk_rv_empty_layout2, mRvIntercept);
            }
        } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
            List<MockTemplateTitleBean> templateTitleBeans = new ArrayList<>();
            for (MockTemplateTitleBean templateTitleBean : mTemplateTitleBeans) {
                MockTemplateApiBean templateApiBean = templateTitleBean.getSubItem(0);
                //分组信息是否匹配
                boolean boolGroupMatched;
                if (TextUtils.isEmpty(mStrTemplateGroup)) {
                    boolGroupMatched = true;
                } else {
                    boolGroupMatched = templateApiBean.getGroup().equals(mStrTemplateGroup);
                }
                //接口开关是否匹配
                boolean boolSwitchOpenMatched;

                if (mTemplateOpenStatus == 0) {
                    boolSwitchOpenMatched = true;
                } else if (mTemplateOpenStatus == 1) {
                    if (templateApiBean.isOpen()) {
                        boolSwitchOpenMatched = true;
                    } else {
                        boolSwitchOpenMatched = false;
                    }
                } else if (mTemplateOpenStatus == 2) {
                    if (templateApiBean.isOpen()) {
                        boolSwitchOpenMatched = false;
                    } else {
                        boolSwitchOpenMatched = true;
                    }
                } else {
                    boolSwitchOpenMatched = false;
                }

                //手动过滤信息是否匹配
                boolean boolStrFilterMatched;
                if (TextUtils.isEmpty(strFilter)) {
                    boolStrFilterMatched = true;
                } else {
                    if (templateApiBean.getMockApiName().contains(strFilter)) {
                        boolStrFilterMatched = true;
                    } else {
                        boolStrFilterMatched = false;
                    }
                }


                if (boolGroupMatched && boolSwitchOpenMatched && boolStrFilterMatched) {
                    templateTitleBeans.add(templateTitleBean);
                }
            }
            mTemplateApiAdapter.setNewData(templateTitleBeans);
            mTemplateApiAdapter.loadMoreEnd();
            if (templateTitleBeans.isEmpty()) {
                mTemplateApiAdapter.setEmptyView(R.layout.dk_rv_empty_layout2, mRvTemplate);
            }
        }
    }


    /**
     * 将数据绑定到intercept RecycleView上
     *
     * @param mockTitleBeans
     */
    private void attachInterceptRv(List<MockInterceptTitleBean> mockTitleBeans) {
        //全局保存列表数据
        mInterceptTitleBeans.addAll(mockTitleBeans);

        mInterceptRefreshLayout.refreshComplete();
        if (mInterceptApiAdapter == null) {
            mInterceptApiAdapter = new InterceptMockAdapter<>(null);
            mInterceptApiAdapter.bindToRecyclerView(mRvIntercept);
            //关闭加载更多
            mInterceptApiAdapter.setEnableLoadMore(false);
            mInterceptApiAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                @Override
                public void onLoadMoreRequested() {
                    loadMoreResponseApis();
                }
            }, mRvIntercept);
            mInterceptApiAdapter.disableLoadMoreIfNotFullPage();
        }
        if (mockTitleBeans.isEmpty()) {
            mInterceptApiAdapter.setEmptyView(R.layout.dk_rv_empty_layout, mRvIntercept);
            return;
        }
        mInterceptApiAdapter.setNewData(mockTitleBeans);
        if (mockTitleBeans.size() < pageSize) {
            mInterceptApiAdapter.loadMoreEnd();
        }

    }

    /**
     * 将数据绑定到template RecycleView上
     *
     * @param mockTitleBeans
     */
    private void attachTemplateRv(List<MockTemplateTitleBean> mockTitleBeans) {
        //全局保存列表数据
        mTemplateTitleBeans.addAll(mockTitleBeans);
        mTemplateRefreshLayout.refreshComplete();
        if (mTemplateApiAdapter == null) {
            //template
            mTemplateApiAdapter = new TemplateMockAdapter<>(null);
            mTemplateApiAdapter.bindToRecyclerView(mRvTemplate);
            //关闭加载更多
            mTemplateApiAdapter.setEnableLoadMore(false);
            mTemplateApiAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                @Override
                public void onLoadMoreRequested() {
                    loadMoreResponseApis();
                }
            }, mRvIntercept);
            mTemplateApiAdapter.disableLoadMoreIfNotFullPage();
        }
        if (mockTitleBeans == null || mockTitleBeans.isEmpty()) {
            mTemplateApiAdapter.setEmptyView(R.layout.dk_rv_empty_layout, mRvTemplate);
            return;
        }

        mTemplateApiAdapter.setNewData(mockTitleBeans);
        if (mockTitleBeans.size() < pageSize) {
            mTemplateApiAdapter.loadMoreEnd();
        }

    }

    /**
     * 加载更多intercept 更新rv
     *
     * @param mockTitleBeans
     */
    private void loadMoreInterceptDates(List<MockInterceptTitleBean> mockTitleBeans) {
        mInterceptApiAdapter.addData(mockTitleBeans);
        if (mockTitleBeans.size() < pageSize) {
            mInterceptApiAdapter.loadMoreEnd();
        } else {
            mInterceptApiAdapter.loadMoreComplete();
        }
    }

    /**
     * 加载更多template 更新rv
     *
     * @param mockTitleBeans
     */
    private void loadMoreTemplateDates(List<MockTemplateTitleBean> mockTitleBeans) {
        mTemplateApiAdapter.addData(mockTitleBeans);
        if (mockTitleBeans.size() < pageSize) {
            mTemplateApiAdapter.loadMoreEnd();
        } else {
            mTemplateApiAdapter.loadMoreComplete();
        }
    }

    /**
     * 初始化mock 接口列表
     */
    private void loadMoreResponseApis() {
        int curPage = 1;
        if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
            curPage = mInterceptApiAdapter.getData().size() / pageSize + 1;
        } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
            curPage = mTemplateApiAdapter.getData().size() / pageSize + 1;
        }
        String apiUrl = String.format(mFormatApiUrl, projectId, curPage, pageSize);
        DokitOkGo.<String>get(apiUrl).tag(this)
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        try {


                            if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                                List<MockInterceptTitleBean> mockInterceptTitleBeans = dealInterceptResponseData(response.body());
                                //插入拦截接口
                                loadMoreInterceptDates(mockInterceptTitleBeans);

                            } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                                List<MockTemplateTitleBean> mockTemplateTitleBeans = dealTemplateResponseData(response.body());
                                loadMoreTemplateDates(mockTemplateTitleBeans);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                                mInterceptApiAdapter.loadMoreEnd();
                            } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                                mTemplateApiAdapter.loadMoreEnd();
                            }
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                            mInterceptApiAdapter.loadMoreEnd();
                        } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                            mTemplateApiAdapter.loadMoreEnd();
                        }
                    }
                });
    }

    /**
     * 初始化顶部筛选状态
     */
    private void initMenus(List<MockInterceptTitleBean> mockInterceptTitleBeans) {
        final List<String> groups = new ArrayList<>();
        groups.add("接口分组");
        for (MockInterceptTitleBean mockInterceptTitleBean : mockInterceptTitleBeans) {
            MockInterceptApiBean mockInterceptApiBean = mockInterceptTitleBean.getSubItem(0);
            if (!groups.contains(mockInterceptApiBean.getGroup())) {
                groups.add(mockInterceptApiBean.getGroup());
            }
        }
        //init group menu
        mGroupListView = new ListView(getActivity());
        mGroupListView.setDividerHeight(0);
        mGroupMenuAdapter = new ListDropDownAdapter(getActivity(), groups);
        mGroupListView.setAdapter(mGroupMenuAdapter);
        mGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mGroupMenuAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(groups.get(position));
                mDropDownMenu.closeMenu();
                //保存删选状态
                if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                    mInterceptFilterBean.setGroupIndex(position);
                    mStrInterceptGroup = groups.get(position).equals("接口分组") ? "" : groups.get(position);
                } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                    mTemplateFilterBean.setGroupIndex(position);
                    mStrTemplateGroup = groups.get(position).equals("接口分组") ? "" : groups.get(position);
                }

                filterAndNotifyData();
            }
        });
        //init switch menu
        mSwitchListView = new ListView(getActivity());
        mSwitchListView.setDividerHeight(0);
        mSwitchMenuAdapter = new ListDropDownAdapter(getActivity(), Arrays.asList(mSwitchMenus));
        mSwitchListView.setAdapter(mSwitchMenuAdapter);
        mSwitchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSwitchMenuAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(mSwitchMenus[position]);
                mDropDownMenu.closeMenu();
                //保存删选状态
                if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                    mInterceptFilterBean.setSwitchIndex(position);
                    mInterceptOpenStatus = position;
                } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                    mTemplateFilterBean.setSwitchIndex(position);
                    mTemplateOpenStatus = position;
                }

                filterAndNotifyData();
            }
        });
        popupViews.add(mGroupListView);
        popupViews.add(mSwitchListView);
        mDropDownMenu.setDropDownMenu(Arrays.asList(mMenuHeaders), popupViews, mRvWrap);
        mInterceptFilterBean = new FilterConditionBean();
        mInterceptFilterBean.setFilterText("");
        mInterceptFilterBean.setGroupIndex(0);
        mInterceptFilterBean.setSwitchIndex(0);
        mTemplateFilterBean = new FilterConditionBean();
        mTemplateFilterBean.setFilterText("");
        mTemplateFilterBean.setGroupIndex(0);
        mTemplateFilterBean.setSwitchIndex(0);

        //初始化tab状态
        switchBottomTabStatus(BOTTOM_TAB_INDEX_0);

    }

    /**
     * 初始化mock 接口列表
     */
    private void initResponseApis() {
        String apiUrl = String.format(mFormatApiUrl, projectId, 1, pageSize);
        DokitOkGo.<String>get(apiUrl).tag(this)
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                                List<MockInterceptTitleBean> mockInterceptTitleBeans = dealInterceptResponseData(response.body());
                                initMenus(mockInterceptTitleBeans);
                                attachInterceptRv(mockInterceptTitleBeans);
                                //测试空数据
                                //attachInterceptRv(null);
                            } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                                List<MockTemplateTitleBean> mockTemplateTitleBeans = dealTemplateResponseData(response.body());
                                attachTemplateRv(mockTemplateTitleBeans);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                                mInterceptRefreshLayout.refreshComplete();
                            } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                                mTemplateRefreshLayout.refreshComplete();
                            }
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        LogHelper.e(TAG, "error====>" + response.getException().getMessage());
                        ToastUtils.showShort(response.getException().getMessage());
                        if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                            mInterceptRefreshLayout.refreshComplete();
                        } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                            mTemplateRefreshLayout.refreshComplete();
                        }
                    }
                });

    }


    //private int rvTypeIntercept = 0;
    //private int rvTypeTemplate = 1;

    /**
     * @param strResponse 返回的数据
     * @return
     */
    private List<MockInterceptTitleBean> dealInterceptResponseData(String strResponse) throws Exception {

        JSONObject responseJsonObject = new JSONObject(strResponse);
        JSONArray jsonArray = responseJsonObject.getJSONObject("data").getJSONArray("datalist");

        MockApiResponseBean mockApiResponseBean = GsonUtils.fromJson(strResponse, MockApiResponseBean.class);
        List<MockApiResponseBean.DataBean.DatalistBean> lists = mockApiResponseBean.getData().getDatalist();
        ArrayList<MockInterceptTitleBean> mockInterceptTitleBeans = new ArrayList<>();
        for (int index = 0; index < lists.size(); index++) {
            MockApiResponseBean.DataBean.DatalistBean datalistBean = lists.get(index);
            JSONObject paramsJsonObject = null;
            if (jsonArray != null) {
                paramsJsonObject = jsonArray.getJSONObject(index).getJSONObject("query");
            }

            String modifyName = "null";
            if (datalistBean.getCurStatus() != null && datalistBean.getCurStatus().getOperator() != null) {
                modifyName = datalistBean.getCurStatus().getOperator().getName();
            }
            //新建 intercept
            MockInterceptTitleBean mockInterceptTitleBean = new MockInterceptTitleBean(datalistBean.getName());
            mockInterceptTitleBean.addSubItem(new MockInterceptApiBean(datalistBean.get_id(), datalistBean.getName(), datalistBean.getPath(), datalistBean.getMethod(), datalistBean.getFormatType(), paramsJsonObject == null ? " " : paramsJsonObject.toString(), datalistBean.getCategoryName(), datalistBean.getOwner().getName(), modifyName, datalistBean.getSceneList()));
            mockInterceptTitleBeans.add(mockInterceptTitleBean);

        }
        //插入拦截接口
        insertAllInterceptApis(mockInterceptTitleBeans);
        return mockInterceptTitleBeans;
    }

    /**
     * @param strResponse 返回的数据
     * @return
     */
    private List<MockTemplateTitleBean> dealTemplateResponseData(String strResponse) throws Exception {

        JSONObject responseJsonObject = new JSONObject(strResponse);
        JSONArray jsonArray = responseJsonObject.getJSONObject("data").getJSONArray("datalist");

        MockApiResponseBean mockApiResponseBean = GsonUtils.fromJson(strResponse, MockApiResponseBean.class);
        List<MockApiResponseBean.DataBean.DatalistBean> lists = mockApiResponseBean.getData().getDatalist();
        ArrayList<MockTemplateTitleBean> mockTemplateTitleBeans = new ArrayList<>();
        for (int index = 0; index < lists.size(); index++) {
            MockApiResponseBean.DataBean.DatalistBean datalistBean = lists.get(index);
            JSONObject paramsJsonObject = null;
            if (jsonArray != null) {
                paramsJsonObject = jsonArray.getJSONObject(index).getJSONObject("query");
            }

            String modifyName = "null";
            if (datalistBean.getCurStatus() != null && datalistBean.getCurStatus().getOperator() != null) {
                modifyName = datalistBean.getCurStatus().getOperator().getName();
            }

            //新建 template
            MockTemplateTitleBean mockTemplateTitleBean = new MockTemplateTitleBean(datalistBean.getName());
            mockTemplateTitleBean.addSubItem(new MockTemplateApiBean(datalistBean.get_id(), datalistBean.getName(), datalistBean.getPath(), datalistBean.getMethod(), datalistBean.getFormatType(), paramsJsonObject == null ? " " : paramsJsonObject.toString(), datalistBean.getCategoryName(), datalistBean.getOwner().getName(), modifyName, datalistBean.getProjectId()));
            mockTemplateTitleBeans.add(mockTemplateTitleBean);
        }

        //插入模板接口
        insertAllTemplateApis(mockTemplateTitleBeans);
        return mockTemplateTitleBeans;
    }


    /**
     * 插入intercept数据
     *
     * @param mockTitleBeans
     */
    private void insertAllInterceptApis(ArrayList<MockInterceptTitleBean> mockTitleBeans) {
        List<MockInterceptApiBean> mockApis = new ArrayList<>();

        for (MockInterceptTitleBean multiItemEntity : mockTitleBeans) {
            MockInterceptTitleBean mockInterceptTitleBean = multiItemEntity;
            MockInterceptApiBean mockApi = mockInterceptTitleBean.getSubItem(0);
            if (!hasInterceptApiInDb(mockApi.getPath(), mockApi.getId())) {
                mockApis.add(mockApi);
            } else {
                updateInterceptApi(mockApi);
            }
        }

        DokitDbManager.getInstance().insertAllInterceptApi(mockApis);

    }


    /**
     * 插入template数据
     *
     * @param mockTitleBeans
     */
    private void insertAllTemplateApis(ArrayList<MockTemplateTitleBean> mockTitleBeans) {
        List<MockTemplateApiBean> mockApis = new ArrayList<>();

        for (MockTemplateTitleBean multiItemEntity : mockTitleBeans) {
            MockTemplateTitleBean mockTemplateTitleBean = multiItemEntity;
            MockTemplateApiBean mockApi = mockTemplateTitleBean.getSubItem(0);
            if (!hasTemplateApiInDb(mockApi.getPath(), mockApi.getId())) {
                mockApis.add(mockApi);
            } else {
                updateTemplateApi(mockApi);
            }
        }

        DokitDbManager.getInstance().insertAllTemplateApi(mockApis);

    }

    /**
     * 更新本地数据到新的数据列表中
     *
     * @param mockApi
     * @return
     */
    private void updateInterceptApi(MockInterceptApiBean mockApi) {
        List<MockInterceptApiBean> localInterceptApis = (List<MockInterceptApiBean>) DokitDbManager.getInstance().getGlobalInterceptApiMaps().get(mockApi.getPath());
        if (localInterceptApis == null) {
            return;
        }
        for (MockInterceptApiBean localMockApi : localInterceptApis) {
            if (localMockApi.getId().equals(mockApi.getId())) {
                mockApi.setOpen(localMockApi.isOpen());
                mockApi.setSelectedSceneId(localMockApi.getSelectedSceneId());
                mockApi.setSelectedSceneName(localMockApi.getSelectedSceneName());
                break;
            }
        }

    }


    /**
     * 更新本地数据到新的数据列表中
     *
     * @param mockApi
     * @return
     */
    private void updateTemplateApi(MockTemplateApiBean mockApi) {
        List<MockTemplateApiBean> localTemplateApis = (List<MockTemplateApiBean>) DokitDbManager.getInstance().getGlobalTemplateApiMaps().get(mockApi.getPath());
        if (localTemplateApis == null) {
            return;
        }
        for (MockTemplateApiBean localMockApi : localTemplateApis) {
            if (localMockApi.getId().equals(mockApi.getId())) {
                mockApi.setOpen(localMockApi.isOpen());
                mockApi.setResponseFrom(localMockApi.getResponseFrom());
                mockApi.setStrResponse(localMockApi.getStrResponse());
                break;
            }
        }
    }


    /**
     * 查找本地数据是否已经存在该条数据
     *
     * @param id
     * @return
     */
    private boolean hasInterceptApiInDb(String path, String id) {
        MockInterceptApiBean mockInterceptApi = (MockInterceptApiBean) DokitDbManager.getInstance().getInterceptApiByIdInMap(path, id, DokitDbManager.FROM_SDK_OTHER);
        return mockInterceptApi != null;
    }


    /**
     * 查找本地数据是否已经存在该条数据
     *
     * @param id
     * @return
     */
    private boolean hasTemplateApiInDb(String path, String id) {
        MockTemplateApiBean mockTemplateApi = (MockTemplateApiBean) DokitDbManager.getInstance().getTemplateApiByIdInMap(path, id, DokitDbManager.FROM_SDK_OTHER);
        return mockTemplateApi != null;
    }

    /**
     * 切换底部tabbar 状态
     *
     * @param tabIndex
     */
    private void switchBottomTabStatus(int tabIndex) {
        switch (tabIndex) {
            case 0:
                mTvMock.setTextColor(getResources().getColor(R.color.dk_color_337CC4));
                mTvTemplate.setTextColor(getResources().getColor(R.color.dk_color_333333));
                mIvMock.setImageResource(R.drawable.dk_mock_highlight);
                mIvTemplate.setImageResource(R.drawable.dk_template_normal);
                mInterceptRefreshLayout.setVisibility(View.VISIBLE);
                mTemplateRefreshLayout.setVisibility(View.GONE);
                mSelectedTableIndex = BOTTOM_TAB_INDEX_0;
                break;
            case 1:
                mTvMock.setTextColor(getResources().getColor(R.color.dk_color_333333));
                mTvTemplate.setTextColor(getResources().getColor(R.color.dk_color_337CC4));
                mIvMock.setImageResource(R.drawable.dk_mock_normal);
                mIvTemplate.setImageResource(R.drawable.dk_template_highlight);
                mInterceptRefreshLayout.setVisibility(View.GONE);
                mTemplateRefreshLayout.setVisibility(View.VISIBLE);
                mSelectedTableIndex = BOTTOM_TAB_INDEX_1;
                if (mTemplateApiAdapter == null) {
                    initResponseApis();
                }
                break;
            default:
                break;
        }
        resetMenuStatus();
    }

    /**
     * 重置删选按钮的状态
     */
    private void resetMenuStatus() {
        if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
            if (mInterceptFilterBean != null) {
                mGroupMenuAdapter.setCheckItem(mInterceptFilterBean.getGroupIndex());
                mSwitchMenuAdapter.setCheckItem(mInterceptFilterBean.getSwitchIndex());
                mDropDownMenu.resetTabText(new String[]{mGroupMenuAdapter.getList().get(mInterceptFilterBean.getGroupIndex()), mSwitchMenuAdapter.getList().get(mInterceptFilterBean.getSwitchIndex())});
                mEditText.setText("" + mInterceptFilterBean.getFilterText());
            }

        } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
            if (mTemplateFilterBean != null) {
                mGroupMenuAdapter.setCheckItem(mTemplateFilterBean.getGroupIndex());
                mSwitchMenuAdapter.setCheckItem(mTemplateFilterBean.getSwitchIndex());
                mDropDownMenu.resetTabText(new String[]{mGroupMenuAdapter.getList().get(mTemplateFilterBean.getGroupIndex()), mSwitchMenuAdapter.getList().get(mTemplateFilterBean.getSwitchIndex())});
                mEditText.setText("" + mTemplateFilterBean.getFilterText());
            }
        }

        mDropDownMenu.closeMenu();
    }


    /**
     * 删选条件保存的状态
     */
    private static class FilterConditionBean {
        int groupIndex;
        int switchIndex;
        String filterText;

        public int getGroupIndex() {
            return groupIndex;
        }

        public void setGroupIndex(int groupIndex) {
            this.groupIndex = groupIndex;
        }

        public int getSwitchIndex() {
            return switchIndex;
        }

        public void setSwitchIndex(int switchIndex) {
            this.switchIndex = switchIndex;
        }

        public String getFilterText() {
            return filterText;
        }

        public void setFilterText(String filterText) {
            this.filterText = filterText;
        }

        @Override
        public String toString() {
            return "FilterConditionBean{" +
                    "groupIndex=" + groupIndex +
                    ", switchIndex=" + switchIndex +
                    ", filterText='" + filterText + '\'' +
                    '}';
        }
    }


}
