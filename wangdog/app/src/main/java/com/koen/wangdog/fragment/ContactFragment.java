package com.koen.wangdog.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.koen.wangdog.R;
import com.koen.wangdog.adapter.UserFriendAdapter;
import com.koen.wangdog.bean.User;
import com.koen.wangdog.util.CharacterParser;
import com.koen.wangdog.util.PinyinComparator;
import com.koen.wangdog.view.ClearEditText;
import com.koen.wangdog.view.MyLetterView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/17.
 */
public class ContactFragment extends BaseFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    ClearEditText mClearEditText;

    TextView dialog;

    ListView list_friend;
    MyLetterView right_letter;

    private UserFriendAdapter userFriendAdapter;  // 好友

    List<User> friends = new ArrayList<User>();

    private InputMethodManager inputMethodManager;

    // 汉字转换成拼音的类
    private CharacterParser characterParser;

    // 根据拼音来排列ListView 里面的数据类
    private PinyinComparator pinyinComparator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        init();
    }

    private void init() {
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
    }

    private void initListView() {
        list_friend = (ListView) findViewById(R.id.list_friends);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }
}
