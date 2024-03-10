package com.didichuxing.doraemonkit.gps_mock.widget;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.mapapi.search.sug.SuggestionResult;
import com.didichuxing.doraemonkit.gps_mock.R;

import java.util.List;

public class PositionSelectRecyclerAdapter extends RecyclerView.Adapter<PositionSelectRecyclerAdapter.PositionItemHolder> {

    private final List<SuggestionResult.SuggestionInfo> mData;
    private IPositionItemSelectedCallback mItemSelectedCallback;

    public PositionSelectRecyclerAdapter(List<SuggestionResult.SuggestionInfo> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public PositionItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dk_item_position_select, parent, false);

        return new PositionItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PositionItemHolder holder, final int position) {
        holder.bindView(mData.get(holder.getAdapterPosition()));
        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemSelectedCallback != null) {
                    mItemSelectedCallback.onItemSelect(mData.get(holder.getAdapterPosition()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class PositionItemHolder extends RecyclerView.ViewHolder {
        private final View mItemView;
        private final TextView mTvAddressName;
        private final TextView mTvAddressDetail;


        public PositionItemHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mTvAddressName = itemView.findViewById(R.id.tv_address_name);
            mTvAddressDetail = itemView.findViewById(R.id.tv_address_detail);
        }

        public void bindView(SuggestionResult.SuggestionInfo suggestionInfo) {
            if (suggestionInfo != null) {
                mTvAddressName.setText(suggestionInfo.key);
                mTvAddressDetail.setText(TextUtils.isEmpty(suggestionInfo.address) ? (suggestionInfo.city + suggestionInfo.district + suggestionInfo.key) : suggestionInfo.address);
            }

        }
    }

    public void setItemSelectedCallback(IPositionItemSelectedCallback mItemSelectedCallback) {
        this.mItemSelectedCallback = mItemSelectedCallback;
    }

    public interface IPositionItemSelectedCallback {
        void onItemSelect(SuggestionResult.SuggestionInfo suggestionInfo);
    }
}
