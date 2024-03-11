package com.example.wifiKettle

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.settings_activity.*
import org.json.JSONObject
import android.widget.AdapterView.OnItemSelectedListener
import java.util.*


class SettingsActivity : AppCompatActivity() {
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        /*supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
         */
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var url = "http://192.168.4.1/getSettings"
        deSetListeners();

        FuelManager.instance.baseHeaders = mapOf(
            "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0"
        )

        url.httpGet().responseString { _, _, result ->
            result.fold(
                { str ->
                    // textView2.text=str
                    var jObject = JSONObject(str)
                    var ledMode_vskipel = jObject.getInt("ledMode_vskipel")
                    /*when(ledMode_vskipel) {
                        999 -> ledMode_vskipel = 0
                        888 -> ledMode_vskipel = 45
                        889 -> ledMode_vskipel = 46
                        890 -> ledMode_vskipel = 47
                    }
*/
                    var ledMode_heating = jObject.getInt("ledMode_heating")
                    ledMode_heating= modeTOspin(ledMode_heating)
                    /*when(ledMode_heating) {
                        999 -> ledMode_heating = 0
                        888 -> ledMode_heating = 45
                        889 -> ledMode_heating = 46
                        890 -> ledMode_heating = 47
                    }*/
                    var ledMode_hot = jObject.getInt("ledMode_hot")
                    ledMode_hot= modeTOspin(ledMode_hot)
                    /*
                    when(ledMode_hot) {
                        999 -> ledMode_hot = 0
                        888 -> ledMode_hot = 45
                        889 -> ledMode_hot = 46
                        890 -> ledMode_hot = 47
                    }*/

                    var ledMode_warm = jObject.getInt("ledMode_warm")
                    ledMode_warm= modeTOspin(ledMode_warm)
                    /*when(ledMode_warm) {
                        999 -> ledMode_warm = 0
                        888 -> ledMode_warm = 45
                        889 -> ledMode_warm = 46
                        890 -> ledMode_warm = 47
                    }*/

                    var ledMode_cold = jObject.getInt("ledMode_cold")
                    ledMode_cold= modeTOspin(ledMode_cold)
                    /*when(ledMode_cold) {
                        999 -> ledMode_cold = 0
                        888 -> ledMode_cold = 45
                        889 -> ledMode_cold = 46
                        890 -> ledMode_cold = 47
                    }*/

                    var ledMode_night_light = jObject.getInt("ledMode_night_light")
                    ledMode_night_light= modeTOspin(ledMode_night_light)
                    /*when(ledMode_night_light) {
                        999 -> ledMode_night_light = 0
                        888 -> ledMode_night_light = 45
                        889 -> ledMode_night_light = 46
                        890 -> ledMode_night_light = 47
                    }*/

                    val b_night_light_mode_overwrite = jObject.getBoolean("b_night_light_mode_overwrite")

                    deSetListeners();
                    cold_spin.setSelection(ledMode_cold,false)
                    hotting_spin.setSelection(ledMode_heating,false)
                    hot_spin.setSelection(ledMode_hot,false)
                    warm_spin.setSelection(ledMode_warm,false)
                    vskipel_spin.setSelection(ledMode_vskipel,false)
                    night_light_spin.setSelection(ledMode_night_light,false)

                   /* setSpinnerSelectionWithoutCallingListener(cold_spin,ledMode_cold)

                    setSpinnerSelectionWithoutCallingListener(hotting_spin,ledMode_heating)

                    setSpinnerSelectionWithoutCallingListener(hot_spin,ledMode_hot)

                    setSpinnerSelectionWithoutCallingListener(warm_spin,ledMode_warm)

                    setSpinnerSelectionWithoutCallingListener(vskipel_spin,ledMode_vskipel)

                    setSpinnerSelectionWithoutCallingListener(night_light_spin,ledMode_night_light)*/

                    night_over.isChecked=b_night_light_mode_overwrite
                    Toast.makeText(this@SettingsActivity, "vskipel=$ledMode_vskipel heating=$ledMode_heating  hot=$ledMode_hot  warm=$ledMode_warm  cold=$ledMode_cold  NL=$ledMode_night_light bool=$b_night_light_mode_overwrite", Toast.LENGTH_SHORT).show()
                    setListeners()
                },
                { fuelError ->
                    //textView2.text="got error: $fuelError"
                })






            val sButton1 = findViewById(R.id.switch1) as Switch
            val sButton2 = findViewById(R.id.switch2) as Switch
            //Set a CheckedChange Listener for Switch Button
            sButton1.setOnCheckedChangeListener { cb, on ->
                if (on) {
                    //Do something when Switch button is on/checked
                    //tView.text = "Switch is on....."
                    sendGetRequest("1","H")
                } else {
                    //Do something when Switch is off/unchecked
                    //tView.text = "Switch is off....."
                    sendGetRequest("1","L")
                }
            }
            sButton2.setOnCheckedChangeListener { cb, on ->
                if (on) {
                    //Do something when Switch button is on/checked
                    //tView.text = "Switch is on....."
                    sendGetRequest("2","H")
                } else {
                    //Do something when Switch is off/unchecked
                    //tView.text = "Switch is off....."
                    sendGetRequest("2","L")
                }
            }
        }

        setListeners()

    }
    fun modeTOspin(sel:Int):Int{
        when(sel) {
            999 ->  return 0
            888 -> return 45
            889 -> return 46
            890 -> return 47
        }
        return 0
    }

    fun spinTOmode(sel:Long):String{
        when (sel.toInt()) {
            0 ->  return "999"
            45 -> return "888"
            46 -> return "889"
            47 -> return "890"
            else -> return (sel.toInt() + 1).toString()
        }
        return ""
    }

    fun deSetListeners(){
        var noon = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {}

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val night_led_Mode = night_light_spin
        night_led_Mode.onItemSelectedListener =noon

        val vskipel_led_Mode = vskipel_spin
        vskipel_led_Mode.onItemSelectedListener = noon
        val hot_led_Mode = hot_spin
        hot_led_Mode.onItemSelectedListener = noon
        val warm_led_Mode = warm_spin
warm_led_Mode.onItemSelectedListener = noon
        val cold_led_Mode = cold_spin
cold_led_Mode.onItemSelectedListener = noon
        val hotting_led_Mode = hotting_spin
hotting_led_Mode.onItemSelectedListener = noon
        val nght_over = night_over
        nght_over.setOnCheckedChangeListener(null)
    }
    fun setListeners(){

        val night_led_Mode = night_light_spin
        night_led_Mode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                print("onItemSelected position = $position id = $id")
                var nMD_SetState = spinTOmode(id)
                var url = "http://192.168.4.1/lightModes?lm_night_light=$nMD_SetState"


                FuelManager.instance.baseHeaders = mapOf(
                    "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0"
                )

                url.httpGet().responseString { _, _, result ->
                    result.fold(
                        { str ->    },
                        { fuelError ->    })
                }
                Toast.makeText(this@SettingsActivity, "нажали  $nMD_SetState", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val vskipel_led_Mode = vskipel_spin

        vskipel_led_Mode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                print("onItemSelected position = $position id = $id")
                var nMD_SetState = spinTOmode(id)

                var url = "http://192.168.4.1/lightModes?lm_vskipel=$nMD_SetState"


                FuelManager.instance.baseHeaders = mapOf(
                    "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0"
                )

                url.httpGet().responseString { _, _, result ->
                    result.fold(
                        { str ->    },
                        { fuelError ->    })
                }
                Toast.makeText(this@SettingsActivity, "нажали  $nMD_SetState", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {    }
        }


        val hot_led_Mode = hot_spin

        hot_led_Mode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                print("onItemSelected position = $position id = $id")
                var nMD_SetState = spinTOmode(id)

                var url = "http://192.168.4.1/lightModes?lm_hot=$nMD_SetState"


                FuelManager.instance.baseHeaders = mapOf(
                    "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0"
                )

                url.httpGet().responseString { _, _, result ->
                    result.fold(
                        { str ->    },
                        { fuelError ->    })
                }
                Toast.makeText(this@SettingsActivity, "нажали  $nMD_SetState", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        val warm_led_Mode = warm_spin

        warm_led_Mode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                print("onItemSelected position = $position id = $id")
                var nMD_SetState = spinTOmode(id)

                var url = "http://192.168.4.1/lightModes?lm_warm=$nMD_SetState"


                FuelManager.instance.baseHeaders = mapOf(
                    "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0"
                )

                url.httpGet().responseString { _, _, result ->
                    result.fold(
                        { str ->    },
                        { fuelError ->    })
                }
                Toast.makeText(this@SettingsActivity, "нажали  $nMD_SetState", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) { }
        }

        val cold_led_Mode = cold_spin

        cold_led_Mode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                print("onItemSelected position = $position id = $id")
                var nMD_SetState = spinTOmode(id)

                var url = "http://192.168.4.1/lightModes?lm_cold=$nMD_SetState"


                FuelManager.instance.baseHeaders = mapOf(
                    "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0"
                )

                url.httpGet().responseString { _, _, result ->
                    result.fold(
                        { str ->    },
                        { fuelError ->    })
                }
                Toast.makeText(this@SettingsActivity, "нажали  $nMD_SetState", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        val hotting_led_Mode = hotting_spin

        hotting_led_Mode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                print("onItemSelected position = $position id = $id")
                var nMD_SetState = spinTOmode(id)

                var url = "http://192.168.4.1/lightModes?lm_heating=$nMD_SetState"

                FuelManager.instance.baseHeaders = mapOf(
                    "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0"
                )

                url.httpGet().responseString { _, _, result ->
                    result.fold(
                        { str ->           },
                        { fuelError ->      })
                }
                Toast.makeText(this@SettingsActivity, "нажали  $nMD_SetState", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }



        val nght_over = night_over

        nght_over.setOnCheckedChangeListener { buttonView, isChecked ->
            var url = ""
            var checked_md = ""
            if (isChecked) {
                checked_md="H"
            }
            else{
                checked_md="L"
            }
            url = "http://192.168.4.1/lightModes?night_light_over=$checked_md"
            FuelManager.instance.baseHeaders = mapOf(
                "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0"
            )

            url.httpGet().responseString { _, _, result ->
                result.fold(
                    { str ->   },
                    { fuelError ->    })
            }
        }
    }

    fun showDialog(view: View) {
        val builder = ColorPickerDialog.Builder(this)
            .setTitle("ColorPicker")
            .setPreferenceName("Test")
            .setPositiveButton("Ok", ColorEnvelopeListener { envelope, fromUser -> setLayoutColor(envelope) })
            .setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.dismiss() }
            .attachAlphaSlideBar(false) // default is true. If false, do not show the AlphaSlideBar.
        builder.show()
    }
    private fun setLayoutColor(envelope: ColorEnvelope) {


        val linearLayout = findViewById<LinearLayout>(R.id.lay_color1)
        linearLayout.setBackgroundColor(envelope.color)
        val color = envelope.color
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        val paraR=red.toString()
        val paraG=green.toString()
        val paraB=blue.toString()

        var url = "http://192.168.4.1/ws2812?R=$paraR&G=$paraG&B=$paraB"

        FuelManager.instance.baseHeaders = mapOf(
            "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0"
        )

        url.httpGet().responseString { _, _, result ->
            result.fold(
                { str ->    },
                { fuelError ->   })
        }
        //textView.text= "R=$paraR&G=$paraG&B=$paraB"
    }

    fun sendGetRequest(outPort:String, value:String) {
        var  url = "http://192.168.4.1/digital?port=$outPort&value=$value"
        FuelManager.instance.baseHeaders = mapOf(
            "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0"
        )

        url.httpGet().responseString { _, _, result ->
            result.fold(
                { str ->   },
                { fuelError ->    })
        }

    }


}