package molab.java;

import molab.java.thread.FetchThread;

public class Main {

	public static void main(String[] args) {
		if(args.length < 3) {
			throw new IllegalArgumentException("Arguments: <host> <username> <password>");
		}
//		Jdbc jdbc = new Jdbc(args[0], args[1], args[2]);
		
//		FetchThread ft = new FetchThread(new Jdbc(args[0], args[1], args[2]));
//		ft.start();
		new FetchThread(new Jdbc(args[0], args[1], args[2])).start();
		
//		CheckThread ct = new CheckThread(new Jdbc(args[0], args[1], args[2]));
//		ct.start();
//		new CheckThread(new Jdbc(args[0], args[1], args[2])).start();
	}

}
