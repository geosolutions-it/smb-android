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
            case Vehicle.StringTypes.FOOT:
                return R.drawable.ic_directions_walk;
            case Vehicle.StringTypes.BIKE:
                return R.drawable.ic_directions_bike;
            case Vehicle.StringTypes.MOPED:
                return R.drawable.ic_directions_motorcycle;
            case Vehicle.StringTypes.CAR:
                return R.drawable.ic_directions_car;
            case Vehicle.StringTypes.BUS:
                return R.drawable.ic_directions_bus;
            case Vehicle.StringTypes.TRAIN:
                return R.drawable.ic_directions_train;
            default:
                return R.drawable.ic_home; // TODO: some question mark.
        }

    }
    public static int getDrawableForVeichle(Vehicle.VehicleType v) {
        switch (v) {
            case FOOT:
                return R.drawable.ic_directions_walk;
            case BIKE:
                return R.drawable.ic_directions_bike;
            case MOPED:
                return R.drawable.ic_directions_motorcycle;
            case CAR:
                return R.drawable.ic_directions_car;
            case BUS:
                return R.drawable.ic_directions_bus;
            case TRAIN:
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
            case Vehicle.StringTypes.FOOT:
                return R.color.walk_color;
            case Vehicle.StringTypes.BIKE:
                return R.color.bike_color;
            case Vehicle.StringTypes.MOPED:
                return R.color.motorcycle_color;
            case Vehicle.StringTypes.CAR:
                return R.color.car_color;
            case Vehicle.StringTypes.BUS:
                return R.color.bus_color;
            case Vehicle.StringTypes.TRAIN:
                return R.color.train_color;
            default:
                return R.color.default_track_color; // TODO: some question mark.
        }
    }

    public static int getVehicleName(Vehicle.VehicleType type) {
        switch (type) {
            case FOOT:
                return R.string.vehicle_foot;
            case BIKE:
                return R.string.vehicle_bike;
            case MOPED:
                return R.string.vehicle_moped;
            case CAR:
                return  R.string.vehicle_car;
            case BUS:
                return  R.string.vehicle_bus;
            case TRAIN:
                return  R.string.vehicle_train;
            default:
                return R.string.vehicle_foot;// TODO: some question mark.
        }
    }
}
