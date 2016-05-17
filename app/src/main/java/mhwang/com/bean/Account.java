package mhwang.com.bean;

/**
 * 项目名称：
 * 类描述：账户类
 * 作者：王明海
 * 创建时间：2016/4/30
 */
public class Account {
    private int id;
    private String name;
    private double income;
    private double outcome;
    public Account(){
        name = "";
        income = 0.00;
        outcome = 0.00;
        id = 0;
    }

    public int getId() {
        return id;
    }

    public Account(String name, double income,double outcome) {
        this.name = name;
        this.outcome = outcome;
        this.income = income;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public double getIncome() {
        return income;
    }

    public double getOutcome() {
        return outcome;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public void setOutcome(double outcome) {
        this.outcome = outcome;
    }

    public void setId(int id) {
        this.id = id;
    }
}
