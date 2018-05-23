package com.example.dx.utilproject.annotation;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ParmeterTest {
    public static final int KEY_1 = 1;
    public static final int KEY_2 = 2;
    public static final int KEY_3 = 3;

    public static final String STR_1 = "1";
    public static final String STR_2 = "2";
    public static final String STR_3 = "3";

    /**
     * 定义一个int类型的参数限定注解,可用于替代枚举
     */
    @Target(ElementType.PARAMETER)
    @IntDef({KEY_1, KEY_2, KEY_3})
    @Retention(RetentionPolicy.SOURCE)
    private @interface KEY {

    }

    /**
     * 定义一个字符串类型的参数限定,可用于替代枚举
     */
    @Target(ElementType.PARAMETER)
    @StringDef({STR_1, STR_2, STR_3})
    @Retention(RetentionPolicy.SOURCE)
    private @interface STR {

    }

    public void test(@KEY int key) {

    }

    public void test2(@STR String str) {
        
    }
}
