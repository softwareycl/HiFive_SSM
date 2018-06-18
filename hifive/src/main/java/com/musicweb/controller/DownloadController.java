package com.musicweb.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/download")
public class DownloadController {
	
	@RequestMapping(value = "/downloadSong", method = RequestMethod.GET)
	@ResponseBody
	public void download(HttpServletRequest request, HttpServletResponse response, int id) {
		request.getSession();
	}
	
}
