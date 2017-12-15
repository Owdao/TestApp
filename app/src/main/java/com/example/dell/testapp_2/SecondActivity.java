package com.example.dell.testapp_2;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.TextureMapView;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity
implements View.OnClickListener,DialogInterface.OnClickListener
{

    //定义ViewPage滑动效果
    private View view1,view2;
    private ViewPager viewPager;
    private List<View> viewList;

    private TextView text2;
    private Button button1;
    private Button button2;
    private boolean listening;
    //动画
    private SilkyAnimation silkyAnimation;
    private SurfaceView surfaceView;

    //骑行时间
    private int timecounter;
    //地图
    private TextureMapView mMapView;
    private BaiduMap mBaiduMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_second);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "暂时没有定位功能", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //viewPage滑动效果
        viewPager=findViewById(R.id.viewpager);
        LayoutInflater inflater=getLayoutInflater();
        view1 = inflater.inflate(R.layout.viewpage_1, null);
        view2 = inflater.inflate(R.layout.viewpage_2,null);

        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(view1);
        viewList.add(view2);

        //适配器
        PagerAdapter pagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return viewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                // TODO Auto-generated method stub
                container.removeView(viewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                container.addView(viewList.get(position));


                return viewList.get(position);
            }
        };

        viewPager.setAdapter(pagerAdapter);

        //地图显示
        mMapView = (TextureMapView)view2.findViewById(R.id.mTexturemap_2);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        text2=view1.findViewById(R.id.text_2_2);
        button1=view1.findViewById(R.id.button_2_1);
        button1.setOnClickListener(this);
        button2=view1.findViewById(R.id.button_2_2);
        button2.setOnClickListener(this);
        surfaceView=view1.findViewById(R.id.surfaceView1);
        surfaceView.setOnClickListener(this);
        //动画
        silkyAnimation = new SilkyAnimation.Builder(surfaceView)
                .setCacheCount(8)
                .setFrameInterval(40)
                .setScaleType(SilkyAnimation.SCALE_TYPE_CENTER)
                .setRepeatMode(SilkyAnimation.MODE_INFINITE)
                .build();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                silkyAnimation.start("nocommand");
                listening=false;
            }
        },40);

        //骑行时间计时
        timecounter=0;
        handler.postDelayed(runnable,1000);
        //标题栏颜色设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.colorPrimary);           //设置标题栏颜色，此颜色在color中声明
    }
    @TargetApi(19)//标题栏颜色设置
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;        // a|=b的意思就是把a和b按位或然后赋值给a   按位或的意思就是先把a和b都换成2进制，然后用或操作，相当于a=a|b
        } else {
            winParams.flags &= ~bits;        //&是位运算里面，与运算  a&=b相当于 a = a&b  ~非运算符
        }
        win.setAttributes(winParams);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.button_2_1){
            if(!listening){
                silkyAnimation.stop();
                silkyAnimation.start("listening");
                button1.setText("停止录音");
                listening=true;
            }
            else{
                silkyAnimation.stop();
                silkyAnimation.start("nocommand");
                button1.setText("说话（录音）");
                listening=false;
            }
        }
        else if(view.getId()==R.id.button_2_2){
            handler.removeCallbacks(runnable);
            ((MyApplication)getApplicationContext()).addRidingTime(timecounter);
            ((MyApplication)getApplicationContext()).addRidingCount();
            new AlertDialog.Builder(this).setMessage("本次骑行时间为："+getStringTime(timecounter))
                    .setTitle("骑行结束：").setPositiveButton("返回",this)
                    .show();
        }
        else if(view.getId()==R.id.surfaceView1){
            silkyAnimation.stop();
            silkyAnimation.start("speak");
        }
    }
    Handler handler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            timecounter++;
            text2.setText("骑行时间："+getStringTime(timecounter));
            handler.postDelayed(this,1000);
        }
    };
    private String getStringTime(int count){
        int hour,min,sec;
        hour=(int)count/3600;
        min=(int)count%3600/60;
        sec=(int)count%60;
        return String.format("%02d:%02d:%02d",hour,min,sec);
    }
    @Override
    public void onClick(DialogInterface dialog,int which){
        if(which==DialogInterface.BUTTON_POSITIVE){
            finish();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
}
