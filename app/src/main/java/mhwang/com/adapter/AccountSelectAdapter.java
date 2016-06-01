package mhwang.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import mhwang.com.takecareofmoney.R;

/**
 * 项目名称：
 * 类描述：
 * 作者：王明海
 * 创建时间：2016/5/30
 */
public class AccountSelectAdapter extends BaseAdapter {
    private String[] accounts;
    private LayoutInflater inflater;
    public AccountSelectAdapter(Context context,String[] accounts) {
        this.accounts = accounts;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return accounts.length;
    }

    @Override
    public Object getItem(int position) {
        return accounts[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String account = accounts[position];
        TextView textView;
        if (convertView == null){
            convertView = inflater.inflate(R.layout.item_account_select,null);
        }
        textView = (TextView) convertView.findViewById(R.id.tv_account_select_name);
        textView.setText(account);
        return convertView;
    }
}
