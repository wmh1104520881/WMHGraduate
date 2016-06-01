package mhwang.com.takecareofmoney;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mhwang.com.abstracts.PagerFragment;
import mhwang.com.adapter.MyFragmentAdapter;
import mhwang.com.bean.BudgetType;
import mhwang.com.bean.Record;
import mhwang.com.bean.User;
import mhwang.com.database.DBUtil;
import mhwang.com.util.DateUtil;
import mhwang.com.util.LogUitl;
import mhwang.com.util.UserUtil;

public class MainActivity extends FragmentActivity {
    public static final int MENU_COUNT = 5;
    public static final int HOME = 0;
    public static final int DETAIL = 1;
    public static final int ACCOUNT = 2;
    public static final int REPORT = 3;
    public static final int MORE = 4;

    private int[] images = {R.drawable.t1,R.drawable.t2,R.drawable.t3,
            R.drawable.t4,R.drawable.t5,R.drawable.t6,
            R.drawable.t7,R.drawable.t8,R.drawable.t9};



    /**
     *  显示的界面列表
     */
    private List<Fragment> mFragments;

    /**
     *  用来显示界面的容器，类似ListView
     */
    private ViewPager mViewPager;

    /**
     *  界面切换的适配器
     */
    private static PagerAdapter pagerAdapter;

    /**
     *  菜单组
     */
    private RadioGroup mRadioGroup;

    private RadioButton[] mMenuButtons;

    /**
     *  Frament管理器
     */
    private FragmentManager fm;

    private LoginFragment lf;
    private RegisterFragment rf;
    private UserInfoFragment uif;

    private Button btn_logout;
    private TextView tv_userName;

    /**
     *  用户头像
     */
    private ImageView iv_userPhoto;

    private void showLog(String msg){
        Log.e("---MainActivity--->", msg);
    }

    /**
     *  更新页面
     */
    public static void updateAdapter(){
        pagerAdapter.notifyDataSetChanged();
        LogUitl.showLog("MainActivity","updateAdapter");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        DateUtil dateUtil = DateUtil.getInstance();
        showLog(dateUtil.getYear()+" "+dateUtil.getMonth()+" "+dateUtil.getDay());
        //////**********************/////
//        BudgetType budgetType = new BudgetType();
//        DBUtil.getInstance(this).updateBudge("ne",budgetType);
        /////////////******************////
        // 初始化数据
        initData();

        // 初始化控件
        initComponent();

        // 添加事件
        initEvent();
    }

    /**
     *  添加事件
     */
    private void initEvent() {
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                LogUitl.showLog("MainActivity","onPageSelected"+i);
                mMenuButtons[i].setChecked(true);
//                PagerFragment fragment = (PagerFragment) mFragments.get(i);
//                fragment.updateData();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int which = -1;
                switch (checkedId) {
                    case R.id.rb_home:
                        which = HOME;
                        break;
                    case R.id.rb_detail:
                        which = DETAIL;
                        break;
                    case R.id.rb_account:
                        which = ACCOUNT;
                        break;
                    case R.id.rb_report:
                        which = REPORT;
                        break;
                    case R.id.rb_more:
                        which = MORE;
                        break;
                }

                if (which != -1) {
                    mViewPager.setCurrentItem(which);
//                    PagerFragment fragment = (PagerFragment) mFragments.get(which);
//                    fragment.updateData();
                }
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(uif);
                ft.show(lf);
                ft.commit();
                // 将当前用户恢复为游客
                UserUtil.getInstance().logOutToDefaultUser();
                // 将登陆的用户设置成默认用户
                tv_userName.setText("欢迎你,"+UserUtil.getInstance().getCurUser().getName());
                // 图片恢复为默认
                iv_userPhoto.setImageResource(R.drawable.ic_user_photo);
                int curItem = mViewPager.getCurrentItem();
                PagerFragment fragment = (PagerFragment)mFragments.get(curItem);
                fragment.updateData();
                btn_logout.setVisibility(View.GONE);
            }
        });

    }

    /**
     *  初始化控件
     */
    private void initComponent() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mRadioGroup = (RadioGroup) findViewById(R.id.menu);
        mMenuButtons[HOME] = (RadioButton) findViewById(R.id.rb_home);
        mMenuButtons[DETAIL] = (RadioButton) findViewById(R.id.rb_detail);
        mMenuButtons[ACCOUNT] = (RadioButton) findViewById(R.id.rb_account);
        mMenuButtons[REPORT] = (RadioButton) findViewById(R.id.rb_report);
        mMenuButtons[MORE] = (RadioButton) findViewById(R.id.rb_more);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        tv_userName = (TextView) findViewById(R.id.tv_user_name);
        iv_userPhoto = (ImageView) findViewById(R.id.iv_user_photo);

        // 设置默认的抽屉fragment
        setDefaultDrawerFragment();
        pagerAdapter = new MyFragmentAdapter(fm,mFragments);
    }

    /**
     *  设置默认抽屉登陆界面
     */
    private void setDefaultDrawerFragment() {
        fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        lf = new LoginFragment();
        rf = new RegisterFragment();
        uif = new UserInfoFragment();
        lf.setOnLoginOrRegisterListener(new LoginFragment.OnItemListener() {
            @Override
            public void Login(User user) {
                addLoginFragment(user);
            }

            @Override
            public void Register() {
                addRegisterFragment();
            }
        });
        ft.add(R.id.fl_user_login_status, lf);
        ft.commit();
    }

    /**
     *  添加登陆后的Fragment
     */
    private void addLoginFragment(User user){
        showLog("Line 234 show UserInfo");
        uif = new UserInfoFragment();
        FragmentTransaction ftUif = fm.beginTransaction();
        // 将登陆的用户设置成当前用户
        UserUtil.getInstance().setCurUser(user);
        // 更新当前页数据
        int curItem = mViewPager.getCurrentItem();
        PagerFragment fragment = (PagerFragment)mFragments.get(curItem);
        fragment.updateData();
        // 如果当前页的前一页存在，更新前一页数据
        if (curItem -1 >0){
            PagerFragment fragment1 = (PagerFragment)mFragments.get(curItem-1);
            fragment1.updateData();
        }
        // 如果当前页的后一页存在，更新后一页数据
        if (curItem + 1 < 4){
            PagerFragment fragment2 = (PagerFragment)mFragments.get(curItem+1);
            fragment2.updateData();
        }
        tv_userName.setText(user.getName());
        // 设置登陆用户头像
        int imageIndex = Integer.parseInt(user.getPhoto());
        if (imageIndex != -1){
            iv_userPhoto.setImageResource(images[imageIndex]);
        }
        btn_logout.setVisibility(View.VISIBLE);
        // 添加用户信息界面，隐藏登陆界面，登陆成功后显示退出按钮
        ftUif.add(R.id.fl_user_login_status, uif);
        ftUif.hide(lf);
        ftUif.commit();
    }

    /**
     *  添加注册Fragment
     */
    private void addRegisterFragment(){
        rf = new RegisterFragment();
        rf.setOnItemClickListener(new RegisterFragment.OnItemClickListener() {
            @Override
            public void onItemClick() {
                FragmentTransaction mft = fm.beginTransaction();
                // 删除注册界面，显示登陆界面
                mft.remove(rf);
                mft.show(lf);
                mft.commit();
            }
        });
        FragmentTransaction ftRf = fm.beginTransaction();
        // 添加注册界面，隐藏登陆界面
        ftRf.add(R.id.fl_user_login_status, rf);
        ftRf.hide(lf);
        ftRf.commit();
    }

    /**
     *  初始化数据
     */
    private void initData() {
        // 初始化显示界面的view
        mFragments = new ArrayList<>();
        mFragments.add(new HomeFragment());
        mFragments.add(new MoneyDetailFragment());
        mFragments.add(new AccountFragment());
        mFragments.add(new MoneyReportFragment());
        mFragments.add(new MoreFragment());

        mMenuButtons = new RadioButton[MENU_COUNT];
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        showLog("onActivityResult");
//        switch (requestCode){
//            case HomeFragment.REQUEST_CODE:
//                Fragment fragment = mFragments.get(0);
//                fragment.onActivityResult(requestCode,resultCode,data);
//                break;
//        }

    }
}
