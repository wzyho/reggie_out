package com.wzy.reggie.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzy.reggie.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

    void updateStatus(@Param("status") int status, @Param("id") Long id);

}
