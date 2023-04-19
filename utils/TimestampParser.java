package utils;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampParser {
    public static Timestamp timeStrToTimestamp(String timestamp_STRING) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp timestamp = null;

        try {
            Date date = dateFormat.parse(timestamp_STRING);
            timestamp = new Timestamp(date.getTime());
        } catch (ParseException e) {
            System.err.println("Invalid parsing date string.");
        }
        return timestamp;
    }


}

