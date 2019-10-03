package com.great.sachin.top10songs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FeedAdapter extends ArrayAdapter {
    private static final String TAG= "Feed Adapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;//inflates the xml objects;
    private List<FeedEntry> application;

    public FeedAdapter(Context context, int resource, List<FeedEntry> appliedResources) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater=LayoutInflater.from(context);
        this.application = appliedResources;
    }

    @Override
    public int getCount() {
        return  application.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView=layoutInflater.inflate(layoutResource,parent,false);

            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder =(ViewHolder) convertView.getTag();
        }
//
//        TextView tvName =  convertView.findViewById(R.id.tvName);
//        TextView tvArtist = convertView.findViewById(R.id.tvArtist);
//        TextView tvSummary = convertView.findViewById(R.id.tvSummary);

        FeedEntry currentApp = application.get(position);

        viewHolder.tvName.setText(currentApp.getName());
        viewHolder.tvArtist.setText(currentApp.getArtist());
        viewHolder.tvSummary.setText(currentApp.getSummary());

        return convertView;
    }
    private class ViewHolder{
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;

        public ViewHolder(View v) {
            this.tvName = v.findViewById(R.id.tvName);
            this.tvArtist = v.findViewById(R.id.tvArtist);
            this.tvSummary = v.findViewById(R.id.tvSummary);
        }

    }

}
