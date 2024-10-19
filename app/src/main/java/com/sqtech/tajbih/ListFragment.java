package com.sqtech.tajbih;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment implements ItemAdapter.OnItemClickListener, TasbeehFragment.OnCountValueChangeListener {

    private static final String SHARED_PREF_KEY = "item_list_key";
    private static final String ITEM_LIST_KEY = "item_list";

    private List<Item> itemList = new ArrayList<>();
    private ItemAdapter itemAdapter;

    public ListFragment() {
        // Required empty public constructor
    }


    public ItemAdapter getItemAdapter() {
        return itemAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        itemAdapter = new ItemAdapter(itemList);
        itemAdapter.setOnItemClickListener(this);

        recyclerView.setAdapter(itemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button createTasbeehButton = view.findViewById(R.id.createButton);
        createTasbeehButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddItemDialog();
            }
        });

        loadItemListFromSharedPreferences();

        return view;
    }

    private void showAddItemDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_item, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextItemName = dialogView.findViewById(R.id.editTextItemName);
        final EditText editTextTargetValue = dialogView.findViewById(R.id.editTextTargetValue);

        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String itemName = editTextItemName.getText().toString().trim();
                String targetValueStr = editTextTargetValue.getText().toString().trim();

                // Validate the input fields
                if (itemName.isEmpty()) {
                    Toast.makeText(requireContext(), "Item name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                int targetValue = 0;
                if (!targetValueStr.isEmpty()) {
                    try {
                        targetValue = Integer.parseInt(targetValueStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(requireContext(), "Invalid target value", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (targetValue <= 0) {
                        Toast.makeText(requireContext(), "Target value must be greater than 0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // Add the item and do not dismiss the dialog
                addNewItem(itemName, targetValue);
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = dialogBuilder.setCancelable(false).create();
        alertDialog.show();
    }






    private void addNewItem(String itemName, int targetValue) {
        Item newItem = new Item(itemName, 0, targetValue);
        itemList.add(newItem);
        itemAdapter.notifyDataSetChanged();
        saveItemListToSharedPreferences();
    }

    private void saveItemListToSharedPreferences() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(itemList);
        editor.putString(ITEM_LIST_KEY, json);
        editor.apply();
    }

    private void loadItemListFromSharedPreferences() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(ITEM_LIST_KEY, "");
        Type type = new TypeToken<List<Item>>() {
        }.getType();
        itemList = gson.fromJson(json, type);

        if (itemList == null) {
            itemList = new ArrayList<>();
        }

        itemAdapter.setItems(itemList);
    }

    @Override
    public void onItemClick(int position) {
        Item clickedItem = itemList.get(position);

        // Create a new instance of TasbeehFragment
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("lastOpenedItemPosition", position);
        editor.apply();

        // Pass data to the fragment using Bundle
        TasbeehFragment tasbeehFragment = new TasbeehFragment();
        Bundle args = new Bundle();
        args.putString(TasbeehFragment.ITEM_NAME_KEY, clickedItem.getItemName());
        args.putInt(TasbeehFragment.CURRENT_VALUE_KEY, clickedItem.getCurrentValue());
        args.putInt("position", position);

        tasbeehFragment.setArguments(args);

        tasbeehFragment.setOnCountValueChangeListener(this);

        // Open the TasbeehFragment
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, tasbeehFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDeleteItemClick(int position) {
        // Handle delete icon click
        removeItemAndUpdateSharedPreferences(position); // Remove the item from the adapter's dataset and save the updated list to SharedPreferences
    }

    private void removeItemAndUpdateSharedPreferences(int position) {
        if (position >= 0 && position < itemList.size()) {
            itemList.remove(position);
            itemAdapter.notifyDataSetChanged();
            saveItemListToSharedPreferences();
        }
    }

    @Override
    public void onCountValueChanged(int position, int newValue) {
        // Update the count value for the corresponding item
        Item item = itemList.get(position);
        item.setCurrentValue(newValue);
        itemAdapter.notifyItemChanged(position);

        // Save the updated list to SharedPreferences
        saveItemListToSharedPreferences();
    }
}