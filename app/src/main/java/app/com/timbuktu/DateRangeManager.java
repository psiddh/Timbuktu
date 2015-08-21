package app.com.timbuktu;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.util.Log;
import android.util.Pair;

import app.com.timbuktu.util.LogUtils;

public class DateRangeManager implements LogUtils {

    private String TAG = "SpickIt> DateRangeManage";
    Calendar Cal = null;
    //private int mCurDayOfWeek = -1;
    private int mYear = -1;
    private int mMonth = -1;
    private int mDayOfMonth = -1;
    public DateRangeManager(){
        // TBD: Timezone issues ?
        //create Calendar instance
        Cal = Calendar.getInstance((Locale.getDefault()));
        //mCurDayOfWeek = Cal.get(Calendar.DAY_OF_WEEK);
        mYear = Cal.get(Calendar.YEAR);
        mMonth = Cal.get(Calendar.MONTH);
        mDayOfMonth = Cal.get(Calendar.DAY_OF_MONTH);
    };

    public int getCurrentYear() {
        return mYear;
    }

    public int getCurrentMonth() {
        return mMonth;
    }

    public int getCurrentDayOfMonth() {
        return mDayOfMonth;
    }

    /*public Pair<Long,Long> getLastWeekEnd1() {
        int offset = 0;
        // TBD: Ah ! This may vary based on different world cultures
        switch (mCurDayOfWeek) {
        case 1: // Sunday
            offset = -8;
            break;
        case 2: // Monday
        case 3: // Tuesday
        case 4: // Wednesday
        case 5: // Thursday
        case 6: // Friday
        case 7: // Saturday
            offset = mCurDayOfWeek  * -1;
            break;
        }
        int start_of_prev_weekend =  offset;

        Calendar prev_weekend_start = Calendar.getInstance((Locale.getDefault()));
        // reset hour, minutes, seconds and millis
        prev_weekend_start.set(Calendar.HOUR_OF_DAY, 0);
        prev_weekend_start.set(Calendar.MINUTE, 0);
        prev_weekend_start.set(Calendar.SECOND, 0);
        prev_weekend_start.set(Calendar.MILLISECOND, 0);
        prev_weekend_start.add(Calendar.DATE, start_of_prev_weekend);

        this.printDateAndTime(prev_weekend_start);
        Long val1 = prev_weekend_start.getTimeInMillis();
        // Compute from Saturday earliest morning (12:00 AM i;e Friday Midnight) till Monday earliest morning (i;e Sunday night)
        prev_weekend_start.add(Calendar.DATE, 2);
        this.printDateAndTime(prev_weekend_start);

        Long val2 = prev_weekend_start.getTimeInMillis();
        return new Pair<Long, Long>(val1,val2);
    }*/

    public Pair<Long,Long> getLastWeekEnd() {
        Long val1, val2;
        Calendar today = Calendar.getInstance((Locale.getDefault()));
        today.set(Calendar.DAY_OF_WEEK, today.getFirstDayOfWeek());
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        int day_of_year = today.get(Calendar.DAY_OF_YEAR);
        today.set(Calendar.DAY_OF_YEAR, day_of_year - 1);
        val1 = today.getTimeInMillis();

        day_of_year = today.get(Calendar.DAY_OF_YEAR);
        today.set(Calendar.DAY_OF_YEAR, day_of_year + 1);
        today.set(Calendar.HOUR_OF_DAY, 23);
        today.set(Calendar.MINUTE, 59);
        today.set(Calendar.SECOND, 59);
        today.set(Calendar.MILLISECOND, 999);
        val2 = today.getTimeInMillis();
        Pair<Long, Long> p = new Pair<Long, Long>(val1,val2);
        return p;
    }

    public Pair<Long,Long> getToday() {
       Long val1, val2;
        Calendar today = Calendar.getInstance((Locale.getDefault()));
        Calendar today1 = Calendar.getInstance((Locale.getDefault()));
        // reset hour, minutes, seconds and millis
        today.set(Calendar.YEAR, mYear);
        today.set(Calendar.MONTH, mMonth);
        today.set(Calendar.DAY_OF_MONTH, mDayOfMonth);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        val1 = today.getTimeInMillis();
        //today.clear();
        today1.set(Calendar.YEAR, mYear);
        today1.set(Calendar.MONTH, mMonth);
        today1.set(Calendar.DAY_OF_MONTH, mDayOfMonth);
        today1.set(Calendar.HOUR_OF_DAY, 23);
        today1.set(Calendar.MINUTE, 59);
        today1.set(Calendar.SECOND, 59);
        today1.set(Calendar.MILLISECOND, 999);
        val2 = today1.getTimeInMillis();
        Pair<Long, Long> p = new Pair<Long, Long>(val1,val2);
        return p;
    }

    public Pair<Long,Long> getYesterday() {
         Long val1, val2;
         Calendar yesterday = Calendar.getInstance((Locale.getDefault()));
         yesterday.set(Calendar.YEAR, mYear);
         yesterday.set(Calendar.MONTH, mMonth);
         yesterday.set(Calendar.DAY_OF_MONTH, mDayOfMonth - 1);
         yesterday.set(Calendar.HOUR_OF_DAY, 0);
         yesterday.set(Calendar.MINUTE, 0);
         yesterday.set(Calendar.SECOND, 0);
         yesterday.set(Calendar.MILLISECOND, 0);
         val1 = yesterday.getTimeInMillis();

         yesterday.set(Calendar.YEAR, mYear);
         yesterday.set(Calendar.MONTH, mMonth);
         yesterday.set(Calendar.DAY_OF_MONTH, mDayOfMonth - 1);
         yesterday.set(Calendar.HOUR_OF_DAY, 23);
         yesterday.set(Calendar.MINUTE, 59);
         yesterday.set(Calendar.SECOND, 59);
         yesterday.set(Calendar.MILLISECOND, 999);
         val2 = yesterday.getTimeInMillis();
         Pair<Long, Long> p = new Pair<Long, Long>(val1,val2);
         return p;
     }

    public Pair<Long,Long> getLastCouple(int ID) {
        int offset = 0;
        switch(ID) {
            case UserFilterAnalyzer.PHRASE_LAST_COUPLE_OF_DAYS:
                offset = 3;
                break;
            case UserFilterAnalyzer.PHRASE_LAST_COUPLE_OF_WEEKS:
                offset = 15;
                break;
            case UserFilterAnalyzer.PHRASE_LAST_COUPLE_OF_MONTHS:
                offset = 62;
                break;
         }
         Long val1, val2;
         Calendar today = Calendar.getInstance((Locale.getDefault()));
         // reset hour, minutes, seconds and millis
         today.set(Calendar.YEAR, mYear);
         today.set(Calendar.MONTH, mMonth);
         today.set(Calendar.DAY_OF_MONTH, mDayOfMonth);
         today.set(Calendar.HOUR_OF_DAY, 23);
         today.set(Calendar.MINUTE, 59);
         today.set(Calendar.SECOND, 59);
         today.set(Calendar.MILLISECOND, 999);
         int dayOfYear = today.get(Calendar.DAY_OF_YEAR);
         val2 = today.getTimeInMillis(); // Remember val2 should be greater than val1
         today.set(Calendar.DAY_OF_YEAR, dayOfYear - offset);
         today.set(Calendar.HOUR_OF_DAY, 0);
         today.set(Calendar.MINUTE, 0);
         today.set(Calendar.SECOND, 0);
         today.set(Calendar.MILLISECOND, 0);
         val1 = today.getTimeInMillis();
         Pair<Long, Long> p = new Pair<Long, Long>(val1,val2);
         return p;
     }

    public Pair<Long,Long> getLastWeek() {
        Long val1, val2;
        Calendar today = Calendar.getInstance((Locale.getDefault()));
        int weekOfYear = today.get(Calendar.WEEK_OF_YEAR);
        today.set(Calendar.WEEK_OF_YEAR, weekOfYear - 1);
        today.set(Calendar.DAY_OF_WEEK, today.getFirstDayOfWeek());
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        int day_of_year = today.get(Calendar.DAY_OF_YEAR);
        val2 = today.getTimeInMillis();

        today.set(Calendar.DAY_OF_YEAR, day_of_year - 7);
        today.set(Calendar.HOUR_OF_DAY, 23);
        today.set(Calendar.MINUTE, 59);
        today.set(Calendar.SECOND, 59);
        today.set(Calendar.MILLISECOND, 999);
        val1 = today.getTimeInMillis();
        Pair<Long, Long> p = new Pair<Long, Long>(val1,val2);
        return p;
     }

    public Pair<Long,Long> getThisWeek() {
        Long val1, val2;
        Calendar today = Calendar.getInstance((Locale.getDefault()));
        today.set(Calendar.DAY_OF_WEEK, today.getFirstDayOfWeek());
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        int day_of_year = today.get(Calendar.DAY_OF_YEAR);
        val1 = today.getTimeInMillis();

        today.set(Calendar.DAY_OF_YEAR, day_of_year + 7);
        today.set(Calendar.HOUR_OF_DAY, 23);
        today.set(Calendar.MINUTE, 59);
        today.set(Calendar.SECOND, 59);
        today.set(Calendar.MILLISECOND, 999);
        val2 = today.getTimeInMillis();
        Pair<Long, Long> p = new Pair<Long, Long>(val1,val2);
        return p;
     }

    public Pair<Long,Long> getLastMonth() {
        Long val1, val2;
        Calendar today = Calendar.getInstance((Locale.getDefault()));
        today.set(Calendar.MONTH, mMonth - 1);
        today.set(Calendar.DAY_OF_MONTH, 1);
        today.set(Calendar.HOUR_OF_DAY, 23);
        today.set(Calendar.MINUTE, 59);
        today.set(Calendar.SECOND, 59);
        today.set(Calendar.MILLISECOND, 999);
        val1 = today.getTimeInMillis();

        today.set(Calendar.DAY_OF_MONTH, today.getActualMaximum(Calendar.DAY_OF_MONTH));
        val2 = today.getTimeInMillis();
        Pair<Long, Long> p = new Pair<Long, Long>(val1,val2);
        return p;
     }

    public Pair<Long,Long> getThisMonth() {
        Long val1, val2;
        Calendar today = Calendar.getInstance((Locale.getDefault()));
        //today.set(Calendar.MONTH, mMonth);
        today.set(Calendar.DAY_OF_MONTH, 1);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        val1 = today.getTimeInMillis();

        today.set(Calendar.DAY_OF_MONTH, today.getActualMaximum(Calendar.DAY_OF_MONTH));
        today.set(Calendar.HOUR_OF_DAY, 23);
        today.set(Calendar.MINUTE, 59);
        today.set(Calendar.SECOND, 59);
        today.set(Calendar.MILLISECOND, 999);
        val2 = today.getTimeInMillis();
        Pair<Long, Long> p = new Pair<Long, Long>(val1,val2);
        return p;
     }

    private Pair<Long,Long> getPhraseRange(String eventDay, Integer month, Integer day) {
        Long val1, val2;
        Calendar today = Calendar.getInstance((Locale.getDefault()));
        //Integer currentMonth = today.get(Calendar.MONTH);
        Integer currentDay = today.get(Calendar.DAY_OF_MONTH);
        if (mMonth == month) {
            if (currentDay >= day ) {
                eventDay += mYear;
                val1 = getMilliSeconds(eventDay);
                val2 = val1 + 86400000;
                Pair<Long, Long> p = new Pair<Long, Long>(val1,val2);
                return p ;
            }
        }
        eventDay += mYear - 1;
        val1 = getMilliSeconds(eventDay);
        val2 = val1 + 86400000;
        Pair<Long, Long> p = new Pair<Long, Long>(val1,val2);
        return p ;

    }

    public Pair<Long,Long> getPharse(Integer phraseId) {
        switch(phraseId) {
            case UserFilterAnalyzer.PHRASE_NEWYEAR :
                return getPhraseRange("01-January-", Calendar.JANUARY, 01);
            case UserFilterAnalyzer.PHRASE_JULY_4TH :
                return getPhraseRange("04-July-", Calendar.JULY , 04);
            case UserFilterAnalyzer.PHRASE_CHRISTMAS_EVE :
                return getPhraseRange("24-December-", Calendar.DECEMBER ,24);
            case UserFilterAnalyzer.PHRASE_CHRISTMAS :
                return getPhraseRange("25-December-", Calendar.DECEMBER, 25);
            case UserFilterAnalyzer.PHRASE_BOXING_DAY :
                return getPhraseRange("26-December-", Calendar.DECEMBER, 26);
            case UserFilterAnalyzer.PHRASE_NEWYEARS_EVE :
                return getPhraseRange("31-December-", Calendar.DECEMBER, 31);
            default:
                return null;
        }
    }

    private long getMilliSeconds(String date) {
        long milliseconds = 0;
        SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy",Locale.getDefault());
        java.util.Date d = null;
        try {
            d = f.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (d != null)
          milliseconds = d.getTime();
        return milliseconds;
    }

    public Pair<Long,Long> getRange(Calendar cal1, Calendar cal2) {
       Long val1, val2;
       val1 = cal1.getTimeInMillis();
       val2 = cal2.getTimeInMillis();
       printDateAndTime(cal1);
       printDateAndTime(cal2);
       return new Pair<Long, Long>(val1,val2);
    }

    public void printDateAndTime(Calendar cal) {
        if (DEBUG) {
            //String date = "" + cal.get(Calendar.DAY_OF_MONTH) + ":" + cal.get(Calendar.MONTH) + ":" + cal.get(Calendar.YEAR);
            String date = "" + cal.get(Calendar.DAY_OF_MONTH) + ":" + cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) + ":" +cal.get(Calendar.YEAR);
            String time = "" + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);

            Log.d(TAG , date + " " + time);
         }
    }

    public Date convertToDate(String input) {
        Date date = null;
        if(null == input) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
        try {
            format.setLenient(false);
            date = (Date) format.parse(input);
        }  catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }
    @Override
    public String toString() {
     // TODO Auto-generated method stub
      return null;
    }

}