
package mr.common.time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Clases útiles para trabajar con fechas, horas, conversiones, etc.
 * @author Mariano Ruiz
 */
public abstract class TimeUtils {

    /**
     * Formato de fecha latinoamericano, ej. <code>20/12/2009</code>.
     */
    public static final String TIME_FORMAT_DDMMYYYY = "dd/MM/yyyy";

    /**
     * Formato de fecha americano, ej. <code>12/20/2009</code>.
     */
    public static final String TIME_FORMAT_MMDDYYYY = "MM/dd/yyyy";

    /**
     * Formato de fecha estándar, ej. <code>20091220</code>.
     */
    public static final String TIME_FORMAT_YYYYMMDD = "yyyyMMdd";

    /**
     * Formato de fecha en texto latinoamericano, ej. <code>1 de septiembre de 2009</code>.
     */
    public static final String TIME_FORMAT_DD_DE_MES_DE_YYYY = "d 'de' MMMM 'de' yyyy";

    /**
     * Formato de fecha en formato TIMESTAMP de base de datos, ej. <code>2009-11-23 14:50:55</code>.
     */
    public static final String TIME_FORMAT_TIMESTAMP = "yyyy-MM-dd hh:mm:ss";

    // Formateador estándar usado por algunos métodos
    private static SimpleDateFormat df = new SimpleDateFormat(TIME_FORMAT_YYYYMMDD);

    /**
     * Método que recibe una fecha en formato String y la devuelve como un objecto {@link java.util.Date}.
     * @param dateString String
     * @param mask String: Formato de la fecha a transformar, ej. <code>yyyy-MM-dd</code>
     * @return date
     * @throws ParseException e
     */
    public static Date strToDate(String dateString, String mask) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(mask);
        return df.parse(dateString);
    }

    /**
     * Método que recibe una fecha en formato String y la devuelve como un objecto {@link java.util.Calendar}.
     * @param dateString String
     * @param mask String: Formato de la fecha a transformar, ej. <code>yyyy-MM-dd</code>
     * @return calendar
     * @throws RuntimeException excepción {@link ParseException} wrappeada en una runtime
     */
    public static Calendar strToCalendar(String dateString, String mask) {
        DateFormat df = new SimpleDateFormat(mask);
        Date date;
		try {
			date = df.parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(df.getTimeZone());
		calendar.setTime(date);
		return calendar;
    }

    /**
     * Formatea a string la fecha pasada con la máscara pasada.
     * @param cal {@link java.util.Calendar}
     * @param mask String
     * @return String
     */
	public static String format(Calendar cal, String mask) {
		DateFormat df = new SimpleDateFormat(mask);
		return df.format(cal.getTime());
	}

    /**
     * Formatea a string la fecha pasada con la máscara pasada.
     * @param date {@link java.util.Date}
     * @param mask String
     * @return String
     */
	public static String format(Date date, String mask) {
		DateFormat df = new SimpleDateFormat(mask);
		return df.format(date.getTime());
	}

    /**
     * Retorna la misma fecha pero sin la información de la hora (o sea con hora 00:00).
     * @param d Date
     * @return Date
     * @throws RuntimeException excepción {@link ParseException} wrappeada en una runtime
     */
    public static Date dateWithoutHour(Date d) {
        Date fecha = null;
        try {
			fecha = df.parse(df.format(d));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
        return fecha;
    }
}