package com.example.app_selfcare;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.app_selfcare.Data.Model.Notification;
import com.example.app_selfcare.R;
import com.google.android.material.textfield.TextInputEditText;

public class SendNotificationDialog extends DialogFragment {

    public interface OnNotificationSentListener {
        void onNotificationSent(Notification notification);
    }

    private OnNotificationSentListener listener;

    public void setListener(OnNotificationSentListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_send_notification, null);

        TextInputEditText etTitle = view.findViewById(R.id.etNotifTitle);
        TextInputEditText etMessage = view.findViewById(R.id.etNotifMessage);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .create();

        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btnSendNotif).setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String message = etMessage.getText().toString().trim();

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(message)) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            Notification notif = new Notification(
                    String.valueOf(System.currentTimeMillis()),
                    title,
                    message
            );

            if (listener != null) listener.onNotificationSent(notif);
            Toast.makeText(getContext(), "Gửi thông báo thành công!", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        return dialog;
    }
}