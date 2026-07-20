package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
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
    @Autowired
    private SetmealDishMapper setMealDishMapper;

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

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO 分页查询条件
     * @return 分页结果
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除菜品：
     * 1. 一次能删除 1 个或多个菜品
     * 2. 启售中的菜品不能删除
     * 3. 被套餐关联的菜品不能删除
     * 4. 删除菜品时，同时删除菜品口味数据
     * @param ids 菜品id列表
     */
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        // 1. 查询这些菜品是否有启售中的
        int count = dishMapper.countByIdsAndStatus(ids, 1);
        if(count > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }
        // 2. 查询这些菜品是否被套餐关联
        int count2 = setMealDishMapper.countByIdsInSetmeal(ids);
        if(count2 > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        // 3. 删除菜品口味表数据
        dishFlavorMapper.deleteBatchByDishIds(ids);
        // 4. 删除菜品表数据
        dishMapper.deleteBatch(ids);
        // 5. TODO 删除阿里 OSS 中对应的图片
    }
}
