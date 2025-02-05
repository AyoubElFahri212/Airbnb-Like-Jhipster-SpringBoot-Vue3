package ma.fullstackclone.airbnb.web.rest;

import static ma.fullstackclone.airbnb.domain.ReviewAsserts.*;
import static ma.fullstackclone.airbnb.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import ma.fullstackclone.airbnb.IntegrationTest;
import ma.fullstackclone.airbnb.domain.Property;
import ma.fullstackclone.airbnb.domain.Review;
import ma.fullstackclone.airbnb.domain.User;
import ma.fullstackclone.airbnb.repository.ReviewRepository;
import ma.fullstackclone.airbnb.repository.UserRepository;
import ma.fullstackclone.airbnb.repository.search.ReviewSearchRepository;
import ma.fullstackclone.airbnb.service.dto.ReviewDTO;
import ma.fullstackclone.airbnb.service.mapper.ReviewMapper;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ReviewResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReviewResourceIT {

    private static final Integer DEFAULT_RATING = 1;
    private static final Integer UPDATED_RATING = 2;
    private static final Integer SMALLER_RATING = 1 - 1;

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_REVIEW_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REVIEW_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/reviews";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/reviews/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private ReviewSearchRepository reviewSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReviewMockMvc;

    private Review review;

    private Review insertedReview;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Review createEntity() {
        return new Review().rating(DEFAULT_RATING).comment(DEFAULT_COMMENT).reviewDate(DEFAULT_REVIEW_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Review createUpdatedEntity() {
        return new Review().rating(UPDATED_RATING).comment(UPDATED_COMMENT).reviewDate(UPDATED_REVIEW_DATE);
    }

    @BeforeEach
    public void initTest() {
        review = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedReview != null) {
            reviewRepository.delete(insertedReview);
            reviewSearchRepository.delete(insertedReview);
            insertedReview = null;
        }
    }

    @Test
    @Transactional
    void createReview() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reviewSearchRepository.findAll());
        // Create the Review
        ReviewDTO reviewDTO = reviewMapper.toDto(review);
        var returnedReviewDTO = om.readValue(
            restReviewMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReviewDTO.class
        );

        // Validate the Review in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedReview = reviewMapper.toEntity(returnedReviewDTO);
        assertReviewUpdatableFieldsEquals(returnedReview, getPersistedReview(returnedReview));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(reviewSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedReview = returnedReview;
    }

    @Test
    @Transactional
    void createReviewWithExistingId() throws Exception {
        // Create the Review with an existing ID
        review.setId(1L);
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reviewSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restReviewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reviewSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkRatingIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reviewSearchRepository.findAll());
        // set the field null
        review.setRating(null);

        // Create the Review, which fails.
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        restReviewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reviewSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkReviewDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reviewSearchRepository.findAll());
        // set the field null
        review.setReviewDate(null);

        // Create the Review, which fails.
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        restReviewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reviewSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllReviews() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList
        restReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(review.getId().intValue())))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].reviewDate").value(hasItem(DEFAULT_REVIEW_DATE.toString())));
    }

    @Test
    @Transactional
    void getReview() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get the review
        restReviewMockMvc
            .perform(get(ENTITY_API_URL_ID, review.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(review.getId().intValue()))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.reviewDate").value(DEFAULT_REVIEW_DATE.toString()));
    }

    @Test
    @Transactional
    void getReviewsByIdFiltering() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        Long id = review.getId();

        defaultReviewFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultReviewFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultReviewFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllReviewsByRatingIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where rating equals to
        defaultReviewFiltering("rating.equals=" + DEFAULT_RATING, "rating.equals=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByRatingIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where rating in
        defaultReviewFiltering("rating.in=" + DEFAULT_RATING + "," + UPDATED_RATING, "rating.in=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByRatingIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where rating is not null
        defaultReviewFiltering("rating.specified=true", "rating.specified=false");
    }

    @Test
    @Transactional
    void getAllReviewsByRatingIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where rating is greater than or equal to
        defaultReviewFiltering("rating.greaterThanOrEqual=" + DEFAULT_RATING, "rating.greaterThanOrEqual=" + (DEFAULT_RATING + 1));
    }

    @Test
    @Transactional
    void getAllReviewsByRatingIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where rating is less than or equal to
        defaultReviewFiltering("rating.lessThanOrEqual=" + DEFAULT_RATING, "rating.lessThanOrEqual=" + SMALLER_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByRatingIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where rating is less than
        defaultReviewFiltering("rating.lessThan=" + (DEFAULT_RATING + 1), "rating.lessThan=" + DEFAULT_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByRatingIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where rating is greater than
        defaultReviewFiltering("rating.greaterThan=" + SMALLER_RATING, "rating.greaterThan=" + DEFAULT_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByReviewDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where reviewDate equals to
        defaultReviewFiltering("reviewDate.equals=" + DEFAULT_REVIEW_DATE, "reviewDate.equals=" + UPDATED_REVIEW_DATE);
    }

    @Test
    @Transactional
    void getAllReviewsByReviewDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where reviewDate in
        defaultReviewFiltering("reviewDate.in=" + DEFAULT_REVIEW_DATE + "," + UPDATED_REVIEW_DATE, "reviewDate.in=" + UPDATED_REVIEW_DATE);
    }

    @Test
    @Transactional
    void getAllReviewsByReviewDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where reviewDate is not null
        defaultReviewFiltering("reviewDate.specified=true", "reviewDate.specified=false");
    }

    @Test
    @Transactional
    void getAllReviewsByAuthorIsEqualToSomething() throws Exception {
        User author;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            reviewRepository.saveAndFlush(review);
            author = UserResourceIT.createEntity();
        } else {
            author = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(author);
        em.flush();
        review.setAuthor(author);
        reviewRepository.saveAndFlush(review);
        Long authorId = author.getId();
        // Get all the reviewList where author equals to authorId
        defaultReviewShouldBeFound("authorId.equals=" + authorId);

        // Get all the reviewList where author equals to (authorId + 1)
        defaultReviewShouldNotBeFound("authorId.equals=" + (authorId + 1));
    }

    @Test
    @Transactional
    void getAllReviewsByPropertyIsEqualToSomething() throws Exception {
        Property property;
        if (TestUtil.findAll(em, Property.class).isEmpty()) {
            reviewRepository.saveAndFlush(review);
            property = PropertyResourceIT.createEntity();
        } else {
            property = TestUtil.findAll(em, Property.class).get(0);
        }
        em.persist(property);
        em.flush();
        review.setProperty(property);
        reviewRepository.saveAndFlush(review);
        Long propertyId = property.getId();
        // Get all the reviewList where property equals to propertyId
        defaultReviewShouldBeFound("propertyId.equals=" + propertyId);

        // Get all the reviewList where property equals to (propertyId + 1)
        defaultReviewShouldNotBeFound("propertyId.equals=" + (propertyId + 1));
    }

    private void defaultReviewFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultReviewShouldBeFound(shouldBeFound);
        defaultReviewShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultReviewShouldBeFound(String filter) throws Exception {
        restReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(review.getId().intValue())))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].reviewDate").value(hasItem(DEFAULT_REVIEW_DATE.toString())));

        // Check, that the count call also returns 1
        restReviewMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultReviewShouldNotBeFound(String filter) throws Exception {
        restReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReviewMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingReview() throws Exception {
        // Get the review
        restReviewMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReview() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        reviewSearchRepository.save(review);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reviewSearchRepository.findAll());

        // Update the review
        Review updatedReview = reviewRepository.findById(review.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReview are not directly saved in db
        em.detach(updatedReview);
        updatedReview.rating(UPDATED_RATING).comment(UPDATED_COMMENT).reviewDate(UPDATED_REVIEW_DATE);
        ReviewDTO reviewDTO = reviewMapper.toDto(updatedReview);

        restReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reviewDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO))
            )
            .andExpect(status().isOk());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReviewToMatchAllProperties(updatedReview);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(reviewSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Review> reviewSearchList = Streamable.of(reviewSearchRepository.findAll()).toList();
                Review testReviewSearch = reviewSearchList.get(searchDatabaseSizeAfter - 1);

                assertReviewAllPropertiesEquals(testReviewSearch, updatedReview);
            });
    }

    @Test
    @Transactional
    void putNonExistingReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reviewSearchRepository.findAll());
        review.setId(longCount.incrementAndGet());

        // Create the Review
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reviewDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reviewSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reviewSearchRepository.findAll());
        review.setId(longCount.incrementAndGet());

        // Create the Review
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reviewSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reviewSearchRepository.findAll());
        review.setId(longCount.incrementAndGet());

        // Create the Review
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reviewSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateReviewWithPatch() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the review using partial update
        Review partialUpdatedReview = new Review();
        partialUpdatedReview.setId(review.getId());

        partialUpdatedReview.reviewDate(UPDATED_REVIEW_DATE);

        restReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReview.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReview))
            )
            .andExpect(status().isOk());

        // Validate the Review in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReviewUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedReview, review), getPersistedReview(review));
    }

    @Test
    @Transactional
    void fullUpdateReviewWithPatch() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the review using partial update
        Review partialUpdatedReview = new Review();
        partialUpdatedReview.setId(review.getId());

        partialUpdatedReview.rating(UPDATED_RATING).comment(UPDATED_COMMENT).reviewDate(UPDATED_REVIEW_DATE);

        restReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReview.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReview))
            )
            .andExpect(status().isOk());

        // Validate the Review in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReviewUpdatableFieldsEquals(partialUpdatedReview, getPersistedReview(partialUpdatedReview));
    }

    @Test
    @Transactional
    void patchNonExistingReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reviewSearchRepository.findAll());
        review.setId(longCount.incrementAndGet());

        // Create the Review
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, reviewDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reviewSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reviewSearchRepository.findAll());
        review.setId(longCount.incrementAndGet());

        // Create the Review
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reviewSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reviewSearchRepository.findAll());
        review.setId(longCount.incrementAndGet());

        // Create the Review
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(reviewDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reviewSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteReview() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);
        reviewRepository.save(review);
        reviewSearchRepository.save(review);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reviewSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the review
        restReviewMockMvc
            .perform(delete(ENTITY_API_URL_ID, review.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reviewSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchReview() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);
        reviewSearchRepository.save(review);

        // Search the review
        restReviewMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + review.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(review.getId().intValue())))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].reviewDate").value(hasItem(DEFAULT_REVIEW_DATE.toString())));
    }

    protected long getRepositoryCount() {
        return reviewRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Review getPersistedReview(Review review) {
        return reviewRepository.findById(review.getId()).orElseThrow();
    }

    protected void assertPersistedReviewToMatchAllProperties(Review expectedReview) {
        assertReviewAllPropertiesEquals(expectedReview, getPersistedReview(expectedReview));
    }

    protected void assertPersistedReviewToMatchUpdatableProperties(Review expectedReview) {
        assertReviewAllUpdatablePropertiesEquals(expectedReview, getPersistedReview(expectedReview));
    }
}
