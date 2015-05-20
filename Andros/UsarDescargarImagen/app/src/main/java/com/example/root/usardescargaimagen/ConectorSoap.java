package com.example.root.usardescargaimagen;




import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.conn.ConnectTimeoutException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.SocketTimeoutException;


/**
 * Created by root on 23/03/15.
 */
public class ConectorSoap {


    private static String NAMESPACE="http://192.168.1.61/decodeImagen/ws/nusoap/";
    private static String URL="http://192.168.1.61/decodeImagen/ws/index.php";
    private static String soap_action="http://192.168.1.61/decodeImagen/ws/index.php/SubirImagen";//agregar soap action conforme peticion
    private static String method_name="SubirImagen";
    private static String method_name1="SubirImagenNoWrap";
    private static String method_name2="getBaseImageWeb";


    public ConectorSoap() {

    }

    public  boolean hasInternet(Context a) {
        boolean hasConnectedWifi = false;
        boolean hasConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) a
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("wifi"))
                if (ni.isConnected())
                    hasConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("mobile"))
                if (ni.isConnected())
                    hasConnectedMobile = true;
        }
        return hasConnectedWifi || hasConnectedMobile;
    }

    /**
     funcion Conectar
     Esta función regresa una cadena de respuesta conforme la conexion solicitada
     @param   imagenBase64 es la cadena de envio
     @parama contexto
     **/

    public String conectar(String imagenBase64, Context contexto,int tipo)
    {

        String metodo="";
        if(tipo==2){
            metodo=this.method_name2;
        }
        else
        {
            metodo=this.method_name1;
        }

        String resultado="0";
        SoapObject request = new SoapObject(NAMESPACE, metodo);
        request.addProperty("cadena", imagenBase64);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = false;//falso porque es NO es .net
        if(hasInternet(contexto))  //si hay solicitud y hay internet haz el soap
        {
            Log.d("SOAP", "antes de peticion");
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,90000); ///TIEMPO DE 90 SEGUNDOs para la recarga
                androidHttpTransport.call(this.soap_action, envelope);
                SoapObject result = (SoapObject)envelope.bodyIn;// Obtener el  SoapResult del envelope body.
                Log.d("SOAP", "tengo el resultado");
                if(result != null)
                {
                    resultado=result.getProperty(0).toString();//Get the first property and change the label text
                }///si esta Nulo
                else
                {
                    resultado="Error de respuesta";
                }
            }
            catch (SocketTimeoutException e) {  ///wait for data

                Log.e("Error",""+e.toString());
                //no comprobado aun al parecer siempre llega a la ultima exception
            }
            catch (ConnectTimeoutException e) {
                Log.e("Error",""+e.toString());
            }
            catch (Exception e) {
                Log.e("Error",""+e.toString());
            }
        }
        else
        {
            //si no hay internet o solcitud es false
            resultado="Error : Tiempo de espera agotado. Intente nuevamente";
            Log.e("Error",""+resultado);
        }
        return resultado;
    }
}
