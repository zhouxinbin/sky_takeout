package com.sky.service;

import com.sky.dto.DishDTO;

/**
 * 菜品管理接口
 */
public interface DishService {
    /**
     * 新增菜品，同时保存口味数据
     * @param dishDTO 菜品信息
     */
    public void saveWithFlavor(DishDTO dishDTO);
}
