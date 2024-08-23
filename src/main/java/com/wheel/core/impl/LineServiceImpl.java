package com.wheel.core.impl;

import com.wheel.core.entity.main.Line;
import com.wheel.core.repository.LineRepository;
import com.wheel.core.service.LineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LineServiceImpl implements LineService {
    private final LineRepository repository;

    @Override
    @Transactional
    public List<Line> getAll() {
        return repository.findAll();
    }
}
