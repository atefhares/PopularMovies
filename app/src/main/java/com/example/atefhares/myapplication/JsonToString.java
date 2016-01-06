package com.example.atefhares.myapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Atef Hares on 25-Dec-15.
 */
public class JsonToString {
    private final String LOG_TAG = JsonToString.class.getSimpleName();

    public static String[] getMoviesPostersArray(String moviesJsonStr) throws JSONException {

        JSONObject MoviesJson = new JSONObject(moviesJsonStr);
        JSONArray MoviesArray = MoviesJson.getJSONArray("results");//geting all movies results
        String moviesPostersPathes[]=new String[MoviesArray.length()];//Array contains the required posters pathes

        for(int i=0 ; i<MoviesArray.length();i++)
        {
            JSONObject movie = MoviesArray.getJSONObject(i);//for each movie
            moviesPostersPathes[i]=movie.getString("poster_path");//saving the image path
        }


//        //testing
//        for(String s : moviesPostersPathes)
//        {
//            Log.d(LOG_TAG, "moviesImages Pathes: " + s);
//        }

        return moviesPostersPathes;//return the array of posters pathes
    }
    static String[] getPostersPathsfromArray(String Movies_JSON_Array[]) throws JSONException {
        String[] res = new String[Movies_JSON_Array.length];
        for (int i=0;i<Movies_JSON_Array.length;i++)
        {
            JSONObject MovieJson = new JSONObject(Movies_JSON_Array[i]);
            String path = MovieJson.getString("poster_path");
            res[i]="http://image.tmdb.org/t/p/"+"w185"+path;
        }
        return res;
    }

    static Movie getMovieDetails(Movie movie) throws JSONException {
        JSONObject jsonObject = new JSONObject(movie.getMovieJsonString());

        final String IMAGE_BASE_URLS = "http://image.tmdb.org/t/p/";
        final String IMAGE_SZIE = "w185";
        final String IMAGE_PATH = jsonObject.getString("poster_path");
        final String BACKDROP_PATH=jsonObject.getString("backdrop_path");


        movie.setPoster_path(IMAGE_BASE_URLS+IMAGE_SZIE+IMAGE_PATH);
        movie.setBackdrop_path(IMAGE_BASE_URLS+"w300"+BACKDROP_PATH);
        movie.setLanguage(jsonObject.getString("original_language"));
        movie.setOverview(jsonObject.getString("overview"));
        movie.setRelease_date(jsonObject.getString("release_date"));
        movie.setTitle(jsonObject.getString("title"));
        movie.setVote_average(jsonObject.getString("vote_average"));
        movie.setVote_count(jsonObject.getString("vote_count"));
        movie.setId(jsonObject.getString("id"));

        //get genres
        JSONArray jsonArray = jsonObject.getJSONArray("genres");
        String genresWords[]=new String[jsonArray.length()];//Array contains the required posters pathes
        for(int i=0 ; i<jsonArray.length();i++)
        {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);//for each movie
            genresWords[i]=jsonObject1.getString("name");//saving the image path
        }
        movie.setGenres(genresWords);

        return movie;
    }
    static Movie getMovieReviews(Movie movie)throws JSONException
    {
        JSONObject jsonObject = new JSONObject(movie.getReviewsJsonString());
        JSONArray jsonArray = jsonObject.getJSONArray("results");
        String temp_authors[] = new String[jsonArray.length()];
        String temp_contents[] = new String[jsonArray.length()];
        String temp_urls[] = new String[jsonArray.length()];

        for (int i=0 ; i<jsonArray.length();i++)
        {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            String auth =jsonObject1.getString("author");
            temp_authors[i]=auth;
            temp_urls[i]=jsonObject1.getString("url");
            String temp = jsonObject1.getString("content");
            if(temp.length() <= 150)
                temp_contents[i]=temp;
            else
            {
                //cutting long contents !
                String short_content = temp.substring(0,temp.length()/3)+"....>>>press Continue Reading to read the full review";
                temp_contents[i]=short_content;
            }
        }
        movie.setReviews_authors(temp_authors);
        movie.setReviews_Urls(temp_urls);
        movie.setReviews_contents(temp_contents);

        return movie;
    }
    static Movie getMovieVedios(Movie movie)throws JSONException
    {
        JSONObject jsonObject = new JSONObject(movie.getTrailersJsonString());
        JSONArray jsonArray = jsonObject.getJSONArray("results");
        Set<String> temp_titels = new HashSet<>();
        Set<String> temp_urls = new HashSet<>();

        for (int i=0 ; i<jsonArray.length();i++)
        {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//            if(jsonObject1.getString("type").equals("Trailer") )
//            {
                temp_titels.add(jsonObject1.getString("name"));
                temp_urls.add(jsonObject1.getString("key"));
//            }
        }
        String res_titels[] = temp_titels.toArray(new String[temp_titels.size()]);
        movie.setVideos_titles(res_titels);

        String res_urls[] = temp_urls.toArray(new String[temp_urls.size()]);
        movie.setVideos_urls(res_urls);
        return movie;
    }
    static Movie BulidMovieDetails (Movie movie)
    {
        try {
            movie = getMovieDetails(movie);
            movie = getMovieReviews(movie);
            movie = getMovieVedios(movie);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movie;
    }
    static String getMovieId(String moviesJsonStr,int position) throws JSONException
    {
        JSONObject MoviesJson = new JSONObject(moviesJsonStr);
        JSONArray MoviesArray = MoviesJson.getJSONArray("results");//geting all movies results
        JSONObject movieJsonObject = MoviesArray.getJSONObject(position);//for each movie
        return movieJsonObject.getString("id");
    }
    static String getMovieId_from_movie(String movieJsonStr) throws JSONException
    {
        JSONObject jsonObject = new JSONObject(movieJsonStr);
        return jsonObject.getString("id");
    }
}
