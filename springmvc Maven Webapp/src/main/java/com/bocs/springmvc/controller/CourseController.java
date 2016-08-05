package com.bocs.springmvc.controller;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.bocs.springmvc.model.Course;
import com.bocs.springmvc.service.CourseService;

@Controller
@RequestMapping("/courses")
public class CourseController {
	
	private static Logger log = Logger.getLogger(CourseController.class);
	
	@Resource
	private CourseService courseService;
	
	//本方法将处理/courses/view?courseId=xxx形式的请求
	@RequestMapping(value="/view", method = { RequestMethod.POST, RequestMethod.GET })
	public String viewCourse(@RequestParam("courseId") Integer courseId, Model model){
		log.debug("In viewCourse, courseId= "+courseId);
		Course course = courseService.getCourseById(courseId);
		model.addAttribute(course);
		return "course_overview";
	}
	
	//本方法将处理/courses/view2/xxx形式的请求
	@RequestMapping(value="/view2/{courseId}", method={ RequestMethod.POST, RequestMethod.GET})
	public String viewCourse2(@PathVariable("courseId") Integer courseId, Map<String, Object> model){
		log.debug("In viewCourse2, courseId= "+courseId);
		Course course = courseService.getCourseById(courseId);
		model.put("course", course);
		return "course_overview";
	}
	
	//本方法将处理/courses/view?courseId=xxx形式的请求
	@RequestMapping(value="/view3", method = { RequestMethod.POST, RequestMethod.GET })
	public String viewCourse3(HttpServletRequest request){
		Integer courseId = Integer.valueOf(request.getParameter("courseId"));
		Course course = courseService.getCourseById(courseId);
		request.setAttribute("course", course);
		return "course_overview";
	}
	
	//本方法将处理/courses/admin?add请求
	@RequestMapping(value="/admin", method = { RequestMethod.POST, RequestMethod.GET },params="add")
	public String createCoursePage(){
		return "course_admin/edit";
	}
	
	//本方法将处理/courses/save请求
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public String saveCourse(Course course){
		log.debug("Info of course:");
		log.debug(ReflectionToStringBuilder.toString(course));
		//在此处进行业务操作，比如数据库持久化
		course.setCourseId(333);
	//	return "redirect:view2/"+ course.getCourseId();
		return "forward:view2/"+ course.getCourseId();
	}
	
	@RequestMapping(value="/showUploadPage",method=RequestMethod.GET)
	public String showUploadPage(){
		return "course_admin/file";
	}
	@RequestMapping(value="/uploadFile",method=RequestMethod.POST)
	public String uploadFile(MultipartFile file, HttpServletRequest request) throws IOException{
		String root = request.getServletContext().getRealPath("/");
		if(!file.isEmpty()){
			log.debug("OriginalFilename:" + file.getOriginalFilename());
			log.debug("ContentType:" + file.getContentType());
			log.debug("Name:" + file.getName());
			FileUtils.copyInputStreamToFile(file.getInputStream(), new File(root + file.getOriginalFilename()));
		}
		return "success";
	}
	
	@RequestMapping(value="/doUpload2", method=RequestMethod.POST)
	public String doUploadFile2(MultipartHttpServletRequest multiRequest, HttpServletRequest request) throws IOException{
		String root = request.getServletContext().getRealPath("/");
		Iterator<String> filesNames = multiRequest.getFileNames();
		while(filesNames.hasNext()){
			String fileName =filesNames.next();
			MultipartFile file =  multiRequest.getFile(fileName);
			if(!file.isEmpty()){
				log.debug("OriginalFilename:" + file.getOriginalFilename());
				log.debug("ContentType:" + file.getContentType());
				log.debug("Name:" + file.getName());
				FileUtils.copyInputStreamToFile(file.getInputStream(), new File(root + file.getOriginalFilename()));
			}
			
		}
		
		return "success";
	}
	
	
	//本方法将处理/courses/xxxx的请求，并返回一个json数据格式
	@RequestMapping(value="/{courseId}",method=RequestMethod.GET)
	public @ResponseBody Course getCourseInJson(@PathVariable Integer courseId){
		return  courseService.getCourseById(courseId);
	}
	
	//本方法将处理/courses/jsontype/xxxx的请求，并返回一个json数据格式
	@RequestMapping(value="/jsontype/{courseId}",method=RequestMethod.GET)
	public  ResponseEntity<Course> getCourseInJson2(@PathVariable Integer courseId){
		Course course =   courseService.getCourseById(courseId);		
		return new ResponseEntity<Course>(course, HttpStatus.OK);
	}
}
