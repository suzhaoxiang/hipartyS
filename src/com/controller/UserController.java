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
	//暖场游戏
	@RequestMapping(value="/warmgame")
	@ResponseBody
	public Chater WarmGame(String level, HttpServletRequest request){
			Session session=sessionFactory.getCurrentSession();
			session.beginTransaction();
			String decode=null;
			try {
				decode= URLDecoder.decode(level,"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return new Chater();
			}

			List<WarmGame> warmGamelist = session.createQuery("from WarmGame where warmGameLevel=:WarmGameLevel")
					.setParameter("WarmGameLevel", decode)
                    .list();
			session.getTransaction().commit();
			String localAddr = request.getLocalAddr();
			// 设定返回值
			for (int i = 0; i <warmGamelist.size(); i++) {
				String url=warmGamelist.get(i).getWarmGameUrl();
				url=url.replace("\\","/");
				warmGamelist.get(i).setWarmGameUrl("http://"+localAddr+":8099/user/download?url="+url);
			}
			Chater chater = new Chater();
			chater.setOrder("warmgame");
			Map<String, Object> object = new HashMap<>();
			object.put("size", warmGamelist.size());
			object.put("list", new Gson().toJson(warmGamelist));
			chater.setObject(object);
			chater.setMessage("SUCCEED");

			return chater;
	}

	@RequestMapping("/test")
	@ResponseBody
	public Chater Test() {
		RoomUser roomuser=new RoomUser();
		roomuser.setUserId("123");
		roomuser.setNickname("nick");

		Werewolf werewolf=new Werewolf();
		werewolf.setGod(roomuser);
		Chater chater=new Chater();
		chater.setRoomId("111");
		chater.setUserId("123");
		chater.setOrder("beginwerewolf");
		chater.setObject(werewolf);
		Lab lab=Lab.getLab();
		List<RoomUser> playerlist= LabUtils.FindRoom(chater.getRoomId()).getUserlist();
		for(int i=0;i<werewolf.getunPlayerlist().size();i++){
			playerlist.remove(werewolf.getunPlayerlist().get(i));
		}
		Chater chater2=new Chater();
		chater2.setOrder("beginwerewolf");
		chater2.setRoomId(chater.getRoomId());
		//上帝
		chater2.setMessage("God");
		return chater2;
//		//预言家
//		int j = (int) ((Math.random() * 1000) % playerlist.size());
//		if (werewolf.isSeerIs()) {
//			RoomUser player = playerlist.get(j);
//			playerlist.remove(player);
//			chater2.setMessage("预言家");
//			chater2.setUserId(player.getUserId());
//			return chater2;
//		}
//		//女巫
//		if (werewolf.isWitchIs()) {
//			RoomUser player = playerlist.get(j);
//			playerlist.remove(player);
//			chater2.setMessage("女巫");
//			chater2.setUserId(player.getUserId());
//			return chater2;
//		}
//		//猎人
//		if (werewolf.isHunterIs()) {
//			RoomUser player = playerlist.get(j);
//			playerlist.remove(player);
//			chater2.setMessage("猎人");
//			chater2.setUserId(player.getUserId());
//			return chater2;
//		}
//		//盗贼
//		if (werewolf.isThiefIs()) {
//			RoomUser player = playerlist.get(j);
//			playerlist.remove(player);
//			chater2.setMessage("盗贼");
//			chater2.setUserId(player.getUserId());
//			return chater2;
//		}
//		//白痴
//		if (werewolf.isIdiotIs()) {
//			RoomUser player = playerlist.get(j);
//			playerlist.remove(player);
//			chater2.setMessage("白痴");
//			chater2.setUserId(player.getUserId());
//			return chater2;
//		}
//		//丘比特
//		if (werewolf.isCupidIs()) {
//			RoomUser player = playerlist.get(j);
//			playerlist.remove(player);
//			chater2.setMessage("丘比特");
//			chater2.setUserId(player.getUserId());
//			return chater2;
//		}
//		//守卫
//		if (werewolf.isGuardIs()) {
//			RoomUser player = playerlist.get(j);
//			playerlist.remove(player);
//			chater2.setMessage("守卫");
//			chater2.setUserId(player.getUserId());
//			return chater2;
//		}
//		//小女孩
//		if (werewolf.isGirlIs()) {
//			RoomUser player = playerlist.get(j);
//			playerlist.remove(player);
//			chater2.setMessage("小女孩");
//			chater2.setUserId(player.getUserId());
//			return chater2;
//		}
//		//长老
//		if (werewolf.isPresbyterIs()) {
//			RoomUser player = playerlist.get(j);
//			playerlist.remove(player);
//			chater2.setMessage("长老");
//			chater2.setUserId(player.getUserId());
//			return chater2;
//		}
//		//狼人
//		for (int i = 0; i < werewolf.getWerewolfnum(); i++) {
//			RoomUser player = playerlist.get(i);
//			playerlist.remove(player);
//			chater2.setMessage("狼人");
//			chater2.setUserId(player.getUserId());
//			return chater2;
//		}
//		//村民
//		for (int i = 0; i < werewolf.getVillagernum(); i++) {
//			RoomUser player = playerlist.get(i);
//			playerlist.remove(player);
//			chater2.setMessage("村民");
//			chater2.setUserId(player.getUserId());
//			return chater2;
//		}
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
