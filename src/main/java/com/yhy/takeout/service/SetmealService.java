package com.yhy.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yhy.takeout.dto.SetmealDto;
import com.yhy.takeout.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时保存套餐和菜品的关联
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    void removeWithDish(List<Long> ids);

    /**
     * 根据id查询套餐信息和口味
     * @param id
     * @return
     */
    SetmealDto getByIdWithDishes(Long id);
}
