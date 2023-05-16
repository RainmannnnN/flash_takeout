package com.yhy.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhy.takeout.entity.Category;
import com.yhy.takeout.entity.Dish;
import com.yhy.takeout.entity.Setmeal;
import com.yhy.takeout.exception.CustomException;
import com.yhy.takeout.mapper.CategoryMapper;
import com.yhy.takeout.service.CategoryService;
import com.yhy.takeout.service.DishService;
import com.yhy.takeout.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前要判断该分类下有没有菜品
     * @param id
     */
    public void remove(Long id){
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加条件查询，根据分类的id来查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count = dishService.count(dishLambdaQueryWrapper);
        // 查询该分类是否关联了菜品，如果已经关联则抛出异常
        if(count > 0){
            // 抛出异常
            throw new CustomException("当前分类项关联了菜品，不能删除！");
        }

        // 查询分类是否关联套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        count = setmealService.count(setmealLambdaQueryWrapper);
        if (count > 0) {
            // 抛出异常
            throw new CustomException("当前分类项关联了套餐，不能删除！");
        }

        // 正常删除分类
        super.removeById(id);
    }

}
