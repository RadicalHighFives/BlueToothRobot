package com.example.blerobot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.view.View.OnClickListener;

//implements View.OnClickListener
public class ControlActivity extends AppCompatActivity  {
    private static int leftIncrementer = 0;
    private static int rightIncrementer = 0;
    private static int motorLeftSpeed = 0;
    private static int motorRightSpeed = 0;
    private static int leftModSpeed = 0;
    private static int rightModSpeed = 0;

    // Objects to access the layout items for Tach, Buttons, and Seek bars
    private static TextView mTachLeftText;
    private static TextView mTachRightText;
    private static SeekBar mSpeedLeftSeekBar;
    private static SeekBar mSpeedRightSeekBar;

    private static Button mSpeedLeftButton;
    private static Button mSpeedRightButton;

    private static Button mSpeedLeftSlowButton;
    private static Button mSpeedRightSlowButton;

    private static Switch mEnableLeftSwitch;
    private static Switch mEnableRightSwitch;

    private static Button mForwardButton;

    private static TextView mLeftMotorSpeedometer;
    private static TextView mRightMotorSpeedometer;


    // This tag is used for debug messages
    private static final String TAG = ControlActivity.class.getSimpleName();

    private static String mDeviceAddress;
    private static PSoCBleRobotService mPSoCBleRobotService;

    /**
     * This manages the lifecycle of the BLE service.
     * When the service starts we get the service object, initialize the service, and connect.
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.i(TAG, "onServiceConnected");
            mPSoCBleRobotService = ((PSoCBleRobotService.LocalBinder) service).getService();
            if (!mPSoCBleRobotService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the car database upon successful start-up initialization.
            mPSoCBleRobotService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mPSoCBleRobotService = null;
        }
    };


//    @Override
//    public void onClick(View view){
//        int speed=30;
//
//        switch (view.getId()) {
//            case R.id.speed_left:
//                speed = scaleSpeed(speed);
//                //mPSoCBleRobotService.setMotorSpeed(PSoCBleRobotService.Motor.LEFT, speed);
//                Log.d(TAG,"Left Pressed"+speed);
//                break;
//            case R.id.speed_right:
//                speed = scaleSpeed(speed);
//                //mPSoCBleRobotService.setMotorSpeed(PSoCBleRobotService.Motor.RIGHT, speed);
//                Log.d(TAG,"Right Pressed"+speed);
//                break;
//        }
//
//    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        // Assign the various layout objects to the appropriate variables
        mTachLeftText = (TextView) findViewById(R.id.tach_left);
        mTachRightText = (TextView) findViewById(R.id.tach_right);
        mEnableLeftSwitch = (Switch) findViewById(R.id.enable_left);
        mEnableRightSwitch = (Switch) findViewById(R.id.enable_right);
//        mSpeedLeftSeekBar = (SeekBar) findViewById(R.id.speed_left);
//        mSpeedRightSeekBar = (SeekBar) findViewById(R.id.speed_right);
        mLeftMotorSpeedometer = (TextView) findViewById(R.id.left_motor_mph);
        mRightMotorSpeedometer = (TextView) findViewById(R.id.right_motor_mph);

        mSpeedLeftButton = (Button) findViewById(R.id.speed_btn_Left);
        mSpeedLeftSlowButton = (Button) findViewById(R.id.slow_btn_Left);

        mSpeedRightButton = (Button) findViewById(R.id.speed_btn_Right);
        mSpeedRightSlowButton = (Button) findViewById(R.id.slow_btn_Right);

//        mForwardButton = (Button) findViewById(R.id.forward);

        //mSpeedLeftSeekBar.setOnClickListener(this);
        //mSpeedRightSeekBar.setOnClickListener(this);

        final Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra(ScanActivity.EXTRAS_BLE_ADDRESS);

        // Bind to the BLE service
        Log.i(TAG, "Binding Service");
        Intent RobotServiceIntent = new Intent(this, PSoCBleRobotService.class);
        bindService(RobotServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        /* This will be called when the left motor enable switch is changed */
        mEnableLeftSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableMotorSwitch(isChecked, PSoCBleRobotService.Motor.LEFT);
            }
        });

        /* This will be called when the right motor enable switch is changed */
        mEnableRightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableMotorSwitch(isChecked, PSoCBleRobotService.Motor.RIGHT);
            }
        });

        // ===== Forward
//        mForwardButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                leftIncrementer = leftIncrementer+1;
//                rightIncrementer = rightIncrementer+1;
//                motorLeftSpeed = scaleSpeed(leftIncrementer);
//                motorRightSpeed = scaleSpeed(rightIncrementer);
//                Log.d(TAG," "+ motorLeftSpeed + " "+ motorRightSpeed);
//                mPSoCBleRobotService.setForwardMotorSpeed(PSoCBleRobotService.Motor.FORWARD, motorLeftSpeed, motorRightSpeed);
//
//            }
//
//        });

        // ===== LEFT speed Button
        mSpeedLeftButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){

                leftIncrementer = leftIncrementer+10;
                //mSpeedLeftSeekBar.setProgress(leftIncrementer);
                Log.d(TAG,"Left Speed Up " + leftIncrementer);

//                mLeftMotorSpedometer.setText(String.format("%d", int(leftIncrementer%10));
                motorLeftSpeed = scaleSpeed(leftIncrementer);
//                leftModSpeed = (leftIncrementer%10);
                Log.d(TAG, "Mod left+ "+motorLeftSpeed);
                mLeftMotorSpeedometer.setText(String.format("%d", motorLeftSpeed));
                mPSoCBleRobotService.setMotorSpeed(PSoCBleRobotService.Motor.LEFT, motorLeftSpeed);
            }
        });

        // ===== LEFT Slow/Reverse Button
        mSpeedLeftSlowButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                leftIncrementer = leftIncrementer-10;
                //mSpeedLeftSeekBar.setProgress(leftIncrementer);
                Log.d(TAG,"Left Slow Down "+leftIncrementer);

                //mLeftMotorSpedometer.setText(String.format("%d", leftIncrementer));
                motorLeftSpeed = scaleSpeed(leftIncrementer);
//                leftModSpeed = (leftIncrementer % 10);
                Log.d(TAG, "Mod left- "+motorLeftSpeed);
                mLeftMotorSpeedometer.setText(String.format("%d", motorLeftSpeed));
                mPSoCBleRobotService.setMotorSpeed(PSoCBleRobotService.Motor.LEFT, motorLeftSpeed);
            }
        });

        // ===== Right Speed Button
        mSpeedRightButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                rightIncrementer = rightIncrementer + 10;
                //mSpeedRightSeekBar.setProgress(rightIncrementer);
                Log.d(TAG,"Right Speed Up "+rightIncrementer);

                motorRightSpeed = scaleSpeed(rightIncrementer);
//                rightModSpeed = (rightIncrementer%10);
                Log.d(TAG, "Mod right+ "+motorRightSpeed);
                mRightMotorSpeedometer.setText(String.format("%d",motorRightSpeed));
                mPSoCBleRobotService.setMotorSpeed(PSoCBleRobotService.Motor.RIGHT, motorRightSpeed);
            }
        });

        // ===== Right Slow Button
        mSpeedRightSlowButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                rightIncrementer = rightIncrementer-10;
                //mSpeedRightSeekBar.setProgress(rightIncrementer);
                Log.d(TAG,"Right Slow Down "+rightIncrementer);
//                mRightMotorSpedometer.setText(String.format("%d", rightIncrementer));

                motorRightSpeed = scaleSpeed(rightIncrementer);
                rightModSpeed = (rightIncrementer%10);
                Log.d(TAG, "Mod right+ "+motorRightSpeed);
                mRightMotorSpeedometer.setText(String.format("%d",rightModSpeed));
                mPSoCBleRobotService.setMotorSpeed(PSoCBleRobotService.Motor.RIGHT, motorRightSpeed);

            }
        });


//        /* This will be called when the left speed seekbar is moved */
//        mSpeedLeftSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//
//            public void onProgressChanged(SeekBar seekBar, int speed, boolean fromUser) {
//                /* Scale the speed from what the seek bar provides to what the PSoC FW expects */
//                speed = scaleSpeed(speed);
//                mPSoCBleRobotService.setMotorSpeed(PSoCBleRobotService.Motor.LEFT, speed);
//                Log.d(TAG, "Left Speed Change to:" + speed);
//            }
//        });
//
//        /* This will be called when the right speed seekbar is moved */
//        mSpeedRightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//
//            public void onProgressChanged(SeekBar seekBar, int speed, boolean fromUser) {
//                /* Scale the speed from what the seek bar provides to what the PSoC FW expects */
//                speed = scaleSpeed(speed);
//                mPSoCBleRobotService.setMotorSpeed(PSoCBleRobotService.Motor.RIGHT, speed);
//                Log.d(TAG, "Right Speed Change to:" + speed);
//            }
//        });
    } /* End of onCreate method */

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mRobotUpdateReceiver, makeRobotUpdateIntentFilter());
        if (mPSoCBleRobotService != null) {
            final boolean result = mPSoCBleRobotService.connect(mDeviceAddress);
            Log.i(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mRobotUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mPSoCBleRobotService = null;
    }

    /**
     * Scale the speed read from the slider (0 to 20) to
     * what the car object expects (-100 to +100).
     *
     * @param speed Input speed from the slider
     * @return scaled value of the speed
     */


    private int scaleSpeed(int speed) {
        final int SCALE = 10;
        final int OFFSET = 100;

        return ((speed * SCALE) - OFFSET);
    }

    /**
     * Enable or disable the left/right motor
     *
     * @param isChecked used to enable/disable motor
     * @param motor is the motor to enable/disable (left or right)
     */
    private void enableMotorSwitch(boolean isChecked, PSoCBleRobotService.Motor motor) {
        if (isChecked) { // Turn on the specified motor
            mPSoCBleRobotService.setMotorState(motor, true);
            Log.d(TAG, (motor == PSoCBleRobotService.Motor.LEFT ? "Left" : "Right") + " Motor On");
        } else { // turn off the specified motor
            mPSoCBleRobotService.setMotorState(motor, false);
            mPSoCBleRobotService.setMotorSpeed(motor, 0); // Force motor off

//            if(motor == PSoCBleRobotService.Motor.LEFT) {
////                mSpeedLeftSeekBar.setProgress(10); // Move slider to middle position
//                mLeftMotorSpeedometer.setText("10");
//            } else {
////                mSpeedRightSeekBar.setProgress(10); // Move slider to middle position
//                mRightMotorSpeedometer.setText("10");
//
//            }
            Log.d(TAG, (motor == PSoCBleRobotService.Motor.LEFT ? "Left" : "Right") + " Motor Off");
        }

    }

    /**
     * Handle broadcasts from the Car service object. The events are:
     * ACTION_CONNECTED: connected to the car.
     * ACTION_DISCONNECTED: disconnected from the car.
     * ACTION_DATA_AVAILABLE: received data from the car.  This can be a result of a read
     * or notify operation.
     */
    private final BroadcastReceiver mRobotUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action) {
                case PSoCBleRobotService.ACTION_CONNECTED:
                    // No need to do anything here. Service discovery is started by the service.
                    break;
                case PSoCBleRobotService.ACTION_DISCONNECTED:
                    mPSoCBleRobotService.close();
                    break;
                case PSoCBleRobotService.ACTION_DATA_AVAILABLE:
                    // This is called after a Notify completes
                    mTachLeftText.setText(String.format("%d", PSoCBleRobotService.getTach(PSoCBleRobotService.Motor.LEFT)));
                    mTachRightText.setText(String.format("%d", PSoCBleRobotService.getTach(PSoCBleRobotService.Motor.RIGHT)));
                    break;
            }
        }
    };

    /**
     * This sets up the filter for broadcasts that we want to be notified of.
     * This needs to match the broadcast receiver cases.
     *
     * @return intentFilter
     */
    private static IntentFilter makeRobotUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PSoCBleRobotService.ACTION_CONNECTED);
        intentFilter.addAction(PSoCBleRobotService.ACTION_DISCONNECTED);
        intentFilter.addAction(PSoCBleRobotService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
