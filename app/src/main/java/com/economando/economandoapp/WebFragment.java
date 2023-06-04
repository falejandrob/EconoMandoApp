package com.economando.economandoapp;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.economando.economandoapp.entities.CustomWebViewClient;

public class WebFragment extends Fragment {

    public String URL;
    private WebView wvEconomando;

    public WebFragment() {
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

        // Obtener los argumentos
        Bundle args = getArguments();
        if (args != null) {
            String homeUrl = args.getString("homeUrl");
            URL = homeUrl;

            // Configurar el WebView
            configureWebView();

            // Obtener las cookies
            //String economandoSessionCookie = args.getString("cookie");
            String economandoSessionCookie = args.getString("cookie");

            Log.d("economando_session",""+economandoSessionCookie);

            // Establecer las cookies en el WebView
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setCookie(homeUrl, economandoSessionCookie);

            // Sincronizar las cookies con el WebView
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.flush();
            } else {
                CookieSyncManager.createInstance(getActivity());
                CookieSyncManager.getInstance().sync();
            }

            // Cargar la URL en el WebView
            wvEconomando.loadUrl(homeUrl);
        }

        WebSettings webSettings = wvEconomando.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

}