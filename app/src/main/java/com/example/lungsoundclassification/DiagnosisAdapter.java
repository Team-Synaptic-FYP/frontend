package com.example.lungsoundclassification;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.gesture.GestureOverlayView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import io.alterac.blurkit.BlurKit;
import io.alterac.blurkit.BlurLayout;

public class DiagnosisAdapter extends RecyclerView.Adapter<DiagnosisAdapter.ViewHolder>{

    private final List<DiagnosisModel> diagnosisList;
    private GestureOverlayView overlayView;
    private BlurLayout blurView;
    private DiagnosisActivity diagnosisActivity;

    public DiagnosisAdapter(List<DiagnosisModel> diagnosisList, DiagnosisActivity diagnosisActivity) {
        this.diagnosisList = diagnosisList;
        overlayView = diagnosisActivity.findViewById(R.id.pop_up_overlay);
        this.diagnosisActivity = diagnosisActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.diagnosis_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiagnosisModel diagnosis = diagnosisList.get(position);

        // Bind data to views
        holder.disease.setText(diagnosis.getDisease());
        holder.confidentLevel.setText(diagnosis.getConfidentLevel());

        holder.itemView.setOnClickListener(v -> {
            // When the item is clicked, show the descriptive pop-up
            // Call the method to show the AlertDialog
//            showDescriptionDialog(v.getContext(), diagnosis.getDisease(), diagnosis.getConfidentLevel());
            showDialog(v.getContext());
        });

        // Add more bindings as needed
    }

    @Override
    public int getItemCount() {
        return diagnosisList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView disease;
        TextView confidentLevel;

        public ViewHolder(View itemView) {
            super(itemView);
            disease = itemView.findViewById(R.id.disease);
            confidentLevel = itemView.findViewById(R.id.confidentLevel);
            // Initialize other views as needed
        }
    }

    private void showDialog(Context context){
        Dialog dialog = new Dialog(context, R.style.DialogStyle);
        dialog.setContentView(R.layout.dialog_custom_layout);


        // Configure the blur effect
        blurView = diagnosisActivity.findViewById(R.id.blurLayout);
        blurView.recomputeViewAttributes(diagnosisActivity.getWindow().getDecorView());
        blurView.invalidate();

        blurView.setVisibility(View.VISIBLE);
        overlayView.setVisibility(View.VISIBLE);
//        BlurKit.getInstance().blur(diagnosisActivity.getWindow().getDecorView(), 3);


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.bg_window);

        Button btnClose = dialog.findViewById(R.id.btn_ok);


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // Add code to handle when the dialog is dismissed
                // This could be triggered by clicking outside the dialog, back button, or close button

                // For example, you can perform additional actions such as changing visibility
                overlayView.setVisibility(View.GONE);
                blurView.setVisibility(View.GONE);
            }
            });

        dialog.show();
    }
}
