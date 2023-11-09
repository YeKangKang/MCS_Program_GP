/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.example.sensor.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.MassTransitRouteLine;
import com.example.sensor.R;

import java.util.List;


public class RouteLineAdapter extends BaseAdapter {

    private List<? extends RouteLine> mRouteLines;
    private LayoutInflater mLayoutInflater;
    private Type mType;

    public RouteLineAdapter(Context context, List<?extends RouteLine> routeLines, Type type) {
        this.mRouteLines = routeLines;
        mLayoutInflater = LayoutInflater.from(context);
        mType = type;
    }

    @Override
    public int getCount() {
        return mRouteLines.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NodeViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.activity_transit_item, null);
            holder = new NodeViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.transitName);
            holder.lightNum = (TextView) convertView.findViewById(R.id.lightNum);
            holder.dis = (TextView) convertView.findViewById(R.id.dis);
            convertView.setTag(holder);
        } else {
            holder = (NodeViewHolder) convertView.getTag();
        }

        switch (mType) {
            case  TRANSIT_ROUTE:
            case WALKING_ROUTE:
            case BIKING_ROUTE:
                holder.name.setText("Route" + (position + 1));
                int time = mRouteLines.get(position).getDuration();
                if ( time / 3600 == 0 ) {
                    holder.lightNum.setText( "Approximately need:" + time / 60 + "minutes" );
                } else {
                    holder.lightNum.setText( "Approximately need:" + time / 3600 + "hours" + (time % 3600) / 60 + "minutes" );
                }
                holder.dis.setText("The distance is about:" + mRouteLines.get(position).getDistance() + "meter");
                break;
            case DRIVING_ROUTE:
                DrivingRouteLine drivingRouteLine = (DrivingRouteLine) mRouteLines.get(position);
                holder.name.setText( "Route" + (position + 1));
                holder.lightNum.setText( "Number of traffic lights:" + drivingRouteLine.getLightNum());
                holder.dis.setText("Congestion distance is:" + drivingRouteLine.getCongestionDistance() + "meter");
                break;
            case MASS_TRANSIT_ROUTE:
                MassTransitRouteLine massTransitRouteLine = (MassTransitRouteLine) mRouteLines.get(position);
                holder.name.setText("Route" + (position + 1));
                holder.lightNum.setText( "Estimated time of arrival:" + massTransitRouteLine.getArriveTime() );
                holder.dis.setText("Gross fare:$" + massTransitRouteLine.getPrice());
                break;
            default:
                break;
        }

        return convertView;
    }

    private class NodeViewHolder {
        private TextView name;
        private TextView lightNum;
        private TextView dis;
    }

    public enum Type {
        MASS_TRANSIT_ROUTE, // 综合交通
        TRANSIT_ROUTE, // 公交
        DRIVING_ROUTE, // 驾车
        WALKING_ROUTE, // 步行
        BIKING_ROUTE // 骑行

    }
}
