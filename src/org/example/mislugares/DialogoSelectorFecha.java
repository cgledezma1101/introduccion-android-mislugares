package org.example.mislugares;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * Created by C.G Ledezma on 28/04/14.
 */
public class DialogoSelectorFecha extends DialogFragment {
   private DatePickerDialog.OnDateSetListener escuchador;

   public void setOnDateSetListener(DatePickerDialog.OnDateSetListener escuchador) {
      this.escuchador = escuchador;
   }

   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {
      Calendar calendario = Calendar.getInstance();
      Bundle args = this.getArguments();
      if(args != null) {
         long fecha = args.getLong("fecha");
         calendario.setTimeInMillis(fecha);
      }
      int agno = calendario.get(Calendar.YEAR);
      int mes = calendario.get(Calendar.MONTH);
      int dia = calendario.get(Calendar.DAY_OF_MONTH);
      return new DatePickerDialog(getActivity(), escuchador, agno, mes, dia);
   }
}
