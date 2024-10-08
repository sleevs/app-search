package br.com.jsn.appsearch;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;



import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Collections;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import br.com.jsn.appsearch.controller.SearchController;
import br.com.jsn.appsearch.model.SearchModel;
import br.com.jsn.appsearch.service.SearchService;


@WebMvcTest(SearchController.class)
class AppSearchApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

    @Test
    public void testStartSearch_withValidTerm_shouldReturnOk() throws Exception {
        String termo = "validTerm";
        String url = "http://www.google.com";

        SearchModel model = new SearchModel();
        model.setKeyword(url);
        model.setId("1");

        when(searchService.startSearch(termo, url)).thenReturn(Collections.singletonList(model));

        mockMvc.perform(post("/crawl")
                .param("termo", termo)
                .param("url", url)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":\"1\",\"keyword\":\"http://www.google.com\"}]"));
    }



    @Test
    public void testStartSearch_withTermOver32Characters_shouldReturnBadRequest() throws Exception {
        String termo = "thisTermIsWayTooLongForValidation"; 
        String url = "http://www.google.com";

        mockMvc.perform(post("/crawl")
                .param("termo", termo)
                .param("url", url)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("O ternmo de busca deve ter entre 4 até 32 caracteres."));
    }


 
}
