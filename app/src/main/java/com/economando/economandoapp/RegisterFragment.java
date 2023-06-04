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
import com.economando.economandoapp.entities.RegisterResponse;
import com.economando.economandoapp.entities.RetrofitClient;
import com.economando.economandoapp.entities.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class RegisterFragment extends Fragment {

    public static final String BASE_URL = "http://telamarinera.zapto.org:16010/api/";
    private Button btnBack;
    private Button btnConfirmRegister;
    private EditText etName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etPass;
    private EditText etConfirmPass;

    private UserService userService;

    public RegisterFragment() {
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        initFragment(view);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        btnConfirmRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        return view;
    }

    private void initFragment(View view) {
        btnBack = view.findViewById(R.id.btn_Back_Register);
        btnConfirmRegister = view.findViewById(R.id.btn_Confirm_Register);
        etName = view.findViewById(R.id.et_Name);
        etLastName = view.findViewById(R.id.et_LastName);
        etEmail = view.findViewById(R.id.et_Email);
        etPass = view.findViewById(R.id.et_Password);
        etConfirmPass = view.findViewById(R.id.et_ConfirmPassword);
    }

    private void goBack() {
        FragmentManager fragmentManager = getParentFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPass.getText().toString().trim();
        String confirmPassword = etConfirmPass.getText().toString().trim();

        Call<RegisterResponse> call = userService.register(name, lastName, email, password, confirmPassword);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Registration successful.", Toast.LENGTH_SHORT).show();

                    RegisterResponse registerResponse = response.body();
                    String token = registerResponse.getToken();
                    String homeUrl = registerResponse.getHome_url();
                    String cookie = response.headers().get("Set-Cookie");

                    //save token
                    savePreferences(token);
                    //navigate to webFragment
                    navigateToWebFragment(homeUrl,cookie);


                } else {
                    if (response.code() == 422) {
                        // Convierte los errores de validación a un objeto de tipo Map
                        Map<String, List<String>> errors;

                        try {
                            errors = convertErrors(response.errorBody().string());
                        }catch (IOException e){
                            throw new RuntimeException("Error parsing error body", e);
                        }

                        // Muestra los errores de validación
                        for (List<String> errorList : errors.values()) {
                            for (String error : errorList) {
                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
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
    private Map<String, List<String>> convertErrors(String jsonErrors) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, List<String>>>() {}.getType();
        return gson.fromJson(jsonErrors, type);
    }


    private void navigateToWebFragment(String homeUrl, String cookie) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        WebFragment webFragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString("homeUrl", homeUrl);
        args.putString("cookie", cookie);
        webFragment.setArguments(args);
        transaction.replace(R.id.wv_EconoMando, webFragment);
        transaction.commit();
    }
}
