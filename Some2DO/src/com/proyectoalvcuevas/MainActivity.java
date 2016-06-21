package com.proyectoalvcuevas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/*
 * >>>>>>>>>>>>>>> PROYECTO FINAL CURSO ANDROID <<<<<<<<<<<<<<<
 * >>>>>>>>>>>>>>>  AUTOR: ÁLVARO CUEVAS 1-DAM  <<<<<<<<<<<<<<<
 * >>>>>>>>>>>>>>>>>>>   SOMETHING 2 DO   <<<<<<<<<<<<<<<<<<<<<
 * 
 * =============================================================================
 * 
 * La idea de la aplicación se basa en la inserción y guardado de notas
 * escritas por el usuario de modo que las almacene para una futura lectura
 * como recordatorio de tareas pendientes, fechas, cosas que hacer..etc
 * 
 * Con este resultado final de la aplicación, finalmente he implementado
 * todos los objetivos que en un principio tenia en mente, los cuales eran:
 * 
 * - SplashScreen de bienvenida
 * - Insercion de notas en un contenedor principal (ListView)
 * - Borrado de notas dependiendo del item de ese contenedor 
 * - Implementar sonidos para cada acción asícomo textos de aviso
 * - Vista general de la nota seleccionada por el usuario mediante Dialogs
 * - Preferencias que guarden dichas notas y nuevos cambios en el ListView
 * 
 * =============================================================================
 * 
 */

public class MainActivity extends Activity implements OnClickListener, OnItemClickListener, OnItemLongClickListener{

	MediaPlayer mplayer;
	ListView lista;
	Context x;
	List<HashMap<String,String>> datos = new ArrayList<HashMap<String,String>>(); 
	SimpleAdapter adaptadorLista;
	SharedPreferences prefs; 

	/*
	 * Método onCreate
	 * 
	 * Inicia la aplicación mostrando un aviso de orientación al usuario
	 * asícomo incluyendo todas las preferencias almacenadas previamente
	 * en el contenedor principal de todas las notas.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		x = this;
		findViewById(R.id.button1).setOnClickListener(this);
		lista = (ListView)findViewById(R.id.lista);
		definirAdaptadorLista();
		
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		rellenarLista();
		Toast.makeText(this, "Manten pulsado para borrar una nota", Toast.LENGTH_SHORT).show();
	}
	
	
	/*
	 * Método rellenarLista
	 * 
	 * Dicho metodo rellena la lista completa con sus elementos al inicio
	 * de la aplicación. En él, se ha utilizado un JSONArray que permite
	 * pasar a un Array normal las cadenas de texto que contenia el HashMap utilizado
	 * y que en este caso son el contenido de cada nota insertada por el usuario
	 * para poderse mostrar correctamente y del mismo modo almacenarlas en las preferencias.
	 * 
	 */
	private void rellenarLista(){
		String notas =  prefs.getString("elementosLista", "");
		try {
			if(!notas.equals("")){
				JSONArray jsonArray = new JSONArray(notas);
				Log.e("jsonArray",jsonArray.toString());
				for (int i=0; i<jsonArray.length(); i++){
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					HashMap<String,String> temp = new HashMap<String,String>();
					temp.put("cabecera", jsonObject.getString("cabecera"));
					temp.put("texto", jsonObject.getString("texto"));
					Log.e("temp",temp.toString());
					datos.add(temp);
				}
				adaptadorLista.notifyDataSetChanged();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * Método guardarListaDatos
	 * 
	 * Guarda todos los datos de los elementos del ListView que hay en el momento
	 * en las preferencias sabiendo como es el formato exacto de los datos
	 * para guardarlos correctamente.
	 */
	private void guardarListaDatos(){
		String jsonString = "[";
		for(HashMap<String,String> t : datos){
			jsonString += String.format("{cabecera:'%s',texto:'%s'},",t.get("cabecera"),t.get("texto"));
		}
		
		jsonString = jsonString.substring(0,jsonString.length()-1);
		jsonString += "]";
		prefs.edit().putString("elementosLista",jsonString).commit();
		adaptadorLista.notifyDataSetChanged();
	}
	
	/*
	 * Método InsertarElementoNuevo
	 * 
	 * Se basa en un HashMap que almacena mediante dos Strings lo que va a formar
	 * la nota completa compuesta por 'titulo' y 'contenido' que se mostrarán
	 * en el ListView al ser insertada.
	 * 
	 * Cada fila de la lista es un HashMap y que a su vez guarda una clave y
	 * un valor referida a un Textview.
	 */
	private void InsertarElementoNuevo(){
		EditText texto = (EditText)findViewById(R.id.editText1); 
		String textoIntroducido = texto.getText().toString()+"";
		texto.setText("");
		
		HashMap<String,String> temp = new HashMap<String,String>();
		   					   temp.put("cabecera", "Nota "+datos.size());
		   					   temp.put("texto", textoIntroducido);
		datos.add(temp);
		guardarListaDatos();
		
	}
	
	/*
	 * Método definirAdaptadorLista
	 * 
	 * Define el adaptador que incluye los datos que van a ser insertados
	 * en el ListView asícomo el layout de cada fila del elemento y sus escuchas.
	 */
	private void definirAdaptadorLista() {
		String claves[] = new String[]{"cabecera","texto"};
		int ids[]       = new int[]{R.id.cabecera, R.id.texto};
		
		adaptadorLista = new SimpleAdapter(x,datos,R.layout.fila_simple,claves,ids);
		lista.setAdapter(adaptadorLista);
		lista.setOnItemClickListener(this);
		lista.setOnItemLongClickListener(this);
	}
	
	/*
	 * Muestra la animación inicial que corresponde al SplashScreen de bienvenida
	 */
	@Override
	protected void onResume() {
		super.onResume();
		overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
	}
	
	
	/*
	 * Método onItemClick
	 * 
	 * Ejecuta la acción de mostrar un dialog independiente que muestre el texto
	 * completo de la nota tras el previo click realizado por el usuario en dicho
	 * elemento del ListView
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int posicion, long arg3) {
		@SuppressWarnings("unused")
		int posicionNota = datos.size();
		Toast.makeText(this, "Esta es tu nota "+posicion, Toast.LENGTH_SHORT).show();
		
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Vista completa");
		alertDialog.setMessage(datos.get(posicion).get("texto"));
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		   public void onClick(DialogInterface dialog, int which) {
		      dialog.cancel();
		   }
		});
		alertDialog.show();
	}
	
	
	/*
	 * Método onClick
	 * 
	 * Mediante dicho método se ejecuta la acción de aquellos botones de la aplicación
	 * que detecten el click del usuario (previamente asignada la escucha a cada botón)
	 * En este caso el boton 'Agregar nota' lleva a cabo la inserción de un nuevo
	 * elemento al ListView acompañado de un sonido vinculado y Toast.
	 */
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.button1:
			InsertarElementoNuevo();
			mplayer = MediaPlayer.create(this, R.raw.bing);
			mplayer.start();
			Toast.makeText(this, "Añadida", Toast.LENGTH_SHORT).show();
		break;
		}
		
	}

	/*
	 * Método onItemLongClick
	 * 
	 * Se ejecuta el borrado de la nota una vez que se autodetecta el click continuo
	 * del usuario sobre la nota elegida, mostrando igualmente un Toast inferior de 
	 * aviso y su sonido asociado que confirmar dicha acción.
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int posicion, long arg3){
		datos.remove(posicion);
		adaptadorLista.notifyDataSetChanged();
		guardarListaDatos();
		
		Toast.makeText(this, "Nota borrada", Toast.LENGTH_SHORT).show();
		mplayer = MediaPlayer.create(this, R.raw.cut);
		mplayer.start();
		return false;
	}

}
