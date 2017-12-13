package com.example.dx.utilproject.annotation;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import java.lang.reflect.Field;

/**
 * 参考ButterKnife,使用@BindID注解代替findViewById
 * 使用方式eg:
 * //@BindID(R.id.xxx)              //1
 * Button btnTest;
 * ...
 * ...onCreate(...){
 *    super.onCreate(savedInstanceState);
 *    setContentView(R.layout.activity_main);
 *    BindHelper.bind(this);        //2
 * }
 * Created by admin on 2017/12/13.
 */

public class BindHelper {
    public static void bind(@NonNull Activity activity) {
        Field[] fields = activity.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(BindID.class)) {
                field.setAccessible(true);
                BindID bindID = field.getAnnotation(BindID.class);
                int id = bindID.value();
                try {
                    View view=activity.findViewById(id);
                    field.set(activity,view);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
