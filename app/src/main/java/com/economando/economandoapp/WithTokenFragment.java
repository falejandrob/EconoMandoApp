package com.economando.economandoapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.economando.economandoapp.Interface.UserService;
import com.economando.economandoapp.entities.RetrofitClient;
import com.economando.economandoapp.entities.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class WithTokenFragment extends Fragment {

    public static final String BASE_URL = "http://telamarinera.zapto.org:16010/api/";
    private UserService userService;

    private Button btnInicio;
    private Button btnSalir;
    private SharedPreferences sharedPreferences;
    private String token;

    public WithTokenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Init Api
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        userService = retrofit.create(UserService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_with_token, container, false);
        initSetting(view);
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            sharedPreferences.edit().clear().apply();
            getActivity().finishAffinity();
            }
        });
        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserWithToken();
            }
        });

        return view;
    }

    private void initSetting(View view) {
        btnInicio = view.findViewById(R.id.btn_Inicio);
        btnSalir = view.findViewById(R.id.btn_Salir);
        sharedPreferences = getActivity().getSharedPreferences("mySharedPreferences", getContext().MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);
    }

    private void loginUserWithToken() {
        Log.d("Token",token);

        if(token == null) {
            Toast.makeText(getActivity(), "No token found. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<User> call = userService.loginWithToken(token);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    Toast.makeText(getActivity(), "Logged in with token successfully.", Toast.LENGTH_SHORT).show();
                    String homeUrl = user.getHome_url();
                    String cookie = response.headers().get("Set-Cookie");
                    navigateToWebFragment(homeUrl, cookie);
                } else {
                    Toast.makeText(getActivity(), "Failed to login with token.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getActivity(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToWebFragment(String homeUrl, String cookie) {
        WebFragment webFragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString("homeUrl", homeUrl);
        args.putString("cookie", cookie);
        webFragment.setArguments(args);
        navigateToFragment(webFragment);
    }

    private void navigateToFragment(Fragment fragment) {
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit();
    }
}
