package it.geosolutions.savemybike.utils;


import android.icu.util.Measure;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;


/**
 * Utility to format unit of measure
 *
 */
public class UOMUtils {
    /**
     * Measure container. Holds the value and the unit of measure.
     */
    public static class Measure {
            Double value;
            String uom;
    }
    // can be extended for custom units, (for instance hg, Tons...)
    protected static NavigableMap<Double, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(0.000000001, "n");
        suffixes.put(0.000001, "µ");
        suffixes.put(0.001, "m");
        suffixes.put(1., "");
        suffixes.put(1000.0, "k");
        suffixes.put(1000000.0, "M");
        suffixes.put(1000000000.0, "G");
        suffixes.put(1000000000000.0, "T");
        suffixes.put(1000000000000000.0, "P");
        suffixes.put(1000000000000000000.0, "E");
    }
    public static String format(double value) {
        return format(value, "###,###.00");
    }
    public static String format(double value, String format) {
        return format(value, format, " ");
    }
    public static String format(double value, String format, String separator) {
        DecimalFormat df = new DecimalFormat(format);

        Measure m = roundToUom(value);
        return df.format(m.value) + separator + m.uom;
    }

    /**
     * Rounds a value to the human readable unit of measure
     * @param value
     * @return a Measure with uom (multiplier). e.g. 1000  becomes (value:1, uom: "k")
     */
    public static Measure roundToUom(double value) {

        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Double.MIN_VALUE) return roundToUom(Double.MIN_VALUE + 1);
        if (value < 0) {
            // use the positive value, then invert to get the correct value
            Measure m = roundToUom(-value);
            m.value = -m.value;
            return  m;
        }


        Map.Entry<Double, String> e = suffixes.floorEntry(value);
        Measure m = new UOMUtils.Measure();
        if(e != null) {
            Double divideBy = e.getKey();
            String suffix = e.getValue();
            m.value = value / divideBy;
            m.uom = suffix;
        } else {

            m.value = value;
            m.uom = "";
        }

        return m;
    }
}