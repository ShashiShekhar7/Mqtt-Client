package com.maxwellindia.mqtt;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";

    TextView textView;
    String clientId;
    MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnPublish = findViewById(R.id.button_publish);
        Button btnSubscibe = findViewById(R.id.button_subscribe);
        textView = findViewById(R.id.textView);


        clientId = MqttClient.generateClientId();
        client  =
                new MqttAndroidClient(MainActivity.this, "tcp://122.180.250.232:1883",
                        clientId);

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    IMqttToken token = client.connect();
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // We are connected
                            Log.d(TAG, "onSuccess");

//                    String topic = "test";
                            int qos = 0;
                            try {
                                IMqttToken subToken = client.subscribe("data", qos);
                                subToken.setActionCallback(new IMqttActionListener() {
                                    @Override
                                    public void onSuccess(IMqttToken asyncActionToken) {
                                        Toast.makeText(MainActivity.this, "Topic Subscribed", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(IMqttToken asyncActionToken,
                                                          Throwable exception) {
                                        // The subscription could not be performed, maybe the user was not
                                        // authorized to subscribe on the specified topic e.g. using wildcards
                                        Toast.makeText(MainActivity.this, "Topic Subscription Failed", Toast.LENGTH_SHORT).show();


                                    }
                                });
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }

                            String topic = "data";
                            String payload = "111111";
                            byte[] encodedPayload = new byte[0];
                            try {
                                encodedPayload = payload.getBytes("UTF-8");
                                MqttMessage message = new MqttMessage(encodedPayload);
                                client.publish(topic, message);
                            } catch (UnsupportedEncodingException | MqttException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            // Something went wrong e.g. connection timeout or firewall problems
                            Log.d(TAG, "onFailure");

                            Toast.makeText(MainActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();


                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }

                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        Toast.makeText(MainActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {

                        String data = message.toString();

//                try {
//
//                    JSONObject obj = new JSONObject(data);
//
//                    Log.d("My App", obj.toString());
//
//                } catch (Throwable t) {
//                    Log.e("My App", "Could not parse malformed JSON: \"" + data + "\"");
//                }

                        textView.setText(message.toString());

                        Log.d(TAG, "Message from Server:" + data);
//                Toast.makeText(MainActivity.this, message.toString(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });


            }
        });

        btnSubscibe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



//        client.setCallback(new MqttCallback() {
//            @Override
//            public void connectionLost(Throwable cause) {
//                Toast.makeText(MainActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void messageArrived(String topic, MqttMessage message) throws Exception {
//
//                String data = message.toString();
//
////                try {
////
////                    JSONObject obj = new JSONObject(data);
////
////                    Log.d("My App", obj.toString());
////
////                } catch (Throwable t) {
////                    Log.e("My App", "Could not parse malformed JSON: \"" + data + "\"");
////                }
//
//                textView.setText(message.toString());
//
//                Log.d(TAG, "Message from Server:" + data);
////                Toast.makeText(MainActivity.this, message.toString(), Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {
//
//            }
//        });

    }

}
