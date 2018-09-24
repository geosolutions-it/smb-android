package it.geosolutions.savemybike.model;

import com.google.gson.annotations.SerializedName;

import it.geosolutions.savemybike.R;

public class Badge {

    public enum Category {
            @SerializedName("user_registration") USER_REGISTRATION,
            @SerializedName("data_collection") DATA_COLLECTION,
            @SerializedName("bike_usage") BIKE_USAGE,
            @SerializedName("public_transport_usage") PUBLIC_TRANSPORT_USAGE,
            @SerializedName("sustainability") SUSTAINABILITY,
            @SerializedName("health_benefits") HEALTH_BENEFITS,
            @SerializedName("cost_savings") COST_SAVINGS
        }

        private int id; //: 473,
        private String url; //: "http://10.0.1.66:8000/api/my-badges/473/",
        private String name; //: "data_collector_level1",
        private Category category; //: "data_collection"
        private boolean acquired; //: false,
        private String description; //: "When you have recorded activity in a week for each day, you will get this badge.",

    public Badge() {

    }
    public Badge(String name) {
        this.name = name;
    }

    public String getTitle() {
        return this.name;
    }

    // PURE GETTERS AND SETTERS
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        /*
            Badge name can not start with a number. If it is the case
            the name is a sorting index that must be removed.
            This is required because the back-end framework (django gameify),
            don't allow to add anything to the model, and in order to sort
            badges, we need to put the order index inside the name.
            All the resources refer to names without order, so we have to remove this part
            before to return the badge name
         */
        String[] parts = name.split("_");
        if(parts[0] != null) {
            try {
                int order = Integer.parseInt(parts[0]);
                return name.replaceFirst(parts[0] + "_", "");
            } catch (Exception e) {
                return name;
            }

        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAcquired() {
        return acquired;
    }

    public void setAcquired(boolean acquired) {
        this.acquired = acquired;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }


}
