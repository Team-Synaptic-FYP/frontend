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
    private ArrayList<String> symptoms_list;
    private ArrayList<String> resource_link_list;

    public DiagnosisAdapter(List<DiagnosisModel> diagnosisList, DiagnosisActivity diagnosisActivity) {
        this.diagnosisList = diagnosisList;
        overlayView = diagnosisActivity.findViewById(R.id.pop_up_overlay);
        this.diagnosisActivity = diagnosisActivity;

        this.about_list = new ArrayList<>();
        this.symptoms_list = new ArrayList<>();
        this.resource_link_list = new ArrayList<>();

        // Source : https://my.clevelandclinic.org/health/articles/4022-upper-respiratory-infection

        // Asthma
        this.about_list.add("Asthma, also called bronchial asthma, is a disease that affects your lungs. It’s a chronic (ongoing) condition, meaning it doesn’t go away and needs ongoing medical management.\n\n" +
                "Asthma affects more than 25 million people in the U.S. currently. This total includes more than 5 million children. Asthma can be life-threatening if you don’t get treatment.");

        this.symptoms_list.add("- Chest tightness, pain or pressure.\n" +
                "- Coughing (especially at night).\n" +
                "- Shortness of breath.\n" +
                "- Wheezing.");

        this.resource_link_list.add("https://my.clevelandclinic.org/health/diseases/6424-asthma");


        // Bronchiectasis
        this.about_list.add("Bronchiectasis (\"bronk-ee-EK-tuh-sis\") is a lung condition where your airways (tubes going into your lungs) get damaged and widen. Damaged airways can’t clear mucus like they're supposed to. Bacteria then grows in the mucus, causing more inflammation and damage to your lungs. This makes you cough a lot as your body tries to remove the infected mucus.");

        this.symptoms_list.add("- Cough with lots of mucus and pus.\n" +
                "- Repeated colds.\n" +
                "- Bad-smelling mucus.\n" +
                "- Shortness of breath (dyspnea).\n" +
                "- Wheezing.\n" +
                "- Coughing up blood (hemoptysis).\n" +
                "- Swollen fingertips with curved nails (nail clubbing).");

        this.resource_link_list.add("https://my.clevelandclinic.org/health/diseases/21144-bronchiectasis");

        // Bronchiolitis
        this.about_list.add("Bronchiolitis is a viral infection that affects children younger than 2 years old. It causes the airways (bronchioles) in your child’s lungs to narrow, which makes breathing difficult. If your child develops this infection, you may notice they have symptoms similar to a common cold (runny nose, cough) in addition to noisy breathing (wheezing).\n\n" +
                "The infection is seasonal. It’s more likely to occur during the winter and early spring.\n\n" +
                "Bronchiolitis isn’t usually serious, but it can be. If your child develops this infection, monitor their breathing and contact a healthcare provider if they have trouble breathing.");

        this.symptoms_list.add("- A runny nose.\n" +
                "- A slight fever (under 101 degrees Fahrenheit or 38 degrees Celsius).\n" +
                "- A cough.\n" +
                "- Fatigue.\n" +
                "- Fussiness or irritability (infants).");

        this.resource_link_list.add("https://my.clevelandclinic.org/health/diseases/8272-bronchiolitis");


        // Bronchitis
        this.about_list.add("Bronchitis is an inflammation of the airways leading into your lungs.\n\n" +
                "When your airways (trachea and bronchi) get irritated, they swell up and fill with mucus, causing you to cough. Your cough can last days to a couple of weeks. It’s the main symptom of bronchitis.\n\n" +
                "Viruses are the most common cause of acute bronchitis. Smoke and other irritants can cause acute and chronic bronchitis.");

        this.symptoms_list.add("- Shortness of breath (dyspnea).\n" +
                "- Fever.\n" +
                "- Runny nose.\n" +
                "- Tiredness (fatigue).");

        this.resource_link_list.add("https://my.clevelandclinic.org/health/diseases/3993-bronchitis");

        // COPD
        this.about_list.add("COPD is an umbrella term for a range of progressive lung diseases. Chronic bronchitis and emphysema can both result in COPD. A COPD diagnosis means you may have one of these lung-damaging diseases or symptoms of both. COPD can progress gradually, making it harder to breathe over time.");

        this.symptoms_list.add("- Cough with mucus that persists for long periods of time.\n" +
                "- Difficulty taking a deep breath.\n" +
                "- Shortness of breath with mild exercise (like walking or using the stairs).\n" +
                "- Shortness of breath performing regular daily activities.\n" +
                "- Wheezing.");

        this.resource_link_list.add("https://my.clevelandclinic.org/health/diseases/8709-chronic-obstructive-pulmonary-disease-copd");

        // Lung Fibrosis
        this.about_list.add("Pulmonary fibrosis is a group of serious lung diseases that affect the respiratory system. Pulmonary fibrosis scars and thickens lung tissue. It impacts the connecting tissue in the lung and the alveoli (air sacs inside the lungs).\n\n" +
                "The lung damage gradually gets worse over time. Hard, stiff lung tissues don’t expand as well as they should, making it harder to breathe. Pulmonary fibrosis may cause shortness of breath when you do routine tasks that never seemed tiring before.");
        this.symptoms_list.add("- Breathing in short, shallow spurts.\n" +
                "- Dry cough that doesn’t go away.\n" +
                "- Fatigue (extreme tiredness, no matter how much you sleep).\n" +
                "- Shortness of breath, especially during or soon after you exercise.\n" +
                "- Weight loss that’s not on purpose or easily explained.");
        this.resource_link_list.add("https://my.clevelandclinic.org/health/diseases/10959-pulmonary-fibrosis");

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

        this.symptoms_list.add("- Chest pain. Coughing or deep breathing makes it worse.\n" +
                "- Dyspnea (shortness of breath, or difficult, labored breathing).\n" +
                "- Orthopnea (the inability to breathe easily unless you’re sitting up straight or standing up straight).\n\n" +
                "Some people with pleural effusion have no symptoms. They find out they have pleural effusion when they have a chest X-ray for another reason.");
        this.resource_link_list.add("https://my.clevelandclinic.org/health/diseases/17373-pleural-effusion");

        // Pneumonia
        this.about_list.add("Pneumonia is an infection in your lungs caused by bacteria, viruses or fungi. Pneumonia causes your lung tissue to swell (inflammation) and can cause fluid or pus in your lungs. Bacterial pneumonia is usually more severe than viral pneumonia, which often resolves on its own.\n\n" +
                "Pneumonia can affect one or both lungs. Pneumonia in both of your lungs is called bilateral or double pneumonia.");

        this.symptoms_list.add("- High fever (up to 105°F or 40.55°C).\n" +
                "- Cough with yellow, green or bloody mucus.\n" +
                "- Tiredness (fatigue).\n" +
                "- Rapid breathing.\n" +
                "- Shortness of breath.\n" +
                "- Rapid heart rate.\n" +
                "- Sweating or chills.\n" +
                "- Chest pain and/or abdominal pain, especially with coughing or deep breathing.\n" +
                "- Loss of appetite.\n" +
                "- Bluish skin, lips or nails (cyanosis).\n" +
                "- Confusion or altered mental state.");

        this.resource_link_list.add("https://my.clevelandclinic.org/health/diseases/4471-pneumonia");

        // URTI
        this.about_list.add("\tA Upper Respiratory Tract Infection (URTI) affects the respiratory system, " +
                "the part of your body responsible for breathing. These infections can affect " +
                "your sinuses, throat, lungs or airways. There are two types of respiratory " +
                "infections\n");
        this.symptoms_list.add("- Cough.\n" +
                "- Fever.\n" +
                "- Hoarse voice.\n" +
                "- Fatigue and lack of energy.\n" +
                "- Red eyes.\n" +
                "- Runny nose.\n" +
                "- Sore throat.\n" +
                "- Swollen lymph nodes (swelling on the sides of your neck).");
        this.resource_link_list.add("https://my.clevelandclinic.org/health/articles/4022-upper-respiratory-infection");



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
        TextView symptoms = dialog.findViewById(R.id.txt_symptoms);
        TextView resourceLink = dialog.findViewById(R.id.txt_res);

        title.setText("What is " + disease + "?");

        int detail_number = -1;

        switch (disease.toLowerCase()){
            case "asthma":
                detail_number = 0;
                break;
            case "bronchiectasis":
                detail_number = 1;
                break;
            case "bronchiolitis":
                detail_number = 2;
                break;
            case "bronchitis":
                detail_number = 3;
                break;
            case "copd":
                detail_number = 4;
                break;
            case "lung fibrosis":
                detail_number = 5;
                break;
            case "pleural effusion":
                detail_number = 6;
                break;
            case "pneumonia":
                detail_number = 7;
                break;
            case "urti":
                detail_number = 8;
                break;

        }

        about.setText(about_list.get(detail_number));
        symptoms.setText(symptoms_list.get(detail_number));
        resourceLink.setText("\nMore details : " + resource_link_list.get(detail_number));


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
