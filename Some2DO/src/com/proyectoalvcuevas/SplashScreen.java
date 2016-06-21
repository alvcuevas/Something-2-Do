package com.proyectoalvcuevas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class SplashScreen extends Activity {
	Context x = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);

		new splashDelay().execute(0); //Se inicia la pantalla de bienvenida
	}

	//Tarea asincrona para la pantalla de bienvenida
	class splashDelay extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... arg0) {

			synchronized (this) {
				try {
					wait(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		//Método que ejecute la aplicacion real DESPUES del Splash
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			Intent i = new Intent(x, MainActivity.class);
			startActivity(i);
			((Activity) x).finish();
		}
	}

}
