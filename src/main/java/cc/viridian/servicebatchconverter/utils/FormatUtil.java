package cc.viridian.servicebatchconverter.utils;

import cc.viridian.servicebatchconverter.payload.DetailPayload;
import cc.viridian.servicebatchconverter.persistence.StatementDetail;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class FormatUtil {
    public static String parseDateDBformat(final String date, final String separator) {
        String rDate = null;
        String[] sptdate = date.split(separator);
        rDate = sptdate[0] + sptdate[1] + sptdate[2];
        return rDate;
    }

    public static String getInitialChar(final String s) {
        String rS = s.substring(0, 1);
        return rS;
    }

    public static String parseToNull(final String s) {
        String rS = s.contains("null") ? null : s;
        return rS;
    }

    public static LocalDate parseDateToLocalDate(final Date date) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        LocalDate localDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
        return localDate;
    }

    public static LocalDateTime parseDateToLocalDateTime(final Date date) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return localDateTime;
    }

}
