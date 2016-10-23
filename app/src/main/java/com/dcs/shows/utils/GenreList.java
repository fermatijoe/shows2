package com.dcs.shows.utils;

import android.content.Context;
import android.view.animation.Animation;

import com.dcs.shows.App;
import com.dcs.shows.R;

import java.util.ArrayList;
import java.util.List;

import static com.dcs.shows.R.string.Comedy;
import static com.dcs.shows.R.string.Crime;
import static com.dcs.shows.R.string.Documentary;
import static com.dcs.shows.R.string.Drama;
import static com.dcs.shows.R.string.Family;
import static com.dcs.shows.R.string.Mystery;
import static com.dcs.shows.R.string.default_web_client_id;

public class GenreList {
    private Context c;
    private List<String> genres;

    public GenreList(){

    }

    public void initGenreListMovie(){
        this.c = App.getContext();
        genres = new ArrayList<>();
        genres.add(c.getString(R.string.genres));
        genres.add(c.getString(R.string.Action));
        genres.add(c.getString(R.string.Adventure));
        genres.add(c.getString(R.string.Animation));
        genres.add(c.getString(Comedy));
        genres.add(c.getString(Crime));
        genres.add(c.getString(Documentary));
        genres.add(c.getString(Drama));
        genres.add(c.getString(Family));
        genres.add(c.getString(R.string.Fantasy));
        genres.add(c.getString(R.string.Foreign));
        genres.add(c.getString(R.string.History));
        genres.add(c.getString(R.string.Horror));
        genres.add(c.getString(R.string.Music));
        genres.add(c.getString(Mystery));
        genres.add(c.getString(R.string.Romance));
        genres.add(c.getString(R.string.Science_Fiction));
        genres.add(c.getString(R.string.TV_Movie));
        genres.add(c.getString(R.string.Thriller));
        genres.add(c.getString(R.string.War));
        genres.add("Western");
    }

    public List<String> getGenresList(){
        return genres;
    }

    public String getIdGenreWithMovie(String[] genres){
        String output = "";
        for(String s : genres){
            String id = associateNameWithId(s);
            output = output + id + ",";
        }
        if(output.substring(output.length() - 1).equals(",")){
            output = output.substring(0, output.length()-1);
        }
        return output;
    }

    public String associateNameWithId(String text){
        if (text.equals(c.getString(R.string.Action))) {
            return "28";
        } else if (text.equals(c.getString(R.string.Adventure))) {
            return "12";
        } else if (text.equals(c.getString(R.string.Animation))) {
            return "16";
        } else if (text.equals(c.getString(R.string.Comedy))) {
            return "35";
        } else if (text.equals(c.getString(R.string.Crime))) {
            return "80";
        } else if (text.equals(c.getString(R.string.Documentary))) {
            return "99";
        } else if (text.equals(c.getString(R.string.Drama))) {
            return "18";
        } else if (text.equals(c.getString(R.string.Family))) {
            return "10751";
        } else if (text.equals(c.getString(R.string.Fantasy))) {
            return "14";
        } else if (text.equals(c.getString(R.string.History))) {
            return "36";
        } else if (text.equals(c.getString(R.string.Horror))) {
            return "27";
        } else if (text.equals(c.getString(R.string.Music))) {
            return "10402";
        } else if (text.equals(c.getString(R.string.Mystery))) {
            return "9648";
        } else if (text.equals(c.getString(R.string.Romance))) {
            return "10749";
        } else if (text.equals(c.getString(R.string.Science_Fiction))) {
            return "878";
        } else if (text.equals(c.getString(R.string.TV_Movie))) {
            return "10770";
        } else if (text.equals(c.getString(R.string.Thriller))) {
            return "53";
        } else if (text.equals(c.getString(R.string.War))) {
            return "10752";
        } else if (text.equals("Western")) {
            return "37";
        } else {
            return genres.get(1);
        }

    }

    public String getTvGenreWithId(int id){
        switch (id){
            case 10759:
                return genres.get(1) + " & " + genres.get(2);

            case 10763:
                return "News";

            case 16:
                return genres.get(3);

            case 35:
                return genres.get(4);

            case 80:
                return genres.get(5);

            case 99:
                return genres.get(6);

            case 18:
                return genres.get(7);

            case 10751:
                return genres.get(8);

            case 14:
                return "";

            case 10769:
                return genres.get(10);

            case 36:
                return genres.get(11);

            case 27:
                return genres.get(12);

            case 10402:
                return genres.get(13);

            case 9648:
                return genres.get(14);

            case 10749:
                return genres.get(15);

            case 10765:
                return genres.get(16);

            case 10770:
                return genres.get(17);

            case 53:
                return genres.get(18);

            case 10768:
                return genres.get(19);

            case 37:
                return genres.get(20);

            default:
                return "";

        }

    }

    public String getMovieGenreWithId(int id){
        switch (id){
            case 28:
                return genres.get(1);

            case 12:
                return genres.get(2);

            case 16:
                return genres.get(3);

            case 35:
                return genres.get(4);

            case 80:
                return genres.get(5);

            case 99:
                return genres.get(6);

            case 18:
                return genres.get(7);

            case 10751:
                return genres.get(8);

            case 14:
                return genres.get(9);

            case 10769:
                return genres.get(10);

            case 36:
                return genres.get(11);

            case 27:
                return genres.get(12);

            case 10402:
                return genres.get(13);

            case 9648:
                return genres.get(14);

            case 10749:
                return genres.get(15);

            case 878:
                return genres.get(16);

            case 10770:
                return genres.get(17);

            case 53:
                return genres.get(18);

            case 10752:
                return genres.get(19);

            case 37:
                return genres.get(20);

            default:

                return genres.get(id);

        }

    }


}
