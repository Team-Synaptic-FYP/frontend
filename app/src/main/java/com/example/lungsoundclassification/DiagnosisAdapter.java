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

import org.w3c.dom.Text;

import java.util.ArrayList;
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

    private ArrayList<String> about_list;

    public DiagnosisAdapter(List<DiagnosisModel> diagnosisList, DiagnosisActivity diagnosisActivity) {
        this.diagnosisList = diagnosisList;
        overlayView = diagnosisActivity.findViewById(R.id.pop_up_overlay);
        this.diagnosisActivity = diagnosisActivity;

        this.about_list = new ArrayList<>();

        // Source : https://my.clevelandclinic.org/health/articles/4022-upper-respiratory-infection
        // Pleural Effusion
        this.about_list.add("\tPleural effusion, which some people call “water on the lungs,” " +
                "is the buildup of excess fluid between the layers of the pleura outside your lungs. " +
                "The pleura are thin membranes that line your lungs and the inside of " +
                "your chest cavity." +
                "\n\n" +
                "\tNormally, everyone has a small amount of fluid in their pleura. This fluid acts " +
                "as a natural lubricant and makes it easier for your lungs to move when " +
                "you breathe. But with pleural effusion, you have too much fluid around your lungs. " +
                "This means your body is producing too much of the " +
                "fluid or not absorbing enough of the fluid it makes.\n");

        // URTI
        this.about_list.add("\tA Upper Respiratory Tract Infection (URTI) affects the respiratory system, " +
                "the part of your body responsible for breathing. These infections can affect " +
                "your sinuses, throat, lungs or airways. There are two types of respiratory " +
                "infections\n");
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
            showDialog(v.getContext(), diagnosis.getDisease());
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

    private void showDialog(Context context, String disease){
        Dialog dialog = new Dialog(context, R.style.DialogStyle);


        dialog.setContentView(R.layout.dialog_custom_layout);

        TextView title = dialog.findViewById(R.id.txt_title);
        TextView about = dialog.findViewById(R.id.txt_about);

        title.setText(disease);

        switch (disease){
            case "Pleural Effusion":
                about.setText(about_list.get(0));
                break;
            case "URTI":
                about.setText(about_list.get(1));
                break;
        }




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
