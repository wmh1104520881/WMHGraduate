package mhwang.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import mhwang.com.takecareofmoney.R;

/**
 * 项目名称：
 * 类描述： 理财列表适配器
 * 作者：王明海
 * 创建时间：2016/5/9
 */
public class FinaceListAdapter extends BaseAdapter{
    private ArrayList<String> lists;
    private LayoutInflater inflater;

    public FinaceListAdapter(Context context, ArrayList<String> lists) {
        this.lists = lists;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        String text = lists.get(position);
        if (convertView == null){
            convertView = inflater.inflate(R.layout.item_button,null);
            holder = new ViewHolder();
            holder.button = (TextView) convertView.findViewById(R.id.btn_item);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.button.setText(text);
        return convertView;
    }
    private static class ViewHolder{
        TextView button;
    }
}
