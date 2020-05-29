package com.didichuxing.doraemonkit.kit.fileexplorer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.SpInputType;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsRecyclerAdapter;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsViewBinder;

public class SpAdapter extends AbsRecyclerAdapter<AbsViewBinder<SpBean>, SpBean> {

    public SpAdapter(Context context) {
        super(context);
    }

    @Override
    protected AbsViewBinder<SpBean> createViewHolder(View view, int viewType) {
        return new ViewHolder(view);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return inflater.inflate(R.layout.dk_item_sp_input, parent, false);
    }

    private class ViewHolder extends AbsViewBinder<SpBean> {

        private TextView key;
        private TextView type;
        private SpInputView inputView;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        protected void getViews() {
            key = getView(R.id.tv_sp_key);
            type = getView(R.id.tv_sp_type);
            inputView = getView(R.id.input_sp_value);

        }

        @Override
        public void bind(final SpBean spBean) {
            if (!spBean.value.getClass().getSimpleName().equals(SpInputType.HASHSET)) {
                key.setText(spBean.key);
                type.setText(spBean.value.getClass().getSimpleName());
                inputView.setInput(spBean, new SpInputView.OnDataChangeListener() {
                    @Override
                    public void onDataChanged() {
                        inputView.refresh();
                        if (onSpDataChangerListener != null) {
                            onSpDataChangerListener.onDataChanged(spBean);
                        }
                    }
                });
            }
        }
    }

    private OnSpDataChangerListener onSpDataChangerListener;

    public void setOnSpDataChangerListener(OnSpDataChangerListener onSpDataChangerListener) {
        this.onSpDataChangerListener = onSpDataChangerListener;
    }

    public interface OnSpDataChangerListener {
        void onDataChanged(SpBean spBean);
    }

}
