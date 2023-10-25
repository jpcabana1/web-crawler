package com.example.tasks.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/crawler")
public class CrawlerController {

    private Collection<String> urlsRoot;

    public CrawlerController() {
        this.urlsRoot = Arrays.asList(
                "https://spring.io",
                "https://learn.microsoft.com/en-us/");
        ;
    }

    @GetMapping("sync")
    public ResponseEntity<Collection<String>> getUrls() throws InterruptedException, ExecutionException {

        return ResponseEntity.ok(
                discoverUrl(urlsRoot.stream().findFirst().get()));
    }

    @GetMapping("paralell")
    public ResponseEntity<Collection<String>> getUrlsParalell() throws InterruptedException, ExecutionException {

        return ResponseEntity.ok(discoverUrlWithMultipleWorkers(urlsRoot.stream().findFirst().get()));
    }

    private Collection<String> discoverUrl(String root) throws InterruptedException, ExecutionException {
        Queue<String> queue = new LinkedList<>();
        Set<String> discoveredWebsitesList = new HashSet<>();

        queue.add(root);
        discoveredWebsitesList.add(root);

        while (!queue.isEmpty()) {
            String actualUrl = queue.remove();
            Set<String> discovered = crawl(actualUrl);
            for (String url : discovered) {
                if (!discoveredWebsitesList.contains(url)) {
                    log.info(url);
                    discoveredWebsitesList.add(url);
                    queue.add(url);
                }
            }
        }

        return discoveredWebsitesList;
    }

    private Collection<String> discoverUrlWithMultipleWorkers(String root)
            throws InterruptedException, ExecutionException {

        Queue<String> queue = new LinkedList<>();
        Set<String> discoveredWebsitesList = new HashSet<>();

        // Preenche fila com resultados da root
        discoveredWebsitesList.add(root);
        crawl(root).forEach(url -> {
            if (!discoveredWebsitesList.contains(url)) {
                discoveredWebsitesList.add(url);
                queue.add(url);
            }
        });

        // Pool suporta 16 threads paralelas, para processar urls da fila
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(16);
        while (!queue.isEmpty()) {
            // Código que executa em paralelo
            threadPool.submit(() -> {
                String actualUrl = queue.remove();
                Set<String> discovered = crawl(actualUrl);// retornar urls encontradas no html

                for (String url : discovered) {
                    if (!discoveredWebsitesList.contains(url)) {
                        // Monitor para controlar inserções paralelas e evitar condições de corrida
                        synchronized (discoveredWebsitesList) {
                            discoveredWebsitesList.add(url);
                        }
                        synchronized (queue) {
                            queue.add(url);
                        }
                    }

                }
            });
        }
        return discoveredWebsitesList;
    }

    private Set<String> crawl(String url) {
        Set<String> list = new HashSet<>();
        try {
            // Extrai documento HTML da url fornecida
            Document doc = Jsoup.connect(url)
                    .timeout(10 * 1000)// Tempo máximo de espera 10s
                    .get();

            Elements links = doc.select("a[href]");// Procura por tags que tenham seletor href. Exemplo: <a href=""></a>
            for (Element link : links) {
                String nextUrl = link.absUrl("href");// Obtém valor do href
                if (nextUrl.startsWith(url) && !nextUrl.contains("#")) {
                    // if (nextUrl.startsWith(url)) {
                    log.info(String.format("Titulo: %s, URL: %s", doc.select("title").text(), url));
                    list.add(nextUrl);
                }
            }
            return list;
        } catch (Exception e) {
            log.error("error fetching data.", e);
            return new HashSet<>();
        }
    }

}
