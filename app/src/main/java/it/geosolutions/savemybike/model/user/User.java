package it.geosolutions.savemybike.model.user;

import com.google.gson.annotations.SerializedName;

import java.net.URL;

/**
 * @author Lorenzo Natali, GeoSolutions S.a.s.
 * Model of User for back-end API
 */
public class User {
    private String uuid;
    private String username;
    private String email;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    private String nickname;
    private  String profile_type;
    private Profile profile;
    private String avatar;
    @SerializedName("accepted_terms_of_service")
    private Boolean acceptedTermsOfService;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfile_type() {
        return profile_type;
    }

    public void setProfile_type(String profile_type) {
        this.profile_type = profile_type;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getAcceptedTermsOfService() {
        return acceptedTermsOfService;
    }

    public void setAcceptedTermsOfService(Boolean acceptedTermsOfService) {
        this.acceptedTermsOfService = acceptedTermsOfService;
    }
}
