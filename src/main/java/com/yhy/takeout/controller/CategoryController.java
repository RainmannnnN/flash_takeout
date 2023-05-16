package com.yhy.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhy.takeout.common.Result;
import com.yhy.takeout.entity.Category;
import com.yhy.takeout.entity.Employee;
import com.yhy.takeout.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody Category category){
        log.info("add new category successfully!");
        categoryService.save(category);
        return Result.success("新增分类成功！");
    }

    /**
     * 种类信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name){
        // 构造分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);
        // 构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 添加排序条件 和 过滤条件
        queryWrapper.orderByAsc(Category::getSort);
        // 执行查询
        categoryService.page(pageInfo, queryWrapper);
        return Result.success(pageInfo);
    }

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    public Result<String> delete(Long ids){
//        categoryService.removeById(ids);
        categoryService.remove(ids);
        return Result.success("分类信息删除成功！");

    }

    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody Category category){

        categoryService.updateById(category);

        return Result.success("更新数据成功！");
    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> list(Category category){
        // 条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加条件
        lambdaQueryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        // 添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(lambdaQueryWrapper);
        return Result.success(list);
    }

}
