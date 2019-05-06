package com.didichuxing.doraemonkit.kit.fileexplorer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.SpInputType;

import java.util.ArrayList;
import java.util.List;

public class SpInputView extends FrameLayout {

    private Spinner spinner;
    private EditText sp_input;
    private static final List<Boolean> selected = new ArrayList<Boolean>() {{
        add(true);
        add(false);
    }};

    public SpInputView(Context context) {
        super(context);
        init();
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
        spinner = inflate.findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, selected));
        sp_input = inflate.findViewById(R.id.sp_input);

    }

    private String currentStatue = "";

    public <T> void setInput(final T t, final OnDataChangeListener onDataChangeListener) {
        currentStatue = t.getClass().getSimpleName();
        switch (currentStatue) {
            case SpInputType.BOOLEAN:
                spinner.setSelection(selected.indexOf(t));
                spinner.setVisibility(VISIBLE);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        onDataChangeListener.onDataChanged(parent.getSelectedItem());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                sp_input.setVisibility(GONE);
                break;
            case SpInputType.INTEGER:
            case SpInputType.LONG:
            case SpInputType.FLOAT:
                sp_input.setInputType(InputType.TYPE_CLASS_NUMBER);
                initEdt(t, onDataChangeListener);
                break;
            case SpInputType.STRING:
                sp_input.setInputType(InputType.TYPE_CLASS_TEXT);
                initEdt(t, onDataChangeListener);

                break;
            default:
                break;
//            case HASHSET:
//                break;

        }
    }

    private <T> void initEdt(T t, final OnDataChangeListener onDataChangeListener) {
        sp_input.setText(t.toString());
        sp_input.setVisibility(VISIBLE);
        spinner.setVisibility(GONE);
        sp_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                onDataChangeListener.onDataChanged(s.toString());

            }
        });
    }

    public interface OnDataChangeListener<T> {
        void onDataChanged(T t);
    }

}

