package com.sqtech.tajbih;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;


public class TasbeehFragment extends Fragment {

    TextView tasbeehName,tasbeehCountView;
    ImageView countButton,resetButton,modeButton;
    private int itemCount = 0;

    public static final String ITEM_NAME_KEY = "item_name";
    public static final String CURRENT_VALUE_KEY = "current_value";
    private static final String MODE_PREF_KEY = "mode_preference_key";
    private OnCountValueChangeListener onCountValueChangeListener;
    private boolean isNightMode = false;

    public TasbeehFragment() {
        // Required empty public constructor
    }

    public void setOnCountValueChangeListener(OnCountValueChangeListener listener) {
        this.onCountValueChangeListener = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return  inflater.inflate(R.layout.fragment_tasbeeh, container, false);

    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tasbeehName = view.findViewById(R.id.selectedItem);
        tasbeehCountView = view.findViewById(R.id.countView);
        countButton = view.findViewById(R.id.countButton);
        resetButton = view.findViewById(R.id.resetButton);

        modeButton = view.findViewById(R.id.mode);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String itemName = sharedPreferences.getString(ITEM_NAME_KEY, "");
        int currentValue = sharedPreferences.getInt(CURRENT_VALUE_KEY, 0);

        isNightMode = sharedPreferences.getBoolean(MODE_PREF_KEY, false);
        updateDayNightMode();


        Bundle args = getArguments();
        if (args != null) {
            itemName = args.getString(ITEM_NAME_KEY);
            currentValue = args.getInt(CURRENT_VALUE_KEY);

            tasbeehName.setText(itemName);

            tasbeehCountView.setText(String.valueOf(currentValue));
        }




        ImageView countButton = view.findViewById(R.id.countButton);
        countButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(90);
                }
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.02f, 1.0f, 1.02f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(90); // Set the animation duration in milliseconds
                countButton.startAnimation(scaleAnimation);


                int currentValue = Integer.parseInt(tasbeehCountView.getText().toString());
                int newValue = currentValue + 1;
                tasbeehCountView.setText(String.valueOf(newValue));

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(CURRENT_VALUE_KEY, newValue);
                editor.apply();

                // Update the count value in the Item object
                if (args != null) {
                    int position = args.getInt("position", -1);
                    if (position != -1) {
                        ListFragment listFragment = (ListFragment) getParentFragment();
                        if (listFragment != null) {
                            ItemAdapter itemAdapter = listFragment.getItemAdapter();
                            itemAdapter.updateItemCount(position, newValue);
                        }
                    }
                }

                // Notify the ListFragment about the count value change
                if (onCountValueChangeListener != null) {
                    onCountValueChangeListener.onCountValueChanged(getArguments().getInt("position", -1), newValue);
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage("Are you sure you want to reset the count?");
                builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tasbeehCountView.setText("0");
                        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(CURRENT_VALUE_KEY, 0);
                        editor.apply();

                        // Update the count value in the Item object
                        if (args != null) {
                            int position = args.getInt("position", -1);
                            if (position != -1) {
                                ListFragment listFragment = (ListFragment) getParentFragment();
                                if (listFragment != null) {
                                    ItemAdapter itemAdapter = listFragment.getItemAdapter();
                                    itemAdapter.updateItemCount(position, 0);
                                }
                            }
                        }

                        // Notify the ListFragment about the count value change
                        if (onCountValueChangeListener != null) {
                            onCountValueChangeListener.onCountValueChanged(getArguments().getInt("position", -1), 0);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog and do nothing
                        dialog.dismiss();
                    }
                });

                // Show the confirmation dialog
                builder.create().show();
            }
        });



        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNightMode = !isNightMode;
                updateDayNightMode();

                // Save the mode preference to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(MODE_PREF_KEY, isNightMode);
                editor.apply();
            }
        });
    }
    private void updateDayNightMode() {
        if (isNightMode) {
            // Apply night mode changes to the fragment UI
            getView().setBackgroundColor(getResources().getColor(R.color.black));
            modeButton.setImageResource(R.drawable.ic_night);
        } else {
            // Apply day mode changes to the fragment UI
            getView().setBackgroundColor(getResources().getColor(R.color.white));
            modeButton.setImageResource(R.drawable.ic_day);
        }
    }


    public interface OnCountValueChangeListener {
        void onCountValueChanged(int position, int newValue);
    }

}