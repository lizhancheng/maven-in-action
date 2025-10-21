package com.app.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;

public class RmiClient {
	public static void main(String[] args) throws RemoteException, NotBoundException {
		// 连接服务器 localhost:1099
		Registry registry = LocateRegistry.getRegistry("localhost", 1099);
		// 查找名称 WorldClock 并获得接口
		WorldClock worldClock = (WorldClock) registry.lookup(WorldClock.class.getName());
		// 正常调用接口方法
		LocalDateTime now = worldClock.getLocalDateTime("Asia/Shanghai");
		
		System.out.println(now);
	}
}
