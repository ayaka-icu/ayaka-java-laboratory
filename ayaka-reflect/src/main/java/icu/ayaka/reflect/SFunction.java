package icu.ayaka.reflect;

import java.io.Serializable;
import java.util.function.Function;

/**
 * SFunction代替Function，获取序列化能力
 */
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {
}