package com.yhy.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhy.takeout.dto.DishDto;
import com.yhy.takeout.entity.Dish;
import com.yhy.takeout.entity.DishFlavor;
import com.yhy.takeout.mapper.DishMapper;
import com.yhy.takeout.service.DishFlavorService;
import com.yhy.takeout.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void addWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息
        this.save(dishDto);

        Long dishDtoId = dishDto.getId();
        // 菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDtoId);
            return item;
        }).collect(Collectors.toList());

        // 保存口味到dish_flavor表
        dishFlavorService.saveBatch(flavors);
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 查询菜品基本信息，dish表
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish, dishDto);

        // 查询当前菜品对应的口味信息，dishFlavor表
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新dish的基本信息
        this.updateById(dishDto);
        // 清理当前菜品对应口味数据
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);

        // 添加当前提交过来的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }
}
