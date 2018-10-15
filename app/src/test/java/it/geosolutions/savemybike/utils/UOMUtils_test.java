package it.geosolutions.savemybike.utils;

import junit.framework.Assert;

import org.junit.Test;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class UOMUtils_test {
   @Test public void testFormat() {

       DecimalFormat format= new DecimalFormat();
       DecimalFormatSymbols symbols=format.getDecimalFormatSymbols();
       char sep=symbols.getDecimalSeparator();
       Assert.assertEquals(UOMUtils.format(0.0001),"100" +sep + "00 µ");
       Assert.assertEquals(UOMUtils.format(0.001),"1" +sep + "00 m");
       Assert.assertEquals(UOMUtils.format(0.01),"10" +sep + "00 m");
       Assert.assertEquals(UOMUtils.format(0.1),"100" +sep + "00 m");
       Assert.assertEquals(UOMUtils.format(1),"1" +sep + "00 ");
       Assert.assertEquals(UOMUtils.format(10),"10" +sep + "00 ");
       Assert.assertEquals(UOMUtils.format(100),"100" +sep + "00 ");
       Assert.assertEquals(UOMUtils.format(1000),"1" +sep + "00 k");
       Assert.assertEquals(UOMUtils.format(10000),"10" +sep + "00 k");
       Assert.assertEquals(UOMUtils.format(100000),"100" +sep + "00 k");
       Assert.assertEquals(UOMUtils.format(1000000),"1" +sep + "00 M");
       Assert.assertEquals(UOMUtils.format(1234567),"1" +sep + "23 M");
       // test trim decimal values
       Assert.assertEquals(UOMUtils.format(12340000),"12" +sep + "34 M");
       // test round values
       Assert.assertEquals(UOMUtils.format(12345000),"12" +sep + "35 M");
       // test custom format
       Assert.assertEquals(UOMUtils.format(12000000, "###.##"),"12 M");
       // test custom format and separator
       Assert.assertEquals(UOMUtils.format(12000000, "###.##", ""),"12M");

       // test 0 case
       Assert.assertEquals(UOMUtils.format(0, "###.##", ""),"0");
       Assert.assertEquals(UOMUtils.format(0, "##0.00", ""),"0" +sep + "00");


       // negative
       Assert.assertEquals(UOMUtils.format(-0.0001),"-100" +sep + "00 µ");
       Assert.assertEquals(UOMUtils.format(-0.001),"-1" +sep + "00 m");
       Assert.assertEquals(UOMUtils.format(-0.01),"-10" +sep + "00 m");
       Assert.assertEquals(UOMUtils.format(-0.1),"-100" +sep + "00 m");
       Assert.assertEquals(UOMUtils.format(1),"1" +sep + "00 ");
       Assert.assertEquals(UOMUtils.format(-10),"-10" +sep + "00 ");
       Assert.assertEquals(UOMUtils.format(-100),"-100" +sep + "00 ");
       Assert.assertEquals(UOMUtils.format(-1000),"-1" +sep + "00 k");
       Assert.assertEquals(UOMUtils.format(-10000),"-10" +sep + "00 k");
       Assert.assertEquals(UOMUtils.format(-100000),"-100" +sep + "00 k");
       Assert.assertEquals(UOMUtils.format(-1000000),"-1" +sep + "00 M");
       Assert.assertEquals(UOMUtils.format(-1234567),"-1" +sep + "23 M");
       // test trim decimal values
       Assert.assertEquals(UOMUtils.format(-12340000),"-12" +sep + "34 M");
       // test round values
       Assert.assertEquals(UOMUtils.format(-12345000),"-12" +sep + "35 M");
   }
}
