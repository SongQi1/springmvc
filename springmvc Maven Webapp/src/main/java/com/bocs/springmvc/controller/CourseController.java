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
	
	//������������/courses/view?courseId=xxx��ʽ������
	@RequestMapping(value="/view", method = { RequestMethod.POST, RequestMethod.GET })
	public String viewCourse(@RequestParam("courseId") Integer courseId, Model model){
		log.debug("In viewCourse, courseId= "+courseId);
		Course course = courseService.getCourseById(courseId);
		model.addAttribute(course);
		return "course_overview";
	}
	
	//������������/courses/view2/xxx��ʽ������
	@RequestMapping(value="/view2/{courseId}", method={ RequestMethod.POST, RequestMethod.GET})
	public String viewCourse2(@PathVariable("courseId") Integer courseId, Map<String, Object> model){
		log.debug("In viewCourse2, courseId= "+courseId);
		Course course = courseService.getCourseById(courseId);
		model.put("course", course);
		return "course_overview";
	}
	
	//������������/courses/view?courseId=xxx��ʽ������
	@RequestMapping(value="/view3", method = { RequestMethod.POST, RequestMethod.GET })
	public String viewCourse3(HttpServletRequest request){
		Integer courseId = Integer.valueOf(request.getParameter("courseId"));
		Course course = courseService.getCourseById(courseId);
		request.setAttribute("course", course);
		return "course_overview";
	}
	
	//������������/courses/admin?add����
	@RequestMapping(value="/admin", method = { RequestMethod.POST, RequestMethod.GET },params="add")
	public String createCoursePage(){
		return "course_admin/edit";
	}
	
	//������������/courses/save����
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public String saveCourse(Course course){
		log.debug("Info of course:");
		log.debug(ReflectionToStringBuilder.toString(course));
		//�ڴ˴�����ҵ��������������ݿ�־û�
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
	
	
	//������������/courses/xxxx�����󣬲�����һ��json���ݸ�ʽ
	@RequestMapping(value="/{courseId}",method=RequestMethod.GET)
	public @ResponseBody Course getCourseInJson(@PathVariable Integer courseId){
		return  courseService.getCourseById(courseId);
	}
	
	//������������/courses/jsontype/xxxx�����󣬲�����һ��json���ݸ�ʽ
	@RequestMapping(value="/jsontype/{courseId}",method=RequestMethod.GET)
	public  ResponseEntity<Course> getCourseInJson2(@PathVariable Integer courseId){
		Course course =   courseService.getCourseById(courseId);		
		return new ResponseEntity<Course>(course, HttpStatus.OK);
	}
}
