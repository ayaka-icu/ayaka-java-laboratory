package icu.ayaka.reflect;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通过反射获取属性，方法工具类
 * 通过 属性 获取 其get/set方法
 * 通过 方法 获取 其属性
 *
 * @author Ayaka
 */
public class FieldUtils {

    public static final String GET = "get";

    public static final String SET = "set";

    public static final String IS = "is";

    private static final String CLASS = "class";

    private static final String WRITE_REPLACE = "writeReplace";

    private static final String ERROR_MESSAGE = "[Field] [Method] 转换异常";

    /**
     * 通过属性获取其get方法
     * <blockquote><pre>
     * {@code for (int i = 0; i < 10000000; i++) {
     *      // Method m = thisIsAVeryStupidMethode(SysUser.class, "name");
     *      // Method m = FieldUtils.getFieldGetMethod(SysUser.class, "name");
     *  }
     *  // thisIsAVeryStupidMethode 耗时： 3661 ms
     *  // getFieldGetMethod        耗时： 402  ms
     * }</blockquote></pre>
     */
    @Deprecated
    public static Method thisIsAVeryStupidMethode(Class<?> clazz, String propertyName) {
        // 为判断 boolean 类型属性，is 为 get 方法的前缀！！！
        String methodName = GET + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    //  ================= 通过字段 获取 get/set方法 ===================

    /**
     * <h3>
     * 根据 Field对象 对应的get方法的Method对象
     * </h3>
     *
     * @param field field对象
     * @return field对应的get方法 的 Method对象
     */
    public static Method getFieldGetMethod(Field field) {
        try {
            PropertyDescriptor[] pds = getPropertyDescriptors(field.getDeclaringClass());
            PropertyDescriptor pd = pdsHitName(pds, field.getName());
            return get(pd);
        } catch (IntrospectionException e) {
            throw new RuntimeException(ERROR_MESSAGE, e);
        }
    }

    /**
     * <h3>
     * 根据 [类] 和 [类的字段名称] 对应的get方法的Method对象
     * </h3>
     *
     * @param clazz     类
     * @param fieldName 字段名称
     * @return 字段对应的get方法 的 Method对象
     */
    public static Method getFieldGetMethod(Class<?> clazz, String fieldName) {
        try {
            PropertyDescriptor[] pds = getPropertyDescriptors(clazz);
            PropertyDescriptor pd = pdsHitName(pds, fieldName);
            return get(pd);
        } catch (IntrospectionException e) {
            throw new RuntimeException(ERROR_MESSAGE, e);
        }
    }

    /**
     * <h3>
     * 根据 Field对象 对应的set方法的Method对象
     * </h3>
     *
     * @param field field对象
     * @return field对应的set方法 的 Method对象
     */
    public static Method getFieldSetMethod(Field field) {
        try {
            PropertyDescriptor[] pds = getPropertyDescriptors(field.getDeclaringClass());
            PropertyDescriptor pd = pdsHitName(pds, field.getName());
            return set(pd);
        } catch (IntrospectionException e) {
            throw new RuntimeException(ERROR_MESSAGE, e);
        }
    }

    /**
     * <h3>
     * 根据 [类] 和 [类的字段名称] 对应的set方法的Method对象
     * </h3>
     *
     * @param clazz     类
     * @param fieldName 字段名称
     * @return 字段对应的set方法 的 Method对象
     */
    public static Method getFieldSetMethod(Class<?> clazz, String fieldName) {
        try {
            PropertyDescriptor[] pds = getPropertyDescriptors(clazz);
            PropertyDescriptor pd = pdsHitName(pds, fieldName);
            return set(pd);
        } catch (IntrospectionException e) {
            throw new RuntimeException(ERROR_MESSAGE, e);
        }
    }

    /**
     * <h3>
     * 根据 [类] 和 [类的字段名称] 对应的set方法的Method对象
     * </h3>
     *
     * @param clazz     类
     * @param fieldName 字段名称
     * @return 字段对应的set方法 的 Method对象
     */
    public static Map<String, Method> getGetSetMethodMap(Class<?> clazz, String fieldName) {
        try {
            PropertyDescriptor[] pds = getPropertyDescriptors(clazz);
            PropertyDescriptor pd = pdsHitName(pds, fieldName);
            return pd == null ? Map.of() : Map.of(
                    GET, get(pd),
                    SET, set(pd)
            );
        } catch (IntrospectionException e) {
            throw new RuntimeException(ERROR_MESSAGE, e);
        }
    }


    /**
     * <h3>
     * 根据 Field对象 获取get和set方法的Method对象
     * </h3>
     *
     * @param field field对象
     * @return Map key: get/set value: Method对象
     */
    public static Map<String, Method> getGetSetMethodMap(Field field) {
        try {
            PropertyDescriptor[] pds = getPropertyDescriptors(field.getDeclaringClass());
            PropertyDescriptor pd = pdsHitName(pds, field.getName());
            return pd == null ? Map.of() : Map.of(
                    GET, get(pd),
                    SET, set(pd)
            );
        } catch (IntrospectionException e) {
            throw new RuntimeException(ERROR_MESSAGE, e);
        }
    }

    /**
     * <h3>
     * 根据class对象 获取所有的Field对象 对一个get方法
     * </h3>
     *
     * @param clazz  类
     * @param fields Field对象数组
     * @return Map key: 字段名称 value: 字段名称对应的get方法
     */
    public static Map<String, Method> getGetMethodMap(Class<?> clazz, Field... fields) {
        Map<String, Method> getMethods = new HashMap<>();
        try {
            PropertyDescriptor[] pds = getPropertyDescriptors(clazz);
            for (Field field : fields) {
                PropertyDescriptor pd = pdsHitName(pds, field.getName());
                getMethods.put(field.getName(), get(pd));
            }
            return getMethods;
        } catch (IntrospectionException e) {
            throw new RuntimeException(ERROR_MESSAGE, e);
        }
    }

    /**
     * <h3>
     * 根据class对象 获取所有的Field对象 对一个set方法
     * </h3>
     *
     * @param clazz  类
     * @param fields Field对象数组
     * @return Map key: 字段名称 value: 字段名称对应的set方法
     */
    public static Map<String, Method> getFieldsSetMethods(Class<?> clazz, Field... fields) {
        Map<String, Method> getMethods = new HashMap<>();
        try {
            PropertyDescriptor[] pds = getPropertyDescriptors(clazz);
            for (Field field : fields) {
                PropertyDescriptor pd = pdsHitName(pds, field.getName());
                getMethods.put(field.getName(), set(pd));
            }
            return getMethods;
        } catch (IntrospectionException e) {
            throw new RuntimeException(ERROR_MESSAGE, e);
        }
    }

    /**
     * <h3>
     * 根据class对象 获取所有的Field对象 对一个get方法
     * </h3>
     * 不包含 class 属性
     *
     * @param clazz 类
     * @return Map key: 字段名称 value: 字段名称对应的get方法
     */
    public static Map<String, Method> getBeanGetMethods(Class<?> clazz) {
        try {
            return Arrays.stream(getPropertyDescriptors(clazz))
                    .filter(p -> !CLASS.equals(p.getName()))
                    .filter(FieldUtils::existsGet)
                    .collect(Collectors.toMap(PropertyDescriptor::getName,
                            PropertyDescriptor::getReadMethod));
        } catch (IntrospectionException e) {
            throw new RuntimeException(ERROR_MESSAGE, e);
        }
    }

    /**
     * <h3>
     * 根据class对象 获取所有的Field对象 对一个set方法
     * </h3>
     *
     * @param clazz 类
     * @return Map key: 字段名称 value: 字段名称对应的set方法
     */
    public static Map<String, Method> getBeanSetMethods(Class<?> clazz) {
        try {
            return Arrays.stream(getPropertyDescriptors(clazz))
                    .filter(p -> !CLASS.equals(p.getName()))
                    .filter(FieldUtils::existsSet)
                    .collect(Collectors.toMap(PropertyDescriptor::getName,
                            PropertyDescriptor::getWriteMethod));
        } catch (IntrospectionException e) {
            throw new RuntimeException(ERROR_MESSAGE, e);
        }
    }


    // ================= 通过 get/set方法 获取字段  ===================

    /**
     * <h3>
     * 将bean的属性的get方法，作为lambda表达式传入时，获取get方法对应的属性Field
     * </h3>
     * <blockquote><pre>
     * {@code //定义 SFunction代替Function，获取序列化能力
     *  @FunctionalInterface
     *  public interface SFunction<T, R> extends Function<T, R>, Serializable {
     *
     *  }
     * }</pre></blockquote>
     *
     * @param fn  lambda表达式，bean的属性的get方法
     * @param <T> 泛型
     * @return 属性对象
     */
    public static <T> Field getField(SFunction<T, ?> fn) {
        try {
            // 通过反射获取 lambda 表达式对象的 writeReplace 方法
            Method writeReplaceMethod = fn.getClass().getDeclaredMethod(WRITE_REPLACE);
            //boolean isAccessible = writeReplaceMethod.isAccessible(); // jdk9 已弃用
            boolean isAccessible = Modifier.isPublic(writeReplaceMethod.getModifiers());
            writeReplaceMethod.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplaceMethod.invoke(fn);
            writeReplaceMethod.setAccessible(isAccessible);
            Class<?> implClass = Class.forName(serializedLambda.getImplClass().replace("/", "."));
            return getFieldByGetMethod(implClass, serializedLambda.getImplMethodName());
        } catch (Exception e) {
            throw new RuntimeException(ERROR_MESSAGE, e);
        }
    }

    /**
     * <h3>
     * 根据 [get方法] 对应的 字段的 Field 对象
     * </h3>
     *
     * @param method get方法
     * @return Field 对象
     */
    public static Field getFieldByGetMethod(Method method) {
        try {
            Class<?> clazz = method.getDeclaringClass();
            PropertyDescriptor[] pds = getPropertyDescriptors(clazz);
            PropertyDescriptor pd = pdsHitGet(pds, method.getName());
            return pd == null ? null : clazz.getDeclaredField(pd.getName());
        } catch (IntrospectionException | NoSuchFieldException e) {
            throw new RuntimeException(ERROR_MESSAGE, e);
        }
    }

    /**
     * <h3>
     * 根据 [类] 和 [字段名称] 对应的get方法的Method对象
     * </h3>
     *
     * @param clazz      类
     * @param methodName get方法名称
     * @return get方法对应的Method对象
     */
    public static Field getFieldByGetMethod(Class<?> clazz, String methodName) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
            PropertyDescriptor pd = pdsHitGet(pds, methodName);
            return pd == null ? null : clazz.getDeclaredField(pd.getName());
        } catch (IntrospectionException | NoSuchFieldException e) {
            throw new RuntimeException(ERROR_MESSAGE, e);
        }
    }

    /**
     * <h3>
     * 根据 [set方法] 对应的 字段的 Field 对象
     * </h3>
     *
     * @param method set方法
     * @return Field 对象
     */
    public static Field getFieldBySetMethod(Method method) {
        try {
            Class<?> clazz = method.getDeclaringClass();
            PropertyDescriptor[] pds = getPropertyDescriptors(clazz);
            PropertyDescriptor pd = pdsHitSet(pds, method.getName());
            return pd == null ? null : clazz.getDeclaredField(pd.getName());
        } catch (IntrospectionException | NoSuchFieldException e) {
            throw new RuntimeException(ERROR_MESSAGE, e);
        }
    }

    /**
     * <h3>
     * 根据 [类] 和 [set方法名称] 对应的get方法的Method对象
     * </h3>
     *
     * @param clazz      类
     * @param methodName set方法名称
     * @return Field 对象
     */
    public static Field getFieldBySetMethod(Class<?> clazz, String methodName) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
            PropertyDescriptor pd = pdsHitSet(pds, methodName);
            return pd == null ? null : clazz.getDeclaredField(pd.getName());
        } catch (IntrospectionException | NoSuchFieldException e) {
            throw new RuntimeException(ERROR_MESSAGE, e);
        }
    }

    // ================= 工具方法  ===================

    /**
     * <h3>
     * 获取class对象的PropertyDescriptor数组
     * </h3>
     *
     * @param clazz 类
     * @return PropertyDescriptor数组
     * @throws IntrospectionException 异常
     */
    private static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        return beanInfo.getPropertyDescriptors();
    }

    /**
     * 获取 get 方法
     */
    private static Method get(PropertyDescriptor pd) {
        return pd == null ? null : pd.getReadMethod();
    }

    /**
     * 获取 set 方法
     */
    private static Method set(PropertyDescriptor pd) {
        return pd == null ? null : pd.getWriteMethod();
    }

    /**
     * pd的字段描述名 是否与 字段名相同
     */
    private static boolean eqName(PropertyDescriptor pd, String name) {
        return pd.getName().equals(name);
    }

    /**
     * pd是否有 get方法
     */
    private static boolean existsGet(PropertyDescriptor pd) {
        return pd.getReadMethod() != null;
    }

    /**
     * pd是否有 set方法
     */
    private static boolean existsSet(PropertyDescriptor pd) {
        return pd.getWriteMethod() != null;
    }

    /**
     * pd是否有 get和set方法
     */
    private static boolean existsGetAndSet(PropertyDescriptor pd) {
        return pd.getWriteMethod() != null && pd.getReadMethod() != null;
    }

    /**
     * 根据字段名称，过滤出pds中，符合的pd
     */
    private static PropertyDescriptor pdsHitName(PropertyDescriptor[] pds, String name) {
        for (PropertyDescriptor pd : pds) {
            if (eqName(pd, name)) {
                return pd;
            }
        }
        return null;
    }

    /**
     * 根据字段get方法名，过滤出pds中，符合的pd
     */
    private static PropertyDescriptor pdsHitGet(PropertyDescriptor[] pds, String name) {
        for (PropertyDescriptor pd : pds) {
            if (existsGet(pd) && get(pd).getName().equals(name)) {
                return pd;
            }
        }
        return null;
    }

    /**
     * 根据字段set方法名，过滤出pds中，符合的pd
     */
    private static PropertyDescriptor pdsHitSet(PropertyDescriptor[] pds, String name) {
        for (PropertyDescriptor pd : pds) {
            if (existsSet(pd) && set(pd).getName().equals(name)) {
                return pd;
            }
        }
        return null;
    }
}