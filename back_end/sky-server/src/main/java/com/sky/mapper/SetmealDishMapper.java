package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 套餐菜品对应表
 */
@Mapper
public interface SetmealDishMapper {
    /**
     * 根据 ids 查询被套餐关联的菜品数量
     * @param ids
     * @return
     */
    int countByIdsInSetmeal(List<Long> ids);
}
