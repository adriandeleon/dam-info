package com.grokthecode.services;

import com.grokthecode.common.GlobalConstants;
import com.grokthecode.data.entities.DamCatalogEntity;
import com.grokthecode.data.repositories.DamCatalogRepository;
import com.grokthecode.models.restapi.PresasDto;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@Log4j2
public class DamCatalogService {

    public final DamCatalogRepository damCatalogRepository;

    @Value("${app.datasource.url}")
    private String appDatasourceUrl;

    public DamCatalogService(final DamCatalogRepository damCatalogRepository) {
        this.damCatalogRepository = damCatalogRepository;
    }

    public DamCatalogEntity createDamCatalog(final DamCatalogEntity damCatalogEntity) {
        Objects.requireNonNull(damCatalogEntity, "damCatalogEntity" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL);

        //Check that the Dam does not exist.
        if (damExistsBySihKey(damCatalogEntity.getSihKey())) {
            throw new IllegalArgumentException("Dam with the same sihKey already exists");
        }

        return damCatalogRepository.save(damCatalogEntity);
    }

    public List<DamCatalogEntity> listAllDams() {

        return List.copyOf(damCatalogRepository.findAll());
    }

    public void updateDamCatalog(final DamCatalogEntity updatedDamCatalogEntity) {
        Objects.requireNonNull(updatedDamCatalogEntity, "updatedDamCatalogEntity" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL);

        final Optional<DamCatalogEntity> optionalDamCatalogEntity = getDamCatalogById(updatedDamCatalogEntity.getId());

        if(optionalDamCatalogEntity.isEmpty()) {
            throw new IllegalArgumentException("Dam Catalog with id " + updatedDamCatalogEntity.getId() + " does not exist.");
        }

        final DamCatalogEntity originalDamCatalogEntity = optionalDamCatalogEntity.get();

        originalDamCatalogEntity.setOfficialName(updatedDamCatalogEntity.getOfficialName());
        originalDamCatalogEntity.setCommonName(updatedDamCatalogEntity.getCommonName());
        originalDamCatalogEntity.setCnaRegion(updatedDamCatalogEntity.getCnaRegion());
        originalDamCatalogEntity.setCurrents(updatedDamCatalogEntity.getCurrents());
        originalDamCatalogEntity.setLatitude(updatedDamCatalogEntity.getLatitude());
        originalDamCatalogEntity.setLongitude(updatedDamCatalogEntity.getLongitude());
        originalDamCatalogEntity.setMunicipality(updatedDamCatalogEntity.getMunicipality());
        originalDamCatalogEntity.setState(updatedDamCatalogEntity.getState());
        originalDamCatalogEntity.setUsage(updatedDamCatalogEntity.getUsage());
        originalDamCatalogEntity.setFreeBorder(updatedDamCatalogEntity.getFreeBorder());
        originalDamCatalogEntity.setElevationCrown(updatedDamCatalogEntity.getElevationCrown());
        originalDamCatalogEntity.setNameCapacity(updatedDamCatalogEntity.getNameCapacity());
        originalDamCatalogEntity.setVerterType(updatedDamCatalogEntity.getVerterType());
        originalDamCatalogEntity.setOperationStartYear(updatedDamCatalogEntity.getOperationStartYear());
        originalDamCatalogEntity.setNameElevation(updatedDamCatalogEntity.getNameElevation());

        damCatalogRepository.save(originalDamCatalogEntity);
    }

    public Pair<List<DamCatalogEntity>,List<String>> syncDamsCatalog() throws URISyntaxException {

        final RestClient restClient = RestClient.create();

        final String currentFormatedDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        final String endpoint = appDatasourceUrl + currentFormatedDate;

        final List<PresasDto> presasDtoList = restClient.get().uri(new URI(endpoint))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        final List<DamCatalogEntity>damCatalogEntityList = new ArrayList<>();
        final List<String> syncErrorMessageList = new ArrayList<>();

        for (final PresasDto presasDto : presasDtoList) {
            final DamCatalogEntity damCatalogEntity = new DamCatalogEntity(
                    presasDto.getClavesih(),
                    presasDto.getNombreoficial(),
                    presasDto.getNombrecomun(),
                    presasDto.getEstado(),
                    presasDto.getNommunicipio(),
                    presasDto.getRegioncna(),
                    presasDto.getLatitud(),
                    presasDto.getLongitud(),
                    presasDto.getUso(),
                    presasDto.getCorriente(),
                    presasDto.getTipovertedor(),
                    presasDto.getInicioop(),
                    presasDto.getElevcorona(),
                    presasDto.getBordolibre(),
                    presasDto.getNameelev(),
                    presasDto.getNamealmac(),
                    presasDto.getAlturacortina());

            if (damExistsBySihKey(damCatalogEntity.getSihKey())) {
                if(getDamCatalogBySihKey(damCatalogEntity.getSihKey()).isPresent()) { //TODO: I don't like this.
                    damCatalogEntity.setId(getDamCatalogBySihKey(damCatalogEntity.getSihKey()).get().getId());
                    updateDamCatalog(damCatalogEntity);
                }
            } else {

                try {
                  damCatalogEntityList.add(createDamCatalog(damCatalogEntity));
                } catch (IllegalArgumentException e) {
                    log.info(e.getMessage());
                    syncErrorMessageList.add(e.getMessage());
                }
            }
        }
        return Pair.of(damCatalogEntityList, syncErrorMessageList);
    }

    public Optional<DamCatalogEntity> getDamCatalogById(final Long damCatalogId) {
        Objects.requireNonNull(damCatalogId, "damCatalogId" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL);

        return damCatalogRepository.findById(damCatalogId);
    }

    public Optional<DamCatalogEntity> getDamCatalogBySihKey(final String sihKey) {
        Objects.requireNonNull(sihKey, "sihKey" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL);

        return damCatalogRepository.findBySihKey(sihKey);
    }

    public List<DamCatalogEntity> getDamCatalogByState(final String state) {
        Objects.requireNonNull(state, "state" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL);

        return List.copyOf(damCatalogRepository.findByState(state));
    }

    public boolean damExistsBySihKey(final String sihKey) {
        Objects.requireNonNull(sihKey, "sihKey" + GlobalConstants.MESSAGE_MUST_NOT_BE_NULL);

        return damCatalogRepository.findBySihKey(sihKey).isPresent();
    }
}
