package br.com.jsn.appsearch.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.jsn.appsearch.model.SearchModel;
import br.com.jsn.appsearch.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/crawl")
public class SearchController {
    
    
    private SearchService searchService;

    
     public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }
    

    @Operation(
    summary = "buscar operation",
    description = "Realiza buscar em sites.",
    tags = {"buscar operation"}
        )
    @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "busca realizado com sucesso"),
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    @PostMapping
    public ResponseEntity<Object> startSearch(@RequestParam String term, @RequestParam String url) {

         try {
            if (term.length() < 4 || term.length() > 32 || term == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O ternmo de buscar deve ter entre 4 até 32 caracteres.");
            }
            
            return ResponseEntity.ok(searchService.startSearch(term, url));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor.");
        }
    }
       
    

    
    @GetMapping("/{id}")
    public ResponseEntity<SearchModel> getTasks(@PathVariable String id) {
        SearchModel searchModel = searchService.getSearchs().get(id);
        if (searchModel == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(searchModel);
    }


}
