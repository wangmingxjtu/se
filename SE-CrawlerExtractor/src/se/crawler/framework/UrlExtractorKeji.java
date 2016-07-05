package se.crawler.framework;
/**
 * 1)抽取导航页面内详细页面的url地址
 * 2)获取所有导航页面的url地址
 * @version 2.0
 * @author pillar
 * @date　  2016.6.13
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import se.crawler.url.fontier.LinkFilter;

public class UrlExtractorKeji {

	/**
     * 抽取导航页面内详细页面的url地址
     * @param url       
     * @param content   String 某个导航页面源码
     * @param filter    LinkFilter url过滤器
     * @return   HashSet<String> 某个导航页面内所有详细url的集合
     */
	public static Set<String> extractLinks(String url, String content, LinkFilter filter) {
		Set<String> links = new HashSet<String>();  
		Document document = Jsoup.parse(content);
		// 详细页面标签:<a class="" href="" target="_blank" />
		Elements elements = document.getElementsByTag("a");		
		for(Element element : elements){						
			String href = element.attr("href"); //href属性
			if(href!=""){								
				if(!href.contains("http://")){
					try{						
						if(url.contains("index")){
							String tmpurl = url.substring(0, url.indexOf("index"));
							href = tmpurl+href.substring(2);
						}else{
							href = url+href.substring(2);
						}
						if(filter.accept(href)){							
							links.add(href);    //解析到底href加入hashset
						}
					}catch(Exception e){
						System.out.println("This is an exception url!");
					}					
				}	
			}else{	
				href = element.attr("src");	
				if(""==href){					
				}
			}					
		}	
		return links;
	}
	/**
	 * 通过种子地址获取导航页面url集合----逐行读入内存
	 * @param srcpath    存放url种子的路径
	 * @return
	 */
	private static Set<String> getNvglinksBySeeds(String srcpath){
		Set<String> nvglinks = new HashSet<String>();  
		try{
			BufferedReader reader = new BufferedReader(new FileReader(srcpath)); 
			String line = reader.readLine();   //逐行读入内存
			while(line!=null){
				if(line.contains("http")){
					nvglinks.add(line);
					String pageNum = reader.readLine();					
					int num = Integer.parseInt(pageNum.substring(pageNum.indexOf("=")+1));
					for(int i=1;i<=num;i++){
						if(num!=0){    			//表示至少有1个翻页
							String tmpurl = line+"index_"+i+".htm";
							nvglinks.add(tmpurl);     
						}																												
					}
					line = reader.readLine();
				}else{
					line = reader.readLine();	
				}
			}
			reader.close();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		return nvglinks;
	}
	
	/**
	 * 得到http://www.agri.cn/kj/  页面所有导航页面url
	 * 即所有http://www.agri.cn/ 科技板块的导航页面url
	 * @return   HashSet<String> 所有导航页面url集合
	 */
	public static Set<String> getAllKjNavigationLinks() {
		Set<String> nvglinks = null; 
		String src = "urlseeds/agriKejiSeeds/agriKejiSeeds.txt";
		nvglinks = getNvglinksBySeeds(src);
		return nvglinks;
	}
	/**
	 * 得到所有导航页面url--普通版
	 * @return
	 */
	public static Set<String> getAllnavigationLinks() {
		System.out.println("此功能待完善...：getAllnavigationLinks()!");
		return null;
	}
}
