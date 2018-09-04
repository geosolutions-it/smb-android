package it.geosolutions.savemybike.model;

import com.google.gson.annotations.SerializedName;

public class HealthData {
    @SerializedName("benefit_index")
    private Double benefitIndex;

    @SerializedName("calories_consumed")
    private Double caloriesConsumed;

    public Double getBenefitIndex() {
        return benefitIndex;
    }

    public void setBenefitIndex(Double benefitIndex) {
        this.benefitIndex = benefitIndex;
    }

    public Double getCaloriesConsumed() {
        return caloriesConsumed;
    }

    public void setCaloriesConsumed(Double caloriesConsumed) {
        this.caloriesConsumed = caloriesConsumed;
    }
}
