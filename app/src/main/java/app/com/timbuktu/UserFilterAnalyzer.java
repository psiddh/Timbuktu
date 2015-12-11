package app.com.timbuktu;

// TBD: Use well-known NLP cloud based APIs for named entity recognition
// For now lets use this dumb version

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import app.com.timbuktu.util.LogUtils;

public class UserFilterAnalyzer implements LogUtils {

    private String TAG = "SpickIt> UserFilterAnalyzer";

    private int   SINGLE_DAY_OFFSET_IN_MS = 86400000;

    private String mUserFilter = null;

    private String[] mWords ;

    private Context mContext;

    DataBaseManager mDbHelper;

    DateRangeManager mRangeMgr = new DateRangeManager();

    Map<String, Integer[]> mDateRangeKeyWord = new HashMap<String, Integer[]>();
    Map<String, Integer> mDateRangeConnectorKeyword = new HashMap<String, Integer>();

    private static final int KEYWORD_MONTH_NAME = 1;
    //private static final int KEYWORD_WEEKEND = 2;
    //private static final int KEYWORD_MONTH = 3;
    private static final int KEYWORD_MONTH_DAYS = 4;
    private static final int KEYWORD_YEAR = 5;
    private static final int KEYWORD_TODAY = 6;
    private static final int KEYWORD_SPECIAL = 7;
    private static final int KEYWORD_PHRASES = 8;
    private static final int KEYWORD_CONNECTOR_AND = 9;
    private static final int KEYWORD_CONNECTOR_TO = 10;
    //private static final int KEYWORD_UNKNOWN = 0xFF;

    public static final int PHRASE_TODAY = 1;
    public static final int PHRASE_YESTERDAY = 2;
    public static final int PHRASE_LAST_COUPLE_OF_DAYS = 3;
    public static final int PHRASE_LAST_COUPLE_OF_WEEKS = 4;
    public static final int PHRASE_LAST_COUPLE_OF_MONTHS = 5;
    public static final int PHRASE_LAST_WEEKEND = 6;
    public static final int PHRASE_LAST_WEEK = 7;
    public static final int PHRASE_THIS_WEEK = 8;
    public static final int PHRASE_LAST_MONTH = 9;
    public static final int PHRASE_THIS_MONTH = 10;
    public static final int PHRASE_CHRISTMAS = 11;
    public static final int PHRASE_CHRISTMAS_EVE = 12;
    public static final int PHRASE_BOXING_DAY = 13;
    public static final int PHRASE_NEWYEAR = 14;
    public static final int PHRASE_NEWYEARS_EVE = 15;
    //public static final int PHRASE_CHRISTMAS_AND_NEWYEAR = 15;
    public static final int PHRASE_THANKSGIVING = 16;
    public static final int PHRASE_JULY_4TH = 17;

    private int mDateStartIndex = -1;
    private int mDateEndIndex = -1;
    //private int mPlaceIndex = -1;

    private int    MATCH_SUCCESS  = 0;
    private int    MATCH_FAILURE  = 1;

    public static final int MATCH_STATE_NONE = 1000;
    public static final int MATCH_STATE_ONLY_DATES = 1001;
    public static final int MATCH_STATE_ONLY_PLACE = 1002;
    public static final int MATCH_STATE_DATES_AND_PLACE = 1003;
    public static final int MATCH_STATE_DATES_AND_PLACE_EXACT = 1004;
    public static final int MATCH_STATE_DATES_AND_UNKNOWN_PLACE_EXACT = 1005;
    public static final int MATCH_STATE_PHRASE_AND_PLACE_EXACT = 1007;

    private void initKeyWords() {
        // Month key words
        mDateRangeKeyWord.put("january", new Integer[] {KEYWORD_MONTH_NAME, 0});
        mDateRangeKeyWord.put("jan", new Integer[] {KEYWORD_MONTH_NAME, 0});
        mDateRangeKeyWord.put("february", new Integer[] {KEYWORD_MONTH_NAME, 1});
        mDateRangeKeyWord.put("feb", new Integer[] {KEYWORD_MONTH_NAME, 1});
        mDateRangeKeyWord.put("march", new Integer[] {KEYWORD_MONTH_NAME, 2});
        mDateRangeKeyWord.put("mar", new Integer[] {KEYWORD_MONTH_NAME, 2});
        mDateRangeKeyWord.put("april", new Integer[] {KEYWORD_MONTH_NAME, 3});
        mDateRangeKeyWord.put("apr", new Integer[] {KEYWORD_MONTH_NAME, 3});
        mDateRangeKeyWord.put("may", new Integer[] {KEYWORD_MONTH_NAME, 4});
        mDateRangeKeyWord.put("june", new Integer[] {KEYWORD_MONTH_NAME, 5});
        mDateRangeKeyWord.put("jun", new Integer[] {KEYWORD_MONTH_NAME, 5});
        mDateRangeKeyWord.put("july", new Integer[] {KEYWORD_MONTH_NAME, 6});
        mDateRangeKeyWord.put("jul", new Integer[] {KEYWORD_MONTH_NAME, 6});
        mDateRangeKeyWord.put("august", new Integer[] {KEYWORD_MONTH_NAME, 7});
        mDateRangeKeyWord.put("aug", new Integer[] {KEYWORD_MONTH_NAME, 7});
        mDateRangeKeyWord.put("september", new Integer[] {KEYWORD_MONTH_NAME, 8});
        mDateRangeKeyWord.put("sep", new Integer[] {KEYWORD_MONTH_NAME, 8});
        mDateRangeKeyWord.put("october", new Integer[] {KEYWORD_MONTH_NAME, 9});
        mDateRangeKeyWord.put("oct", new Integer[] {KEYWORD_MONTH_NAME, 9});
        mDateRangeKeyWord.put("november", new Integer[] {KEYWORD_MONTH_NAME, 10});
        mDateRangeKeyWord.put("nov", new Integer[] {KEYWORD_MONTH_NAME, 10});
        mDateRangeKeyWord.put("december", new Integer[] {KEYWORD_MONTH_NAME, 11});
        mDateRangeKeyWord.put("dec", new Integer[] {KEYWORD_MONTH_NAME, 11});

        // Days of Month related keywords
        mDateRangeKeyWord.put("st", new Integer[] {KEYWORD_MONTH_DAYS, -1}); // treat this as day for now!
        mDateRangeKeyWord.put("1", new Integer[] {KEYWORD_MONTH_DAYS, 1});
        mDateRangeKeyWord.put("1 st", new Integer[] {KEYWORD_MONTH_DAYS, 1});
        mDateRangeKeyWord.put("1st", new Integer[] {KEYWORD_MONTH_DAYS, 1});
        mDateRangeKeyWord.put("2", new Integer[] {KEYWORD_MONTH_DAYS, 2});
        mDateRangeKeyWord.put("2nd", new Integer[] {KEYWORD_MONTH_DAYS, 2});
        mDateRangeKeyWord.put("3", new Integer[] {KEYWORD_MONTH_DAYS, 3});
        mDateRangeKeyWord.put("3rd", new Integer[] {KEYWORD_MONTH_DAYS, 3});
        mDateRangeKeyWord.put("4", new Integer[] {KEYWORD_MONTH_DAYS, 4});
        mDateRangeKeyWord.put("4th", new Integer[] {KEYWORD_MONTH_DAYS, 4});
        mDateRangeKeyWord.put("5", new Integer[] {KEYWORD_MONTH_DAYS, 5});
        mDateRangeKeyWord.put("5th", new Integer[] {KEYWORD_MONTH_DAYS, 5});
        mDateRangeKeyWord.put("6", new Integer[] {KEYWORD_MONTH_DAYS, 6});
        mDateRangeKeyWord.put("6th", new Integer[] {KEYWORD_MONTH_DAYS, 6});
        mDateRangeKeyWord.put("7", new Integer[] {KEYWORD_MONTH_DAYS, 7});
        mDateRangeKeyWord.put("7th", new Integer[] {KEYWORD_MONTH_DAYS, 7});
        mDateRangeKeyWord.put("8", new Integer[] {KEYWORD_MONTH_DAYS, 8});
        mDateRangeKeyWord.put("8th", new Integer[] {KEYWORD_MONTH_DAYS, 8});
        mDateRangeKeyWord.put("9", new Integer[] {KEYWORD_MONTH_DAYS, 9});
        mDateRangeKeyWord.put("9th", new Integer[] {KEYWORD_MONTH_DAYS, 9});
        mDateRangeKeyWord.put("10", new Integer[] {KEYWORD_MONTH_DAYS, 10});
        mDateRangeKeyWord.put("10th", new Integer[] {KEYWORD_MONTH_DAYS, 10});
        mDateRangeKeyWord.put("11", new Integer[] {KEYWORD_MONTH_DAYS, 11});
        mDateRangeKeyWord.put("11th", new Integer[] {KEYWORD_MONTH_DAYS, 11});
        mDateRangeKeyWord.put("12", new Integer[] {KEYWORD_MONTH_DAYS, 12});
        mDateRangeKeyWord.put("12th", new Integer[] {KEYWORD_MONTH_DAYS, 12});
        mDateRangeKeyWord.put("13", new Integer[] {KEYWORD_MONTH_DAYS, 13});
        mDateRangeKeyWord.put("13th", new Integer[] {KEYWORD_MONTH_DAYS, 13});
        mDateRangeKeyWord.put("14", new Integer[] {KEYWORD_MONTH_DAYS, 14});
        mDateRangeKeyWord.put("14th", new Integer[] {KEYWORD_MONTH_DAYS, 14});
        mDateRangeKeyWord.put("15", new Integer[] {KEYWORD_MONTH_DAYS, 15});
        mDateRangeKeyWord.put("15th", new Integer[] {KEYWORD_MONTH_DAYS, 15});
        mDateRangeKeyWord.put("16", new Integer[] {KEYWORD_MONTH_DAYS, 16});
        mDateRangeKeyWord.put("16th", new Integer[] {KEYWORD_MONTH_DAYS, 16});
        mDateRangeKeyWord.put("17", new Integer[] {KEYWORD_MONTH_DAYS, 17});
        mDateRangeKeyWord.put("17th", new Integer[] {KEYWORD_MONTH_DAYS, 17});
        mDateRangeKeyWord.put("18", new Integer[] {KEYWORD_MONTH_DAYS, 18});
        mDateRangeKeyWord.put("18th", new Integer[] {KEYWORD_MONTH_DAYS, 18});
        mDateRangeKeyWord.put("19", new Integer[] {KEYWORD_MONTH_DAYS, 19});
        mDateRangeKeyWord.put("19th", new Integer[] {KEYWORD_MONTH_DAYS, 19});
        mDateRangeKeyWord.put("20", new Integer[] {KEYWORD_MONTH_DAYS, 20});
        mDateRangeKeyWord.put("20th", new Integer[] {KEYWORD_MONTH_DAYS, 20});
        mDateRangeKeyWord.put("21", new Integer[] {KEYWORD_MONTH_DAYS, 21});
        mDateRangeKeyWord.put("21 st", new Integer[] {KEYWORD_MONTH_DAYS, 21});
        mDateRangeKeyWord.put("22", new Integer[] {KEYWORD_MONTH_DAYS, 22});
        mDateRangeKeyWord.put("22nd", new Integer[] {KEYWORD_MONTH_DAYS, 22});
        mDateRangeKeyWord.put("23", new Integer[] {KEYWORD_MONTH_DAYS, 23});
        mDateRangeKeyWord.put("23rd", new Integer[] {KEYWORD_MONTH_DAYS, 23});
        mDateRangeKeyWord.put("24", new Integer[] {KEYWORD_MONTH_DAYS, 24});
        mDateRangeKeyWord.put("24th", new Integer[] {KEYWORD_MONTH_DAYS, 24});
        mDateRangeKeyWord.put("25", new Integer[] {KEYWORD_MONTH_DAYS, 25});
        mDateRangeKeyWord.put("25th", new Integer[] {KEYWORD_MONTH_DAYS, 25});
        mDateRangeKeyWord.put("26", new Integer[] {KEYWORD_MONTH_DAYS, 26});
        mDateRangeKeyWord.put("26th", new Integer[] {KEYWORD_MONTH_DAYS, 26});
        mDateRangeKeyWord.put("27", new Integer[] {KEYWORD_MONTH_DAYS, 27});
        mDateRangeKeyWord.put("27th", new Integer[] {KEYWORD_MONTH_DAYS, 27});
        mDateRangeKeyWord.put("28", new Integer[] {KEYWORD_MONTH_DAYS, 28});
        mDateRangeKeyWord.put("28th", new Integer[] {KEYWORD_MONTH_DAYS, 28});
        mDateRangeKeyWord.put("29", new Integer[] {KEYWORD_MONTH_DAYS, 29});
        mDateRangeKeyWord.put("29th", new Integer[] {KEYWORD_MONTH_DAYS, 29});
        mDateRangeKeyWord.put("30", new Integer[] {KEYWORD_MONTH_DAYS, 30});
        mDateRangeKeyWord.put("30th", new Integer[] {KEYWORD_MONTH_DAYS, 30});
        mDateRangeKeyWord.put("31", new Integer[] {KEYWORD_MONTH_DAYS, 31});
        mDateRangeKeyWord.put("31 st", new Integer[] {KEYWORD_MONTH_DAYS, 31});

        // Year keywords
        mDateRangeKeyWord.put("2000",new Integer[] {KEYWORD_YEAR, 2000});
        mDateRangeKeyWord.put("2001",new Integer[] {KEYWORD_YEAR, 2001});
        mDateRangeKeyWord.put("2002",new Integer[] {KEYWORD_YEAR, 2002});
        mDateRangeKeyWord.put("2003",new Integer[] {KEYWORD_YEAR, 2003});
        mDateRangeKeyWord.put("2004",new Integer[] {KEYWORD_YEAR, 2004});
        mDateRangeKeyWord.put("2005",new Integer[] {KEYWORD_YEAR, 2005});
        mDateRangeKeyWord.put("2006",new Integer[] {KEYWORD_YEAR, 2006});
        mDateRangeKeyWord.put("2007",new Integer[] {KEYWORD_YEAR, 2007});
        mDateRangeKeyWord.put("2008",new Integer[] {KEYWORD_YEAR, 2008});
        mDateRangeKeyWord.put("2009",new Integer[] {KEYWORD_YEAR, 2009});
        mDateRangeKeyWord.put("2010",new Integer[] {KEYWORD_YEAR, 2010});
        mDateRangeKeyWord.put("2011",new Integer[] {KEYWORD_YEAR, 2011});
        mDateRangeKeyWord.put("2012",new Integer[] {KEYWORD_YEAR, 2012});
        mDateRangeKeyWord.put("2013",new Integer[] {KEYWORD_YEAR, 2013});
        mDateRangeKeyWord.put("2014",new Integer[] {KEYWORD_YEAR, 2014});
        mDateRangeKeyWord.put("2015",new Integer[] {KEYWORD_YEAR, 2015});
        mDateRangeKeyWord.put("2016",new Integer[] {KEYWORD_YEAR, 2016});
        mDateRangeKeyWord.put("2017",new Integer[] {KEYWORD_YEAR, 2017});
        mDateRangeKeyWord.put("2018",new Integer[] {KEYWORD_YEAR, 2018});
        mDateRangeKeyWord.put("2019",new Integer[] {KEYWORD_YEAR, 2009});
        mDateRangeKeyWord.put("2020",new Integer[] {KEYWORD_YEAR, 2020});
        mDateRangeKeyWord.put("Year",new Integer[] {KEYWORD_YEAR, -1});

        // Today keywords
        mDateRangeKeyWord.put("today",new Integer[] {KEYWORD_TODAY, -1});
        mDateRangeKeyWord.put("today's",new Integer[] {KEYWORD_TODAY, -1});

        // Special words
        mDateRangeKeyWord.put("till",new Integer[] {KEYWORD_SPECIAL, -1});
        mDateRangeKeyWord.put("since",new Integer[] {KEYWORD_SPECIAL, -1});
        mDateRangeKeyWord.put("to",new Integer[] {KEYWORD_SPECIAL, -1});
        mDateRangeKeyWord.put("and",new Integer[] {KEYWORD_SPECIAL, -1});
        mDateRangeKeyWord.put("st",new Integer[] {KEYWORD_SPECIAL, -1});

        // phrases
        mDateRangeKeyWord.put("today",new Integer[] {KEYWORD_PHRASES, PHRASE_TODAY});
        mDateRangeKeyWord.put("today's pictures",new Integer[] {KEYWORD_PHRASES, PHRASE_TODAY});
        mDateRangeKeyWord.put("yesterday",new Integer[] {KEYWORD_PHRASES, PHRASE_YESTERDAY});
        mDateRangeKeyWord.put("yesterday's",new Integer[] {KEYWORD_PHRASES, PHRASE_YESTERDAY});
        mDateRangeKeyWord.put("last couple of weeks",new Integer[] {KEYWORD_PHRASES, PHRASE_LAST_COUPLE_OF_WEEKS});
        mDateRangeKeyWord.put("couple of weeks back",new Integer[] {KEYWORD_PHRASES, PHRASE_LAST_COUPLE_OF_WEEKS});
        mDateRangeKeyWord.put("couple of weeks ago",new Integer[] {KEYWORD_PHRASES, PHRASE_LAST_COUPLE_OF_WEEKS});
        mDateRangeKeyWord.put("couple of days back",new Integer[] {KEYWORD_PHRASES, PHRASE_LAST_COUPLE_OF_DAYS});
        mDateRangeKeyWord.put("couple of days ago",new Integer[] {KEYWORD_PHRASES, PHRASE_LAST_COUPLE_OF_DAYS});
        mDateRangeKeyWord.put("last couple of days",new Integer[] {KEYWORD_PHRASES, PHRASE_LAST_COUPLE_OF_DAYS});
        mDateRangeKeyWord.put("this week", new Integer[] {KEYWORD_PHRASES, PHRASE_THIS_WEEK});
        mDateRangeKeyWord.put("last week",new Integer[] {KEYWORD_PHRASES, PHRASE_LAST_WEEK});
        mDateRangeKeyWord.put("last weekend",new Integer[] {KEYWORD_PHRASES, PHRASE_LAST_WEEKEND});
        mDateRangeKeyWord.put("last month",new Integer[] {KEYWORD_PHRASES, PHRASE_LAST_MONTH});
        mDateRangeKeyWord.put("this month", new Integer[] {KEYWORD_PHRASES, PHRASE_THIS_MONTH});
        mDateRangeKeyWord.put("last couple of months",new Integer[] {KEYWORD_PHRASES, PHRASE_LAST_COUPLE_OF_MONTHS});
        mDateRangeKeyWord.put("couple of months back",new Integer[] {KEYWORD_PHRASES, PHRASE_LAST_COUPLE_OF_MONTHS});
        mDateRangeKeyWord.put("couple of months ago",new Integer[] {KEYWORD_PHRASES, PHRASE_LAST_COUPLE_OF_MONTHS});
        mDateRangeKeyWord.put("christmas",new Integer[] {KEYWORD_PHRASES, PHRASE_CHRISTMAS});
        mDateRangeKeyWord.put("christmas eve",new Integer[] {KEYWORD_PHRASES, PHRASE_CHRISTMAS_EVE});
        mDateRangeKeyWord.put("boxing day",new Integer[] {KEYWORD_PHRASES, PHRASE_BOXING_DAY});
        mDateRangeKeyWord.put("new years",new Integer[] {KEYWORD_PHRASES, PHRASE_NEWYEAR});
        mDateRangeKeyWord.put("new year",new Integer[] {KEYWORD_PHRASES, PHRASE_NEWYEAR});
        mDateRangeKeyWord.put("new year's day",new Integer[] {KEYWORD_PHRASES, PHRASE_NEWYEAR});
        mDateRangeKeyWord.put("new years eve",new Integer[] {KEYWORD_PHRASES, PHRASE_NEWYEARS_EVE});
        mDateRangeKeyWord.put("new year eve",new Integer[] {KEYWORD_PHRASES, PHRASE_NEWYEARS_EVE});
        //mDateRangeKeyWord.put("christmas and new year",new Integer[] {KEYWORD_PHRASES, PHRASE_CHRISTMAS_AND_NEWYEAR});
        //mDateRangeKeyWord.put("thanksgiving",new Integer[] {KEYWORD_PHRASES, PHRASE_THANKSGIVING});
        mDateRangeKeyWord.put("4th of July",new Integer[] {KEYWORD_PHRASES, PHRASE_JULY_4TH});

        // Connector key words
        mDateRangeConnectorKeyword.put("and", KEYWORD_CONNECTOR_AND);
        mDateRangeConnectorKeyword.put("to", KEYWORD_CONNECTOR_TO);
    }

    public UserFilterAnalyzer(Context context, String filter) {
        if (filter == null) filter = "";
        mUserFilter = filter;
        mWords = mUserFilter.split("\\s+");
        if (DEBUG) {
            for (int i = 0; i < mWords.length; i++) {
                if (DEBUG) Log.d(TAG, "Word "+ i+1 + " :" + mWords[i]);
            }
        }
        for (int i = 0; i < mWords.length; i++) {
            mWords[i]= mWords[i].replace(" +", " ");
        }
        initKeyWords();
        mContext = context;
        mDbHelper = DataBaseManager.getInstance(mContext);
    }

    public boolean isPrepositionKeywordFoundBeforeFilter(String compareString, boolean place) {
        int index = -1;
        int retry = 0;
        int currentIndex = 0;
        boolean foundPreposition = false;

        if (compareString == null) return false;
        String concat = "";
        for (int i = 0; i < mWords.length; i++) {
          concat += mWords[i] + " ";
          for (int j = i+1; j < mWords.length; j++) {
            concat += mWords[j] + " ";
            if(concat.toLowerCase().contains(compareString.toLowerCase())) {
              index = j;
              break;
            }
            if (j+1 == mWords.length) {
                concat = "";
            }
          }
          if(index != -1) {
              break;
          }
        }

        if (-1 == index) return foundPreposition;
        currentIndex = index;
        //if(place)
          //mPlaceIndex = index;

        // Now we have index
        do {
            if ((currentIndex - 1) >= 0) {
                if (isFillerWord(mWords[currentIndex - 1])) {
                    currentIndex--;
                    retry++;
                    continue;
                }
                if (isWordAPreposition(mWords[currentIndex - 1])) {
                    foundPreposition = true;
                    break;
                }
                currentIndex--;
                retry++;
                continue;
            }
        } while (currentIndex > 0 && retry < 6 && currentIndex < mWords.length);  // value 3 for fault tolerance.. yeah I know
            //if (DEBUG && (currentIndex - 1) >= 0 )
                //Log.d(TAG, "Found preposition at index " + (currentIndex - 1) + " Word : "  + mWords[currentIndex - 1]);
            return foundPreposition;
        }

        private boolean isFillerWord(String word){
        String fillers[] = {"A", "AN", "THE"};
        for (String s : fillers) {
            if (s.equalsIgnoreCase(word))
            return true;
             }
        return false;
    }

    private boolean isWordAPreposition(String word){
        String fillers[] = {"At", "IN", "FROM", "AROUND", "NEAR", "NEAR TO", "@"};
        for (String s : fillers) {
            if (s.equalsIgnoreCase(word))
                return true;
         }
         return false;
    }

    public int compareUserFilterForCity(String compareString) {
        //if (DEBUG) Log.d(TAG, "compareUserFilterForCity String : " + compareString);
        String concat = "";
        for (int i = 0; i < mWords.length; i++) {
          concat += mWords[i] + " ";
          //if (DEBUG) Log.d(TAG, "First Index : " + i + " - " + concat);
            for (int j = i+1; j < mWords.length; j++) {
              concat += mWords[j] + " ";
              //if (DEBUG) Log.d(TAG, "Second Index : " + j + " - " + concat);
              if(concat.toLowerCase().contains(compareString.toLowerCase())) {
              //if (DEBUG) Log.d(TAG, "***** Place Matched - " + compareString);
                  return MATCH_SUCCESS;
              }
              if (j+1 == mWords.length) {
                 // if (DEBUG) Log.d(TAG, "**** Reset ****");
                  concat = "";
              }
            }
          if(concat.toLowerCase().contains(compareString.toLowerCase())) {
        //if (DEBUG) Log.d(TAG, "***** Place Matched - " + compareString);
            return MATCH_SUCCESS;
          }
        }
        return MATCH_FAILURE;
    }

    public Pair<Long,Long> getRangeForSingleDateIfValid(Calendar range1, Calendar range2) {
        long offset = 1;
        if ((range1.isSet(Calendar.MONTH)) && (range1.isSet(Calendar.DAY_OF_MONTH))) {
          range1.set(Calendar.YEAR,mRangeMgr.getCurrentYear());
        } else if ((range1.isSet(Calendar.MONTH)) && (range1.isSet(Calendar.YEAR))) {
           range1.set(Calendar.DAY_OF_MONTH,1);
           offset = range1.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else if ((range1.isSet(Calendar.YEAR)) && (range1.isSet(Calendar.DAY_OF_MONTH))) {
           range1.set(Calendar.MONTH,range1.get(mRangeMgr.getCurrentMonth()));
        } else {
            // ONLY month is set. Ex:- May
            if (range1.isSet(Calendar.MONTH) && !range1.isSet(Calendar.DAY_OF_MONTH) && !range1.isSet(Calendar.YEAR)) {
                range1.set(Calendar.YEAR,mRangeMgr.getCurrentYear());
                range1.set(Calendar.DAY_OF_MONTH,1);
                offset = range1.getActualMaximum(Calendar.DAY_OF_MONTH);
            } else if (!range1.isSet(Calendar.MONTH) && !range1.isSet(Calendar.DAY_OF_MONTH) && range1.isSet(Calendar.YEAR))  {
                // Only YEAR is set Ex:- 2013
                int year = mRangeMgr.getCurrentYear();
                boolean isLeapYear = ((year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0)));
                offset = isLeapYear ? 366 : 365;
                range1.set(Calendar.MONTH,Calendar.JANUARY);
                range1.set(Calendar.DAY_OF_MONTH,1);

            }  else if (!range1.isSet(Calendar.MONTH) && range1.isSet(Calendar.DAY_OF_MONTH) && !range1.isSet(Calendar.YEAR))  {
                // Only DAY is set Ex:- 1st -- does not make sense.. but for completeness, lets assume something
                   range1.set(Calendar.YEAR,mRangeMgr.getCurrentYear());
                   range1.set(Calendar.MONTH,mRangeMgr.getCurrentMonth());
               } else {
                return null;
            }
        }
        // Looks like a valid single date at this point
        long val1 = range1.getTimeInMillis();
        long val2 = val1 + SINGLE_DAY_OFFSET_IN_MS * (offset);
        Pair<Long, Long> p = new Pair<Long, Long>(val1,val2);
        return p;
    }

    public Pair<Long,Long> getDateRange(String compareString) {
        //if (DEBUG) Log.d(TAG, "getDateRange String : " + compareString);
        int index = 0;
        int[] validRange = {0,0,0};
        boolean isSecondRange = false;
        int unknownCnt = 0;
        Calendar range1 = getNewCalObj(true);
        Calendar range2 = getNewCalObj(false);

        Pair<Long, Long> p = getPhraseDateRangeFromUserFilterIfExists();
        if (p!= null)
            return p;
        for (index = 0; index < mWords.length; index++) {
            if(!mDateRangeKeyWord.containsKey(mWords[index].toLowerCase())) {
                continue;
            } else {
                if (mDateRangeConnectorKeyword.containsKey(mWords[index].toLowerCase())) {
                    isSecondRange |= isCalendarObjSet(range1);
                    continue;
                }
            }
            Integer[] keyword_Val = mDateRangeKeyWord.get(mWords[index].toLowerCase());
            if (keyword_Val == null) {
              return null;
            }
            switch (keyword_Val[0]) {
              case KEYWORD_YEAR :
                  if (mDateStartIndex == -1)
                      mDateStartIndex = index;
                  mDateEndIndex = index;
                  if (!range1.isSet(Calendar.YEAR) && !isSecondRange )
                      range1.set(Calendar.YEAR,keyword_Val[1]);
                  else {
                      isSecondRange = true;
                      range2.set(Calendar.YEAR,keyword_Val[1]);
                  }
                  validRange[0]++;
                  break;
              case KEYWORD_MONTH_NAME :
                  if (mDateStartIndex == -1)
                      mDateStartIndex = index;
                  mDateEndIndex = index;
                  if (!range1.isSet(Calendar.MONTH) && !isSecondRange)
                      range1.set(Calendar.MONTH,keyword_Val[1]);
                  else {
                    isSecondRange = true;
                    range2.set(Calendar.MONTH,keyword_Val[1]);
                  }
                  validRange[1]++;
                  break;
              case KEYWORD_MONTH_DAYS :
                  if (mDateStartIndex == -1)
                      mDateStartIndex = index;
                  mDateEndIndex = index;
                  if (!range1.isSet(Calendar.DAY_OF_MONTH)&& !isSecondRange )
                      range1.set(Calendar.DAY_OF_MONTH,keyword_Val[1]);
                  else {
                    isSecondRange = true;
                    range2.set(Calendar.DAY_OF_MONTH,keyword_Val[1]);
                  }
                  validRange[2]++;
                  break;
              case KEYWORD_PHRASES:
              case KEYWORD_SPECIAL:
                  //if (DEBUG) Log.d(TAG, "Word : " + mWords[index] + " " +  getStringFromKeyWord(keyword_Val));
                  break;
              default:
                  //if (DEBUG && (mWords[index] != null)) Log.d(TAG, mWords[index] + " - Unknown Cnt : " + unknownCnt);
                  // Uh! dumb, lets stick to this fault tolerance for now.
                  if(++unknownCnt > 3) return null;
                  break;
            }
        }

        if (!(validRange[0] > 1 || validRange[1] > 1 || validRange[2] > 1)) {
          p = getRangeForSingleDateIfValid(range1, range2);
          return p;
        }

        if (range1.isSet(Calendar.YEAR) && !range2.isSet(Calendar.YEAR)) {
            int thisYear = range1.get(Calendar.YEAR);
            range2.set(Calendar.YEAR,thisYear);
        }

        if (range2.isSet(Calendar.YEAR) && !range1.isSet(Calendar.YEAR)) {
            int thisYear = range2.get(Calendar.YEAR);
            range1.set(Calendar.YEAR,thisYear);
            if (range1.getTimeInMillis() > range2.getTimeInMillis()) {
                range1.add(Calendar.YEAR, -1);
            }
        }

        if (!range1.isSet(Calendar.YEAR) && !range2.isSet(Calendar.YEAR)) {
            range1.set(Calendar.YEAR,mRangeMgr.getCurrentYear());
            range2.set(Calendar.YEAR,mRangeMgr.getCurrentYear());
        }

        if (!range1.isSet(Calendar.MONTH))
            range1.set(Calendar.MONTH,0);
        if (!range2.isSet(Calendar.MONTH)) {
            // Now this can be an interesting usecase, Say "February 15th and 16th"
            if (range2.isSet(Calendar.DAY_OF_MONTH) && range1.isSet(Calendar.DAY_OF_MONTH)) {
                int thisMonth = range1.get(Calendar.MONTH);
                range2.set(Calendar.MONTH,thisMonth);
            } else {
                range2.set(Calendar.MONTH,0);
            }
        }

        if (!range1.isSet(Calendar.DAY_OF_MONTH))
            range1.set(Calendar.DAY_OF_MONTH,1);
        if (!range2.isSet(Calendar.DAY_OF_MONTH))
            range2.set(Calendar.DAY_OF_MONTH,1);

        if (0 == range1.compareTo(range2)) {
            //return null;
        }

        if (range1.getTimeInMillis() > range2.getTimeInMillis()) {
          return mRangeMgr.getRange(range2, range1);
        }

        p = mRangeMgr.getRange(range1, range2);
        return p;
    }

    private Integer getPhraseIdIfExistsInUserFilter() {
        Iterator<Entry<String, Integer[]>> iterator = mDateRangeKeyWord.entrySet().iterator();
        while(iterator.hasNext()){
           Entry<String, Integer[]> entry = iterator.next();
           Integer[] vals = entry.getValue();
           if (vals[0] == KEYWORD_PHRASES) {
               String phrase = entry.getKey();
               if (mUserFilter.toLowerCase().contains(phrase.toLowerCase())) {
                   if (vals[1] == PHRASE_LAST_WEEK) {
                       if (mUserFilter.toLowerCase().contains("last weekend")) {
                           return PHRASE_LAST_WEEKEND;
                       }
                   }

                   if (vals[1] == PHRASE_CHRISTMAS) {
                       /*if (mUserFilter.toLowerCase().contains("christmas and new year")) {
                           return PHRASE_CHRISTMAS_AND_NEWYEAR;
                       }*/

                       if (mUserFilter.toLowerCase().contains("christmas eve")) {
                           return PHRASE_CHRISTMAS_EVE;
                       }
                   }

                   return vals[1];
               }
           }
        }
        return -1;
    }

    public String getPhraseIfExistsInUserFilter() {
        Iterator<Entry<String, Integer[]>> iterator = mDateRangeKeyWord.entrySet().iterator();
        while(iterator.hasNext()){
           Entry<String, Integer[]> entry = iterator.next();
           Integer[] vals = entry.getValue();
           if (vals[0] == KEYWORD_PHRASES) {
               String phrase = entry.getKey();
               if (mUserFilter.toLowerCase().contains(phrase.toLowerCase())) {
                   // Dirty as hell! Live with this for now!

                   if (vals[1] == PHRASE_LAST_WEEK) {
                       if (mUserFilter.toLowerCase().contains("last weekend")) {
                           return "last weekend";
                       }
                   }

                   if (vals[1] == PHRASE_CHRISTMAS) {
                       /*if (mUserFilter.toLowerCase().contains("christmas and new year")) {
                           return "christmas and new year";
                       }*/

                       if (mUserFilter.toLowerCase().contains("christmas eve")) {
                           return "christmas eve";
                       }
                   }
                   return phrase;
               }
           }
        }
        return null;
    }

    public Pair<Long,Long>  getPhraseDateRangeFromUserFilterIfExists() {
        Integer phraseId = getPhraseIdIfExistsInUserFilter();
        switch(phraseId) {
            case PHRASE_TODAY :
                return mRangeMgr.getToday();
            case PHRASE_YESTERDAY:
                return mRangeMgr.getYesterday();
            case PHRASE_LAST_COUPLE_OF_DAYS :
            case PHRASE_LAST_COUPLE_OF_WEEKS :
            case PHRASE_LAST_COUPLE_OF_MONTHS :
                return mRangeMgr.getLastCouple(phraseId);
            case PHRASE_LAST_WEEKEND :
                return mRangeMgr.getLastWeekEnd();
            case PHRASE_LAST_WEEK :
                return mRangeMgr.getLastWeek();
            case PHRASE_THIS_WEEK :
                return mRangeMgr.getThisWeek();
            case PHRASE_LAST_MONTH :
                return mRangeMgr.getLastMonth();
            case PHRASE_THIS_MONTH :
                return mRangeMgr.getThisMonth();
            case PHRASE_CHRISTMAS :
            case PHRASE_CHRISTMAS_EVE :
            case PHRASE_BOXING_DAY :
            case PHRASE_NEWYEAR :
            case PHRASE_NEWYEARS_EVE :
            case PHRASE_JULY_4TH :
                return mRangeMgr.getPharse(phraseId);
            case PHRASE_THANKSGIVING :
                break;
            default:
                break;
        }
        return null;
    }

    private boolean isCalendarObjSet(Calendar cal) {
        return (cal.isSet(Calendar.YEAR) || cal.isSet(Calendar.MONTH) || cal.isSet(Calendar.DAY_OF_MONTH));
    }

    /*private String getStringFromKeyWord(Integer[] keyword_Val) {
        switch(keyword_Val[0]) {
            case KEYWORD_MONTH_NAME:
                return "KEYWORD_MONTH_NAME";
            case KEYWORD_WEEKEND:
                return "KEYWORD_WEEKEND";
            case KEYWORD_MONTH:
                return "KEYWORD_MONTH";
            case KEYWORD_MONTH_DAYS:
            return "KEYWORD_MONTH_DAYS";
            case KEYWORD_YEAR:
                return "KEYWORD_YEAR";
            case KEYWORD_TODAY:
                return "KEYWORD_TODAY";
            case KEYWORD_PHRASES:
                return "KEYWORD_PHRASES";
            case KEYWORD_SPECIAL:
                return "KEYWORD_SPECIAL";
            case KEYWORD_UNKNOWN:
                return "KEYWORD_UNKNOWN";
            default:
                return "";
        }

    }*/

    /*private int computeDateRanges(Map<String, Integer> pattern) {
        Calendar range1 = getNewCalObj();
        Calendar range2 = getNewCalObj();

        int count = Collections.frequency(new ArrayList<Integer>(pattern.values()), KEYWORD_YEAR);
        if (count >= 2) {

        }

        for (Entry<String, Integer> entry : pattern.entrySet()) {
              String key = entry.getKey();
              Integer value = entry.getValue();
              switch (value) {
                case KEYWORD_YEAR:
                    break;
                default:
                    break;
              }
        }
        //col.
        return 0;
    }*/

    private Calendar getNewCalObj(boolean isFirstValue) {
        Calendar cal = Calendar.getInstance((Locale.getDefault()));
        // reset hour, minutes, seconds and millis
        cal.clear();
        if (isFirstValue) {
          cal.set(Calendar.HOUR_OF_DAY, 0);
          cal.set(Calendar.MINUTE, 0);
          cal.set(Calendar.SECOND, 0);
          cal.set(Calendar.MILLISECOND, 0);
        } else {
          cal.set(Calendar.HOUR_OF_DAY, 23);
          cal.set(Calendar.MINUTE, 59);
          cal.set(Calendar.SECOND, 59);
          cal.set(Calendar.MILLISECOND, 999);
        }
        return cal;
    }

    private boolean doesPrepositionOccurAfterDates() {
        boolean ret = false;
        if ((mDateEndIndex == -1) || (mDateEndIndex >= mWords.length))
            return ret;
        for (int index = mDateEndIndex; index < mWords.length; index++) {
            if (isWordAPreposition(mWords[index])) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    @Override
    public String toString() {
      // TODO Auto-generated method stub
      return null;
    }

    public String getStartDate() {
        if ((mDateStartIndex == -1) || (mDateStartIndex >= mWords.length))
            return null;
        return mWords[mDateStartIndex];
    }

    public int getMatchState() {
        int mMatchState = MATCH_STATE_NONE;
        Pair<Long, Long> mPairRange = getDateRange(mUserFilter);
        ArrayList<String> places = mDbHelper.retreiveAllPlacesFromStringIfExists(mUserFilter);
        String place = "";
        String country = "";
        String admin = "";
        boolean alsoMatchCity = false;
        boolean alsoMatchDate = false;
        boolean bMatchPlace = false;
        boolean bPhraseExists = (getPhraseIdIfExistsInUserFilter() != -1);

        if (places != null && places.size() > 0 ) {
            if (places.size() > 0) {
                place = places.get(0);
              }
                if (places.size() > 1) {
                    country = places.get(1);
                }
                if (places.size() > 2) {
                    admin = places.get(2);
                }

            if (place != null && place != "") {
                alsoMatchCity |= isPrepositionKeywordFoundBeforeFilter(place, true);
                bMatchPlace = true;
            }
            if (country != null && country != "") {
                alsoMatchCity |= isPrepositionKeywordFoundBeforeFilter(country, true);
                bMatchPlace = true;
            }
            if (admin != null && admin != "")  {
                alsoMatchCity |= isPrepositionKeywordFoundBeforeFilter(admin, true);
                bMatchPlace = true;
            }
        }

        alsoMatchDate = isPrepositionKeywordFoundBeforeFilter(getStartDate(), false);

        if ((mPairRange != null) && (bMatchPlace)) {
            if (alsoMatchCity || alsoMatchDate) {
                 mMatchState = MATCH_STATE_DATES_AND_PLACE_EXACT;
            } else {
                mMatchState = MATCH_STATE_DATES_AND_PLACE;
            }
        } else if (mPairRange != null) {
            // place is null here
            if (doesPrepositionOccurAfterDates()) {
            mMatchState = MATCH_STATE_DATES_AND_UNKNOWN_PLACE_EXACT;
            } else
                mMatchState = MATCH_STATE_ONLY_DATES;
        } else if (bMatchPlace && bPhraseExists) {
            mMatchState = MATCH_STATE_PHRASE_AND_PLACE_EXACT;
        } else if (bMatchPlace){
             mMatchState = MATCH_STATE_ONLY_PLACE;
        } else {
            // maintain status-quo
        }

        return mMatchState;
    }
}