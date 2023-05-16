package com.yhy.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhy.takeout.common.Result;
import com.yhy.takeout.entity.Employee;
import com.yhy.takeout.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        // 加密密码
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 根据username查询用户
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 如果没查询到或者密码不一致则返回错误结果
        if (emp == null) {
            return Result.error("user not exit!");
        }

        if (!emp.getPassword().equals(password)) {
            return Result.error("password error!");
        }

        // 查看员工状态是否可用
        if(emp.getStatus() == 0){
            return Result.error("the account is banned!");
        }

        request.getSession().setAttribute("employee", emp.getId());
        return Result.success(emp);

    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
        // 清理session中的员工数据
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功!");
    }

    /**
     * 新增员工
     * @param employee
     * @param request
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody Employee employee, HttpServletRequest request){
        log.info("add employee!");
        // 初始密码设置为123456
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        // 获得当前登录用户的id
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);
        return Result.success("update user successfully!");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name){
        // 构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        // 构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // 添加排序条件 和 过滤条件
        queryWrapper.like(!StringUtils.isEmpty(name), Employee::getName, name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        // 执行查询
        employeeService.page(pageInfo, queryWrapper);
        return Result.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @param request
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody Employee employee, HttpServletRequest request){

//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);

        employeeService.updateById(employee);
        return Result.success("employee info update successfully!");
    }

    /**
     * 根据id查询信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if(null != employee){
            return Result.success(employee);
        }
        return Result.error("can not find info of employee!");

    }

}
