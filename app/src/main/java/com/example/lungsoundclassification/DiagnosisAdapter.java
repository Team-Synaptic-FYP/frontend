package com.example.lungsoundclassification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DiagnosisAdapter extends RecyclerView.Adapter<DiagnosisAdapter.ViewHolder>{

    private final List<DiagnosisModel> diagnosisList;

    public DiagnosisAdapter(List<DiagnosisModel> diagnosisList) {
        this.diagnosisList = diagnosisList;
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
            showDescriptionDialog(v.getContext(), diagnosis.getDisease(), diagnosis.getConfidentLevel());
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

    private void showDescriptionDialog(Context context, String title, String description) {
        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the title and message (description)
        builder.setTitle(title);
        builder.setMessage(description);

        // Add a button to close the dialog
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
