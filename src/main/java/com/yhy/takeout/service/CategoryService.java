package com.yhy.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yhy.takeout.entity.Category;

public interface CategoryService extends IService<Category> {

    void remove(Long id);
}
