package com.company.tempo_gnettrack;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static android.content.Context.TELEPHONY_SERVICE;

public class SendData implements MyIntentService.CollectData {

    static TelephonyManager tManager;
    LocationManager locationManager;
    String Long = "";
    String Lat = "";
    int Type = 0;
    NetworkInfo wifiCheck;
    Activity context;

    public SendData(Activity context) {
        this.context = context;

    }

    public void Start() throws Exception {

        getpermitions();

        MyIntentService myIntentService = new MyIntentService(this);
        context.startService(new Intent(context, myIntentService.getClass()));

        tManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        AndroidNetworking.initialize(context);

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                0, mLocationListener);
        Log.d("mobileee", "mobile name : "+android.os.Build.MANUFACTURER + "/" + android.os.Build.MODEL);

    }

    @Override
    public void OnDataSignalChanged() {

                getData();
    }


    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {

            Long= String.valueOf(location.getLongitude());
            Lat= String.valueOf(location.getLatitude());
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N){
                getData();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    @SuppressLint("LongLogTag")
    public void getData() {
        JSONObject obj = new JSONObject();
        if (tManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                ActivityCompat.requestPermissions(context,
                        new String[]
                                {
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                },1);

                return;
            }
        }


        List<CellInfo> neighbours2 = tManager.getAllCellInfo();
        try {
            obj.put("uuid", UUID.randomUUID());
            obj.put("timestamp", Calendar.getInstance().getTimeInMillis());
            obj.put("model",android.os.Build.MANUFACTURER + "/" + android.os.Build.MODEL);
//            Log.d("mobileee", "mobile name : "+android.os.Build.MODEL);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (CellInfo info : neighbours2)
        {

            if (info.isRegistered()) {
                if (info instanceof CellInfoGsm) {
                    final CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                    final CellIdentityGsm identityGsm = ((CellInfoGsm) info).getCellIdentity();

                    try {
//                        obj.put("Type",  "2G");
                        Type=2;
                        obj.put("mcc",  identityGsm.getMcc()+"");
                        obj.put("mnc", identityGsm.getMnc()+"");
                        obj.put("lac", identityGsm.getLac()+"");
                        obj.put("cid", identityGsm.getCid()+"");
                        obj.put("rxLevel", +gsm.getDbm()+"");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            obj.put("bsic", identityGsm.getBsic()+"");
                            obj.put("frequency", identityGsm.getArfcn()+"");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (info instanceof CellInfoCdma) {
                    final CellSignalStrengthCdma cdma = ((CellInfoCdma) info).getCellSignalStrength();
                    final CellIdentityCdma identityCdma = ((CellInfoCdma) info).getCellIdentity();


                }  else if (info instanceof CellInfoWcdma) {
                    final CellSignalStrengthWcdma wcdma = ((CellInfoWcdma) info).getCellSignalStrength();
                    final CellIdentityWcdma identityWcdma = ((CellInfoWcdma) info).getCellIdentity();

                    try {
//                        obj.put("Type",  "3G");
                        Type=3;
                        obj.put("mcc", identityWcdma.getMcc()+"");
                        obj.put("mnc", identityWcdma.getMnc()+"");
                        obj.put("lac", identityWcdma.getLac()+"");
                        obj.put("rscp", wcdma.getDbm()+"");
                        obj.put("psc", identityWcdma.getPsc()+"");
                        String s =Integer.toString(identityWcdma.getCid(), 16);
                        obj.put("rncId", Integer.parseInt(s.substring(0,s.length()-4), 16)+"");
                        String ss =Integer.toString(identityWcdma.getCid(), 16);
                        obj.put("cid", Integer.parseInt(ss.substring(ss.length()-4,ss.length()), 16)+"");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                } else if (info instanceof CellInfoLte) {
                    final CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                    final CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();
                    String s =Integer.toString(identityLte.getCi(), 16);

                    try {
//                        obj.put("Type",  "4G");
                        Type=4;
                        obj.put("mcc", identityLte.getMcc()+"");
                        obj.put("mnc", identityLte.getMnc()+"");
                        obj.put("tac", identityLte.getTac()+"");
                        obj.put("pci", identityLte.getPci()+"");
                        String ss =Integer.toString(identityLte.getCi(), 16);
                        ss=ss.substring(ss.length()-2,ss.length());
                        obj.put("cid", Integer.parseInt(ss, 16)+"");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            obj.put("earfcn", identityLte.getEarfcn()+"");
                            obj.put("rssnr", lte.getRssnr()+"");
                            obj.put("rsrq", lte.getRsrq()+"");
                            obj.put("rsrp", lte.getRsrp()+"");
                        }
                        obj.put("enodeB", Integer.parseInt(s.substring(0,s.length()-2), 16)+"");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Log.i("asd", "Unknown type of cell signal!"
                            + "\n ClassName: " + info.getClass().getSimpleName()
                            + "\n ToString: " + info.toString());
                }
            }
        }

        if (!Long.equals("")||!Lat.equals("")){
            try {
                obj.put("longitude",Long+"");
                obj.put("latitude",Lat+"");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        SendToUrl(obj,Type);

    }

    private void SendToUrl(JSONObject obj, int type) {

        ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiCheck = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        String URL="http://192.168.1.33:8080";

        if (wifiCheck.isConnected()) {
            switch (type){
                case 2:
                    URL+="/rsrp/lte/";
                    break;
                case 3:
                    URL+="/rsrp/wcdma/";
                    break;
                case 4:
                    URL+="/rsrp/gsm";
                    break;
            }
            Toast.makeText(context, "Reached "+String.valueOf(obj), Toast.LENGTH_SHORT).show();
//            AndroidNetworking.post(URL)
//                    .addBodyParameter(obj)
//                    .setTag("test")
//                    .setPriority(Priority.MEDIUM)
//                    .build()
//                    .getAsJSONObject(new JSONObjectRequestListener() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            Toast.makeText(context, "Succisfull", Toast.LENGTH_SHORT).show();
//                            // do anything with response
//                        }
//                        @Override
//                        public void onError(ANError error) {
//                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
//                            // handle error
//                        }
//                    });
            // Do whatever here wifi is connected

        } else {
            // Do whatever here wifi is not connected
        }

    }

    private boolean getpermitions() throws Exception {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            throw new Exception("Access denied i need location and phone state permission");
        }
        return true;
    }

}
