package app;

public class SimpleStats {
    public int lga_code;
    public String year;
    public String age1;
    public String age2;
    public String sex; 
    public int count;
    public String display;
    //public String display;

    public SimpleStats() {  
    }

    public SimpleStats(int lga_code, String year, String age1, String age2, String sex, int count, String display) {
        this.lga_code = lga_code;
        this.year = year;
        this.age1 = age1;
        this.age2 = age2;
        this.sex = sex;
        this.count = count;
        this.display = display;
    }

}
