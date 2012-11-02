package com.cmm.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/cron")
public class CronController {
	private static final String SECRET = "cse403secret";
	private static final String FILTER_URL = "http://changemymood.herokuapp.com/api/filter/";
	private static final String COLLECT_URL = "http://changemymood.herokuapp.com/api/importsomepic/";
	static int total = 0;
	
	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public String getCount(ModelMap model) {
		model.addAttribute("total", total);

		return "list";
	}

	// cron job running this
	@RequestMapping(value = "/upday/{num}", method = RequestMethod.GET)
	public String addCount(@PathVariable int num, ModelMap model) {
		// http://www.coderanch.com/t/356021/Servlets/java/Creating-HTTP-request-java-getting
		total += num;
		model.addAttribute("total", total);

		doUrl(FILTER_URL);
		doUrl(COLLECT_URL);
		
		return "list";
	}

	private void doUrl(String url) {
		HttpURLConnection connection = null;
		PrintWriter outWriter = null;
		BufferedReader serverResponse = null;
		StringBuffer buff = new StringBuffer();
		try {
			connection = (HttpURLConnection) new URL(url)
					.openConnection();

			connection.setRequestMethod("POST");
			connection.setDoOutput(true);

			outWriter = new PrintWriter(connection.getOutputStream());

			buff.append("secret=");
			buff.append(URLEncoder.encode(SECRET, "UTF-8"));

			outWriter.print(buff.toString());
			outWriter.flush();
			outWriter.close();

			serverResponse = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = serverResponse.readLine()) != null) {

			}
		} catch (Exception e) {
			total = -1;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}

			if (serverResponse != null) {
				try {
					serverResponse.close();
				} catch (Exception ex) {
				}
			}
		}

	}
}
