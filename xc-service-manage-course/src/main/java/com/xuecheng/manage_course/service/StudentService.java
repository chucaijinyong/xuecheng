package com.xuecheng.manage_course.service;

import com.xuecheng.manage_course.dao.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-09-12 18:32
 **/
@Service
public class StudentService {

    @Autowired
    StudentMapper studentRepository;
    public void updateData(String name){
        studentRepository.updateData(name);
    }
}
