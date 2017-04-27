package com.controller;

import com.beans.*;
import com.google.gson.Gson;
import com.utils.HibernateUtil;
import com.utils.LabUtils;
import org.apache.mina.core.session.IoSession;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/24.
 */

@Controller
@RequestMapping(value="/room")
public class RoomController {
    private Lab lab= Lab.getLab();
    private SessionFactory sessionFactory= HibernateUtil.getSessionFactory();
    //暖场游戏
    @RequestMapping(value="/warmgame")
    @ResponseBody
    public Chater WarmGame(String level, HttpServletRequest request){
        String decode=null;
        try {
            decode= URLDecoder.decode(level,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Chater();
        }

        Session session=sessionFactory.getCurrentSession();
        session.beginTransaction();
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

    @RequestMapping("/searchroom")
    @ResponseBody
    public Chater SearchRoom(Chater chater) {
        Room room=LabUtils.FindRoom(chater.getRoomId());
        RoomUser roomUser=LabUtils.FindRoomUser(chater.getRoomId(),chater.getUserId());
        String roomname =chater.getMessage();
        List<RoomDTO> roomDTOlist=new ArrayList<>();
        Chater chater2 = new Chater();
        chater2.setOrder("searchroom");
        chater2.setRoomId(chater.getRoomId());
        chater2.setUserId(chater.getUserId());
        for(int i=0;i<lab.getList().size();i++){
            if(lab.getList().get(i).getRoomname().equals(roomname)){
                RoomDTO roomDTO=new RoomDTO();
                roomDTO.setRoomId(lab.getList().get(i).getRoomId());
                roomDTO.setRoomname(lab.getList().get(i).getRoomname());
                roomDTO.setHostId(lab.getList().get(i).getHostId());
                roomDTOlist.add(roomDTO);
            }
        }
        chater2.setObject(roomDTOlist);
        chater2.setMessage("SUCCEED");
        return chater2;
    }
}
