package org.example.mislugares;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by C.G Ledezma on 25/03/14.
 */
public class VistaLugarFragment extends Fragment
      implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
   private long id;
   private Lugar lugar;
   private Uri uriFoto;
   final static int RESULTADO_EDITAR = 1;
   final static int RESULTADO_GALERIA = 2;
   final static int RESULTADO_FOTO = 3;

   public void actualizarVistas(final long id) {
      this.id = id;
      lugar = Lugares.elemento((int) id);
      if (lugar != null) {
         View v = getView();
         TextView nombre = (TextView) v.findViewById(R.id.nombre);
         nombre.setText(lugar.getNombre());

         ImageView logo_tipo = (ImageView) v.findViewById(R.id.logo_tipo);
         logo_tipo.setImageResource(lugar.getTipo().getRecurso());

         TextView tipo = (TextView) v.findViewById(R.id.tipo);
         tipo.setText(lugar.getTipo().getTexto());

         if(lugar.getDireccion() == null || lugar.getDireccion().equals("")) {
            v.findViewById(R.id.direccion).setVisibility(View.GONE);
            v.findViewById(R.id.logo_direccion).setVisibility(View.GONE);
         } else {
            TextView direccion = (TextView) v.findViewById(R.id.direccion);
            direccion.setText(lugar.getDireccion());
         }

         if(lugar.getTelefono() == 0){
            v.findViewById(R.id.telefono).setVisibility(View.GONE);
            v.findViewById(R.id.logo_telefono).setVisibility(View.GONE);
         } else {
            TextView telefono = (TextView) v.findViewById(R.id.telefono);
            telefono.setText(Integer.toString(lugar.getTelefono()));
         }

         if(lugar.getUrl() == null || lugar.getUrl().equals("")){
            v.findViewById(R.id.url).setVisibility(View.GONE);
            v.findViewById(R.id.logo_url).setVisibility(View.GONE);
         } else {
            TextView url = (TextView) v.findViewById(R.id.url);
            url.setText(lugar.getUrl());
         }

         if(lugar.getComentario() == null || lugar.getComentario().equals("")){
            v.findViewById(R.id.comentario).setVisibility(View.GONE);
            v.findViewById(R.id.logo_comentarios).setVisibility(View.GONE);
         } else {
            TextView comentario = (TextView) v.findViewById(R.id.comentario);
            comentario.setText(lugar.getComentario());
         }

         TextView fecha = (TextView) v.findViewById(R.id.fecha);
         fecha.setText(DateFormat.getDateInstance().format(
               new Date(lugar.getFecha())));

         TextView hora = (TextView) v.findViewById(R.id.hora);
         hora.setText(DateFormat.getTimeInstance().format(
               new Date(lugar.getFecha())));

         RatingBar valoracion = (RatingBar) v.findViewById(R.id.valoracion);
         valoracion.setOnRatingBarChangeListener(null);
         valoracion.setRating(lugar.getValoracion());
         valoracion.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
               lugar.setValoracion(v);
               Lugares.actualizarLugar((int) id, lugar);
            }
         });

         ponerFoto((ImageView) v.findViewById(R.id.foto), lugar.getFoto());
      }
   }

   public void cambiarFecha() {
      DialogoSelectorFecha dialogoFecha = new DialogoSelectorFecha();
      dialogoFecha.setOnDateSetListener(this);
      Bundle args = new Bundle();
      args.putLong("fecha", lugar.getFecha());
      dialogoFecha.setArguments(args);
      dialogoFecha.show(getActivity().getSupportFragmentManager(), "selectorFecha");
   }

   public void cambiarHora() {
      DialogoSelectorHora dialogoHora = new DialogoSelectorHora();
      dialogoHora.setOnTimeSetListener(this);
      Bundle args = new Bundle();
      args.putLong("fecha", lugar.getFecha());
      dialogoHora.setArguments(args);
      dialogoHora.show(getActivity().getSupportFragmentManager(), "selectorHora");
   }

   protected void confirmarEliminacion(int id) {
      final int idLugar = id;
      new AlertDialog.Builder(getActivity())
            .setTitle("Confirmación de eliminación")
            .setMessage("¿Estás seguro que quieres eliminar este lugar?")
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                  Lugares.borrar((int) idLugar);
                  SelectorFragment selectorFragment =
                     (SelectorFragment) getActivity().getSupportFragmentManager()
                                                     .findFragmentById(R.id.selector_fragment);
                  if(selectorFragment == null) {
                     getActivity().finish();
                  } else {
                     ((MainActivity) getActivity()).muestraLugar(Lugares.primerId());
                     ((MainActivity) getActivity()).actualizarLista();
                  }
               }
            })
            .setNegativeButton("Cancelar", null)
            .show();
   }

   public void eliminarFoto(View view) {
      lugar.setFoto(null);
      ponerFoto((ImageView) getView().findViewById(R.id.foto), null);
      Lugares.actualizarLugar((int) id, lugar);
   }

   public void galeria(View view) {
      Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
      intent.addCategory((Intent.CATEGORY_OPENABLE));
      intent.setType("image/*");
      startActivityForResult(intent, RESULTADO_GALERIA);
   }

   public void llamadaTelefono(View view) {
      startActivity(new Intent(Intent.ACTION_DIAL,
                               Uri.parse("tel:" + lugar.getTelefono())));
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      if(requestCode == RESULTADO_EDITAR) {
         actualizarVistas(id);
         getView().findViewById(R.id.scrollView1).invalidate();
      } else if (requestCode == RESULTADO_GALERIA &&
                 resultCode == Activity.RESULT_OK) {
         lugar.setFoto(data.getDataString());
         Lugares.actualizarLugar((int) id, lugar);
         ponerFoto((ImageView) getView().findViewById(R.id.foto), lugar.getFoto());
      } else if (requestCode == RESULTADO_FOTO &&
                 resultCode == Activity.RESULT_OK &&
                 lugar != null && uriFoto != null) {
         lugar.setFoto(uriFoto.toString());
         Lugares.actualizarLugar((int) id, lugar);
         ponerFoto((ImageView) getView().findViewById(R.id.foto), lugar.getFoto());
      }
   }

   @Override
   public View onCreateView(LayoutInflater inflador, ViewGroup contenedor,
                            Bundle savedInstanceState) {
      View vista = inflador.inflate(R.layout.vista_lugar, contenedor, false);
      setHasOptionsMenu(true);

      LinearLayout pUrl = (LinearLayout) vista.findViewById(R.id.p_url);
      pUrl.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            pgWeb(null);
         }
      });

      LinearLayout pMapa = (LinearLayout) vista.findViewById(R.id.p_mapa);
      pMapa.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            verMapa(null);
         }
      });

      LinearLayout pTelefono = (LinearLayout) vista.findViewById(R.id.p_telefono);
      pTelefono.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            llamadaTelefono(null);
         }
      });

      ImageView pEliminarFoto = (ImageView) vista.findViewById(R.id.p_eliminar_foto);
      pEliminarFoto.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            eliminarFoto(null);
         }
      });

      ImageView pTomarFoto = (ImageView) vista.findViewById(R.id.p_tomar_foto);
      pTomarFoto.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            tomarFoto(null);
         }
      });

      ImageView pGaleria = (ImageView) vista.findViewById(R.id.p_galeria);
      pGaleria.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            galeria(null);
         }
      });

      ImageView iconoHora = (ImageView) vista.findViewById(R.id.icono_hora);
      iconoHora.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            cambiarHora();
         }
      });

      ImageView iconoFecha = (ImageView) vista.findViewById(R.id.icono_fecha);
      iconoFecha.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            cambiarFecha();
         }
      });
      return vista;
   }

   @Override
   public void onActivityCreated(Bundle state) {
      super.onActivityCreated(state);
      Bundle extras = getActivity().getIntent().getExtras();
      if(extras != null) {
         id = extras.getLong("id", -1);
         if(id != -1) {
            actualizarVistas(id);
         }
      }
   }

   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
      inflater.inflate(R.menu.vista_lugar, menu);
      super.onCreateOptionsMenu(menu, inflater);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item){
      Intent intent;
      switch(item.getItemId()) {
         case R.id.accion_compartir:
            intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT,
                            lugar.getNombre() + " - " + lugar.getUrl());
            startActivity(intent);
            return true;
         case R.id.accion_llegar:
            verMapa(null);
            return true;
         case R.id.accion_editar:
            intent = new Intent(getActivity(), EdicionLugar.class);
            intent.putExtra("id", id);
            startActivityForResult(intent, RESULTADO_EDITAR);
            return true;
         case R.id.accion_borrar:
            confirmarEliminacion((int) id);
            return true;
         default:
            return super.onOptionsItemSelected(item);
      }
   }

   public void pgWeb(View view) {
      startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(lugar.getUrl())));
   }

   protected void ponerFoto(ImageView imageView, String uri) {
      if(uri != null) {
         imageView.setImageURI(Uri.parse(uri));
      } else {
         imageView.setImageBitmap(null);
      }
   }

   public void tomarFoto(View view) {
      Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
      uriFoto = Uri.fromFile(
                       new File(Environment.getExternalStorageDirectory() +
                                File.separator + "img_" +
                                (System.currentTimeMillis() / 1000) + ".jpg"));
      intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
      startActivityForResult(intent, RESULTADO_FOTO);
   }

   public void verMapa(View view) {
      Uri uri;
      double lat = lugar.getPosicion().getLatitud();
      double lon = lugar.getPosicion().getLongitud();
      if(lat != 0 || lon != 0) {
         uri = Uri.parse("geo:" + lat + "," + lon);
      } else {
         uri = Uri.parse("geo:0,0?q=" + lugar.getDireccion());
      }
      Intent intent = new Intent(Intent.ACTION_VIEW, uri);
      startActivity(intent);
   }

   @Override
   public void onTimeSet(TimePicker vista, int hora, int minuto) {
      Calendar calendario = Calendar.getInstance();
      calendario.setTimeInMillis(lugar.getFecha());
      calendario.set(Calendar.HOUR_OF_DAY, hora);
      calendario.set(Calendar.MINUTE, minuto);
      lugar.setFecha(calendario.getTimeInMillis());
      Lugares.actualizarLugar((int) id, lugar);
      TextView tHora = (TextView) getView().findViewById(R.id.hora);
      SimpleDateFormat formato = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
      tHora.setText(formato.format(new Date(lugar.getFecha())));
   }

   @Override
   public void onDateSet(DatePicker view, int agno, int mes, int dia) {
      Calendar calendario = Calendar.getInstance();
      calendario.setTimeInMillis(lugar.getFecha());
      calendario.set(Calendar.YEAR, agno);
      calendario.set(Calendar.MONTH, mes);
      calendario.set(Calendar.DAY_OF_MONTH, dia);

      lugar.setFecha(calendario.getTimeInMillis());
      Lugares.actualizarLugar((int) id, lugar);

      TextView tFecha = (TextView) getView().findViewById(R.id.fecha);
      DateFormat formato = DateFormat.getDateInstance();
      tFecha.setText(formato.format(new Date(lugar.getFecha())));
   }
}