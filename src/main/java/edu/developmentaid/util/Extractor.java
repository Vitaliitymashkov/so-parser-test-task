package edu.developmentaid.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Extractor {
    public static Set<String> getElementsCollection(String commaSeparatedElements) {
        String[] elements = commaSeparatedElements.split(",");
        return new HashSet<>(List.of(elements));
    }

    public static int getUnixTime(String dateFromConfig, int defaultValue) {
        if (dateFromConfig.equalsIgnoreCase("NOW")) {
            return (int) (System.currentTimeMillis() / 1000L);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date date;
        try {
            date = format.parse(dateFromConfig);
            return (int) date.getTime();
        } catch (ParseException e) {
            if (dateFromConfig.equalsIgnoreCase("NOW")) {
                return (int) (System.currentTimeMillis() / 1000L);
            } else {
                return defaultValue;
            }
        }
    }
}
