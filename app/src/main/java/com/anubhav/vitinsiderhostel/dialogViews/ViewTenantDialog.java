package com.anubhav.vitinsiderhostel.dialogViews;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.models.Tenant;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.Locale;
import java.util.Objects;

public class ViewTenantDialog extends DialogFragment implements View.OnClickListener {

    private Tenant tenant;

    public ViewTenantDialog() {

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
        ImageView copyMailId = view.findViewById(R.id.dialogTenantMailCopyIcon);
        ImageView contactIcon = view.findViewById(R.id.dialogTenantContactIcon);

        MaterialTextView userNameTxt = view.findViewById(R.id.dialogTenantName);
        MaterialTextView mailIdTxt = view.findViewById(R.id.dialogTenantMail);
        MaterialTextView contactNumTxt = view.findViewById(R.id.dialogTenantContactNum);
        MaterialTextView nativeStateTxt = view.findViewById(R.id.dialogTenantNativeState);
        MaterialTextView branchTxt = view.findViewById(R.id.dialogTenantBranch);
        MaterialTextView messTxt = view.findViewById(R.id.dialogTenantMess);

        backButton.setOnClickListener(this);
        copyMailId.setOnClickListener(this);
        contactIcon.setOnClickListener(this);

        tenant = extractTenantFromBundle();
        if (tenant.getTenantMailID() == null) {
            Objects.requireNonNull(getDialog()).dismiss();
        }

        String name = transformText(tenant.getTenantName().split(" "));
        userNameTxt.setText(name);
        mailIdTxt.setText(tenant.getTenantMailID());
        String contactNumber = tenant.getTenantContactNumber();
        String contact = contactNumber==null ? "N/A" : contactNumber;
        contactNumTxt.setText(contact);
        String nativeState = tenant.getTenantNativeState();
        String nativeS = contactNumber==null ? "N/A" : nativeState;
        nativeStateTxt.setText(nativeS);
        branchTxt.setText(tenant.getTenantBranch());
        messTxt.setText(tenant.getTenantMess());

        this.setCancelable(false);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dialogTenantBackIcon) {
            Objects.requireNonNull(getDialog()).dismiss();
        } else if (id == R.id.dialogTenantMailCopyIcon) {
            copyMailId();
        } else if (id == R.id.dialogTenantContactIcon) {
            callTenant();
        }

    }

    private void copyMailId() {
        getContext();
        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Tenant Mail Id", tenant.getTenantMailID());
        clipboard.setPrimaryClip(clip);
    }

    private void callTenant() {
        if (tenant.getTenantContactNumber() != null) {
            if (tenant.getTenantContactNumber() != null && !Objects.requireNonNull(tenant.getTenantContactNumber()).equalsIgnoreCase("N/A") && !tenant.getTenantContactNumber().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:()" + tenant.getTenantContactNumber()));
                requireActivity().startActivity(intent);
            }
        } else {
            Objects.requireNonNull(getDialog()).dismiss();
        }
    }

    private String transformText(String[] words) {
        StringBuilder name = new StringBuilder();
        for (String w : words) {
            String n = firstCaps(w) + " ";
            name.append(n);
        }
        return name.toString();
    }

    private String firstCaps(String name) {
        name = name.toLowerCase(Locale.ROOT);
        char[] arr = name.toCharArray();
        arr[0] = String.valueOf(arr[0]).toUpperCase(Locale.ROOT).charAt(0);
        return String.valueOf(arr);
    }

    private Tenant extractTenantFromBundle() {
        Bundle args = getArguments();
        if (args != null) {
            String tenantName = args.getString("tenantStudentName");
            String tenantMailId = args.getString("tenantMailId");
            String tenantContactNumber = args.getString("tenantContactNumber");
            String tenantNativeState = args.getString("tenantNativeState");
            String tenantBranch = args.getString("tenantBranch");
            String tenantMess = args.getString("tenantMess");
            return new Tenant(tenantMailId, tenantName, tenantContactNumber, tenantNativeState, tenantBranch, 100, tenantMess);
        }
        return new Tenant();
    }

    @Override
    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
    }
}
