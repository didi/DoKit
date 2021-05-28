package com.didichuxing.doraemonkit.kit.fileexplorer;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.widget.tableview.TableConfig;
import com.didichuxing.doraemonkit.widget.tableview.bean.ArrayTableData;
import com.didichuxing.doraemonkit.widget.tableview.format.FastTextDrawFormat;
import com.didichuxing.doraemonkit.widget.tableview.style.FontStyle;
import com.didichuxing.doraemonkit.widget.tableview.component.SmartTable;
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar;
import com.didichuxing.doraemonkit.util.DatabaseUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseDetailFragment extends BaseFragment {

    private SmartTable table;
    private ListView tableListView;

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_db_detail;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle data = getArguments();
        SQLiteDatabase sqLiteDatabase = null;
        List<String> tableNames = new ArrayList<>();
        if (data != null) {
            File mFile = (File) data.getSerializable(BundleKey.FILE_KEY);
            String path = mFile.getPath();
            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(path, null);
            tableNames = DatabaseUtil.queryTableName(sqLiteDatabase);
        }
        table = findViewById(R.id.table);
        FontStyle fontStyle = new FontStyle(getContext(), 15, ContextCompat.getColor(getContext(), R.color.dk_color_000000));
        TableConfig.getInstance().setVerticalPadding(10).setHorizontalPadding(10);
        TableConfig.getInstance().columnTitleStyle = fontStyle;
        table.setZoom(true, 2f, 0.4f);


        tableListView = findViewById(R.id.lv_table_name);
        tableListView.setAdapter(new DBListAdapter(getContext(), tableNames));
        final List<String> finalStrings = tableNames;
        final SQLiteDatabase finalSqLiteDatabase = sqLiteDatabase;
        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }

            @Override
            public void onRightClick() {

            }
        });
        tableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectTableName = finalStrings.get(position);

                String[][] data = DatabaseUtil.queryAll(finalSqLiteDatabase, finalStrings.get(position));
                String[] titleName = DatabaseUtil.queryTableColumnName(finalSqLiteDatabase, selectTableName);
                if (table.getTableData() != null) {
                    table.getTableData().clear();
                }
                table.setTableData(ArrayTableData.create(selectTableName, titleName, data, new FastTextDrawFormat<String>()));
                table.getMatrixHelper().reset();
                tableListView.setVisibility(View.GONE);
                table.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        if (table.getVisibility() == View.VISIBLE) {
            table.setVisibility(View.GONE);
            tableListView.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
        return true;
    }

}