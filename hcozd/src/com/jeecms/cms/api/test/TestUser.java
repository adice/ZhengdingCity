package com.jeecms.cms.api.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.jeecms.common.util.AES128Util;
import com.jeecms.common.util.Num62;
import com.jeecms.common.util.PayUtil;
import com.jeecms.common.web.HttpClientUtil;

public class TestUser {

	public static void main(String[] args) {
		//testLogin();
		testGetUserStatus();
		//testSaveUser();
		//testUpdateUser();
		//testPasswdEdit();
		//testUserGet();
		//testLogout();
	}
	
	private static String testLogin(){
		String url="http://192.168.0.173:8080/jeecmsv8f/api/user/login.jspx?";
		StringBuffer paramBuff=new StringBuffer();
		String password="password";
		paramBuff.append("username="+"admin");
		try {
			paramBuff.append("&aesPassword="+AES128Util.encrypt(password, aesKey,ivKey));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String nonce_str=RandomStringUtils.random(16,Num62.N62_CHARS);
		//String nonce_str="ofIcgEJdPN7FoGVY";
		paramBuff.append("&appId="+appId);
		paramBuff.append("&nonce_str="+nonce_str);
		
		Map<String, String>param=new HashMap<String, String>();
		String []params=paramBuff.toString().split("&");
		for(String p:params){
			String keyValue[]=p.split("=");
			param.put(keyValue[0], keyValue[1]);
		}
		String sign=PayUtil.createSign(param, appKey);
		paramBuff.append("&sign="+sign);
		url=url+paramBuff.toString();
		String res=HttpClientUtil.getInstance().get(url);
		System.out.println(res);
		JSONObject json;
		try {
			json = new JSONObject(res);
			String sessionKey=(String) json.get("body");
			try {
				String descryptKey=AES128Util.decrypt(sessionKey, aesKey,ivKey);
				System.out.println(descryptKey);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	private static String testLogout(){
		String url="http://192.168.0.173:8080/jeecmsv8f/api/user/logout.jspx?";
		StringBuffer paramBuff=new StringBuffer();
		paramBuff.append("username="+"admin");
		try {
			paramBuff.append("&sessionKey="+AES128Util.encrypt(sessionKey, aesKey,ivKey));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String nonce_str=RandomStringUtils.random(16,Num62.N62_CHARS);
		//String nonce_str="ofIcgEJdPN7FoGVY";
		paramBuff.append("&appId="+appId);
		paramBuff.append("&nonce_str="+nonce_str);
		
		Map<String, String>param=new HashMap<String, String>();
		String []params=paramBuff.toString().split("&");
		for(String p:params){
			String keyValue[]=p.split("=");
			param.put(keyValue[0], keyValue[1]);
		}
		String sign=PayUtil.createSign(param, appKey);
		paramBuff.append("&sign="+sign);
		url=url+paramBuff.toString();
		String res=HttpClientUtil.getInstance().get(url);
		System.out.println(res);
		return res;
	}
	
	private static String testGetUserStatus(){
		String url="http://192.168.0.173:8080/jeecmsv8f/api/user/getStatus.jspx?";
		StringBuffer paramBuff=new StringBuffer();
		paramBuff.append("username="+"admin");
		try {
			paramBuff.append("&sessionKey="+AES128Util.encrypt(sessionKey, aesKey,ivKey));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String nonce_str=RandomStringUtils.random(16,Num62.N62_CHARS);
		//String nonce_str="ofIcgEJdPN7FoGVY";
		paramBuff.append("&appId="+appId);
		paramBuff.append("&nonce_str="+nonce_str);
		
		Map<String, String>param=new HashMap<String, String>();
		String []params=paramBuff.toString().split("&");
		for(String p:params){
			String keyValue[]=p.split("=");
			param.put(keyValue[0], keyValue[1]);
		}
		String sign=PayUtil.createSign(param, appKey);
		paramBuff.append("&sign="+sign);
		url=url+paramBuff.toString();
		String res=HttpClientUtil.getInstance().get(url);
		System.out.println(res);
		JSONObject json;
		try {
			json = new JSONObject(res);
			String message=(String) json.get("message");
			System.out.println(message);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(res);
		return res;
	}
	
	private static String testUserGet(){
		String url="http://192.168.0.173:8080/jeecmsv8f/api/user/get.jspx";
		StringBuffer paramBuff=new StringBuffer();
		paramBuff.append("username="+"admin");
		String nonce_str=RandomStringUtils.random(16,Num62.N62_CHARS);
		//String nonce_str="ofIcgEJdPN7FoGVY";
		paramBuff.append("&appId="+appId);
		paramBuff.append("&nonce_str="+nonce_str);
		
		Map<String, String>param=new HashMap<String, String>();
		String []params=paramBuff.toString().split("&");
		for(String p:params){
			String keyValue[]=p.split("=");
			param.put(keyValue[0], keyValue[1]);
		}
		String sign=PayUtil.createSign(param, appKey);
		paramBuff.append("&sign="+sign);
		param.put("sign", sign);
	//	url=url+paramBuff.toString();
		//String res=HttpClientUtil.getInstance().get(url);
		String res=HttpClientUtil.getInstance().postParams(url, param);
		JSONObject json;
		try {
			json = new JSONObject(res);
			/*
			String sessionKey=(String) json.get("body");
			try {
				String descryptKey=AES128Util.decrypt(sessionKey, aesKey);
				System.out.println(descryptKey);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
			System.out.println(json.get("body"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(res);
		return res;
	}
	
	private static String testSaveUser(){
		String url="http://192.168.0.173:8080/jeecmsv8f/api/user/add.jspx?";
		StringBuffer paramBuff=new StringBuffer();
		paramBuff.append("username="+"test1112");
		paramBuff.append("&email="+"test1@qq.com");
		paramBuff.append("&loginPassword="+"password");
		paramBuff.append("&realname="+"realname");
		paramBuff.append("&gender="+true);
		paramBuff.append("&birthdayStr="+"1982-05-09");
		paramBuff.append("&phone=0791-88888888");
		paramBuff.append("&mobile=13888888888");
		paramBuff.append("&qq=123456");
		paramBuff.append("&userImg=/user/1.png");
		String nonce_str=RandomStringUtils.random(16,Num62.N62_CHARS);
		paramBuff.append("&appId="+appId);
		paramBuff.append("&nonce_str="+nonce_str);
		
		Map<String, String>param=new HashMap<String, String>();
		String []params=paramBuff.toString().split("&");
		for(String p:params){
			String keyValue[]=p.split("=");
			param.put(keyValue[0], keyValue[1]);
		}
		String sign=PayUtil.createSign(param, appKey);
		paramBuff.append("&sign="+sign);
		url=url+paramBuff.toString();
		String res=HttpClientUtil.getInstance().get(url);
		System.out.println("res->"+res);
		return res;
	}
	
	private static String testUpdateUser(){
		String url="http://192.168.0.173:8080/jeecmsv8f/api/user/edit.jspx?";
		StringBuffer paramBuff=new StringBuffer();
		paramBuff.append("username="+"test1112");
		paramBuff.append("&realname="+"realname1");
		paramBuff.append("&gender="+false);
		paramBuff.append("&birthdayStr="+"1983-06-10");
		paramBuff.append("&phone=0791-77777777");
		paramBuff.append("&mobile=13899999999");
		paramBuff.append("&qq=1234561");
		paramBuff.append("&userImg=/user/2.png");
		String nonce_str=RandomStringUtils.random(16,Num62.N62_CHARS);
		paramBuff.append("&appId="+appId);
		paramBuff.append("&nonce_str="+nonce_str);
		
		Map<String, String>param=new HashMap<String, String>();
		String []params=paramBuff.toString().split("&");
		for(String p:params){
			String keyValue[]=p.split("=");
			param.put(keyValue[0], keyValue[1]);
		}
		String sign=PayUtil.createSign(param, appKey);
		paramBuff.append("&sign="+sign);
		url=url+paramBuff.toString();
		String res=HttpClientUtil.getInstance().get(url);
		System.out.println("res->"+res);
		return res;
	}
	
	private static String testPasswdEdit(){
		String url="http://192.168.0.173:8080/jeecmsv8f/api/user/pwd.jspx?";
		StringBuffer paramBuff=new StringBuffer();
		paramBuff.append("username="+"test1112");
		paramBuff.append("&origPwd="+"password");
		paramBuff.append("&newPwd=123456");
		paramBuff.append("&email="+"112@qq.com");
		String nonce_str=RandomStringUtils.random(16,Num62.N62_CHARS);
		paramBuff.append("&appId="+appId);
		paramBuff.append("&nonce_str="+nonce_str);
		
		Map<String, String>param=new HashMap<String, String>();
		String []params=paramBuff.toString().split("&");
		for(String p:params){
			String keyValue[]=p.split("=");
			param.put(keyValue[0], keyValue[1]);
		}
		String sign=PayUtil.createSign(param, appKey);
		paramBuff.append("&sign="+sign);
		url=url+paramBuff.toString();
		String res=HttpClientUtil.getInstance().get(url);
		System.out.println("res->"+res);
		return res;
	}

	private static String appId="111111";
	private static String appKey="Sd6qkHm9o4LaVluYRX5pUFyNuiu2a8oi";
	private static String sessionKey="9CBB23E7490F2053418499E9A7079ACE";
	private static String aesKey="S9u978Q31NGPGc5H";
	private static String ivKey="X83yESM9iShLxfwS";

}
