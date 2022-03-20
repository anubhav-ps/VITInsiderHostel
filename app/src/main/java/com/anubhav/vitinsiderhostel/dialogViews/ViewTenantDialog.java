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
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

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
        MaterialTextView nativeLanguageTxt = view.findViewById(R.id.dialogTenantNativeLanguage);
        MaterialTextView branchTxt = view.findViewById(R.id.dialogTenantBranch);

        backButton.setOnClickListener(this);
        copyMailId.setOnClickListener(this);
        contactIcon.setOnClickListener(this);

        tenant = extractTenantFromBundle();
        if (tenant.getTenantMailID() == null) {
            Objects.requireNonNull(getDialog()).dismiss();
        }

        userNameTxt.setText(tenant.getTenantUserName());
        mailIdTxt.setText(tenant.getTenantMailID());
        contactNumTxt.setText(tenant.getTenantContactNumber());
        nativeLanguageTxt.setText(tenant.getTenantNativeLanguage());
        branchTxt.setText(tenant.getTenantBranch());

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
            if (tenant.getTenantContactNumber() != null && !Objects.requireNonNull(tenant.getTenantContactNumber()).equalsIgnoreCase("N/A") && !tenant.getTenantMailID().equalsIgnoreCase(User.getInstance().getUserMailID())) {
                Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:()" + tenant.getTenantContactNumber()));
                requireActivity().startActivity(intent);
            }
        } else {
            Objects.requireNonNull(getDialog()).dismiss();
        }
    }

    private Tenant extractTenantFromBundle() {
        Bundle args = getArguments();
        if (args != null) {
            String tenantName = args.getString("tenantName");
            String tenantMailId = args.getString("tenantMailId");
            String tenantContactNumber = args.getString("tenantContactNumber");
            String tenantNativeLanguage = args.getString("tenantNativeLanguage");
            String tenantBranch = args.getString("tenantBranch");
            return new Tenant(tenantName, tenantMailId, tenantContactNumber, tenantNativeLanguage, tenantBranch);
        }
        return new Tenant();
    }

}
