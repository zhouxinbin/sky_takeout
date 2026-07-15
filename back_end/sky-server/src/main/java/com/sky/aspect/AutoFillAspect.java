package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 公共字段自动填充切面。
 *
 * 你可以把“切面”理解成一段统一的拦截逻辑：
 * 当某些 Mapper 方法被调用时，不用每个业务方法都手动去写
 * “设置创建时间、更新时间、创建人、修改人” 这类重复代码，
 * 而是交给这里统一处理。
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点表达式。
     *
     * 这句话的意思是：
     * 1. 只拦截 com.sky.mapper 包下的任意方法；
     * 2. 并且这个方法上必须标了 @AutoFill 注解。
     *
     * 所以，只有符合这两个条件的方法，才会进入下面的前置通知。
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {}

    /**
     * 前置通知。
     *
     * “前置通知”表示：目标方法真正执行之前，先执行这里的代码。
     * 这里的职责是：
     * 1. 找到当前被调用的 mapper 方法；
     * 2. 读取方法上的 @AutoFill 注解，拿到它对应的操作类型；
     * 3. 取出方法参数中的实体对象；
     * 4. 根据新增 / 修改，自动给实体对象补公共字段。
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("公共字段自动填充...");
        // 获取当前被拦截的方法签名，用它可以拿到方法信息、参数信息、注解信息等。
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 读取方法上的 @AutoFill 注解，从注解里拿到操作类型（INSERT / UPDATE）。
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        // 获取 mapper 方法的参数列表。
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            // 没有参数就没法给实体对象赋值，所以直接结束。
            return;
        }
        // 约定：第一个参数通常就是需要自动填充的实体对象。
        Object entity = args[0];
        // 准备要填充到实体中的公共字段数据。
        // 当前时间：用于 createTime / updateTime
        LocalDateTime localDateTime = LocalDateTime.now();
        // 当前登录用户 ID：用于 createUser / updateUser
        Long currentId = BaseContext.getCurrentId();
        // 根据不同的操作类型，通过反射给实体类调用对应的 setter 方法。
        if (operationType == OperationType.INSERT) {
            try {
                // 新增时，需要同时设置：
                // 1. 创建时间
                // 2. 修改时间
                // 3. 创建人
                // 4. 修改人
                // 这里之所以用反射，是因为不同实体类字段相同，但具体类型不同，
                // 可以统一按方法名来调用，避免每个实体都写一遍重复代码。
                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class).invoke(entity, localDateTime);
                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class).invoke(entity, localDateTime);
                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class).invoke(entity, currentId);
                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class).invoke(entity, currentId);
            } catch (Exception e) {
                // 反射调用失败时，直接抛运行时异常，让问题暴露出来，避免悄悄失败。
                throw new RuntimeException(e);
            }
        } else if (operationType == OperationType.UPDATE) {
            try {
                // 修改时，只需要更新：
                // 1. 修改时间
                // 2. 修改人
                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class).invoke(entity, localDateTime);
                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class).invoke(entity, currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
