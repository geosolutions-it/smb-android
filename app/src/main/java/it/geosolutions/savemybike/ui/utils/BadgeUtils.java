package it.geosolutions.savemybike.ui.utils;

import java.util.HashMap;
import java.util.Map;

import it.geosolutions.savemybike.R;

public class BadgeUtils {
    // map of the title strings
    public static final Map<String, Integer> NAME_TITLE_MAP;
    static
    {
        // TITLE
        NAME_TITLE_MAP = new HashMap<String, Integer>();
        NAME_TITLE_MAP.put("new_user", R.string.badge_title_new_user);
        NAME_TITLE_MAP.put("data_collector_level0", R.string.badge_title_data_collector_level0);
        NAME_TITLE_MAP.put("data_collector_level1", R.string.badge_title_data_collector_level1);
        NAME_TITLE_MAP.put("data_collector_level2", R.string.badge_title_data_collector_level2);
        NAME_TITLE_MAP.put("data_collector_level3", R.string.badge_title_data_collector_level3);
        NAME_TITLE_MAP.put("biker_level1", R.string.badge_title_biker_level1);
        NAME_TITLE_MAP.put("biker_level2", R.string.badge_title_biker_level2);
        NAME_TITLE_MAP.put("biker_level3", R.string.badge_title_biker_level3);
        NAME_TITLE_MAP.put("public_mobility_level1", R.string.badge_title_public_mobility_level1);
        NAME_TITLE_MAP.put("public_mobility_level2", R.string.badge_title_public_mobility_level2);
        NAME_TITLE_MAP.put("public_mobility_level3", R.string.badge_title_public_mobility_level3);
        NAME_TITLE_MAP.put("bike_surfer_level1", R.string.badge_title_bike_surfer_level1);
        NAME_TITLE_MAP.put("bike_surfer_level2", R.string.badge_title_bike_surfer_level2);
        NAME_TITLE_MAP.put("bike_surfer_level3", R.string.badge_title_bike_surfer_level3);
        NAME_TITLE_MAP.put("tpl_surfer_level1", R.string.badge_title_tpl_surfer_level1);
        NAME_TITLE_MAP.put("tpl_surfer_level2", R.string.badge_title_tpl_surfer_level2);
        NAME_TITLE_MAP.put("tpl_surfer_level3", R.string.badge_title_tpl_surfer_level3);
        NAME_TITLE_MAP.put("multi_surfer_level1", R.string.badge_title_multi_surfer_level1);
        NAME_TITLE_MAP.put("multi_surfer_level2", R.string.badge_title_multi_surfer_level2);
        NAME_TITLE_MAP.put("multi_surfer_level3", R.string.badge_title_multi_surfer_level3);
        NAME_TITLE_MAP.put("ecologist_level1", R.string.badge_title_ecologist_level1);
        NAME_TITLE_MAP.put("ecologist_level2", R.string.badge_title_ecologist_level2);
        NAME_TITLE_MAP.put("ecologist_level3", R.string.badge_title_ecologist_level3);
        NAME_TITLE_MAP.put("healthy_level1", R.string.badge_title_healthy_level1);
        NAME_TITLE_MAP.put("healthy_level2", R.string.badge_title_healthy_level2);
        NAME_TITLE_MAP.put("healthy_level3", R.string.badge_title_healthy_level3);
        NAME_TITLE_MAP.put("money_saver_level1", R.string.badge_title_money_saver_level1);
        NAME_TITLE_MAP.put("money_saver_level2", R.string.badge_title_money_saver_level2);
        NAME_TITLE_MAP.put("money_saver_level3", R.string.badge_title_money_saver_level3);


    }
    // map of the descriptions strings when not ack
    public static final Map<String, Integer> NAME_BEFORE_MAP;
    static
    {
        // Before
        NAME_BEFORE_MAP = new HashMap<String, Integer>();
        NAME_BEFORE_MAP.put("new_user", R.string.badge_before_new_user);
        NAME_BEFORE_MAP.put("data_collector_level0", R.string.badge_before_data_collector_level0);
        NAME_BEFORE_MAP.put("data_collector_level1", R.string.badge_before_data_collector_level1);
        NAME_BEFORE_MAP.put("data_collector_level2", R.string.badge_before_data_collector_level2);
        NAME_BEFORE_MAP.put("data_collector_level3", R.string.badge_before_data_collector_level3);
        NAME_BEFORE_MAP.put("biker_level1", R.string.badge_before_biker_level1);
        NAME_BEFORE_MAP.put("biker_level2", R.string.badge_before_biker_level2);
        NAME_BEFORE_MAP.put("biker_level3", R.string.badge_before_biker_level3);
        NAME_BEFORE_MAP.put("public_mobility_level1", R.string.badge_before_public_mobility_level1);
        NAME_BEFORE_MAP.put("public_mobility_level2", R.string.badge_before_public_mobility_level2);
        NAME_BEFORE_MAP.put("public_mobility_level3", R.string.badge_before_public_mobility_level3);
        NAME_BEFORE_MAP.put("bike_surfer_level1", R.string.badge_before_bike_surfer_level1);
        NAME_BEFORE_MAP.put("bike_surfer_level2", R.string.badge_before_bike_surfer_level2);
        NAME_BEFORE_MAP.put("bike_surfer_level3", R.string.badge_before_bike_surfer_level3);
        NAME_BEFORE_MAP.put("tpl_surfer_level1", R.string.badge_before_tpl_surfer_level1);
        NAME_BEFORE_MAP.put("tpl_surfer_level2", R.string.badge_before_tpl_surfer_level2);
        NAME_BEFORE_MAP.put("tpl_surfer_level3", R.string.badge_before_tpl_surfer_level3);
        NAME_BEFORE_MAP.put("multi_surfer_level1", R.string.badge_before_multi_surfer_level1);
        NAME_BEFORE_MAP.put("multi_surfer_level2", R.string.badge_before_multi_surfer_level2);
        NAME_BEFORE_MAP.put("multi_surfer_level3", R.string.badge_before_multi_surfer_level3);
        NAME_BEFORE_MAP.put("ecologist_level1", R.string.badge_before_ecologist_level1);
        NAME_BEFORE_MAP.put("ecologist_level2", R.string.badge_before_ecologist_level2);
        NAME_BEFORE_MAP.put("ecologist_level3", R.string.badge_before_ecologist_level3);
        NAME_BEFORE_MAP.put("healthy_level1", R.string.badge_before_healthy_level1);
        NAME_BEFORE_MAP.put("healthy_level2", R.string.badge_before_healthy_level2);
        NAME_BEFORE_MAP.put("healthy_level3", R.string.badge_before_healthy_level3);
        NAME_BEFORE_MAP.put("money_saver_level1", R.string.badge_before_money_saver_level1);
        NAME_BEFORE_MAP.put("money_saver_level2", R.string.badge_before_money_saver_level2);
        NAME_BEFORE_MAP.put("money_saver_level3", R.string.badge_before_money_saver_level3);
    }
    // map of the descriptions strings when  ack
    public static final Map<String, Integer> NAME_AFTER_MAP;
    static
    {
        // After
        NAME_AFTER_MAP = new HashMap<String, Integer>();
        NAME_AFTER_MAP.put("new_user", R.string.badge_after_new_user);
        NAME_AFTER_MAP.put("data_collector_level0", R.string.badge_after_data_collector_level0);
        NAME_AFTER_MAP.put("data_collector_level1", R.string.badge_after_data_collector_level1);
        NAME_AFTER_MAP.put("data_collector_level2", R.string.badge_after_data_collector_level2);
        NAME_AFTER_MAP.put("data_collector_level3", R.string.badge_after_data_collector_level3);
        NAME_AFTER_MAP.put("biker_level1", R.string.badge_after_biker_level1);
        NAME_AFTER_MAP.put("biker_level2", R.string.badge_after_biker_level2);
        NAME_AFTER_MAP.put("biker_level3", R.string.badge_after_biker_level3);
        NAME_AFTER_MAP.put("public_mobility_level1", R.string.badge_after_public_mobility_level1);
        NAME_AFTER_MAP.put("public_mobility_level2", R.string.badge_after_public_mobility_level2);
        NAME_AFTER_MAP.put("public_mobility_level3", R.string.badge_after_public_mobility_level3);
        NAME_AFTER_MAP.put("bike_surfer_level1", R.string.badge_after_bike_surfer_level1);
        NAME_AFTER_MAP.put("bike_surfer_level2", R.string.badge_after_bike_surfer_level2);
        NAME_AFTER_MAP.put("bike_surfer_level3", R.string.badge_after_bike_surfer_level3);
        NAME_AFTER_MAP.put("tpl_surfer_level1", R.string.badge_after_tpl_surfer_level1);
        NAME_AFTER_MAP.put("tpl_surfer_level2", R.string.badge_after_tpl_surfer_level2);
        NAME_AFTER_MAP.put("tpl_surfer_level3", R.string.badge_after_tpl_surfer_level3);
        NAME_AFTER_MAP.put("multi_surfer_level1", R.string.badge_after_multi_surfer_level1);
        NAME_AFTER_MAP.put("multi_surfer_level2", R.string.badge_after_multi_surfer_level2);
        NAME_AFTER_MAP.put("multi_surfer_level3", R.string.badge_after_multi_surfer_level3);
        NAME_AFTER_MAP.put("ecologist_level1", R.string.badge_after_ecologist_level1);
        NAME_AFTER_MAP.put("ecologist_level2", R.string.badge_after_ecologist_level2);
        NAME_AFTER_MAP.put("ecologist_level3", R.string.badge_after_ecologist_level3);
        NAME_AFTER_MAP.put("healthy_level1", R.string.badge_after_healthy_level1);
        NAME_AFTER_MAP.put("healthy_level2", R.string.badge_after_healthy_level2);
        NAME_AFTER_MAP.put("healthy_level3", R.string.badge_after_healthy_level3);
        NAME_AFTER_MAP.put("money_saver_level1", R.string.badge_after_money_saver_level1);
        NAME_AFTER_MAP.put("money_saver_level2", R.string.badge_after_money_saver_level2);
        NAME_AFTER_MAP.put("money_saver_level3", R.string.badge_after_money_saver_level3);
    }

    public static final Map<String, Integer> NAME_ICON_MAP;
    static {
        // Icons
        NAME_ICON_MAP = new HashMap<String, Integer>();
        NAME_ICON_MAP.put("new_user", R.drawable.badge_ic_new_user);
        NAME_ICON_MAP.put("data_collector_level0", R.drawable.badge_ic_data_collector_level0);
        NAME_ICON_MAP.put("data_collector_level1", R.drawable.badge_ic_data_collector_level1);
        NAME_ICON_MAP.put("data_collector_level2", R.drawable.badge_ic_data_collector_level2);
        NAME_ICON_MAP.put("data_collector_level3", R.drawable.badge_ic_data_collector_level3);
        NAME_ICON_MAP.put("biker_level1", R.drawable.badge_ic_biker_level1);
        NAME_ICON_MAP.put("biker_level2", R.drawable.badge_ic_biker_level2);
        NAME_ICON_MAP.put("biker_level3", R.drawable.badge_ic_biker_level3);
        NAME_ICON_MAP.put("public_mobility_level1", R.drawable.badge_ic_public_mobility_level1);
        NAME_ICON_MAP.put("public_mobility_level2", R.drawable.badge_ic_public_mobility_level2);
        NAME_ICON_MAP.put("public_mobility_level3", R.drawable.badge_ic_public_mobility_level3);
        NAME_ICON_MAP.put("bike_surfer_level1", R.drawable.badge_ic_bike_surfer_level1);
        NAME_ICON_MAP.put("bike_surfer_level2", R.drawable.badge_ic_bike_surfer_level2);
        NAME_ICON_MAP.put("bike_surfer_level3", R.drawable.badge_ic_bike_surfer_level3);
        NAME_ICON_MAP.put("tpl_surfer_level1", R.drawable.badge_ic_tpl_surfer_level1);
        NAME_ICON_MAP.put("tpl_surfer_level2", R.drawable.badge_ic_tpl_surfer_level2);
        NAME_ICON_MAP.put("tpl_surfer_level3", R.drawable.badge_ic_tpl_surfer_level3);
        NAME_ICON_MAP.put("multi_surfer_level1", R.drawable.badge_ic_multi_surfer_level1);
        NAME_ICON_MAP.put("multi_surfer_level2", R.drawable.badge_ic_multi_surfer_level2);
        NAME_ICON_MAP.put("multi_surfer_level3", R.drawable.badge_ic_multi_surfer_level3);
        NAME_ICON_MAP.put("ecologist_level1", R.drawable.badge_ic_ecologist_level1);
        NAME_ICON_MAP.put("ecologist_level2", R.drawable.badge_ic_ecologist_level2);
        NAME_ICON_MAP.put("ecologist_level3", R.drawable.badge_ic_ecologist_level3);
        NAME_ICON_MAP.put("healthy_level1", R.drawable.badge_ic_healthy_level1);
        NAME_ICON_MAP.put("healthy_level2", R.drawable.badge_ic_healthy_level2);
        NAME_ICON_MAP.put("healthy_level3", R.drawable.badge_ic_healthy_level3);
        NAME_ICON_MAP.put("money_saver_level1", R.drawable.badge_ic_money_saver_level1);
        NAME_ICON_MAP.put("money_saver_level2", R.drawable.badge_ic_money_saver_level2);
        NAME_ICON_MAP.put("money_saver_level3", R.drawable.badge_ic_money_saver_level3);

    }
}
