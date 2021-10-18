package com.douzone.hellospring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author user
 * @RequestMapping 클래스(타입) + 핸들러(메소드)
 * 
 */

@Controller
@RequestMapping("/user")
public class UserController {
	
	@RequestMapping(value="/join", method=RequestMethod.GET)
	public String join() {
		return "/WEB-INF/views/join.jsp";
	}

	@RequestMapping(value="/join", method=RequestMethod.POST)
	public String join(UserVo vo) {
		System.out.println(vo);
		return "redirect:/";
	}
	
	@ResponseBody
	@RequestMapping("/update")
	public String update(@RequestParam("n") String name) { //n으로 파라미터 넘어옴
		System.out.println(name);
		/**
		 * 만일 n이라는 이름의 파라미터가 없는 경우
		 * 400 bad request 에러 발생
		 */
		return "UserController.update()";
	}
	
	@ResponseBody
	@RequestMapping("/update2")
	public String update2(@RequestParam(value="n", required=true, defaultValue="") String name,
			@RequestParam(value="a", required=true, defaultValue="0") int age) {
		
			System.out.println("-----" + name + "-----");
			System.out.println(age);
			return "UserController.update()";
	}
	
}