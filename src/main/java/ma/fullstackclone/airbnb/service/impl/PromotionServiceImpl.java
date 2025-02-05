package ma.fullstackclone.airbnb.service.impl;

import java.util.Optional;
import ma.fullstackclone.airbnb.domain.Promotion;
import ma.fullstackclone.airbnb.repository.PromotionRepository;
import ma.fullstackclone.airbnb.repository.search.PromotionSearchRepository;
import ma.fullstackclone.airbnb.service.PromotionService;
import ma.fullstackclone.airbnb.service.dto.PromotionDTO;
import ma.fullstackclone.airbnb.service.mapper.PromotionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ma.fullstackclone.airbnb.domain.Promotion}.
 */
@Service
@Transactional
public class PromotionServiceImpl implements PromotionService {

    private static final Logger LOG = LoggerFactory.getLogger(PromotionServiceImpl.class);

    private final PromotionRepository promotionRepository;

    private final PromotionMapper promotionMapper;

    private final PromotionSearchRepository promotionSearchRepository;

    public PromotionServiceImpl(
        PromotionRepository promotionRepository,
        PromotionMapper promotionMapper,
        PromotionSearchRepository promotionSearchRepository
    ) {
        this.promotionRepository = promotionRepository;
        this.promotionMapper = promotionMapper;
        this.promotionSearchRepository = promotionSearchRepository;
    }

    @Override
    public PromotionDTO save(PromotionDTO promotionDTO) {
        LOG.debug("Request to save Promotion : {}", promotionDTO);
        Promotion promotion = promotionMapper.toEntity(promotionDTO);
        promotion = promotionRepository.save(promotion);
        promotionSearchRepository.index(promotion);
        return promotionMapper.toDto(promotion);
    }

    @Override
    public PromotionDTO update(PromotionDTO promotionDTO) {
        LOG.debug("Request to update Promotion : {}", promotionDTO);
        Promotion promotion = promotionMapper.toEntity(promotionDTO);
        promotion = promotionRepository.save(promotion);
        promotionSearchRepository.index(promotion);
        return promotionMapper.toDto(promotion);
    }

    @Override
    public Optional<PromotionDTO> partialUpdate(PromotionDTO promotionDTO) {
        LOG.debug("Request to partially update Promotion : {}", promotionDTO);

        return promotionRepository
            .findById(promotionDTO.getId())
            .map(existingPromotion -> {
                promotionMapper.partialUpdate(existingPromotion, promotionDTO);

                return existingPromotion;
            })
            .map(promotionRepository::save)
            .map(savedPromotion -> {
                promotionSearchRepository.index(savedPromotion);
                return savedPromotion;
            })
            .map(promotionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PromotionDTO> findOne(Long id) {
        LOG.debug("Request to get Promotion : {}", id);
        return promotionRepository.findById(id).map(promotionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Promotion : {}", id);
        promotionRepository.deleteById(id);
        promotionSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PromotionDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Promotions for query {}", query);
        return promotionSearchRepository.search(query, pageable).map(promotionMapper::toDto);
    }
}
