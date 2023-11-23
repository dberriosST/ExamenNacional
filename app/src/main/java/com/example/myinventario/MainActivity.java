package com.example.myinventario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity {

    public void Iniciar(View view){
        Intent iniciar = new Intent(this, IniciarSesion.class);
        startActivity(iniciar);
    }

    public void Registrar(View view){
        Intent registrar = new Intent(this, Registrarse.class);
    }

    static String MQTTHOST = "myinventario.cloud.shiftr.io";
    static String MQTTUSER = "myinventario";
    static String MQTTPASS = "JH7lgFA6SCvMv7lo";

    MqttAndroidClient cliente;
    MqttConnectOptions opciones;
    Boolean permisoPublicar;
    String clienteID = "";

    FloatingActionButton fab;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    RecyclerView recyclerView;
    List<DataClase> dataList;
    MyAdapter adapter;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectBroker();

        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        dataList = new ArrayList<>();
        adapter = new MyAdapter(MainActivity.this, dataList);
        recyclerView.setAdapter(adapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("Empresa XYZ");
        dialog.show();

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    DataClase dataClass = itemSnapshot.getValue(DataClase.class);
                    dataClass.setKey(itemSnapshot.getKey());
                    dataList.add(dataClass);
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return false;
            }
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, subir.class);
                startActivity(intent);
            }
        });

    }

    private void searchList(String text){
        ArrayList<DataClase> searchList = new ArrayList<>();
        for (DataClase dataClass: dataList){
            if (dataClass.getDataNombre().toLowerCase().contains(text.toLowerCase())){
                searchList.add(dataClass);
            }
        }
        adapter.searchDataList(searchList);
    }

    private void checkConnection(){
        if (this.cliente.isConnected()){
            this.permisoPublicar = true;
        } else {
            this.permisoPublicar = false;
            connectBroker();
        }
    }

    private void connectBroker(){
        this.cliente = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, this.clienteID);
        this.opciones = new MqttConnectOptions();
        this.opciones.setUserName(MQTTUSER);
        this.opciones.setPassword(MQTTPASS.toCharArray());
        try {
            IMqttToken token = this.cliente.connect(opciones);
            token.setActionCallback(new IMqttActionListener(){
                @Override
                public void onSuccess(IMqttToken asyncActionToken){
                    Toast.makeText(getBaseContext(), "Conectado", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception){
                    Toast.makeText(getBaseContext(), "Conexion Fallida", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e){
            e.printStackTrace();
        }
    }
}