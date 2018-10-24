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
import it.geosolutions.savemybike.ui.utils.BadgeUtils;

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
        Integer id = BadgeUtils.NAME_TITLE_MAP.get(name);
        if(id != null) {
            return this.getContext().getResources().getString(id);
        } else {
            return name;
        }
    }
    private Integer getIcon(Badge badge) {
        String name = badge.getName();
        Integer id = BadgeUtils.NAME_ICON_MAP.get(name);
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
            id = BadgeUtils.NAME_AFTER_MAP.get(badge.getName());
        } else {
            id = BadgeUtils.NAME_BEFORE_MAP.get(badge.getName());
        }

        if(id != null) {
            return this.getContext().getResources().getString(id);
        } else {
            return badge.getDescription();
        }
    }
}