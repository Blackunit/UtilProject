package com.example.dx.utilproject.string;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/9/13.
 */

public class StringUtil {
    private boolean isExistToStringMethod(Class<?> cls){
        try {
            cls.getDeclaredMethod("toString",new Class[]{});
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
    private String invokeToStringMethod(Class<?> cls,Object obj){
        try {
            Method toStringMethod=cls.getDeclaredMethod("toString",new Class[]{});
            String result=(String) toStringMethod.invoke(obj,new Object[]{});
            return result;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 判断Field是否为基本类型的数据
     * @param field
     * @return
     */
    private boolean isBasicField(Field field){
        Class<?> cls=field.getType();
        if (cls==String.class
                ||cls==int.class
                ||cls==float.class
                ||cls==double.class
                ||cls==List.class){
            // TODO: 2017/9/13 判断还不够全
            return true;
        }
        return false;
    }
    private boolean isStaticOrFinalField(Field field){
        int modifier=field.getModifiers();
        return Modifier.isFinal(modifier)||Modifier.isStatic(modifier);
    }
    private List<Field> mergeFields(Field[] declaredField,Field[] fields){
        List<Field> fieldList=new ArrayList<>();
        List<String> fieldNamelist=new ArrayList<>();
        if (declaredField!=null&&declaredField.length>0) {
            for (int i = 0; i < declaredField.length; i++) {
                if (!isStaticOrFinalField(declaredField[i])) {
                    fieldList.add(declaredField[i]);
                    fieldNamelist.add(declaredField[i].getName());
                    declaredField[i].setAccessible(true);
                }
            }
        }
        if (fields!=null&&fields.length>0) {
            for (int i = 0; i < fields.length;i++) {
                if (!fieldNamelist.contains(fields[i].getName())){
                    if (!isStaticOrFinalField(fields[i])) {
                        fieldList.add(fields[i]);
                        fieldNamelist.add(fields[i].getName());
                        fields[i].setAccessible(true);
                    }
                }
            }
        }
        fieldNamelist.clear();
        return fieldList;
    }
    private void getAllAccessibleFileds(Class<?> cls,Object obj){
        Field[] declaredField=cls.getDeclaredFields();
        Field[] fields=cls.getFields();
        List<Field> allFields=mergeFields(declaredField,fields);
    }
    private String allFieldToString(Class<?> cls,Object obj){
        StringBuilder result=new StringBuilder();
        String clsName=cls.getName();
        result.append(clsName+"={");
        result.append("}");
        return result.toString();
    }
    public String toString(Class<?> cls,Object obj){
        if (cls==null||obj==null){
            return "StringUtil.toString()args is null";
        }
        if (isExistToStringMethod(cls)){
            return invokeToStringMethod(cls,obj);
        }
        return allFieldToString(cls,obj);
    }
}
