package com.didichuxing.doraemonkit.kit.network.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.network.bean.NetworkRecord;
import com.didichuxing.doraemonkit.kit.network.bean.Request;
import com.didichuxing.doraemonkit.kit.network.bean.Response;
import com.didichuxing.doraemonkit.kit.network.utils.ByteUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * @desc: 显示request和response的view
 */
public class NetworkDetailView extends LinearLayout {
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS");

    private TextView url;
    private TextView method;
    private TextView size;
    private TextView header;
    private TextView body;
    private TextView time;

    private TextView diverTime;
    private TextView diverHeader;
    private TextView diverBody;

    private ClipboardManager mClipboard;

    public NetworkDetailView(final Context context) {
        super(context);
        inflate(context, R.layout.dk_view_network_request, this);
        mClipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);

        url = findViewById(R.id.tv_url);
        method = findViewById(R.id.tv_method);
        size = findViewById(R.id.tv_data_size);
        header = findViewById(R.id.tv_header);
        body = findViewById(R.id.tv_body);
        time = findViewById(R.id.tv_time);
        diverTime = findViewById(R.id.diver_time);
        diverHeader = findViewById(R.id.diver_header);
        diverBody = findViewById(R.id.diver_body);
        body.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData clipData = ClipData.newPlainText("Label", body.getText());
                mClipboard.setPrimaryClip(clipData);
                Toast.makeText(getContext(), "copy success", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    public NetworkDetailView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void bindRequest(NetworkRecord record) {
        diverTime.setText(R.string.dk_network_detail_title_request_time);
        diverHeader.setText(R.string.dk_network_detail_title_request_header);
        diverBody.setText(R.string.dk_network_detail_title_request_body);
        if (record.mRequest != null) {
            Request request = record.mRequest;
            url.setText(request.url);
            method.setText(request.method);
            header.setText(request.headers);
            time.setText(mDateFormat.format(new Date(record.startTime)));
            size.setText(ByteUtil.getPrintSize(record.requestLength));
            body.setText(TextUtils.isEmpty(request.postData) ? "NULL" : request.postData);
        }
    }

    public void bindResponse(NetworkRecord record) {
        diverTime.setText(R.string.dk_network_detail_title_response_time);
        diverHeader.setText(R.string.dk_network_detail_title_response_header);
        diverBody.setText(R.string.dk_network_detail_title_response_body);
        if (record.mResponse != null) {
            Response response = record.mResponse;
            Request request = record.mRequest;
            url.setText(response.url);
            method.setText(request.method);
            header.setText(response.headers);
            time.setText(mDateFormat.format(new Date(record.endTime)));
            size.setText(ByteUtil.getPrintSize(record.responseLength));
            body.setText(TextUtils.isEmpty(record.mResponseBody) ? "NULL" : record.mResponseBody);
        }
    }
}
