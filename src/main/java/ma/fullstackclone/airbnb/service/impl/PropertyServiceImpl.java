package ma.fullstackclone.airbnb.service.impl;

import java.util.Optional;
import ma.fullstackclone.airbnb.domain.Property;
import ma.fullstackclone.airbnb.repository.PropertyRepository;
import ma.fullstackclone.airbnb.repository.search.PropertySearchRepository;
import ma.fullstackclone.airbnb.service.PropertyService;
import ma.fullstackclone.airbnb.service.dto.PropertyDTO;
import ma.fullstackclone.airbnb.service.mapper.PropertyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ma.fullstackclone.airbnb.domain.Property}.
 */
@Service
@Transactional
public class PropertyServiceImpl implements PropertyService {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyServiceImpl.class);

    private final PropertyRepository propertyRepository;

    private final PropertyMapper propertyMapper;

    private final PropertySearchRepository propertySearchRepository;

    public PropertyServiceImpl(
        PropertyRepository propertyRepository,
        PropertyMapper propertyMapper,
        PropertySearchRepository propertySearchRepository
    ) {
        this.propertyRepository = propertyRepository;
        this.propertyMapper = propertyMapper;
        this.propertySearchRepository = propertySearchRepository;
    }

    @Override
    public PropertyDTO save(PropertyDTO propertyDTO) {
        LOG.debug("Request to save Property : {}", propertyDTO);
        Property property = propertyMapper.toEntity(propertyDTO);
        property = propertyRepository.save(property);
        propertySearchRepository.index(property);
        return propertyMapper.toDto(property);
    }

    @Override
    public PropertyDTO update(PropertyDTO propertyDTO) {
        LOG.debug("Request to update Property : {}", propertyDTO);
        Property property = propertyMapper.toEntity(propertyDTO);
        property = propertyRepository.save(property);
        propertySearchRepository.index(property);
        return propertyMapper.toDto(property);
    }

    @Override
    public Optional<PropertyDTO> partialUpdate(PropertyDTO propertyDTO) {
        LOG.debug("Request to partially update Property : {}", propertyDTO);

        return propertyRepository
            .findById(propertyDTO.getId())
            .map(existingProperty -> {
                propertyMapper.partialUpdate(existingProperty, propertyDTO);

                return existingProperty;
            })
            .map(propertyRepository::save)
            .map(savedProperty -> {
                propertySearchRepository.index(savedProperty);
                return savedProperty;
            })
            .map(propertyMapper::toDto);
    }

    public Page<PropertyDTO> findAllWithEagerRelationships(Pageable pageable) {
        return propertyRepository.findAllWithEagerRelationships(pageable).map(propertyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PropertyDTO> findOne(Long id) {
        LOG.debug("Request to get Property : {}", id);
        return propertyRepository.findOneWithEagerRelationships(id).map(propertyMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Property : {}", id);
        propertyRepository.deleteById(id);
        propertySearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PropertyDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Properties for query {}", query);
        return propertySearchRepository.search(query, pageable).map(propertyMapper::toDto);
    }
}
