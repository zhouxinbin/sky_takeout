package com.sky.service;

import com.sky.dto.CategoryDTO;

public interface CategoryService {
    /**
     * 新建分类
     * @param categoryDTO
     */
    void insert(CategoryDTO categoryDTO);
}
