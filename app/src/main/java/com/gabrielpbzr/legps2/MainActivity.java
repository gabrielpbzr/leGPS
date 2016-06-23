package com.gabrielpbzr.legps2;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LocationListener {

    //Pra gente saber quais foram as nossas chamadas no Logcat ;)
    public final static String TAG = "LE_GPS_2";

    // Aguardar 5s entre um request e outro
    private final long MIN_REQUEST_TIME = 5000;

    // Considerar pelo menos 1m entre uma leitura e outra
    private final float MIN_DISTANCE = 1;


    private LocationManager locationManager;
    private LocationProvider locationProvider;
    private TextView tvLatitude;
    private TextView tvLongitude;
    private TextView tvSpeed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.tvLatitude = (TextView) findViewById(R.id.tvLatitude);
        this.tvLongitude = (TextView) findViewById(R.id.tvLongitude);
        this.tvSpeed = (TextView) findViewById(R.id.tvSpeed);

        // Vamos pedir ao sistema o servico de localizacao
        this.locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        /*
        Determinamos aqui o nosso provedor de localizacao (Nesse caso o GPS). Aqui poderiamos utilizar
        outro provedor (LocationManager.NETWORK_PROVIDER) ou deixar que o locationManager escolhesse
        o melhor tipo de provedor baseado no criterio que estabelecessemos (melhor precisao,
        economia de energia...)
         */
        this.locationProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
        boolean isGPSEnabled = this.isGPSEnabled();

        if (!isGPSEnabled) {
            this.openLocationSettings();
        }
    }

    /**
     * Inicia a intent que vai abrir as configuracoes de localizacao para que o usuario as ative.
     */
    private void openLocationSettings() {
        Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(gpsIntent);
    }

    /**
     * Verifica se o GPS esta habilitado.
     * @return boolean
     */
    private boolean isGPSEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onResume() {
        super.onResume();
        try{
            if (!isGPSEnabled()) {
                this.openLocationSettings();
            }
            //Vamos ouvir o GPS assim que a activity entrar em execucao
            this.locationManager.requestLocationUpdates(this.locationProvider.getName(), MIN_REQUEST_TIME, MIN_DISTANCE, this);
            Log.i(TAG, "GPS na escuta");
        } catch(SecurityException se) {
            //O usuario nao nos deixou usar o GPS!
            return;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try{
            //Vamos parar de ouvir o GPS quando a activity for pausada (Economizar recursos e bateria!)
            this.locationManager.removeUpdates(this);
            Log.i(TAG, "GPS Parado");
        }catch(SecurityException se){
            //O usuario nao nos deixou usar o GPS!
            return;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Nova leitura do GPS recebida");
        // Vamos formatar a longitude pra 4 digitos de precisao
        String longitude = String.format("%.5f", location.getLongitude());
        this.tvLongitude.setText(longitude);

        // Vamos formatar a latitude pra 4 digitos de precisao
        String latitude = String.format("%.5f", location.getLatitude());
        this.tvLatitude.setText(latitude);
        // Vamos formatar a velocidade pra 1 digito de precisao
        String speed = String.format("%.1f", location.getSpeed());
        this.tvSpeed.setText(speed);
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
}
