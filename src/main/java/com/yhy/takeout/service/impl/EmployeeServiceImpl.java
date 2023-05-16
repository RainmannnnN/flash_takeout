package com.yhy.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhy.takeout.entity.Employee;
import com.yhy.takeout.mapper.EmployeeMapper;
import com.yhy.takeout.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
