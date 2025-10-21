package com.app.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiServer {
	public static void main(String[] args) throws RemoteException {
		System.out.println("Start Remote Service...");
		
		WorldClock worldClock = new WorldClockService();
		// 将服务转换为远程服务端口
		WorldClock skeleton = (WorldClock) UnicastRemoteObject.exportObject(worldClock, 0);
		// 将RMI服务注册到1099端口
		Registry registry = LocateRegistry.createRegistry(1099);
		// 注册服务
		registry.rebind(WorldClock.class.getName(), skeleton);
	}
}
