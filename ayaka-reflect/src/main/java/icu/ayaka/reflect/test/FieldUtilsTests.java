package icu.ayaka.reflect.test;

import icu.ayaka.common.entry.User;
import icu.ayaka.reflect.FieldUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

import static icu.ayaka.reflect.FieldUtils.GET;
import static icu.ayaka.reflect.FieldUtils.SET;

public class FieldUtilsTests {

    @Test
    public void getFieldMethodTest() {
        User user = User.getTestUser();

        Field nameField = FieldUtils.getField(User::getName);
        System.out.println(nameField.getName() + '\n');

        Method nameMethod = FieldUtils.getFieldGetMethod(nameField);
        System.out.println(nameMethod.getName() + '\n');

        nameMethod = FieldUtils.getFieldGetMethod(User.class, "name");
        System.out.println(nameMethod.getName() + '\n');

        nameMethod = FieldUtils.getFieldSetMethod(nameField);
        System.out.println(nameMethod.getName() + '\n');

        Method adminMe = FieldUtils.getFieldSetMethod(User.class, "admin");
        System.out.println(adminMe.getName() + '\n');

        Map<String, Method> admin = FieldUtils.getGetSetMethodMap(User.class, "admin");
        System.out.println("GET: " + admin.get(GET).getName());
        System.out.println("SET: " + admin.get(SET).getName() + '\n');

        Map<String, Method> map = FieldUtils.getGetSetMethodMap(nameField);
        System.out.println("GET: " + map.get(GET).getName());
        System.out.println("SET: " + map.get(SET).getName() + '\n');

        Map<String, Method> map2 = FieldUtils.getGetMethodMap(User.class, nameField, FieldUtils.getField(User::isAdmin));
        map2.forEach((k, v) -> System.out.println(k + ": " + v.getName()));
        System.out.println();

        Map<String, Method> map3 = FieldUtils.getFieldsSetMethods(User.class, nameField, FieldUtils.getField(User::isAdmin));
        map3.forEach((k, v) -> System.out.println(k + ": " + v.getName()));
        System.out.println();

        Map<String, Method> beanGetMethods = FieldUtils.getBeanGetMethods(User.class);
        beanGetMethods.forEach((k, v) -> System.out.println(k + ": " + v.getName()));
        System.out.println();

        Map<String, Method> beanSetMethods = FieldUtils.getBeanSetMethods(User.class);
        beanGetMethods.forEach((k, v) -> System.out.println(k + ": " + v.getName()));
        System.out.println();

    }

    @Test
    public void getMethodFieldTest() {
        User user = User.getTestUser();
        Method emailMe1 = FieldUtils.getFieldGetMethod(User.class, "email");
        Method emailMe2 = FieldUtils.getFieldSetMethod(User.class, "email");

        Field field = FieldUtils.getField(User::getName);
        System.out.println(field.getName() + '\n');

        Field email = FieldUtils.getFieldByGetMethod(emailMe1);
        System.out.println(email.getName() + '\n');

        Field admin = FieldUtils.getFieldByGetMethod(User.class, "isAdmin");
        System.out.println(admin.getName() + '\n');

        email = FieldUtils.getFieldBySetMethod(emailMe2);
        System.out.println(email.getName() + '\n');

        email = FieldUtils.getFieldBySetMethod(User.class, "setEmail");
        System.out.println(email.getName() + '\n');

    }


    @Test
    public void getFieldMethodTimeTest() {
        Field field = FieldUtils.getField(User::getName);
        Method method = null;
        String name = "name";
        int count = 20000000;

        long time = new Date().getTime();
        for (int i = 0; i < count; i++) {
            // method = FieldUtils.getFieldGetMethod(field);
            method = FieldUtils.getFieldGetMethod(User.class, name);
        }
        time = new Date().getTime() - time;
        System.out.println("运行次数：" + count + "次");
        System.out.println("运行耗时：" + time + "ms");
        // 100000次 ≈ 17ms
        // 20000000次 ≈ 755ms
        System.out.println("运行结果：" + method.getName());
    }

    @Test
    public void getFieldByMethodTest() {
        Field field = null;
        String name = "getName";
        Method method = FieldUtils.getFieldGetMethod(User.class, "name");

        int count = 20000000;
        long time = new Date().getTime();
        for (int i = 0; i < count; i++) {
            field = FieldUtils.getFieldByGetMethod(method);
            // field = FieldUtils.getFieldByGetMethod(User.class, name);
        }
        time = new Date().getTime() - time;
        System.out.println("运行次数：" + count + "次");
        System.out.println("运行耗时：" + time + "ms");
        // 100000次 ≈ 35ms
        // 20000000次 ≈ 2000~2200ms
        System.out.println("运行结果：" + field.getName());
    }

    @Test
    public void getMeLambda() {
        Field field = null;
        int count = 10000000;
        long time = new Date().getTime();
        for (int i = 0; i < count; i++) {
            field = FieldUtils.getField(User::getName);
        }
        time = new Date().getTime() - time;
        System.out.println("运行次数：" + count + "次");
        System.out.println("运行耗时：" + time + "ms");
        // 1000次 ≈ 21ms
        // 10000次 ≈ 45ms
        // 100000次 ≈ 163ms
        // 1000000次 ≈ 800ms
        // 10000000次 ≈ 6500ms
        System.out.println("运行结果：" + field.getName());
    }

}
