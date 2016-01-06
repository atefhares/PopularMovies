package com.example.atefhares.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * A placeholder fragment containing a simple view.
 */

public class MainActivityFragment extends android.app.Fragment {
    private MenuItem Fav_menuItem, Main_menuItem, SettingsMenuItem;
    private GridView gridView;
    boolean Showing_Favorites = false;
    static boolean mTwopane,//Tablet or Phone
            onFavOrNot = false;//used to determine that we are showing Favorite movies or not
    String FavoriteMoviesIDs[];
    private ImageAdapter imageAdapter = new ImageAdapter();//The custom grid Adapter
    private int mPosition, scroll_pos;//for saving state
    private String mID;//used for saving state
    private String JsonString;//to save the JSON result from FetchMoviesTask

    public void setJsonString(String jsonString) {
        JsonString = jsonString;
    }

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        String s = prefs.getString("FavoriteMovies_IDs_String", "");
        int i = prefs.getInt("FavoriteMovies_number", 0);

        //Initializing the SharedPref file if it is Empty
        if (s.equals(""))
            prefs.edit().putString("FavoriteMovies_IDs_String", "").apply();//Initializing the SharedPref file
        if (i == 0)
            prefs.edit().putInt("FavoriteMovies_number", 0).apply();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        gridView = (GridView) inflater.inflate(R.layout.fragment_main, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        String saved_movies_IDs = prefs.getString("FavoriteMovies_IDs_String", null);
        onFavOrNot = prefs.getBoolean("onFavOrNot", onFavOrNot);
        if (onFavOrNot) {
            if (saved_movies_IDs != "")
                showFavorites();
            else {
                onFavOrNot = false;
                prefs.edit().putBoolean("onFavOrNot", false).apply();//saving in sharedprefrence that we are on main
                showMain();
            }
        } else {
            if (savedInstanceState != null && savedInstanceState.containsKey("onFavOrNot")) {
                onFavOrNot = savedInstanceState.getBoolean("onFavOrNot");
                if (onFavOrNot) {
                    showFavorites();
                } else {
                    showMain();
                }
            } else {
                showMain();
            }
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (!onFavOrNot) {
                        String movieID = JsonToString.getMovieId(JsonString, position);
                        ((Callback) getActivity()).onItemSelected(movieID);
                        mID = movieID;
                    } else {
                        ((Callback) getActivity()).onItemSelected(FavoriteMoviesIDs[position]);
                        mID = FavoriteMoviesIDs[position];
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mPosition = position;
            }
        });

        if (mTwopane) {
            if (savedInstanceState != null && savedInstanceState.containsKey("pos")) {
                mPosition = savedInstanceState.getInt("pos");
                mID = savedInstanceState.getString("mID");
                if (mID != null) {
                    Bundle args = new Bundle();
                    args.putString("ID", mID);
                    DetailActivityFragment fragment = new DetailActivityFragment();
                    fragment.setArguments(args);
                    // In two-pane mode, show the detail view in this activity by
                    // adding or replacing the detail fragment using a
                    // fragment transaction.
                    getFragmentManager().beginTransaction()
                            .replace(R.id.detailfragment, fragment/*, DETAILFRAGMENT_TAG*/)
                            .commit();
                }
            }
        } else {
            if (savedInstanceState != null && savedInstanceState.containsKey("scroll_pos")) {
                scroll_pos = savedInstanceState.getInt("scroll_pos");
            }
        }
        return gridView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);
        Fav_menuItem = menu.findItem(R.id.action_show_favorites);
        Main_menuItem = menu.findItem(R.id.action_show_Main);
        SettingsMenuItem = menu.findItem(R.id.action_settings);

        if (onFavOrNot) {
            Fav_menuItem.setVisible(false);//hide showFavorites
            SettingsMenuItem.setVisible(false);//hide Settings
            Main_menuItem.setVisible(true);//show back to home only
        } else {
            Fav_menuItem.setVisible(true);//show showFavorites
            SettingsMenuItem.setVisible(true);//show Settings
            Main_menuItem.setVisible(false);//hide back to home
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mTwopane) {
            outState.putString("mID", mID);
            if (mPosition != GridView.INVALID_POSITION)
                outState.putInt("pos", mPosition);
        } else {
            scroll_pos = gridView.getFirstVisiblePosition();
            outState.putInt("scroll_pos", scroll_pos);
        }

        outState.putBoolean("onFavOrNot", onFavOrNot);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_show_favorites) {
            SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
            String Favorite_Movies_IDSs = prefs.getString("FavoriteMovies_IDs_String", "");
            if (Favorite_Movies_IDSs.equals("")) {
                //no favorite movies
                Toast.makeText(getActivity(), "You didn't favorite any movie yet", Toast.LENGTH_SHORT).show();
            } else {
                if (Fav_menuItem.isVisible()) {
                    Fav_menuItem.setVisible(false);
                    SettingsMenuItem.setVisible(false);
                    Main_menuItem.setVisible(true);
                }
                showFavorites();
                onFavOrNot = true;
            }
        } else if (id == R.id.action_show_Main) {
            if (Main_menuItem.isVisible()) {
                Fav_menuItem.setVisible(true);
                SettingsMenuItem.setVisible(true);
                Main_menuItem.setVisible(false);
            }
            showMain();
            onFavOrNot = false;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showMain() {
        try {
            updateGrideView("", null);//Updating the Grid
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showFavorites() {
        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        String Favorite_Movies_IDSs = prefs.getString("FavoriteMovies_IDs_String", "");
        Movie.no_of_favorite_movies = prefs.getInt("FavoriteMovies_number", 0);
        if (Favorite_Movies_IDSs.equals("")) {
            //no favorite movies
            Toast.makeText(getActivity(), "You didn't favorite any movie yet", Toast.LENGTH_SHORT).show();
        } else {
            String Movies_IDs[] = Favorite_Movies_IDSs.split("/");
            try {
                updateGrideView("ids", Movies_IDs);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        ;
        prefs.edit().putBoolean("onFavOrNot", onFavOrNot).apply();
        super.onPause();
    }

    public void updateGrideView(String type, String[] ids) throws ExecutionException, InterruptedException {
        //getting the SortType
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortType = prefs.getString(
                getString(R.string.pref_SortType_key),
                getString(R.string.pref_sort_type_default)
        );

        if (type.equals("ids")) {
            new FetchMoviesFromIDs().execute(ids);
        } else {
            Showing_Favorites = false;
            new FetchMoviesTask().execute(sortType);
        }
    }

    String[] Fetch_posters_urls(String[] pathes) {
//        for (String s : pathes) {
//            Log.d("Ftech_posters_urls", "Pathes: " + s);
//        }
        String posters_urls[] = new String[pathes.length];

        //("http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg");
        final String IMAGE_BASE_URLS = "http://image.tmdb.org/t/p/";
        final String IMAGE_SZIE = "w185";
        for (int i = 0; i < pathes.length; i++) {
            posters_urls[i] = "" + IMAGE_BASE_URLS + IMAGE_SZIE + pathes[i];
//            Log.d("Ftech_posters_urls", "posters_urls: " + posters_urls[i]);

        }
        return posters_urls;
    }

    class FetchMoviesTask extends AsyncTask<String, Void, String> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        ProgressDialog progressDialog = new ProgressDialog(getActivity());


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
        protected String doInBackground(String... params) {


            //If there's no SortType, there's nothing to look up.
            //Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String MoviesJsonStr = null;

            try {
                // Construct the URL for the themoviedb query
                final String Movies_BASE_URL = "http://api.themoviedb.org/3/discover/movie?sort_by=";
                final String SORTING_TYPE = params[0];//the type of sorting
//                Log.v(LOG_TAG, "SORTING_TYPE: " + SORTING_TYPE);
                final String API_PARAM = "&api_key=";

                String Movies_URL = Movies_BASE_URL + SORTING_TYPE + API_PARAM + /*The API key*/"a99b28595d12eaa9e396d63b566048bc";
                Uri builtUri = Uri.parse(Movies_URL).buildUpon().build();

                URL url = new URL(builtUri.toString());
//                Log.v(LOG_TAG, "Built URI: " + builtUri.toString());

                // Create the request to themoviedb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();//getting the data
                StringBuffer buffer = new StringBuffer(); // for reading the data
                if (inputStream == null) {
                    // Nothing to do
                    MoviesJsonStr = null;
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    MoviesJsonStr = null;
                    return null;
                }
                MoviesJsonStr = buffer.toString(); //saving the data
//                Log.v(LOG_TAG, "Movies JSON String " + MoviesJsonStr);//testing

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the Movies data, there's no point in attempting
                // to parse it.
                MoviesJsonStr = null;
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return MoviesJsonStr; // returning the full JSON Data
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (result != null) {
                setJsonString(result);//saving the JSON String output from Fetch Movies Task into the global variable
                try {
                    String[] ImagesPathes = JsonToString.getMoviesPostersArray(result);//getting the posters pathes
                    String[] Correct_images_urls = Fetch_posters_urls(ImagesPathes);//correcting the urls
                    imageAdapter.setData(getActivity(), Correct_images_urls);//setting the data to the adapter
                    gridView.setAdapter(imageAdapter);//setting the adapter

                    //restoring the savedInstance State
                    if (mTwopane)
                        gridView.setSelection(mPosition);
                    else
                        gridView.setSelection(scroll_pos);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

//            if(mTwopane) {
//                try {
//                    String movieID = JsonToString.getMovieId(JsonString, 0);
//                    mID=movieID;
//                    if(mID!=null) {
//                        getFragmentManager().beginTransaction()
//                                .replace(R.id.detailfragment, new DetailActivityFragment(movieID))
//                                .commit();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }

    public class FetchMoviesFromIDs extends AsyncTask<String[], Void, String[]> {
        private final String LOG_TAG = FetchMoviesFromIDs.class.getSimpleName();
        ProgressDialog progressDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Fetching Data, Please Wait....");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String[] doInBackground(String[]... params) {

            if (params.length == 0) {
                return null;
            }

            String Fav_IDs[] = params[0];
            String res[] = new String[Fav_IDs.length];
            FavoriteMoviesIDs = new String[Fav_IDs.length];

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            for (int i = 0; i < Fav_IDs.length; i++) {
                // Will contain the raw JSON response as a string.
                String MovieJsonSts = null;

                try {
                    // Construct the URL for the themoviedb query
                    final String Movies_BASE_URL = "http://api.themoviedb.org/3/movie/";
                    final String MOVIE_ID = Fav_IDs[i];//the id
//                    Log.v(LOG_TAG, "MOVIE_ID: " + MOVIE_ID);

                    final String API_PARAM = "&api_key=";

                    String URL_movie = Movies_BASE_URL + Fav_IDs[i] + "?" + API_PARAM + /*My API key*/ "a99b28595d12eaa9e396d63b566048bc";

                    Uri builtUri = Uri.parse(URL_movie).buildUpon().build();
                    URL url_movie = new URL(builtUri.toString());
//                    Log.v(LOG_TAG, "Built URI: " + builtUri.toString());


                    // Create the request to themoviedb, and open the connection
                    urlConnection = (HttpURLConnection) url_movie.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream_movie = urlConnection.getInputStream();//getting the data
                    StringBuffer buffer = new StringBuffer(); // for reading the data

                    if (inputStream_movie == null) {
                        // Nothing to do
                        MovieJsonSts = null;
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream_movie));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }


                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        MovieJsonSts = null;
                        return null;
                    }

                    MovieJsonSts = buffer.toString(); //saving the data
//                    Log.v(LOG_TAG, "MovieJsonStr " + MovieJsonSts);//testing
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the Movies data, there's no point in attempting
                    // to parse it.
                    MovieJsonSts = null;
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();

                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }
                res[i] = MovieJsonSts;

                try {
                    FavoriteMoviesIDs[i] = JsonToString.getMovieId_from_movie(MovieJsonSts);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            try {
                res = JsonToString.getPostersPathsfromArray(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return res; // returning the full array of all favorit movies posters urls
        }

        @Override
        protected void onPostExecute(String[] URLS) {
            progressDialog.dismiss();
            if (URLS != null) {
                imageAdapter.setData(getActivity(), URLS);//setting the data to the adapter
                gridView.setAdapter(imageAdapter);//setting the adapter

                //restoring the savedInstance State
                if (mTwopane)
                    gridView.setSelection(mPosition);
                else
                    gridView.setSelection(scroll_pos);
            }

//            if(mTwopane) {
//                try {
//                    String movieID = JsonToString.getMovieId(JsonString, 0);
//                    mID=movieID;
//                    if(mID!=null) {
//                        getFragmentManager().beginTransaction()
//                                .replace(R.id.detailfragment, new DetailActivityFragment(movieID))
//                                .commit();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }

        }
    }
}