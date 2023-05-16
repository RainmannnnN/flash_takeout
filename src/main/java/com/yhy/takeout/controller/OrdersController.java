package com.yhy.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhy.takeout.common.Result;

import com.yhy.takeout.entity.Orders;
import com.yhy.takeout.service.OrdersService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return Result.success("下单成功");
    }

    /**
     * 订单查询页面
     * 这个功能还没有完善，在订单页面的价格显示有问题
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public Result<Page> page(int page, int pageSize){
        // 构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        // 构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        // 添加排序条件 和 过滤条件
        queryWrapper.orderByDesc(Orders::getOrderTime);
        // 执行查询
        ordersService.page(pageInfo, queryWrapper);
//        ordersService.getById(Orders::getUserId, )
        return Result.success(pageInfo);
    }

    /**
     * 订单明细
     * 这里订单显示不了用户名字，应该要进行值的拷贝用OrderDto
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, Long number, String beginTime, String endTime){
        // 构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        // 构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        // 添加排序条件 和 过滤条件
        queryWrapper.like(!StringUtils.isEmpty(number), Orders::getId, number);
        queryWrapper.orderByDesc(Orders::getOrderTime);

//        List<Orders> list = ordersService.list(queryWrapper);
//
//        List<OrdersDto> ordersDtoList = list.stream().map((item) -> {
//            OrdersDto ordersDto = new OrdersDto();
//            BeanUtils.copyProperties(item, ordersDto);
//            String consignee = item.getConsignee();
//            ordersDto.setConsignee(consignee);
//
//            return ordersDto;
//        }).collect(Collectors.toList());

        // 执行查询
        ordersService.page(pageInfo, queryWrapper);

        return Result.success(pageInfo);
    }

    /**
     * 订单状态改变
     * @return
     */
    @PutMapping
    public Result<String> send(@RequestBody Orders orders){
        // 查询到具体订单
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(Orders::getId, orders.getId());
        Orders orders1 = ordersService.getOne(ordersLambdaQueryWrapper);
        Integer status = orders1.getStatus();
        if (status == 2){
            // 发货
            orders1.setStatus(3);
        } else if (status == 3){
            // 订单完成
            orders1.setStatus(4);
        } else {
            orders1.setStatus(5);
        }

        ordersService.updateById(orders1);
        return Result.success("订单状态已改变！");
    }

    /**
     * 再来一单
     * @param id
     * @return
     */
    @PostMapping("/again")
    public Result<String> again(Long id){
//        Orders orders = new Orders();
//        Orders byId = ordersService.getById(id);
//        BeanUtils.copyProperties(byId, orders, "");
        return Result.success("再来一单！");
    }
}
