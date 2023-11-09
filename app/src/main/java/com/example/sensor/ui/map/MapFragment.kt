package com.example.sensor.ui.map

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.BitmapDescriptor
import com.baidu.mapapi.map.BitmapDescriptorFactory
import com.baidu.mapapi.map.MapStatus
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.Marker
import com.baidu.mapapi.map.MarkerOptions
import com.baidu.mapapi.map.MyLocationConfiguration
import com.baidu.mapapi.map.MyLocationData
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.core.SearchResult
import com.baidu.mapapi.search.route.BikingRouteResult
import com.baidu.mapapi.search.route.DrivingRouteResult
import com.baidu.mapapi.search.route.IndoorRouteResult
import com.baidu.mapapi.search.route.MassTransitRouteResult
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener
import com.baidu.mapapi.search.route.RoutePlanSearch
import com.baidu.mapapi.search.route.TransitRouteResult
import com.baidu.mapapi.search.route.WalkingRouteLine
import com.baidu.mapapi.search.route.WalkingRoutePlanOption
import com.baidu.mapapi.search.route.WalkingRouteResult
import com.baidu.mapapi.search.sug.SuggestionResult
import com.baidu.mapapi.walknavi.WalkNavigateHelper
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam
import com.baidu.mapapi.walknavi.params.WalkRouteNodeInfo
import com.dongquan.adapter.SearchResultAdapter
import com.example.sensor.R
import com.example.sensor.databinding.FragmentMapBinding
import com.example.sensor.overlay.OverlayManager
import com.example.sensor.overlay.WalkingRouteOverlay
import com.example.sensor.search.RouteLineAdapter
import com.example.sensor.search.SelectRouteDialog
import com.example.sensor.ui.activity.WNaviGuideActivity


class MapFragment : Fragment(), OnGetRoutePlanResultListener, SuggestSearchCallback {

    private var _binding: FragmentMapBinding? = null

    private val binding get() = _binding!!

    private val planSearch: RoutePlanSearch by lazy {
        RoutePlanSearch.newInstance()
    }

    private var walkingResult: WalkingRouteResult? = null

    private var routeLine: WalkingRouteLine? = null

    private var routeOverlay: OverlayManager? = null

    private var hasShowDialog = false

    private lateinit var walkParam: WalkNaviLaunchParam

    private val viewModel by lazy {
        ViewModelProvider(this)[MapViewModel::class.java]
    }

    private val searchAdapter by lazy {
        SearchResultAdapter()
    }

    private var isChange = false

    private val bdStart = BitmapDescriptorFactory.fromResource(R.drawable.icon_st)
    private val bdEnd = BitmapDescriptorFactory.fromResource(R.drawable.icon_en)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        binding.baiduMap.map.isMyLocationEnabled = true
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.searchList.layoutManager = layoutManager
        binding.searchList.adapter = searchAdapter
        initMap()
        onClick()
        return binding.root
    }

    private fun initNavi() {
        if(viewModel.getStartInfo() == null && viewModel.getBDLocation() == null) {
            Toast.makeText(requireContext(), "Please enter starting point location", Toast.LENGTH_SHORT).show()
            return
        }

        if (viewModel.getEndInfo() == null) {
            Toast.makeText(requireContext(), "Please enter the end location", Toast.LENGTH_SHORT).show()
            return
        }

        val startPt: LatLng = if(viewModel.getStartInfo() != null) {
            viewModel.getStartInfo()!!.getPt()
        } else {
            val location = viewModel.getBDLocation()!!
            LatLng(location.latitude, location.longitude)
        }

        val endPt = viewModel.getEndInfo()!!.getPt()

        val walkStartNode = WalkRouteNodeInfo()
        walkStartNode.location = startPt
        val walkEndNode = WalkRouteNodeInfo()
        walkEndNode.location = endPt
        walkParam = WalkNaviLaunchParam().startNodeInfo(walkStartNode).endNodeInfo(walkEndNode)

        initOverlay(startPt, endPt)
    }

    private fun initOverlay(startPt: LatLng, endPt: LatLng) {
        var ptStart = startPt
        var ptEnd = endPt
        val ooA = MarkerOptions().position(startPt).icon(bdStart).zIndex(9).draggable(true)
        val startMark = binding.baiduMap.map.addOverlay(ooA) as Marker
        startMark.isDraggable = true

        val ooB = MarkerOptions().position(endPt).icon(bdEnd).zIndex(5)
        val endMark = binding.baiduMap.map.addOverlay(ooB) as Marker
        endMark.isDraggable = true

        binding.baiduMap.map.setOnMarkerDragListener(object : BaiduMap.OnMarkerDragListener {
            override fun onMarkerDrag(marker: Marker?) {
                Log.d("Maizi", "onMarkerDrag")
            }

            override fun onMarkerDragEnd(marker: Marker?) {
                Log.d("Maizi", "onMarkerDragEnd")
                if(marker == startMark) {
                    ptStart = marker.position
                } else if (marker == endMark){
                    ptEnd = marker.position
                }

                val walkStartNode = WalkRouteNodeInfo()
                walkStartNode.location = startPt
                val walkEndNode = WalkRouteNodeInfo()
                walkEndNode.location = endPt
                walkParam = WalkNaviLaunchParam().startNodeInfo(walkStartNode).endNodeInfo(walkEndNode)
            }

            override fun onMarkerDragStart(marker: Marker?) {
                Log.d("Maizi", "onMarkerDragStart")
            }
        })
    }

    private fun onClick() {
        binding.btnSearch.setOnClickListener {
            if(viewModel.getStartPlanNode() == null) {
                Toast.makeText(requireContext(), "Please enter starting point", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(viewModel.getEndPlanNode() == null) {
                Toast.makeText(requireContext(), "Please enter the end", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.baiduMap.map.clear()
            planSearch.walkingSearch((WalkingRoutePlanOption()).from(viewModel.getStartPlanNode()).to(viewModel.getEndPlanNode()))
        }

        binding.btnNavigation.setOnClickListener {
            initNavi()
            walkParam.extraNaviMode(0)
            startWalkNavi()
        }

        binding.startNode.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 在文本改变之前执行的操作
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 在文本改变时执行的操作
                if(isChange) {
                    p0?.let {
                        viewModel.search(
                            it.toString(),
                            binding.startCity.text.trim().toString(),
                            true,
                            this@MapFragment
                        )
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                // 在文本改变后执行的操作
            }

        })

        binding.endNode.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 在文本改变之前执行的操作
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 在文本改变时执行的操作
                if(isChange) {
                    p0?.let {
                        viewModel.search(
                            it.toString(),
                            binding.startCity.text.trim().toString(),
                            false,
                            this@MapFragment
                        )
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                // 在文本改变后执行的操作
            }

        })

        searchAdapter.setOnItemOnclickListener(object : SearchResultAdapter.OnItemOnclickListener {
            override fun onItemOnclick(info: SuggestionResult.SuggestionInfo) {
                binding.searchLayout.visibility = View.GONE
                searchAdapter.clearData()
                viewModel.setSuggestionInfo(info)
                isChange = false
                if(viewModel.isStart) {
                    binding.startNode.setText(info.key)
                    binding.startNode.setSelection(info.key.length)
                    hideSoftInput(requireContext(),binding.startNode)
                } else {
                    binding.endNode.setText(info.key)
                    binding.endNode.setSelection(info.key.length)
                    hideSoftInput(requireContext(), binding.endNode)
                }
                isChange = true
            }
        })
    }

    private fun initMap() {
        LocationClient.setAgreePrivacy(true)
        val locationClient = LocationClient(requireContext().applicationContext)
        val clientOption = LocationClientOption()
        clientOption.openGps = true
        clientOption.setIsNeedAddress(true)
        clientOption.coorType = "bd09ll"
        clientOption.scanSpan = 1000
        clientOption.locationMode = LocationClientOption.LocationMode.Device_Sensors
        locationClient.locOption = clientOption

        val locationListener = MyLocationListener()
        locationClient.registerLocationListener(locationListener)
        locationClient.start()

        val config = MyLocationConfiguration.Builder(MyLocationConfiguration.LocationMode.FOLLOWING, true).build()
        binding.baiduMap.map.setMyLocationConfiguration(config)
        val builder = MapStatus.Builder()
        builder.overlook(0f)
        binding.baiduMap.map.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
        planSearch.setOnGetRoutePlanResultListener(this)
    }

    // 开始步行导航
    private fun startWalkNavi() {
        Log.d("Maizi", "startWalkNavi")
        try {
            WalkNavigateHelper.getInstance().initNaviEngine(requireActivity(), object : IWEngineInitListener {
                override fun engineInitSuccess() {
                    Log.d("Maizi", "WalkNavi engineInitSuccess")
                    routePlanWithWalkParam()
                }

                override fun engineInitFail() {
                    Log.d("Maizi", "WalkNavi engineInitFail")
                    WalkNavigateHelper.getInstance().unInitNaviEngine()
                }
            })
        } catch (e: Exception) {
            Log.d("Maizi", "startBikeNavi Exception")
            e.printStackTrace()
        }
    }

    // 以步行为默认选择计算路线
    // Walk mode as default, calculate the route
    private fun routePlanWithWalkParam() {
        WalkNavigateHelper.getInstance()
            .routePlanWithRouteNode(walkParam, object : IWRoutePlanListener {
                override fun onRoutePlanStart() {
                    Log.d("Maizi", "WalkNavi onRoutePlanStart")
                }

                override fun onRoutePlanSuccess() {
                    Log.d("Maizi", "onRoutePlanSuccess")
                    val intent = Intent()
                    intent.setClass(requireContext(), WNaviGuideActivity::class.java)
                    startActivity(intent)
                }

                override fun onRoutePlanFail(error: WalkRoutePlanError) {
                    Log.d("Maizi", "WalkNavi onRoutePlanFail$error"
                    )
                }
            })
    }

    override fun onResume() {
        binding.baiduMap.onResume()
        super.onResume()
    }

    override fun onPause() {
        binding.baiduMap.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        binding.baiduMap.map.isMyLocationEnabled = false
        binding.baiduMap.onDestroy()
        super.onDestroy()
        _binding = null
        viewModel.onDestroy()
    }

    inner class MyLocationListener : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation?) {
            Log.e("Maizi", "onReceiveLocation")
            location?.let {
                viewModel.setLocation(it)
                val localeData = MyLocationData.Builder().accuracy(it.radius)
                    .direction(it.direction).latitude(it.latitude).longitude(it.longitude).build()
                binding.baiduMap.map.setMyLocationData(localeData)

                Log.e("Maizi", "onReceiveLocation --- city = ${it.city} && country = ${it.country}")
                if(binding.startCity.text != it.city) {
                    binding.startCity.text = "${it.city}${it.district}"
                    binding.endCity.text = it.city
                }
                if(binding.startCountry.text != it.country) {
                    binding.startCountry.text = it.country
                    binding.endCountry.text = it.country
                }
                isChange = false
                binding.startNode.setText(it.addrStr)
                isChange = true
            }
        }

    }

    fun hideSoftInput(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (view != null && imm != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            // imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY); // 或者第二个参数传InputMethodManager.HIDE_IMPLICIT_ONLY
        }
    }

    override fun onGetWalkingRouteResult(result: WalkingRouteResult?) {
        Log.d("Maizi", "onGetWalkingRouteResult")
        result?.let {
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Hint")
                builder.setMessage("Search address is ambiguous, please reset!")
                builder.setPositiveButton("Confirm"
                ) { dialog, _ -> dialog.dismiss() }
                builder.create().show()
                return
            }

            if(result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(requireContext(), "Sorry, no results found", Toast.LENGTH_SHORT).show()
            } else {
                if(result.routeLines.size > 1) {
                    walkingResult = result
                    if(!hasShowDialog) {
                        val routeDialog = SelectRouteDialog(requireContext(), result.routeLines, RouteLineAdapter.Type.WALKING_ROUTE)
                        routeDialog.setOnDismissListener { hasShowDialog = false }
                        routeDialog.setOnItemInDlgClickLinster {
                            routeLine = walkingResult?.routeLines?.get(it)
                            val overlay = MyWalkingRouteOverlay(binding.baiduMap.map)
                            binding.baiduMap.map.setOnMarkerClickListener(overlay)
                            routeOverlay = overlay
                            overlay.setData(walkingResult?.routeLines?.get(it))
                            overlay.addToMap()
                            overlay.zoomToSpan()
                        }

                        routeDialog.show()
                        hasShowDialog = true
                    }
                } else if(result.routeLines.size == 1) {
                    routeLine = result.routeLines[0]
                    val overlay = MyWalkingRouteOverlay(binding.baiduMap.map)
                    binding.baiduMap.map.setOnMarkerClickListener(overlay)
                    routeOverlay = overlay
                    overlay.setData(result.routeLines[0])
                    overlay.addToMap()
                    overlay.zoomToSpan()
                } else {
                    Toast.makeText(requireContext(), "No feasible route", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onGetTransitRouteResult(result: TransitRouteResult?) {
        Log.d("Maizi", "onGetTransitRouteResult")
    }

    override fun onGetMassTransitRouteResult(result: MassTransitRouteResult?) {
        Log.d("Maizi", "onGetMassTransitRouteResult")
    }

    override fun onGetDrivingRouteResult(result: DrivingRouteResult?) {
        Log.d("Maizi", "onGetDrivingRouteResult")
    }

    override fun onGetIndoorRouteResult(result: IndoorRouteResult?) {
        Log.d("Maizi", "onGetIndoorRouteResult")
    }

    override fun onGetBikingRouteResult(result: BikingRouteResult?) {
        Log.d("Maizi", "onGetBikingRouteResult")
    }

    private class MyWalkingRouteOverlay(baiduMap: BaiduMap): WalkingRouteOverlay(baiduMap) {

        override fun getStartMarker(): BitmapDescriptor {

            return BitmapDescriptorFactory.fromResource(R.drawable.icon_st)
        }

        override fun getTerminalMarker(): BitmapDescriptor {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_en)
        }
    }

    override fun onResult(result: MutableList<SuggestionResult.SuggestionInfo>?) {
        if (!result.isNullOrEmpty()) {
            binding.searchLayout.visibility = View.VISIBLE
            searchAdapter.setData(result)
        }
    }
}