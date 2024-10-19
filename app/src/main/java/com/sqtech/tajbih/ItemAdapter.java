package com.sqtech.tajbih;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> itemList;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setItems(List<Item> items) { this.itemList = items; notifyDataSetChanged(); }


    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.itemNameTextView.setText(item.getItemName());
        holder.currentValueTextView.setText("" + item.getCurrentValue());
        holder.targetValueTextView.setText("" + item.getTargetValue());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void addItem(Item item) {
        itemList.add(item);
        notifyDataSetChanged();
    }


    public void updateItemCount(int position, int newValue) {
        if (position >= 0 && position < itemList.size()) {
            Item item = itemList.get(position);
            item.setCurrentValue(newValue);
            notifyItemChanged(position);
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemNameTextView;
        TextView currentValueTextView;
        TextView targetValueTextView;
        ImageView itemDelete;
        LinearLayout itemLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.textZikirName);
            currentValueTextView = itemView.findViewById(R.id.textCurrent);
            targetValueTextView = itemView.findViewById(R.id.textTarget);
            itemDelete = itemView.findViewById(R.id.imgDelete);

            itemLayout = itemView.findViewById(R.id.itemLayout);
            itemLayout.setOnClickListener(this);

            itemDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (itemClickListener != null) {
                            itemClickListener.onDeleteItemClick(position);
                        }
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(position);
                }
            }
        }
    }
}
