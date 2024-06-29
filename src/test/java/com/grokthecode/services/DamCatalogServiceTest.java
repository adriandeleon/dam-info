package com.grokthecode.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grokthecode.data.entities.DamCatalogEntity;
import com.grokthecode.data.repositories.DamCatalogRepository;
import com.grokthecode.models.restapi.PresasDto;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(DamCatalogService.class)
public class DamCatalogServiceTest {

    static MockRestServiceServer mockServer;

    @MockBean
    DamCatalogRepository damCatalogRepository;

    @Autowired
    DamCatalogService damCatalogService;

    @Autowired
    static RestClient restClient;
    @Autowired
    ObjectMapper objectMapper;
    static String endpoint = "https://my.endpoint.com/";

    @BeforeAll
    public static void setUp() {
        //mockServer = MockRestServiceServer.bindTo(restClient);
        //damCatalogRepository = Mockito.mock(DamCatalogRepository.class);
    }

    @Test
    public void testCreateDamCatalog_WhenDamCatalogDoesNotExist() {

        //given
        final DamCatalogEntity damCatalogEntity = new DamCatalogEntity();
        damCatalogEntity.setSihKey("myKey");

        //when
        Mockito.when(damCatalogRepository.findBySihKey(Mockito.anyString()))
                .thenReturn(Optional.empty());
        Mockito.when(damCatalogRepository.save(Mockito.any(DamCatalogEntity.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        final DamCatalogEntity result = damCatalogService.createDamCatalog(damCatalogEntity);

        //then
        Assertions.assertEquals(damCatalogEntity, result);
    }

    @Test
    public void testCreateDamCatalog_WhenDamCatalogAlreadyExist() {
        //given
        final DamCatalogEntity damCatalogEntity = new DamCatalogEntity();
        damCatalogEntity.setSihKey("test");

        //when
        Mockito.when(damCatalogRepository.findBySihKey(Mockito.anyString()))
                .thenReturn(Optional.of(damCatalogEntity));

        //then
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                damCatalogService.createDamCatalog(damCatalogEntity));
    }

    @Test
    public void testListAllDams_WhenDamsExist() {
        //given
        final DamCatalogEntity damCatalogEntity1 = new DamCatalogEntity();
        final DamCatalogEntity damCatalogEntity2 = new DamCatalogEntity();

        final List<DamCatalogEntity> damList = Arrays.asList(damCatalogEntity1, damCatalogEntity2);

        //when
        Mockito.when(damCatalogRepository.findAll())
                .thenReturn(damList);

        final List<DamCatalogEntity> result = damCatalogService.listAllDams();

        //then
        Assertions.assertEquals(damList.size(), result.size());
        Assertions.assertEquals(damList, result);
    }

    @Test
    public void testListAllDams_WhenNoDamsExist() {
        //given
        final List<DamCatalogEntity> damList = new ArrayList<>();

        //when
        Mockito.when(damCatalogRepository.findAll())
                .thenReturn(damList);

        final List<DamCatalogEntity> result = damCatalogService.listAllDams();

        //then
        Assertions.assertEquals(damList.size(), result.size());
    }
    @Test
    public void testUpdateDamCatalog_WhenDamCatalogAlreadyExists() {
        //given
        final DamCatalogEntity originalDamCatalogEntity = new DamCatalogEntity();
        final DamCatalogEntity updatedDamCatalogEntity = new DamCatalogEntity();

        originalDamCatalogEntity.setId(1L);
        updatedDamCatalogEntity.setId(1L);
        updatedDamCatalogEntity.setOfficialName("UpdatedName");

        //when
        Mockito.when(damCatalogRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(originalDamCatalogEntity));
        Mockito.when(damCatalogRepository.save(Mockito.any(DamCatalogEntity.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        damCatalogService.updateDamCatalog(updatedDamCatalogEntity);

        //then
        Assertions.assertEquals(updatedDamCatalogEntity.getOfficialName(), originalDamCatalogEntity.getOfficialName());
    }

   @Test
   public void testUpdateDamCatalog_WhenDamCatalogDoesNotExists() {
       //given
       final DamCatalogEntity updatedDamCatalogEntity = new DamCatalogEntity();
       updatedDamCatalogEntity.setId(1L);

       //when
       Mockito.when(damCatalogRepository.findById(Mockito.anyLong()))
               .thenReturn(Optional.empty());

       //then
       Assertions.assertThrows(IllegalArgumentException.class, () ->
               damCatalogService.updateDamCatalog(updatedDamCatalogEntity));
   }
   @Test
   @Disabled
   public void testSyncDamsCatalog_WhenNewDamDataExists() throws URISyntaxException, JsonProcessingException {
       //given
       final DamCatalogEntity damCatalogEntity1 = new DamCatalogEntity();
       damCatalogEntity1.setSihKey("newKey1");
       damCatalogEntity1.setOfficialName("newOfficialName1");

       final DamCatalogEntity damCatalogEntity2 = new DamCatalogEntity();
       damCatalogEntity2.setSihKey("newKey2");
       damCatalogEntity2.setOfficialName("newOfficialName2");
       final List<PresasDto> presasDtoList = new ArrayList<>();

       //when
       Mockito.when(damCatalogRepository.findBySihKey("newKey1")).thenReturn(Optional.empty());
       Mockito.when(damCatalogRepository.findBySihKey("newKey2")).thenReturn(Optional.empty());
       Mockito.when(damCatalogRepository.save(Mockito.any(DamCatalogEntity.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

       mockServer.expect(requestTo(endpoint)).andRespond(withSuccess(objectMapper.writeValueAsString(presasDtoList), MediaType.APPLICATION_JSON));

       final List<DamCatalogEntity> initialDamList = damCatalogService.listAllDams();
       final Pair<List<DamCatalogEntity>, List<String>> result = damCatalogService.syncDamsCatalog();
       final List<DamCatalogEntity> finalDamList = damCatalogService.listAllDams();

       //then
       Assertions.assertEquals(initialDamList.size() + 2, finalDamList.size());
       Assertions.assertEquals(0, result.getRight().size());
       Assertions.assertNotEquals(result.getLeft(), initialDamList);
   }
   @Test
   @Disabled
   public void testSyncDamsCatalog_WhenExistingDamDataUpdated() throws URISyntaxException {
        //given
       final DamCatalogEntity damCatalogEntity1 = new DamCatalogEntity();
       damCatalogEntity1.setSihKey("existingKey1");
       damCatalogEntity1.setOfficialName("existingOfficialName1");

       final DamCatalogEntity damCatalogEntity2 = new DamCatalogEntity();
       damCatalogEntity2.setSihKey("existingKey2");
       damCatalogEntity2.setOfficialName("existingOfficialName2");

       //when
       Mockito.when(damCatalogRepository.findBySihKey("existingKey1")).thenReturn(Optional.of(damCatalogEntity1));
       Mockito.when(damCatalogRepository.findBySihKey("existingKey2")).thenReturn(Optional.of(damCatalogEntity2));
       Mockito.when(damCatalogRepository.save(Mockito.any(DamCatalogEntity.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

       final List<DamCatalogEntity> initialDamList = damCatalogService.listAllDams();
       final Pair<List<DamCatalogEntity>, List<String>> result = damCatalogService.syncDamsCatalog();
       final List<DamCatalogEntity> finalDamList = damCatalogService.listAllDams();

       //then
       Assertions.assertEquals(initialDamList.size(), finalDamList.size());
       Assertions.assertEquals(0, result.getRight().size());
       Assertions.assertNotEquals(result.getLeft(), initialDamList);
   }
}
