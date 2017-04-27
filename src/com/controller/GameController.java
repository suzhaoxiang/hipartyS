package com.controller;

import com.beans.Chater;
import com.beans.Room;
import com.beans.RoomUser;
import com.beans.Werewolf;
import com.google.gson.Gson;
import com.utils.HibernateUtil;
import com.utils.LabUtils;
import org.apache.mina.core.session.IoSession;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/21.
 */
@Controller
@RequestMapping("/game")
public class GameController {
    private SessionFactory sessionFactory= HibernateUtil.getSessionFactory();

    @RequestMapping("/getlist")
    @ResponseBody
    public Chater gelist(Chater chater){
        Chater chater2 = new Chater();
        chater2.setOrder("getlist");
        if(chater.getRoomId()==null||chater.getUserId()==null) {
            chater2.setMessage("False");
            return chater2;
        }
        chater2.setMessage("SUCCEED");
        chater2.setRoomId(chater.getRoomId());
        chater2.setUserId(chater.getUserId());
        chater2.setObject(LabUtils.FindRoom(chater.getRoomId()).getUserlist());
        return chater2;
    }
    @RequestMapping("/beginwerewolf")
    @ResponseBody
    public void BeginWerewolf(Chater chater) {
        Werewolf werewolf = (Werewolf) chater.getObject();
        Room room=LabUtils.FindRoom(chater.getRoomId());
        List<RoomUser> playerlist = room.getUserlist();
        for (int i = 0; i < werewolf.getunPlayerlist().size(); i++) {
            playerlist.remove(werewolf.getunPlayerlist().get(i));
        }
        Chater chater2 = new Chater();
        chater2.setOrder("beginwerewolf");
        chater2.setRoomId(chater.getRoomId());

        //上帝
        chater2.setMessage("God");
        playerlist.remove(LabUtils.FindRoomUser(chater.getRoomId(),chater.getUserId()));
        room.sendSingle(chater2, werewolf.getGod().getSession());
        //预言家
        if (werewolf.isSeerIs()) {
            chater2.setMessage("预言家");
            playerlist=createPlayer(chater2,playerlist);
        }
        //女巫
        if (werewolf.isWitchIs()) {
            chater2.setMessage("女巫");
            playerlist=createPlayer(chater2,playerlist);
        }
        //猎人
        if (werewolf.isHunterIs()) {
            chater2.setMessage("猎人");
            playerlist=createPlayer(chater2,playerlist);
        }
        //盗贼
        if (werewolf.isThiefIs()) {
            chater2.setMessage("盗贼");
            playerlist=createPlayer(chater2,playerlist);
        }
        //白痴
        if (werewolf.isIdiotIs()) {
            chater2.setMessage("白痴");
            playerlist=createPlayer(chater2,playerlist);
        }
        //丘比特
        if (werewolf.isCupidIs()) {
            chater2.setMessage("丘比特");
            playerlist=createPlayer(chater2,playerlist);
        }
        //守卫
        if (werewolf.isGuardIs()) {
            chater2.setMessage("守卫");
            playerlist=createPlayer(chater2,playerlist);
        }
        //小女孩
        if (werewolf.isGirlIs()) {
            chater2.setMessage("小女孩");
            playerlist=createPlayer(chater2,playerlist);
        }
        //长老
        if (werewolf.isPresbyterIs()) {
            chater2.setMessage("长老");
            playerlist=createPlayer(chater2,playerlist);
        }
        //狼人
        for (int i = 0; i < werewolf.getWerewolfnum(); i++) {
            chater2.setMessage("狼人");
            playerlist=createPlayer(chater2,playerlist);
        }
        //村民
        for (int i = 0; i < werewolf.getVillagernum(); i++) {
            chater2.setMessage("村民");
            playerlist=createPlayer(chater2,playerlist);
        }
    }

    private List<RoomUser> createPlayer(Chater chater,List<RoomUser> playerlist){
        Room room=LabUtils.FindRoom(chater.getRoomId());
        RoomUser player = playerlist.get((int) (Math.random() * 1000) % playerlist.size());
        playerlist.remove(player);
        chater.setUserId(player.getUserId());
        room.sendSingle(chater, player.getSession());
        return playerlist;
    }
    @RequestMapping("/youguess")
    @ResponseBody
    public Chater YouGuess(Chater chater){
        Map<String,Object> objectMap=(Map<String, Object>) chater.getObject();
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
        Map<String,Object> map = new HashMap<>();
        map.put("wordList",sendList);
        chater2.setObject(map);
        return chater2;
    }
}
