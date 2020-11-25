package com.xuecheng.manage_course.dao;
import com.xuecheng.framework.domain.course.Student;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface StudentMapper {

    public void updateData(@Param("name") String name);

    public List<Student> selectData();
}
