package com.didichuxing.doraemonkit.kit.network.ui;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.didichuxing.doraemonkit.DoKitEnv;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.loginfo.LogExportDialog;
import com.didichuxing.doraemonkit.kit.network.bean.NetworkRecord;
import com.didichuxing.doraemonkit.kit.network.bean.Request;
import com.didichuxing.doraemonkit.kit.network.bean.Response;
import com.didichuxing.doraemonkit.kit.network.utils.ByteUtil;
import com.didichuxing.doraemonkit.util.AppUtils;
import com.didichuxing.doraemonkit.util.DoKitFileUtil;
import com.didichuxing.doraemonkit.util.FileIOUtils;
import com.didichuxing.doraemonkit.util.FileUtils;
import com.didichuxing.doraemonkit.util.PathUtils;
import com.didichuxing.doraemonkit.util.ThreadUtils;
import com.didichuxing.doraemonkit.util.TimeUtils;
import com.didichuxing.doraemonkit.util.ToastUtils;
import com.didichuxing.doraemonkit.widget.dialog.DialogProvider;
import com.didichuxing.doraemonkit.widget.dialog.UniversalDialogFragment;
import com.didichuxing.doraemonkit.widget.jsonviewer.JsonRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    /**
     * 针对响应体 格式化
     */
    private TextView diverFormat;

    /**
     * 针对响应体 导出
     */
    private TextView diverExport;

    private JsonRecyclerView jsonView;

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
        diverFormat = findViewById(R.id.diver_format);
        diverExport = findViewById(R.id.diver_export);
        jsonView = findViewById(R.id.json_body);

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
        diverFormat.setVisibility(View.GONE);
        diverExport.setVisibility(View.GONE);
        jsonView.setVisibility(View.GONE);
        body.setVisibility(View.VISIBLE);
        if (record.mRequest != null) {
            Request request = record.mRequest;
            url.setText(request.url);
            method.setText(request.method);
            try {
                header.setText(URLDecoder.decode(request.headers, "utf-8"));
            } catch (Exception e) {
                header.setText(request.headers);
            }
            time.setText(mDateFormat.format(new Date(record.startTime)));
            size.setText(ByteUtil.getPrintSize(record.requestLength));
            try {
                String strBody = TextUtils.isEmpty(request.postData) ? "NULL" : request.postData;
                strBody = URLDecoder.decode(strBody, "utf-8");
                body.setText(strBody);
            } catch (Exception e) {
                body.setText(TextUtils.isEmpty(request.postData) ? "NULL" : request.postData);
            }
        }
    }

    public void bindResponse(final NetworkRecord record) {
        diverTime.setText(R.string.dk_network_detail_title_response_time);
        diverHeader.setText(R.string.dk_network_detail_title_response_header);
        diverBody.setText(R.string.dk_network_detail_title_response_body);
        diverExport.setVisibility(View.VISIBLE);
        diverExport.setText("导出");
        diverFormat.setVisibility(View.VISIBLE);
        diverFormat.setText("unFormat");
        jsonView.setVisibility(View.VISIBLE);
        jsonView.setTextSize(16.0f);
        jsonView.setScaleEnable(false);
        body.setVisibility(View.GONE);
        //响应体格式化
        diverFormat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (body.getVisibility() == View.VISIBLE) {
                    //格式化
                    String strBody = TextUtils.isEmpty(record.mResponseBody) ? "NULL" : record.mResponseBody;
                    try {
                        new JSONObject(strBody);
                        jsonView.setVisibility(View.VISIBLE);
                        body.setVisibility(View.GONE);
                        diverFormat.setText("unFormat");
                    } catch (JSONException e) {
                        jsonView.setVisibility(View.GONE);
                        body.setVisibility(View.VISIBLE);
                        body.setText(strBody);
                        diverFormat.setText("format");
                        ToastUtils.showShort("format error");
                    }
                } else {
                    //反格式化
                    String strBody = TextUtils.isEmpty(record.mResponseBody) ? "NULL" : record.mResponseBody;
                    body.setText(strBody);
                    diverFormat.setText("format");
                    jsonView.setVisibility(View.GONE);
                    body.setVisibility(View.VISIBLE);
                }
            }
        });
        //响应体导出
        diverExport.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //格式化
                final String strBody = TextUtils.isEmpty(record.mResponseBody) ? "NULL" : record.mResponseBody;
                if (strBody.equals("NULL")) {
                    ToastUtils.showShort("暂无响应体可以导出");
                    return;
                }

                LogExportDialog logExportDialog = new LogExportDialog(new Object(), null);
                logExportDialog.setOnButtonClickListener(new LogExportDialog.OnButtonClickListener() {
                    @Override
                    public void onSaveClick(LogExportDialog dialog) {
                        export2File(100, strBody);
                        dialog.dismiss();

                    }

                    @Override
                    public void onShareClick(LogExportDialog dialog) {
                        export2File(101, strBody);
                        dialog.dismiss();

                    }
                });
                showDialog(logExportDialog);


            }
        });

        if (record.mResponse != null) {
            Response response = record.mResponse;
            Request request = record.mRequest;
            url.setText(response.url);
            method.setText(request.method);
            header.setText(response.headers);
            time.setText(mDateFormat.format(new Date(record.endTime)));
            size.setText(ByteUtil.getPrintSize(record.responseLength));
            String strBody = TextUtils.isEmpty(record.mResponseBody) ? "NULL" : record.mResponseBody;
            try {
                new JSONObject(strBody);
                jsonView.bindJson(strBody);
            } catch (JSONException e) {
                e.printStackTrace();
                body.setVisibility(View.VISIBLE);
                jsonView.setVisibility(View.GONE);
                diverFormat.setText("format");
                body.setText(strBody);
            }

        }
    }


    /**
     * 将响应体保存到文件
     *
     * @param operateType  100 保存到本地  101 保存到本地并分享
     * @param responseBody 响应体
     */
    private void export2File(final int operateType, final String responseBody) {
        ToastUtils.showShort("日志保存中,请稍后...");
        final String logPath = PathUtils.getInternalAppFilesPath() + File.separator + AppUtils.getAppName() + "_response_" + TimeUtils.getNowString(new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss")) + ".txt";
        final File logFile = new File(logPath);

        ThreadUtils.executeByCpu(new ThreadUtils.Task<Boolean>() {
            @Override
            public Boolean doInBackground() throws Throwable {
                try {
                    FileIOUtils.writeFileFromString(logFile, responseBody, true);

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    ToastUtils.showShort("文件保存在:" + logPath);
                    //分享
                    if (operateType == 101) {
                        DoKitFileUtil.systemShare(DoKitEnv.requireApp(), logFile);
                    }
                }
            }

            @Override
            public void onCancel() {
                if (logFile.exists()) {
                    FileUtils.delete(logFile);
                }
                ToastUtils.showShort("日志保存失败");
            }

            @Override
            public void onFail(Throwable t) {
                if (logFile.exists()) {
                    FileUtils.delete(logFile);
                }
                ToastUtils.showShort("日志保存失败");
            }
        });

    }

    private void showDialog(DialogProvider provider) {
        if (getContext() == null || !(getContext() instanceof Activity)) {
            return;
        }

        Activity activity = (Activity) getContext();

        UniversalDialogFragment dialog = new UniversalDialogFragment();
        provider.setHost(dialog);
        dialog.setProvider(provider);
        provider.show(((FragmentActivity) activity).getSupportFragmentManager());
    }
}
