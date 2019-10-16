package com.progresee.app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class OpenController {

	@RequestMapping(value = "/googlebc08507e895b1822", method = RequestMethod.GET)
	public ModelAndView method2() {
		return new ModelAndView("redirect:"
				+ "http://progresee-env-1.bfenzgpjz4.eu-central-1.elasticbeanstalk.com/googlebc08507e895b1822.html");
	}
}
