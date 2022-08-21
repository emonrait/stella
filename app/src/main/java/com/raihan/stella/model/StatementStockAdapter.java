package com.raihan.stella.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.raihan.stella.R;

import java.util.ArrayList;


public class StatementStockAdapter extends RecyclerView.Adapter<StatementStockAdapter.MyViewHolder>
        implements Filterable {
    public static Object ContactsAdapterListener;
    private Context context;
    private ArrayList<Stock> contactList = null;
    private ArrayList<Stock> contactListFiltered;
    private OnItemClickListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_product_id;
        TextView tv_product_name;
        TextView tv_color;
        TextView tv_date;
        TextView tv_product_mrp;
        TextView tv_product_percentage;
        TextView tv_product_quantity;
        TextView tv_add_by;
        Button btn_payment_details;

        public MyViewHolder(View view) {
            super(view);
            tv_product_id = (TextView) itemView.findViewById(R.id.tv_product_id);
            tv_product_name = (TextView) itemView.findViewById(R.id.tv_product_name);
            tv_color = (TextView) itemView.findViewById(R.id.tv_color);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_product_mrp = (TextView) itemView.findViewById(R.id.tv_product_mrp);
            tv_product_percentage = (TextView) itemView.findViewById(R.id.tv_product_percentage);
            tv_product_quantity = (TextView) itemView.findViewById(R.id.tv_product_quantity);
            tv_add_by = (TextView) itemView.findViewById(R.id.tv_add_by);
            btn_payment_details = (Button) itemView.findViewById(R.id.btn_payment_details);

        }
    }


    public StatementStockAdapter(Context context, ArrayList<Stock> contactList, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.contactList = contactList;
        this.contactListFiltered = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_statement_product, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Stock data = contactList.get(position);

        holder.tv_product_id.setText(data.getProductId());
        holder.tv_product_name.setText(data.getProductName());
        holder.tv_color.setText(data.getColor());
        holder.tv_date.setText(data.getDate());
        holder.tv_product_mrp.setText(ValidationUtil.commaSeparateAmount(data.getProductMrp()));
        holder.tv_product_percentage.setText(data.getProductPercent() + "%");
        holder.tv_product_quantity.setText(data.getProductQty());
        holder.tv_add_by.setText(data.getUpdateBy());
        holder.btn_payment_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onContactSelected(contactList.get(position));
            }
        });


    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactList = contactListFiltered;
                } else {
                    ArrayList<Stock> filteredList = new ArrayList<>();
                    for (Stock row : contactListFiltered) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getId().toLowerCase().contains(charString.toLowerCase())
                                || row.getDate().contains(charSequence)) {
                            filteredList.add(row);

                            // || row.getAccountTitle().contains(charSequence)
                        }
                    }

                    contactList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactList = (ArrayList<Stock>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnItemClickListener {
        void onContactSelected(Stock contact);
    }
}