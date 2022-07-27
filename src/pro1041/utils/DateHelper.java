package pro1041.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {

    private static SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
    private static SimpleDateFormat timestampFormater = new SimpleDateFormat("dd/MM/yyyy - hh:mm a");

    public static Date toDate(String date, String... pattern) {
        try {
            if (pattern.length > 0) {

                formater.applyPattern(pattern[0]);

            }
            if (date == null) {
                return DateHelper.now();
            }
            System.out.println(date);
            return formater.parse(date);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String toString(Date date, String... pattern) {
        if (pattern.length > 0) {
            formater.applyPattern(pattern[0]);
        }
        if (date == null) {
            date = DateHelper.now();
        }
        return formater.format(date);
    }
    
    public static String toString(Timestamp date) {
        return timestampFormater.format(date);
    }

    public static Date addDays(Date date, long days) {
        date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);
        return date;
    }

    public static Date add(int days) {
        Date now = DateHelper.now();
        now.setTime(now.getTime() + days * 24 * 60 * 60 * 1000);
        return now;
    }

    public static Date now() {
        return new Date();
    }
}
