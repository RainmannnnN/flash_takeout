package com.yhy.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhy.takeout.dto.DishDto;
import com.yhy.takeout.dto.SetmealDto;
import com.yhy.takeout.entity.*;
import com.yhy.takeout.exception.CustomException;
import com.yhy.takeout.mapper.SetmealMapper;
import com.yhy.takeout.service.SetmealDishService;
import com.yhy.takeout.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;


    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);

    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // 查询套餐状态，看是否可以删除
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId, ids);
        lambdaQueryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(lambdaQueryWrapper);
        if (count > 0) {
            // 如果不能删除，抛出异常
            throw new CustomException("套餐在售卖中，不能删除");
        }
        // 可以删除，先删除套餐表中的数据
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        // 删除关系表中的数据
        setmealDishService.remove(dishLambdaQueryWrapper);
    }

    @Override
    public SetmealDto getByIdWithDishes(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();

        BeanUtils.copyProperties(setmeal, setmealDto);

        // 获取套餐有什么餐品
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());

        List<SetmealDish> dishes = setmealDishService.list(setmealDishLambdaQueryWrapper);
        setmealDto.setSetmealDishes(dishes);

        return setmealDto;
    }
}
