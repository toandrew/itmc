package com.infthink.itmc.util;

public final class Util {

    public static String replaceString(String str, String oldStr, String newStr) {
        return str.replace(oldStr, newStr);
    }

    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0);
    }
    

    public static String secondTimeToString(int time) {
        if (time <= 0) {
            return "";
        }
        int hour = 0;
        int minute = 0;
        int second = 0;
        minute = time / 60;
        if (minute < 60) {
            second = time % 60;
            String str = "00:" + ((minute >= 10) ? minute : ("0" + minute)) + ":" + ((second >= 10) ? second : ("0" + second));
            return str;
        } else {
            hour = minute / 60;
            minute = minute % 60;
            second = time - hour * 3600 - minute * 60;
            String str = "" + ((hour >= 10) ? hour : ("0" + hour)) + ":" + ((minute >= 10) ? minute : ("0" + minute)) + ":" + ((second >= 10) ? second : ("0" + second));
            return str;
        }
    }


    public static String formatScore(double score) {
        // boolean bool = paramDouble < 0.0D;
        // int i = 0;
        // if (bool)
        // {
        // i = 1;
        // paramDouble = -paramDouble;
        // }
        // int j = (int)Math.round((float)Math.round(100.0D * paramDouble) /
        // 10.0F);
        // String str = j + "";
        // if (j == 0)
        // return "0.0";
        // StringBuilder localStringBuilder = new StringBuilder();
        // if (i != 0)
        // localStringBuilder.append("-");
        // if (str.length() == 1)
        // {
        // localStringBuilder.append("0.");
        // localStringBuilder.append(str);
        // }
        // while (true)
        // {
        // return localStringBuilder.toString();
        // localStringBuilder.append(str.substring(0, -1 + str.length()));
        // localStringBuilder.append(".");
        // localStringBuilder.append(str.charAt(-1 + str.length()));
        // }
        return "";
    }
}
