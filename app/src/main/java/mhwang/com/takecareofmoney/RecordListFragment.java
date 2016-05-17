package mhwang.com.takecareofmoney;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import mhwang.com.activity.RecordDetailActivity;
import mhwang.com.adapter.RecordAdapter;
import mhwang.com.bean.Record;
import mhwang.com.bean.Request;
import mhwang.com.database.DBUtil;
import mhwang.com.util.DateUtil;

/**
 * 项目名称：
 * 类描述：
 * 作者：王明海
 * 创建时间：2016/5/6
 */
public class RecordListFragment extends Fragment {
    public static final String KEY_WHICH_FRAGMENT = "which";
    public static final int TODAY = 0;
    public static final int THIS_WEEK = 1;
    public static final int THIS_MONTH = 2;
    public static final int THIS_YEAR = 3;
    public static final String[] TITLES = {"今天","本周","本月","本年"};

    private View mView;
    private ListView lv_list;
    private LinearLayout ll_bottomBar;
    private LinearLayout ll_allSelect;
    private Button btn_delete;
    private TextView tv_close;
    private ImageView iv_allSelect;
    private RecordAdapter adapter;
    private ImageView iv_back;
    private TextView tv_title;

    private ArrayList<Record> records;

    private int recordPos;
    private int recordId;
    /**
     *  显示哪个页面的数据
     */
    private int fragmentId;
    /**
     *  是否删除
     */
    private boolean isDel = false;
    /**
     * 是否全选
     */
    private boolean isAllSelect = false;

    private void showLog(String msg){
        Log.d("--RecordListFragment-->", msg);
    }

    /** 获取实例对象
     * @param which 显示哪种数据，0表示今天的数据，1表示本周，2表示本月，3表示本年
     * @return
     */
    public static RecordListFragment newInstance(int which){
        Bundle args = new Bundle();
        args.putInt(KEY_WHICH_FRAGMENT, which);
        RecordListFragment fragment = new RecordListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_record_list,null);
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
     *  显示数据
     */
    private void showData() {
        tv_title.setText(TITLES[fragmentId]);
    }

    /**
     *  事件处理
     */
    private void initEvent() {
        lv_list.setAdapter(adapter);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Record record = (Record) adapter.getItem(position);
                // 如果处于删除模式，则选中
                if (adapter.isDelMode()) {
                    record.setIsSelect(record.isSelect() ? false : true);
                    adapter.notifyDataSetChanged();
                } else {
                    recordPos = position;
                    recordId = record.getId();
                    Intent intent = new Intent(getActivity(), RecordDetailActivity.class);
                    intent.putExtra(RecordDetailFragment.KEY_RECORD_ID, recordId);
                    startActivityForResult(intent, Request.RECORD_DETAIL);
                }
            }
        });
        lv_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (ll_bottomBar.getVisibility() == View.GONE) {
                    ll_bottomBar.setVisibility(View.VISIBLE);
                    adapter.setDelMode(true);
                } else {
                    ll_bottomBar.setVisibility(View.GONE);
                    adapter.setDelMode(false);
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        // 全选
        ll_allSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAllSelect){
                    iv_allSelect.setImageResource(R.drawable.ic_box_unchecked);
                    // 将所有记录置为未选中
                    for(Record record : records){
                        record.setIsSelect(false);
                    }
                    isAllSelect = false;
                }else {
                    iv_allSelect.setImageResource(R.drawable.ic_box_checked);
                    // 将所有记录置为选中
                    for(Record record : records){
                        record.setIsSelect(true);
                    }
                    isAllSelect = true;
                }
                adapter.notifyDataSetChanged();
            }
        });
        // 关闭
        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ll_bottomBar.setVisibility(View.GONE);
                adapter.setDelMode(false);
                for(Record record : records){
                    record.setIsSelect(false);
                }
                adapter.notifyDataSetChanged();
            }
        });
        // 删除
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBUtil dbUtil = DBUtil.getInstance(getActivity());
                for(int i = 0; i < records.size(); i++){
                    Record record = records.get(i);
                    if (record.isSelect()){
                        dbUtil.deleteRecord(record.getId());
                        records.remove(i);
                        i--;
                    }
                }
                // 关闭删除模式
                adapter.setDelMode(false);
                // 隐藏删除框
                ll_bottomBar.setVisibility(View.GONE);
                // 将全选置为false
                isAllSelect = false;
                adapter.notifyDataSetChanged();
            }
        });
        // 返回
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    /**
     *  初始化控件
     */
    private void initComponent() {
        lv_list = (ListView) mView.findViewById(R.id.lv_record_list);
        ll_bottomBar = (LinearLayout) mView.findViewById(R.id.ll_record_bottom_bar);
        ll_allSelect = (LinearLayout) mView.findViewById(R.id.ll_record_list_all_select);
        btn_delete = (Button) mView.findViewById(R.id.btn_record_list_del);
        tv_close = (TextView) mView.findViewById(R.id.tv_record_list_close);
        iv_allSelect = (ImageView) mView.findViewById(R.id.iv_record_list_all_select);
        iv_back = (ImageView) mView.findViewById(R.id.iv_record_list_back);
        tv_title = (TextView) mView.findViewById(R.id.tv_record_list_title);

        adapter = new RecordAdapter(getActivity(),records);
    }

    /**
     *  初始化数据
     */
    private void initData(){
        fragmentId = getActivity().getIntent().getIntExtra(KEY_WHICH_FRAGMENT,-1);
        if (fragmentId == -1){
            return;
        }
        int year = DateUtil.getInstance().getYear();
        int month = DateUtil.getInstance().getMonth();
        int day = DateUtil.getInstance().getDay();
        DBUtil dbUtil = DBUtil.getInstance(getActivity());
        switch (fragmentId){
            case TODAY:
                records = dbUtil.readRecordsByDay(year,month,day);
                break;
            case THIS_WEEK:
                String[] weekDate = DateUtil.getInstance().getDateOfWeek();
                records = dbUtil.readRecordsByWeek(weekDate[0], weekDate[1]);
                break;
            case THIS_MONTH:
                records = dbUtil.readRecordsByMonth(year,month);
                break;
            case THIS_YEAR:
                records = dbUtil.readRecordsByYear(year);
                break;
        }
        showLog("read the fragmentId "+fragmentId+" data!");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != getActivity().RESULT_OK){
            return;
        }
        int status = data.getIntExtra(RecordDetailFragment.KEY_RECORD_MODIFY_STATUS,-1);
        showLog("onActivityResult status is "+status);
        if (status == RecordDetailFragment.DELETE){
            // 删除数据集中该记录
            adapter.getData().remove(recordPos);
        }else{
            // 获取该记录修改后的详细信息
            Record modifyRecord  = DBUtil.getInstance(getActivity()).
                    readRecordById(recordId);
            Record detailRecord = adapter.getData().get(recordPos);
            // 查看时间是否相同，如果不同，则直接将该记录从数据集中删除,否则修改该数据
            String modifyTime = ""+modifyRecord.getYear()+modifyRecord.getMonth()
                    +modifyRecord.getDay()+modifyRecord.getTime();
            String detailTime = ""+detailRecord.getYear()+detailRecord.getMonth()
                    +detailRecord.getDay()+detailRecord.getTime();
            if (modifyTime.equals(detailTime)){
                detailRecord.setMoney(modifyRecord.getMoney());
                detailRecord.setStatus(modifyRecord.getStatus());
                detailRecord.setAccount(modifyRecord.getAccount());
                detailRecord.setNote(modifyRecord.getNote());
                detailRecord.setType(modifyRecord.getType());
                detailRecord.setTypeChild(modifyRecord.getTypeChild());
            }else {
                adapter.getData().remove(recordPos);
            }
        }
        // 刷新列表
        adapter.notifyDataSetChanged();
    }
}
