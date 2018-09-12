package com.zhoulesin.threaddemo;

import java.lang.Thread.State;

public class ThreadStatus {
	public static void main(String[] args) {
		State state = new Thread().getState();
		System.out.println(state);
		
		Thread t2 = new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					
					System.out.println("t2 print :" + Thread.currentThread().getState());
				}
			}
			
		});
		t2.start();
		
		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					
					System.out.println("t1 print :" + Thread.currentThread().getState() +"," + t2.getState());
				}
			}
			
		});
		t1.start();
		
		
		
	}
}
