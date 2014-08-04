package org.example.mislugares;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * Created by C.G Ledezma on 28/04/14.
 */
public class SelectorFragment extends Fragment implements AdapterView.OnItemClickListener{
   @Override
   public View onCreateView(LayoutInflater inflador, ViewGroup contenedor, Bundle savedInstanceState) {
      View vista = inflador.inflate(R.layout.fragment_selector, contenedor, false);
      return vista;
   }

   @Override
   public void onActivityCreated(Bundle state) {
      super.onActivityCreated(state);
      BaseAdapter adaptador = new AdaptadorCursorLugares(getActivity(),
                                                         Lugares.listado());
      ListView listView = (ListView) getView().findViewById(R.id.listView);
      listView.setAdapter(adaptador);
      listView.setOnItemClickListener(this);
   }

   @Override
   public void onItemClick(AdapterView<?> arg0, View vista, int posicion, long id) {
      ((MainActivity) getActivity()).muestraLugar(id);
   }
}
