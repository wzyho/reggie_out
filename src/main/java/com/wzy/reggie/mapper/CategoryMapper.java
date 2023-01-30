package com.wzy.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzy.reggie.pojo.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    void updateStatus (@Param("name")String name,@Param("sort") Integer sort,@Param("id")Long id);

    void addID(@Param("name")String name,@Param("type")int type,@Param("sort") int sort);
}
