package com.didichuxing.doraemonkit.gps_mock.gpsmock

import android.content.Context
import android.os.IBinder
import com.didichuxing.doraemonkit.util.ReflectUtils
import java.lang.reflect.Proxy
import java.util.*

/**
 * Created by wanglikun on 2019/4/2
 */
object ServiceHookManager {
    //https://www.androidos.net.cn/android/9.0.0_r8/xref/frameworks/base/core/java/android/os/ServiceManager.java
    var isHookSuccess = false
    private val mHookers: MutableList<BaseServiceHooker> = ArrayList()

    const val TAG = "ServiceHookManager"

    init {
        init()
    }

    private fun init() {
        //mHookers.add(new WifiHooker());
//        mHookers.add(LocationHooker())
//        mHookers.add(ActivityMangerHooker())
//        mHookers.add(ActivityTaskMangerHooker())
//        mHookers.add(PackageManagerHooker())
        //mHookers.add(new TelephonyHooker());
    }

    /**
     * 1、 IBinder是一个接口，它代表了一种跨进程传输的能力；只要实现了这个接口，就能将这个对象进行跨进程传递；这是驱动底层支持的；
     * 在跨进程数据流经驱动的时候，驱动会识别IBinder类型的数据，从而自动完成不同进程Binder本地对象以及Binder代理对象的转换。
     * 2、 IBinder负责数据传输，那么client与server端的调用契约（这里不用接口避免混淆）呢？这里的IInterface代表的就是远程server对象具有什么能力。
     * 具体来说，就是aidl里面的接口。
     * 3、Java层的Binder类，代表的其实就是Binder本地对象。BinderProxy类是Binder类的一个内部类，它代表远程进程的Binder对象的本地代理；
     * 这两个类都继承自IBinder, 因而都具有跨进程传输的能力；实际上，在跨越进程的时候，Binder驱动会自动完成这两个对象的转换。
     * 4、在使用AIDL的时候，编译工具会给我们生成一个Stub的静态内部类；这个类继承了Binder, 说明它是一个Binder本地对象，它实现了IInterface接口，
     * 表明它具有远程Server承诺给Client的能力；Stub是一个抽象类，具体的IInterface的相关实现需要我们手动完成，这里使用了策略模式。
     * @param context
     */
    fun install(context: Context) {
        try {
            //ServiceManager 中存储了本地Binder对象的代理对象
            val serviceManager = ReflectUtils.reflect("android.os.ServiceManager")
            for (hooker in mHookers) {
                hooker.init()
                //BinderProxy ：反射得到具体的本地Binder对象的代理对象
                val binderProxy =
                    serviceManager.method("getService", hooker.serviceName()).get<IBinder>()
                        ?: continue
                //LogHelper.i(TAG, "service in ServiceManager====>" + nativeBinderProxy);
                //转化为 native Binder proxy 的 proxy
                hooker.asInterface(binderProxy)
                val classLoader = binderProxy.javaClass.classLoader
                val iBinders = arrayOf<Class<*>>(IBinder::class.java)

                //目的是为了为IBinder.queryLocalInterface每次返回都不为null 而是返回我们自定义的Binder对象
                /* loader: 用哪个类加载器去加载代理对象
                 *
                 * interfaces:动态代理类需要实现的接口 指定newProxyInstance()方法返回的对象要实现哪些接口
                 *
                 * h:动态代理方法在执行时，会调用h里面的invoke方法去执行
                 */
                val handler = BinderHookHandler(binderProxy, hooker)
                val proxy = Proxy.newProxyInstance(classLoader, iBinders, handler) as IBinder
                //替换具体**Manager的mService
                hooker.replaceBinderProxy(context, proxy)

                //替换serviceManager中的缓存service
                val cache = serviceManager.field("sCache").get<MutableMap<String, IBinder>>()
                cache[hooker.serviceName()] = proxy
            }
            isHookSuccess = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
