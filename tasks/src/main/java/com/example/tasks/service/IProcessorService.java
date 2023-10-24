package com.example.tasks.service;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IProcessorService {
    CompletableFuture<Void> processTask(List<HashMap<String, Object>> list, HashMap<String, Object> map, int index);
    CompletableFuture<Void> processCount(Integer id);
}
