package com.example.kbok.domain.player.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kbok.domain.player.dto.PitcherListItem;
import com.example.kbok.domain.player.dto.PitcherSearchCondition;
import com.example.kbok.domain.player.repository.PitcherStatQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PitcherQueryService {
    private final PitcherStatQueryRepository pitcherStatQueryRepository;

    //목록 조회
    public Page<PitcherListItem> search(PitcherSearchCondition cond, Pageable pageable){
        return pitcherStatQueryRepository.search(cond, pageable);
    }

}
