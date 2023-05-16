package com.yhy.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yhy.takeout.common.Result;
import com.yhy.takeout.entity.SetmealDish;
import com.yhy.takeout.entity.ShoppingCart;
import com.yhy.takeout.service.ShoppingCartService;
import com.yhy.takeout.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/shoppingCart")
@Slf4j
@RestController
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据:{}",shoppingCart);

        //设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = ThreadLocalUtil.getCurrentId();
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        if(dishId != null){
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);

        }else{
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //查询当前菜品或者套餐是否在购物车中
        //SQL:select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        if(cartServiceOne != null){
            //如果已经存在，就在原来数量基础上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        }else{
            //如果不存在，则添加到购物车，数量默认就是一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }

        return Result.success(cartServiceOne);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        log.info("查看购物车...");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,ThreadLocalUtil.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return Result.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public Result<String> clean(){
        //SQL:delete from shopping_cart where user_id = ?

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,ThreadLocalUtil.getCurrentId());

        shoppingCartService.remove(queryWrapper);

        return Result.success("清空购物车成功");
    }

    /**
     * 删减购物车商品的数量
     * @param setmealDish
     * @return
     */
    @PostMapping("/sub")
    public Result<ShoppingCart> sub(@RequestBody SetmealDish setmealDish){
        // 当前是哪个用户的购物车数据
        Long currentId = ThreadLocalUtil.getCurrentId();

        Long dishId = setmealDish.getDishId();

        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);

        if(dishId != null){
            // 购物车的是菜品
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            // 购物车的是套餐
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId,setmealDish.getSetmealId());
        }

        //查询当前菜品或者套餐是否在购物车中
        ShoppingCart cartServiceOne = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);

        if(cartServiceOne != null){
            //把数量-1
            Integer number = cartServiceOne.getNumber();
            if(number == 1){
                // 如果只有一个那么直接删除记录
                shoppingCartService.removeById(cartServiceOne.getId());
            } else {
                // 否则则数量-1
                cartServiceOne.setNumber(number - 1);
            }

            shoppingCartService.updateById(cartServiceOne);
        }

        return Result.success(cartServiceOne);
    }

}
