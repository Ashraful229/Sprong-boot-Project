package com.emon.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.emon.dao.ContactRepository;
import com.emon.dao.UserRepository;
import com.emon.entities.Contact;
import com.emon.entities.User;
import com.emon.helper.Message;






@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@ModelAttribute
	public void passCommondataToView(Model model,Principal principal) {
		String userName=  principal.getName();
		User user=	 userRepository.getUserByUserNamr(userName);
		model.addAttribute("user",user);
	}
	
	@RequestMapping("/index")
	public String dashboard(Model model)
	{
	 
	model.addAttribute("title","UserProfile");
	
	return "normal/user_dashboard";
	}

	//open add from handler
	@GetMapping("/addcontact")
	public String openAddContactForm(Model model)
	{
		 
		model.addAttribute("title","AddContact");
		model.addAttribute("contact",new Contact());
		return "normal/add_contact_form";
	}
	
	//processing add contacr from
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,
		@RequestParam("profileImage") MultipartFile file,
		Principal principal,
		HttpSession session )
	{
		try {
			String name=principal.getName();
		    User user=	this.userRepository.getUserByUserNamr(name);
		    
		    //pic uplode
		    if(file.isEmpty())
		    {
		    	//throw new Exception();
		    }
		    else {
				//uplode file to folder ad update name to contact
		    	contact.setImage(file.getOriginalFilename());
		        File savefile=	new ClassPathResource("static/img").getFile();
		        Path path=  Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
		        Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
			}
		     
		    
		    contact.setUser(user);
		    user.getContacts().add(contact);
		    this.userRepository.save(user); 
	 		System.out.println(contact);
			//success message
	 		session.setAttribute("message", new Message("Successfully added", "success"));
			
		} catch (Exception e) {
			// TODO: handle exception
			//error message
			session.setAttribute("message", new Message("Something wrong", "danger"));
			
		}
		return "normal/add_contact_form";
		
	}
	
	//show contacts
	
	@GetMapping("/show-contacts")
	public String showContacts(Model m,Principal principal)
	{
		m.addAttribute("title","Show Contacts");
		String userName=principal.getName();
		System.out.println(userName);
	   User user=this.userRepository.getUserByUserNamr(userName);
		
	   List<Contact> contacts =	this.contactRepository.findContactsByUser(user.getId());
	  
	  m.addAttribute("contacts",contacts);
		
		return "normal/show_contacts";
	}
	 
}
