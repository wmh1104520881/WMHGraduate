package mhwang.com.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import mhwang.com.takecareofmoney.FinaceListFragment;
import mhwang.com.takecareofmoney.R;
import mhwang.com.util.LogUitl;

/**
 * 项目名称：
 * 类描述：
 * 作者：王明海
 * 创建时间：2016/5/9
 */
public class FinaceListActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_fragment);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_fragment,new FinaceListFragment());
        ft.commit();
    }
}
