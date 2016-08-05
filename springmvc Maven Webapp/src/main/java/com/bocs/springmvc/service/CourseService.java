package com.bocs.springmvc.service;

import org.springframework.transaction.annotation.Transactional;

import com.bocs.springmvc.model.Course;

@Transactional
public interface CourseService {

	Course getCourseById(Integer courseId);
}
