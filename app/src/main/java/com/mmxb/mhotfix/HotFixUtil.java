package com.mmxb.mhotfix;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by wxy on 2018/12/24
 */
public class HotFixUtil {

    public static void loadFixDex(Context context, File dexFile) {
        if (context == null || dexFile == null || !dexFile.isFile()) {
            return;
        }
        try {
            // 1.加载应用程序dex的Loader
            PathClassLoader pathLoader = (PathClassLoader) context.getClassLoader();
            // 2.加载指定的修复的dex文件的Loader
            DexClassLoader dexLoader = new DexClassLoader(
                    dexFile.getAbsolutePath(),    // 修复好的dex（补丁）所在目录
                    dexFile.getParentFile().getAbsolutePath(),  // 存放dex的解压目录（用于jar、zip、apk格式的补丁）
                    null,      // 加载dex时需要的库
                    pathLoader   // 父类加载器
            );
            // 3.开始合并
            // 合并的目标是Element[],重新赋值它的值即可

            /**
             * BaseDexClassLoader中有 变量: DexPathList pathList
             * DexPathList中有 变量 Element[] dexElements
             * 依次反射即可
             */

            //3.1 准备好pathList的引用
            Object dexPathList = getPathList(dexLoader);
            Object pathPathList = getPathList(pathLoader);
            //3.2 从pathList中反射出element集合
            Object leftDexElements = getDexElements(dexPathList);
            Object rightDexElements = getDexElements(pathPathList);
            //3.3 合并两个dex数组
            Object dexElements = combineArray(leftDexElements, rightDexElements);

            // 重写给PathList里面的Element[] dexElements;赋值
            Object pathList = getPathList(pathLoader);   // 一定要重新获取，不要用pathPathList，会报错
            setField(pathList, pathList.getClass(), "dexElements", dexElements);
            Toast.makeText(context, "修复完成", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 反射给对象中的属性重新赋值
     */
    private static void setField(Object obj, Class<?> cl, String field, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = cl.getDeclaredField(field);
        declaredField.setAccessible(true);
        declaredField.set(obj, value);
    }

    /**
     * 反射得到对象中的属性值
     */
    private static Object getField(Object obj, Class<?> cl, String field) throws NoSuchFieldException, IllegalAccessException {
        Field localField = cl.getDeclaredField(field);
        localField.setAccessible(true);
        return localField.get(obj);
    }


    /**
     * 反射得到类加载器中的pathList对象
     */
    private static Object getPathList(Object baseDexClassLoader) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(baseDexClassLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    /**
     * 反射得到pathList中的dexElements
     */
    private static Object getDexElements(Object pathList) throws NoSuchFieldException, IllegalAccessException {
        return getField(pathList, pathList.getClass(), "dexElements");
    }

    /**
     * 数组合并
     */
    private static Object combineArray(Object arrayLhs, Object arrayRhs) {
        Class<?> clazz = arrayLhs.getClass().getComponentType();
        int i = Array.getLength(arrayLhs);// 得到左数组长度（补丁数组）
        int j = Array.getLength(arrayRhs);// 得到原dex数组长度
        int k = i + j;// 得到总数组长度（补丁数组+原dex数组）
        Object result = Array.newInstance(clazz, k);// 创建一个类型为clazz，长度为k的新数组
        System.arraycopy(arrayLhs, 0, result, 0, i);
        System.arraycopy(arrayRhs, 0, result, i, j);
        return result;
    }
}