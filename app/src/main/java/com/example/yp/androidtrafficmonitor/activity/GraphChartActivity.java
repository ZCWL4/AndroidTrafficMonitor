package com.example.yp.androidtrafficmonitor.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.yp.androidtrafficmonitor.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

/**
 * Created by yp on 2016/9/26.
 */
public class GraphChartActivity extends AppCompatActivity{
    private LineChart chart;
    private LineData data;
    private ArrayList<String> xVals;
    private LineDataSet dataSet;
    private ArrayList<Entry> yVals;
    private Random random;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.line_chart_layout);//设置主视图

        chart = (LineChart) findViewById(R.id.chart);

        xVals = new ArrayList<>();
        yVals = new ArrayList<>();
        random = new Random();//产生随机数字

        for(int i = 1 ; i<=getCurrentDay(); i++) {
            SharedPreferences sp = getSharedPreferences("trafficInfor", Context.MODE_PRIVATE);

            float x = sp.getLong(String.valueOf(i),0)/1024/1024; //获取value值
            yVals.add(new Entry(x, i));//创建Entry并且添加到Y值的list中，Y轴的值，一个entry代表一个显示的值
            xVals.add( i + "号");//横坐标显示xxx月
        }

        dataSet = new LineDataSet(yVals, "流量");//创建数据集并设置标签

        //dataSet.setColors(ColorTemplate.COLORFUL_COLORS);//设置数据集显示的颜色，预支颜色模版ColorTemplate，也可以设置单一颜色和colors
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setHighlightEnabled(true);//设置高亮
        dataSet.setValueTextColor(Color.BLUE);//设置Value值的显示文字颜色，字体大小和字体种类，这里我没有添加对应字体可以自己修改

        dataSet.setValueTextSize(10.0f);
        dataSet.setValueTypeface(null);

        data = new LineData(xVals, dataSet);//创建LineData,x轴List和Y轴数据集为参数
        chart.setBorderColor(Color.GRAY);
        chart.setDrawGridBackground(false);
        chart.setData(data);//给图表添加数据
        chart.setDescription("流量使用");//设置图表描述的内容位置，字体等等
        chart.setDescriptionColor(Color.GREEN);
        chart.setDescriptionTextSize(15f);
        chart.setDescriptionPosition(540, 40);

        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的显示位置，通过XAxisPosition枚举类型来设置
        chart.getXAxis().setAxisMinValue(0.0f);//设置X轴的最小值
        chart.getAxisRight().setEnabled(false);//关闭右边的Y轴，因为默认有两条，左边一条，右边一条，MPAndroidChart中有setEnabled方法的元素基本上都是使能的作用
        chart.animateY(1000);//动画效果，MPAndroidChart中还有很多动画效果可以挖掘

    }


    public int getCurrentDay(){
        SimpleDateFormat sf = new SimpleDateFormat("dd");
        Calendar c = Calendar.getInstance();
        return Integer.valueOf(sf.format(c.getTime()));
    }

}
