package mhwang.com.takecareofmoney;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import mhwang.com.adapter.FinaceListAdapter;
import mhwang.com.util.LogUitl;

/**
 * 项目名称：
 * 类描述：
 * 作者：王明海
 * 创建时间：2016/5/9
 */
public class FinaceListFragment extends Fragment {
    private static final String WANGYI_FINANCE = "http://money.163.com/";
    private static final String SINA_FINANCE = "http://finance.sina.cn/";
    private static final String FENGHUANG_FINANCE = "http://ifinance.ifeng.com/";
    private static final String QQ_FINANCE = "http://finance.qq.com/";
    private View mView;
    private ListView lv_list;
    private ArrayList<String> financeNames;
    private String[] financeUris = {SINA_FINANCE,WANGYI_FINANCE,FENGHUANG_FINANCE,QQ_FINANCE};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_list_finace_web,null);
        initComponent();
        initEvent();
        return mView;
    }

    private void initComponent(){
        lv_list = (ListView) mView.findViewById(R.id.lv_finace_web);
    }

    private void initEvent(){
        FinaceListAdapter adapter = new FinaceListAdapter(getActivity(),financeNames);
        lv_list.setAdapter(adapter);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUitl.showLog("FinaceListFragment","onItemClick");
                if (isNetAvailable()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(financeUris[position]));
                    startActivity(intent);
                } else {
                    showWarmDialog();
                }
            }
        });
    }

    private void initData(){
        financeNames = new ArrayList<>();
        financeNames.add("新浪财经");
        financeNames.add("网易财经");
        financeNames.add("凤凰财经");
        financeNames.add("腾讯财经");
    }

    /** 判断网络是否可用
     * @return
     */
    private boolean isNetAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isAvailable());
    }

    /**
     *  提出网络 不可用的提示
     */
    private void showWarmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage("网络不可用，请先设置网络");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

}
