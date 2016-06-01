package mhwang.com.takecareofmoney;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mhwang.com.bean.User;
import mhwang.com.database.DBUtil;
import mhwang.com.util.LogUitl;
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

    private EditText et_word;
    private EditText et_age;
    private EditText et_sex;
    private EditText et_number;
    private Button btn_modify;
    private RelativeLayout rl_title;
    private LinearLayout ll_sex;
    private LinearLayout ll_sex_modify;
    private ImageView iv_back;
    private ImageView iv_finish;
    private RadioGroup rg_sex;
    private RadioButton rb_man;


    private String sex = "男";

    private boolean isModify = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_userinfo,null);
        user = UserUtil.getInstance().getCurUser();
        initComponent();
        showData();
        initEvent();
        return mView;
    }

    /**
     *  初始化控件
     */
    private void initComponent() {
        et_word = (EditText) mView.findViewById(R.id.et_userinfo_word);
        et_age = (EditText) mView.findViewById(R.id.et_userinfo_age);
        et_sex = (EditText) mView.findViewById(R.id.et_userinfo_sex);
        et_number = (EditText) mView.findViewById(R.id.et_userinfo_number);
        rl_title = (RelativeLayout) mView.findViewById(R.id.rl_userinfo_title);
        ll_sex = (LinearLayout) mView.findViewById(R.id.ll_userinfo_sex);
        ll_sex_modify = (LinearLayout) mView.findViewById(R.id.ll_userinfo_sex_edit);
        btn_modify = (Button) mView.findViewById(R.id.btn_userinfo_modify);
        iv_back = (ImageView) mView.findViewById(R.id.iv_userinfo_back);
        iv_finish = (ImageView) mView.findViewById(R.id.iv_userinfo_finish);
        rg_sex = (RadioGroup) mView.findViewById(R.id.rg_userinfo_sex);
        rb_man = (RadioButton) mView.findViewById(R.id.rb_userinfo_man);

    }

    /**
     *  显示数据
     */
    private void showData(){
        et_word.setText(user.getWord());
        et_age.setText(Integer.toString(user.getAge()));
        et_number.setText(user.getNumber());
        et_sex.setText(user.getSex());
    }

    private void initEvent(){
        // 修改
        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEditable(true, true, true, true, true, true);

            }
        });

        // 返回
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEditable(false, false, false, false, false, false);
                showData();
            }
        });

        // 姓别选择
        rg_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                LogUitl.showLog("UserInfoFragment",""+checkedId);
                if (checkedId == rb_man.getId()){
                    sex = "男";
                }else{
                    sex = "女";
                }
            }
        });

        // 完成
        iv_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyUser();
                showData();
                changeEditable(false,false,false,false,false,false);
            }
        });

    }

    /**
     *  修改用户
     */
    private void modifyUser(){
        String word = et_word.getText().toString();
        int age = Integer.parseInt(et_age.getText().toString());
        String number = et_number.getText().toString();
        user.setWord(word);
        user.setAge(age);
        user.setNumber(number);
        user.setSex(sex);
        DBUtil.getInstance(getActivity()).modifyUser(user);
    }

    /** 转换是否编辑状态
     * @param title
     * @param age
     * @param word
     * @param number
     * @param sex
     * @param button
     */
    private void changeEditable(boolean title,boolean age,boolean word,boolean number,
                              boolean sex,boolean button){
        rl_title.setVisibility(title?View.VISIBLE:View.GONE);
        et_age.setEnabled(age);
        et_word.setEnabled(word);
        et_number.setEnabled(number);
        if (sex){
            ll_sex.setVisibility(View.GONE);
            ll_sex_modify.setVisibility(View.VISIBLE);
        }else {
            ll_sex_modify.setVisibility(View.GONE);
            ll_sex.setVisibility(View.VISIBLE);
        }
        btn_modify.setVisibility(button?View.GONE:View.VISIBLE);
    }

    /** 设置显示信息的用户
     * @param user
     */
    public void setUser(User user){
        this.user = user;
    }
}
