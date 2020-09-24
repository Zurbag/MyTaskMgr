package sample.sourse;

public class DateStringParser {
    private String date;
    private int year;
    private int month;
    private int day;

    public DateStringParser(String date) {
        this.date = date;
    }

    public int getYear() {
        return year = Integer.parseInt(date.substring(0,4));
    }

    public int getMonth() {
        return Integer.parseInt(date.substring(5,7))-1;
    }

    public int getDay() {
        return Integer.parseInt(date.substring(8,10));
    }
}
