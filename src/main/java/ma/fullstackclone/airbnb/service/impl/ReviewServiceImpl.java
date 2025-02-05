package ma.fullstackclone.airbnb.service.impl;

import java.util.Optional;
import ma.fullstackclone.airbnb.domain.Review;
import ma.fullstackclone.airbnb.repository.ReviewRepository;
import ma.fullstackclone.airbnb.repository.search.ReviewSearchRepository;
import ma.fullstackclone.airbnb.service.ReviewService;
import ma.fullstackclone.airbnb.service.dto.ReviewDTO;
import ma.fullstackclone.airbnb.service.mapper.ReviewMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ma.fullstackclone.airbnb.domain.Review}.
 */
@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewRepository reviewRepository;

    private final ReviewMapper reviewMapper;

    private final ReviewSearchRepository reviewSearchRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, ReviewMapper reviewMapper, ReviewSearchRepository reviewSearchRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.reviewSearchRepository = reviewSearchRepository;
    }

    @Override
    public ReviewDTO save(ReviewDTO reviewDTO) {
        LOG.debug("Request to save Review : {}", reviewDTO);
        Review review = reviewMapper.toEntity(reviewDTO);
        review = reviewRepository.save(review);
        reviewSearchRepository.index(review);
        return reviewMapper.toDto(review);
    }

    @Override
    public ReviewDTO update(ReviewDTO reviewDTO) {
        LOG.debug("Request to update Review : {}", reviewDTO);
        Review review = reviewMapper.toEntity(reviewDTO);
        review = reviewRepository.save(review);
        reviewSearchRepository.index(review);
        return reviewMapper.toDto(review);
    }

    @Override
    public Optional<ReviewDTO> partialUpdate(ReviewDTO reviewDTO) {
        LOG.debug("Request to partially update Review : {}", reviewDTO);

        return reviewRepository
            .findById(reviewDTO.getId())
            .map(existingReview -> {
                reviewMapper.partialUpdate(existingReview, reviewDTO);

                return existingReview;
            })
            .map(reviewRepository::save)
            .map(savedReview -> {
                reviewSearchRepository.index(savedReview);
                return savedReview;
            })
            .map(reviewMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReviewDTO> findOne(Long id) {
        LOG.debug("Request to get Review : {}", id);
        return reviewRepository.findById(id).map(reviewMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Review : {}", id);
        reviewRepository.deleteById(id);
        reviewSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Reviews for query {}", query);
        return reviewSearchRepository.search(query, pageable).map(reviewMapper::toDto);
    }
}
