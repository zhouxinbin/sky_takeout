package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 菜品管理实现类
 */
@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品，同时保存口味数据
     * @param dishDTO 菜品信息
     */
    @Override
    @Transactional // 多表操作，开启事务
    public void saveWithFlavor(DishDTO dishDTO) {
        // 向菜品表添加 1 条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);
        // 获取 DishMapper.XML 中 insert 之后返回的菜品id
        Long dishId = dish.getId();
        // 循环设置菜品口味的菜品id
        for(DishFlavor flavor : dishDTO.getFlavors()) {
            flavor.setDishId(dishId);
        }
        // 向菜品口味表添加多条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && !flavors.isEmpty()) {
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
