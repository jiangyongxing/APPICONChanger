package cn.fengrong.appiconchanger

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    val COMPONENT_NAME = "componentName"
    var nextComponentName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var messageChanger = findViewById<Button>(R.id.am_change_message)
        var settingChanger = findViewById<Button>(R.id.am_change_setting)
        var cameraChanger = findViewById<Button>(R.id.am_change_camera)
        var defaultChanger = findViewById<Button>(R.id.am_default)
        componentName

        messageChanger.setOnClickListener {

            nextComponentName = "cn.fengrong.appiconchanger.StartUpAliasActivity3"
        }

        settingChanger.setOnClickListener {
            nextComponentName = "cn.fengrong.appiconchanger.StartUpAliasActivity2"
        }


        cameraChanger.setOnClickListener {
            nextComponentName = "cn.fengrong.appiconchanger.StartUpAliasActivity1"
        }


        defaultChanger.setOnClickListener {
            nextComponentName = "cn.fengrong.appiconchanger.MainActivity"
        }
    }

    fun getCurrentComponentName(): String {
        return SharePreferencesUtils.getParam(this, COMPONENT_NAME, "cn.fengrong.appiconchanger.MainActivity") as String
    }

    fun setCurrentComponentName(componentName: String) {
        SharePreferencesUtils.setParam(this, COMPONENT_NAME, componentName)
    }

    override fun onDestroy() {
        var currentComponentName = getCurrentComponentName()
        if (!TextUtils.isEmpty(nextComponentName)) {
            setCurrentComponentName(nextComponentName!!)

            Toast.makeText(this, "更换成功", Toast.LENGTH_SHORT).show()
            changeIcon(this, currentComponentName, nextComponentName!!)
        }
        super.onDestroy()
    }

}
