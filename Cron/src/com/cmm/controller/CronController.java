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
	private static final boolean DEPLOY = true;

	private static final String SECRET = "cse403secret";
	private static final String FILTER_URL = "http://changemymood.herokuapp.com/api/filter/";
	private static final String COLLECT_URL = "http://changemymood.herokuapp.com/api/importsomepic/";
	static int total = 0;

	private static final String FILTER_URL_NOT_DEPLOY = "http://testcmm.herokuapp.com/api/filter/";
	private static final String COLLECT_URL_NOT_DEPLOY = "http://testcmm.herokuapp.com/api/importsomepic/";

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public String getCount(ModelMap model) {
		model.addAttribute("total", total);

		return "list";
	}

	@RequestMapping(value = "/pull", method = RequestMethod.GET)
	public String pullAndFilter(ModelMap model) {
		String url;
		if (DEPLOY) {
			url = COLLECT_URL;
		} else {
			url = COLLECT_URL_NOT_DEPLOY;
		}
		for (int i = 0; i < 5; i++) {
			doUrl(url, "HA", "PI");
			doUrl(url, "RO", "PI");
			doUrl(url, "EX", "PI");
			doUrl(url, "IN", "PI");
			doUrl(url, "HA", "VI");
			doUrl(url, "RO", "VI");
			doUrl(url, "EX", "VI");
			doUrl(url, "IN", "VI");
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return "list";
	}

	@RequestMapping(value = "/broken", method = RequestMethod.GET)
	public String brokenFilter(ModelMap model) {
		String url;
		if (DEPLOY) {
			url = FILTER_URL;
		} else {
			url = FILTER_URL_NOT_DEPLOY;
		}
		for (int i = 0; i < 5; i++) {
			doUrl(url, "HA", "PI", "broken");
			doUrl(url, "RO", "PI", "broken");
			doUrl(url, "EX", "PI", "broken");
			doUrl(url, "IN", "PI", "broken");
			doUrl(url, "HA", "VI", "broken");
			doUrl(url, "RO", "VI", "broken");
			doUrl(url, "EX", "VI", "broken");
			doUrl(url, "IN", "VI", "broken");
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return "list";
	}

	@RequestMapping(value = "/score", method = RequestMethod.GET)
	public String scoreFilter(ModelMap model) {
		String url;
		if (DEPLOY) {
			url = FILTER_URL;	
		} else {
			url = FILTER_URL_NOT_DEPLOY;
		}
		doUrl(url, "HA", "PI", "score");
		doUrl(url, "RO", "PI", "score");
		doUrl(url, "EX", "PI", "score");
		doUrl(url, "IN", "PI", "score");
		doUrl(url, "HA", "VI", "score");
		doUrl(url, "RO", "VI", "score");
		doUrl(url, "EX", "VI", "score");
		doUrl(url, "IN", "VI", "score");
		return "list";
	}

	// cron job running this
	@Deprecated
	@RequestMapping(value = "/upday/{num}", method = RequestMethod.GET)
	public String addCount(@PathVariable int num, ModelMap model) {
		// http://www.coderanch.com/t/356021/Servlets/java/Creating-HTTP-request-java-getting
		total += num;
		model.addAttribute("total", total);

		if (DEPLOY) {
			doUrl(FILTER_URL);
			doUrl(COLLECT_URL, "HA");
			doUrl(COLLECT_URL, "RO");
		} else {
			doUrl(FILTER_URL_NOT_DEPLOY, "HA");
			doUrl(FILTER_URL_NOT_DEPLOY, "RO");
			doUrl(COLLECT_URL_NOT_DEPLOY, "HA", "PI");
			doUrl(COLLECT_URL_NOT_DEPLOY, "RO", "PI");
			doUrl(COLLECT_URL_NOT_DEPLOY, "HA", "VI");
			doUrl(COLLECT_URL_NOT_DEPLOY, "RO", "VI");
		}

		return "list";
	}

	private void doUrl(String url) {
		doUrl(url, null, null, null);
	}

	private void doUrl(String url, String mood) {
		doUrl(url, mood, null, null);
	}

	private void doUrl(String url, String mood, String content) {
		doUrl(url, mood, content, null);
	}

	private void doUrl(String url, String mood, String content, String filter) {
		HttpURLConnection connection = null;
		PrintWriter outWriter = null;
		BufferedReader serverResponse = null;
		StringBuffer buff = new StringBuffer();
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();

			connection.setRequestMethod("POST");
			connection.setDoOutput(true);

			outWriter = new PrintWriter(connection.getOutputStream());

			buff.append("secret=");
			buff.append(URLEncoder.encode(SECRET, "UTF-8"));

			if (mood != null) {
				buff.append("&mood=");
				buff.append(URLEncoder.encode(mood, "UTF-8"));
			}

			if (content != null) {
				buff.append("&content=");
				buff.append(URLEncoder.encode(content, "UTF-8"));
			}

			if (filter != null) {
				buff.append("&filter=");
				buff.append(URLEncoder.encode(filter, "UTF-8"));
			}

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
