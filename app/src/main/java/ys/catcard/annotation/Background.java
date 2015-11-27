package ys.catcard.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 메소드를 백그라운드 쓰레드에서 실행해주는 어노테이션.
 *
 * ※ 메소드 제약사항
 * - 메소드 리턴타입은 void만 가능
 * - Exception을 던지는 것이 가능하지만 에러정책에 따라 메소드 caller한테는 throw되지 않을 수 있음
 *
 * ※ NOTE
 * - 이 어노테이션은 작업진행상태 스핀다이얼로그 표시, 작업취소 처리, 작업 콜백메소드 등의 기능을 지원하지 않는다.
 * </pre>
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Background {
}