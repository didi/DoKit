package com.didichuxing.doraemonkit.kit.network.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.network.bean.NetworkRecord;
import com.didichuxing.doraemonkit.kit.network.bean.Request;
import com.didichuxing.doraemonkit.kit.network.bean.Response;
import com.didichuxing.doraemonkit.kit.network.utils.ByteUtil;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsRecyclerAdapter;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsViewBinder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @desc: 抓包列表页适配器
 */
public class NetworkListAdapter extends AbsRecyclerAdapter<AbsViewBinder<NetworkRecord>, NetworkRecord> implements Filterable {

    private OnItemClickListener mListener;
    private List<NetworkRecord> mSourceList = new ArrayList<>();

    public NetworkListAdapter(Context context) {
        super(context);
    }

    @Override
    protected AbsViewBinder<NetworkRecord> createViewHolder(View view, int viewType) {
        return new ItemViewHolder(view);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return inflater.inflate(R.layout.dk_item_network_list, parent, false);
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemViewHolder extends AbsViewBinder<NetworkRecord> {
        private static final String METHOD_FORMAT = "%s>%s";
        private static final String FLOW_FORMAT = "↑ %s ↓%s";
        private static final String CODE_FORMAT = "[%d]";
        private static final String UNKNOWN = "unknown";
        private TextView url;
        private TextView platform;
        private TextView method;
        private TextView code;
        private TextView time;
        private TextView flow;
        private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS");

        public ItemViewHolder(View view) {
            super(view);
        }

        @Override
        protected void getViews() {
            url = getView(R.id.network_list_url);
            platform = getView(R.id.network_list_platform);
            method = getView(R.id.network_list_method);
            code = getView(R.id.network_list_code);
            time = getView(R.id.network_list_time_and_cost);
            flow = getView(R.id.network_list_flow);
        }

        @Override
        public void bind(final NetworkRecord record) {
            if (record.mRequest != null) {
                Request request = record.mRequest;
                url.setText(request.url);
                String cost;
                if (record.endTime < record.startTime) {
                    cost = UNKNOWN;
                } else {
                    cost = ((float) (record.endTime - record.startTime)) / 1000f + "s";
                }
                String startTime = mDateFormat.format(new Date(record.startTime));
                time.setText(getContext().getString(R.string.dk_kit_network_time_format, startTime, cost));
            } else {
                url.setText(UNKNOWN);
                time.setText(getContext().getString(R.string.dk_kit_network_time_format, UNKNOWN, UNKNOWN));
            }
            if (record.mResponse != null && record.mRequest != null) {
                Request request = record.mRequest;
                Response response = record.mResponse;
                method.setText(String.format(METHOD_FORMAT, request.method, response.mimeType));
                code.setText(String.format(CODE_FORMAT, response.status));
            } else {
                code.setText(UNKNOWN);
                method.setText(UNKNOWN);
            }

            platform.setText(String.format("platform: %s", record.mPlatform));

            flow.setText(String.format(FLOW_FORMAT, ByteUtil.getPrintSize(record.requestLength), ByteUtil.getPrintSize(record.responseLength)));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onClick(record);
                    }
                }
            });
        }
    }

    private Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String charString = constraint.toString();
            List<NetworkRecord> filteredList = new ArrayList<>();
            if (charString.isEmpty()) {
                filteredList = mSourceList;
            } else {
                for (NetworkRecord record : mSourceList) {
                    //这里根据需求，添加匹配规则
                    if (record.filter(charString)) {
                        filteredList.add(record);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<NetworkRecord> filteredList = (List<NetworkRecord>) results.values;
            if (filteredList == null || filteredList.size() == 0) {
                clear();
            } else {
                NetworkListAdapter.super.setData(filteredList);
            }
            notifyDataSetChanged();
        }
    };

    @Override
    public void setData(Collection<NetworkRecord> items) {
        mSourceList.clear();
        mSourceList.addAll(items);
        super.setData(items);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onClick(NetworkRecord info);
    }
}

