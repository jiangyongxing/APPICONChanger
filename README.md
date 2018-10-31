### 前言

当老板和产品提出这种需求的时候，我并不感到害怕，心里甚至有点窃喜，因为大厂基本都有这种效果，那肯定也好实现。当我一查资料的时候，发现情况不容乐观。

首先我尝试着使用给我们的 `activity` 设置别名，也就是 `activity-alias` ，但是我在网上看到好多人都说，这个有以下的坑，当然，我也验证了，确实有以下坑：

+ 在动态更换完ICON以后，可能会发生关闭APP，
+ 在三星手机(可能还有其他的手机)上，更换ICON以后，ICON在桌面上的位置会发生变化。
+ 更换ICON以后，在桌面上显示还是原来的ICON，点击原来的ICON会出现**未安装应用程序**提示，过个几秒钟才会更换ICON。

看到这些坑就觉得害怕，就在想大厂应该不会用这种方式，他们更换ICON的时候都没有出现这些情况，他们应该用的**热修复**。没错，我对他们的技术方案进行了定义，我觉得他们应该采用的是热修复，然后就跟我们的产品说：我们可以使用热修复来达到这种效果。最后产品也同意我们使用热修复了，我们决定使用阿里家的Sophix，这是一款商业化收费的框架，它的接入程度要比其他所有的框架都要简单。**可是，通过它的文档我才知道，它不支持更换桌面ICON**

还是老老实实使用 `activity-alias` 吧，把它的坑都踩一踩、填一填。

### 正文

我们这边先直接上代码，有坑的时候，我们再一个一个填：

一般来说：我们定义一个入口的 `Activity` 一般来说是这样的：

```xml
<application
    android:allowBackup="true"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:roundIcon="@mipmap/ic_launcher_round"
    android:supportsRtl="true"
    android:theme="@style/AppTheme"
    tools:ignore="GoogleAppIndexingWarning">
    <activity android:name=".MainActivity">
        <intent-filter>
            <action android:name="android.intent.action.MAIN"/>
            <category android:name="android.intent.category.LAUNCHER"/>
        </intent-filter>
    </activity>
</application>
```

这样子的话，就会在我们的桌面上显示一个入口

![](https://ws3.sinaimg.cn/large/006tNbRwly1fwrfhcip4aj30f20qqgse.jpg)

如何显示多入口呢？

```xml
<application
    android:allowBackup="true"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:roundIcon="@mipmap/ic_launcher_round"
    android:supportsRtl="true"
    android:theme="@style/AppTheme"
    tools:ignore="GoogleAppIndexingWarning">
    <activity android:name=".MainActivity">
        <intent-filter>
            <action android:name="android.intent.action.MAIN"/>

            <category android:name="android.intent.category.LAUNCHER"/>
        </intent-filter>
    </activity>

    <activity-alias
        android:name=".StartUpAliasActivity1"
        android:enabled="true"
        android:icon="@drawable/ic_camera"
        android:targetActivity=".MainActivity">

        <intent-filter>
            <action android:name="android.intent.action.MAIN"/>
            <category android:name="android.intent.category.LAUNCHER"/>
        </intent-filter>
    </activity-alias>
    <activity-alias
        android:name=".StartUpAliasActivity2"
        android:enabled="true"
        android:icon="@drawable/ic_message"
        android:targetActivity=".MainActivity">

        <intent-filter>
            <action android:name="android.intent.action.MAIN"/>
            <category android:name="android.intent.category.LAUNCHER"/>
        </intent-filter>
    </activity-alias>
    <activity-alias
        android:name=".StartUpAliasActivity3"
        android:enabled="true"
        android:icon="@drawable/ic_settings"
        android:targetActivity=".MainActivity">

        <intent-filter>
            <action android:name="android.intent.action.MAIN"/>
            <category android:name="android.intent.category.LAUNCHER"/>
        </intent-filter>
    </activity-alias>

</application>
```

我们这边又是多定义了三组 `activity-alias` ，这样子的话，将会在桌面上多出三个入口。

![image-20181031152258739](https://ws3.sinaimg.cn/large/006tNbRwly1fwrfqpm6zrj30ey0rg0zp.jpg)

现在我们随便选一组讲解一下：

```xml
<activity-alias
                
        <!--这个就是别名，当前入口的别名-->
        android:name=".StartUpAliasActivity2"
		<!--是否在桌面上显示，当前入口是否要在桌面上显示图标-->
        android:enabled="true"
        <!--在桌面上显示时的图标-->
        android:icon="@drawable/ic_message"
		<!--给哪个activity设置的别名呢-->
        android:targetActivity=".MainActivity">
        <!--通过桌面图标打开的intent-filter的配置-->
        <intent-filter>
            <action android:name="android.intent.action.MAIN"/>
            <category android:name="android.intent.category.LAUNCHER"/>
        </intent-filter>
    </activity-alias>
```

通过这个操作，其实我们可以知道更换图标也就是显示一个，隐藏剩下的，然后就是显示另一个，隐藏剩下的。

更换ICON的主要核心代码如下：

```kotlin
fun changeIcon(context: Activity, currentComponentName: String, nextComponentName: String) {
    val pm = context.packageManager
    // 隐藏当前显示的，
    pm.setComponentEnabledSetting(ComponentName(context, currentComponentName), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
	// 显示需要展示的
    pm.setComponentEnabledSetting(ComponentName(context, nextComponentName), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0)
}
```

在 `Activity` 中的调用代码：

```kotlin
changeIcon(this, "cn.fengrong.appiconchanger.MainActivity", "cn.fengrong.appiconchanger.StartUpAliasActivity1")
```

我们这里对`changeIcon` 的参数进行解释，**这里有一个坑**：

+ `currentComponentName` ：这个参数是指需要隐藏的图标所对应的 `ActivityName` ，这个参数的来源可以到 `AndroidManifest.xml` 文件中查看，首先看看默认的 `ActivityName` 是如何获得的：

  ![image-20181031155432214](https://ws2.sinaimg.cn/large/006tNbRwly1fwrgkab3nxj30yg0db77c.jpg)

  图片中有两个框，默认的  `ActivityName` 就是通过这两个框里面的参数拼接起来的，所以默认的  `ActivityName` 为：**`cn.fengrong.appiconchanger.MainActivity`**

  我们再来看看 `cn.fengrong.appiconchanger.StartUpAliasActivity1` 这个是如何获得的：

  ![image-20181031160027291](https://ws1.sinaimg.cn/large/006tNbRwly1fwrgqgls7uj31kw136n85.jpg)

  有些例子中 `currentComponentName` 都是直接通过 `Activity.componentName` 去获取的，但是我这里将它写成活的主要是因为，当前的 `Activity` 有可能并不是应用入口，通常我们的APP都会有一个欢迎页面，然后再进入到主页，如果我们在主页中去修改图标的话，调用 `Activity.componentName` 就不能到正确的 `ActivityName` 了，**这就是一个大坑** 。

+ `nextComponentName` 这个参数指的是需要展示图标所对应的 `ActivityName` 。

 通过以上的这些方法，你就可以动态的更换ICON。

### 坑

#### 坑一：APP会被关闭

在调用更换ICON的方法以后，会有几率出现关闭APP的情况，就算我们将 `setComponentEnabledSetting` 中最后一个参数都设置为 `PackageManager.DONT_KILL_APP` 也于事无补，最后我决定，在关闭App 的时候在调用这个方法。我看到有同学在博客中说到可以这样做：用一个 `Activity` 进行切换ICON的操作，切换完成以后，在2s之后关闭 `activity` ，这样子就不会出现关闭APP的情况。有兴趣的同学自己自己试试，本人并没有测试过这种方式。

#### 坑二：桌面图标刷新慢

调用 `changeIcon` 方法以后回到桌面上可以看到ICON并没有被立刻更换掉，点击原来的ICON会出现**未安装程序应用**提示，然后过了大概6、7秒，甚至更长时间才会更换ICON，在小米手机上(这里本人只测试三星S9和小米6)上调用刷新桌面的方法还有效果，会将桌面进行重启，但是三星手机理都不理我。

这种坑的解决办法是基于我们只在App关闭的时候进行ICON的更换，我们可以看到 `changeIcon` 方法：

```kotlin
fun changeIcon(context: Activity, currentComponentName: String, nextComponentName: String) {
    val pm = context.packageManager
    pm.setComponentEnabledSetting(ComponentName(context, currentComponentName), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
    pm.setComponentEnabledSetting(ComponentName(context, nextComponentName), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0)
}
```

在设置图标不可见的方法中，我们传递的是 `PackageManager.DONT_KILL_APP` ，在设置图标可见的方法中我们传递的是 **0** 。传递0表示会杀死包含该组件的app，桌面图标立即会更改掉。**这样设置以后，在  `changeIcon` 以后执行的方法都不会被调用了，因为APP已经被杀死了。**

#### 坑三：更改完图标以后，Android studio就不能更新app了

对于这个坑的解决方法，我们这边设置了恢复默认ICON的按钮，并没有解决这个坑，只是绕过了它。但是APP通过包的安装更新好像还是可以的。

#### 坑四：更换ICON以后，图标的位置变了

部分手机会出现这种问题，部分手机不会，这个坑，我也无法解决。

#### 坑五：ICON只能预存，不能从服务器下载

暂时只能预存进去APP，还不能从服务器下载

### 效果展示

![SM-G9600_20181031163018](https://ws2.sinaimg.cn/large/006tNbRwly1fwriezgz6lg314024wnpk.gif)

如果图片无法查看，可以尝试查看项目中的 img 文件夹中的sample.gif文件

