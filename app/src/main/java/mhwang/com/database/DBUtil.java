package mhwang.com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mhwang.com.bean.Account;
import mhwang.com.bean.BudgetType;
import mhwang.com.bean.Record;
import mhwang.com.bean.User;
import mhwang.com.takecareofmoney.R;
import mhwang.com.util.DateUtil;
import mhwang.com.util.LogUitl;
import mhwang.com.util.RecordTypeToDBWordUtil;
import mhwang.com.util.UserUtil;

/**
 * 项目名称：
 * 类描述：数据库工具类对象
 * 作者：王明海
 * 创建时间：2016/4/16
 */
public class DBUtil {
    private void showLog(String msg){
        Log.d("---DBUtil--->",msg);
    }

    private static final String TABLE_USER = "users";
    private static final String TABLE_RECORD = "records";
    private static final String TABLE_BUDGET = "budget";
    private static final String TABLE_ACCOUNT = "account";


    private static DBUtil util = null;
    private DBHelper helper;
    private SQLiteDatabase sdb;
    private Context mContext;
    private UserUtil userUtil;


    private DBUtil(Context context) {
        helper = new DBHelper(context);
        sdb = helper.getReadableDatabase();
        mContext = context;
        userUtil = UserUtil.getInstance();
    }

    /** 实例化数据库工具类对象
     * @param context
     * @return
     */
    public static DBUtil getInstance(Context context){
        if (util == null){
            util = new DBUtil(context);
        }
        return util;
    }

    /** 插入用户
     * @param user
     */
    public void insertUser(User user){
        ContentValues values = new ContentValues();
        values.put("name",user.getName());
        values.put("word",user.getWord());
        values.put("info",user.getInfo());
        values.put("password",user.getPassword());
        sdb.insert(TABLE_USER, null, values);
    }

    /** 读取用户
     * @param name
     * @param password
     * @return
     */
    public User readUsers(String name,String password){
        Cursor cursor = sdb.query(TABLE_USER,null,"name=? AND password=?",
                new String[]{name,password},null,null,null);
        User user = null;
        if (cursor.moveToFirst()){
            user = new User();
            user.setUserId(cursor.getInt(cursor.getColumnIndex("userId")));
            user.setName(cursor.getString(cursor.getColumnIndex("name")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            user.setWord(cursor.getString(cursor.getColumnIndex("word")));
            user.setInfo(cursor.getString(cursor.getColumnIndex("info")));

            showLog("get the user info id: "+user.getUserId()+
            "\n name: "+user.getName()+
            "\n password: "+user.getPassword()+
            "\n word: "+user.getWord()+
            "\n info: "+user.getInfo());
        }
        cursor.close();
        return user;
    }

    /** 插入一条记录
     * @param record
     */
    public void insertRecord(Record record){
        ContentValues values = new ContentValues();
        values.put("money", record.getMoney());
        values.put("status",record.getStatus());
        values.put("type",record.getType());
        values.put("typeChild",record.getTypeChild());
        values.put("account",record.getAccount());
        values.put("note",record.getNote());
        values.put("userId",record.getUserId());
        values.put("year", record.getYear());
        values.put("month", record.getMonth());
        values.put("day", record.getDay());
        values.put("time", record.getTime());
        values.put("picture",record.getPhotoPath());
        values.put("userId",record.getUserId());

        showLog("add a record userId :" + record.getUserId() +
                " money :" + record.getMoney() + " status :" + record.getStatus() +
                " type :" + record.getType() + " account :" + record.getAccount() +
                " note :" + record.getNote());

        sdb.insert(TABLE_RECORD, null, values);
    }

    /** 查询某月份下的所有记录
     * @param month
     * @return
     */
    public ArrayList<Record> readRecordsByMonth(int year,int month){
        ArrayList<Record> records = new ArrayList<>();
        Cursor cursor = sdb.query(TABLE_RECORD, null, "year = ? AND month = ? AND userId = ?",
                new String[]{intToString(year),intToString(month),intToString(userUtil.getCurUserId())}, null, null, "day");
        while (cursor.moveToNext()){
            Record record = getRecordFromDB(cursor);
            records.add(record);
        }
        cursor.close();
        showLog("Line 144 read month data size is  " + records.size());
        return records;
    }

    /** 读取某年数据
     * @param year
     * @return
     */
    public ArrayList<Record> readRecordsByYear(int year){
        ArrayList<Record> records = new ArrayList<>();
        Cursor cursor = sdb.query(TABLE_RECORD, null, "year = ? AND userId = ?",
                new String[]{Integer.toString(year),intToString(userUtil.getCurUserId())}, null, null, null);
        while (cursor.moveToNext()){
            Record record = getRecordFromDB(cursor);
            records.add(record);
        }
        cursor.close();
        showLog("Line 162 read year data size is  " + records.size());
        return records;
    }

    /** 根据id读取记录
     * @return recordId
     */
    public Record readRecordById(int recordId){
        Cursor cursor = sdb.query(TABLE_RECORD,null,"id = ? AND userId = ?",
                new String[]{intToString(recordId),intToString(userUtil.getCurUserId())},null,null,null);
        Record record = null;
        if(cursor.moveToFirst()){
            record = getRecordFromDB(cursor);
        }
        cursor.close();
        showLog("Line 178 userUtil id is   " + userUtil.getCurUserId());
        return record;
    }

    /** 读取本月某个类型的记录
     * @return
     */
    public ArrayList<Record> readRecordsByType(String type){
        int month = DateUtil.getInstance().getMonth();
        int year = DateUtil.getInstance().getYear();
        Cursor cursor = sdb.query(TABLE_RECORD, null,
                "year = ? and month = ? and type = ? and userId = ?",
                new String[]{intToString(year),intToString(month), type,intToString(userUtil.getCurUserId())},
                null, null, null);
        if (cursor.getCount() == 0){
            cursor.close();
            showLog("not find the record !");
            return null;
        }
        ArrayList<Record> records = new ArrayList<>();
        while (cursor.moveToNext()){
            Record record = getRecordFromDB(cursor);
            records.add(record);
        }
        showLog("LINE 203 get the type " + type + " data size is "+records.size());
        cursor.close();
        return records;
    }

    /**
     *  获取某天的数据
     */
    public ArrayList<Record> readRecordsByDay(int year, int month,int day){
        Cursor cursor = sdb.query(TABLE_RECORD, null,
                "year = ? and month = ? and day = ? and userId = ?",
                new String[]{intToString(year), intToString(month), intToString(day),intToString(userUtil.getCurUserId())},
                null, null, null);

        ArrayList<Record> records = new ArrayList<>();
        while (cursor.moveToNext()){
            Record record = getRecordFromDB(cursor);
            records.add(record);
        }
        showLog("Line 220 get the month " + month + " day " + day + " data size is "+records.size());
        cursor.close();
        return records;
    }

    /** 读取某个时间范围的数据
     * @return
     */
    public ArrayList<Record> readRecordsByWeek(String startDate,String endDate){
        String[] startDates = startDate.split("-");
        String[] endDates = endDate.split("-");
        int startYear = Integer.parseInt(startDates[0]);
        int startMonth = Integer.parseInt(startDates[1]);
        int startDay = Integer.parseInt(startDates[2]);
        int endYear = Integer.parseInt(endDates[0]);
        int endMonth = Integer.parseInt(endDates[1]);
        int endDay = Integer.parseInt(endDates[2]);
        Cursor cursor;
        // 如果两个日期是在同一个月的
        if (startDay < endDay){
            cursor = sdb.query(TABLE_RECORD,null,
                    "day BETWEEN ? AND ? AND year = ? AND month = ? AND userId = ?",
                    new String[]{intToString(startDay),intToString(endDay),
                    intToString(startYear),intToString(startMonth),intToString(userUtil.getCurUserId())}
            ,null,null,null);
            showLog("the two date "+startDay+" and "+endDay+" is in the same month");
        }else{
            cursor = sdb.query(TABLE_RECORD,null,"((month = ? AND day >= ?) OR" +
                    "(month = ? AND day <= ?)) AND year = ? AND userId = ?",
                    new String[]{intToString(startMonth), intToString(startDay),
                            intToString(endMonth), intToString(endDay),
                            intToString(startYear),intToString(userUtil.getCurUserId())},
                    null,null,null);
        }

        ArrayList<Record> records = new ArrayList<>();
        while (cursor.moveToNext()){
            Record record = getRecordFromDB(cursor);
            records.add(record);
        }
        cursor.close();
        showLog("Line 261 read the time " + startDate + " to " + endDate + " data" + " result size is " + records.size());
        return records;

    }

    /** 读取某个账户下的记录
     * @param account
     * @return
     */
    public ArrayList<Record> readRecordsByAccount(String account){
        Cursor cursor = sdb.query(TABLE_RECORD, null, "account = ? AND userId = ?",
                new String[]{account,intToString(userUtil.getCurUserId())}, null, null, null);
        ArrayList<Record> records = new ArrayList<>();
        while (cursor.moveToNext()){
            Record record = getRecordFromDB(cursor);
            records.add(record);
        }
        showLog("lien 279 read the record "+" by account "+account+" size is "+records.size());
        cursor.close();
        return records;
    }

    /** 根据关键词和日期搜索
     * @param date
     * @param key
     * @return
     */
    public ArrayList<Record> readRecordsByDateAndKey(String date,String key){
        String[] dates = date.split("-");
        ArrayList<Record> records = new ArrayList<>();
        RecordTypeToDBWordUtil rttdbwu = RecordTypeToDBWordUtil.getInstance(mContext);
        String word = rttdbwu.getDBWord(key);
        if (word.isEmpty()){
            return records;
        }
        final String nodate = "无";
        int year = 0;
        int month = 0;
        int day = 0;
        if (!dates[0].equals(nodate)){
            year = Integer.parseInt(dates[0]);
        }
        if (!dates[1].equals(nodate)){
            month = Integer.parseInt(dates[1]);
        }
        if (!dates[2].equals(nodate)){
            day = Integer.parseInt(dates[2]);
        }

        Cursor cursor;
        // 如果月份不为0，则查看具体日期是否为0,如果具体日期不为0，则按具体日期查询
        //如果月份为0，则查询具体年份
        if (month != 0){
            // 如果日期不为0,查询具体日期,否则查询具体月份
            if (day != 0){
                cursor = sdb.query(TABLE_RECORD,null,
                        "year = ? And month = ? And day = ? And "+word+"=? AND userId = ?",
                        new String[]{intToString(year),intToString(month),
                        intToString(day),key,intToString(userUtil.getCurUserId())},null,null,null);
            }else{
                cursor = sdb.query(TABLE_RECORD,null,"year = ? AND month = ? AND "+word+"=? AND userId = ?",
                        new String[]{intToString(year),intToString(month),key,intToString(userUtil.getCurUserId())},null,null,null);
            }
        }else{
            cursor = sdb.query(TABLE_RECORD,null,"year = ? AND "+word+"=? AND userId = ?",
                    new String[]{intToString(year),key,intToString(userUtil.getCurUserId())},null,null,null);
        }

        while (cursor.moveToNext()){
            Record record = getRecordFromDB(cursor);
            records.add(record);
        }
        showLog("Line 334 readRecordsByDate the date is "+date+"key is "+key+" result size is "+records.size());
        cursor.close();
        return records;

    }

    /** 根据日期查询记录
     * @param date
     * @return
     */
    public ArrayList<Record> readRecordsByDate(String date){
        ArrayList<Record> records = new ArrayList<>();
        String[] dates = date.split("-");
        final String nodate = "无";
        int year = 0;
        int month = 0;
        int day = 0;
        if (!dates[0].equals(nodate)){
            year = Integer.parseInt(dates[0]);
        }
        if (!dates[1].equals(nodate)){
            month = Integer.parseInt(dates[1]);
        }
        if (!dates[2].equals(nodate)){
            day = Integer.parseInt(dates[2]);
        }

        Cursor cursor;
        // 如果月份不为0，则查看具体日期是否为0,如果具体日期不为0，则按具体日期查询
        //如果月份为0，则查询具体年份
        if (month != 0){
            // 如果日期不为0,查询具体日期,否则查询具体月份
            if (day != 0){
                cursor = sdb.query(TABLE_RECORD,null,
                        "year = ? And month = ? And day = ? AND userId = ?",
                        new String[]{intToString(year),intToString(month),
                                intToString(day),intToString(userUtil.getCurUserId())},null,null,null);
            }else{
                cursor = sdb.query(TABLE_RECORD,null,"year = ? AND month = ? And userId = ?",
                        new String[]{intToString(year),intToString(month),
                                intToString(userUtil.getCurUserId())},null,null,null);
            }
        }else{
            cursor = sdb.query(TABLE_RECORD,null,"year = ? AND userId = ?",
                    new String[]{intToString(year),intToString(userUtil.getCurUserId())},null,null,null);
        }

        while (cursor.moveToNext()){
            Record record = getRecordFromDB(cursor);
            records.add(record);
        }
        showLog("line 385 readRecordsByDate the date is "+date+" result size is "+records.size());
        cursor.close();
        return records;
    }


    /** 根据关键词查询记录
     * @param key
     * @return
     */
    public ArrayList<Record> readRecordsByKey(String key){
        ArrayList<Record> records = new ArrayList<>();
        RecordTypeToDBWordUtil rttdbwu = RecordTypeToDBWordUtil.getInstance(mContext);
        String word = rttdbwu.getDBWord(key);
        if (word.isEmpty()){
            return records;
        }
        Cursor cursor = sdb.query(TABLE_RECORD,null,word+"=? And userId = ?",
                new String[]{key,intToString(userUtil.getCurUserId())},null,null,null);
        while (cursor.moveToNext()){
            Record record = getRecordFromDB(cursor);
            records.add(record);
        }
        cursor.close();
        return records;
    }

    /** 更新记录
     * @param record
     */
    public void updateRecord(Record record){
        ContentValues values = new ContentValues();
        values.put("money",record.getMoney());
        values.put("status",record.getStatus());
//        values.put("picture",record.);
        values.put("type",record.getType());
        values.put("typeChild",record.getTypeChild());
        values.put("account",record.getAccount());
        values.put("note",record.getNote());
        values.put("year",record.getYear());
        values.put("month",record.getMonth());
        values.put("day",record.getDay());
        values.put("time",record.getTime());
        sdb.update(TABLE_RECORD, values, "id = ?",
                new String[]{intToString(record.getId())});
    }


    /**
     *  更新预算
     */
    public void updateBudge(BudgetType budget){
        // 更新数据
        ContentValues values = new ContentValues();
        values.put("budget",budget.getBudget());
        values.put("surplus",budget.getSurplus());
        sdb.update(TABLE_BUDGET, values, "type = ? AND userId = ?",
                new String[]{budget.getType(),intToString(userUtil.getCurUserId())});
    }

    /** 读取预算类型
     * @return
     */
    public ArrayList<BudgetType> readBudgetTypes(){
        Cursor cursor = sdb.query(TABLE_BUDGET,null,null,null,null,null,null);
        ArrayList<BudgetType> budgetTypes = new ArrayList<>();
        while (cursor.moveToNext()){
            String type = cursor.getString(cursor.getColumnIndex("type"));
            double budget = cursor.getDouble(cursor.getColumnIndex("budget"));
            double surplus = cursor.getDouble(cursor.getColumnIndex("surplus"));
            BudgetType budgetType = new BudgetType(type,surplus,budget);
            budgetTypes.add(budgetType);
        }
        cursor.close();
        return budgetTypes;
    }

    /** 读取账户列表
     * @return
     */
    public ArrayList<Account> readAccounts(){
        Cursor cursor = sdb.query(TABLE_ACCOUNT,null,null,null,null,null,null);
        ArrayList<Account> accounts = new ArrayList<>();
        while (cursor.moveToNext()){
            Account account = new Account();
            account.setName(cursor.getString(cursor.getColumnIndex("name")));
//            account.setMoney(cursor.getDouble(cursor.getColumnIndex("money")));
            accounts.add(account);
        }
        cursor.close();
        return accounts;
    }

    /** 删除单条记录
     * @param recordId
     */
    public void deleteRecord(int recordId){
        sdb.delete(TABLE_RECORD,"id = ?",new String[]{intToString(recordId)});
        showLog("delete a record id is "+recordId);
    }


    /** 从数据库获取记录
     * @param cursor
     * @return
     */
    private Record getRecordFromDB(Cursor cursor){
        Record record = new Record();
        record.setId(cursor.getInt(cursor.getColumnIndex("id")));
        record.setTime(cursor.getString(cursor.getColumnIndex("time")));
//            record.setName(cursor.getString(cursor.getColumnIndex("name")));
        record.setStatus(cursor.getString(cursor.getColumnIndex("status")));
        record.setMoney(cursor.getDouble(cursor.getColumnIndex("money")));
        record.setAccount(cursor.getString(cursor.getColumnIndex("account")));
        record.setNote(cursor.getString(cursor.getColumnIndex("note")));
        record.setType(cursor.getString(cursor.getColumnIndex("type")));
        record.setTypeChild(cursor.getString(cursor.getColumnIndex("typeChild")));
        record.setUserId(cursor.getInt(cursor.getColumnIndex("userId")));
        record.setYear(cursor.getInt(cursor.getColumnIndex("year")));
        record.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
        record.setDay(cursor.getInt(cursor.getColumnIndex("day")));
        record.setPhotoPath(cursor.getString(cursor.getColumnIndex("picture")));
        return record;
    }


    /** int 转 String
     * @param value
     * @return
     */
    private String intToString(int value){
        return Integer.toString(value);
    }

}
