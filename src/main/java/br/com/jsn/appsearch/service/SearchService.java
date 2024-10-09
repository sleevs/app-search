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


@Service
public class SearchService {


    private ConcurrentHashMap<String, SearchModel> searchs = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final List<SearchModel> tasks = new CopyOnWriteArrayList<>();


    public void performSearch(SearchModel task, String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            String pageSource = doc.text();

            if (pageSource.contains(task.getKeyword())) {
                task.getUrls().add(url);
            }

            
        } catch (Exception e) {
            e.printStackTrace();
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

