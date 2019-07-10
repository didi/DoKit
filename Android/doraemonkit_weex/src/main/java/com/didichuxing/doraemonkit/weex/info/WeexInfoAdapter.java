package com.didichuxing.doraemonkit.weex.info;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.didichuxing.doraemonkit.weex.R;

import java.util.List;

/**
 * @author haojianglong
 * @date 2019-06-25
 */
public class WeexInfoAdapter extends BaseAdapter {

    private Context mContext;

    private List<WeexInfo> mWeexInfos;

    public void setWeexInfos(List<WeexInfo> infos) {
        this.mWeexInfos = infos;
    }

    public WeexInfoAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mWeexInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mWeexInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.dk_fragment_info_item, null);
        }
        ViewHolder viewHolder;
        if (convertView.getTag() == null) {
            viewHolder = new ViewHolder();
            viewHolder.keyText = convertView.findViewById(R.id.info_item_key);
            viewHolder.valueText = convertView.findViewById(R.id.info_item_value);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        setText(viewHolder.keyText, mWeexInfos.get(position).key);
        setText(viewHolder.valueText, mWeexInfos.get(position).value);
        convertView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) mContext.getResources().getDimension(R.dimen.dk_title_height)));
        return convertView;
    }

    private void setText(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        }
    }

    class ViewHolder {
        TextView keyText;
        TextView valueText;
    }

}
