package com.dcs.shows;

import org.json.JSONException;
import org.json.JSONObject;

public class CrewMember {
    //in role there will be stored the "KNOWN FOR" movies. This is bad and only acceptable
    //to avoid erasing existing databases (i don't know how to migrate)
    private String character, name, image, role, job, personId, IMDBId, birthDay, biography, knownFor;

    public CrewMember(){}

    //constructor for an actor that appears in the movie
    public CrewMember(JSONObject person) throws JSONException {
        this.character = person.getString("character");
        this.name = person.getString("name");
        this.image = person.getString("profile_path");
        this.personId = person.getString("id");
        this.role = "actor";
    }

    //constructor for the directors
    public CrewMember(JSONObject person, String neededForDirector) throws JSONException {
        this.job = person.getString("job");
        this.name = person.getString("name");
        this.image = person.getString("profile_path");
        this.personId = person.getString("id");
        this.role = "crew";
    }

    //this constructor is used when displaying additional informations about an actor
    //it holds the movie in which he appears etc
    //those inf. should be fetched from /person/{person_id} node (ActorAsyncTask)
    public CrewMember(JSONObject person, int neededForActorDetail) throws JSONException {
        this.name = person.getString("name");
        this.image = person.getString("profile_path");
        this.personId = person.getString("id");
        this.IMDBId = person.getString("imdb_id");
        this.birthDay = person.getString("birthday");
        this.biography = person.getString("biography");
    }

    //lite version which hold data to be shown in the Advanced Actor Search
    public CrewMember(JSONObject person, boolean lightVersion) throws JSONException {
        this.name = person.getString("name");
        this.image = person.getString("profile_path");
        this.personId = person.getString("id");
    }

    public String getKnownFor() {

        return knownFor.replaceAll(", $", "").substring(4);

    }

    public void setKnownFor(String knownFor) {
        this.knownFor = this.knownFor + knownFor;
    }

    public String getCharacter() {
        return character;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return "http://image.tmdb.org/t/p/w90/" + image;
    }

    public String getRole() {
        return role;
    }

    public String getJob() {
        return job;
    }

    public String getPersonId() {
        return personId;
    }

    public String getIMDBId() {
        return IMDBId;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public String getBiography() {
        return biography;
    }
}
