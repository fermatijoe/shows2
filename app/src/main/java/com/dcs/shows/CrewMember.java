package com.dcs.shows;

import org.json.JSONException;
import org.json.JSONObject;

public class CrewMember {
    private String character, name, image, role, job;

    public CrewMember(){}

    public CrewMember(JSONObject person) throws JSONException {
        this.character = person.getString("character");
        this.name = person.getString("name");
        this.image = person.getString("profile_path");
        this.role = "actor";
    }

    public CrewMember(JSONObject person, String notNeeded) throws JSONException {
        this.job = person.getString("job");
        this.name = person.getString("name");
        this.image = person.getString("profile_path");
        this.role = "crew";
    }

    public String getCharacter() {
        return character;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getRole() {
        return role;
    }

    public String getJob() {
        return job;
    }
}
