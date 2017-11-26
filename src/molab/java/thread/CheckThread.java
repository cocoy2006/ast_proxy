package molab.java.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import molab.java.Jdbc;
import molab.java.entity.Proxy;
import molab.java.util.HttpUtil;

public class CheckThread extends Thread {
	
	private final Logger log = Logger.getLogger(CheckThread.class.getName());
	private final int NUMBER_OF_THREADS = 10;
	private final int SLEEP_TIME = 5000;
	private Jdbc jdbc = null;
	
	public CheckThread(Jdbc jdbc) {
		this.jdbc = jdbc;
	}
	
	@Override
	public void run() {
		log.info("Start check.");
		final List<Integer> okIds = new ArrayList<Integer>();
		final List<Integer> otherIds = new ArrayList<Integer>();
		while(true) {
			int totalCount = jdbc.getCount();
			log.info("There are " + totalCount + " proxys in database.");
			int loop = totalCount / NUMBER_OF_THREADS;
			// need <loop> times
			for(int i = 0; i < loop; i++) {
				List<Proxy> pl = jdbc.getProxy(i * NUMBER_OF_THREADS, NUMBER_OF_THREADS);
				final CountDownLatch counter = new CountDownLatch(pl.size());
				okIds.clear();
				otherIds.clear();
				// check <NUMBER_OF_THREADS> proxy everytime
				for (Proxy proxy : pl) {
					final Proxy p = proxy;
					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
							if(HttpUtil.check(p.getIp(), p.getPort())) {
								okIds.add(p.getId());
							} else {
								otherIds.add(p.getId());
							}
							counter.countDown();
						}

					});
					t.start();
					try {
						t.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				try {
					counter.await();
					if(okIds != null && okIds.size() > 0) {
						int okRows = jdbc.updateTime(okIds);
						log.info(okRows + " rows' time updated.");
					}
					if(otherIds != null && otherIds.size() > 0) {
						int otherRows = jdbc.updateIsused(otherIds);
						log.info(otherRows + " rows' isused updated.");
					}
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					log.severe(e.getMessage());
				}
			}
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				log.severe(e.getMessage());
			}
		}
	}

}
