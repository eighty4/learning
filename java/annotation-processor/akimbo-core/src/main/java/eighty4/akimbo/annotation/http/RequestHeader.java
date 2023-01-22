package eighty4.akimbo.annotation.http;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestHeader {
    String value() default "";
    String name() default "";
    boolean required() default false;
    String defaultValue() default "";
}
