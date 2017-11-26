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
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class Goubanjia extends Thread {
	
	private final Logger log = Logger.getLogger(Goubanjia.class.getName());
	private final String SERVER_HOST = "http://proxy.goubanjia.com/";
	private final int SLEEP_TIME = 200000; // 20 * 5 + 100 = 200 seconds
	private Jdbc jdbc = null;
	private Map<String, Integer> districtMap = null;
	private int total = 0;
	
	public Goubanjia(Jdbc jdbc, Map<String, Integer> districtMap) {
		this.jdbc = jdbc;
		this.districtMap = districtMap;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				List<String> sqlList = new ArrayList<String>();
				String url = SERVER_HOST;
				List<Proxy> proxyList = getProxy(url, districtMap);
				// save all
				if(proxyList != null && proxyList.size() > 0) {
					
					for(Proxy proxy : proxyList) {
						if(!jdbc.exist(proxy.getIp(), proxy.getPort())
								&& HttpUtil.check(proxy.getIp(), proxy.getPort())) {
							sqlList.add(Jdbc.parseSql(proxy.getIp(), proxy.getPort(),
									proxy.getDistrictCode(),
									proxy.getDistrictName()));
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
		Elements ipList = doc.select("td.ip");
		if (ipList != null && ipList.size() > 0) {
			total += ipList.size();
			List<Proxy> proxyList = new ArrayList<Proxy>();
			for (Element e : ipList) {
				// fetch ip
				String ip = "";
				List<Node> nodeList = e.childNodes();
				for(Node n : nodeList) {
					if(n.attr("style") == null ||
							(n.attr("style") != null && n.attr("style").indexOf("none") == -1)) {
						if(n.childNodeSize() > 0) {
							ip += n.childNode(0).toString().trim();
						}
					}
				}
				// fetch port
				Integer port = Integer.parseInt(e.siblingNodes().get(2).childNode(0).toString());
				// fetch district
				Node a = e.siblingNodes().get(10).childNode(0);
				if(a.childNodeSize() <= 0) {
					continue;
				}
				String district = a.childNode(0).toString();
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
