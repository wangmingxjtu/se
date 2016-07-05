package se.crawler.framework;
/**
 * 中国茶网导航页面抽取详细页面url
 * 1)抽取导航页面内详细页面的url地址
 * 2)获取所有导航页面的url地址
 * @version 2.0
 * @author pillar
 * @date　  2016.6.15
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

public class UrlExtractorTea {
	
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
				}else{
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
	 * 通过种子地址获取导航页面url集合
	 * @param srcpath    存放url种子的路径
	 * @return		HashSet<String> 所有导航页面url集合
	 */
	private static Set<String> getNvglinksBySeeds(String srcpath){
		Set<String> nvglinks = new HashSet<String>();  
		try{
			BufferedReader reader = new BufferedReader(new FileReader(srcpath)); 
			String line = reader.readLine();
			while(line!=null){				
				if(line.contains("http")){
					nvglinks.add(line);
					System.out.println("add new link1: "+line);
					String pageNum = reader.readLine();					
					int num = Integer.parseInt(pageNum.substring(pageNum.indexOf("=")+1));
					for(int i=1;i<=num;i++){
						if(num!=0){    			//表示至少有1个翻页
							String tmpurl = line+"index_"+i+".htm";
							nvglinks.add(tmpurl);   
							System.out.println("add new link2: "+tmpurl);
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
	 * 得到所有http://www.tea.agri.cn/导航页面url
	 * @return   HashSet<String> 所有导航页面url集合
	 */
	public static Set<String> getAllTeaNavigationLinks() {
		Set<String> nvglinks = null; 
		String src = "urlseeds/agriTeaSeeds/agriTeaSeeds.txt";
		nvglinks = getNvglinksBySeeds(src);
		System.out.println("nvglinks个数为: "+nvglinks.size());
		return nvglinks;
	}
}
