package mhwang.com.takecareofmoney;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.sourceforge.pinyin4j.PinyinHelper;

import mhwang.com.abstracts.PagerFragment;
import mhwang.com.activity.FinaceListActivity;
import mhwang.com.activity.SearchActivity;

/**
 * 项目名称：
 * 类描述：
 * 作者：王明海
 * 创建时间：2016/4/7
 */
public class MoreFragment extends PagerFragment {
    private View mView;
    private Button btn_search;
    private Button btn_finance;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_more,null);
        initComponent();
        initEvent();
        return mView;
    }

    /**
     *  初始化控件
     */
    private void initComponent(){
        btn_search = (Button) mView.findViewById(R.id.btn_more_search);
        btn_finance = (Button) mView.findViewById(R.id.btn_more_finance);
    }

    private void initEvent(){
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        btn_finance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FinaceListActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void updateData() {
    }
}
