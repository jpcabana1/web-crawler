package com.example.tasks.service.Impl;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.example.tasks.service.IRandomService;

@Service
public class RandomServiceImpl implements IRandomService {

    @Override
    public Long generateRandomTime() {
        long min = 1;
        long max = 10;

        Random random = new Random();

        Long number = random.nextLong(max - min + 1) + min;
        return number * 1000;
    }

}
