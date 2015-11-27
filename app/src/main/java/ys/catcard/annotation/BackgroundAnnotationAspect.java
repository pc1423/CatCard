package ys.catcard.annotation;

import android.os.Handler;
import android.os.Looper;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * <pre>
 * {@link Background}, {@link UiThread} 어노테이션이 붙은 메소드가
 * 백그라운드 및 UI쓰레드에서 실행되도록 애스펙트로 처리한다.
 * </pre>
 */
@Aspect
public class BackgroundAnnotationAspect {

    private Handler uiHandler = new Handler(Looper.getMainLooper());

    private Executor threadExecutor = Executors.newCachedThreadPool();

    @Around("execution(@ys.catcard.annotation.Background * *.*(..))")
    public void proceedInBackground(final ProceedingJoinPoint pjp) {
        threadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    pjp.proceed();
                } catch (final Throwable e) {
                    // 에러정책에 따라 적절히 에러 처리
                    e.printStackTrace();
                }
            }
        });
    }

    @Around("@annotation(uithread) && execution(@ys.catcard.annotation.UiThread * *.*(..))")
    public void proceedInUiThread(final ProceedingJoinPoint pjp, UiThread uithread) {
        runInUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    pjp.proceed();
                } catch (Throwable e) {
                    // 에러정책에 따라 적절히 에러 처리
                    e.printStackTrace();
                }
            }
        }, uithread.delay());
    }


    /**
     * {@link Runnable}을 UI쓰레드에서 실행한다.
     *
     * @param run
     * @param delayMillis 지연시간. 밀리초
     */
    private void runInUiThread(Runnable run, long delayMillis) {
        if (delayMillis <= 0) {
            // 현재 쓰레드가 UI 쓰레드인 경우 그냥 run 실행.
            if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                run.run();
                return;
            }

            uiHandler.post(run);
            return;
        }

        uiHandler.postDelayed(run, delayMillis);
    }
}