package com.zhoulesin.threaddemo;

public class ThreadStatus2 {
	public static void main(String[] args) {
		Thread thread2 = new Thread(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < 1000; i++) {
					System.out.println("t2:" + i);
				}
			}
		});
		
		//�̵߳�������sleep���������̵߳�join����,��������״̬
		//��sleep��������join����,���߳̽��������״̬
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < 1000; i++) {
					System.out.println("t1 :" + i);
					if (i == 500) {
//						try {
//							thread2.join();
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});
		
		thread.start();
		thread2.start();
	}
}
