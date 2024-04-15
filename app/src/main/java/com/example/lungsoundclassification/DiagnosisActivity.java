package com.example.lungsoundclassification;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.webkit.WebSettings.RenderPriority.HIGH;
import static android.webkit.WebSettings.RenderPriority.LOW;

import static java.text.DateFormat.MEDIUM;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class DiagnosisActivity extends AppCompatActivity {

    private List<DiagnosisModel> diagnosisList;
    private List<DiagnosisModel> viewableDiagnosisList;
    private DiagnosisAdapter adapter;
    private RadarChart radarChart;
    private ResponseObject responseObject;

    // Audio Player related Variables
    private AudioPlayer audioPlayer;
    private Button playPauseButton;
    private SeekBar seekBar;
    private boolean isPlaying = false;
    private Handler handler;
    private Runnable updateSeekBarRunnable;


    private CardView healthyCard;
    private CardView diagnosisCard;
    private CardView radarChartCard;
    private CardView analysisCard;
    private TextView healthyDisclaimer;
    private View emptySpaceView;

    private static final int PERMISSION_REQUEST_CODE = 200;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagnosis_view);

        // getting extra
        responseObject = (ResponseObject) getIntent().getSerializableExtra("response_object");
        byte[] wavData = (byte[]) getIntent().getSerializableExtra("wav_data");

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(wavData);


        // Initialize common UI components
        healthyCard = findViewById(R.id.healthy_card);
        diagnosisCard = findViewById(R.id.diagnosis_card);
        radarChartCard = findViewById(R.id.radar_chart_card);
        analysisCard = findViewById(R.id.analysis_card);

        healthyDisclaimer = findViewById(R.id.healthy_description);

        assert responseObject != null;
        if (responseObject.getDiseases().size() == 0){ // True here

            String updatedPercentage = "85.12";  // Replace with your updated percentage value
            String updatedText = getString(R.string.disclaimer_health_1, updatedPercentage);
            healthyDisclaimer.setText(updatedText);

            healthyCard.setVisibility(View.VISIBLE);
            diagnosisCard.setVisibility(View.GONE);
            radarChartCard.setVisibility(View.GONE);
            analysisCard.setVisibility(View.GONE);

        }
        else {
            healthyCard.setVisibility(View.GONE);
            diagnosisCard.setVisibility(View.VISIBLE);
            radarChartCard.setVisibility(View.VISIBLE);
            analysisCard.setVisibility(View.VISIBLE);

            // Radar chart configuration -------------------------------------

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



            // Recycler View configuration -----------------------------------

            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            DefaultItemAnimator animator = new DefaultItemAnimator();
            animator.setAddDuration(200);
            animator.setRemoveDuration(200);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setItemAnimator(animator);

            List<DiagnosisModel> diagnosisList = getDiagnosisData(responseObject.getDiseases(), responseObject.getProbabilities()); // Replace with your data source
            adapter = new DiagnosisAdapter(diagnosisList, this);
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


            // XAI Media player configuration -----------------------------------

            // Initialize UI components for audio player
            playPauseButton = findViewById(R.id.playPauseButton);
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
                        handler.postDelayed(this, 10); // Update every 10 ms
                    }
                }
            };

            // Play/Pause button functionality
            playPauseButton.setOnClickListener(view -> {
                if (isPlaying) {
                    // If audio is playing, pause it
                    audioPlayer.pause();
                    isPlaying = false;

                    // Update the button to "Play"
                    playPauseButton.setBackgroundResource(R.drawable.play_btn);

                    // Stop updating the seek bar
                    handler.removeCallbacks(updateSeekBarRunnable);
                } else {
                    // If audio is paused, play it
                    audioPlayer.play();
                    isPlaying = true;

                    // Update the button text to "Pause"
                    playPauseButton.setBackgroundResource(R.drawable.pause_btn);

                    // Start updating the seek bar
                    handler.post(updateSeekBarRunnable);
                }
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
                    isPlaying = false;
                    playPauseButton.setBackgroundResource(R.drawable.play_btn);
                    handler.removeCallbacks(updateSeekBarRunnable);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // Resume audio after seeking
                    audioPlayer.play();
                    isPlaying = true;
                    playPauseButton.setBackgroundResource(R.drawable.pause_btn);
                    handler.post(updateSeekBarRunnable);
                }

            });

            audioPlayer.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    // When audio is completed, set the button text to "Play"
                    isPlaying = false;
                    playPauseButton.setBackgroundResource(R.drawable.play_btn);

                    // Stop updating the seek bar
                    handler.removeCallbacks(updateSeekBarRunnable);
                }
            } );

            // Configuring download pdf button

            Button downloadPDF = findViewById(R.id.download_pdf);

            // Set an OnClickListener for the button
            downloadPDF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (checkPermission()) {
                        Toast.makeText(DiagnosisActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        requestPermission();
                    }

                    createPDF(responseObject.getDiseases(), responseObject.getProbabilities()); // Call your PDF generation method

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

    private void createPDF(List<String> diagnosisNames, List<Float> diagnosisProbs){

        PdfDocument pdfDocument = new PdfDocument();

        // define page size in pixels (A4)
        int desiredDPI = 300; // Higher DPI for higher quality images
        int width = (int) (8.27 * desiredDPI); // A4 page width in inches (8.27) * desiredDPI
        int height = (int) (11.69 * desiredDPI);
        float padding = 100f;

        // Assigning the content padding
        float cont_padding_left = padding + 100;
        float cont_padding_right = width - padding - 100;
        float cont_padding_top = padding + 100;
        float cont_padding_bottom = padding - height - 100;

        // Create page info and start a page
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Paint Objects for different types of text and drawing
        Paint paint = new Paint();
        Paint titlePaint = new Paint();
        Paint borderPaint = new Paint();
        Paint watermarkPaint = new Paint();
        Paint underlinePaint = new Paint();

        // Border ---------------------------------------------

        // Configure border Paint
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(ContextCompat.getColor(this, R.color.black));
        borderPaint.setStrokeWidth(2);

        // Draw the border around the page
        float left = padding;
        float top = padding;
        float right = width - padding;
        float bottom = height - padding;

        canvas.drawRect(left, top, right, bottom, borderPaint);

        // Watermark ----------------------------------------------------------

        // Load watermark image
        Bitmap watermarkBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pdf_watermark);
        // Scale the watermark image to fit the page

        // Draw watermark image behind all other content
        canvas.drawBitmap(watermarkBitmap, 0, 0, watermarkPaint);

        // Header --------------------------------------------------------------

        // Configure title paint
        titlePaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        titlePaint.setTextSize(150);
        titlePaint.setColor(ContextCompat.getColor(this, R.color.black));

        // Draw the application title at the top left corner
        float titleX = cont_padding_left;
        float titleY = cont_padding_top + 200;
        canvas.drawText("PulmoSync", titleX, titleY, titlePaint);

        // Body -------------------------------------------------------------------
        // Add underlined text
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(60);
        paint.setColor(ContextCompat.getColor(this, R.color.black));
        paint.setUnderlineText(true); // Enable underline

        // Define the text to be underlined
        String underlinedText = "Disease Classification";

        // Define the x and y coordinates for the text
        float textX = cont_padding_left;
        float textY = cont_padding_top + 600;

        // Draw the underlined text
        canvas.drawText(underlinedText, textX, textY, paint);

        // Diseases - tabulated representation
        float tableStartX = cont_padding_left;
        float tableStartY = cont_padding_top + 750;
        float column1Width = 500;
        float column2Width = 600;
        float rowHeight = 120;

        // Define the headers for the table
        String[] headers = {"Disease", "Probability"};

        // Paint for the header row (bold text)
        Paint headerPaint = new Paint();
        headerPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        headerPaint.setTextSize(60);
        headerPaint.setColor(ContextCompat.getColor(this, R.color.black));

        // Draw the header row
        canvas.drawText(headers[0], tableStartX, tableStartY, headerPaint); // Left-aligned
        canvas.drawText(headers[1], tableStartX + column1Width + column2Width - headerPaint.measureText(headers[1]), tableStartY, headerPaint); // Right-aligned

        // Define the data rows
        String[][] data = {
                {diagnosisNames.get(0), String.format("%.2f%%", diagnosisProbs.get(0) * 100)},
                {diagnosisNames.get(1), String.format("%.2f%%", diagnosisProbs.get(1) * 100)},
                {diagnosisNames.get(2), String.format("%.2f%%", diagnosisProbs.get(2) * 100)},

        };

        // Paint for the data rows
        Paint dataPaint = new Paint();
        dataPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        dataPaint.setTextSize(60);
        dataPaint.setColor(ContextCompat.getColor(this, R.color.black));

        // Iterate over the data and draw each row
        for (int i = 0; i < data.length; i++) {
            // Calculate the Y position for the current row
            float rowY = tableStartY + (i + 1) * rowHeight;

            // Draw the data in the left column (left-aligned)
            canvas.drawText(data[i][0], tableStartX, rowY, dataPaint);

            // Calculate the X position for the right column and draw the data (right-aligned)
            float rightColumnX = tableStartX + column1Width + column2Width - dataPaint.measureText(data[i][1]);
            canvas.drawText(data[i][1], rightColumnX, rowY, dataPaint);
        }

        // Applying the radar chart -------------------------------------------------
        radarChart.setDrawingCacheEnabled(true);
        radarChart.buildDrawingCache();
        Bitmap radarChartBitmap = Bitmap.createBitmap(radarChart.getDrawingCache());

        radarChart.setDrawingCacheEnabled(false);

        // defining positions
        float chartX = cont_padding_left + 1000;
        float chartY = cont_padding_top + 500;

        canvas.drawBitmap(radarChartBitmap, chartX, chartY, null);

        // Comment and disclaimer -----------------------------------------------------------

        // Configure text paint
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(ContextCompat.getColor(this, R.color.black));
        textPaint.setTextSize(50); // Adjust text size as needed

        // Set justified alignment using Layout.Alignment
        Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
        textPaint.setTextAlign(Paint.Align.LEFT);

        // Paragraph text
        String paragraphText = "COMMENT : \nThis categorization is only diagnosing 10 lung diseases including Asthma, Bronchiectasis, Bronchiolitis, Bronchitis, COPD, Lung Fibrosis, Pleural Effusion, Pneumonia, URTI any other disease containing lung audio will give incorrect results for the application. Therefore users are advised to seek professional medical care before taking any medication based on the results of this mobile application.\n\n" +
                "This sound categorization is for indication purposes only and should not be taken as a replacement or alternative for professional medical advice.";

        SpannableStringBuilder spannableText = new SpannableStringBuilder(paragraphText);

        int boldStartIndex = paragraphText.indexOf("This sound categorization is for indication purposes only");
        int boldEndIndex = paragraphText.indexOf("This sound categorization is for indication purposes only") + "This sound categorization is for indication purposes only and should not be taken as a replacement or alternative for professional medical advice.".length();


        // Apply bold style using a StyleSpan
        spannableText.setSpan(new StyleSpan(Typeface.BOLD), boldStartIndex, boldEndIndex, 0);


        // Create a StaticLayout to handle the text layout
        StaticLayout staticLayout = new StaticLayout(
                spannableText,
                textPaint,
                (int) (cont_padding_right - cont_padding_left), // Width of the paragraph area (page width minus padding)
                alignment,
                1.5f, // Line spacing multiplier
                0.0f, // Line spacing extra
                true // Justify the paragraph
        );

        // Draw the paragraph within the specified area
        canvas.save();
        canvas.translate(cont_padding_left, cont_padding_top + 1600); // Adjust the starting position of the text as needed
        staticLayout.draw(canvas);
        canvas.restore();

        // Tested date time -------------------------------------------------------------
        Paint testTime = new Paint();
        testTime.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)); // Normal text style
        testTime.setTextSize(40); // Adjust text size as needed
        testTime.setColor(ContextCompat.getColor(this, R.color.black)); // Set text color

        // Define the contact details
        String testTimeText = "Diagnosed time : " + getCurrentDateTimeShort();


        // Define the starting X and Y coordinates for the contact details
        float textTimeX = cont_padding_left; // Align with left padding
        float textTimeY = height - padding - 300; // Position just above the bottom padding

        canvas.drawText(testTimeText, textTimeX, textTimeY, testTime);

        // Contact details ---------------------------------------------------------------

        Paint contactPaint = new Paint();
        contactPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)); // Normal text style
        contactPaint.setTextSize(40); // Adjust text size as needed
        contactPaint.setColor(ContextCompat.getColor(this, R.color.black)); // Set text color

        // Define the contact details
        String contactDetails = "Contact Us: \nEmail: lungsoundclassification@gmail.com \nPhone: +94 76 781 6691";

        String[] lines = contactDetails.split("\n");

        // Define the starting X and Y coordinates for the contact details
        float contactX = cont_padding_left; // Align with left padding
        float contactY = height - padding - 200; // Position just above the bottom padding

        // Draw each line of contact details separately
        for (String line : lines) {
            // Draw the current line of text on the canvas
            canvas.drawText(line, contactX, contactY, contactPaint);

            // Increment the Y-coordinate to move to the next line
            contactY += contactPaint.getTextSize() * 1.5; // Adjust the line height if needed
        }



        pdfDocument.finishPage(page);

        // TODO : Change this file path it is just for the emulator
        File file = new File(getExternalFilesDir(null), "pulmosync_diagnosis_report_" + getCurrentDateTimeShort() + ".pdf");

        try {
            // after creating a file name we will
            // write our PDF file to that location.
            pdfDocument.writeTo(new FileOutputStream(file));

            // below line is to print toast message
            // on completion of PDF generation.
            Toast.makeText(DiagnosisActivity.this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // below line is used
            // to handle error
            e.printStackTrace();
        }
        // after storing our pdf to that
        // location we are closing our PDF file.
        pdfDocument.close();



    }

    public String getCurrentDateTimeShort() {
        // Get the current date and time
        LocalDateTime currentDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDateTime = LocalDateTime.now();
            // Define the short date-time format pattern
            // You can adjust the format pattern according to your preference
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            // Format the current date and time using the defined pattern

            // Return the formatted date and time
            return currentDateTime.format(formatter);
        }



        return "";

    }

    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release resources when the activity is destroyed
        if(handler != null){
            handler.removeCallbacks(updateSeekBarRunnable);
        }

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
