//package com.example.lungsoundclassification;
//
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.Button;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.fragment.app.DialogFragment;
//
//import eightbitlab.com.blurview.BlurView;
//import eightbitlab.com.blurview.RenderScriptBlur;
//
//public class CustomDialogFragment extends DialogFragment {
//
//    private BlurView blurView;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//    }
//
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//        // Inflate your custom layout
//        View customView = View.inflate(getActivity(), R.layout.dialog_custom_layout, null);
//        builder.setView(customView);
//
//        // Create and return the dialog
//        AlertDialog dialog = builder.create();
//
//        // Get the window and set the background to be transparent
//        Window window = dialog.getWindow();
//        if (window != null) {
//            window.setBackgroundDrawableResource(android.R.color.transparent);
//        }
//
//        // Set up the BlurView
//        blurView = customView.findViewById(R.id.);
//        blurView.setupWith((ViewGroup) getActivity().findViewById(android.R.id.content))
//                .setFrameClearDrawable(getActivity().getWindow().getDecorView().getBackground())
//                .setBlurAlgorithm(new RenderScriptBlur(getActivity()))
//                .setBlurRadius(15f)
//                .setHasFixedTransformationMatrix(true);
//
//        // Set a positive button to close the dialog
//        Button positiveButton = customView.findViewById(R.id.dialog_button);
//        positiveButton.setOnClickListener(v -> {
//            dialog.dismiss();
//        });
//
//        return dialog;
//    }
//
//    @Override
//    public void onDismiss(DialogInterface dialog) {
//        super.onDismiss(dialog);
//        if (blurView != null) {
//            blurView.setVisibility(View.GONE);
//        }
//    }
//}