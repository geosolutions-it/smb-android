package it.geosolutions.savemybike.ui.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.model.Badge;

/**
 * adapter for Badges
 */
public class BadgeAdapter extends ArrayAdapter<Badge> {

    private	int resource;
    static class ViewHolder {
        @BindView(R.id.item_badge) View view;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.description) TextView description;
        @BindView(R.id.badge_icon) ImageView icon;
        @BindView(R.id.icon_background) View iconBackground;
        @BindView(R.id.level) LinearLayout level;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
    // map of the title strings
    private static final Map<String, Integer> NAME_TITLE_MAP;
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
    private static final Map<String, Integer> NAME_BEFORE_MAP;
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
    private static final Map<String, Integer> NAME_AFTER_MAP;
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

    private static final Map<String, Integer> NAME_ICON_MAP;
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
    public BadgeAdapter(final Context context, int textViewResourceId, List<Badge> badges){
        super(context, textViewResourceId, badges);

        resource = textViewResourceId;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {

        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        Badge badge = getItem(position);
        // setup view
        if(badge != null) {

            holder.title.setText(getTitle(badge));
            holder.description.setText(getDescription(badge));
            holder.icon.setImageResource(getIcon(badge));
            Drawable background = holder.iconBackground.getBackground();
            background.mutate();
            // set acquired or not acquired style
            view.setAlpha(badge.isAcquired() ? 1.0f : 0.5f);
            if (background instanceof ShapeDrawable) {
                ((ShapeDrawable)background).getPaint().setColor(getColor(badge));
            } else if (background instanceof GradientDrawable) {
                ((GradientDrawable)background).setColor(getColor(badge));
            } else if (background instanceof ColorDrawable) {
                ((ColorDrawable)background).setColor(getColor(badge));
            }

        }

        return view;
    }

    private String getTitle(Badge badge) {
        String name = badge.getName();
        Integer id = NAME_TITLE_MAP.get(name);
        if(id != null) {
            return this.getContext().getResources().getString(id);
        } else {
            return name;
        }
    }
    private Integer getIcon(Badge badge) {
        String name = badge.getName();
        Integer id = NAME_ICON_MAP.get(name);
        if(id != null) {
            return id;
        } else {
            return R.drawable.ic_badge;
        }
    }
    private int getColor(Badge badge) {
        return getContext().getResources().getColor( badge.isAcquired() ? R.color.badge_background : R.color.badge_not_acquired);
    }
    private String getDescription(Badge badge) {
        Integer id;
        if (badge.isAcquired()) {
            id = NAME_AFTER_MAP.get(badge.getName());
        } else {
            id = NAME_BEFORE_MAP.get(badge.getName());
        }

        if(id != null) {
            return this.getContext().getResources().getString(id);
        } else {
            return badge.getDescription();
        }
    }
}