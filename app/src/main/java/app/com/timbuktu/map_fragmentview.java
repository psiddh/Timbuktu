package app.com.timbuktu;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class map_fragmentview extends Fragment {
    private ArrayList<Integer> eventIDs;
    private SimpleDateFormat simpleDateFormat;
    // Key: Marker item
    // value: Integer[2] - [0]: eventID and [1]: click count
    private Map<Marker, Integer[]> allMarkersMap = new HashMap<Marker, Integer[]>();
    private SupportMapFragment fragment;
    private GoogleMap gMap = null;

    private final int DEFAULT_ZOOM_LEVEL = 12;
    private final int NO_ZOOM_LEVEL = -1;

    private final int DEFAULT_CLICK_STATE = 1;
    private final int USER_CLICKED_TWICE_STATE = 2;

    private float currentZoom = NO_ZOOM_LEVEL;

    public interface Listener {
        public void onItemSelected(String text);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        simpleDateFormat = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a", Locale.getDefault());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragmentview, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.location_map);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.location_map, fragment).commit();
        }
        gMap = fragment.getMap();
        //fragment.getMapAsync(this);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        currentZoom = NO_ZOOM_LEVEL;
    }

    @Override
    public void onPause() {
        super.onPause();
        currentZoom = NO_ZOOM_LEVEL;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setEventIds(List<Integer> eventIds) {
        try {
            if (this.eventIDs != null)
                this.eventIDs.clear();
            this.eventIDs = new ArrayList<>(eventIds);
            if (gMap != null) {
                gMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        //addMarkers();
                    }
                });
            }

        } catch (Exception e) {

        }
    }


    /*private void addMarkers() {
        int inValidMarkers = 0;

        final ArrayList<Marker> markers = new ArrayList<>();
        for (int i = 0; i < events.size(); i++) {
            if (null == events.get(i).locations) {
                ++inValidMarkers;
                continue;
            }
            double latitude = events.get(i).locations[0].latitude;
            double longitude = events.get(i).locations[0].longitude;

            LatLng ll = new LatLng(latitude, longitude);
            BitmapDescriptor bitmapMarker;
            Uri uri = events.get(i).collage;
            bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
            // TBD: Need to work on this. We need to think about UX / UI scenarios
            //bitmapMarker = (uri == null) ? BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE) :
            //                                                       BitmapDescriptorFactory.fromPath(new File(uri.toString()).getAbsolutePath());

            String title = events.get(i).title;
            if (title == null || title.isEmpty()) {
                long time = events.get(i).startTime;
                title = simpleDateFormat.format(time);
            }

            String snippet = events.get(i).locations[0].name;
            String description = events.get(i).description;
            if (description != null && description != "null") {
                snippet += events.get(i).description;
            }
            final Marker locationMarker = gMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(title)
                    .snippet(snippet)
                    .icon(bitmapMarker));
            //locationMarker.showInfoWindow();
            markers.add(locationMarker);
            allMarkersMap.put(locationMarker, new Integer[]{events.get(i).id, DEFAULT_CLICK_STATE });
        }

        if (events.size() > 1) {
            // TBD : We should address this condition as well if (events.size() - inValidMarkers == 1)
            LatLngBounds.Builder builder = LatLngBounds.builder();
            for(Marker m : markers){
                builder.include(m.getPosition());
            }
            LatLngBounds bounds = builder.build();
            gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));

            //loopAnimateCamera(markers);
        }
        if (events.size() == 1) {
            double latitude = 0.0;
            double longitude = 0.0;
            // For 1 marker : Special case to set the zoom level
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude))      // Sets the center of the map to location user
                    .zoom(DEFAULT_ZOOM_LEVEL)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                if (allMarkersMap.containsKey(marker)) {
                    Integer[] values = allMarkersMap.get(marker);
                    if (values[1] == DEFAULT_CLICK_STATE) {

                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                marker.getPosition(), (currentZoom == NO_ZOOM_LEVEL) ?  DEFAULT_ZOOM_LEVEL : currentZoom));

                        marker.showInfoWindow();
                        values[1] = USER_CLICKED_TWICE_STATE;
                        Toast.makeText(getActivity(), "Click one more time to see its event details", Toast.LENGTH_SHORT).show();

                    } else if (values[1] == USER_CLICKED_TWICE_STATE) {


                        // reset
                        values[1] = DEFAULT_CLICK_STATE;
                    }
                    return true;
                }

                // reset other markers click count
                for(Map.Entry<Marker, Integer[]>  markerEntry : allMarkersMap.entrySet() ) {
                    Marker key = markerEntry.getKey();
                    Integer[] values = markerEntry.getValue();

                    if (marker != key)
                        values[1] = DEFAULT_CLICK_STATE;

                }
                return false;

            }
        });

        gMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition pos) {
                if (pos.zoom != currentZoom){
                    currentZoom = pos.zoom;
                    //if (markers != null && markers.size() > 1)
                    loopAnimateCamera(markers);
                }
            }
        });

        if (inValidMarkers != 0)
            Toast.makeText(getActivity(), "Atleast " + inValidMarkers + " events do not have valid latitude / longitude set.", Toast.LENGTH_SHORT).show();

    }*/

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            currentZoom = NO_ZOOM_LEVEL;
            if (gMap != null)
                gMap.clear();
            allMarkersMap.clear();
        }
    }

    // TBD: Should we be smarter about this from memory point of view ?
    private void loopAnimateCamera(final ArrayList<Marker> updates) {
        final Marker marker = updates.remove(0);
        updates.add(marker);
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                marker.getPosition(), (currentZoom == NO_ZOOM_LEVEL) ?  DEFAULT_ZOOM_LEVEL : currentZoom), 3000, new GoogleMap.CancelableCallback() {

            @Override
            public void onFinish() {
                marker.showInfoWindow();
                loopAnimateCamera(updates);
            }

            @Override
            public void onCancel() {
                //Log.debug(TAG, "camera animation cancelled");
            }
        });
    }
}
