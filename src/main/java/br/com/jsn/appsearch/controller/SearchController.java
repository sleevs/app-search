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
    summary = "Realizar Busca",
    description = "Informe um termo(palavra-chave) e uma URL para fazer a busca no Web Site.",
    tags = {"Operação de busca"}
        )
    @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Busca realizado com sucesso"),
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    @PostMapping
    public ResponseEntity<Object> startSearch(@RequestParam String termo, @RequestParam String url) {

         try {
            if (termo.length() < 4 || termo.length() > 32 || termo == null || termo.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O ternmo de busca deve ter entre 4 até 32 caracteres.");
            }
            
            return ResponseEntity.ok(searchService.startSearch(termo, url));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor.");
        }
    }
       
    

    @Operation(
    summary = "Consultar resultado",
    description = "Informe o id retornado na Operação de Busca para consultar o resultado da busca.",
    tags = {"Operação de consulta"}
        )
    @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso"),
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SearchModel> getTasks(@PathVariable String id) {
        SearchModel searchModel = searchService.getSearchs().get(id);
        if (searchModel == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(searchModel);
    }


}
