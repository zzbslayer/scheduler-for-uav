package com.github.zzbslayer.vnfdemo.service;

import com.github.zzbslayer.vnfdemo.entity.Dblock;
import com.github.zzbslayer.vnfdemo.repo.DblockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LockService {
    @Autowired
    DblockRepository dblockRepository;

    public Dblock lock(String resource, String description) {
        log.debug("Try to get lock {}", resource);
        Dblock dblock = null;
        while (dblock == null) {
            try {
                dblock = dblockRepository.save(Dblock.builder()
                        .resource(resource)
                        .description(description)
                        .build());
            }
            catch (DataIntegrityViolationException e) {
                log.debug("Fail to get lock {}. Retrying...", resource);
            }
        }
        log.debug("Succeed to get lock {}", resource);
        return dblock;
    }

    public Dblock lock(String resource) {
        return lock(resource, "");
    }

    public void release(Dblock dblock) {
        dblockRepository.delete(dblock);
    }
}
