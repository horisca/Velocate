package com.Velocate.getaccesstolocationservices_mapversion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MapMarkerInfoWindow implements GoogleMap.InfoWindowAdapter {

    private final View Window;

    //Constructor
    @SuppressLint("InflateParams")
    MapMarkerInfoWindow(Context mContext) {
        Window = LayoutInflater.from(mContext).inflate(R.layout.mapmarker, null); //mapmarker.xml file is set as layout file for the marker info window
    }

    //Method in which the title and snippet of the info window are set
    private void renderWindowText(Marker marker, View view) {
        String title = marker.getTitle();
        TextView newTitle = view.findViewById(R.id.title);
        String snippet = marker.getSnippet();
        TextView newSnippet = view.findViewById(R.id.snippet);

        if(!title.equals("")){
            newTitle.setText(title);
        }
        if(!snippet.equals("")){
            newSnippet.setText(snippet);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, Window);
        return Window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, Window);
        return Window;
    }
}
