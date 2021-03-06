package com.kyberswap.android.presentation.main.balance.chart

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.kyberswap.android.AppExecutors
import com.kyberswap.android.R
import com.kyberswap.android.databinding.FragmentCandleStickChartBinding
import com.kyberswap.android.domain.model.Chart
import com.kyberswap.android.domain.model.Token
import com.kyberswap.android.domain.model.Wallet
import com.kyberswap.android.presentation.base.BaseFragment
import com.kyberswap.android.presentation.helper.Navigator
import com.kyberswap.android.presentation.splash.GetWalletState
import com.kyberswap.android.util.di.ViewModelFactory
import com.kyberswap.android.util.ext.toDisplayNumber
import kotlinx.android.synthetic.main.fragment_candle_stick_chart.*
import java.math.BigDecimal
import javax.inject.Inject

class CandleStickChartFragment : BaseFragment() {

    private lateinit var binding: FragmentCandleStickChartBinding

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var appExecutors: AppExecutors

    private var token: Token? = null

    private var chartType: ChartType? = null

    private var market: String? = null

    var changedRate: BigDecimal = BigDecimal.ZERO

    private var isShowMarker: Boolean = false

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(CandleStickChartViewModel::class.java)
    }

    private val lineWidth by lazy {
        resources.getDimension(R.dimen.line_chart_width)
    }

    private val gridLineWidth by lazy {
        lineWidth * 0.3f
    }

    private val resolution by lazy {
        if (chartType == ChartType.DAY) {
            15
        } else if (chartType == ChartType.WEEK) {
            60
        } else if (chartType == ChartType.MONTH) {
            60
        } else {
            60 * 24
        }
    }

    private val decreasingColor by lazy {
        context?.let { ContextCompat.getColor(it, R.color.desc_color) }
    }

    private val increasingColor by lazy {
        context?.let { ContextCompat.getColor(it, R.color.inc_color) }
    }

    private val lineColor by lazy {
        ContextCompat.getColor(context!!, R.color.limit_line_color)
    }

    private val gridLineColor by lazy {
        ContextCompat.getColor(context!!, R.color.color_chart_grid_line)
    }

    private val typeFace by lazy {
        Typeface.createFromAsset(activity!!.assets, "fonts/Roboto-Medium.ttf")
    }

    private val lineLimitTextColor by lazy {
        ContextCompat.getColor(context!!, R.color.limit_line_text_color)
    }
    private var wallet: Wallet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        token = arguments?.getParcelable(TOKEN_PARAM)
        chartType = arguments?.getParcelable(CHART_TYPE)
        market = arguments?.getString(MARKET_PARAM)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCandleStickChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.candleStickChart.setNoDataText(getString(R.string.chart_updating_data))
        viewModel.getSelectedWallet()
        viewModel.getSelectedWalletCallback.observe(parentFragment!!.viewLifecycleOwner, Observer {
            it?.peekContent()?.let { state ->
                when (state) {
                    is GetWalletState.Success -> {
                        viewModel.getChartData(
                            market,
                            chartType
                        )
                    }
                    is GetWalletState.ShowError -> {

                    }
                }
            }
        })

        viewModel.getChartCallback.observe(viewLifecycleOwner, Observer {
            it?.getContentIfNotHandled()?.let { state ->
                when (state) {
                    is GetChartState.Success -> {
                        configChart(state.chart)
                        updateDataSet(state.chart, binding.candleStickChart)
                    }

                    is GetChartState.ShowError -> {
                        binding.candleStickChart.setNoDataText(getString(R.string.something_wrong))
                        binding.candleStickChart.invalidate()
                        showError(
                            state.message ?: getString(R.string.something_wrong)
                        )
                    }
                }
            }
        })
    }

    private fun configChart(chart: Chart) {
        candleStickChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        candleStickChart.axisRight.isEnabled = true
        candleStickChart.axisLeft.isEnabled = false
        candleStickChart.setDrawGridBackground(false)
        candleStickChart.legend.isEnabled = false
        candleStickChart.description.isEnabled = false
//        candleStickChart.xAxis.setDrawGridLines(true)
//        candleStickChart.xAxis.setDrawAxisLine(false)

        candleStickChart.axisRight.setDrawAxisLine(true)
        candleStickChart.axisRight.setDrawGridLines(true)
        candleStickChart.axisRight.setDrawGridLinesBehindData(true)
        candleStickChart.axisRight.gridColor = gridLineColor
        candleStickChart.axisRight.gridLineWidth = gridLineWidth
        candleStickChart.xAxis.setDrawAxisLine(true)
        candleStickChart.xAxis.setDrawGridLines(true)
        candleStickChart.xAxis.setDrawGridLinesBehindData(true)
        candleStickChart.xAxis.gridColor = gridLineColor
        candleStickChart.xAxis.gridLineWidth = gridLineWidth

        candleStickChart.xAxis.axisLineColor = gridLineColor
        candleStickChart.xAxis.axisLineWidth = gridLineWidth

        candleStickChart.axisRight.axisLineColor = gridLineColor
        candleStickChart.axisRight.axisLineWidth = gridLineWidth


        candleStickChart.xAxis.setAvoidFirstLastClipping(true)
        candleStickChart.fitScreen()
        candleStickChart.xAxis.granularity = 1f
        candleStickChart.xAxis.setLabelCount(
            if (chartType == ChartType.YEAR || chartType == ChartType.ALL) 2 else 3,
            false
        )
        candleStickChart.xAxis.valueFormatter = CandleStickXAxisValueFormatter(chart, resolution)
        val markerView = context?.let {
            CustomMarkerView(
                it,
                XAxisValueFormatter(chart)
            )
        }
        markerView?.chartView = candleStickChart
        candleStickChart.marker = markerView
        candleStickChart.isHighlightPerTapEnabled = false
        candleStickChart.isHighlightPerDragEnabled = true
        candleStickChart.onChartGestureListener = object : OnChartGestureListener {
            override fun onChartGestureEnd(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
                candleStickChart.isDragEnabled = true
            }

            override fun onChartFling(
                me1: MotionEvent?,
                me2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ) {
                isShowMarker = false
                candleStickChart.highlightValue(null)
            }

            override fun onChartSingleTapped(me: MotionEvent?) {
                isShowMarker = false
                candleStickChart.highlightValue(null)
            }

            override fun onChartGestureStart(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
                candleStickChart.isDragEnabled = true
            }

            override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
                candleStickChart.highlightValue(null)
            }

            override fun onChartLongPressed(me: MotionEvent?) {
                isShowMarker = true
                candleStickChart.isDragEnabled = false
                if (me != null) {

                    val highlightByTouchPoint =
                        candleStickChart.getHighlightByTouchPoint(me.x, me.y)
                    candleStickChart.highlightValue(highlightByTouchPoint)
                    val listener = candleStickChart.onTouchListener
                    if (listener is CandleStickChartTouchListener) {
                        if (listener.touchMode == 0) {
                            listener.touchMode = 1
                        }
                    }
                }
            }

            override fun onChartDoubleTapped(me: MotionEvent?) {
            }

            override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {

                if (candleStickChart.isDragEnabled && isShowMarker) {
                    candleStickChart.isDragEnabled = false
                }
                if (me != null && isShowMarker && isShowMarker) {
                    val highlightByTouchPoint =
                        candleStickChart.getHighlightByTouchPoint(me.x, me.y)
                    candleStickChart.highlightValue(highlightByTouchPoint)
                }
            }
        }
    }

    private fun updateChangeRate() {
        val parent = parentFragment
        if (parent is ChartFragment) {
            chartType?.let { parent.updateChangeRate(changedRate, it) }
        }
    }

    private fun updateDataSet(chart: Chart, candleStickChart: CandleStickChart) {
        if (chart.t.isEmpty()) {
            if (token?.isOther == true) {
                candleStickChart.setNoDataText(getString(R.string.unsupported_token))
            } else {
                candleStickChart.setNoDataText(getString(R.string.chart_no_token_data))
            }
            return
        }
        candleStickChart.setNoDataText("")
        val last = chart.c.last()
        val first = chart.c.first()

        if (first > BigDecimal.ZERO) {
            changedRate = (last - first) / first * BigDecimal(100)
        }
        updateChangeRate()
        candleStickChart.resetTracking()
        val entries = mutableListOf<CandleEntry>()
        chart.t.forEachIndexed { index, time ->
            entries.add(
                CandleEntry(
                    (chart.t[index] - chart.t[0]).toFloat() / 60f / resolution,
                    chart.h[index].toFloat(),
                    chart.l[index].toFloat(),
                    chart.o[index].toFloat(),
                    chart.c[index].toFloat()
                )
            )
        }

        val candleDataSet = CandleDataSet(entries, "Data Set")

        candleDataSet.setDrawIcons(false)
        candleDataSet.axisDependency = AxisDependency.RIGHT
        decreasingColor?.let {
            candleDataSet.decreasingColor = it
        }
        increasingColor?.let {
            candleDataSet.increasingColor = it
        }

        candleDataSet.decreasingPaintStyle = Paint.Style.FILL
        candleDataSet.increasingPaintStyle = Paint.Style.FILL
        candleDataSet.shadowColorSameAsCandle = true

        candleDataSet.setDrawHighlightIndicators(true)
        candleDataSet.highlightLineWidth = lineWidth
        candleDataSet.enableDashedHighlightLine(10f, 10f, 0f)
        candleDataSet.setDrawValues(false)

        val candleData = CandleData(candleDataSet)
        candleStickChart.data = candleData
        if (entries.size < 60) {
            candleStickChart.xAxis.axisMaximum = 60f
        }

        val max = chart.h.max() ?: BigDecimal.ZERO
        val min = chart.l.min() ?: BigDecimal.ZERO

        val ll1 = LimitLine(max.toFloat(), max.toDisplayNumber())
        ll1.lineWidth = lineWidth
        ll1.typeface = typeFace
        ll1.lineColor = lineColor
        ll1.textColor = lineLimitTextColor
        ll1.enableDashedLine(10f, 10f, 0f)
        ll1.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP

        val ll2 = LimitLine(min.toFloat(), min.toDisplayNumber())
        ll1.lineWidth = lineWidth
        ll2.typeface = typeFace
        ll2.lineColor = lineColor
        ll2.textColor = lineLimitTextColor
        ll2.enableDashedLine(10f, 10f, 0f)
        ll2.labelPosition = LimitLine.LimitLabelPosition.LEFT_BOTTOM

        candleStickChart.axisRight.addLimitLine(ll1)
        candleStickChart.axisRight.addLimitLine(ll2)


        if (chartType == ChartType.DAY || chartType == ChartType.WEEK) {
            val entry = entries[entries.size - 1]
            candleStickChart.zoom(entries.size / 60f, 1f, 0f, 0f)
            candleStickChart.moveViewToAnimated(
                entry.x,
                entry.y,
                candleDataSet.axisDependency,
                1500
            )
        }

        candleStickChart.invalidate()
    }

    companion object {
        private const val TOKEN_PARAM = "token_param"
        private const val CHART_TYPE = "chart_type"
        private const val MARKET_PARAM = "market_param"
        fun newInstance(token: Token?, type: ChartType, market: String) =
            CandleStickChartFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(TOKEN_PARAM, token)
                    putParcelable(CHART_TYPE, type)
                    putString(MARKET_PARAM, market)
                }
            }
    }
}
