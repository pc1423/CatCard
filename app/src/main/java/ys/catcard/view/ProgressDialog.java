package ys.catcard.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import ys.catcard.R;

public class ProgressDialog extends DialogFragment {

    public static String DIALOG_ID = "snail_progress_dialog";

    private TextView textView;

    private static ProgressDialog newInstance() {
        return new ProgressDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);

        View rootView = inflater.inflate(R.layout.activity_snail, container, false);
        textView = (TextView) rootView.findViewById(R.id.progress_text);

        return rootView;
    }

    public static ProgressDialog create(Activity activity) {
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        Fragment prev = activity.getFragmentManager().findFragmentByTag(ProgressDialog.DIALOG_ID);

        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);
        return ProgressDialog.newInstance();
    }

    public void show(Activity activity) {
        if (!isAdded()) {
            FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
            show(ft, DIALOG_ID);
        }
    }

    public TextView getTextView() {
        return textView;
    }
}
