package com.yhy.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhy.takeout.common.Result;
import com.yhy.takeout.dto.DishDto;
import com.yhy.takeout.entity.Category;
import com.yhy.takeout.entity.Dish;
import com.yhy.takeout.entity.DishFlavor;
import com.yhy.takeout.entity.Setmeal;
import com.yhy.takeout.service.CategoryService;
import com.yhy.takeout.service.DishFlavorService;
import com.yhy.takeout.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public Result<String> add(@RequestBody DishDto dishDto){

        dishService.addWithFlavor(dishDto);

        return Result.success("添加菜品成功！");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name){
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        // 条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        // 添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        // 执行分页查询
        dishService.page(dishPage, queryWrapper);
        // 对象拷贝
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        List<Dish> records = dishPage.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId(); // 分类id
            // 根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return Result.success(dishDtoPage);

    }

    /**
     * 根据id查询菜品信息和对应口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishDto> get(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return Result.success(dishDto);
    }

    /**
     * 修改菜品信息
     * @return
     */
    @PutMapping
    public Result<String> save(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return Result.success("更改信息成功！");
    }

    /**
     * 根据查询条件查询对应的菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish){
        List<DishDto> listDto = null;

        // 动态构造key
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        // 从redis中获取缓存数据
        listDto = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if (listDto != null) {
            // 缓存中存在则直接返回
            return Result.success(listDto);
        }

        //不存在则继续查询
        // 构造查询条件
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        // 只查询状态为1的
        lambdaQueryWrapper.eq(Dish::getStatus, 1);
        // 添加排序条件
        lambdaQueryWrapper.orderByDesc(Dish::getSort).orderByAsc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(lambdaQueryWrapper);

        listDto = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId(); // 分类id
            // 根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            // 当前菜品的id
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, id);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());

        // 把结果缓存到redis里,30s
        redisTemplate.opsForValue().set(key, listDto, 30, TimeUnit.SECONDS);

        return Result.success(listDto);
    }

    /**
     * 更改状态
     * @param info
     * @param ids
     * @return
     */
    @PostMapping("/status/{info}")
    public Result<List<Dish>> change(@PathVariable Integer info, @RequestParam List<Long> ids) {
        List<Dish> list = new ArrayList<>();
        for (Long id : ids) {
            Dish dish = dishService.getById(id);
            if (dish != null) {
                dish.setStatus(info);
                dishService.updateById(dish);
                list.add(dish);
            }

        }

        return Result.success(list);
    }

    /**
     * 删除餐品信息
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids){

        dishService.removeByIds(ids);

        return Result.success("删除菜品成功！");
    }

}
