package org.example.mislugares;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by C.G Ledezma on 13/04/14.
 */
public class EdicionLugar extends Activity {
   private long id;
   private Lugar lugar;
   private EditText nombre;
   private Spinner tipo;
   private EditText direccion;
   private EditText telefono;
   private EditText url;
   private EditText comentario;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.edicion_lugar);

      Bundle extras = getIntent().getExtras();
      id = extras.getLong("id", -1);
      lugar = Lugares.elemento((int) id);

      nombre = (EditText) findViewById(R.id.nombre);
      nombre.setText(lugar.getNombre());

      direccion = (EditText) findViewById(R.id.direccion);
      direccion.setText(lugar.getDireccion());

      telefono = (EditText) findViewById(R.id.telefono);
      telefono.setText(Integer.toString(lugar.getTelefono()));

      url = (EditText) findViewById(R.id.url);
      url.setText(lugar.getUrl());

      comentario = (EditText) findViewById(R.id.comentario);
      comentario.setText(lugar.getComentario());

      tipo = (Spinner) findViewById(R.id.tipo);
      ArrayAdapter<String> adaptador =
         new ArrayAdapter<String>(this,
                                  android.R.layout.simple_spinner_item,
                                  TipoLugar.getNombres());
      adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      tipo.setAdapter(adaptador);
      tipo.setSelection(lugar.getTipo().ordinal());
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.edicion_lugar, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch(item.getItemId()) {
         case R.id.cancelar:
            return true;
         case R.id.guardar_lugar:
            lugar.setNombre(nombre.getText().toString());
            lugar.setTipo(TipoLugar.values()[tipo.getSelectedItemPosition()]);
            lugar.setDireccion(direccion.getText().toString());
            lugar.setTelefono(Integer.parseInt(telefono.getText().toString()));
            lugar.setUrl(url.getText().toString());
            lugar.setComentario(comentario.getText().toString());
            Lugares.actualizarLugar((int) id, lugar);
            finish();
         default:
            super.onOptionsItemSelected(item);
      }
      return true;
   }
}
