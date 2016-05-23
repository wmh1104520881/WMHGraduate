package mhwang.com.takecareofmoney;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mhwang.com.bean.User;
import mhwang.com.util.UserUtil;

/**
 * 项目名称：
 * 类描述：
 * 作者：王明海
 * 创建时间：2016/4/16
 */
public class UserInfoFragment extends Fragment {
    private View mView;
    /**
     *  登陆的用户
     */
    private User user = null;

    private TextView tv_word;
    private TextView tv_age;
    private TextView tv_sex;
    private TextView tv_number;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_userinfo,null);
        user = UserUtil.getInstance().getCurUser();
        initComponent();
        return mView;
    }

    /**
     *  初始化控件
     */
    private void initComponent() {
        tv_word = (TextView) mView.findViewById(R.id.tv_userinfo_word);
        tv_age = (TextView) mView.findViewById(R.id.tv_userinfo_age);
        tv_sex = (TextView) mView.findViewById(R.id.tv_userinfo_sex);
        tv_number = (TextView) mView.findViewById(R.id.tv_userinfo_number);
        tv_word.setText(user.getWord());
        tv_age.setText(Integer.toString(user.getAge()));
        tv_number.setText(user.getNumber());
        tv_sex.setText(user.getSex());
    }

    /** 设置显示信息的用户
     * @param user
     */
    public void setUser(User user){
        this.user = user;
    }
}
