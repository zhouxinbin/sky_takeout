package com.sky.annotation;

import com.sky.enumeration.OperationType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 公共字段自动填充注解。
 *
 * 这个注解不是给实体类用的，而是给 Mapper 的方法用的。
 * 只要某个方法标了这个注解，切面类 AutoFillAspect 就会在方法执行前拦截到它，
 * 然后根据这里传入的操作类型（新增 / 修改）自动给参数对象补上公共字段。
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    /**
     * 当前方法对应的数据库操作类型。
     *
     * 例如：
     * - INSERT：新增时，需要补 createTime、updateTime、createUser、updateUser
     * - UPDATE：修改时，只需要补 updateTime、updateUser
     */
    OperationType value();
}
