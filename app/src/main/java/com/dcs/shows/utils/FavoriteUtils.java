package com.dcs.shows.utils;

import com.dcs.shows.Show;
import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

public class FavoriteUtils {
    private final static String WHERE_CLAUSE_ID = "SHOW_ID";


    public static void addThisToFavorites(Show s){
        s.save();
    }

    public static void removeThisFromFavorites(Show s){
        getListFromDb(s, WHERE_CLAUSE_ID).get(0).delete();
    }

    public static boolean checkIfThisIsFavorite(Show s){
        List<Show> results = getListFromDb(s, WHERE_CLAUSE_ID);
        if(results.size() > 0){
            //found match
            return true;
        }else {
            //no match
            return false;
        }
    }
    private static List<Show> getListFromDb(Show s, String whereClause){
        int showId = s.getShowId();
         return Select.from(Show.class)
                .where(Condition.prop(whereClause)
                        .eq(Integer.valueOf(showId)
                                .toString()))
                .list();
    }

    public static List<Show> getAllFavorites(){
        return Show.listAll(Show.class);
    }
}
