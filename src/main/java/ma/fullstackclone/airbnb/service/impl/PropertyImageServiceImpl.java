package ma.fullstackclone.airbnb.service.impl;

import java.util.Optional;
import ma.fullstackclone.airbnb.domain.PropertyImage;
import ma.fullstackclone.airbnb.repository.PropertyImageRepository;
import ma.fullstackclone.airbnb.repository.search.PropertyImageSearchRepository;
import ma.fullstackclone.airbnb.service.PropertyImageService;
import ma.fullstackclone.airbnb.service.dto.PropertyImageDTO;
import ma.fullstackclone.airbnb.service.mapper.PropertyImageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ma.fullstackclone.airbnb.domain.PropertyImage}.
 */
@Service
@Transactional
public class PropertyImageServiceImpl implements PropertyImageService {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyImageServiceImpl.class);

    private final PropertyImageRepository propertyImageRepository;

    private final PropertyImageMapper propertyImageMapper;

    private final PropertyImageSearchRepository propertyImageSearchRepository;

    public PropertyImageServiceImpl(
        PropertyImageRepository propertyImageRepository,
        PropertyImageMapper propertyImageMapper,
        PropertyImageSearchRepository propertyImageSearchRepository
    ) {
        this.propertyImageRepository = propertyImageRepository;
        this.propertyImageMapper = propertyImageMapper;
        this.propertyImageSearchRepository = propertyImageSearchRepository;
    }

    @Override
    public PropertyImageDTO save(PropertyImageDTO propertyImageDTO) {
        LOG.debug("Request to save PropertyImage : {}", propertyImageDTO);
        PropertyImage propertyImage = propertyImageMapper.toEntity(propertyImageDTO);
        propertyImage = propertyImageRepository.save(propertyImage);
        propertyImageSearchRepository.index(propertyImage);
        return propertyImageMapper.toDto(propertyImage);
    }

    @Override
    public PropertyImageDTO update(PropertyImageDTO propertyImageDTO) {
        LOG.debug("Request to update PropertyImage : {}", propertyImageDTO);
        PropertyImage propertyImage = propertyImageMapper.toEntity(propertyImageDTO);
        propertyImage = propertyImageRepository.save(propertyImage);
        propertyImageSearchRepository.index(propertyImage);
        return propertyImageMapper.toDto(propertyImage);
    }

    @Override
    public Optional<PropertyImageDTO> partialUpdate(PropertyImageDTO propertyImageDTO) {
        LOG.debug("Request to partially update PropertyImage : {}", propertyImageDTO);

        return propertyImageRepository
            .findById(propertyImageDTO.getId())
            .map(existingPropertyImage -> {
                propertyImageMapper.partialUpdate(existingPropertyImage, propertyImageDTO);

                return existingPropertyImage;
            })
            .map(propertyImageRepository::save)
            .map(savedPropertyImage -> {
                propertyImageSearchRepository.index(savedPropertyImage);
                return savedPropertyImage;
            })
            .map(propertyImageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PropertyImageDTO> findOne(Long id) {
        LOG.debug("Request to get PropertyImage : {}", id);
        return propertyImageRepository.findById(id).map(propertyImageMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete PropertyImage : {}", id);
        propertyImageRepository.deleteById(id);
        propertyImageSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PropertyImageDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of PropertyImages for query {}", query);
        return propertyImageSearchRepository.search(query, pageable).map(propertyImageMapper::toDto);
    }
}
