package com.example.wifiKettle

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import kotlinx.android.synthetic.main.activity_main.*

import android.widget.*
import android.widget.TextView
import android.widget.EditText
import android.app.TimePickerDialog
import android.net.wifi.WifiConfiguration
import android.text.InputType
import java.util.*
import com.wnafee.vector.MorphButton.OnStateChangedListener
import android.widget.Toast

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Timer
import kotlin.concurrent.schedule
import android.content.IntentFilter
import androidx.annotation.NonNull
import android.content.Context.WIFI_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.net.wifi.WifiManager
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import kotlinx.android.synthetic.main.settings_activity.*


class MainActivity : AppCompatActivity() {


    private var alarm_Hour=0
    private var alarm_Minut=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var eText = findViewById<View>(R.id.editText2) as EditText
        eText.setInputType(InputType.TYPE_NULL)
        eText.setOnClickListener(View.OnClickListener {
            val cldr = Calendar.getInstance()
            val hour = cldr.get(Calendar.HOUR_OF_DAY)
            val minutes = cldr.get(Calendar.MINUTE)
            // time picker dialog
            var picker = TimePickerDialog(this@MainActivity,
                TimePickerDialog.OnTimeSetListener { tp, sHour, sMinute -> eText.setText("$sHour:$sMinute")

                    alarm_Hour=sHour
                    alarm_Minut= sMinute
                },
                hour,
                minutes,
                true
            )
            picker.show()

        })

        //var btnGet = findViewById<View>(R.id.button) as Button
        //btnGet.setOnClickListener(View.OnClickListener { tvw.setText("Selected Time: " + eText.getText()) })


        val MyMorphButton = tONOFF

        MyMorphButton.setOnStateChangedListener(OnStateChangedListener { changedTo, isAnimating ->
            // Do something here
            //Toast.makeText(this@MainActivity, "Changed to: $changedTo", Toast.LENGTH_SHORT).show()
            var url = ""
            if(changedTo.toString()== "END"){

                Toast.makeText(this@MainActivity, "КИПИТИМ", Toast.LENGTH_SHORT).show()

                url = "http://192.168.4.1/settemp?kipitim=H"
            }
            else{
                Toast.makeText(this@MainActivity, "ВЫКЛЮЧАЕМ", Toast.LENGTH_SHORT).show()
                url = "http://192.168.4.1/settemp?kipitim=L"
            }
            FuelManager.instance.baseHeaders = mapOf(
                "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0"
            )

            url.httpGet().responseString { _, _, result ->
                result.fold(
                    { str ->
                        // textView2.text=str
                        Toast.makeText(this@MainActivity, str, Toast.LENGTH_SHORT).show()
                    },
                    { fuelError ->
                        Toast.makeText(this@MainActivity, fuelError.toString(), Toast.LENGTH_SHORT).show()
                    })
            }
        })
        val MyMorphButton2 = tWARM

        MyMorphButton2.setOnStateChangedListener(OnStateChangedListener { changedTo, isAnimating ->
            // Do something here
            //Toast.makeText(this@MainActivity, "Changed to: $changedTo", Toast.LENGTH_SHORT).show()
            var url = ""
            if(changedTo.toString()== "END"){
                Toast.makeText(this@MainActivity, "ПОДДЕРЖИВАЕМ ГОРЯЧИМ", Toast.LENGTH_SHORT).show()
                url = "http://192.168.4.1/settemp?hotting=H&settemp=80"
            }
            else{
                Toast.makeText(this@MainActivity, "ВЫКЛЮЧАЕМ", Toast.LENGTH_SHORT).show()
                url = "http://192.168.4.1/settemp?hotting=L"
            }
            FuelManager.instance.baseHeaders = mapOf(
                "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0"
            )

            url.httpGet().responseString { _, _, result ->
                result.fold(
                    { str ->
                        // textView2.text=str
                    },
                    { fuelError ->
                        //textView2.text="got error: $fuelError"
                    })
            }
        })

        val MyMorphButton3 = tWARM2

        MyMorphButton3.setOnStateChangedListener(OnStateChangedListener { changedTo, isAnimating ->
            // Do something here
            //Toast.makeText(this@MainActivity, "Changed to: $changedTo", Toast.LENGTH_SHORT).show()
            var url = ""
            if(changedTo.toString()== "END"){
                Toast.makeText(this@MainActivity, "ПОДДЕРЖИВАЕМ ТЕПЛЫМ", Toast.LENGTH_SHORT).show()
                url = "http://192.168.4.1/settemp?hotting=H&settemp=50"
            }
            else{
                Toast.makeText(this@MainActivity, "ВЫКЛЮЧАЕМ", Toast.LENGTH_SHORT).show()
                url = "http://192.168.4.1/settemp?hotting=L"
            }
            FuelManager.instance.baseHeaders = mapOf(
                "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0"
            )

            url.httpGet().responseString { _, _, result ->
                result.fold(
                    { str ->
                        // textView2.text=str
                    },
                    { fuelError ->
                        //textView2.text="got error: $fuelError"
                    })
            }
        })

        connectToWPAWiFi("Naukograd_kettle","")

        /*
        var stimer = Timer("update_actions_kettle", true).schedule(3000) {
            wifiKettle_update_actions()
        }

        Timer().schedule(object : TimerTask() {
            override fun run() {
                ()
            }
        }, 5000)
*/
        val timer = Timer()
        //Set the schedule function
        timer.scheduleAtFixedRate(
            object : TimerTask() {

                override fun run() {
                    wifiKettle_update_actions()// Magic here
                }
            },
            0, 30000
        )   // 1000 Millisecond  = 1 second


        val almSw= alarm_switch
        almSw.setOnCheckedChangeListener { _, on ->
            val almSpin=alarm_spin
            val alSelID=almSpin.selectedItemId
            var almSetState=""
            if (alSelID.toInt()==0){
                almSetState="kipitim"
            }
            if (alSelID.toInt()==1){
                almSetState="keep_hot"
            }
            if (alSelID.toInt()==2){
                almSetState="keep_warm"
            }
            var url = ""
            if(on){
                Toast.makeText(this@MainActivity, "ТАЙМЕР ВКЛ  $alarm_Hour$alarm_Minut", Toast.LENGTH_SHORT).show()
                url = "http://192.168.4.1/settime?setalarm=H&hour=$alarm_Hour&minut=$alarm_Minut&alarm_hot_kip=$almSetState"
            }
            else{
                Toast.makeText(this@MainActivity, "ТАЙМЕР ВЫКЛ", Toast.LENGTH_SHORT).show()
                url = "http://192.168.4.1/settime?setalarm=L"

            }

            FuelManager.instance.baseHeaders = mapOf(
                "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0"
            )

            url.httpGet().responseString { _, _, result ->
                result.fold(
                    { str ->
                        // textView2.text=str
                    },
                    { fuelError ->
                        //textView2.text="got error: $fuelError"
                    })
            }
        }







        var night_led_Sw= night_led

        night_led_Sw.setOnCheckedChangeListener { cb, on ->

            var url = ""
            if(on){
                Toast.makeText(this@MainActivity, "ночнник ВКЛ  ", Toast.LENGTH_SHORT).show()
                url = "http://192.168.4.1/nightLED?ledMode_night_light=H"
            }
            else{
                Toast.makeText(this@MainActivity, "ночнник ВЫКЛ", Toast.LENGTH_SHORT).show()
                url = "http://192.168.4.1/nightLED?ledMode_night_light=L"

            }

            FuelManager.instance.baseHeaders = mapOf(
                "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0"
            )

            url.httpGet().responseString { _, _, result ->
                result.fold(
                    { str ->
                        // textView2.text=str
                    },
                    { fuelError ->
                        //textView2.text="got error: $fuelError"
                    })
            }
        }

    }

    fun wifiKettle_update_actions(){
       // Toast.makeText(this@MainActivity, "UPDATE ACTIONS", Toast.LENGTH_SHORT).show()
        var sdf = SimpleDateFormat("yyyy")
        val nyear = sdf.format(Date())
        sdf = SimpleDateFormat("MM")
        val nmonth=sdf.format(Date())
        sdf=SimpleDateFormat("dd")
        val nday=sdf.format(Date())
        sdf=SimpleDateFormat("HH")
        val nhour=sdf.format(Date())
        sdf=SimpleDateFormat("mm")
        val nminut=sdf.format(Date())
        sdf=SimpleDateFormat("ss")
        val nsec=sdf.format(Date())

        var url = "http://192.168.4.1/settime?setdate=H&year=$nyear&month=$nmonth&day=$nday&hour=$nhour&minut=$nminut&second=$nsec"

        FuelManager.instance.baseHeaders = mapOf(
            "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0"
        )

        url.httpGet().responseString { _, _, result ->
            result.fold(
                { str ->
                    // textView2.text=str
                },
                { fuelError ->
                    //textView2.text="got error: $fuelError"
                })
        }

    }








    fun connectToWPAWiFi(ssid:String,pass:String){
        if(isConnectedTo(ssid)){ //see if we are already connected to the given ssid
            Toast.makeText(this@MainActivity, "Connected to$ssid", Toast.LENGTH_SHORT)
            return
        }
        val wm:WifiManager= applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var wifiConfig=getWiFiConfig(ssid)
        if(wifiConfig==null){//if the given ssid is not present in the WiFiConfig, create a config for it
            createWPAProfile(ssid,pass)
            wifiConfig=getWiFiConfig(ssid)

        }
        wm.disconnect()
        wm.enableNetwork(wifiConfig!!.networkId,true)
        wm.reconnect()
        //Log.d(TAG,"intiated connection to SSID"+ssid);
    }
    fun isConnectedTo(ssid: String):Boolean{
        val wm:WifiManager= applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        /*Check enable wifi, if it is disable -> enable it*/
        if(!wm.isWifiEnabled){
            wm.isWifiEnabled = true
            return false
        }

        if(wm.connectionInfo.ssid == ssid) {
            return true
        }
        return false
    }

    fun getWiFiConfig(ssid: String): WifiConfiguration? {
        val wm:WifiManager= applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiList=wm.configuredNetworks
        for (item in wifiList){
            if(item.SSID != null && item.SSID.equals(ssid)){
                return item
            }
        }
        return null
    }
    fun createWPAProfile(ssid: String,pass: String){
        //Log.d(TAG,"Saving SSID :"+ssid)
        val conf = WifiConfiguration()
        conf.SSID = ssid
        conf.preSharedKey = pass

        conf.status = WifiConfiguration.Status.ENABLED;
        conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);

        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);

        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

        conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);


        val wm:WifiManager= applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wm.addNetwork(conf)
        //Log.d(TAG,"saved SSID to WiFiManger")
    }

    class WiFiChngBrdRcr : BroadcastReceiver(){ // shows a toast message to the user when device is connected to a AP
        private val TAG = "WiFiChngBrdRcr"
        override fun onReceive(context: Context, intent: Intent) {
            val networkInfo=intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
            if(networkInfo.state == NetworkInfo.State.CONNECTED){
                val bssid=intent.getStringExtra(WifiManager.EXTRA_BSSID)
                //Log.d(TAG, "Connected to BSSID:"+bssid)
                val ssid=intent.getParcelableExtra<WifiInfo>(WifiManager.EXTRA_WIFI_INFO).ssid
                //val log="Connected to SSID:"+ssid
                //Log.d(TAG,"Connected to SSID:"+ssid)
                //Toast.makeText(context, log, Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun start_settings(view: View){
        val intent = Intent(this, SettingsActivity::class.java)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }


}
