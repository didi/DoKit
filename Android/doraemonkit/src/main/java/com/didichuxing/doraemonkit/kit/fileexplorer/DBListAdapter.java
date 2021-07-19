package com.didichuxing.doraemonkit.kit.fileexplorer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;

import java.util.List;

public class DBListAdapter extends BaseAdapter {
    private List data;
    private Context context;

    public DBListAdapter(Context context, List data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.dk_item_file_info, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.setData((String) data.get(position));
        return convertView;
    }

    public class ViewHolder {
        public TextView textView;

        public ViewHolder(View convertView) {
            textView = (TextView) convertView.findViewById(R.id.name);
            convertView.findViewById(R.id.icon).setVisibility(View.GONE);

        }

        public void setData(String data) {
            textView.setText(data);
        }
    }
}
