package com.emon.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.emon.dao.UserRepository;
import com.emon.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal)
	{
	 String userName=  principal.getName();
	 System.out.println(userName);
		 
	 User user=	 userRepository.getUserByUserNamr(userName);
	model.addAttribute("user",user);
	model.addAttribute("title","UserProfile");
	 System.out.println(user);
	return "normal/user_dashboard";
	}

}
