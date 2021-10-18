package com.douzone.hellospring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author user
 * 클래스(타입) 단독 매핑
 */

@Controller
@RequestMapping("/guestbook/*")
public class GuestbookController {
	
	@ResponseBody
	@RequestMapping // url을 안넣는게 아니라 메소드로 대체
	public String list() {
		return "GuestBookController.list()";
	}
	
	@ResponseBody
	@RequestMapping 
	public String delete() {
		return "GuestBookController.delete()";
	}

}
