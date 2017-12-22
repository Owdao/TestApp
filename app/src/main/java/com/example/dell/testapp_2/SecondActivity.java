package com.example.dell.testapp_2;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.AsyncTask;
import android.speech.SpeechRecognizer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import com.baidu.mapapi.model.LatLngBounds;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity
implements View.OnClickListener,DialogInterface.OnClickListener
{
    //坐标常数
    //紫荆公寓
    private String onRide = "第327位使用者，在你之前有253位用户留下了他们的故事，故事就像是旅程中的糖果，不知道在什么地方就" +
            "能捡到一颗，这样的未知性美丽而迷人，给熟悉的日常里添加不熟悉的色彩。你也可以在旅程中以语音的方式留下一段故事，" +
            "一起构筑这份献给陌生人的温柔。准备好开始你的旅程了吗";
    private String inZhuanwan = "上次有人在这里摔了一跤，你可要小心点儿";
    private String inJianguanBaogaoting = "别看这个地方其貌不扬，很多演讲都是在这里举办的哦，前些日子我刚掉到《白夜追凶》的坑里疯狂补剧，" +
            "然后......然后主创团队就来清华了！就是在建馆报告厅进行演讲！啊啊啊！我当时可兴奋了，可惜没有学生卡不能领票。";
    private String inJinaguan = "这是第203位用户留下的故事：10月份的时候，有次我连续三天待在建馆，没日没夜的干活，准备交图。交完图的那个中午" +
            "我从建馆走出来，本来有些丧丧的，推开门突然发现门口的三颗银杏叶子黄了。温暖的颜色和温暖的阳光一起扑面而来，像是要把人融化一样。虽然" +
            "也有些“到乡翻似烂柯人”的感触，但还是感觉被治愈了";
    private String Huifu = "我不干活的时候会在网上看，还可以直接跳广告呢，活在网络世界里可能就这点比较方便吧，哈哈哈哈哈哈哈哈哈";
    private String inZhulouqian = "这是第187位用户留下的故事，我是国旗仪仗队的一员，最近刚刚退队有些感慨，虽然要早起要晚训，要在黑得像夜里的清晨赶到" +
            "主楼，但每次看到天际微明时红旗鼓动着上升的那一刻，就觉得之前的一切付出都值得";
    private String inYibo = "这里是清华大学艺术博物馆，我和你说，这里可高级了，展出过达芬奇的手稿，伦勃朗和莫奈的画，简直就是清华大学艺术爱好者的天堂啊" +
            "我也好想进去逛逛";
    private String onEnd = "看来这次行程就要结束了，没关系，我啥都懂点儿还能讲段子，你肯定会忍不住再来找我唠嗑的对不对？下次要找我玩哦，拜拜";
    private RecognizerDialog mIatD;
    private RecognizerDialogListener mDListener;
    private SpeechSynthesizer mTts;
    private SynthesizerListener mSynListener;
    private String result; //use for speech result
    private Position Zijing;
    private static LatLng northeast_Zijing=new LatLng(40.0178480000,116.3367140000);
    private static LatLng southwest_Zijing=new LatLng(40.0173780000,116.3356450000);
    //六教
    private Position Liujiao;
    private static LatLng northeast_Liujiao=new LatLng(40.0093000000,116.3372260000);
    private static LatLng southwest_Liujiao=new LatLng(40.0079180000,116.3356140000);
    //主楼
    private Position Zhulou;
    private static LatLng northeast_Zhulou=new LatLng(40.0080670000,116.3407430000);
    private static LatLng southwest_Zhulou=new LatLng(40.0067200000,116.3373790000);
    //主楼前
    private Position Zhulouqian;
    private static LatLng northeast_Zhulouqian=new LatLng(40.0069510000,116.3403070000);
    private static LatLng southwest_Zhulouqian=new LatLng(40.0059530000,116.3383760000);
    //建馆报告厅
    private Position Jianguanbaogaoting;
    private static LatLng northeast_Jianguanbaogaoting=new LatLng(40.0058320000,116.3411250000);
    private static LatLng southwest_Jianguanbaogaoting=new LatLng(40.0053350000,116.3404740000);
    //建管
    private Position Jianguan;
    private static LatLng northeast_Jianguan=new LatLng(40.0056420000,116.3402890000);
    private static LatLng southwest_Jianguan=new LatLng(40.0049230000,116.3391130000);
    //艺术博物馆
    private Position Yibo;
    private static LatLng northeast_Yibo=new LatLng(40.0079870000,116.3425220000);
    private static LatLng southwest_Yibo=new LatLng(40.0069720000,116.3410040000);
    //美院
    private Position Meiyuan;
    private static LatLng northeast_Meiyuan=new LatLng(40.0063980000,116.3434200000);
    private static LatLng southwest_Meiyuan=new LatLng(40.0048060000,116.3415200000);

    //定义ViewPage滑动效果
    private View view1,view2;
    private ViewPager viewPager;
    private List<View> viewList;

    private TextView text2;
    private Button button1;
    private Button button2;
    private boolean listening;
    private boolean speaking;
    //动画
    private SilkyAnimation silkyAnimation;
    private SurfaceView surfaceView;

    //骑行时间
    private int timecounter;
    //地图
    private TextureMapView mMapView;
    private BaiduMap mBaiduMap;

    private static final int BAIDU_READ_PHONE_STATE = 100;
    //private Button mGetMylocationBN;
    //private TextView text1;

    private LocationClient mlocationClient;
    private MylocationListener2 mlistener;
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
        SpeechUtility.createUtility(this,SpeechConstant.APPID +"=5a29fbfa");

        setContentView(R.layout.activity_second);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "暂时没有定位功能", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1,true);
                getMyLocation();
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
        mDListener = new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                if(recognizerResult != null) {
                    result = parseIatResult(recognizerResult.getResultString());
                    mIatD.hide();
                    if (result.indexOf("追剧") != -1){
                        mTts.startSpeaking(Huifu, mSynListener);
                    }
                }
                else {
                    //tv.setText("声音太小了~");
                }
            }

            @Override
            public void onError(SpeechError speechError) {

            }
        };
        mSynListener = new SynthesizerListener() {
            //会话结束回调接口，没有错误时，error为null
            public void onCompleted(SpeechError error) {
            }
            //缓冲进度回调
            //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
            public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            }
            //开始播放
            public void onSpeakBegin() {
            }
            //暂停播放
            public void onSpeakPaused() {
            }
            //播放进度回调
            //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
            public void onSpeakProgress(int percent, int beginPos, int endPos) {
            }
            //恢复播放回调接口
            public void onSpeakResumed() {
            }
            //会话事件回调接口
            public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
            }
        };
        viewPager.setAdapter(pagerAdapter);
        mTts = SpeechSynthesizer.createSynthesizer(this, null);
        //mIat = SpeechRecognizer.createRecognizer(MainActivity.this, null);
        mIatD = new RecognizerDialog(this, null);
        mIatD.setListener(mDListener);
        setIatParam("listenfile");
        //地图显示
        mTts.startSpeaking(onRide, mSynListener);
        mMapView = (TextureMapView)view2.findViewById(R.id.mTexturemap_2);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
        mBaiduMap.setMapStatus(msu);
        iniBounds();

        text2=view1.findViewById(R.id.text_2_2);
        button1=view1.findViewById(R.id.button_2_1);
        button1.setOnClickListener(this);
        button2=view1.findViewById(R.id.button_2_2);
        button2.setOnClickListener(this);
        surfaceView=view1.findViewById(R.id.surfaceView1);
        //surfaceView.setOnClickListener(this);
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
        mlistener =new MylocationListener2();

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
            ActivityCompat.requestPermissions(SecondActivity.this, new String[]{
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
                mIatD.show();
                silkyAnimation.stop();
                silkyAnimation.start("listening");
                button1.setText("停止录音");
                listening=true;
                speaking=false;
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
        /*else if(view.getId()==R.id.surfaceView1){
            silkyAnimation.stop();
            silkyAnimation.start("speak");
        }*/
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

    public class MylocationListener2 implements BDLocationListener {
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

            //判断是否为第一次定位,是的话需要定位到用户当前位置
            if (isFirstIn) {
                //地理坐标基本数据结构
                LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                //描述地图状态将要发生的变化,通过当前经纬度来使地图显示到该位置
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                //改变地图状态
                mBaiduMap.setMapStatus(msu);
                isFirstIn = false;
                Toast.makeText(getApplicationContext(), "扫码成功，骑行开始",Toast.LENGTH_SHORT).show();
            }
            //这里可以获得位置信息！！！！！！！！！！！！！！！！！！！！！！！！！！！！
            //text1.setText("您当前的位置为：" + bdLocation.getAddrStr());
            //检测是否进入某地址范围
            positionCheck(bdLocation);
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
    //存储位置范围坐标
    public class Position{
        public int ID;
        //public LatLng NorthEast;
        //public LatLng SouthWest;
        public boolean Trigger;
        public LatLngBounds Bounds;

        Position(int id,LatLng northEast,LatLng southWest ){
            this.ID=id;
            //this.NorthEast=northEast;
            //this.SouthWest=southWest;
            this.Trigger=true;
            this.Bounds=new LatLngBounds.Builder()
                    .include(northEast)
                    .include(southWest)
                    .build();
        }
        public boolean isTrigger(LatLng mCurrentLat){
            if(this.Bounds.contains(mCurrentLat)&&Trigger){
                Trigger=false;
                return true;
            }
            return false;
        }
    }
    private void iniBounds(){
        Zijing=new Position(0,northeast_Zijing,southwest_Zijing);
        Liujiao=new Position(1,northeast_Liujiao,southwest_Liujiao);
        Zhulou=new Position(2,northeast_Zhulou,southwest_Zhulou);
        Zhulouqian=new Position(3,northeast_Zhulouqian,southwest_Zhulouqian);
        Jianguanbaogaoting=new Position(4,northeast_Jianguanbaogaoting,southwest_Jianguanbaogaoting);
        Jianguan=new Position(5,northeast_Jianguan,southwest_Jianguan);
        Yibo=new Position(6,northeast_Yibo,southwest_Yibo);
        Meiyuan=new Position(7,northeast_Meiyuan,southwest_Meiyuan);
    }
    private void setSpeaking(){
        listening=false;
        speaking=true;
        silkyAnimation.stop();
        silkyAnimation.start("speak");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                silkyAnimation.start("nocommand");
                listening=false;
                speaking=false;
            }
        },10000);
    }
    private void positionCheck(BDLocation bdLocation){
        if(Zijing.isTrigger(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()))&&!speaking){
            setSpeaking();
            Toast.makeText(getApplicationContext(), "进入紫荆范围",Toast.LENGTH_SHORT).show();
        }
        else if(Liujiao.isTrigger(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()))&&!speaking){
            setSpeaking();
            Toast.makeText(getApplicationContext(), "进入六教范围",Toast.LENGTH_SHORT).show();
        }
        else if(Zhulou.isTrigger(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()))&&!speaking){
            setSpeaking();
            Toast.makeText(getApplicationContext(), "进入主楼范围",Toast.LENGTH_SHORT).show();
        }
        else if(Zhulouqian.isTrigger(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()))&&!speaking){
            setSpeaking();
            Toast.makeText(getApplicationContext(), "进入主楼前范围",Toast.LENGTH_SHORT).show();
            mTts.startSpeaking(inZhulouqian, mSynListener);
        }
        else if(Jianguanbaogaoting.isTrigger(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()))&&!speaking){
            setSpeaking();
            Toast.makeText(getApplicationContext(), "进入建馆报告厅范围",Toast.LENGTH_SHORT).show();
            mTts.startSpeaking(inJianguanBaogaoting, mSynListener);
        }
        else if(Jianguan.isTrigger(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()))&&!speaking){
            setSpeaking();
            Toast.makeText(getApplicationContext(), "进入建馆范围",Toast.LENGTH_SHORT).show();
            mTts.startSpeaking(inJinaguan, mSynListener);
        }
        else if(Yibo.isTrigger(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()))&&!speaking){
            setSpeaking();
            Toast.makeText(getApplicationContext(), "进入艺术博物馆范围",Toast.LENGTH_SHORT).show();
            mTts.startSpeaking(inYibo, mSynListener);
        }
        else if(Meiyuan.isTrigger(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()))&&!speaking){
            setSpeaking();
            Toast.makeText(getApplicationContext(), "进入美院范围",Toast.LENGTH_SHORT).show();
            mTts.startSpeaking(inZhuanwan, mSynListener);
        }
    }

    private void setIatParam(String filename) {
        // 清空参数
        mIatD.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        mIatD.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        mIatD.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置语言
        mIatD.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIatD.setParameter(SpeechConstant.ACCENT, "mandarin");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理vixy
        mIatD.setParameter(SpeechConstant.VAD_BOS, "4000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIatD.setParameter(SpeechConstant.VAD_EOS, "2000");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIatD.setParameter(SpeechConstant.ASR_PTT, "1");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIatD.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mIatD.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/MyApplication/" + filename + ".wav");
        mTts.setParameter(SpeechConstant.PARAMS, null);

        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        mTts.setParameter(SpeechConstant.VOICE_NAME, "");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
    }

    public static String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);
            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }
}
