package com.dcs.shows.utils;

import android.content.Context;

import com.dcs.shows.App;
import com.dcs.shows.R;

import java.util.ArrayList;
import java.util.List;

public class GenreList {
    private Context c;
    private List<String> genres;

    public GenreList(){

    }

    public void initGenreList(){
        this.c = App.getContext();
        genres = new ArrayList<>();
        genres.add("help me god");
        genres.add(c.getString(R.string.Action));
        genres.add(c.getString(R.string.Adventure));
        genres.add(c.getString(R.string.Animation));
        genres.add(c.getString(R.string.Comedy));
        genres.add(c.getString(R.string.Crime));
        genres.add(c.getString(R.string.Documentary));
        genres.add(c.getString(R.string.Drama));
        genres.add(c.getString(R.string.Family));
        genres.add(c.getString(R.string.Fantasy));
        genres.add(c.getString(R.string.Foreign));
        genres.add(c.getString(R.string.History));
        genres.add(c.getString(R.string.Horror));
        genres.add(c.getString(R.string.Music));
        genres.add(c.getString(R.string.Mystery));
        genres.add(c.getString(R.string.Romance));
        genres.add(c.getString(R.string.Science_Fiction));
        genres.add(c.getString(R.string.TV_Movie));
        genres.add(c.getString(R.string.Thriller));
        genres.add(c.getString(R.string.War));
        genres.add("Western");
    }

    public String getGenreWithId(int id){
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
