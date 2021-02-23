package com.emon.controller;

import java.security.Principal;
import java.util.List;

import javax.swing.table.TableStringConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.emon.dao.ContactRepository;
import com.emon.dao.UserRepository;
import com.emon.entities.Contact;
import com.emon.entities.User;

@RestController
public class SearchController {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal)
	{
		User user=this.userRepository.getUserByUserNamr(principal.getName());
		List<Contact> contact= this.contactRepository.findByNameContainingAndUser(query,user);
		return ResponseEntity.ok(contact);
	}

}
