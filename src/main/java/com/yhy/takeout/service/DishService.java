package com.yhy.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yhy.takeout.dto.DishDto;
import com.yhy.takeout.entity.Dish;


public interface DishService extends IService<Dish> {

    /**
     * 新增菜品，同时插入菜品对应的口味数据
     * @param dishDto
     */
    void addWithFlavor(DishDto dishDto);

    /**
     * 根据id来查询菜品信息和口味信息
     * @param id
     * @return
     */
    DishDto getByIdWithFlavor(Long id);

    /**
     * 更新菜品和口味信息
     * @param dishDto
     */
    void updateWithFlavor(DishDto dishDto);
}
