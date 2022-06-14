package com.didichuxing.doraemonkit.kit.permissionlist;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * Created by lmh 2022/6/14
 */
public class ExpandableActivity extends AppCompatActivity {
    private HashMap<String,MyPermissionInfo> permissonmap;  //获取预处理的所有权限map
    private ArrayList<MyPermissionInfo> PermissionInfoList; //获取预处理的所有权限list
    public  ArrayList<MyPermissionInfo> normalList;
    public  ArrayList<MyPermissionInfo> signatureList;
    public  ArrayList<MyPermissionInfo> dangerousList;
    public  ArrayList<MyPermissionInfo> notclassfied;

    private RecyclerView rvList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隐藏标题栏
        //获取分组的信息
        normalList=new ArrayList<>();
        signatureList=new ArrayList<>();
        dangerousList=new ArrayList<>();
        notclassfied=new ArrayList<>();
        XMLparser Infoparser=new XMLparser(); //分组预处理，取得是安卓9的系统源码xml文件
        InputStream path = null;
        try {
            path = getResources().getAssets().open("AndroidManifest_meta.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        PermissionInfoList =Infoparser.parser(path);
        permissonmap=Infoparser.getMap();
        try {
            getUsesPermission(this.getPackageName()); //对包权限进行分类
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //设置adapter
        setContentView(R.layout.dk_permission_list_activity_group_list);
        HomeTitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setListener(() -> finish());
        rvList = (RecyclerView) findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<ArrayList<MyPermissionInfo>>grouplist=new ArrayList<>();
        grouplist.add(normalList);
        grouplist.add(signatureList);
        grouplist.add(dangerousList);
        grouplist.add(notclassfied);
        ExpandableAdapter adapter = new ExpandableAdapter(this, GroupModel.getExpandableGroups(PermissionInfoList,
            grouplist));
        adapter.setOnHeaderClickListener(new GroupedRecyclerViewAdapter.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder,
                                      int groupPosition) {
                ExpandableAdapter expandableAdapter = (ExpandableAdapter) adapter;
                if (expandableAdapter.isExpand(groupPosition)) {
                    expandableAdapter.collapseGroup(groupPosition);
                } else {
                    expandableAdapter.expandGroup(groupPosition);
                }
            }
        });
        adapter.setOnChildClickListener(new GroupedRecyclerViewAdapter.OnChildClickListener() {
            @Override
            public void onChildClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder,
                                     int groupPosition, int childPosition) {
//                Toast.makeText(ExpandableActivity.this, "子项：groupPosition = " + groupPosition+ ", childPosition = " + childPosition,Toast.LENGTH_LONG).show();
                if (!holder.isExpanded()) { //如果当前是缩小的
                    holder.setBackgroundColor(R.id.all, Color.BLACK);
                    holder.setExpanded(true);
                    holder.setTextColor(R.id.tv_child,Color.WHITE);
                    if(holder.getText(R.id.p_group)!="notfound"){
                        holder.setVisible(R.id.p_group,true);
                        holder.setTextColor(R.id.p_group,Color.WHITE);
                    }
                    holder.setVisible(R.id.p_label,true);
                    holder.setTextColor(R.id.p_label,Color.WHITE);
                    holder.setVisible(R.id.p_description,true);
                    holder.setTextColor(R.id.p_description,Color.WHITE);
                } else {
                    holder.setTextColor(R.id.tv_child,Color.BLACK);
                    holder.setBackgroundColor(R.id.all,Color.WHITE);
                    holder.setExpanded(false);
                    holder.setVisible(R.id.p_group,false);
                    holder.setVisible(R.id.p_label,false);
                    holder.setVisible(R.id.p_description,false);
                }

            }
        });
        rvList.setAdapter(adapter);

    }

    public static void openActivity(Context context) {
        Intent intent = new Intent(context, ExpandableActivity.class);
        context.startActivity(intent);
    }
    private void getUsesPermission(String packageName) throws PackageManager.NameNotFoundException {
        PackageManager packageManager=this.getPackageManager();
        PackageInfo packageInfo=packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
        String [] usesPermissionsArray=packageInfo.requestedPermissions;
        System.out.println(usesPermissionsArray.length);

        for (int i = 0; i < usesPermissionsArray.length; i++) {
            try {
                //得到每个权限的名字,如:android.permission.INTERNET
                String usesPermissionName=usesPermissionsArray[i];
                MyPermissionInfo p= permissonmap.get(usesPermissionName);

                //通过usesPermissionName获取该权限的详细信息
                PermissionInfo permissionInfo=packageManager.getPermissionInfo(usesPermissionName, 0);
                //获取该权限的标签信息,比如:完全的网络访问权限
                String permissionLabel=permissionInfo.loadLabel(packageManager).toString();
                //获取该权限的详细描述信息,比如:允许该应用创建网络套接字和使用自定义网络协议
                //浏览器和其他某些应用提供了向互联网发送数据的途径,因此应用无需该权限即可向互联网发送数据.
                String permissionDescription=permissionInfo.loadDescription(packageManager).toString();
                p.setDescription(permissionDescription);
                p.setLabel(permissionLabel);

                if(checkname(p.getProtectionLevel(),"normal")){
                    normalList.add(p);
                }
                else if(checkname(p.getProtectionLevel(),"signature ")){
                    signatureList.add(p);
                }
                else {
                    dangerousList.add(p);
                }
            } catch (Exception e) {
                // TODO: handle exception
                String invalidname=usesPermissionsArray[i];
                System.out.println(invalidname+"is not found in package");
                if(permissonmap.containsKey(invalidname)){
                    MyPermissionInfo p=permissonmap.get(invalidname);
                    if(checkname(p.getProtectionLevel(),"normal")){
                        normalList.add(p);
                    }
                    else if(checkname(p.getProtectionLevel(),"signature")){
                        signatureList.add(p);
                    }
                    else {
                        dangerousList.add(p);
                    }
                }
                else{
                    MyPermissionInfo p=new MyPermissionInfo(invalidname,"","","");
                    notclassfied.add(p);
                }
            }
        }
    }
    public boolean checkname(String name,String element){
        if(name.indexOf(element)>-1){
            return true;
        }
        else {
            return false;
        }
    }

}
