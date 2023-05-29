package com.economando.economandoapp;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.economando.economandoapp.entities.CustomWebViewClient;

public class AppFragment extends Fragment {

    public static final String URL = "http://192.168.33.20:8000/";
    private WebView wvEconomando;

    public AppFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        initSettings();

        configureWebView();
    }

    private void configureWebView() {
        wvEconomando.setDownloadListener(new DownloadListener() {

            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Log.d("CONTENTDISPOSITION", contentDisposition);
                String[] dispositionParts = contentDisposition.split(";");
                String fileName = "";

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));


                CookieManager cookieManager = CookieManager.getInstance();
                String cookie = cookieManager.getCookie(URL);
                request.addRequestHeader("Cookie", cookie);

                for (String part : dispositionParts) {
                    if (part.trim().startsWith("filename")) {
                        String[] fileNameParts = part.split("=");
                        if (fileNameParts.length > 1) {
                            fileName = fileNameParts[1].trim().replaceAll("\"", "");
                            fileName = fileName.replace(".pdf", "");
                            fileName = fileName.replace(":", "");
                            fileName = fileName.replace("-", "");
                            break;
                        }
                    }
                }

                Log.d("Nombre de archivo ", fileName);
                request.allowScanningByMediaScanner();
                Environment.getExternalStorageDirectory();
                getContext().getFilesDir().getPath(); //which returns the internal app files directory path

                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                DownloadManager dm = (DownloadManager) getContext().getSystemService(DOWNLOAD_SERVICE);


                // Iniciar la descarga
                if (dm != null) {
                    dm.enqueue(request);
                    Toast.makeText(getContext(), "Descarga iniciada del archivo " +"'"+ fileName+"'", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "No se pudo iniciar la descarga", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initSettings() {
        wvEconomando = getView().findViewById(R.id.wv_EconoMando);
        wvEconomando.setWebViewClient(new CustomWebViewClient(getContext()));
        wvEconomando.loadUrl("http://192.168.33.20:8000/");
        WebSettings webSettings = wvEconomando.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }
}