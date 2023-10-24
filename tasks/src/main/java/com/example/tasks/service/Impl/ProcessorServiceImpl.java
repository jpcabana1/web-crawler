package com.example.tasks.service.Impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.tasks.service.IProcessorService;
import com.example.tasks.service.IRandomService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProcessorServiceImpl implements IProcessorService {

    private final IRandomService randomService;

    @Autowired
    public ProcessorServiceImpl(IRandomService randomService) {
        this.randomService = randomService;
    }

    @Async
    @Override
    public CompletableFuture<Void> processTask(List<HashMap<String, Object>> list, HashMap<String, Object> map,
            int index) {
        return CompletableFuture.runAsync(() -> {
            String message = "";

            try {
                Long time = randomService.generateRandomTime();
                // Long time = 5000L;

                message = String.format("Thread await time: %s, index: %s", time, index);
                log.info(message);

                Thread.sleep(time);

                synchronized (list) {
                    HashMap<String, Object> newMap = new HashMap<>();
                    newMap.put("index" + index,
                            Map.of(
                                    "index", index,
                                    "time", time,
                                    "message", message));
                    list.add(newMap);
                }
                log.info(String.format("time %s OK", time));
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        });

    }

    @Async
    @Override
    public CompletableFuture<Void> processCount(Integer id) {
        return CompletableFuture.runAsync(() -> {
            try {
                Long time = randomService.generateRandomTime();
                Thread.sleep(time);
                log.info(String.format("Task id: %s, time: %", id, time));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
    }

}
