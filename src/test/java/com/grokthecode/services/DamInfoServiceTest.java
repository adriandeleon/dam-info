package com.grokthecode.services;

import com.grokthecode.data.entities.DailyMeasurementEntity;
import com.grokthecode.data.entities.DamCatalogEntity;
import com.grokthecode.data.repositories.DailyMeasurementRepository;
import com.grokthecode.data.repositories.DamCatalogRepository;
import com.grokthecode.data.responses.DamInfoResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Tag("UnitTest")
public class DamInfoServiceTest {

    static DamCatalogRepository damCatalogRepository;
    static DailyMeasurementRepository dailyMeasurementRepository;
    static DamInfoService damInfoService;
    static DamCatalogEntity damCatalogEntity;
    static List<DamCatalogEntity> damCatalogEntityList;
    List<DailyMeasurementEntity> dailyMeasurementEntityList;

    @BeforeAll
    public static void setUp() {
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
        //given
        final List<DailyMeasurementEntity> dailyMeasurementEntityList = new ArrayList<>();

        //when
        Mockito.when(dailyMeasurementRepository.findByDamCatalogEntityOrderByMeasurementDateDesc(damCatalogEntity)).thenReturn(dailyMeasurementEntityList);
        final List<DamInfoResponse> responses = damInfoService.getDamsInfo();

        //then
        verifyDamInfoResponse(responses, dailyMeasurementEntityList);
    }

    @Test
    public void getDamsInfoByDates_GivenValidDates_ShouldReturnDamInfoList() {
        //given
        final List<DailyMeasurementEntity> dailyMeasurementEntities = new ArrayList<>();
        final LocalDate startDate = LocalDate.of(2023, 1, 1);
        final LocalDate endDate = LocalDate.of(2023, 12, 31);

        //when
        Mockito.when(dailyMeasurementRepository.findByMeasurementDateBetweenOrderByMeasurementDateDesc(Mockito.any(LocalDate.class), Mockito.any(LocalDate.class))).thenReturn(dailyMeasurementEntities);
        final List<DamInfoResponse> responses = damInfoService.getDamsInfoByDates(startDate, endDate);

        //then
        verifyDamInfoResponse(responses, dailyMeasurementEntities);
    }

    // Extracted method to verify
    private void verifyDamInfoResponse(final List<DamInfoResponse> damInfoResponses,
                                       final List<DailyMeasurementEntity> expectedMeasurements) {
        final DamInfoResponse damInfoResponse = damInfoResponses.getFirst();

        Assertions.assertEquals(1, damInfoResponses.size());
        verifyDamInfoResponse(damInfoResponse, expectedMeasurements);
    }

    private void verifyDamInfoResponse(final DamInfoResponse damInfoResponse,
                                       final List<DailyMeasurementEntity> expectedMeasurements) {

        Assertions.assertEquals(damCatalogEntity, damInfoResponse.dam());
        Assertions.assertEquals(expectedMeasurements, damInfoResponse.dailyMeasurementList());
    }
    @Test
    public void getDamsInfoByState_GivenState_ShouldReturnDamInfoList() {
        //given
        final String state = "Aguascalientes";
        final List<DailyMeasurementEntity> dailyMeasurementEntityList = new ArrayList<>();

        //when
        Mockito.when(damCatalogRepository.findByState(state)).thenReturn(damCatalogEntityList);
        Mockito.when(dailyMeasurementRepository.findByDamCatalogEntityOrderByMeasurementDateDesc(damCatalogEntity)).thenReturn(dailyMeasurementEntityList);

        //then
        final List<DamInfoResponse> responses = damInfoService.getDamsInfoByState(state);
        verifyDamInfoResponse(responses, dailyMeasurementEntityList);
    }

    @Test
    public void getDamsInfoByState_GivenInvalidState_ShouldReturnEmptyList() {
        //given
        final String state = "InvalidState";

        //when
        Mockito.when(damCatalogRepository.findByState(state)).thenReturn(new ArrayList<>());
        final List<DamInfoResponse> responses = damInfoService.getDamsInfoByState(state);

        //then
        Assertions.assertTrue(responses.isEmpty());
    }

    @Test
    public void getDamsInfoByState_NullState_ShouldThrowNullPointerException() {
        //when/then
        Assertions.assertThrows(NullPointerException.class, () -> damInfoService.getDamsInfoByState(null));
    }

    @Test
    public void getDamsInfoBySihKey_GivenSihKey_ShouldReturnDamInfoResponse() {
        //given
        final String sihKey = "12345";

        //when
        Mockito.when(damCatalogRepository.findBySihKey(sihKey)).thenReturn(Optional.of(damCatalogEntity));
        Mockito.when(dailyMeasurementRepository.findByDamCatalogEntityOrderByMeasurementDateDesc(damCatalogEntity)).thenReturn(dailyMeasurementEntityList);
        final DamInfoResponse response = damInfoService.getDamsInfoBySihKey(sihKey);

        //then
        verifyDamInfoResponse(response, dailyMeasurementEntityList);
    }

    @Test
    public void getDamsInfoBySihKey_GivenNonExistingSihKey_ShouldThrowIllegalArgumentException() {
        //given
        final String sihKey = "nonExistingKey";

        //when
        Mockito.when(damCatalogRepository.findBySihKey(sihKey)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(IllegalArgumentException.class, () -> damInfoService.getDamsInfoBySihKey(sihKey));
    }

    @Test
    public void getDamsInfoBySihKey_GivenInvalidSihKey_ShouldThrowIllegalArgumentException() {
        //given
        final String sihKey = "InvalidSihKey";

        //when
        Mockito.when(damCatalogRepository.findBySihKey(sihKey)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(IllegalArgumentException.class, () -> damInfoService.getDamsInfoBySihKey(sihKey));
    }

    @Test
    public void getDamsInfoBySihKey_NullSihKey_ShouldThrowNullPointerException() {
        //when/then
        Assertions.assertThrows(NullPointerException.class, () -> damInfoService.getDamsInfoBySihKey(null));
    }

    @Test
    public void getDamsInfoBySihKey_GivenSihKeyAndValidDates_ShouldReturnDamInfoResponse() {
        //given
        final String sihKey = "12345";
        final LocalDate startDate = LocalDate.of(2023, 1, 1);
        final LocalDate endDate = LocalDate.of(2023, 12, 31);

        //when
        Mockito.when(damCatalogRepository.findBySihKey(sihKey)).thenReturn(Optional.of(damCatalogEntity));
        Mockito.when(dailyMeasurementRepository.findByDamCatalogEntityAndMeasurementDateBetweenOrderByMeasurementDateDesc(damCatalogEntity, startDate, endDate)).thenReturn(dailyMeasurementEntityList);
        final DamInfoResponse response = damInfoService.getDamsInfoBySihKey(sihKey, startDate, endDate);

        //then
        verifyDamInfoResponse(response, dailyMeasurementEntityList);
    }

    @Test
    public void getDamsInfoBySihKey_GivenNonExistingSihKeyAndValidDates_ShouldThrowIllegalArgumentException() {
       //given
       final String sihKey = "nonExistingKey";
       final LocalDate startDate = LocalDate.of(2023, 1, 1);
       final LocalDate endDate = LocalDate.of(2023, 12, 31);

       //when
       Mockito.when(damCatalogRepository.findBySihKey(sihKey)).thenReturn(Optional.empty());

       //then
       Assertions.assertThrows(IllegalArgumentException.class, () -> damInfoService.getDamsInfoBySihKey(sihKey, startDate, endDate));
   }

    @Test
    public void getDamsInfoBySihKey_GivenInvalidSihKeyAndValidDates_ShouldThrowIllegalArgumentException() {
        //given
        final String sihKey = "InvalidSihKey";
        final LocalDate startDate = LocalDate.of(2023, 1, 1);
        final LocalDate endDate = LocalDate.of(2023, 12, 31);

        //when
        Mockito.when(damCatalogRepository.findBySihKey(sihKey)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(IllegalArgumentException.class, () -> damInfoService.getDamsInfoBySihKey(sihKey, startDate, endDate));
    }

    @Test
    public void getDamsInfoBySihKey_NullSihKeyAndValidDates_ShouldThrowNullPointerException() {
        //given
        final LocalDate startDate = LocalDate.of(2023, 1, 1);
        final LocalDate endDate = LocalDate.of(2023, 12, 31);

        //when/then
        Assertions.assertThrows(NullPointerException.class, () -> damInfoService.getDamsInfoBySihKey(null, startDate, endDate));
    }

    @Test
    public void getDamsInfoBySihKey_GivenSihKeyAndNullStartDate_ShouldThrowNullPointerException() {
        //given
        final String sihKey = "12345";
        final LocalDate endDate = LocalDate.of(2023, 12, 31);

        //when/then
        Assertions.assertThrows(NullPointerException.class, () -> damInfoService.getDamsInfoBySihKey(sihKey, null, endDate));
    }

    @Test
    public void getDamsInfoBySihKey_GivenSihKeyAndNullEndDate_ShouldThrowNullPointerException() {
        //given
        final String sihKey = "12345";
        final LocalDate startDate = LocalDate.of(2023, 1, 1);

        //when/then
        Assertions.assertThrows(NullPointerException.class, () -> damInfoService.getDamsInfoBySihKey(sihKey, startDate, null));
    }
}