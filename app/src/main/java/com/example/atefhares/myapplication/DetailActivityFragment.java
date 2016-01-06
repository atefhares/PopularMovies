package com.example.atefhares.myapplication;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    Movie movie = new Movie();
    static String ID;
    static boolean mTwopane;
    private ImageView posterImageView, timeLineImageView, FavoriteImageView;
    TextView titleTextview, overviewTextview, rateTextview, languageTextview, release_dateTextview, CountTextview, genresTextView;
    ScrollView scrollView;
    SharedPreferences prefs;
    String Favorite_Movies_IDSs;

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getActivity(), Settings.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle args = getArguments();
        if (args != null) {
            ID = args.getString("ID");
        }
        posterImageView = (ImageView) view.findViewById(R.id.posterImage);
        timeLineImageView = (ImageView) view.findViewById(R.id.timeLineImage);
        titleTextview = (TextView) view.findViewById(R.id.titleTextview);
        overviewTextview = (TextView) view.findViewById(R.id.Over_ViewTextview);
        rateTextview = (TextView) view.findViewById(R.id.rateTextview);
        release_dateTextview = (TextView) view.findViewById(R.id.relaseDateTextview);
        languageTextview = (TextView) view.findViewById(R.id.langTextview);
        CountTextview = (TextView) view.findViewById(R.id.countText);
        genresTextView = (TextView) view.findViewById(R.id.genres);
        scrollView = (ScrollView) view.findViewById(R.id.scrView);
        scrollView.setVisibility(View.GONE);

        /*Checking savedInstanceState*/
        if (savedInstanceState != null && savedInstanceState.containsKey("mID"))
            ID = savedInstanceState.getString("mID");

        /* Reading the ID to show it's details */
        if (!mTwopane) {//Phone Mode
            if (ID == null) {
                Intent intent = getActivity().getIntent();
                if (intent != null)
                    ID = intent.getStringExtra(Intent.EXTRA_TEXT);
            }
        }

        movie.setId(ID);//setting the movie id

        //Checking if the movie is favorite
        prefs = getActivity().getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        Favorite_Movies_IDSs = prefs.getString("FavoriteMovies_IDs_String", null);
        Movie.no_of_favorite_movies = prefs.getInt("FavoriteMovies_number", 0);
        try {
            if (Favorite_Movies_IDSs.contains(movie.getId()))
                movie.setFavorite(true);//this movie is favorite
        } catch (Exception e) {
            e.printStackTrace();
        }


        FavoriteImageView = (ImageView) view.findViewById(R.id.favImageView);
        if (movie.isFavorite()) {
            FavoriteImageView.setImageResource(R.drawable.favfilled);
        }

        FavoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!movie.isFavorite()) {
                    FavoriteImageView.setImageResource(R.drawable.favfilled);
                    AddMovieToFavorites(movie);
                } else {
                    FavoriteImageView.setImageResource(R.drawable.favicon);
                    RemoveMovieToFavorites(movie);
                }

            }
        });


        UpdateDetailsActivity();//Building the UI

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (MainActivityFragment.mTwopane)
            outState.putString("mID", movie.getId());

        super.onSaveInstanceState(outState);
    }

    private void AddMovieToFavorites(Movie movie) {
        movie.setFavorite(true);
        prefs.edit().putString("FavoriteMovies_IDs_String", Favorite_Movies_IDSs + movie.getId() + "/").apply();
        Movie.no_of_favorite_movies++;
        prefs.edit().putInt("FavoriteMovies_number", Movie.no_of_favorite_movies).apply();
    }

    private void RemoveMovieToFavorites(Movie movie) {
        movie.setFavorite(false);
        String new_Favorite_Movies_IDSs = Favorite_Movies_IDSs.replace(movie.getId() + "/", "");
        prefs.edit().putString("FavoriteMovies_IDs_String", new_Favorite_Movies_IDSs).apply();
        Movie.no_of_favorite_movies--;
        prefs.edit().putInt("FavoriteMovies_number", Movie.no_of_favorite_movies).apply();
        ;
    }

    private void UpdateDetailsActivity() {
        new FetchMovieData().execute(movie.getId());
    }

    private void Update_UI_Elements_With_Movie_Details(Movie movie) {
        scrollView.setVisibility(View.VISIBLE);

        movie = JsonToString.BulidMovieDetails(movie);
        //poster
        posterImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Picasso.with(getActivity())
                .load(Fetch_Images_url(movie.getPoster_path()))
                //.placeholder(context.getResources().getDrawable(R.drawable.noimage))
                .error(this.getResources().getDrawable(R.drawable.noimg))
                .into(posterImageView);
        //Timeline
        Picasso.with(getActivity())
                .load(Fetch_Images_url(movie.getBackdrop_path()))
                .resize(800, 300)
                //.placeholder(context.getResources().getDrawable(R.drawable.noimage))
                .error(getActivity().getResources().getDrawable(R.drawable.noimg))
                .into(timeLineImageView);

        titleTextview.setText(movie.getTitle());
        overviewTextview.setText(movie.getOverview());
        rateTextview.setText(movie.getVote_average());
        release_dateTextview.setText(movie.getRelease_date());
        languageTextview.setText(movie.getLanguage());
        CountTextview.setText(movie.getVote_count());
        genresTextView.setText(movie.getGenres());

        ListView reviewslistView = (ListView) getActivity().findViewById(R.id.listView);
        reviewslistView.setAdapter(new ReviewsListAdapter(getActivity(), movie.getReviews_authors(), movie.getReviews_contents(), movie.getReviews_Urls()));
        if (mTwopane)
            setListViewHeightBasedOnChildren(reviewslistView);
        reviewslistView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        ListView trlistView = (ListView) getActivity().findViewById(R.id.listView2);
        trlistView.setAdapter(new TrailersListAdapter(getActivity(), movie.getVideos_titles(), movie.getVideos_urls()));
        setListViewHeightBasedOnChildren(trlistView);

    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, listView.getEmptyView(), listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private String Fetch_Images_url(String imagePath) {
        return "http://image.tmdb.org/t/p/" + "w342" + imagePath;
    }

    class FetchMovieData extends AsyncTask<String, Void, String[]> {

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        private final String LOG_TAG = FetchMovieData.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Fetching Data, Please Wait....");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        /**
         * Take the String representing the complete MoviesJson in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */


        @Override
        protected String[] doInBackground(String... params) {


            //If there's no correct query, there's nothing to look up.
            //Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection_movie = null, urlConnection_reviews = null, urlConnection_videos = null;
            BufferedReader reader_movie = null, reader_reviews = null, reader_videos = null;

            // Will contain the raw JSON response as a string.
            String MoviesJsonStr_movie = null, MoviesJsonStr_reviews = null, MoviesJsonStr_videos = null;

            try {

                // Construct the URL for the themoviedb query
                final String Movies_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String MOVIE_ID = params[0];//the id
//                Log.v(LOG_TAG, "MOVIE_ID: " + MOVIE_ID);
                //Log.v(LOG_TAG, "TrailersandReviews: " + TrailersandReviews);

                final String API_PARAM = "&api_key=";

                String URL_movie = Movies_BASE_URL + MOVIE_ID + "?" + API_PARAM + /*My API key*/ "a99b28595d12eaa9e396d63b566048bc";
                String URL_reviews = Movies_BASE_URL + MOVIE_ID + "/reviews" + "?" + API_PARAM + /*My API key*/ "a99b28595d12eaa9e396d63b566048bc";
                String URL_videos = Movies_BASE_URL + MOVIE_ID + "/videos" + "?" + API_PARAM + /*My API key*/ "a99b28595d12eaa9e396d63b566048bc";


                Uri builtUri_movie = Uri.parse(URL_movie).buildUpon().build();
                URL url_movie = new URL(builtUri_movie.toString());
//                Log.v(LOG_TAG, "Built URI: " + builtUri_movie.toString());

                Uri builtUri_reviews = Uri.parse(URL_reviews).buildUpon().build();
                URL url_reviews = new URL(builtUri_reviews.toString());
//                Log.v(LOG_TAG, "Built URI: " + builtUri_reviews.toString());

                Uri builtUri_videos = Uri.parse(URL_videos).buildUpon().build();
                URL url_videos = new URL(builtUri_videos.toString());
//                Log.v(LOG_TAG, "Built URI: " + builtUri_videos.toString());

                // Create the request to themoviedb, and open the connection
                urlConnection_movie = (HttpURLConnection) url_movie.openConnection();
                urlConnection_movie.setRequestMethod("GET");
                urlConnection_movie.connect();

                urlConnection_reviews = (HttpURLConnection) url_reviews.openConnection();
                urlConnection_reviews.setRequestMethod("GET");
                urlConnection_reviews.connect();

                urlConnection_videos = (HttpURLConnection) url_videos.openConnection();
                urlConnection_videos.setRequestMethod("GET");
                urlConnection_videos.connect();

                // Read the input stream into a String
                InputStream inputStream_movie = urlConnection_movie.getInputStream();//getting the data
                StringBuffer buffer_movie = new StringBuffer(); // for reading the data

                InputStream inputStream_reviews = urlConnection_reviews.getInputStream();//getting the data
                StringBuffer buffer_reviews = new StringBuffer(); // for reading the data

                InputStream inputStream_videos = urlConnection_videos.getInputStream();//getting the data
                StringBuffer buffer_videos = new StringBuffer(); // for reading the data
                if (inputStream_movie == null || inputStream_reviews == null || inputStream_videos == null) {
                    // Nothing to do
                    MoviesJsonStr_movie = null;
                    MoviesJsonStr_reviews = null;
                    MoviesJsonStr_videos = null;
                    return null;
                }

                reader_movie = new BufferedReader(new InputStreamReader(inputStream_movie));
                reader_reviews = new BufferedReader(new InputStreamReader(inputStream_reviews));
                reader_videos = new BufferedReader(new InputStreamReader(inputStream_videos));

                String line_movie;
                while ((line_movie = reader_movie.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer_movie.append(line_movie + "\n");
                }

                String line_reviews;
                while ((line_reviews = reader_reviews.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer_reviews.append(line_reviews + "\n");
                }

                String line_vedios;
                while ((line_vedios = reader_videos.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer_videos.append(line_vedios + "\n");
                }

                if (buffer_movie.length() == 0 || buffer_reviews.length() == 0 || buffer_videos.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    MoviesJsonStr_movie = null;
                    MoviesJsonStr_reviews = null;
                    MoviesJsonStr_videos = null;
                    return null;
                }

                MoviesJsonStr_movie = buffer_movie.toString(); //saving the data
//                Log.v(LOG_TAG, "MoviesJsonStr_movie " + MoviesJsonStr_movie);//testing

                MoviesJsonStr_reviews = buffer_reviews.toString(); //saving the data
//                Log.v(LOG_TAG, "MoviesJsonStr_reviews " + MoviesJsonStr_reviews);//testing

                MoviesJsonStr_videos = buffer_videos.toString(); //saving the data
//                Log.v(LOG_TAG, "MMoviesJsonStr_videos " + MoviesJsonStr_videos);//testing

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the Movies data, there's no point in attempting
                // to parse it.
                MoviesJsonStr_movie = null;
                MoviesJsonStr_reviews = null;
                MoviesJsonStr_videos = null;
                return null;
            } finally {
                if (urlConnection_movie != null && urlConnection_reviews != null && urlConnection_videos != null) {
                    urlConnection_movie.disconnect();
                    urlConnection_reviews.disconnect();
                    urlConnection_videos.disconnect();
                }
                if (reader_movie != null && reader_reviews != null && reader_videos != null) {
                    try {
                        reader_movie.close();
                        reader_reviews.close();
                        reader_videos.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            String res[] = new String[3];
            res[0] = MoviesJsonStr_movie;
            res[1] = MoviesJsonStr_reviews;
            res[2] = MoviesJsonStr_videos;
            return res; // returning the full JSON Data
        }

        @Override
        protected void onPostExecute(String[] result) {
            progressDialog.dismiss();
            if (result != null) {
                movie.setTrailersJsonString(result[2]);
//                Log.e("setTrailersJsonString", movie.getTrailersJsonString());

                movie.setMovieJsonString(result[0]);
//                Log.e("setMovieJsonString", movie.getMovieJsonString());

                movie.setReviewsJsonString(result[1]);
//                Log.e("setReviewsJsonString", movie.getReviewsJsonString());

                if (isAdded())
                    Update_UI_Elements_With_Movie_Details(movie);
            }
        }
    }

}