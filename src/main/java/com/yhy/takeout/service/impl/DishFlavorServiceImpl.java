package com.yhy.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhy.takeout.entity.DishFlavor;
import com.yhy.takeout.mapper.DishFlavorMapper;
import com.yhy.takeout.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
