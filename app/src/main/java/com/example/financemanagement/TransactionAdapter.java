package com.example.financemanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class TransactionAdapter extends BaseAdapter {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    Context context;
    ArrayList<Object> listTransaction;


    public TransactionAdapter(Context context, ArrayList<Object> listTransaction) {
        this.context = context;
        this.listTransaction = listTransaction;
    }

    @Override
    public int getCount() {
        return listTransaction.size();
    }

    @Override
    public Object getItem(int i) {
        return listTransaction.get(i);
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemViewType(int position) {
        if (listTransaction.get(position) instanceof HeaderModel) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2; // Header và Item
    }


    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        int viewType = getItemViewType(i);
        View view = convertView; // Sử dụng convertView
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (viewType == TYPE_HEADER) {
                view = inflater.inflate(R.layout.transaction_list, null);
                HeaderViewHolder headerHolder = new HeaderViewHolder();
                headerHolder.tvDate = view.findViewById(R.id.tvDate);
                headerHolder.tvSpending = view.findViewById(R.id.tvSpending);
                headerHolder.tvIncome = view.findViewById(R.id.tvIncome);
                view.setTag(headerHolder);
            } else {
                view = inflater.inflate(R.layout.transaction_list, null);
                ItemViewHolder itemHolder = new ItemViewHolder();
                itemHolder.tvCategoryName = view.findViewById(R.id.tvCategoryName);
                itemHolder.tvDescription = view.findViewById(R.id.tvDescription);
                itemHolder.tvAmount = view.findViewById(R.id.tvAmount);
                itemHolder.ivCategoryIcon = view.findViewById(R.id.ivCategoryIcon);
                view.setTag(itemHolder);
            }
        }

        if (viewType == TYPE_HEADER) {
            HeaderViewHolder holder = (HeaderViewHolder) view.getTag();
            HeaderModel header = (HeaderModel) getItem(i);
            // Set visibility for header views
            View headerLayout = view.findViewById(R.id.headerLayout);
            View itemLayout = view.findViewById(R.id.itemLayout);
            if (headerLayout != null) {
                headerLayout.setVisibility(View.VISIBLE);
            }
            if (itemLayout != null) {
                itemLayout.setVisibility(View.GONE);
            }


            holder.tvDate.setText(header.getDate());
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.##");
            holder.tvSpending.setText("Chi tiêu: " + decimalFormat.format(header.getTotalSpending()));
            holder.tvIncome.setText("Thu nhập: " + decimalFormat.format(header.getTotalIncome()));
        } else {
            ItemViewHolder holder = (ItemViewHolder) view.getTag();
            TransactionModel transaction = (TransactionModel) getItem(i);

            // Set visibility for item views
            View headerLayout = view.findViewById(R.id.headerLayout);
            View itemLayout = view.findViewById(R.id.itemLayout);
            if (headerLayout != null) {
                headerLayout.setVisibility(View.GONE);
            }
            if (itemLayout != null) {
                itemLayout.setVisibility(View.VISIBLE);
            }

            holder.tvCategoryName.setText(transaction.getCategoryName());
            holder.tvDescription.setText(transaction.getDescription());

            DecimalFormat decimalFormat = new DecimalFormat("#,##0.##");
            holder.tvAmount.setText(decimalFormat.format(Double.parseDouble(transaction.getAmount())));
        }
        return view;
    }


    // ViewHolder for Header
    static class HeaderViewHolder {
        TextView tvDate;
        TextView tvSpending;
        TextView tvIncome;
    }

    // ViewHolder for Item
    static class ItemViewHolder {
        TextView tvCategoryName;
        TextView tvDescription;
        TextView tvAmount;
        ImageView ivCategoryIcon;
    }
}