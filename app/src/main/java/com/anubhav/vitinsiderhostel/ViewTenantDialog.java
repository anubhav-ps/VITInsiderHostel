package com.anubhav.vitinsiderhostel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;

import java.util.Objects;

public class ViewTenantDialog extends DialogFragment implements View.OnClickListener {

    ViewTenantDialog() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_tenant_view, container, false);
        MaterialButton backButton = view.findViewById(R.id.dialogTenantBackIcon);
        backButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id==R.id.dialogTenantBackIcon){
            Objects.requireNonNull(getDialog()).dismiss();
        }
    }
}
