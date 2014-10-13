package test;

import com.ichat.util.Out;

public class Test {
	public static void main(String[] args) {
		Out.println("start-------");
		thread();
		for(char s='a';s<'y';s++){
			Out.println(Character.toString(s));
		}

	}

	private static void thread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i=1;i<100;i++){
					Out.println(Integer.toString(i));
				}
			}
		}).start();
	}

}
