package com.evader.rookies.lingo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by bshah on 4/21/2016.
 */
public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyAdapterHolder>{

    private List<UrbanDefinition> companies;
    private Context context;

    public CompanyAdapter(List<UrbanDefinition> companiesHolder, Context contextHolder) {
        companies = companiesHolder;
        context = contextHolder;
    }


    @Override
    public int getItemCount() {
        return companies.size();
    }

    @Override
    public void onBindViewHolder(CompanyAdapterHolder companyViewHolder, int i) {
        final UrbanDefinition company = companies.get(i);

        companyViewHolder.term.setText(company.getTerm());
        companyViewHolder.description.setText(company.getDefinition());
    }

    @Override
    public CompanyAdapterHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_layout, viewGroup, false);

        return new CompanyAdapterHolder(itemView);
    }

    public static class CompanyAdapterHolder extends RecyclerView.ViewHolder {
        protected TextView description;
        protected TextView term;


        public CompanyAdapterHolder(View v) {
            super(v);
            term =  (TextView) v.findViewById(R.id.termhj);
            description = (TextView) v.findViewById(R.id.definitionhj);
        }
    }
}
