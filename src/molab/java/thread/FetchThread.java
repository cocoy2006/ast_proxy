package molab.java.thread;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import molab.java.Jdbc;
import molab.java.daili.Goubanjia;
import molab.java.daili.Kuaidaili;
import molab.java.daili.Qiaodm;
import molab.java.entity.District;

public class FetchThread extends Thread {

	private final Logger log = Logger.getLogger(FetchThread.class.getName());
	private Jdbc jdbc = null;
	private Map<String, Integer> districtMap = new LinkedHashMap<String, Integer>();
	
	public boolean init() {
		List<District> dl = jdbc.findAllDistrict();
		if(dl != null && dl.size() > 0) {
			for(District d : dl) {
				districtMap.put(d.getName(), d.getCode());
			}
			return true;
		}
		return false;
	}
	
	public FetchThread(Jdbc jdbc) {
		this.jdbc = jdbc;
	}
	
	@Override
	public void run() {
		log.info("Start fetch.");
		if(!init()) {
			throw new RuntimeException("Table district cannot be empty.");
		}
		
		new Kuaidaili(jdbc, districtMap).start();

		new Goubanjia(jdbc, districtMap).start();
		
		new Qiaodm(jdbc, districtMap).start();
	}
	
}
