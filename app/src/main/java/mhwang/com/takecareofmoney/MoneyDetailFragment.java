package mhwang.com.takecareofmoney;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mhwang.com.abstracts.PagerFragment;
import mhwang.com.activity.RecordDetailActivity;
import mhwang.com.adapter.DetailExpandableAdapter;
import mhwang.com.bean.DateRecord;
import mhwang.com.bean.Record;
import mhwang.com.database.DBUtil;
import mhwang.com.util.DateUtil;
import mhwang.com.util.LogUitl;
import mhwang.com.util.NumberFormat;

/**
 * 项目名称：
 * 类描述：
 * 作者：王明海
 * 创建时间：2016/4/7
 */
public class MoneyDetailFragment extends PagerFragment {
    public static final int ONE_YEAR_MONTH = 12;
    /**
     *  月份列表
     */
    private List<DateRecord> groups;
    /**
     *  记录列表
     */
    private List<ArrayList<Record>> childs;
    private ExpandableListView elv_list;
    private DetailExpandableAdapter adapter;
    private View mView;
    private TextView tv_surplus;
    private TextView tv_income;
    private TextView tv_outcome;
    private TextView tv_year;

    private ImageView iv_previous;
    private ImageView iv_next;



    private double totalIncome = 0.00;
    private double totalOutcome = 0.00;
    private double totalSurplus = 0.00;

    private int year;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        year = DateUtil.getInstance().getYear();
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_money_detail,null);
        LogUitl.showLog("MoneyDetailFragment", "onCreateView");

        initComponent();
        initEvent();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        showData();
    }

    /**
     *  添加事件
     */
    private void initEvent(){
        adapter = new DetailExpandableAdapter(getActivity(),groups,childs);
        elv_list.setAdapter(adapter);
        elv_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Record record = (Record) adapter.getChild(groupPosition, childPosition);
                Intent intent = new Intent(getActivity(), RecordDetailActivity.class);
                intent.putExtra(RecordDetailFragment.KEY_RECORD_ID, record.getId());
                startActivity(intent);
                return true;
            }
        });
        iv_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year -= 1;
                updateData();
            }
        });
        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year += 1;
                updateData();
            }
        });
    }

    /**
     *  更新数据
     */
    public void updateData(){
        LogUitl.showLog("MoneyDetailFragment", "updateData");
        clearData();
        getData(year);
        adapter.notifyDataSetChanged();
        showData();
    }

    /**
     * 清除数据
     */
    private void clearData(){
        groups.clear();
        childs.clear();
        totalIncome = 0.00;
        totalOutcome = 0.00;
    }

    /**
     *  初始化组件
     */
    private void initComponent() {
        elv_list = (ExpandableListView) mView.findViewById(R.id.elv_detail_expListView);
        tv_income = (TextView) mView.findViewById(R.id.tv_detail_income);
        tv_surplus = (TextView) mView.findViewById(R.id.tv_detail_surplus);
        tv_outcome = (TextView) mView.findViewById(R.id.tv_detail_outcome);
        tv_year = (TextView) mView.findViewById(R.id.tv_money_detail_year);
        iv_previous = (ImageView) mView.findViewById(R.id.ib_money_detail_previous);
        iv_next = (ImageView) mView.findViewById(R.id.ib_money_detail_next);
    }

    /**
     *  显示数据
     */
    private void showData(){
        tv_income.setText(NumberFormat.format(totalIncome));
        tv_outcome.setText(NumberFormat.format(totalOutcome));
        tv_surplus.setText(NumberFormat.format(totalSurplus));
        tv_year.setText(year+"年");
    }

    /**
     *  初始化数据
     */
    private void initData() {
        // 模拟数据
//        groups = readMonthGroups();
//        childs = readMonthRecords();

        groups = new ArrayList<>();
        childs = new ArrayList<>();

        getData(year);

    }

    /**
     *  获取数据库数据
     */
    private void getData(int year){
        int curYear = DateUtil.getInstance().getYear();
        int month = 0;
        if (year != curYear){
            month = ONE_YEAR_MONTH;
        }else{
            month = DateUtil.getInstance().getMonth();
        }

        for (int i = 1; i <= month; i++){
            DateRecord drecord = new DateRecord();
            drecord.setName(i + "月");
            // 获取该月份下的所有记录
            ArrayList<Record> groupChilds = DBUtil.getInstance(getActivity()).readRecordsByMonth(year,i);
            childs.add(groupChilds);
            double monthIncome = 0.00;
            double monthOutput = 0.00;
            // 统计该月份下的收入和支出
            for (Record r : groupChilds){
                if(r.getStatus().equals("收入")) {
                    monthIncome += r.getMoney();
                }else {
                    monthOutput += r.getMoney();
                }
            }
            drecord.setIncome(monthIncome);
            drecord.setOutput(monthOutput);
            totalIncome += monthIncome;
            totalOutcome += monthOutput;
            LogUitl.showLog("MoneyDetailFragment","month "+i+" Income is "+monthIncome);
            LogUitl.showLog("MoneyDetailFragment","month "+i+" Outcome is "+monthOutput);
            groups.add(drecord);
        }

        totalSurplus = totalIncome - totalOutcome;
        LogUitl.showLog("MoneyDetailFragment","month Total "+" Income is "+totalIncome);
        LogUitl.showLog("MoneyDetailFragment","month Total "+" Outcome is "+totalOutcome);


    }

    /** 读取月分列表
     * @return
     */
    private List<DateRecord> readMonthGroups() {
        // 获取当前月份
        int month = DateUtil.getInstance().getMonth();

        List<DateRecord> monthGroups = new ArrayList<>();
        for(int i = 1; i <= month; i++){
            DateRecord record = new DateRecord();
            record.setName(i+"月");
            record.setIncome(i + 0.33);
            record.setOutput(i - 0.33);
            monthGroups.add(record);
        }
        return monthGroups;
    }



    /** 读取月份下的记录
     * @return
     */
    private List<ArrayList<Record>> readMonthRecords(){
        List<ArrayList<Record>> monthChilds = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++){
            ArrayList<Record> child = new ArrayList<Record>();
            for(int j = 1; j < 6; j++){
                Record record = new Record("2015年"+i+"月20：22：22","早餐"+i,i+0.3);
                child.add(record);
            }
            monthChilds.add(child);
        }
        return  monthChilds;
    }




}
