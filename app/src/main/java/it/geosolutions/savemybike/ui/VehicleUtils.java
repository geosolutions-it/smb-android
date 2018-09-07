package it.geosolutions.savemybike.ui;

import android.graphics.Color;

import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.model.Vehicle;

public class VehicleUtils {
    /**
     * Returns the resource id of the drawable assigned to a vehicle.
     * Example: vehicleIcon.setImageResource(getVehicleColor("bike"))
     * @param v
     * @return
     */
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

    /**
     * Returns the resource id of the color assigned to a vehicle.
     * Example: getResources().getColor(getVehicleColor("bike"))
     * @param v
     * @return
     */
    public static int getVehicleColor(String v) {
        switch (v) {
            case "walk":
                return R.color.walk_color;
            case "bike":
                return R.color.bike_color;
            case "motorcycle":
                return R.color.motorcycle_color;
            case "car":
                return R.color.car_color;
            case "bus":
                return R.color.bus_color;
            case "train":
                return R.color.train_color;
            default:
                return R.color.default_track_color; // TODO: some question mark.
        }
    }
}
