package com.example.jere.theamblepurpose;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//Custom adapter based on ArraayAdapter. Handles the population of the route list view with the needed routes.
public class RouteArrayAdapter extends ArrayAdapter<JSONObject> {
    public RouteArrayAdapter(Context context, ArrayList<JSONObject> routeArrayList) {
        super(context, 0, routeArrayList);
    }


    //Updates the list view with the need route content items.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JSONObject route = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.routelistcontent_activity, parent, false);
        }

        TextView routeName = (TextView) convertView.findViewById(R.id.routeName);

        try {
            routeName.setText(route.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        routeName.setTag(position);

        return convertView;
    }
}