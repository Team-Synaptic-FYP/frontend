package com.example.lungsoundclassification;

import static android.webkit.WebSettings.RenderPriority.HIGH;
import static android.webkit.WebSettings.RenderPriority.LOW;

import static java.text.DateFormat.MEDIUM;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class DiagnosisActivity extends AppCompatActivity {

    private List<DiagnosisModel> diagnosisList;
    private List<DiagnosisModel> viewableDiagnosisList;
    private DiagnosisAdapter adapter;
    private RadarChart radarChart;

    // Audio Player related Variables
    private AudioPlayer audioPlayer;
    private Button playButton;
    private Button pauseButton;
    private SeekBar seekBar;
    private boolean isPlaying = false;
    private Handler handler;
    private Runnable updateSeekBarRunnable;


    private CardView healthyCard;
    private CardView diagnosisCard;
    private CardView radarChartCard;
    private TextView healthyDisclaimer;
    private View emptySpaceView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagnosis_view);

        // getting extra
        ResponseObject responseObject = (ResponseObject) getIntent().getSerializableExtra("response_object");
        byte[] wavData = (byte[]) getIntent().getSerializableExtra("wav_data");

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(wavData);


        // Initialize common UI components
        healthyCard = findViewById(R.id.healthy_card);
        diagnosisCard = findViewById(R.id.diagnosis_card);
        radarChartCard = findViewById(R.id.radar_chart_card);

        healthyDisclaimer = findViewById(R.id.healthy_description);

        // Initialize UI components for audio player
        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        seekBar = findViewById(R.id.seekBar);

        // Initialize the audio player
        audioPlayer = new AudioPlayer(this, byteArrayInputStream);
        seekBar.setMax(audioPlayer.getDuration());

        // Initialize a Handler
        handler = new Handler();

        // Define the runnable to update the seek bar
        updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                // Update the seek bar with the current audio position
                if (isPlaying) {
                    int currentPosition = audioPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);

                    // Schedule the runnable to run again after a delay
                    handler.postDelayed(this, 10); // Update every 500 ms
                }
            }
        };

        playButton.setOnClickListener(view -> {
            audioPlayer.play();
            isPlaying = true;

            // Start updating the seek bar
            handler.post(updateSeekBarRunnable);
        });

        pauseButton.setOnClickListener(view -> {
            audioPlayer.pause();
            isPlaying = false;

            // Stop updating the seek bar
            handler.removeCallbacks(updateSeekBarRunnable);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Pause audio while seeking
                audioPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Resume playing audio after seeking
                if (isPlaying) {
                    audioPlayer.play();
                }
            }
        });



        assert responseObject != null;
        if (responseObject.getDiseases().size() == 0){ // True here

            String updatedPercentage = "85.12";  // Replace with your updated percentage value
            String updatedText = getString(R.string.disclaimer_health_1, updatedPercentage);
            healthyDisclaimer.setText(updatedText);

            healthyCard.setVisibility(View.VISIBLE);
            diagnosisCard.setVisibility(View.GONE);
            radarChartCard.setVisibility(View.GONE);

        }
        else {
            healthyCard.setVisibility(View.GONE);
            diagnosisCard.setVisibility(View.VISIBLE);
            radarChartCard.setVisibility(View.VISIBLE);

            radarChart = findViewById(R.id.radarChart);

            List<Float> probabilities = responseObject.getProbabilities();



            List<RadarEntry> entries = new ArrayList<>();
            entries.add(new RadarEntry(probabilities.get(0) * 100));
            entries.add(new RadarEntry(probabilities.get(1) * 100));
            entries.add(new RadarEntry(probabilities.get(2) * 100));

            RadarDataSet dataSet = new RadarDataSet(entries, "Label");
            dataSet.setColor(Color.RED);
            dataSet.setFillColor(Color.RED);
            dataSet.setDrawFilled(true);

            radarChart.getLegend().setEnabled(false); // Remove the description (legend)

            radarChart.setExtraOffsets(0, 0, 0, 0);

            radarChart.setRotationEnabled(false);

            RadarData data = new RadarData(dataSet);
            radarChart.setData(data);
            radarChart.getDescription().setEnabled(false);

            YAxis yAxis = radarChart.getYAxis();
            yAxis.setAxisMaximum(90f); // Set maximum value to 100
            yAxis.setGranularity(10f); // Set granularity to 10

            dataSet.setDrawValues(false);

            // Customize the X axis labels
            XAxis xAxis = radarChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(responseObject.getDiseases())); // Set custom labels

            radarChart.invalidate(); // Refresh the chart



            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            DefaultItemAnimator animator = new DefaultItemAnimator();
            animator.setAddDuration(200);
            animator.setRemoveDuration(200);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setItemAnimator(animator);

            List<DiagnosisModel> diagnosisList = getDiagnosisData(responseObject.getDiseases(), responseObject.getProbabilities()); // Replace with your data source
            adapter = new DiagnosisAdapter(diagnosisList);
            recyclerView.setAdapter(adapter);

            TextView expand_btn = findViewById(R.id.diagnosis_seemore);

            expand_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle button click
                    if(expand_btn.getText().equals(getString(R.string.show_more))){
                        expandList();
                        expand_btn.setText(getString(R.string.show_less));
                        adapter.notifyDataSetChanged();

                    } else {
                        minimizeList();
                        expand_btn.setText(getString(R.string.show_more));
                        adapter.notifyDataSetChanged();
                    }
                }
            });

        }

    }


    // Replace this method with your actual data source logic
    private List<DiagnosisModel> getDiagnosisData(List<String> diagnosisNames, List<Float> diagnosisProbs) {
        this.diagnosisList = new ArrayList<>();
        this.diagnosisList.add(new DiagnosisModel(diagnosisNames.get(0), String.format("%.2f%%", diagnosisProbs.get(0) * 100)));
        this.diagnosisList.add(new DiagnosisModel(diagnosisNames.get(1), String.format("%.2f%%", diagnosisProbs.get(1) * 100)));
        this.diagnosisList.add(new DiagnosisModel(diagnosisNames.get(2), String.format("%.2f%%", diagnosisProbs.get(2) * 100)));

        viewableDiagnosisList = new ArrayList<>();
        viewableDiagnosisList.add(diagnosisList.get(0));

        return viewableDiagnosisList;
    }



    private void expandList(){
        for(int i = 1; i < diagnosisList.size(); i++){
            viewableDiagnosisList.add(i, diagnosisList.get(i));
            adapter.notifyItemInserted(i);
        }

    }

    private void minimizeList(){
        for(int i = diagnosisList.size() - 1; i >= 1; i--){
            viewableDiagnosisList.remove(i);
            adapter.notifyItemRemoved(i);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release resources when the activity is destroyed
        handler.removeCallbacks(updateSeekBarRunnable);
        if (audioPlayer != null) {
            audioPlayer.release();
        }
    }

}


//
//    private int calculateSeverity(List<Integer> severityValues){
//        int severity_sum = 0;
//        for (int severityValue:severityValues) {
//            severity_sum += severityValue;
//        }
//
//        return severity_sum / severityValues.size();
//    }
//
//    private void setSeverityLevel(int severity) {
//        int progressValue;
//
//        switch (severity) {
//            case 4:
//                progressValue = 100;
//                severityImpression.setImageResource(R.drawable.sad_plus);
//                break;
//            case 3:
//                progressValue = 75;
//                severityImpression.setImageResource(R.drawable.sad);
//                break;
//            case 2:
//                progressValue = 50;
//                severityImpression.setImageResource(R.drawable.neutral);
//                break;
//            case 1:
//                progressValue = 25;
//                severityImpression.setImageResource(R.drawable.happy);
//                break;
//            case 0:
//                progressValue = 0;
//                severityImpression.setImageResource(R.drawable.happy_plus);
//                break;
//            default:
//                progressValue = 0;
//                break;
//        }
//
//        animateProgressBar(progressValue);
//    }

//    private void animateProgressBar(final int targetProgress) {
//
//        final int steps = 25; // You can adjust the animation speed by changing the number of steps
//        final int delay = 20; // Delay between each step in milliseconds
//
//        final Handler handler = new Handler(Looper.getMainLooper());
//        handler.post(new Runnable() {
//
//            int currentProgress = severityProgressBar.getProgress();
//            final int diff = targetProgress - currentProgress;
//            final float step = (float) diff / steps;
//
//            @Override
//            public void run() {
//                if (currentProgress != targetProgress) {
//
//                    int color = calculateColor(currentProgress);
//                    severityProgressBar.setProgressTintList(ColorStateList.valueOf(color));
//
//                    severityProgressBar.setProgress(currentProgress + Math.round(step));
//                    currentProgress = Math.round(currentProgress + step);
//
//
//                    handler.postDelayed(this, delay);
//                }
//            }
//        });
//    }

//    private int calculateColor(int progress) {
////        int green = (int) Math.max(((150 - progress) * 2.55), 0); // 0% -> Green
////        int red = (int) (progress * 2.55); // 100% -> Red
//
//        int green, red;
//
//        if (progress < 50){
//            green = 255;
//            red = (int) (progress * 2.55 * 2);
//        }
//
//        else{
//            red = 255;
//            green = (int)(255 + (100 - progress * 2) * 2.55);
//
//        }
//
//        return Color.rgb(red, green, 0);
//    }
