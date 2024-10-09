package br.com.jsn.appsearch.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SearchModel {

    private String id;
    private String keyword;
    private String status;
    private List<String> urls = new CopyOnWriteArrayList<>();

    public SearchModel(){

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    
    
}
