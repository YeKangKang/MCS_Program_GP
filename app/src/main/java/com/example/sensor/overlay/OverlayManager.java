package com.example.sensor.overlay;

import static com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnPolylineClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

// 该类提供一个能够显示和管理多个Overlay的基类
public abstract class OverlayManager implements OnMarkerClickListener, OnPolylineClickListener {

    BaiduMap mBaiduMap = null;
    private List<OverlayOptions> mOverlayOptionList = null;

    List<Overlay> mOverlayList = null;


    public OverlayManager(BaiduMap baiduMap) {
        mBaiduMap = baiduMap;
        // mBaiduMap.setOnMarkerClickListener(this);
        if (mOverlayOptionList == null) {
            mOverlayOptionList = new ArrayList<OverlayOptions>();
        }
        if (mOverlayList == null) {
            mOverlayList = new ArrayList<Overlay>();
        }
    }


    public abstract List<OverlayOptions> getOverlayOptions();

    // 将所有Overlay 添加到地图上
    public final void addToMap() {
        if (mBaiduMap == null) {
            return;
        }

        removeFromMap();
        List<OverlayOptions> overlayOptions = getOverlayOptions();
        if (overlayOptions != null) {
            mOverlayOptionList.addAll(overlayOptions);
        }

        for (OverlayOptions option : mOverlayOptionList) {
            mOverlayList.add(mBaiduMap.addOverlay(option));
        }
    }

    // 将所有Overlay 从 地图上消除
    public final void removeFromMap() {
        if (mBaiduMap == null) {
            return;
        }
        for (Overlay marker : mOverlayList) {
            marker.remove();
        }
        mOverlayOptionList.clear();
        mOverlayList.clear();

    }

    // 缩放地图，使所有Overlay都在合适的视野内
    public void zoomToSpan() {
        if (mBaiduMap == null) {
            return;
        }
        if (mOverlayList.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Overlay overlay : mOverlayList) {
                // polyline 中的点可能太多，只按marker 缩放
                if (overlay instanceof Marker) {
                    builder.include(((Marker) overlay).getPosition());
                }
            }
            MapStatus mapStatus = mBaiduMap.getMapStatus();
            if (null != mapStatus){
                int width = mapStatus.winRound.right - mBaiduMap.getMapStatus().winRound.left - 400;
                int height = mapStatus.winRound.bottom - mBaiduMap.getMapStatus().winRound.top - 400;
                mBaiduMap.setMapStatus(MapStatusUpdateFactory
                        .newLatLngBounds(builder.build(), width, height));
            }

        }
    }


     // 设置显示在规定宽高中的地图地理范围
    public void zoomToSpanPaddingBounds(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        if (mBaiduMap == null) {
            return;
        }
        if (mOverlayList.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Overlay overlay : mOverlayList) {
                // polyline 中的点可能太多，只按marker 缩放
                if (overlay instanceof Marker) {
                    builder.include(((Marker) overlay).getPosition());
                }
            }

            mBaiduMap.setMapStatus(MapStatusUpdateFactory
                    .newLatLngBounds(builder.build(), paddingLeft, paddingTop, paddingRight, paddingBottom));
        }
    }

}
