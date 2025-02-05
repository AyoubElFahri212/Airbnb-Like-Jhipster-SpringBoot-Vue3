package ma.fullstackclone.airbnb.service.impl;

import java.util.Optional;
import ma.fullstackclone.airbnb.domain.PropertyCategory;
import ma.fullstackclone.airbnb.repository.PropertyCategoryRepository;
import ma.fullstackclone.airbnb.repository.search.PropertyCategorySearchRepository;
import ma.fullstackclone.airbnb.service.PropertyCategoryService;
import ma.fullstackclone.airbnb.service.dto.PropertyCategoryDTO;
import ma.fullstackclone.airbnb.service.mapper.PropertyCategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ma.fullstackclone.airbnb.domain.PropertyCategory}.
 */
@Service
@Transactional
public class PropertyCategoryServiceImpl implements PropertyCategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyCategoryServiceImpl.class);

    private final PropertyCategoryRepository propertyCategoryRepository;

    private final PropertyCategoryMapper propertyCategoryMapper;

    private final PropertyCategorySearchRepository propertyCategorySearchRepository;

    public PropertyCategoryServiceImpl(
        PropertyCategoryRepository propertyCategoryRepository,
        PropertyCategoryMapper propertyCategoryMapper,
        PropertyCategorySearchRepository propertyCategorySearchRepository
    ) {
        this.propertyCategoryRepository = propertyCategoryRepository;
        this.propertyCategoryMapper = propertyCategoryMapper;
        this.propertyCategorySearchRepository = propertyCategorySearchRepository;
    }

    @Override
    public PropertyCategoryDTO save(PropertyCategoryDTO propertyCategoryDTO) {
        LOG.debug("Request to save PropertyCategory : {}", propertyCategoryDTO);
        PropertyCategory propertyCategory = propertyCategoryMapper.toEntity(propertyCategoryDTO);
        propertyCategory = propertyCategoryRepository.save(propertyCategory);
        propertyCategorySearchRepository.index(propertyCategory);
        return propertyCategoryMapper.toDto(propertyCategory);
    }

    @Override
    public PropertyCategoryDTO update(PropertyCategoryDTO propertyCategoryDTO) {
        LOG.debug("Request to update PropertyCategory : {}", propertyCategoryDTO);
        PropertyCategory propertyCategory = propertyCategoryMapper.toEntity(propertyCategoryDTO);
        propertyCategory = propertyCategoryRepository.save(propertyCategory);
        propertyCategorySearchRepository.index(propertyCategory);
        return propertyCategoryMapper.toDto(propertyCategory);
    }

    @Override
    public Optional<PropertyCategoryDTO> partialUpdate(PropertyCategoryDTO propertyCategoryDTO) {
        LOG.debug("Request to partially update PropertyCategory : {}", propertyCategoryDTO);

        return propertyCategoryRepository
            .findById(propertyCategoryDTO.getId())
            .map(existingPropertyCategory -> {
                propertyCategoryMapper.partialUpdate(existingPropertyCategory, propertyCategoryDTO);

                return existingPropertyCategory;
            })
            .map(propertyCategoryRepository::save)
            .map(savedPropertyCategory -> {
                propertyCategorySearchRepository.index(savedPropertyCategory);
                return savedPropertyCategory;
            })
            .map(propertyCategoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PropertyCategoryDTO> findOne(Long id) {
        LOG.debug("Request to get PropertyCategory : {}", id);
        return propertyCategoryRepository.findById(id).map(propertyCategoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete PropertyCategory : {}", id);
        propertyCategoryRepository.deleteById(id);
        propertyCategorySearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PropertyCategoryDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of PropertyCategories for query {}", query);
        return propertyCategorySearchRepository.search(query, pageable).map(propertyCategoryMapper::toDto);
    }
}
