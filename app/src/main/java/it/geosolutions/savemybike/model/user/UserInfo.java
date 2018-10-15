package it.geosolutions.savemybike.model.user;

import com.google.gson.annotations.SerializedName;

import it.geosolutions.savemybike.model.EmissionData;
import it.geosolutions.savemybike.model.HealthData;

/**
 * Extends User with the information about badges and results
 */
public class UserInfo extends User {
    @SerializedName("total_emissions")
    EmissionData totalEmissions;
    @SerializedName("total_health_benefits") HealthData totalHealthBenefits;

    public EmissionData getTotalEmissions() {
        return totalEmissions;
    }

    public void setTotalEmissions(EmissionData totalEmissions) {
        this.totalEmissions = totalEmissions;
    }

    public HealthData getTotalHealthBenefits() {
        return totalHealthBenefits;
    }

    public void setTotalHealthBenefits(HealthData totalHealthBenefits) {
        this.totalHealthBenefits = totalHealthBenefits;
    }
}
