package com.didichuxing.doraemonkit.kit.network.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;

import java.util.List;



public class ListDropDownAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;
    private int checkItemPosition = 0;

    public List<String> getList() {
        return list;
    }

    public void setCheckItem(int position) {
        checkItemPosition = position;
        notifyDataSetChanged();
    }

    public ListDropDownAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.dk_item_default_drop_down, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        viewHolder.mText.setText(list.get(position));
        if (checkItemPosition != -1) {
            if (checkItemPosition == position) {
                viewHolder.mText.setTextColor(context.getResources().getColor(R.color.dk_drop_down_selected));
                viewHolder.mText.setBackgroundResource(R.color.dk_check_bg);
            } else {
                viewHolder.mText.setTextColor(context.getResources().getColor(R.color.dk_drop_down_unselected));
                viewHolder.mText.setBackgroundResource(R.color.dk_color_FFFFFF);
            }
        }
    }

    static class ViewHolder {
        TextView mText;

        ViewHolder(View contenView) {
            mText = contenView.findViewById(R.id.text);
        }
    }
}
