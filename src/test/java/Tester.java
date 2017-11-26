package test.java;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Tester {

//	private static final String SCH_SCORE_URL = "http://gkcx.eol.cn%s%d.htm#";
	
	public static void main(String[] args) throws IOException {
		String url = "http://gkcx.eol.cn/schoolhtm/schoolInfo/1024/10071/list_1.htm";
//		String url = "http://gkcx.eol.cn/schoolhtm/schoolAreaPoint/3212/schoolAreaPoint.htm#";
//		String url = "http://gkcx.eol.cn/schoolhtm/schoolSpecailtyMark/573/schoolSpecailtyMark.htm";
//		Document doc = Jsoup.connect(url).get();
//		Elements aList = doc.select(".S_result table tr a");
//		if (aList != null && aList.size() > 0) {
//			for(Element a : aList) {
//				String href = a.attr("href");
//				System.out.println(href);
//			}
//		}
		
//		String url = "http://gkcx.eol.cn/schoolhtm/specialty/573/10035/specialtyScoreDetail_2012_10003.htm";
		Document doc = Jsoup.connect(url).get();
		Elements aList = doc.select(".S_result a");
		if (aList != null && aList.size() > 0) {
			for(Element a : aList) {
				if(a.html().indexOf("2016") > 0) {
					System.out.println("2016:" + a.attr("href"));
				} else {
					System.out.println("other:" + a.attr("href"));
				}
				
			}
		}
		
		System.out.println("Done.");
		System.exit(0);
	}
	
}
