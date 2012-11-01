package com.cmm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/cron")
public class CronController {

		static int total = 0;
		
		@RequestMapping(value="/count", method = RequestMethod.GET)
		public String getCount(ModelMap model) {
			model.addAttribute("total", total);
			
			return "list";
		}
		
		//cron job running this
		@RequestMapping(value="/addCount/{num}", method = RequestMethod.GET)
		public String addCount(@PathVariable int num, ModelMap model) {
			total += num;
			model.addAttribute("total", total);
			
			return "list";
		}
}
