package com.yhy.takeout.dto;

import com.yhy.takeout.entity.Dish;
import com.yhy.takeout.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    // 菜品的口味数据
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
