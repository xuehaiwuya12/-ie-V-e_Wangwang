package com.koen.wangdog.fragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.koen.wangdog.view.ClearEditText;

/**
 * Created by Administrator on 2016/1/17.
 */
public class ContactFragment extends BaseFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    ClearEditText mClearEditText;

    TextView dialog;

    ListView list_friend;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }
}
