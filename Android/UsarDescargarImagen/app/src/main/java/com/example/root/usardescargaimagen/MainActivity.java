package com.example.root.usardescargaimagen;

/**
 * Created by root on 27/04/15.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MainActivity extends Activity {
    Button load_img,load_base,save_img,send_img,decode_img;
    ImageView img;
    Bitmap bitmap;
    ProgressDialog pDialog;

    String image = "naruhina";
    EditText etImagen;
    private ConectorSoap conexionsoap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        load_img = (Button)findViewById(R.id.load);
        load_base = (Button)findViewById(R.id.loadbase);
        save_img = (Button)findViewById(R.id.save);
        send_img=(Button)findViewById(R.id.send);
        decode_img = (Button)findViewById(R.id.decode);
        img = (ImageView)findViewById(R.id.img);
        etImagen=(EditText) findViewById(R.id.etImagen);


        load_img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                new LoadImage().execute("https://raw.githubusercontent.com/renekaigen/UsarDescargaImagen/master/naruhina.jpg");
            }
        });

        load_base.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                new LoadImagenBase().execute();
            }
        });

        decode_img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String nombre_imagen = etImagen.getText().toString();
                if (nombre_imagen == null || nombre_imagen.equals("")) {
                    nombre_imagen=image;
                }
                Log.d("imagen", "" + nombre_imagen);
                String imagen_codificada=encondeBase64(nombre_imagen + ".jpg");
                decodeBase64(imagen_codificada);
            }
        });



        save_img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String nombre_imagen = etImagen.getText().toString();
                if (nombre_imagen == null || nombre_imagen.equals("")) {
                    nombre_imagen=image;
                }
                Log.d("imagen", "" + nombre_imagen);
                guardar_imagen(nombre_imagen + ".jpg");
            }
        });

        send_img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String nombre_imagen = etImagen.getText().toString();
                if (nombre_imagen == null || nombre_imagen.equals("")) {
                    nombre_imagen=image;
                }
                Log.d("imagen", "" + nombre_imagen);
                String imagen_codificada=encondeBase64(nombre_imagen + ".jpg");
                Log.d("imagen", " codificada " + imagen_codificada);
                new UploadImage().execute(imagen_codificada);
            }
         });


    }

    public void guardar_imagen(String nombre_imagen) {
        if (bitmap != null) {
            File sdCard = Environment.getExternalStorageDirectory();
            File path = new File(sdCard.getAbsolutePath() + "/myImages");
            if (!path.exists())
                path.mkdirs();
            File file = new File(path,nombre_imagen);
            FileOutputStream fOut;
            try {
                fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                fOut.flush();
                fOut.close();
                Toast.makeText(MainActivity.this, "Imagen guardada", Toast.LENGTH_LONG)
                        .show();
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        } else {
            Toast.makeText(MainActivity.this, "No se ha bajado la imagen",
                    Toast.LENGTH_LONG).show();
        }

    }
    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading Image ....");
            pDialog.show();

        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                img.setImageBitmap(image);
                pDialog.dismiss();

            }else{

                pDialog.dismiss();
                Toast.makeText(MainActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }


    private class UploadImage extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Uploading Image ....");
            pDialog.show();

        }
        protected String doInBackground(String... args) {
            Log.d("ASYNC", "imagen es "+args[0]);
            conexionsoap=new ConectorSoap();
            String respuestaSoap = conexionsoap.conectar(args[0], MainActivity.this,1);
            return respuestaSoap;
        }

        protected void onPostExecute(String respuestaSoap) {
            pDialog.dismiss();
            Toast.makeText(MainActivity.this, "" + respuestaSoap, Toast.LENGTH_SHORT).show();
        }
    }

    private class  LoadImagenBase extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Uploading Image ....");
            pDialog.show();

        }
        protected String doInBackground(String... args) {

            conexionsoap=new ConectorSoap();
            String respuestaSoap = conexionsoap.conectar("", MainActivity.this,2);
            return respuestaSoap;
        }

        protected void onPostExecute(String respuestaSoap) {
            pDialog.dismiss();
            decodeBase64(respuestaSoap);
           // Toast.makeText(MainActivity.this, "" + respuestaSoap, Toast.LENGTH_SHORT).show();
        }
    }


    public String encondeBase64(String nombreImagen){
        File sdCard = Environment.getExternalStorageDirectory();
        Bitmap myBitmap = BitmapFactory.decodeFile(sdCard.getAbsolutePath() + "/myImages/"+nombreImagen);//imgFile.getAbsolutePath());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);//Base64.DEFAULT);
        return encoded;
    }

    public void decodeBase64(String encodeImage){
        byte[] imageAsBytes = Base64.decode(encodeImage.getBytes(), Base64.DEFAULT);
        ImageView image = (ImageView)this.findViewById(R.id.img2);
        image.setImageBitmap(
                BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length)
        );
    }
}