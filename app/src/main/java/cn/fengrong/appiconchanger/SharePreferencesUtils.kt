package cn.fengrong.appiconchanger

import android.content.Context


/**
 *
 * Created by jiangyongxing on 2018/10/31.
 * 描述：
 *
 */
object SharePreferencesUtils {

    /**
     * 保存在手机里面的文件名
     */
    private val FILE_NAME = "share_date"

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     * @param context
     * @param key
     * @param object
     */
    fun setParam(context: Context, key: String, data: String) {
        val sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        val editor = sp.edit()

        editor.putString(key, data)

        editor.commit()
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    fun getParam(context: Context, key: String, data: String): String? {
        val sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        return sp.getString(key, data)
    }

}
