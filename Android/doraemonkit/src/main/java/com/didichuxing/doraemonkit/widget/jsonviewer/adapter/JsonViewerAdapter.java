package com.didichuxing.doraemonkit.widget.jsonviewer.adapter;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;


import androidx.recyclerview.widget.RecyclerView;

import com.didichuxing.doraemonkit.widget.jsonviewer.utils.Utils;
import com.didichuxing.doraemonkit.widget.jsonviewer.view.JsonItemView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by yuyuhang on 2017/11/29.
 */
public class JsonViewerAdapter extends BaseJsonViewerAdapter<JsonViewerAdapter.JsonItemViewHolder> {

    private String jsonStr;

    private JSONObject mJSONObject;
    private JSONArray mJSONArray;

    public JsonViewerAdapter(String jsonStr) {
        this.jsonStr = jsonStr;

        Object object = null;
        try {
            object = new JSONTokener(jsonStr).nextValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (object != null && object instanceof JSONObject) {
            mJSONObject = (JSONObject) object;
        } else if (object != null && object instanceof JSONArray) {
            mJSONArray = (JSONArray) object;
        } else {
            throw new IllegalArgumentException("jsonStr is illegal.");
        }
    }

    public JsonViewerAdapter(JSONObject jsonObject) {
        this.mJSONObject = jsonObject;
        if (mJSONObject == null) {
            throw new IllegalArgumentException("jsonObject can not be null.");
        }
    }

    public JsonViewerAdapter(JSONArray jsonArray) {
        this.mJSONArray = jsonArray;
        if (mJSONArray == null) {
            throw new IllegalArgumentException("jsonArray can not be null.");
        }
    }

    @Override
    public JsonItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new JsonItemViewHolder(new JsonItemView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(JsonItemViewHolder holder, int position) {
        JsonItemView itemView = holder.itemView;
        itemView.setTextSize(TEXT_SIZE_DP);
        itemView.setRightColor(BRACES_COLOR);
        if (mJSONObject != null) {
            if (position == 0) {
                itemView.hideLeft();
                itemView.hideIcon();
                itemView.showRight("{");
                return;
            } else if (position == getItemCount() - 1) {
                itemView.hideLeft();
                itemView.hideIcon();
                itemView.showRight("}");
                return;
            } else if (mJSONObject.names() == null) {
                return;
            }
            // 遍历key
            String key = mJSONObject.names().optString(position - 1);
            Object value = mJSONObject.opt(key);
            if (position < getItemCount() - 2) {
                handleJsonObject(key, value, itemView, true, 1);
            } else {
                handleJsonObject(key, value, itemView, false, 1); // 最后一组，结尾不需要逗号
            }
        }

        if (mJSONArray != null) {
            if (position == 0) {
                itemView.hideLeft();
                itemView.hideIcon();
                itemView.showRight("[");
                return;
            } else if (position == getItemCount() - 1) {
                itemView.hideLeft();
                itemView.hideIcon();
                itemView.showRight("]");
                return;
            }

            Object value = mJSONArray.opt(position - 1); // 遍历array
            if (position < getItemCount() - 2) {
                handleJsonArray(value, itemView, true, 1);
            } else {
                handleJsonArray(value, itemView, false, 1); // 最后一组，结尾不需要逗号
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mJSONObject != null) {
            if (mJSONObject.names() != null) {
                return mJSONObject.names().length() + 2;
            } else {
                return 2;
            }
        }
        if (mJSONArray != null) {
            return mJSONArray.length() + 2;
        }
        return 0;
    }

    /**
     * 处理 value 上级为 JsonObject 的情况，value有key
     *
     * @param value
     * @param key
     * @param itemView
     * @param appendComma
     * @param hierarchy
     */
    private void handleJsonObject(String key, Object value, JsonItemView itemView, boolean appendComma, int hierarchy) {
        SpannableStringBuilder keyBuilder = new SpannableStringBuilder(Utils.getHierarchyStr(hierarchy));
        keyBuilder.append("\"").append(key).append("\"").append(":");
        keyBuilder.setSpan(new ForegroundColorSpan(KEY_COLOR), 0, keyBuilder.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        keyBuilder.setSpan(new ForegroundColorSpan(BRACES_COLOR), keyBuilder.length() - 1, keyBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        itemView.showLeft(keyBuilder);

        handleValue(value, itemView, appendComma, hierarchy);
    }

    /**
     * 处理 value 上级为 JsonArray 的情况，value无key
     *
     * @param value
     * @param itemView
     * @param appendComma 结尾是否需要逗号(最后一组 value 不需要逗号)
     * @param hierarchy   缩进层级
     */
    private void handleJsonArray(Object value, JsonItemView itemView, boolean appendComma, int hierarchy) {
        itemView.showLeft(new SpannableStringBuilder(Utils.getHierarchyStr(hierarchy)));

        handleValue(value, itemView, appendComma, hierarchy);
    }

    /**
     * @param value
     * @param itemView
     * @param appendComma 结尾是否需要逗号(最后一组 key:value 不需要逗号)
     * @param hierarchy   缩进层级
     */
    private void handleValue(Object value, JsonItemView itemView, boolean appendComma, int hierarchy) {
        SpannableStringBuilder valueBuilder = new SpannableStringBuilder();
        if (value instanceof Number) {
            valueBuilder.append(value.toString());
            valueBuilder.setSpan(new ForegroundColorSpan(NUMBER_COLOR), 0, valueBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (value instanceof Boolean) {
            valueBuilder.append(value.toString());
            valueBuilder.setSpan(new ForegroundColorSpan(BOOLEAN_COLOR), 0, valueBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (value instanceof JSONObject) {
            itemView.showIcon(true);
            valueBuilder.append("Object{...}");
            valueBuilder.setSpan(new ForegroundColorSpan(BRACES_COLOR), 0, valueBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            itemView.setIconClickListener(new JsonItemClickListener(value, itemView, appendComma, hierarchy + 1));
        } else if (value instanceof JSONArray) {
            itemView.showIcon(true);
            valueBuilder.append("Array[").append(String.valueOf(((JSONArray) value).length())).append("]");
            int len = valueBuilder.length();
            valueBuilder.setSpan(new ForegroundColorSpan(BRACES_COLOR), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            valueBuilder.setSpan(new ForegroundColorSpan(NUMBER_COLOR), 6, len - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            valueBuilder.setSpan(new ForegroundColorSpan(BRACES_COLOR), len - 1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            itemView.setIconClickListener(new JsonItemClickListener(value, itemView, appendComma, hierarchy + 1));
        } else if (value instanceof String) {
            itemView.hideIcon();
            valueBuilder.append("\"").append(value.toString()).append("\"");
            if (Utils.isUrl(value.toString())) {
                valueBuilder.setSpan(new ForegroundColorSpan(TEXT_COLOR), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                valueBuilder.setSpan(new ForegroundColorSpan(URL_COLOR), 1, valueBuilder.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                valueBuilder.setSpan(new ForegroundColorSpan(TEXT_COLOR), valueBuilder.length() - 1, valueBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                valueBuilder.setSpan(new ForegroundColorSpan(TEXT_COLOR), 0, valueBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else if (valueBuilder.length() == 0 || value == null) {
            itemView.hideIcon();
            valueBuilder.append("null");
            valueBuilder.setSpan(new ForegroundColorSpan(NULL_COLOR), 0, valueBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (appendComma) {
            valueBuilder.append(",");
        }

        itemView.showRight(valueBuilder);
    }

    class JsonItemClickListener implements View.OnClickListener {

        private Object value;
        private JsonItemView itemView;
        private boolean appendComma;
        private int hierarchy;

        private boolean isCollapsed = true;
        private boolean isJsonArray;

        JsonItemClickListener(Object value, JsonItemView itemView, boolean appendComma, int hierarchy) {
            this.value = value;
            this.itemView = itemView;
            this.appendComma = appendComma;
            this.hierarchy = hierarchy;
            this.isJsonArray = value != null && value instanceof JSONArray;
        }

        @Override
        public void onClick(View view) {
            if (itemView.getChildCount() == 1) { // 初始（折叠） --> 展开""
                isCollapsed = false;
                itemView.showIcon(false);
                itemView.setTag(itemView.getRightText());
                itemView.showRight(isJsonArray ? "[" : "{");
                JSONArray array = isJsonArray ? (JSONArray) value : ((JSONObject) value).names();
                for (int i = 0; array != null && i < array.length(); i++) {
                    JsonItemView childItemView = new JsonItemView(itemView.getContext());
                    childItemView.setTextSize(TEXT_SIZE_DP);
                    childItemView.setRightColor(BRACES_COLOR);
                    Object childValue = array.opt(i);
                    if (isJsonArray) {
                        handleJsonArray(childValue, childItemView, i < array.length() - 1, hierarchy);
                    } else {
                        handleJsonObject((String) childValue, ((JSONObject) value).opt((String) childValue), childItemView, i < array.length() - 1, hierarchy);
                    }
                    itemView.addViewNoInvalidate(childItemView);
                }

                JsonItemView childItemView = new JsonItemView(itemView.getContext());
                childItemView.setTextSize(TEXT_SIZE_DP);
                childItemView.setRightColor(BRACES_COLOR);
                StringBuilder builder = new StringBuilder(Utils.getHierarchyStr(hierarchy - 1));
                builder.append(isJsonArray ? "]" : "}").append(appendComma ? "," : "");
                childItemView.showRight(builder);
                itemView.addViewNoInvalidate(childItemView);
                itemView.requestLayout();
                itemView.invalidate();
            } else {                            // 折叠 <--> 展开
                CharSequence temp = itemView.getRightText();
                itemView.showRight((CharSequence) itemView.getTag());
                itemView.setTag(temp);
                itemView.showIcon(!isCollapsed);
                for (int i = 1; i < itemView.getChildCount(); i++) {
                    itemView.getChildAt(i).setVisibility(isCollapsed ? View.VISIBLE : View.GONE);
                }
                isCollapsed = !isCollapsed;
            }
        }
    }

    class JsonItemViewHolder extends RecyclerView.ViewHolder {

        JsonItemView itemView;

        JsonItemViewHolder(JsonItemView itemView) {
            super(itemView);
            setIsRecyclable(false);
            this.itemView = itemView;
        }
    }
}
