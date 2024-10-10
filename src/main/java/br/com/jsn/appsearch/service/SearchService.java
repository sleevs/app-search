package br.com.jsn.appsearch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import br.com.jsn.appsearch.model.SearchModel;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.HttpStatusException;


@Service
public class SearchService {

    private static final Logger logger = Logger.getLogger(SearchService.class.getName());
    private ConcurrentHashMap<String, SearchModel> searchStorage = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final List<SearchModel> searchList = new CopyOnWriteArrayList<>();


    public void performSearch(SearchModel search, String url) {
        try {
            Document doc = Jsoup.connect(url).get();
         
         Elements links = doc.select("a[href]");

         for (Element link : links) {
           
             if (link.text().contains(search.getKeyword())) {
                 String nextUrl = link.absUrl("href");
              
                 if (!search.getUrls().contains(nextUrl)) {
                    search.getUrls().add(nextUrl);
                 }
             }
         }
 
         Elements divs = doc.select("div:has(a)");
         Elements paragraphs = doc.select("p:has(a)");
         Elements spans = doc.select("span:has(a)");
         Elements sections = doc.select("section:has(a)");
         Elements footers = doc.select("footer:has(a)");
 
         checkAndAddUrls(search, divs);
         checkAndAddUrls(search, paragraphs);
         checkAndAddUrls(search, spans);
         checkAndAddUrls(search, sections);
         checkAndAddUrls(search, footers);
 
         for (Element link : links) {
             String nextUrl = link.absUrl("href");
 
           
             if (nextUrl.startsWith(url) && !search.getUrls().contains(nextUrl)) {
               
                 performSearch(search, nextUrl);
             }
         }

            
        } catch (HttpStatusException e) {
            logger.log(Level.WARNING,  String.valueOf(e.getStatusCode()));
            logger.log(Level.INFO, e.getUrl());
        } catch (IOException e) {
            logger.log(Level.WARNING,e.getMessage());
        } catch (Exception e) {
            System.out.println( e.getMessage());
            logger.log(Level.WARNING,e.getMessage());
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


        SearchModel search = new SearchModel();
        UUID generate = UUID.randomUUID();
        search.setId(generate.toString().substring(0,8));
        search.setUrls(new ArrayList<>());
        search.setKeyword(term);
        search.setStatus("active");
        

        searchList.add(search);
        searchStorage.put(search.getId(), search);
        executorService.submit(() -> {
            try {
                performSearch(search, url);
            } finally {
                search.setStatus("done");
            }
        });

        return searchList;
    }


    public ConcurrentHashMap<String, SearchModel> getSearch() {
        return searchStorage;
    }


    public void setSearch(ConcurrentHashMap<String, SearchModel> searchs) {
        this.searchStorage = searchStorage;
    }

    

}

