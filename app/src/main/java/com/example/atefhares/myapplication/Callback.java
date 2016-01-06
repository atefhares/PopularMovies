package com.example.atefhares.myapplication;

/**
 * Created by Atef Hares on 03-Jan-16.
 */

/**
 * A callback interface that all activities containing this fragment must
 * implement. This mechanism allows activities to be notified of item
 * selections.
 */
public interface Callback {
    /**
     * DetailFragmentCallback for when an item has been selected.
     */
    public void onItemSelected(String id);
}