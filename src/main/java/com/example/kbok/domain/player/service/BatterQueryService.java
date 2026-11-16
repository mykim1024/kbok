package com.example.kbok.domain.player.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kbok.domain.player.dto.BatterListItem;
import com.example.kbok.domain.player.dto.BatterSearchCondition;
import com.example.kbok.domain.player.repository.BatterStatQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BatterQueryService {
    
    private final BatterStatQueryRepository batterStatQueryRepository;

    public Page<BatterListItem> search(BatterSearchCondition cond, Pageable pageable){
        return batterStatQueryRepository.search(cond, pageable);
    }
}
