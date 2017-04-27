package com.controller;

import java.io.*;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beans.*;
import com.google.gson.Gson;
import com.utils.LabUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utils.HibernateUtil;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value="/user")
public class UserController {

	private SessionFactory sessionFactory=HibernateUtil.getSessionFactory();
	//用户登录
	@RequestMapping(value="/login")
	@ResponseBody
 	public Chater UserLogin(String userId,String password){
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		User user = (User) session.createQuery("from User where userId=:userId")
		.setParameter("userId", userId)
		.uniqueResult();
		session.getTransaction().commit();
		Chater chater = new Chater();
		chater.setUserId(userId);
		chater.setOrder("login");

		if(user==null){
			chater.setMessage("NO USER");
			return chater;
		}
		if(!user.getPassword().equals(password)){
			chater.setMessage("PASSWORD FAILED");
			return chater;
		}
		chater.setMessage("SUCCEED");


		return chater;
	}
	//用户注册
	@RequestMapping(value="/regist")
	@ResponseBody
	public Chater UserRegist(String userId,String username,String password){

		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		User user = (User) session.createQuery("from User where userId=:userId")
		.setParameter("userId", userId)
		.uniqueResult();
		Chater chater = new Chater();
		chater.setUserId(userId);
		chater.setOrder("regist");

		if(user!=null){
			chater.setMessage("You have already Registed");
			return chater;
		}

		user =new User();
		user.setPassword(password);
		user.setUserId(userId);
		user.setUsername(username);
		session.save(user);
		session.getTransaction().commit();
		chater.setMessage("SUCCEED");
		return chater;
	}


	@RequestMapping("/test")
	@ResponseBody
	public Chater Test() {
		RoomUser roomuser=new RoomUser();
		roomuser.setUserId("123");
		roomuser.setNickname("nick");

		Chater chater=new Chater();
		chater.setRoomId("111");
		chater.setUserId("123");
		chater.setOrder("getlist");
		Map<String,Object> map = new HashMap<>();
		map.put("type","体育");
		chater.setObject(map);
		Lab lab=Lab.getLab();

		Map<String,Object> objectMap= (Map<String, Object>) chater.getObject();
		String type= (String) objectMap.get("type");
		Session session=sessionFactory.getCurrentSession();
		session.beginTransaction();
		List<String> wordList=session.createQuery("SELECT word FROM GameWords where type=:type")
				.setParameter("type",type)
				.list();
		session.getTransaction().commit();
		List<String> sendList=new ArrayList<>();
		Chater chater2=new Chater();
		chater2.setRoomId(chater.getRoomId());
		chater2.setUserId(chater.getUserId());
		chater2.setOrder("YouGuess");
		if (wordList.size()==0){
			chater2.setMessage("type error");
			return chater2;
		}
		int size=wordList.size();
		for (int i = 0; i <size ; i++) {
			int index= (int) ((Math.random()*1000)%wordList.size());
			sendList.add(wordList.get(index));
			wordList.remove(index);
		}
		Map<String,Object> Wmap = new HashMap<>();
		Wmap.put("wordList",sendList);
		chater2.setObject(Wmap);
		return chater2;
	}

	@RequestMapping("/download")
    @ResponseBody
	public Chater downloadFile(String url,HttpServletResponse response){
		response.setCharacterEncoding("utf-8");
		response.setContentType("multipart/form-data");
		response.setHeader("Content-Disposition", "attachment;fileName="+url);
		try {
			File file=new File(url);
			System.out.println(file.getAbsolutePath());
			InputStream inputStream= null;
			try {
				inputStream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			OutputStream os=response.getOutputStream();
			byte[] b=new byte[1024];
			int length;
			while((length=inputStream.read(b))>0){
				os.write(b,0,length);
			}
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Chater chater=new Chater();
		chater.setOrder("download");
		chater.setMessage("SUCCEED");
		return chater;
	}

    @RequestMapping("/upload")
    @ResponseBody
    public Chater uploadFile(HttpServletRequest request,@RequestParam("excelFile")MultipartFile file){
        //获取上传文件的名称
        String filename = file.getOriginalFilename();
        File file2 = new File("C:\\Users\\Administrator\\Desktop\\hiparty\\hipartyDB\\loadfile\\");
        if (!file2.exists()) {
            //创建临时目录
            file2.mkdir();
        }
        try {
            //文件存放的路径
        FileOutputStream fileOutputStream = new FileOutputStream(file2+"/"+filename);
        fileOutputStream.write(file.getBytes());
        fileOutputStream.flush();
        fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Session session=sessionFactory.getCurrentSession();
        session.beginTransaction();
		LoadFile loadFile=new LoadFile();
		loadFile.setFilename(filename);
		loadFile.setFileUrl("C:\\Users\\Administrator\\Desktop\\hiparty\\hipartyDB\\loadfile\\"+filename);
		session.save(loadFile);
		session.getTransaction().commit();

		Chater chater =new Chater();
		chater.setMessage("SUCCEED");
		chater.setOrder("upload");
		return  chater;
	}

}
