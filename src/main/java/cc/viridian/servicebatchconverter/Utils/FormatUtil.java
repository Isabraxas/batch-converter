package cc.viridian.servicebatchconverter.Utils;

public class FormatUtil {
    public static String parseDateDBformat( final String date, final String separator){
        String rDate= null;
        String[] sptdate = date.split(separator);
        rDate = sptdate[0] + sptdate[1] + sptdate[2];
        return rDate;
    }
    public static String getInitialChar( final String s){
        String rS= s.substring(0,1);
        return rS;
    }
}
