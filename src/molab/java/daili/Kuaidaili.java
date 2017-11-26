package molab.java.daili;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import molab.java.Jdbc;
import molab.java.entity.Proxy;
import molab.java.util.HttpUtil;
import molab.java.util.Status;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Kuaidaili extends Thread {
	
	private final Logger log = Logger.getLogger(Kuaidaili.class.getName());
	private final String SERVER_HOST = "http://www.kuaidaili.com/proxylist/";
	private final int SLEEP_TIME = 200000; // 20 * 5 + 100 = 200 seconds
	private Jdbc jdbc = null;
	private Map<String, Integer> districtMap = null;
	private int total = 0;
	
	public Kuaidaili(Jdbc jdbc, Map<String, Integer> districtMap) {
		this.jdbc = jdbc;
		this.districtMap = districtMap;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				List<String> sqlList = new ArrayList<String>();
				for(int i = 1; i <= 2; i++) {
					String url = SERVER_HOST + i + "/";
					List<Proxy> proxyList = getProxy(url, districtMap);
					// save all
					if(proxyList != null && proxyList.size() > 0) {
						
						for(Proxy proxy : proxyList) {
							if(!jdbc.exist(proxy.getIp(), proxy.getPort())
									&& HttpUtil.check(proxy.getIp(), proxy.getPort())) {
//								jdbc.saveProxy(proxy.getIp(), proxy.getPort(),
//										proxy.getDistrictCode(),
//										proxy.getDistrictName());
								sqlList.add(Jdbc.parseSql(proxy.getIp(), proxy.getPort(),
										proxy.getDistrictCode(),
										proxy.getDistrictName()));
							}
						}
					}
				}
				if(sqlList.size() > 0) {
					jdbc.batchUpdate(sqlList);
					log.info(total + " proxys found but " + sqlList.size() + " fetched.");
				} else {
					log.info(total + " proxys found but 0 fetched.");
				}
				total = 0;
			} catch (IOException e) {
				log.severe(e.getMessage());
			}
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				log.severe(e.getMessage());
			}
		}
	}
	
	public List<Proxy> getProxy(String url, Map<String, Integer> districtMap) throws IOException {
		Document doc = Jsoup.connect(url).get();
		Elements trList = doc.select("#list table tbody tr");
		if (trList != null && trList.size() > 0) {
			total += trList.size();
			List<Proxy> proxyList = new ArrayList<Proxy>();
			for (Element e : trList) {
				// fetch ip
				String ip = e.childNode(1).childNode(0).toString();
				// fetch port
				Integer port = Integer.parseInt(e.childNode(3).childNode(0).toString());
				// fetch district
				String district = e.childNode(11).childNode(0).toString();
				Integer districtCode = 0;
				String districtName = null;
				boolean found = false;
				for(String name : districtMap.keySet()) {
					if(district.indexOf(name) > -1) {
						districtName = name;
						districtCode = districtMap.get(name);
						found = true;
						break;
					}
				}
				if(!found) { // give up if district info is unknown
					continue;
				}
				// build proxy
				Proxy proxy = new Proxy(ip, port, districtCode, districtName,
						Status.Common.FALSE.getInt(),
						System.currentTimeMillis());
				proxyList.add(proxy);
			}
			return proxyList;
		}
		return null;
	}
	
}
