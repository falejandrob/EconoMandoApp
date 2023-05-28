package com.economando.economandoapp.entities;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;

public class CustomWebViewClient extends WebViewClient implements DownloadListener {

    private Context context;

    public CustomWebViewClient(Context context) {
        this.context = context;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // Cargar todas las URL dentro del WebView
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, android.net.http.SslError error) {
        // Ignorar errores SSL y continuar cargando la página
        handler.proceed();
    }


    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
        // Obtener el nombre del archivo desde contentDisposition
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);

        // Directorio de destino para la descarga
        String downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

        // Ruta completa del archivo a descargar
        String filePath = downloadDirectory + "/" + fileName;

        // Crear la solicitud de descarga
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setTitle(fileName)
                .setDescription("Descargando archivo...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationUri(Uri.fromFile(new File(filePath)));

        // Obtener el servicio de descargas
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Activity.DOWNLOAD_SERVICE);

        // Iniciar la descarga
        if (downloadManager != null) {
            downloadManager.enqueue(request);
            Toast.makeText(context, "Descarga iniciada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "No se pudo iniciar la descarga", Toast.LENGTH_SHORT).show();
        }
    }
}
