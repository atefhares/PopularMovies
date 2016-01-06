package com.example.atefhares.myapplication;

/**
 * Created by Atef Hares on 26-Dec-15.
 */
public class Movie{
    public static int no_of_favorite_movies=0;
    private String TrailersJsonString;
    private String ReviewsJsonString;
    private String MovieJsonString;
    private String id;
    private boolean Favorite;

    private String poster_path;
    private String backdrop_path;
    private String overview;
    private String title;
    private String language;
    private String vote_average;
    private String vote_count;
    private String release_date;
    private String[] genres;

    private String[] reviews_authors;
    private String[] reviews_contents;
    private String[] reviews_Urls;

    private String[] videos_titles;
    private String[] videos_urls;

    public String[] getVideos_urls() {
        return videos_urls;
    }

    public String[] getVideos_titles() {
        return videos_titles;
    }

    public String[] getReviews_contents() {
        return reviews_contents;
    }

    public String[] getReviews_authors() {
        return reviews_authors;
    }

    public void setReviews_authors(String[] reviews_authors) {
        this.reviews_authors = reviews_authors;
    }

    public void setReviews_contents(String[] reviews_contents) {
        this.reviews_contents = reviews_contents;
    }

    public void setVideos_titles(String[] videos_titles) {
        this.videos_titles = videos_titles;
    }

    public void setVideos_urls(String[] videos_urls) {
        this.videos_urls = videos_urls;
    }

    public void setFavorite(boolean favorite) {
        Favorite = favorite;
    }

    public boolean isFavorite() {
        return Favorite;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public String getGenres() {
        String res ="";
        for (int i=0;i<genres.length;i++)
        {
            if(i != genres.length -1)
                res = res + genres[i]+" | ";
            else
                res = res + genres[i];
        }
        return res;
    }

    public void setVote_count(String vote_count) {
        this.vote_count = vote_count;
    }

    public String getVote_count() {
        return vote_count;
    }

    public String getMovieJsonString() {
        return MovieJsonString;
    }

    public void setMovieJsonString(String movieJsonString) {
        MovieJsonString = movieJsonString;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTrailersJsonString(String trailersJsonString) {
        TrailersJsonString = trailersJsonString;
    }

    public void setReviewsJsonString(String reviewsJsonString) {
        ReviewsJsonString = reviewsJsonString;
    }

    public String getTrailersJsonString() {
        return TrailersJsonString;
    }

    public String getReviewsJsonString() {
        return ReviewsJsonString;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public String getOverview() {
        return overview;
    }

    public String getTitle() {
        return title;
    }

    public String getLanguage() {
        return language;
    }

    public String getVote_average() {
        return vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setPoster_path(String poster_path) {

        this.poster_path = poster_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String[] getReviews_Urls() {
        return reviews_Urls;
    }

    public void setReviews_Urls(String[] reviews_Urls) {
        this.reviews_Urls = reviews_Urls;
    }
}
