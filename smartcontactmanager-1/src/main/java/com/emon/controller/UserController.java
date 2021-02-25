package com.emon.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
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
		    	contact.setImage("contact.png");
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
	
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m,Principal principal)
	{
		m.addAttribute("title","Show Contacts");
		String userName=principal.getName();
		System.out.println(userName);
	   User user=this.userRepository.getUserByUserNamr(userName);
	   
	 Pageable pageable =  PageRequest.of(page, 4);
		
	   Page<Contact> contacts =	this.contactRepository.findContactsByUser(user.getId(),pageable);
	  
	  m.addAttribute("contacts",contacts);
	  m.addAttribute("currentPage",page);
	  m.addAttribute("totalPages",contacts.getTotalPages());
		
		return "normal/show_contacts";
	}
	
	@RequestMapping("/contact/{cId}")
	public String showContactDetail(@PathVariable("cId") Integer cId,Model model,Principal principal,HttpSession session){
		
		try {
			System.out.println("CID"+cId);
			Optional<Contact> contactOptional =  this.contactRepository.findById(cId);

			Contact contact=contactOptional.get();
			//
			String userName = principal.getName();

			User user = this.userRepository.getUserByUserNamr(userName);
			
			
				if(user.getId()==contact.getUser().getId()){
					model.addAttribute("contact",contact);
					model.addAttribute("title",contact.getName());
				}
			
			
		} catch (Exception e) {
			// TODO: handle exception
			//session.setAttribute("message", new Message("No user Found", "danger"));
		}
		
		

		
		
	
	return "normal/contact_detail";
	}
	 
	//delete contact
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId,Model model,Principal principal,HttpSession session) {
	
		try {
			Optional<Contact> contactOptional= this.contactRepository.findById(cId);
			Contact contact=contactOptional.get();
			String userName = principal.getName();

			User user = this.userRepository.getUserByUserNamr(userName);
			
			
				if(user.getId()==contact.getUser().getId()){
				
			     this.contactRepository.delete(contact);
			     session.setAttribute("message", new Message("Delete Successfully", "success"));
				}
		} catch (Exception e) {
			// TODO: handle exception
			session.setAttribute("message", new Message("Delete not possible", "danger"));
		}
		
	
		
		
		return "redirect:/user/show-contacts/0";
	}
	
	//open update from
	@PostMapping("/update-contact/{cid}")
	public String updateFrom(@PathVariable("cid") Integer cid, Model m) {
		
		m.addAttribute("title","Update Contact");
		Contact contact= this.contactRepository.findById(cid).get();
		m.addAttribute("contact",contact);
		return "normal/update_from";
		
	}
	
	//update contact
	
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,HttpSession session,Principal principal) {
		try {
			
			//old contact details;
			Contact oldContactDetails=this.contactRepository.findById(contact.getcId()).get();
			//check image
			if(!file.isEmpty())
			{
				//old pic delete
				File deletefile=	new ClassPathResource("static/img").getFile();
				File file2=new File(deletefile,oldContactDetails.getImage());
				file2.delete();				
				//new photo uplode
				 File savefile=	new ClassPathResource("static/img").getFile();
			     Path path=  Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			     Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
			     contact.setImage(file.getOriginalFilename());

				session.setAttribute("message",new Message("Update Successfully", "success"));
				
			} 
			else
			{
				contact.setImage(oldContactDetails.getImage());
			}
			User user=this.userRepository.getUserByUserNamr(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return "redirect:/user/contact/"+contact.getcId();
	}
	
	//YOUR PROFILE
	@GetMapping("/profile")
	public String yourProfile(Model model)
	{
		model.addAttribute("title","Profile");
		return "normal/profile";
	}
	 
	//open password setting
	@GetMapping("/settings")
	public String openSettings()
	{
		return "normal/settings";
	}
	
	
	//change password handelr
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("CurrentPassword") String oldpassword,
			@RequestParam("NewPassword") String newpassword,
			Principal principal,HttpSession session)
	{
		System.out.println(oldpassword);
		System.out.println(newpassword);
		
		
		
		String userName=principal.getName();
	   User currentUser=this.userRepository.getUserByUserNamr(userName);
	   
	   if(this.bCryptPasswordEncoder.matches(oldpassword, currentUser.getPassword()))
	   {
		  System.out.println("ok"); 
		  
		  currentUser.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
		  this.userRepository.save(currentUser);
		  session.setAttribute("message", new Message("Password successfully changed", "success"));	
		  }
	   else {
		   
		   session.setAttribute("message", new Message("Old password is wrong", "danger"));	
		System.out.println("not ok");
		 return "redirect:/user/settings";
		 //return "normal/settings";
	}
		
	   return "redirect:/user/index";
	}
	
}
