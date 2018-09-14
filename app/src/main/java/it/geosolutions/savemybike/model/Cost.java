package it.geosolutions.savemybike.model;

import com.google.gson.annotations.SerializedName;

public class Cost {
    @SerializedName("fuel_cost")
    private double fuelCost;

    @SerializedName("time_cost")
    private double timeCost;

    @SerializedName("total_cost")
    private double totalCost;

    @SerializedName("operation_cost")
    private double operation_cost;

    @SerializedName("depreciation_cost")
    private double depreciation_cost;

    public double getFuelCost() {
        return fuelCost;
    }

    public void setFuelCost(double fuelCost) {
        this.fuelCost = fuelCost;
    }

    public double getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(double timeCost) {
        this.timeCost = timeCost;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getOperation_cost() {
        return operation_cost;
    }

    public void setOperation_cost(double operation_cost) {
        this.operation_cost = operation_cost;
    }

    public double getDepreciation_cost() {
        return depreciation_cost;
    }

    public void setDepreciation_cost(double depreciation_cost) {
        this.depreciation_cost = depreciation_cost;
    }
}
