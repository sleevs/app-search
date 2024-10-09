package br.com.jsn.appsearch.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import br.com.jsn.appsearch.model.SearchModel;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


@Service
public class SearchService {


    private ConcurrentHashMap<String, SearchModel> searchs = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final List<SearchModel> tasks = new CopyOnWriteArrayList<>();


    public void performSearch(SearchModel task, String url) {
        try {
            Document doc = Jsoup.connect(url).get();
         
         Elements links = doc.select("a[href]");

         for (Element link : links) {
           
             if (link.text().contains(task.getKeyword())) {
                 String nextUrl = link.absUrl("href");
              
                 if (!task.getUrls().contains(nextUrl)) {
                     task.getUrls().add(nextUrl);
                 }
             }
         }
 
         Elements divs = doc.select("div:has(a)");
         Elements paragraphs = doc.select("p:has(a)");
         Elements spans = doc.select("span:has(a)");
         Elements sections = doc.select("section:has(a)");
         Elements footers = doc.select("footer:has(a)");
 
         checkAndAddUrls(task, divs);
         checkAndAddUrls(task, paragraphs);
         checkAndAddUrls(task, spans);
         checkAndAddUrls(task, sections);
         checkAndAddUrls(task, footers);
 
         for (Element link : links) {
             String nextUrl = link.absUrl("href");
 
           
             if (nextUrl.startsWith(url) && !task.getUrls().contains(nextUrl)) {
               
                 performSearch(task, nextUrl);
             }
         }

            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void checkAndAddUrls(SearchModel task, Elements elements) {
        for (Element element : elements) {
            if (element.text().contains(task.getKeyword())) {
             
                Element link = element.selectFirst("a[href]");
                if (link != null) {
                    String nextUrl = link.absUrl("href");
                  
                    if (!task.getUrls().contains(nextUrl)) {
                        task.getUrls().add(nextUrl);
                    }
                }
            }
        }

    }

    

     public List<SearchModel> startSearch( String term, String url) {


        SearchModel task = new SearchModel();
        UUID generate = UUID.randomUUID();
        task.setUrls(new ArrayList<>());
        task.setKeyword(term);
        task.setStatus("active");
        task.setId(generate.toString().substring(0,8));

        tasks.add(task);
        searchs.put(task.getId(), task);
        executorService.submit(() -> {
            try {
                performSearch(task, url);
            } finally {
                task.setStatus("done");
            }
        });

        return tasks;
    }


    public ConcurrentHashMap<String, SearchModel> getSearchs() {
        return searchs;
    }


    public void setSearchs(ConcurrentHashMap<String, SearchModel> searchs) {
        this.searchs = searchs;
    }

    

}

