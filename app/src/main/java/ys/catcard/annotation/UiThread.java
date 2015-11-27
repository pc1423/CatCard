package ys.catcard.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 메소드를 UI 쓰레드에서 실행해주는 어노테이션.
 *
 * ※ delay
 * - delay 파라미터 설정시 해당 밀리초 이후에 실행된다. 기본값은 0이다.
 *
 * ※ 메소드 제약사항
 * - 메소드 리턴타입은 void만 가능
 * - Exception을 던지는 것이 가능하지만 에러정책에 따라 메소드 caller한테는 throw되지 않을 수 있음
 * </pre>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UiThread {
    long delay() default 0;
}
