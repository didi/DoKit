package com.didichuxing.doraemonkit.kit.network.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.didichuxing.doraemonkit.util.ConvertUtils;
import com.didichuxing.doraemonkit.util.GsonUtils;
import com.didichuxing.doraemonkit.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.core.DoKitManager;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.network.bean.MockApiResponseBean;
import com.didichuxing.doraemonkit.kit.network.bean.MockInterceptTitleBean;
import com.didichuxing.doraemonkit.kit.network.bean.MockTemplateTitleBean;
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager;
import com.didichuxing.doraemonkit.kit.network.room_db.MockInterceptApiBean;
import com.didichuxing.doraemonkit.kit.network.room_db.MockTemplateApiBean;
import com.didichuxing.doraemonkit.util.DoKitCommUtil;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.volley.VolleyManager;
import com.didichuxing.doraemonkit.widget.brvah.listener.OnLoadMoreListener;
import com.didichuxing.doraemonkit.widget.brvah.module.BaseLoadMoreModule;
import com.didichuxing.doraemonkit.widget.dropdown.DkDropDownMenu;
import com.didichuxing.doraemonkit.widget.easyrefresh.EasyRefreshLayout;
import com.didichuxing.doraemonkit.widget.easyrefresh.LoadModel;
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数据mock 相关设置 详情页
 *
 * @author jintai
 */
public class NetWorkMockFragment extends BaseFragment {
    private String projectId = DoKitManager.PRODUCT_ID;
    private int pageSize = 50;
    private String mFormatApiUrl = NetworkManager.MOCK_DOMAIN + "/api/app/interface?projectId=%s&isfull=1&curPage=%s&pageSize=%s";
    private EditText mEditText;
    private EasyRefreshLayout mInterceptRefreshLayout, mTemplateRefreshLayout;
    private InterceptMockAdapter mInterceptApiAdapter;
    private TemplateMockAdapter mTemplateApiAdapter;
    private BaseLoadMoreModule mInterceptLoadMoreModule;
    private BaseLoadMoreModule mTemplateLoadMoreModule;
    private RecyclerView mRvIntercept;
    private RecyclerView mRvTemplate;
    private FrameLayout mRvWrap;
    private TextView mTvMock, mTvTemplate;
    private ImageView mIvMock, mIvTemplate;
    private String[] mMenuHeaders = {DoKitCommUtil.getString(R.string.dk_data_mock_group),
            DoKitCommUtil.getString(R.string.dk_data_mock_switch_status)};

    private DkDropDownMenu mDropDownMenu;
    /**
     * drop down 分组adapter
     */
    private ListDropDownAdapter mGroupMenuAdapter, mSwitchMenuAdapter;


    private String[] mSwitchMenus = {DoKitCommUtil.getString(R.string.dk_data_mock_switch_all),
            DoKitCommUtil.getString(R.string.dk_data_mock_switch_opened),
            DoKitCommUtil.getString(R.string.dk_data_mock_switch_closed)};
    private List<View> popupViews = new ArrayList<>();

    private FilterConditionBean mInterceptFilterBean, mTemplateFilterBean;
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

    HomeTitleBar mHomeTitleBar;

    private void initView() {
        if (getActivity() == null) {
            return;
        }
        mHomeTitleBar = findViewById(R.id.title_bar);
        mHomeTitleBar.setListener(new HomeTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                finish();
            }
        });
        if (TextUtils.isEmpty(projectId)) {
            ToastUtils.showLong(DoKitCommUtil.getString(R.string.dk_data_mock_plugin_toast));
            return;
        }
        mEditText = findViewById(R.id.edittext);
        TextView mTvSearch = findViewById(R.id.tv_search);
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
        LinearLayout mLlBottomInterceptWrap = findViewById(R.id.ll_bottom_tab_mock);
        mLlBottomInterceptWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchBottomTabStatus(BOTTOM_TAB_INDEX_0);
            }
        });
        LinearLayout mLlBottomTemplateWrap = findViewById(R.id.ll_bottom_tab_template);
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
                initResponseApis(1);
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
                initResponseApis(1);
            }
        });
        mRvWrap.setBackgroundColor(getResources().getColor(R.color.dk_color_F5F6F7));
        mRvWrap.setPadding(0, ConvertUtils.dp2px(4), 0, 0);
        mRvWrap.addView(mInterceptRefreshLayout);
        mRvWrap.addView(mTemplateRefreshLayout);


        mRvIntercept.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRvTemplate.setLayoutManager(new LinearLayoutManager(getActivity()));
        //请求接口列表
        initResponseApis(1);
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
                MockInterceptApiBean interceptApiBean = (MockInterceptApiBean) interceptTitleBean.getChildNode().get(0);
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
            mInterceptApiAdapter.setNewInstance((List) interceptTitleBeans);
            mInterceptLoadMoreModule.loadMoreEnd();
            if (interceptTitleBeans.isEmpty()) {
                mInterceptApiAdapter.setEmptyView(R.layout.dk_rv_empty_layout2);
            }

            //设置数据条数
            mHomeTitleBar.setTitle(DoKitCommUtil.getString(R.string.dk_kit_network_mock) + "(" + interceptTitleBeans.size() + ")");
        } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
            List<MockTemplateTitleBean> templateTitleBeans = new ArrayList<>();
            for (MockTemplateTitleBean templateTitleBean : mTemplateTitleBeans) {
                MockTemplateApiBean templateApiBean = (MockTemplateApiBean) templateTitleBean.getChildNode().get(0);
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
            mTemplateApiAdapter.setNewInstance((List) templateTitleBeans);
            mTemplateLoadMoreModule.loadMoreEnd();
            if (templateTitleBeans.isEmpty()) {
                mTemplateApiAdapter.setEmptyView(R.layout.dk_rv_empty_layout2);
            }
            //设置数据条数
            mHomeTitleBar.setTitle(DoKitCommUtil.getString(R.string.dk_kit_network_mock) + "(" + templateTitleBeans.size() + ")");
        }
    }


    /**
     * 将数据绑定到intercept RecycleView上
     *
     * @param mockTitleBeans
     */
    private void attachInterceptRv(@NonNull List<MockInterceptTitleBean> mockTitleBeans) {
        //全局保存列表数据
        mInterceptTitleBeans.addAll(mockTitleBeans);

        mInterceptRefreshLayout.refreshComplete();
        if (mInterceptApiAdapter == null) {
            mInterceptApiAdapter = new InterceptMockAdapter(null);
            mRvIntercept.setAdapter(mInterceptApiAdapter);
            mInterceptLoadMoreModule = mInterceptApiAdapter.getLoadMoreModule();
            //关闭加载更多
            mInterceptLoadMoreModule.setEnableLoadMore(false);
            mInterceptLoadMoreModule.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                        mInterceptLoadMoreModule.loadMoreEnd();
                    } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                        mTemplateLoadMoreModule.loadMoreEnd();
                    }
                    //loadMoreResponseApis();
                }
            });

            mInterceptLoadMoreModule.setEnableLoadMoreIfNotFullPage(false);
        }
        if (mockTitleBeans.isEmpty()) {
            mInterceptApiAdapter.setEmptyView(R.layout.dk_rv_empty_layout);
            return;
        }
        mInterceptApiAdapter.setNewInstance((List) mockTitleBeans);
        if (mockTitleBeans.size() < pageSize) {
            mInterceptLoadMoreModule.loadMoreEnd();
        }

    }

    /**
     * 将数据绑定到template RecycleView上
     *
     * @param mockTitleBeans
     */
    private void attachTemplateRv(@NonNull List<MockTemplateTitleBean> mockTitleBeans) {
        //全局保存列表数据
        mTemplateTitleBeans.addAll(mockTitleBeans);
        mTemplateRefreshLayout.refreshComplete();
        if (mTemplateApiAdapter == null) {
            //template
            mTemplateApiAdapter = new TemplateMockAdapter(null);
            mRvTemplate.setAdapter(mTemplateApiAdapter);
            mTemplateLoadMoreModule = mTemplateApiAdapter.getLoadMoreModule();
            //关闭加载更多
            mTemplateLoadMoreModule.setEnableLoadMore(false);
            mTemplateLoadMoreModule.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                        mInterceptLoadMoreModule.loadMoreEnd();
                    } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                        mTemplateLoadMoreModule.loadMoreEnd();
                    }
                    //loadMoreResponseApis();
                }
            });

            mTemplateLoadMoreModule.setEnableLoadMoreIfNotFullPage(false);
        }
        if (mockTitleBeans.isEmpty()) {
            mTemplateApiAdapter.setEmptyView(R.layout.dk_rv_empty_layout);
            return;
        }

        mTemplateApiAdapter.setNewInstance((List) mockTitleBeans);
        if (mockTitleBeans.size() < pageSize) {
            mTemplateLoadMoreModule.loadMoreEnd();
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
            mInterceptLoadMoreModule.loadMoreEnd();
        } else {
            mInterceptLoadMoreModule.loadMoreComplete();
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
            mTemplateLoadMoreModule.loadMoreEnd();
        } else {
            mTemplateLoadMoreModule.loadMoreComplete();
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

        Request<String> request = new StringRequest(Request.Method.GET, apiUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                        List<MockInterceptTitleBean> mockInterceptTitleBeans = dealInterceptResponseData(response);
                        //插入拦截接口
                        loadMoreInterceptDates(mockInterceptTitleBeans);

                    } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                        List<MockTemplateTitleBean> mockTemplateTitleBeans = dealTemplateResponseData(response);
                        loadMoreTemplateDates(mockTemplateTitleBeans);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                        mInterceptLoadMoreModule.loadMoreEnd();
                    } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                        mTemplateLoadMoreModule.loadMoreEnd();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                    mInterceptLoadMoreModule.loadMoreEnd();
                } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                    mTemplateLoadMoreModule.loadMoreEnd();
                }
            }
        });

        VolleyManager.INSTANCE.add(request);

    }

    /**
     * 初始化顶部筛选状态
     */
    private void initMenus(List<MockInterceptTitleBean> mockInterceptTitleBeans) {
        final List<String> groups = new ArrayList<>();
        groups.add(DoKitCommUtil.getString(R.string.dk_data_mock_group));
        for (MockInterceptTitleBean mockInterceptTitleBean : mockInterceptTitleBeans) {
            MockInterceptApiBean mockInterceptApiBean = (MockInterceptApiBean) mockInterceptTitleBean.getChildNode().get(0);
            if (!groups.contains(mockInterceptApiBean.getGroup())) {
                groups.add(mockInterceptApiBean.getGroup());
            }
        }
        //init group menu
        ListView mGroupListView = new ListView(getActivity());
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
                    mStrInterceptGroup = groups.get(position).equals(DoKitCommUtil.getString(R.string.dk_data_mock_group)) ? "" : groups.get(position);
                } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                    mTemplateFilterBean.setGroupIndex(position);
                    mStrTemplateGroup = groups.get(position).equals(DoKitCommUtil.getString(R.string.dk_data_mock_group)) ? "" : groups.get(position);
                }

                filterAndNotifyData();
            }
        });
        //init switch menu
        ListView mSwitchListView = new ListView(getActivity());
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

    String initMockInterceptResponse = "";
    String initTemplateInterceptResponse = "";

    /**
     * 由于一次性请求数据过多会导致服务挂掉 所以拆分多次请求
     */
    private void initResponseApis(int currentPage) {
        if (currentPage == 1) {
            if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                initMockInterceptResponse = "";
            } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                initTemplateInterceptResponse = "";
            }
        }
        String apiUrl = String.format(mFormatApiUrl, projectId, currentPage, pageSize);
        LogHelper.i(TAG, "apiUrl===>" + apiUrl);

        Request<String> request = new StringRequest(Request.Method.GET, apiUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                        MockApiResponseBean mockApiResponseBean = GsonUtils.fromJson(response, MockApiResponseBean.class);
                        List<MockApiResponseBean.DataBean.DatalistBean> lists = mockApiResponseBean.getData().getDatalist();
                        //判断是否为null
                        if (initMockInterceptResponse.isEmpty()) {
                            initMockInterceptResponse = response;
                        } else {
                            MockApiResponseBean AllMockApiResponseBean = GsonUtils.fromJson(initMockInterceptResponse, MockApiResponseBean.class);
                            List<MockApiResponseBean.DataBean.DatalistBean> AllLists = AllMockApiResponseBean.getData().getDatalist();
                            AllLists.addAll(lists);
                            initMockInterceptResponse = GsonUtils.toJson(AllMockApiResponseBean);
                        }
                        if (lists.size() < pageSize) {
                            List<MockInterceptTitleBean> mockInterceptTitleBeans = dealInterceptResponseData(initMockInterceptResponse);
                            initMenus(mockInterceptTitleBeans);
                            attachInterceptRv(mockInterceptTitleBeans);
                            mHomeTitleBar.setTitle(DoKitCommUtil.getString(R.string.dk_kit_network_mock) + "(" + mockInterceptTitleBeans.size() + ")");
                        } else {
                            MockApiResponseBean AllMockApiResponseBean = GsonUtils.fromJson(initMockInterceptResponse, MockApiResponseBean.class);
                            initResponseApis(AllMockApiResponseBean.getData().getDatalist().size() / pageSize + 1);
                        }

                        //测试空数据
                        //attachInterceptRv(null);
                    } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                        MockApiResponseBean mockApiResponseBean = GsonUtils.fromJson(response, MockApiResponseBean.class);
                        List<MockApiResponseBean.DataBean.DatalistBean> lists = mockApiResponseBean.getData().getDatalist();
                        //判断是否为null
                        if (initTemplateInterceptResponse.isEmpty()) {
                            initTemplateInterceptResponse = response;
                        } else {
                            MockApiResponseBean AllMockApiResponseBean = GsonUtils.fromJson(initMockInterceptResponse, MockApiResponseBean.class);
                            List<MockApiResponseBean.DataBean.DatalistBean> AllLists = AllMockApiResponseBean.getData().getDatalist();
                            AllLists.addAll(lists);
                            initTemplateInterceptResponse = GsonUtils.toJson(AllMockApiResponseBean);
                        }
                        if (lists.size() < pageSize) {
                            List<MockTemplateTitleBean> mockInterceptTitleBeans = dealTemplateResponseData(initTemplateInterceptResponse);
                            attachTemplateRv(mockInterceptTitleBeans);
                            mHomeTitleBar.setTitle(DoKitCommUtil.getString(R.string.dk_kit_network_mock) + "(" + mockInterceptTitleBeans.size() + ")");
                        } else {
                            MockApiResponseBean AllMockApiResponseBean = GsonUtils.fromJson(initTemplateInterceptResponse, MockApiResponseBean.class);
                            initResponseApis(AllMockApiResponseBean.getData().getDatalist().size() / pageSize + 1);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                        mInterceptRefreshLayout.refreshComplete();
                        mHomeTitleBar.setTitle(DoKitCommUtil.getString(R.string.dk_kit_network_mock) + "(0)");
                    } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                        mTemplateRefreshLayout.refreshComplete();
                        mHomeTitleBar.setTitle(DoKitCommUtil.getString(R.string.dk_kit_network_mock) + "(0)");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.e(TAG, "error====>" + error.getMessage());
                ToastUtils.showShort(error.getMessage());
                if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
                    mInterceptRefreshLayout.refreshComplete();
                } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
                    mTemplateRefreshLayout.refreshComplete();
                }
            }
        });

        VolleyManager.INSTANCE.add(request);
    }

    /**
     * 初始化mock 接口列表
     */
//    private void initResponseApis() {
//        String apiUrl = String.format(mFormatApiUrl, projectId, 1, pageSize);
//        LogHelper.i(TAG, "apiUrl===>" + apiUrl);
//
//        Request<String> request = new StringRequest(Request.Method.GET, apiUrl, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
//                        List<MockInterceptTitleBean> mockInterceptTitleBeans = dealInterceptResponseData(response);
//                        initMenus(mockInterceptTitleBeans);
//                        attachInterceptRv(mockInterceptTitleBeans);
//
//                        mHomeTitleBar.setTitle(DokitUtil.getString(R.string.dk_kit_network_mock) + "(" + mockInterceptTitleBeans.size() + ")");
//                        //测试空数据
//                        //attachInterceptRv(null);
//                    } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
//                        List<MockTemplateTitleBean> mockTemplateTitleBeans = dealTemplateResponseData(response);
//                        attachTemplateRv(mockTemplateTitleBeans);
//                        mHomeTitleBar.setTitle(DokitUtil.getString(R.string.dk_kit_network_mock) + "(" + mockTemplateTitleBeans.size() + ")");
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
//                        mInterceptRefreshLayout.refreshComplete();
//                        mHomeTitleBar.setTitle(DokitUtil.getString(R.string.dk_kit_network_mock) + "(0)");
//                    } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
//                        mTemplateRefreshLayout.refreshComplete();
//                        mHomeTitleBar.setTitle(DokitUtil.getString(R.string.dk_kit_network_mock) + "(0)");
//                    }
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                LogHelper.e(TAG, "error====>" + error.getMessage());
//                ToastUtils.showShort(error.getMessage());
//                if (mSelectedTableIndex == BOTTOM_TAB_INDEX_0) {
//                    mInterceptRefreshLayout.refreshComplete();
//                } else if (mSelectedTableIndex == BOTTOM_TAB_INDEX_1) {
//                    mTemplateRefreshLayout.refreshComplete();
//                }
//            }
//        });
//
//        VolleyManager.INSTANCE.add(request);
//
//
//    }


    //private int rvTypeIntercept = 0;
    //private int rvTypeTemplate = 1;

    /**
     * @param strResponse 返回的数据
     * @return
     */
    private @NonNull
    List<MockInterceptTitleBean> dealInterceptResponseData(String strResponse) throws Exception {

        JSONObject responseJsonObject = new JSONObject(strResponse);
        JSONArray jsonArray = responseJsonObject.getJSONObject("data").getJSONArray("datalist");

        MockApiResponseBean mockApiResponseBean = GsonUtils.fromJson(strResponse, MockApiResponseBean.class);
        List<MockApiResponseBean.DataBean.DatalistBean> lists = mockApiResponseBean.getData().getDatalist();
        ArrayList<MockInterceptTitleBean> mockInterceptTitleBeans = new ArrayList<>();
        for (int index = 0; index < lists.size(); index++) {
            MockApiResponseBean.DataBean.DatalistBean datalistBean = lists.get(index);
            JSONObject queryJsonObject;
            JSONObject bodyJsonObject;
            JSONObject mockJsonObject = jsonArray.getJSONObject(index);
            if (mockJsonObject.has("query")) {
                queryJsonObject = mockJsonObject.getJSONObject("query");
            } else {
                queryJsonObject = new JSONObject();
            }

            if (mockJsonObject.has("body")) {
                bodyJsonObject = mockJsonObject.getJSONObject("body");
            } else {
                bodyJsonObject = new JSONObject();
            }

            String modifyName = "null";
            if (datalistBean.getCurStatus() != null && datalistBean.getCurStatus().getOperator() != null) {
                modifyName = datalistBean.getCurStatus().getOperator().getName();
            }
            //新建 intercept
            List<MockInterceptApiBean> mockInterceptApiBeans = new ArrayList<>();
            mockInterceptApiBeans.add(new MockInterceptApiBean(datalistBean.get_id(), datalistBean.getName(), datalistBean.getPath()
                    , datalistBean.getMethod(), datalistBean.getFormatType(),
                    queryJsonObject.toString(), bodyJsonObject.toString(),
                    datalistBean.getCategoryName(), datalistBean.getOwner().getName(),
                    modifyName, datalistBean.getSceneList()));
            MockInterceptTitleBean mockInterceptTitleBean = new MockInterceptTitleBean(datalistBean.getName(), mockInterceptApiBeans);
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
    private @NonNull
    List<MockTemplateTitleBean> dealTemplateResponseData(String strResponse) throws Exception {

        JSONObject responseJsonObject = new JSONObject(strResponse);
        JSONArray jsonArray = responseJsonObject.getJSONObject("data").getJSONArray("datalist");

        MockApiResponseBean mockApiResponseBean = GsonUtils.fromJson(strResponse, MockApiResponseBean.class);
        List<MockApiResponseBean.DataBean.DatalistBean> lists = mockApiResponseBean.getData().getDatalist();
        ArrayList<MockTemplateTitleBean> mockTemplateTitleBeans = new ArrayList<>();
        for (int index = 0; index < lists.size(); index++) {
            MockApiResponseBean.DataBean.DatalistBean datalistBean = lists.get(index);
            JSONObject queryJsonObject;
            JSONObject bodyJsonObject;
            JSONObject mockJsonObject = jsonArray.getJSONObject(index);
            if (mockJsonObject.has("query")) {
                queryJsonObject = mockJsonObject.getJSONObject("query");
            } else {
                queryJsonObject = new JSONObject();
            }

            if (mockJsonObject.has("body")) {
                bodyJsonObject = mockJsonObject.getJSONObject("body");
            } else {
                bodyJsonObject = new JSONObject();
            }

            String modifyName = "null";
            if (datalistBean.getCurStatus() != null && datalistBean.getCurStatus().getOperator() != null) {
                modifyName = datalistBean.getCurStatus().getOperator().getName();
            }
            //新建 template
            List<MockTemplateApiBean> mockTemplateApiBeans = new ArrayList<>();
            mockTemplateApiBeans.add(new MockTemplateApiBean(datalistBean.get_id(), datalistBean.getName(),
                    datalistBean.getPath(), datalistBean.getMethod(),
                    datalistBean.getFormatType(), queryJsonObject.toString(),
                    bodyJsonObject.toString(), datalistBean.getCategoryName(), datalistBean.getOwner().getName(),
                    modifyName, datalistBean.getProjectId()));
            MockTemplateTitleBean mockTemplateTitleBean = new MockTemplateTitleBean(datalistBean.getName(), mockTemplateApiBeans);
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

        for (MockInterceptTitleBean mockInterceptTitleBean : mockTitleBeans) {
            MockInterceptApiBean mockApi = (MockInterceptApiBean) mockInterceptTitleBean.getChildNode().get(0);
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

        for (MockTemplateTitleBean mockTemplateTitleBean : mockTitleBeans) {
            MockTemplateApiBean mockApi = (MockTemplateApiBean) mockTemplateTitleBean.getChildNode().get(0);
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
                mIvMock.setImageResource(R.mipmap.dk_mock_highlight);
                mIvTemplate.setImageResource(R.mipmap.dk_template_normal);
                mInterceptRefreshLayout.setVisibility(View.VISIBLE);
                mTemplateRefreshLayout.setVisibility(View.GONE);
                mSelectedTableIndex = BOTTOM_TAB_INDEX_0;
                //设置数据条数
                if (mInterceptApiAdapter != null) {
                    mHomeTitleBar.setTitle(DoKitCommUtil.getString(R.string.dk_kit_network_mock) + "(" + mInterceptApiAdapter.getData().size() + ")");
                }
                break;
            case 1:
                mTvMock.setTextColor(getResources().getColor(R.color.dk_color_333333));
                mTvTemplate.setTextColor(getResources().getColor(R.color.dk_color_337CC4));
                mIvMock.setImageResource(R.mipmap.dk_mock_normal);
                mIvTemplate.setImageResource(R.mipmap.dk_template_highlight);
                mInterceptRefreshLayout.setVisibility(View.GONE);
                mTemplateRefreshLayout.setVisibility(View.VISIBLE);
                mSelectedTableIndex = BOTTOM_TAB_INDEX_1;
                if (mTemplateApiAdapter == null) {
                    initResponseApis(1);
                }
                //设置数据条数
                //设置数据条数
                if (mTemplateApiAdapter != null) {
                    mHomeTitleBar.setTitle(DoKitCommUtil.getString(R.string.dk_kit_network_mock) + "(" + mTemplateApiAdapter.getData().size() + ")");
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
