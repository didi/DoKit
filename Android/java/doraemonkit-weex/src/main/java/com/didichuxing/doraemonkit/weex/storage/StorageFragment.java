package com.didichuxing.doraemonkit.weex.storage;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.widget.recyclerview.DividerItemDecoration;
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar;
import com.didichuxing.doraemonkit.weex.R;

import java.util.List;

/**
 * @author haojianglong
 * @date 2019-06-18
 */
public class StorageFragment extends BaseFragment {

    private StorageHacker mStorageHacker;

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_storage;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        mStorageHacker = new StorageHacker(getContext(), true);

        HomeTitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setListener(new HomeTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                getActivity().finish();
            }
        });
        RecyclerView recycler = findViewById(R.id.info_list);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recycler.addItemDecoration(new DividerItemDecoration(DividerItemDecoration.VERTICAL));
        final StorageAdapter storageItemAdapter = new StorageAdapter(getActivity());
        mStorageHacker.fetch(new StorageHacker.OnLoadListener() {
            @Override
            public void onLoad(List<StorageInfo> list) {
                storageItemAdapter.append(list);
            }
        });

        storageItemAdapter.setOnItemClickListener(new StorageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(StorageInfo info) {
                StorageDialogFragment fragment = new StorageDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(StorageDialogFragment.KEY_STORAGE_INFO, info);
                fragment.setArguments(bundle);
                fragment.show(getFragmentManager(), "dialog");
            }
        });
        recycler.setAdapter(storageItemAdapter);
    }

}
