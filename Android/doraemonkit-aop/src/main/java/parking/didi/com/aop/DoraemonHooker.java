package parking.didi.com.aop;

import android.app.Application;
import android.util.Log;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.kit.timecounter.TimeCounterManager;
import com.didichuxing.doraemonkit.util.LogHelper;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 通用的hooker
 */
@Aspect
public class DoraemonHooker {
    public static final String TAG = "DoraemonHooker";

    /**
     * @param proceedingJoinPoint
     * @throws Throwable
     * @Around 完全替代目标方法执行
     */
    @Around("execution(* android.app.Application.onCreate(..))")
    public void install(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Log.v(TAG, "hook application.onCreate start");
        TimeCounterManager.get().onAppCreateStart();
        //执行原有方法
        proceedingJoinPoint.proceed();
//        Application app = (Application) proceedingJoinPoint.getTarget();
//        DoraemonKit.install(app);
        TimeCounterManager.get().onAppCreateEnd();
        Log.v(TAG, "hook application.onCreate end");
    }
}