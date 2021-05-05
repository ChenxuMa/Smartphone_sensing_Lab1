package com.example.example4;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class fragment extends Activity  implements SensorEventListener {
    private WifiManager wifiManager;
    private List wifi_name_list = new ArrayList<>();
    private TextView motion_result;
    private TextView location_result;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float aX;
    private float aY;
    private float aZ;
    private Button start;
    private Button close_window;
    private Button stop;
    private List XValue=new ArrayList<>();
    private List YValue=new ArrayList<>();
    private List ZValue=new ArrayList<>();
    private List Motion_sumlist=new ArrayList<>();
    private float XAverage=0;
    private float YAverage=0;
    private float ZAverage=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        wifi_name_list.add("92:9a:4a:25:f5:b6");
        wifi_name_list.add("28:d1:27:de:d1:52");
        wifi_name_list.add("64:6e:97:83:fc:b3");
        wifi_name_list.add("c4:06:83:45:89:a0");
        wifi_name_list.add("5c:b0:66:e2:17:17");
        wifi_name_list.add("c4:06:83:45:89:a5");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        motion_result = (TextView) findViewById(R.id.motion_result);
        location_result = (TextView) findViewById(R.id.location_result);
        motion_result.setText("");
        location_result.setText("");
        start = (Button) findViewById(R.id.start_button_in_fragment);
        stop=(Button) findViewById(R.id.stop_in_fragment);
        close_window=(Button) findViewById(R.id.close_in_fragment);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                motion_result.setText("Detecting....");
                messageHandler.postDelayed(task, 10);
                messageHandler.postDelayed(task1,10);
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageHandler.removeCallbacks(task);
                messageHandler.removeCallbacks(task1);
                motion_result.setText("");
                location_result.setText("");
            }
        });
        close_window.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private final Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    stop();
                    break;
                case 1:
                    trigger();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            messageHandler.sendEmptyMessage(1);
            messageHandler.sendEmptyMessageDelayed(0, 2 * 1000);
            messageHandler.postDelayed(this, 1*1000);

        }
    };
    public Runnable task1 =new Runnable(){

        @Override
        public void run() {

            scanwifi();

            messageHandler.postDelayed(this,4*1000);
        }
    };
    public void trigger(){
        //mHandler.postDelayed(task,1000);
        //System.out.println("------------sensorManager---------------------");
        //scanwifi();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // if the default accelerometer exists
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // set accelerometer
            accelerometer = sensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else {

        }
        sensorManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // get the the x,y,z values of the accelerometer
        aX = sensorEvent.values[0];
        aY = sensorEvent.values[1];
        aZ = sensorEvent.values[2];
        XValue.add(aX);
        YValue.add(aY);
        ZValue.add(aZ);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    public void stop() {
        sensorManager.unregisterListener(fragment.this);
        predict_motion(XValue,YValue,ZValue);
    }

    private void predict_motion(List xValue, List yValue, List zValue) {
        List<KNN.Data> test_dataset = null;
        float X_sum=0;
        float Y_sum=0;
        float Z_sum=0;
        for (int i = 0; i< xValue.size(); i++){
            X_sum+=(float) xValue.get(i);
        }
        for (int j = 0; j< yValue.size(); j++){
            Y_sum+=(float) yValue.get(j);
        }
        for (int k = 0; k< zValue.size(); k++){
            Z_sum+=(float) zValue.get(k);
        }
        Motion_sumlist.add(X_sum);
        Motion_sumlist.add(Y_sum);
        Motion_sumlist.add(Z_sum);
        XAverage=((float)Motion_sumlist.get(0))/ xValue.size();
        YAverage=((float)Motion_sumlist.get(1))/ yValue.size();
        ZAverage=((float)Motion_sumlist.get(2))/ zValue.size();
        XValue.clear();
        YValue.clear();
        ZValue.clear();
        Motion_sumlist.clear();
        KNN.Data data=new KNN.Data();
        data.setX(XAverage);
        data.setY(YAverage);
        data.setZ(ZAverage);
        int k = 2;
        KNN knn1 = null;
        try {
            knn1 = new KNN("motion_dataset.txt", this.getApplicationContext());
            test_dataset = knn1.dataset;


        } catch (IOException e) {
            e.printStackTrace();
        }
        if(knn1.knn(data, test_dataset, k) == 1){
            motion_result.setText("Still");
        }else if(knn1.knn(data, test_dataset, k) == 2){
            motion_result.setText("Walking");
        }else{
            motion_result.setText("Jump");
        }
    }

    public void scanwifi() {
        Map<String, Integer> result_list=new HashMap<String, Integer>();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        List<ScanResult> scanResults = wifiManager.getScanResults();
        predict_wifi(scanResults);
    }

    private void predict_wifi(List<ScanResult> scanResults) {
        int k=3;
        List mac_list=new ArrayList<>();
        List signal_level=new ArrayList<>();
        List<Data> wifi_dataset=null;
        KNNP knn2=null;
        List<Data> testDataSet = null;
        try{
            knn2=new KNNP("wifi_data_new.txt", this.getApplicationContext());
            wifi_dataset=knn2.wifi_dataset;
        } catch (IOException e) {
            e.printStackTrace();

        }


        for (ScanResult scanResult : scanResults) {
            if (wifi_name_list.contains(scanResult.BSSID)) {

                mac_list.add(scanResult.BSSID);
                signal_level.add(scanResult.level);



            }
        }
        String[] mac=new String[mac_list.size()];
        Integer[] text_list=new Integer[wifi_name_list.size()];
        List scan_result_list=new ArrayList<>();
        for (int i=0;i<mac_list.size();i++){
            mac[i]= (String) mac_list.get(i);
        }
        for(int i=0;i<wifi_name_list.size();i++){
            for (int j=0;j<mac.length;j++){
                if(!wifi_name_list.get(i).equals(mac[j])){
                    text_list[i]=0;

                }
                else if(wifi_name_list.get(i).equals(mac[j])){
                    text_list[i]= (int)signal_level.get(j);

                    break;
                }
            }
        }



        Data wifi_data=new Data();
        wifi_data.setWIFI1((double)text_list[0]);
        wifi_data.setWIFI2((double)text_list[1]);
        wifi_data.setWIFI3((double)text_list[2]);
        wifi_data.setWIFI4((double)text_list[3]);
        wifi_data.setWIFI5((double)text_list[4]);
        wifi_data.setWIFI6((double)text_list[5]);
        scan_result_list.add(wifi_data);




        if(knn2.knn(wifi_data, wifi_dataset, k)==1){
            location_result.setText("687");
            scan_result_list.add(1);
        }else if(knn2.knn(wifi_data, wifi_dataset, k)==2){
            location_result.setText("689");
            scan_result_list.add(2);
        }else if(knn2.knn(wifi_data, wifi_dataset, k)==3){
            location_result.setText("691");
            scan_result_list.add(3);
        }else if(knn2.knn(wifi_data, wifi_dataset, k)==4){
            location_result.setText("693");
            scan_result_list.add(4);
        }else if(knn2.knn(wifi_data, wifi_dataset, k)==5){
            location_result.setText("695");
            scan_result_list.add(5);
        }else if(knn2.knn(wifi_data, wifi_dataset, k)==6){
            location_result.setText("697");
            scan_result_list.add(6);
        }else if(knn2.knn(wifi_data, wifi_dataset, k)==7) {
            location_result.setText("699");
            scan_result_list.add(7);
        }else if(knn2.knn(wifi_data, wifi_dataset, k)==8) {
            location_result.setText("701");
            scan_result_list.add(8);
        }else if(knn2.knn(wifi_data, wifi_dataset, k)==9) {
            location_result.setText("703");
            scan_result_list.add(9);
        }else if(knn2.knn(wifi_data, wifi_dataset, k)==10) {
            location_result.setText("705");
            scan_result_list.add(10);
        }else if(knn2.knn(wifi_data, wifi_dataset, k)==11) {
            location_result.setText("elevator");
            scan_result_list.add(11);
        }
        //System.out.println(scan_result_list);
        //test_accuracy(wifi_data,wifi_dataset, knn2);
        //test_accuracy(scan_result_list, knn2);



    }
    /*
    private void test_accuracy(List scan_result_list, KNNP knn2){
        String text="";
        List<Data> testDataSet=null;
        //System.out.println(scan_result_list);
        try{
            testDataSet=knn2.initDataSet("wifi_testdata_random_revise",this.getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(knn2.knn((Data) scan_result_list.get(0), testDataSet, 3)!=(int)scan_result_list.get(1)){
            ++error_count;
        }
        //System.out.println(testDataSet);
        text="错误率：" + (double)error_count / testDataSet.size() + "%"+"\r\n";
        System.out.println("错误率：" + (double)error_count / testDataSet.size() + "%");
        try{
            fos=openFileOutput("WIFIaccuracy", MODE_APPEND);
            fos.write(text.getBytes());
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try{
                fos.close();
                //Toast.makeText(SCAN.this, "Saved successfull", Toast.LENGTH_LONG).show();
            } catch (IOException e){
                e.printStackTrace();
            }

        }

     */
}
