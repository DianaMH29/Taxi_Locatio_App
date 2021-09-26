package com.example.udp_tcpapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private EditText ip1, udpport;
    private TextView lat, lon, hora1, fech;
    private Button env1;
    private Socket user;
    private String smsave;
    private DatagramSocket socketudp;
    public String coords;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private final long MIN_TIME = 4000;
    private final long MIN_DIST = 0;
    public LatLng latLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fech = findViewById(R.id.fecha1);
        hora1 = findViewById(R.id.hora);
        lon = findViewById(R.id.long1);
        lat = findViewById(R.id.lat1);
        ip1 = findViewById(R.id.ip);
        udpport = findViewById(R.id.port2);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, PackageManager.PERMISSION_GRANTED);


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try {

                    Date d = new Date();

                    CharSequence fecha = DateFormat.format("MMMM d, yyyy ", d.getTime());
                    String fecha_ = String.valueOf(fecha);

                    latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    Long time = location.getTime();
                    Date date = new Date(time);

                    SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                    String h1 = sdf.format(date);

                    Log.i("tiempo", "tiempo: " + h1);

                    String myLatitude = String.valueOf(location.getLatitude());
                    String myLongitude = String.valueOf(location.getLongitude());
                    coords = myLatitude + "," + myLatitude + "," + myLongitude + "," + h1 + "," + fecha_ + "," + fecha_;
                    Log.i("Coords", "Coords: " + coords);

                    lat.setText(myLatitude);
                    lon.setText(myLongitude);
                    hora1.setText(h1);
                    fech.setText(fecha_);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }


        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (coords != null && ip1 != null && udpport != null) {

                    byte[] buffer;

                    try {
                        // Para UDP
                        DatagramSocket socketudp = new DatagramSocket();

                        try {

                            InetAddress address = InetAddress.getByName(ip1.getText().toString());
                            int port = Integer.parseInt(udpport.getText().toString());

                            buffer = coords.getBytes();

                            DatagramPacket peticion = new DatagramPacket(buffer, buffer.length, address, port);
                            socketudp.send(peticion);
                            Log.i("Confirmation", "Packet Sent!");

                        }catch(NumberFormatException ex){

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, 0, 3000);
    }
}

