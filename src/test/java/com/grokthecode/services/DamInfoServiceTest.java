package com.grokthecode.services;

import com.grokthecode.data.entities.DailyMeasurementEntity;
import com.grokthecode.data.entities.DamCatalogEntity;
import com.grokthecode.data.repositories.DailyMeasurementRepository;
import com.grokthecode.data.repositories.DamCatalogRepository;
import com.grokthecode.data.responses.DamInfoResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DamInfoServiceTest {

    DamCatalogRepository damCatalogRepository;
    DailyMeasurementRepository dailyMeasurementRepository;
    DamInfoService damInfoService;
    DamCatalogEntity damCatalogEntity;
    List<DamCatalogEntity> damCatalogEntityList;
    List<DailyMeasurementEntity> dailyMeasurementEntityList;

    @BeforeEach
    public void setUp() {
        damCatalogRepository = Mockito.mock(DamCatalogRepository.class);
        dailyMeasurementRepository = Mockito.mock(DailyMeasurementRepository.class);

        damInfoService = new DamInfoService(damCatalogRepository, dailyMeasurementRepository);
        damCatalogEntity = new DamCatalogEntity();
        damCatalogEntityList = new ArrayList<>();
        damCatalogEntityList.add(damCatalogEntity);

        Mockito.when(damCatalogRepository.findAll()).thenReturn(damCatalogEntityList);
    }

    @Test
    public void getDamsInfo_GivenServiceCall_ShouldReturnDamInfoList() {
        // Specialize setup
        final List<DailyMeasurementEntity> dailyMeasurementEntityList = new ArrayList<>();
        Mockito.when(dailyMeasurementRepository.findByDamCatalogEntityOrderByMeasurementDateDesc(damCatalogEntity)).thenReturn(dailyMeasurementEntityList);

        // Call and verify
        final List<DamInfoResponse> responses = damInfoService.getDamsInfo();
        verifyDamInfoResponse(responses, dailyMeasurementEntityList);
    }

    @Test
    public void getDamsInfoByDates_GivenValidDates_ShouldReturnDamInfoList() {
        // Specialize setup
        final List<DailyMeasurementEntity> dailyMeasurementEntities = new ArrayList<>();
        Mockito.when(dailyMeasurementRepository.findByMeasurementDateBetweenOrderByMeasurementDateDesc(Mockito.any(LocalDate.class), Mockito.any(LocalDate.class))).thenReturn(dailyMeasurementEntities);

        // Call and verify
        final LocalDate startDate = LocalDate.of(2023, 1, 1);
        final LocalDate endDate = LocalDate.of(2023, 12, 31);
        final List<DamInfoResponse> responses = damInfoService.getDamsInfoByDates(startDate, endDate);
        verifyDamInfoResponse(responses, dailyMeasurementEntities);
    }

    // Extracted method to verify
    private void verifyDamInfoResponse(final List<DamInfoResponse> damInfoResponses, final List<DailyMeasurementEntity> expectedMeasurements) {
        Assertions.assertEquals(1, damInfoResponses.size());

        final DamInfoResponse damInfoResponse = damInfoResponses.getFirst();
        verifyDamInfoResponse(damInfoResponse, expectedMeasurements);
    }

    private void verifyDamInfoResponse(DamInfoResponse damInfoResponse, List<DailyMeasurementEntity> expectedMeasurements) {
        Assertions.assertEquals(damCatalogEntity, damInfoResponse.dam());
        Assertions.assertEquals(expectedMeasurements, damInfoResponse.dailyMeasurementList());
    }
    @Test
    public void getDamsInfoByState_GivenState_ShouldReturnDamInfoList() {
        // Specialize setup
        final String state = "Aguascalientes";
        final List<DailyMeasurementEntity> dailyMeasurementEntityList = new ArrayList<>();
        Mockito.when(damCatalogRepository.findByState(state)).thenReturn(damCatalogEntityList);
        Mockito.when(dailyMeasurementRepository.findByDamCatalogEntityOrderByMeasurementDateDesc(damCatalogEntity)).thenReturn(dailyMeasurementEntityList);

        // Call and verify
        final List<DamInfoResponse> responses = damInfoService.getDamsInfoByState(state);
        verifyDamInfoResponse(responses, dailyMeasurementEntityList);
    }

    @Test
    public void getDamsInfoByState_GivenInvalidState_ShouldReturnEmptyList() {
        // Specialize setup
        final String state = "InvalidState";
        Mockito.when(damCatalogRepository.findByState(state)).thenReturn(new ArrayList<>());

        // Call and verify
        final List<DamInfoResponse> responses = damInfoService.getDamsInfoByState(state);
        Assertions.assertTrue(responses.isEmpty());
    }

    @Test
    public void getDamsInfoByState_NullState_ShouldThrowNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> damInfoService.getDamsInfoByState(null));
    }

    @Test
    public void getDamsInfoBySihKey_GivenSihKey_ShouldReturnDamInfoResponse() {
        // Specialize setup
        final String sihKey = "12345";
        Mockito.when(damCatalogRepository.findBySihKey(sihKey)).thenReturn(Optional.of(damCatalogEntity));
        Mockito.when(dailyMeasurementRepository.findByDamCatalogEntityOrderByMeasurementDateDesc(damCatalogEntity)).thenReturn(dailyMeasurementEntityList);

        // Call and verify
        final DamInfoResponse response = damInfoService.getDamsInfoBySihKey(sihKey);
        verifyDamInfoResponse(response, dailyMeasurementEntityList);
    }

    @Test
    public void getDamsInfoBySihKey_GivenNonExistingSihKey_ShouldThrowIllegalArgumentException() {
        // Specialize setup
        final String sihKey = "nonExistingKey";
        Mockito.when(damCatalogRepository.findBySihKey(sihKey)).thenReturn(Optional.empty());

        // Call and verify
        Assertions.assertThrows(IllegalArgumentException.class, () -> damInfoService.getDamsInfoBySihKey(sihKey));
    }

    @Test
    public void getDamsInfoBySihKey_GivenInvalidSihKey_ShouldThrowIllegalArgumentException() {
        // Specialize setup
        final String sihKey = "InvalidSihKey";
        Mockito.when(damCatalogRepository.findBySihKey(sihKey)).thenReturn(Optional.empty());

        // Call and verify
        Assertions.assertThrows(IllegalArgumentException.class, () -> damInfoService.getDamsInfoBySihKey(sihKey));
    }

    @Test
    public void getDamsInfoBySihKey_NullSihKey_ShouldThrowNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> damInfoService.getDamsInfoBySihKey(null));
    }

    @Test
    public void getDamsInfoBySihKey_GivenSihKeyAndValidDates_ShouldReturnDamInfoResponse() {
        // Specialize setup
        final String sihKey = "12345";
        final LocalDate startDate = LocalDate.of(2023, 1, 1);
        final LocalDate endDate = LocalDate.of(2023, 12, 31);
        Mockito.when(damCatalogRepository.findBySihKey(sihKey)).thenReturn(Optional.of(damCatalogEntity));
        Mockito.when(dailyMeasurementRepository.findByDamCatalogEntityAndMeasurementDateBetweenOrderByMeasurementDateDesc(damCatalogEntity, startDate, endDate)).thenReturn(dailyMeasurementEntityList);

        // Call and verify
        final DamInfoResponse response = damInfoService.getDamsInfoBySihKey(sihKey, startDate, endDate);
        verifyDamInfoResponse(response, dailyMeasurementEntityList);
    }

    @Test
    public void getDamsInfoBySihKey_GivenNonExistingSihKeyAndValidDates_ShouldThrowIllegalArgumentException() {
       // Specialize setup
       final String sihKey = "nonExistingKey";
       final LocalDate startDate = LocalDate.of(2023, 1, 1);
       final LocalDate endDate = LocalDate.of(2023, 12, 31);
       Mockito.when(damCatalogRepository.findBySihKey(sihKey)).thenReturn(Optional.empty());

       // Call and verify
       Assertions.assertThrows(IllegalArgumentException.class, () -> damInfoService.getDamsInfoBySihKey(sihKey, startDate, endDate));
   }

    @Test
    public void getDamsInfoBySihKey_GivenInvalidSihKeyAndValidDates_ShouldThrowIllegalArgumentException() {
        // Specialize setup
        final String sihKey = "InvalidSihKey";
        final LocalDate startDate = LocalDate.of(2023, 1, 1);
        final LocalDate endDate = LocalDate.of(2023, 12, 31);
        Mockito.when(damCatalogRepository.findBySihKey(sihKey)).thenReturn(Optional.empty());

        // Call and verify
        Assertions.assertThrows(IllegalArgumentException.class, () -> damInfoService.getDamsInfoBySihKey(sihKey, startDate, endDate));
    }

    @Test
    public void getDamsInfoBySihKey_NullSihKeyAndValidDates_ShouldThrowNullPointerException() {
        final LocalDate startDate = LocalDate.of(2023, 1, 1);
        final LocalDate endDate = LocalDate.of(2023, 12, 31);
        Assertions.assertThrows(NullPointerException.class, () -> damInfoService.getDamsInfoBySihKey(null, startDate, endDate));
    }

    @Test
    public void getDamsInfoBySihKey_GivenSihKeyAndNullStartDate_ShouldThrowNullPointerException() {
        final String sihKey = "12345";
        final LocalDate endDate = LocalDate.of(2023, 12, 31);
        Assertions.assertThrows(NullPointerException.class, () -> damInfoService.getDamsInfoBySihKey(sihKey, null, endDate));
    }

    @Test
    public void getDamsInfoBySihKey_GivenSihKeyAndNullEndDate_ShouldThrowNullPointerException() {
        final String sihKey = "12345";
        final LocalDate startDate = LocalDate.of(2023, 1, 1);
        Assertions.assertThrows(NullPointerException.class, () -> damInfoService.getDamsInfoBySihKey(sihKey, startDate, null));
    }
}