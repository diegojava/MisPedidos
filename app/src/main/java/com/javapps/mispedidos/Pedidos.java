package com.javapps.mispedidos;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Diego on 15/08/2017.
 */

        public class Pedidos extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final String LOGTAG = "android-localizacion";

    private static final int PETICION_PERMISO_LOCALIZACION = 101;

    TextView username;
    private GoogleApiClient apiClient;

    private TextView lblLatitud;
    private TextView lblLongitud;
    private ToggleButton btnActualizar;
    Button enviarPedido, calcularPedido;
    RequestQueue requestQueue;
    TextView total, lista;
    CheckBox checkbox1, checkbox2, checkbox3, checkbox4, checkbox5, checkbox6;
    Integer suma = 0, i = 0, check1 = 15, check2 = 10, check3 = 12, check4 = 16, check5 = 9, check6 = 5;
    EditText textMessage, direccion;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // note that use pedidos.xmlad of our single_post.xml
        setContentView(R.layout.pedidos);



        username = (TextView) findViewById(R.id.textView);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String datoNombre = (String) extras.get("username");
        username.setText(datoNombre);

        lblLatitud = (TextView) findViewById(R.id.lblLatitud);
        lblLongitud = (TextView) findViewById(R.id.lblLongitud);
        btnActualizar = (ToggleButton) findViewById(R.id.btnActualizar);
        checkbox1 = (CheckBox) findViewById(R.id.checkBox1);
        checkbox2 = (CheckBox) findViewById(R.id.checkBox2);
        checkbox3 = (CheckBox) findViewById(R.id.checkBox3);
        checkbox4 = (CheckBox) findViewById(R.id.checkBox4);
        checkbox5 = (CheckBox) findViewById(R.id.checkBox5);
        checkbox6 = (CheckBox) findViewById(R.id.checkBox6);
        direccion = (EditText) findViewById(R.id.txtDireccion);

        lista = (TextView) findViewById(R.id.txtLista);
        total = (TextView) findViewById(R.id.total);


        calcularPedido = (Button) findViewById(R.id.btnCalcular);
        enviarPedido = (Button) findViewById(R.id.btnEnviar);
        requestQueue = Volley.newRequestQueue(this);


        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();


      enviarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                enviarDatos();

            }
        });

        calcularPedido.setOnClickListener(new View.OnClickListener() {

            List<String> listaProductos = new ArrayList<String>();

            @Override
            public void onClick(View view) {
                suma = 0;
                i = 0;
                listaProductos.clear();
                if(checkbox1.isChecked())
                {
                    suma += check1;
                    listaProductos.add(i, "Refresco 600ml \n");
                    i++;
                }
                if(checkbox2.isChecked())
                {
                    suma += check2;
                    listaProductos.add(i, "Botanas 150gr \n");
                    i++;
                }
                if(checkbox3.isChecked())
                {
                    suma += check3;
                    listaProductos.add(i, "Cerveza 355ml \n");
                    i++;
                }
                if(checkbox4.isChecked())
                {
                    suma += check4;
                    listaProductos.add(i, "Leche 1lt \n");
                    i++;
                }
                if(checkbox5.isChecked())
                {
                    suma += check5;
                    listaProductos.add(i, "Jabón \n");
                    i++;
                }
                if(checkbox6.isChecked())
                {
                    suma += check6;
                    listaProductos.add(i, "Chicles");
                    i++;
                }

                total.setText(suma.toString());
                lista.setText(listaProductos.toString());

            }
        });
    }



    private void enviarDatos() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Pedido enviado")
                .setTitle("Tu pedido ha sido enviado satisfactoriamente")
                .setCancelable(false)
                .setNeutralButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();
                                startActivity(getIntent());
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();

        String url = "http://mispedidos.x10.bz/app/guardar.php";
        StringRequest request = new StringRequest(Request.Method.POST,url,new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

            }
        }){
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("direccion", direccion.getText().toString());
                map.put("total", total.getText().toString());
                map.put("usuario", username.getText().toString());
                map.put("longitud", lblLongitud.getText().toString());
                map.put("latitud", lblLatitud.getText().toString());
                map.put("descripcion", lista.getText().toString());
                return map;
            }
        };
        requestQueue.add(request);

    }


    private void updateUI(Location loc) {
        if (loc != null) {
            lblLatitud.setText(String.valueOf(loc.getLatitude()));
            lblLongitud.setText(String.valueOf(loc.getLongitude()));
        } else {
            lblLatitud.setText("Latitud: (desconocida)");
            lblLongitud.setText("Longitud: (desconocida)");
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //Se ha producido un error que no se puede resolver automáticamente
        //y la conexión con los Google Play Services no se ha establecido.

        Log.e(LOGTAG, "Error grave al conectar con Google Play Services");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Conectado correctamente a Google Play Services

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {

            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);

            updateUI(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Se ha interrumpido la conexión con Google Play Services

        Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PETICION_PERMISO_LOCALIZACION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Permiso concedido

                @SuppressWarnings("MissingPermission")
                Location lastLocation =
                        LocationServices.FusedLocationApi.getLastLocation(apiClient);

                updateUI(lastLocation);

            } else {
                //Permiso denegado:
                //Deberíamos deshabilitar toda la funcionalidad relativa a la localización.

                Log.e(LOGTAG, "Permiso denegado");
            }
        }
    }
}