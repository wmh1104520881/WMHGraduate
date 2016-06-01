package mhwang.com.takecareofmoney;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import mhwang.com.bean.User;
import mhwang.com.database.DBUtil;
import mhwang.com.dialog.ImageListDialog;
import mhwang.com.util.DateUtil;
import mhwang.com.util.LogUitl;

/**
 * 项目名称：
 * 类描述：
 * 作者：王明海
 * 创建时间：2016/4/16
 */
public class RegisterFragment extends Fragment {
    public static final int REQUEST_IMAGE_LIST = 1002;

    private View mView;
    private ImageView ib_back;
    private ImageView ib_finish;
    private OnItemClickListener mListener;

    private EditText et_user_name;
    private EditText et_user_password;
    private EditText et_user_password_again;
    private EditText et_word;
    private EditText et_age;
    private EditText et_number;
    private ImageView iv_photo;
    private RadioGroup rg_sex;
    private RadioButton rb_man;

    private int photoIndex = -1;

    private String sex = "男";
    private int[] images = {R.drawable.t1,R.drawable.t2,R.drawable.t3,
            R.drawable.t4,R.drawable.t5,R.drawable.t6,
            R.drawable.t7,R.drawable.t8,R.drawable.t9};

    private void showToast(String msg){
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_register,null);
        initComponent();
        return mView;
    }

    /**
     *  初始化控件
     */
    private void initComponent(){
        ib_back = (ImageView) mView.findViewById(R.id.ib_regiter_back);
        ib_finish = (ImageView) mView.findViewById(R.id.ib_register_finish);
        et_user_name = (EditText) mView.findViewById(R.id.et_register_user_name);
        et_user_password = (EditText) mView.findViewById(R.id.et_register_password);
        et_user_password_again = (EditText) mView.findViewById(R.id.et_register_password_again);
        et_word = (EditText) mView.findViewById(R.id.et_register_word);
        et_age = (EditText) mView.findViewById(R.id.et_register_age);
        et_number = (EditText) mView.findViewById(R.id.et_register_number);
        rg_sex = (RadioGroup) mView.findViewById(R.id.rg_register_sex);
        iv_photo = (ImageView) mView.findViewById(R.id.iv_register_userphoto);
        rb_man = (RadioButton) mView.findViewById(R.id.rb_register_men);

        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick();
            }
        });

        ib_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!putRegisterDataToDB())
                    return;
                mListener.onItemClick();
            }
        });

        rg_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == rb_man.getId()){
                    sex = "男";
                }else{
                    sex = "女";
                }
            }
        });

        // 图片点击
        iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开图片选择
                showImageDialog();
            }
        });

    }

    /**
     *  显示图片选择框
     */
    private void showImageDialog(){
        Intent intent = new Intent(getActivity(), ImageListDialog.class);
        startActivityForResult(intent,REQUEST_IMAGE_LIST);
    }

    /**
     *  将注册信息保存到数据库
     */
    private boolean putRegisterDataToDB() {
        String userName = et_user_name.getText().toString();
        String password = et_user_password.getText().toString();
        String passwordAgain = et_user_password_again.getText().toString();
        String word = et_word.getText().toString();
        String number = et_number.getText().toString();
        String strAge = et_age.getText().toString();
        int age;
        if (strAge.isEmpty()){
            age = 0;
        }else {
            age = Integer.parseInt(et_age.getText().toString());
        }

        // 判断用户填入的数据是否有效
        if (userName.isEmpty() || password.isEmpty() || passwordAgain.isEmpty()){
            showToast("请填写完整");
            return false;
        }

        // 判断用户输入的密码是否一致
        if (!password.equals(passwordAgain)){
            showToast("密码不一致");
            return false;
        }

        // 判断用户是否存在
        DBUtil util = DBUtil.getInstance(getActivity());
        if (util.searchUser(userName) != null){
            showToast("该用户已存在，请更换用户名");
            return false;
        }

        // 将注册数据提交数据库
        User user = new User();
        user.setPassword(password);
        user.setName(userName);
        user.setPhoto(Integer.toString(photoIndex));
        user.setWord(word);
        user.setSex(sex);
        user.setAge(age);
        user.setNumber(number);
        DBUtil dbUtil = DBUtil.getInstance(getActivity());
        dbUtil.insertUser(user);
        // 查找刚插进去用户的id
        User lastUser = dbUtil.searchUser(user.getName());
        // 插入该用户的预算
        dbUtil.insertBudget(lastUser.getUserId());
        LogUitl.showLog("RegisterFragment","last insert user id is "+lastUser.getUserId());
        showToast("注册成功");
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != getActivity().RESULT_OK){
            return;
        }
        photoIndex = data.getIntExtra(ImageListDialog.KEY_IMAGE_INDEX,0);
        iv_photo.setImageResource(images[photoIndex]);
    }

    /** 监听按钮点击
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    /**
     *  按钮点击接口
     */
    public interface OnItemClickListener{
        public void onItemClick();
    }
}
