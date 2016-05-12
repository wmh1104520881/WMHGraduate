package mhwang.com.util;

import android.util.Log;

import mhwang.com.bean.User;

/**
 * 项目名称：
 * 类描述：现在登陆的用户工具类
 * 作者：王明海
 * 创建时间：2016/5/10
 */
public class UserUtil  {
    private static UserUtil userUtil = null;
    private User curUser;
    private void showLog(String msg){
        Log.d("--UserUtil-->",msg);
    }

    public UserUtil(){
        curUser = new User();
    }

    /** 获取实例
     * @return
     */
    public static UserUtil getInstance(){
        if (userUtil == null){
            userUtil = new UserUtil();
        }
        return userUtil;
    }

    /** 设置当前用户
     * @param user
     */
    public void setCurUser(User user){
        curUser = user;
        showLog("Current User Change to "+user.getUserId());
    }

    /** 获取当前用户
     * @return
     */
    public User getCurUser(){
        return curUser;
    }

    /**
     *  获取当前用户id
     */
    public int getCurUserId(){
        return curUser.getUserId();
    }

    /**
     *  转成默认用户(游客)
     */
    public void logOutToDefaultUser(){
        curUser = new User();
        showLog("Current User Change to "+curUser.getUserId());
    }


}
