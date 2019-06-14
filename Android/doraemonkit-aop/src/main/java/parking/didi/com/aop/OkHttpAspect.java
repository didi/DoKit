package parking.didi.com.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

import okhttp3.OkHttpClient;

@Aspect
public class OkHttpAspect {
    private static final String TAG = "OkHttpAspect";

    @After("execution(okhttp3.OkHttpClient.Builder.new(..))")
    public void addInterceptor(JoinPoint joinPoint) {
        AopUtils.addInterceptor((OkHttpClient.Builder) joinPoint.getTarget());
    }
}