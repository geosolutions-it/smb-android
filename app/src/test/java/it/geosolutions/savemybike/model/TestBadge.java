package it.geosolutions.savemybike.model;

import junit.framework.Assert;

import org.junit.Test;

public class TestBadge {
    final String BADGE_NAME_NORMAL = "MY_BADGE_WITHOUT_ANY_ORDER_NUMBER";
    final String BADGE_NAME_ORLDER = "13456_" + BADGE_NAME_NORMAL;
    @Test
    public void testBadgeName() {

        Assert.assertEquals((new Badge(BADGE_NAME_NORMAL)).getName(), BADGE_NAME_NORMAL);
        Assert.assertEquals((new Badge(BADGE_NAME_ORLDER)).getName(), BADGE_NAME_NORMAL);
    }
}
