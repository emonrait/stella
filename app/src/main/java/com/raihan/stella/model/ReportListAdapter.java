package com.raihan.stella.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.raihan.stella.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;


public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.MyViewHolder>
        implements Filterable {
    public static Object ContactsAdapterListener;
    private Context context;
    private ArrayList<Report> contactList = null;
    private ArrayList<Report> contactListFiltered;
    private OnItemClickListener listener;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_deposit_amount, tv_due_balance, tv_due_months, tv_due_balance_label, tv_due_months_label;
        Button btn_payment_details;

        public MyViewHolder(View view) {
            super(view);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_deposit_amount = (TextView) itemView.findViewById(R.id.tv_deposit_amount);
            tv_due_balance = (TextView) itemView.findViewById(R.id.tv_due_balance);
            tv_due_months = (TextView) itemView.findViewById(R.id.tv_due_months);
            tv_due_balance_label = (TextView) itemView.findViewById(R.id.tv_due_balance_label);
            tv_due_months_label = (TextView) itemView.findViewById(R.id.tv_due_months_label);
            btn_payment_details = (Button) itemView.findViewById(R.id.btn_payment_details);

          /*  view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(contactListFiltered.get(getAdapterPosition()));
                }
            });*/
        }
    }


    public ReportListAdapter(Context context, ArrayList<Report> contactList, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.contactList = contactList;
        this.contactListFiltered = contactList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_report_listviewall, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Report data = contactList.get(position);


        holder.tv_name.setText(data.getProductName());
        holder.tv_due_months.setText(data.getSellPrice());
        holder.tv_due_balance.setText(data.getBuyPrice());


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
                    ArrayList<Report> filteredList = new ArrayList<>();
                    for (Report row : contactListFiltered) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getProductName().toLowerCase().contains(charString.toLowerCase())
                        ) {
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
                contactList = (ArrayList<Report>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnItemClickListener {
        void onContactSelected(Report contact);
    }

}