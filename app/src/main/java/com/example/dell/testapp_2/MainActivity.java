package com.example.dell.testapp_2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //private static Context mContext;
    private TextureMapView mMapView;
    private BaiduMap mBaiduMap;

    private static final int BAIDU_READ_PHONE_STATE = 100;
    //private Button mGetMylocationBN;
    //private TextView text1;

    private LocationClient mlocationClient;
    private MylocationListener mlistener;
    //private Context context;

    private double mLatitude;
    private double mLongitude;
    private float mCurrentX;

    //自定义图标
    private BitmapDescriptor mIconLocation;

    private MyOrientationListener myOrientationListener;
    //定位图层显示方式
    private MyLocationConfiguration.LocationMode locationMode;
    private boolean isFirstIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "暂时没有定位功能", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                getMyLocation();
            }
        });
        //地图显示
        mMapView = (TextureMapView) findViewById(R.id.mTexturemap);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
        mBaiduMap.setMapStatus(msu);



        //“扫码开始”按钮：
        Button button1=findViewById(R.id.button_0_1);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(MainActivity.this,SecondActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (Build.VERSION.SDK_INT >= 23) {
            showLocMap();
        } else {
            initLocation();//initLocation为定位方法
        }

    }
    private void initLocation() {
        locationMode = MyLocationConfiguration.LocationMode.NORMAL;

        //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        mlocationClient = new LocationClient(this);
        mlistener = new MylocationListener();

        //注册监听器
        mlocationClient.registerLocationListener(mlistener);
        //配置定位SDK各配置参数，比如定位模式、定位时间间隔、坐标系类型等
        LocationClientOption mOption = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //设置坐标类型
        mOption.setCoorType("bd09ll");
        //设置是否需要地址信息，默认为无地址
        mOption.setIsNeedAddress(true);
        //默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        mOption.setIsNeedLocationDescribe(true);
        //设置是否打开gps进行定位
        mOption.setOpenGps(true);
        //设置扫描间隔，单位是毫秒，当<1000(1s)时，定时定位无效
        int span = 1000;
        mOption.setScanSpan(span);
        //设置 LocationClientOption
        mlocationClient.setLocOption(mOption);

        //初始化图标,BitmapDescriptorFactory是bitmap 描述信息工厂类.
        mIconLocation = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_geo);

        myOrientationListener = new MyOrientationListener(getApplicationContext());
        //通过接口回调来实现实时方向的改变
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX = x;
            }
        });

    }
    public void showLocMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "没有权限,请手动开启定位权限", Toast.LENGTH_SHORT).show();
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE
            }, BAIDU_READ_PHONE_STATE);
        } else {
            initLocation();
        }
    }

    public void getMyLocation() {
        LatLng latLng = new LatLng(mLatitude, mLongitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(msu);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_pet) {
            startActivity(new Intent(MainActivity.this,PetActivity.class));
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(MainActivity.this,GalleryActivity.class));
        } else if (id == R.id.nav_club) {
            new AlertDialog.Builder(this).setMessage("现在没有这个功能")
                    .setTitle("抱歉：").setPositiveButton("返回",null)
                    .show();
        } else if (id == R.id.nav_manage) {
            new AlertDialog.Builder(this).setMessage("这个功能也是没有的")
                    .setTitle("抱歉：").setPositiveButton("返回",null)
                    .show();
        } else if (id == R.id.nav_share) {
            new AlertDialog.Builder(this).setMessage("这个功能也是没有的")
                    .setTitle("抱歉：").setPositiveButton("返回",null)
                    .show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();
        //开启定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!mlocationClient.isStarted()) {
            mlocationClient.start();
        }
        myOrientationListener.start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mlocationClient.stop();                       //退出时销毁定位
        myOrientationListener.stop();
        mBaiduMap.setMyLocationEnabled(false);   //关闭定位图层
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

    public class MylocationListener implements BDLocationListener {
        //定位请求回调接口

        //定位请求回调函数,这里面会得到定位信息
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //BDLocation 回调的百度坐标类，内部封装了如经纬度、半径等属性信息
            //MyLocationData 定位数据,定位数据建造器
            /**
             * 可以通过BDLocation配置如下参数
             * 1.accuracy 定位精度
             * 2.latitude 百度纬度坐标
             * 3.longitude 百度经度坐标
             * 4.satellitesNum GPS定位时卫星数目 getSatelliteNumber() gps定位结果时，获取gps锁定用的卫星数
             * 5.speed GPS定位时速度 getSpeed()获取速度，仅gps定位结果时有速度信息，单位公里/小时，默认值0.0f
             * 6.direction GPS定位时方向角度
             * */
            mLatitude = bdLocation.getLatitude();
            mLongitude = bdLocation.getLongitude();
            MyLocationData data = new MyLocationData.Builder()
                    .direction(mCurrentX)//设定图标方向
                    .accuracy(bdLocation.getRadius())//getRadius 获取定位精度,默认值0.0f
                    .latitude(mLatitude)//百度纬度坐标
                    .longitude(mLongitude)//百度经度坐标
                    .build();
            //设置定位数据, 只有先允许定位图层后设置数据才会生效，参见 setMyLocationEnabled(boolean)
            mBaiduMap.setMyLocationData(data);
            //配置定位图层显示方式,三个参数的构造器
            /**
             * 1.定位图层显示模式
             * 2.是否允许显示方向信息
             * 3.用户自定义定位图标
             * */
            MyLocationConfiguration configuration
                    = new MyLocationConfiguration(locationMode, true, null);
            //设置定位图层配置信息，只有先允许定位图层后设置定位图层配置信息才会生效，参见 setMyLocationEnabled(boolean)
            mBaiduMap.setMyLocationConfigeration(configuration);
            //text1.setText("您当前的位置为：" + bdLocation.getAddrStr());
            //判断是否为第一次定位,是的话需要定位到用户当前位置
            if (isFirstIn) {
                //地理坐标基本数据结构
                LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                //描述地图状态将要发生的变化,通过当前经纬度来使地图显示到该位置
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                //改变地图状态
                mBaiduMap.setMapStatus(msu);
                isFirstIn = false;
                Toast.makeText(getApplicationContext(), "您当前的位置为：" + bdLocation.getAddrStr(),Toast.LENGTH_LONG).show();
            }
        }
    }
    //Android 6.0 以上的版本申请权限的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case BAIDU_READ_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                    initLocation();
                } else {
                    // 没有获取到权限，做特殊处理
                    Toast.makeText(getApplicationContext(), "获取位置权限失败，请手动开启", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
