package mhwang.com.takecareofmoney;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import mhwang.com.abstracts.PagerFragment;
import mhwang.com.activity.RecordListActivity;
import mhwang.com.adapter.AccountFragmentAdapter;
import mhwang.com.bean.Account;
import mhwang.com.bean.Record;
import mhwang.com.database.DBUtil;
import mhwang.com.util.DateUtil;
import mhwang.com.util.NumberFormat;

/**
 * 项目名称：
 * 类描述：
 * 作者：王明海
 * 创建时间：2016/4/7
 */
public class AccountFragment extends PagerFragment{
    private View mView;
    private ArrayList<Account> accounts;
    private ListView mListView;

    private AccountFragmentAdapter adapter;
    private TextView tv_totalSurplusAsset;
    private TextView tv_totalNetAsset;
    private TextView tv_totalNegativeAsset;

    private double totalIncome ;
    private double totalOutcome ;
    private double totalSurplus ;
    private double negativeAsset ;
    private DBUtil dbUtil;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("AccountFragment", "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_account,null);
        initComponent();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("AccountFragment", "onResume");
        initData();
        initEvent();
        MainActivity.updateAdapter();
        showData();
    }

    /**
     *  初始化控件
     */
    private void initComponent() {
        mListView = (ListView)mView.findViewById(R.id.lv_account_list);
        tv_totalNegativeAsset = (TextView)mView.findViewById(R.id.tv_total_negative_asset);
        tv_totalNetAsset = (TextView)mView.findViewById(R.id.tv_account_net_asset);
        tv_totalSurplusAsset = (TextView)mView.findViewById(R.id.tv_account_total_surplus);
    }

    /**
     *  添加事件
     */
    private void initEvent(){
        adapter = new AccountFragmentAdapter(accounts,getActivity());
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), RecordListActivity.class);
                // 加4是因为前面0123在首页的今天、本周、本月、本年被占用
                intent.putExtra(RecordListFragment.KEY_WHICH_FRAGMENT,position+4);
                startActivity(intent);
            }
        });
    }

    /**
     *  显示数据
     */
    private void showData(){
        tv_totalNegativeAsset.setText(NumberFormat.format(negativeAsset));
        tv_totalSurplusAsset.setText(NumberFormat.format(totalIncome));
        tv_totalNetAsset.setText(NumberFormat.format(totalSurplus));
    }

    /**
     *  初始化数据
     */
    private void initData() {
        dbUtil = DBUtil.getInstance(getActivity());
        accounts = dbUtil.readAccounts();
        totalIncome = 0.00;
        totalOutcome = 0.00;
        totalSurplus = 0.00;
        negativeAsset = 0.00;
        getDataFromDB();
    }

    /**
     *  从数据库获取数据
     */
    private void getDataFromDB(){
        for(Account account : accounts){
            ArrayList<Record> records = dbUtil.readRecordsByAccount(account.getName());
            double outcome = 0.00;
            double income = 0.00;
            for (Record record : records){
                if (record.getStatus().equals("支出")){
                    outcome += record.getMoney();
                    if (record.getAccount().equals("信用卡")){
                        negativeAsset += record.getMoney();
                    }
                }else{
                    income += record.getMoney();
                }
            }
            if (!account.getName().equals("信用卡")) {
                totalIncome += income;
            }
            totalOutcome += outcome;
            account.setIncome(income);
            account.setOutcome(outcome);
        }
        totalSurplus = totalIncome - totalOutcome;
    }





    @Override
    public void updateData() {
        totalIncome = 0.00;
        totalOutcome = 0.00;
        totalSurplus = 0.00;
        negativeAsset = 0.00;
        getDataFromDB();
        showData();
        adapter.notifyDataSetChanged();
    }
}
