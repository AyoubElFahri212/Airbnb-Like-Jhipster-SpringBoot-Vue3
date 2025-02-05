package ma.fullstackclone.airbnb.service.impl;

import java.util.Optional;
import ma.fullstackclone.airbnb.domain.Amenity;
import ma.fullstackclone.airbnb.repository.AmenityRepository;
import ma.fullstackclone.airbnb.repository.search.AmenitySearchRepository;
import ma.fullstackclone.airbnb.service.AmenityService;
import ma.fullstackclone.airbnb.service.dto.AmenityDTO;
import ma.fullstackclone.airbnb.service.mapper.AmenityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ma.fullstackclone.airbnb.domain.Amenity}.
 */
@Service
@Transactional
public class AmenityServiceImpl implements AmenityService {

    private static final Logger LOG = LoggerFactory.getLogger(AmenityServiceImpl.class);

    private final AmenityRepository amenityRepository;

    private final AmenityMapper amenityMapper;

    private final AmenitySearchRepository amenitySearchRepository;

    public AmenityServiceImpl(
        AmenityRepository amenityRepository,
        AmenityMapper amenityMapper,
        AmenitySearchRepository amenitySearchRepository
    ) {
        this.amenityRepository = amenityRepository;
        this.amenityMapper = amenityMapper;
        this.amenitySearchRepository = amenitySearchRepository;
    }

    @Override
    public AmenityDTO save(AmenityDTO amenityDTO) {
        LOG.debug("Request to save Amenity : {}", amenityDTO);
        Amenity amenity = amenityMapper.toEntity(amenityDTO);
        amenity = amenityRepository.save(amenity);
        amenitySearchRepository.index(amenity);
        return amenityMapper.toDto(amenity);
    }

    @Override
    public AmenityDTO update(AmenityDTO amenityDTO) {
        LOG.debug("Request to update Amenity : {}", amenityDTO);
        Amenity amenity = amenityMapper.toEntity(amenityDTO);
        amenity = amenityRepository.save(amenity);
        amenitySearchRepository.index(amenity);
        return amenityMapper.toDto(amenity);
    }

    @Override
    public Optional<AmenityDTO> partialUpdate(AmenityDTO amenityDTO) {
        LOG.debug("Request to partially update Amenity : {}", amenityDTO);

        return amenityRepository
            .findById(amenityDTO.getId())
            .map(existingAmenity -> {
                amenityMapper.partialUpdate(existingAmenity, amenityDTO);

                return existingAmenity;
            })
            .map(amenityRepository::save)
            .map(savedAmenity -> {
                amenitySearchRepository.index(savedAmenity);
                return savedAmenity;
            })
            .map(amenityMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AmenityDTO> findOne(Long id) {
        LOG.debug("Request to get Amenity : {}", id);
        return amenityRepository.findById(id).map(amenityMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Amenity : {}", id);
        amenityRepository.deleteById(id);
        amenitySearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AmenityDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Amenities for query {}", query);
        return amenitySearchRepository.search(query, pageable).map(amenityMapper::toDto);
    }
}
