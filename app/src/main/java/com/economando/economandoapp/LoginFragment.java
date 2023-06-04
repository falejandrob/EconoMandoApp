package com.economando.economandoapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.economando.economandoapp.Interface.UserService;
import com.economando.economandoapp.entities.RetrofitClient;
import com.economando.economandoapp.entities.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginFragment extends Fragment {

    public static final String BASE_URL = "http://telamarinera.zapto.org:16010/api/";
    private EditText et_email, et_Password;
    private Button btn_Confirm_Login;
    private UserService userService;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        userService = retrofit.create(UserService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        et_email = view.findViewById(R.id.et_Username);
        et_Password = view.findViewById(R.id.et_Password);
        btn_Confirm_Login = view.findViewById(R.id.btn_Confirm_Login);

        btn_Confirm_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString().trim();
                String password = et_Password.getText().toString().trim();
                loginUser(email, password);
            }
        });

        Button btnBack = view.findViewById(R.id.btn_Back_Login);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        return view;
    }

    private void loginUser(String email, String password) {
        Call<User> call = userService.login(email, password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    // Manejar la respuesta del servidor, puedes navegar a otra pantalla aquí
                    Toast.makeText(getActivity(), "Bienvenido ", Toast.LENGTH_SHORT).show();
                    String homeUrl = user.getHome_url();
                    String token = user.getToken();
                    String cookie = response.headers().get("Set-Cookie");
                    savePreferences(token);
                    navigateToWebFragment(homeUrl, cookie);
                } else {
                    Toast.makeText(getActivity(), "Error de inicio de sesión, por favor inténtelo de nuevo.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getActivity(), "Algo salió mal. Por favor inténtelo de nuevo.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePreferences(String token) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("mySharedPreferences", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String[] parts = token.split("\\|");
        Log.w("PARTS_TOKEN", parts.toString());
        editor.putString("token", parts[1]);
        editor.apply();
    }

    private void navigateToWebFragment(String homeUrl, String cookie) {
        WebFragment webFragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString("homeUrl", homeUrl);
        args.putString("cookie", cookie);
        webFragment.setArguments(args);
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView,webFragment)
                .addToBackStack(null)
                .commit();
    }

    private void goBack() {
        FragmentManager fragmentManager = getParentFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }
}

