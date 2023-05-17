package com.yhy.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhy.takeout.common.Result;
import com.yhy.takeout.dto.SetmealDto;
import com.yhy.takeout.entity.Category;
import com.yhy.takeout.entity.Setmeal;
import com.yhy.takeout.service.CategoryService;
import com.yhy.takeout.service.SetmealDishService;
import com.yhy.takeout.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/setmeal")
@RestController
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public Result<String> save(@RequestBody SetmealDto setmealDto){

        setmealService.saveWithDish(setmealDto);

        return Result.success("新增套餐成功！");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name){

        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null, Setmeal::getName, name);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, lambdaQueryWrapper);

        // 拷贝对象
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            // 拷贝对象
            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId();
            // 根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String name1 = category.getName();
                setmealDto.setCategoryName(name1);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return Result.success(dtoPage);
    }

    /**
     * 删除套餐信息，只能删除停售的套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public Result<String> delete(@RequestParam List<Long> ids){

        setmealService.removeWithDish(ids);

        return Result.success("删除套餐成功！");
    }

    /**
     * 根据条件查询套餐数据，Get请求不容RequestBody
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    public Result<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        setmealLambdaQueryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(setmealLambdaQueryWrapper);

        return Result.success(list);
    }

    /**
     * 根据id查询套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SetmealDto> get(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDishes(id);

        return Result.success(setmealDto);
    }

    /**
     * 根据id修改套餐信息
     * @param setmeal
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody Setmeal setmeal){
        setmealService.updateById(setmeal);
        return Result.success("修改成功！");
    }

    /**
     * 根据id，改变状态
     * @param info
     * @param ids
     * @return
     */
    @PostMapping("/status/{info}")
    public Result<List<Setmeal>> change(@PathVariable Integer info, @RequestParam List<Long> ids) {
        List<Setmeal> list = new ArrayList<>();
        for (Long id : ids) {
            Setmeal setmeal = setmealService.getById(id);
            if (setmeal != null) {
                setmeal.setStatus(info);
                setmealService.updateById(setmeal);
                list.add(setmeal);
            }

        }

        return Result.success(list);
    }


}
