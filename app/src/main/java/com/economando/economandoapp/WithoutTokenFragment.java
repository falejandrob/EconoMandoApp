package com.economando.economandoapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class WithoutTokenFragment extends Fragment {

    private Button btnLogin;
    private Button btnRegister;

    public WithoutTokenFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_without_token, container, false);
        initializeViews(view);
        setupListeners();
        return view;
    }

    private void initializeViews(View view) {
        btnLogin = view.findViewById(R.id.btn_Login);
        btnRegister = view.findViewById(R.id.btn_Register);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginFragment();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterFragment();
            }
        });
    }

    private void openLoginFragment() {
        Fragment loginFragment = new LoginFragment();
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView,loginFragment)
                .addToBackStack(null)
                .commit();
    }

    private void openRegisterFragment() {
        Fragment registerFragment = new RegisterFragment();
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView,registerFragment)
                .addToBackStack(null)
                .commit();
    }
}
