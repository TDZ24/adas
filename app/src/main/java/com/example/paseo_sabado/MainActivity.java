package com.example.paseo_sabado;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText jetcodigo, jetnombre, jetciudad, jetcantidad;
    CheckBox jcbactivo;
    String codigo, nombre, ciudad, cantidad, codigo_id;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    byte sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ocultar el menu de titulos y asociar objetos
        getSupportActionBar().hide();
        jetcantidad=findViewById(R.id.etcantidad);
        jetciudad=findViewById(R.id.etciudad);
        jetcodigo=findViewById(R.id.etcodigo);
        jetnombre=findViewById(R.id.etnombre);
        jcbactivo=findViewById(R.id.cbactivo);
        sw=0;
    }
    public void Adicionar(View view){
        codigo=jetcodigo.getText().toString();
        nombre=jetnombre.getText().toString();
        ciudad=jetciudad.getText().toString();
        cantidad=jetcantidad.getText().toString();
        if(codigo.isEmpty() || nombre.isEmpty() || ciudad.isEmpty() || cantidad.isEmpty()){
            Toast.makeText(this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();
        }
        else{
            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            user.put("code", codigo);
            user.put("nombre", nombre);
            user.put("ciudad", ciudad);
            user.put("cantidad", cantidad);
            user.put("activo", "si");

            // Add a new document with a generated ID
            db.collection("Factura")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(MainActivity.this, "Datos Guardados", Toast.LENGTH_SHORT).show();
                            Limpiar_campos();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Error Guardando datos", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    public void Consultar (View view){
        codigo=jetcodigo.getText().toString();
        if (codigo.isEmpty()){
            Toast.makeText(this, "Codigo requerido para la consulta", Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();
        }
        else {
            db.collection("Factura")
                    .whereEqualTo("code",codigo)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    sw=1;
                                    codigo_id= document.getId();
                                    Toast.makeText(MainActivity.this, "Documento Encontrado", Toast.LENGTH_SHORT).show();
                                    jetnombre.setText(document.getString("nombre"));
                                    jetciudad.setText(document.getString("ciudad"));
                                    jetcantidad.setText(document.getString("cantidad"));
                                    if (document.getString("activo").equals("si"))
                                        jcbactivo.setChecked(true);
                                    else
                                        jcbactivo.setChecked(false);
                                    //Log.d(TAG, document.getId() + " => " + document.getData());
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Error Consultando Documento", Toast.LENGTH_SHORT).show();
                                //Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
    }

    public void Modificar(View view){
        if (sw == 0){
            Toast.makeText(this, "Para modificar debe primero consultar", Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();
        }
        else{
            codigo=jetcodigo.getText().toString();
            nombre=jetnombre.getText().toString();
            ciudad=jetciudad.getText().toString();
            cantidad=jetcantidad.getText().toString();
            if (codigo.isEmpty() || nombre.isEmpty() || ciudad.isEmpty() || cantidad.isEmpty()){
                Toast.makeText(this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
                jetcodigo.requestFocus();
            }
            else{
                Map<String, Object> user = new HashMap<>();
                user.put("code", codigo);
                user.put("nombre", nombre);
                user.put("ciudad", ciudad);
                user.put("cantidad", cantidad);
                user.put("activo", "si");

                // Modify a new document with a generated ID
                db.collection("Factura").document(codigo_id)
                        .set(user)

                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this,"Documento actualizado correctmente...",Toast.LENGTH_SHORT).show();
                                Limpiar_campos();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,"Error actualizando Documento...",Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        }
    }

    public void Eliminar(View view){
        if (sw == 0){
            Toast.makeText(this, "Para eliminar debe primero consultar", Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();
        }
        else {
            // Modify a new document with a generated ID
            db.collection("Factura").document(codigo_id)
                    .delete()

                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MainActivity.this,"Documento eliminado correctmente...",Toast.LENGTH_SHORT).show();
                            Limpiar_campos();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this,"Error al eliminar",Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void Anular(View view){
        if (sw == 0){
            Toast.makeText(this, "Debe Primero Consultar oara anular", Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();
        }
        else {
            codigo=jetcodigo.getText().toString();
            nombre=jetnombre.getText().toString();
            ciudad=jetciudad.getText().toString();
            cantidad=jetcantidad.getText().toString();
            if (codigo.isEmpty() || nombre.isEmpty() || ciudad.isEmpty() || cantidad.isEmpty()){
                Toast.makeText(this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
                jetcodigo.requestFocus();
            }
            else{
                Map<String, Object> user = new HashMap<>();
                user.put("code", codigo);
                user.put("nombre", nombre);
                user.put("ciudad", ciudad);
                user.put("cantidad", cantidad);
                user.put("activo", "no");

                // Modify a new document with a generated ID
                db.collection("Factura").document(codigo_id)
                        .set(user)

                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this,"Documento anulado correctmente...",Toast.LENGTH_SHORT).show();
                                Limpiar_campos();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,"Error al anular...",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    public void Cancelar(View view){
        Limpiar_campos();
    }


    private void Limpiar_campos(){
        jetcodigo.setText("");
        jetnombre.setText("");
        jetciudad.setText("");
        jetcantidad.setText("");
        jetcodigo.requestFocus();
        jcbactivo.setChecked(false);
        sw=0;
    }
}