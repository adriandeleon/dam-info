package com.grokthecode.services;

import com.grokthecode.data.entities.DailyMeasurementEntity;
import com.grokthecode.data.entities.DamCatalogEntity;
import com.grokthecode.data.repositories.DailyMeasurementRepository;
import com.grokthecode.data.repositories.DamCatalogRepository;
import com.grokthecode.data.responses.DamInfoResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Log4j2
public class DamInfoService {

    final private DamCatalogRepository damCatalogRepository;
    final private DailyMeasurementRepository dailyMeasurementRepository;

    public DamInfoService(final DamCatalogRepository damCatalogRepository,
                          final DailyMeasurementRepository dailyMeasurementRepository) {
        this.damCatalogRepository = damCatalogRepository;
        this.dailyMeasurementRepository = dailyMeasurementRepository;
    }

    public List<DamInfoResponse> getAllDamsInfo() {

        List<DamInfoResponse> damInfoResponseList = new ArrayList<>();
        for (DamCatalogEntity damCatalogEntity : damCatalogRepository.findAll()) {
            List<DailyMeasurementEntity> dailyMeasurementEntityList = dailyMeasurementRepository.findByDamCatalogEntityOrderByIdDesc(damCatalogEntity);
            final DamInfoResponse damInfoResponse = new DamInfoResponse(damCatalogEntity, dailyMeasurementEntityList);
            damInfoResponseList.add(damInfoResponse);
        }
        return damInfoResponseList;
    }
}
