package com.wzy.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzy.reggie.common.R;
import com.wzy.reggie.mapper.EmployeeMapper;
import com.wzy.reggie.pojo.Employee;
import com.wzy.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
/*
员工管理页面
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    EmployeeMapper employeeMapper;
    /*
        员工登录功能
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //这部分就是MP
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(lqw);
        if (emp == null) {
            return R.error("登陆失败");
        }
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }
        if (emp.getStatus() == 0) {
            return R.error("该用户已被禁用");
        }
        //存个Session，只存个id就行了
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }
        /*
        员工退出功能
        */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /*
        添加员工信息
     */
    @PostMapping()
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        //设置默认密码为123456，并采用MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //设置createTime和updateTime
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //根据session来获取创建人的id
        Long empId = (Long) request.getSession().getAttribute("employee");
        //并设置
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);
        //存入数据库
        employeeService.save(employee);
        return R.success("添加员工成功");
    }

    /**
     *
     *员工分页查询
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){

        //构造分页构造器
        Page pageInFo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInFo,queryWrapper);
        return R.success(pageInFo);
    }

    /**
     * 根据id修改员工信息、禁用和启用
     * @param
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee){

        Long id = employee.getId();
        Integer status = employee.getStatus();
        employeeMapper.updateStatus(status,id);
        employee.setUpdateUser(id);
        employeeService.updateById(employee);
        return R.success("修改成功");
    }
    /**
     * 根据id来查询员工信息
     */

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee id1 = employeeService.getById(id);
        if (id1 != null){
            return R.success(id1);
        }
        return R.error("没有查到员工信息");
    }
}
