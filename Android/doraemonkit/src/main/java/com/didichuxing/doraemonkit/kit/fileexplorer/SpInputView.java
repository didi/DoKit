package com.didichuxing.doraemonkit.kit.fileexplorer;

import android.content.Context;
import androidx.annotation.Nullable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.SpInputType;
import com.didichuxing.doraemonkit.widget.bottomview.BottomUpWindow;
import com.didichuxing.doraemonkit.widget.bottomview.EditSpInputView;

public class SpInputView extends FrameLayout {

    private OnDataChangeListener onDataChangeListener;

    private static final int FLOAT = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
    private static final int INTEGER = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED;
    private static final int STRING = InputType.TYPE_CLASS_TEXT;

    private TextView spValue;
    private Switch switchBtn;
    private SpBean bean;


    public SpInputView(Context context) {
        super(context, null);
    }

    public SpInputView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public SpInputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.kd_item_sp_input, this, true);
        switchBtn = inflate.findViewById(R.id.switch_btn);
        spValue = inflate.findViewById(R.id.tv_sp_value);

    }

    public void setInput(final SpBean bean, final OnDataChangeListener onDataChangeListener) {
        this.bean = bean;
        this.onDataChangeListener = onDataChangeListener;
        switch (bean.value.getClass().getSimpleName()) {
            case SpInputType.BOOLEAN:
                switchBtn.setChecked((Boolean) bean.value);
                switchBtn.setVisibility(VISIBLE);
                switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        bean.value = isChecked;
                        onDataChangeListener.onDataChanged();
                    }
                });
                spValue.setVisibility(GONE);
                break;
            case SpInputType.INTEGER:
            case SpInputType.LONG:
                initEdt(bean, INTEGER);
                break;
            case SpInputType.FLOAT:
                initEdt(bean, FLOAT);
                break;
            case SpInputType.STRING:
                initEdt(bean, STRING);
                break;
            default:
                break;
//            case HASHSET:
//                break;

        }
    }

    private void initEdt(final SpBean spBean, final int inputType) {
        spValue.setVisibility(VISIBLE);
        switchBtn.setVisibility(GONE);
        spValue.setText(spBean.value.toString());
        spValue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputView(v, spBean, inputType);
            }
        });
    }

    public void refresh() {
        if (bean != null) {
            spValue.setText(bean.value.toString());
        }
    }

    private void showInputView(View view, final SpBean spBean, int inputType) {
        new BottomUpWindow(getContext()).setContent(new EditSpInputView(getContext(), spBean, inputType))
                .show(view).setOnSubmitListener(new BottomUpWindow.OnSubmitListener() {
            @Override
            public void submit(Object object) {
                spBean.value = object;
                if (onDataChangeListener != null) {
                    onDataChangeListener.onDataChanged();
                }
            }

            @Override
            public void cancel() {

            }
        });
    }

    public interface OnDataChangeListener {
        void onDataChanged();
    }

}

