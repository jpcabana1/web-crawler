package com.example.tasks.controller;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tasks.service.IProcessorService;
import com.example.tasks.service.IRandomService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/api/task")
public class TaskController {

    private final IProcessorService processorService;
    private final IRandomService randomService;

    public TaskController(IProcessorService processorService, IRandomService randomService) {
        this.processorService = processorService;
        this.randomService = randomService;
    }

    @GetMapping
    public ResponseEntity<List<HashMap<String, Object>>> getSimpleTask()
            throws InterruptedException, ExecutionException {

        List<HashMap<String, Object>> list = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        List<CompletableFuture<Void>> tasks = new LinkedList<>();

        List<Integer> numbers = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 9, 10);

        numbers.parallelStream().forEach(n -> {
            tasks.add(processorService.processTask(list, map, 0));

        });

        CompletableFuture<Void> allOf = CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]));
        try {
            allOf.get();
            log.info("All tasks completed successfully.");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(list);
    }

    @GetMapping("/counter")
    public ResponseEntity<String> getCounter() {

        List<Integer> numbers = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 9, 10);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

        numbers.parallelStream().forEach(n -> {
            executor.submit(() -> {
                try {
                    Long time = randomService.generateRandomTime();
                    Thread.sleep(time);
                    log.info(String.format("Task: %s, time: %s", n, time));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });
        });

        log.info("Ok");
        return ResponseEntity.ok("Ok");
    }
}