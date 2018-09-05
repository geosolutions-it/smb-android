package it.geosolutions.savemybike.ui;

import android.graphics.Color;

import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.model.Vehicle;

public class VehicleUtils {
    public static int getDrawableForVeichle(String v) {
        switch (v) {
            case "walk":
                return R.drawable.ic_directions_walk;
            case "bike":
                return R.drawable.ic_directions_bike;
            case "motorcycle":
                return R.drawable.ic_directions_motorcycle;
            case "car":
                return R.drawable.ic_directions_car;
            case "bus":
                return R.drawable.ic_directions_bus;
            case "train":
                return R.drawable.ic_directions_train;
            default:
                return R.drawable.ic_home; // TODO: some question mark.
        }

    }
}
