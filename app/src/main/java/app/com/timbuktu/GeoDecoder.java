package app.com.timbuktu;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class GeoDecoder {

    private static final int TIMEOUT_DELAY = 20000;
    private static long LAST_TIMEOUT;

    private boolean DEBUG = false;
    private String TAG = "GeoDecoder > ";

    private Context mContext;
    private boolean mValid = false;
    Float Latitude, Longitude;

    public GeoDecoder(Context context, ExifInterface exif) {
        mContext = context;
        String attrLATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String attrLATITUDE_REF = exif
                .getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        String attrLONGITUDE = exif
                .getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        String attrLONGITUDE_REF = exif
                .getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

        if ((attrLATITUDE != null) && (attrLATITUDE_REF != null)
                && (attrLONGITUDE != null) && (attrLONGITUDE_REF != null)) {
            mValid = true;

            if (attrLATITUDE_REF.equals("N")) {
                Latitude = convertToDegree(attrLATITUDE);
            } else {
                Latitude = 0 - convertToDegree(attrLATITUDE);
            }

            if (attrLONGITUDE_REF.equals("E")) {
                Longitude = convertToDegree(attrLONGITUDE);
            } else {
                Longitude = 0 - convertToDegree(attrLONGITUDE);
            }

        }
    };

    public GeoDecoder(ExifInterface exifInterface) {
    }

    private Float convertToDegree(String stringDMS) {
        Float result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0 / S1;

        result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;
    };

    public boolean isValid() {
        return mValid;
    }

    @Override
    public String toString() {
        return (String.valueOf(Latitude) + ", " + String.valueOf(Longitude));
    }

    public int getLatitudeE6() {
        return (int) (Latitude * 1000000);
    }

    public int getLongitudeE6() {
        return (int) (Longitude * 1000000);
    }

    public double getLat() {
        return Latitude;
    }

    public double getLong() {
        return Longitude;
    }

    public List<Address> getAddress() throws IOException {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(getLat(), getLong(), 1);
            if (addresses.size() <= 0) {
                return addresses;
            }
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getAddressLine(1);
            String country = addresses.get(0).getAddressLine(2);
            String local = addresses.get(0).getLocality();
            if (DEBUG)
                Log.d(TAG, address + " - " + city + " - " + country + " - "
                        + local);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        return addresses;
    }

    public String getCity() throws IOException {
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(getLat(), getLong(), 1);
            if (addresses.size() <= 0) {
                return null;
            }
            return addresses.get(0).getLocality();

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String getCityStateCountry(Context context, double latitude, double longitude)
            throws IOException {
        String location = null;

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

        if (!addresses.isEmpty()) {
            Address address = addresses.get(0);
            location = "";

            if (address.getLocality() != null) {
                location += address.getLocality();
            }

            if (address.getAdminArea() != null) {
                if (!location.isEmpty()) {
                    location += ", ";
                }

                location += address.getAdminArea();
            }

            if (address.getCountryName() != null) {
                if (!location.isEmpty()) {
                    location += ", ";
                }

                location += address.getCountryName();
            }
        }

        return location;
    }

    public static String getLocationByGeoDecoding(Context context, double latitude,
                                                  double longitude) {
        String location = null;

        try {
            // Check Network First
            ConnectivityManager manager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            // No Network = No Timeout
            if (info != null && info.isConnectedOrConnecting()) {
                if (LAST_TIMEOUT == 0 || System.currentTimeMillis() - LAST_TIMEOUT > TIMEOUT_DELAY) {
                    location = getCityStateCountry(context, latitude, longitude);
                }
            }
        } catch (IOException e) {
            LAST_TIMEOUT = System.currentTimeMillis();
        }

        return location;
    }

    public static String getLocationByGeoDecoding(Context context, String path) {
        if (path == null) return null;

        String location = null;

        try {
            GeoDecoder geoDecoder = new GeoDecoder(context, new ExifInterface(path));
            if (!geoDecoder.isValid()) {
                return null;
            }

            location = getLocationByGeoDecoding(context,  geoDecoder.getLat(), geoDecoder.getLong());
        } catch (IOException e) {
            Log.d("", "", e);
        }

        return location;
    }
}