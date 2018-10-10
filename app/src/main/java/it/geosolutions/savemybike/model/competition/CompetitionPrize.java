package it.geosolutions.savemybike.model.competition;

import com.google.gson.annotations.SerializedName;

public class CompetitionPrize {
    Prize prize;
    @SerializedName("user_rank") Integer userRank;

    public Prize getPrize() {
        return prize;
    }

    public void setPrize(Prize prize) {
        this.prize = prize;
    }

    public Integer getUserRank() {
        return userRank;
    }

    public void setUserRank(Integer userRank) {
        this.userRank = userRank;
    }
}
