package com.santiagoaranguri.extraviodepersonas;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings.Secure;
import android.widget.Button;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    Context contextNotification;
    private double latitude;
    private double longitude;
    AppLocationService appLocationService;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context.getApplicationContext(), "4", Toast.LENGTH_SHORT).show();

        appLocationService = new AppLocationService(context);

        Location nwLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);

        if (nwLocation != null) {
            latitude = nwLocation.getLatitude();
            longitude = nwLocation.getLongitude();
            /*
            Toast.makeText(
                    context.getApplicationContext(),
                    "Mobile Location (NW): \nLatitude: " + latitude
                            + "\nLongitude: " + longitude,
                    Toast.LENGTH_LONG).show();
             */
        } else {
            //showSettingsAlert("NETWORK");
        }

        contextNotification = context;
        String android_id = Secure.getString(contextNotification.getContentResolver(), Secure.ANDROID_ID);
        String latitudeText = String.valueOf(latitude);
        String longitudeText = String.valueOf(longitude);
        new RequestTask(context, new RequestTask.OnReadyCallback() {
            @Override
            public void onReady(String response) {
                if (response != null) {
                    String[] parts = response.split("__");
                    if (parts[0].equals("1")) {
                        String name = parts[1];
                        String address = parts[2];
                        String gender = parts[3];
                        String type = parts[4];
                        String lo = "";
                        String encontrarlo = "";
                        if(gender.equals("undefined")){
                            lo = "lo";
                            encontrarlo = "encontrarlo";
                        }else if (gender.equals("male")){
                            lo = "lo";
                            encontrarlo = "encontrarlo";
                        }else if (gender.equals("female")){
                            lo = "la";
                            encontrarlo = "encontrarla";
                        }

                        if(!address.equals("null")){
                            address = " se " + lo + " vio por última vez en: " + address;
                        }else{
                            address = "";
                        }
                        String textNotificationBig = "";
                        String textNotificationSmall = "";
                        String titleNotification = "";

                        if(type.equals("0")){
                            titleNotification = "Persona perdida cercana";
                            textNotificationBig = "Estamos buscando a " + name + address + ". Necesitamos tu ayuda para " + encontrarlo + ".";
                            textNotificationSmall = "Estamos buscando a " + name + ".";
                        }else if(type.equals("1")) {
                            titleNotification = "Persona encontrada";
                            textNotificationBig = "Gracias a la ayuda de todos nosotros, se encontró a " + name + ".";
                            textNotificationSmall = "Se encontró a " + name + ".";
                        }else if(type.equals("2")) {
                            titleNotification = "Búsqueda de familiares";
                            textNotificationBig = "Estamos buscando a familiares de " + name + ". Necesitamos tu ayuda para encontrarlos.";
                            textNotificationSmall = "Buscamos a familiares de " + name + ".";
                        }

                        if(name.equals("null")){
                            titleNotification = "Personas Perdidas";
                            textNotificationBig = "Nuevos casos de personas perdidas. Entrá a la aplicación de Personas Perdidas para verlos.";
                            textNotificationSmall = "Nuevos casos de personas perdidas";
                        }
                        // Notificación: Configuración de las notificaciones
                        // Prepare intent which is triggered if the notification is selected
                        Intent intent = new Intent(contextNotification, NotificationReceiverActivity.class);
                        PendingIntent pIntent = PendingIntent.getActivity(contextNotification, (int) System.currentTimeMillis(), intent, 0);
                        // Build notification
                        Notification noti = new Notification.Builder(contextNotification)
                                .setContentTitle(titleNotification)
                                .setContentText(textNotificationSmall).setSmallIcon(R.drawable.icon)
                                .setContentIntent(pIntent)
                                .setStyle(new Notification.BigTextStyle().bigText(textNotificationBig)).build();
                        NotificationManager notificationManager = (NotificationManager) contextNotification.getSystemService(contextNotification.NOTIFICATION_SERVICE);
                        // Hide the notification after its selected
                        noti.flags |= Notification.FLAG_AUTO_CANCEL;
                        notificationManager.notify(0, noti);
                    }
                }
            }
        }).execute(Util.SERVER_URL+"getNotification.php?ID="+android_id + "&latitude=" + latitudeText + "&longitude=" + longitudeText);
    }
}