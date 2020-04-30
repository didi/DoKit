package com.didichuxing.doraemonkit.widget.bottomview;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.didichuxing.doraemonkit.kit.fileexplorer.SpBean;


public class EditSpInputView extends AssociationView {
    private final EditText editText;
    private SpBean spBean;

    public EditSpInputView(Context context, SpBean spBean, int inputType) {
        this.spBean = spBean;
        editText = new EditText(context);
        editText.setText(spBean.value.toString());
        editText.setInputType(inputType | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        editText.setSelection(spBean.value.toString().length());
    }

    @Override
    public Object submit() {
        return spBean.toDefaultClass(editText.getText().toString());
    }

    @Override
    public void cancel() {

    }

    @Override
    public View getView() {
        return editText;
    }

    @Override
    public boolean isCanSubmit() {
        return true;
    }

    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {

    }
}
