package com.kjjd.community.community.controller;

import com.kjjd.community.community.util.CommunityUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot";
    }
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            System.out.println(name+":"+value);
        }
        System.out.println(request.getParameter("name"));
        response.setContentType("text/html;charset=utf-8");
        try{
            PrintWriter writer = response.getWriter();
            writer.write("<h1>牛客网</h1>");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @RequestMapping(path="students",method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(@RequestParam(name = "current",required = false, defaultValue = "1") int current,
                              @RequestParam(name = "limit",required = false, defaultValue = "20") int limit){
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }
    @RequestMapping(path="/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "a student";
    }
    @RequestMapping(path="/student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }
    @RequestMapping(path="/teacher",method = RequestMethod.GET)
    public ModelAndView saveTeacher()
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name","jjd");
        modelAndView.addObject("age","20");
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }
    @RequestMapping(path="/school",method = RequestMethod.GET)
    public String getSchool(Model model)
    {
        model.addAttribute("name","hdu");
        model.addAttribute("age","20");
        return "/demo/view";
    }
    @RequestMapping(path="/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getemp()
    {
        Map<String,Object> map=new java.util.HashMap<>();
        map.put("name","jjd");
        map.put("age","20");
        return map;
    }
    @RequestMapping(path="/emps",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getemps()
    {
        Map<String,Object> map=new java.util.HashMap<>();
        map.put("name","jjd");
        map.put("age","20");
        List<Map<String,Object>> mapList=new java.util.ArrayList<>();
        mapList.add(map);
        map=new java.util.HashMap<>();
        map.put("name","hdu");
        map.put("age","20");
        mapList.add(map);
        return mapList;
    }
    @RequestMapping(path="/cookie/set")
    @ResponseBody
    public String setCookie(HttpServletResponse httpServletResponse)
    {
        Cookie cookie=new Cookie("code", CommunityUtil.generateUUID());
        //生效范围
        cookie.setPath("/community/alpha");
        cookie.setMaxAge(10*60);
        httpServletResponse.addCookie(cookie);

        return "set cookie";
    }
    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code) {
        System.out.println(code);
        return "get cookie";
    }
    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session) {
        session.setAttribute("name","2c1");
        session.setAttribute("age","11");
        return "set session";
    }
    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) {
        System.out.println(session.getAttribute("age"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

}