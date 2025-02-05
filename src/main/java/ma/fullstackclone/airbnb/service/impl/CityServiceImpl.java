package ma.fullstackclone.airbnb.service.impl;

import java.util.Optional;
import ma.fullstackclone.airbnb.domain.City;
import ma.fullstackclone.airbnb.repository.CityRepository;
import ma.fullstackclone.airbnb.repository.search.CitySearchRepository;
import ma.fullstackclone.airbnb.service.CityService;
import ma.fullstackclone.airbnb.service.dto.CityDTO;
import ma.fullstackclone.airbnb.service.mapper.CityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ma.fullstackclone.airbnb.domain.City}.
 */
@Service
@Transactional
public class CityServiceImpl implements CityService {

    private static final Logger LOG = LoggerFactory.getLogger(CityServiceImpl.class);

    private final CityRepository cityRepository;

    private final CityMapper cityMapper;

    private final CitySearchRepository citySearchRepository;

    public CityServiceImpl(CityRepository cityRepository, CityMapper cityMapper, CitySearchRepository citySearchRepository) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
        this.citySearchRepository = citySearchRepository;
    }

    @Override
    public CityDTO save(CityDTO cityDTO) {
        LOG.debug("Request to save City : {}", cityDTO);
        City city = cityMapper.toEntity(cityDTO);
        city = cityRepository.save(city);
        citySearchRepository.index(city);
        return cityMapper.toDto(city);
    }

    @Override
    public CityDTO update(CityDTO cityDTO) {
        LOG.debug("Request to update City : {}", cityDTO);
        City city = cityMapper.toEntity(cityDTO);
        city = cityRepository.save(city);
        citySearchRepository.index(city);
        return cityMapper.toDto(city);
    }

    @Override
    public Optional<CityDTO> partialUpdate(CityDTO cityDTO) {
        LOG.debug("Request to partially update City : {}", cityDTO);

        return cityRepository
            .findById(cityDTO.getId())
            .map(existingCity -> {
                cityMapper.partialUpdate(existingCity, cityDTO);

                return existingCity;
            })
            .map(cityRepository::save)
            .map(savedCity -> {
                citySearchRepository.index(savedCity);
                return savedCity;
            })
            .map(cityMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CityDTO> findOne(Long id) {
        LOG.debug("Request to get City : {}", id);
        return cityRepository.findById(id).map(cityMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete City : {}", id);
        cityRepository.deleteById(id);
        citySearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CityDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Cities for query {}", query);
        return citySearchRepository.search(query, pageable).map(cityMapper::toDto);
    }
}
