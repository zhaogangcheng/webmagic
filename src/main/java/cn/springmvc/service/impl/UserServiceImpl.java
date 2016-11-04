package cn.springmvc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.springmvc.dao.UserMapper;
import cn.springmvc.model.User;
import cn.springmvc.service.UserService;


public class UserServiceImpl implements UserService{

	private UserMapper userDAO;

	public int insertUser(User user) {
		// TODO Auto-generated method stub
		return 0;
	}
	

}
