package ma.fullstackclone.airbnb.web.rest;

import static ma.fullstackclone.airbnb.domain.PromotionAsserts.*;
import static ma.fullstackclone.airbnb.web.rest.TestUtil.createUpdateProxyForBean;
import static ma.fullstackclone.airbnb.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import ma.fullstackclone.airbnb.IntegrationTest;
import ma.fullstackclone.airbnb.domain.Promotion;
import ma.fullstackclone.airbnb.domain.enumeration.DiscountType;
import ma.fullstackclone.airbnb.repository.PromotionRepository;
import ma.fullstackclone.airbnb.repository.search.PromotionSearchRepository;
import ma.fullstackclone.airbnb.service.dto.PromotionDTO;
import ma.fullstackclone.airbnb.service.mapper.PromotionMapper;
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
 * Integration tests for the {@link PromotionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PromotionResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final DiscountType DEFAULT_DISCOUNT_TYPE = DiscountType.PERCENTAGE;
    private static final DiscountType UPDATED_DISCOUNT_TYPE = DiscountType.FIXED_AMOUNT;

    private static final BigDecimal DEFAULT_DISCOUNT_VALUE = new BigDecimal(0);
    private static final BigDecimal UPDATED_DISCOUNT_VALUE = new BigDecimal(1);
    private static final BigDecimal SMALLER_DISCOUNT_VALUE = new BigDecimal(0 - 1);

    private static final Instant DEFAULT_VALID_FROM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_FROM = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_VALID_UNTIL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_UNTIL = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_MAX_USES = 1;
    private static final Integer UPDATED_MAX_USES = 2;
    private static final Integer SMALLER_MAX_USES = 1 - 1;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/promotions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/promotions/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private PromotionMapper promotionMapper;

    @Autowired
    private PromotionSearchRepository promotionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPromotionMockMvc;

    private Promotion promotion;

    private Promotion insertedPromotion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Promotion createEntity() {
        return new Promotion()
            .code(DEFAULT_CODE)
            .discountType(DEFAULT_DISCOUNT_TYPE)
            .discountValue(DEFAULT_DISCOUNT_VALUE)
            .validFrom(DEFAULT_VALID_FROM)
            .validUntil(DEFAULT_VALID_UNTIL)
            .maxUses(DEFAULT_MAX_USES)
            .isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Promotion createUpdatedEntity() {
        return new Promotion()
            .code(UPDATED_CODE)
            .discountType(UPDATED_DISCOUNT_TYPE)
            .discountValue(UPDATED_DISCOUNT_VALUE)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL)
            .maxUses(UPDATED_MAX_USES)
            .isActive(UPDATED_IS_ACTIVE);
    }

    @BeforeEach
    public void initTest() {
        promotion = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedPromotion != null) {
            promotionRepository.delete(insertedPromotion);
            promotionSearchRepository.delete(insertedPromotion);
            insertedPromotion = null;
        }
    }

    @Test
    @Transactional
    void createPromotion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);
        var returnedPromotionDTO = om.readValue(
            restPromotionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PromotionDTO.class
        );

        // Validate the Promotion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPromotion = promotionMapper.toEntity(returnedPromotionDTO);
        assertPromotionUpdatableFieldsEquals(returnedPromotion, getPersistedPromotion(returnedPromotion));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedPromotion = returnedPromotion;
    }

    @Test
    @Transactional
    void createPromotionWithExistingId() throws Exception {
        // Create the Promotion with an existing ID
        promotion.setId(1L);
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restPromotionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        // set the field null
        promotion.setCode(null);

        // Create the Promotion, which fails.
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        restPromotionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDiscountTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        // set the field null
        promotion.setDiscountType(null);

        // Create the Promotion, which fails.
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        restPromotionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDiscountValueIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        // set the field null
        promotion.setDiscountValue(null);

        // Create the Promotion, which fails.
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        restPromotionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkValidFromIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        // set the field null
        promotion.setValidFrom(null);

        // Create the Promotion, which fails.
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        restPromotionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkValidUntilIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        // set the field null
        promotion.setValidUntil(null);

        // Create the Promotion, which fails.
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        restPromotionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        // set the field null
        promotion.setIsActive(null);

        // Create the Promotion, which fails.
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        restPromotionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllPromotions() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList
        restPromotionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(promotion.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].discountType").value(hasItem(DEFAULT_DISCOUNT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].discountValue").value(hasItem(sameNumber(DEFAULT_DISCOUNT_VALUE))))
            .andExpect(jsonPath("$.[*].validFrom").value(hasItem(DEFAULT_VALID_FROM.toString())))
            .andExpect(jsonPath("$.[*].validUntil").value(hasItem(DEFAULT_VALID_UNTIL.toString())))
            .andExpect(jsonPath("$.[*].maxUses").value(hasItem(DEFAULT_MAX_USES)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    void getPromotion() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get the promotion
        restPromotionMockMvc
            .perform(get(ENTITY_API_URL_ID, promotion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(promotion.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.discountType").value(DEFAULT_DISCOUNT_TYPE.toString()))
            .andExpect(jsonPath("$.discountValue").value(sameNumber(DEFAULT_DISCOUNT_VALUE)))
            .andExpect(jsonPath("$.validFrom").value(DEFAULT_VALID_FROM.toString()))
            .andExpect(jsonPath("$.validUntil").value(DEFAULT_VALID_UNTIL.toString()))
            .andExpect(jsonPath("$.maxUses").value(DEFAULT_MAX_USES))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getPromotionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        Long id = promotion.getId();

        defaultPromotionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPromotionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPromotionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPromotionsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where code equals to
        defaultPromotionFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllPromotionsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where code in
        defaultPromotionFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllPromotionsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where code is not null
        defaultPromotionFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where code contains
        defaultPromotionFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllPromotionsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where code does not contain
        defaultPromotionFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountType equals to
        defaultPromotionFiltering("discountType.equals=" + DEFAULT_DISCOUNT_TYPE, "discountType.equals=" + UPDATED_DISCOUNT_TYPE);
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountType in
        defaultPromotionFiltering(
            "discountType.in=" + DEFAULT_DISCOUNT_TYPE + "," + UPDATED_DISCOUNT_TYPE,
            "discountType.in=" + UPDATED_DISCOUNT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountType is not null
        defaultPromotionFiltering("discountType.specified=true", "discountType.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountValueIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountValue equals to
        defaultPromotionFiltering("discountValue.equals=" + DEFAULT_DISCOUNT_VALUE, "discountValue.equals=" + UPDATED_DISCOUNT_VALUE);
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountValueIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountValue in
        defaultPromotionFiltering(
            "discountValue.in=" + DEFAULT_DISCOUNT_VALUE + "," + UPDATED_DISCOUNT_VALUE,
            "discountValue.in=" + UPDATED_DISCOUNT_VALUE
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountValue is not null
        defaultPromotionFiltering("discountValue.specified=true", "discountValue.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountValueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountValue is greater than or equal to
        defaultPromotionFiltering(
            "discountValue.greaterThanOrEqual=" + DEFAULT_DISCOUNT_VALUE,
            "discountValue.greaterThanOrEqual=" + UPDATED_DISCOUNT_VALUE
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountValueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountValue is less than or equal to
        defaultPromotionFiltering(
            "discountValue.lessThanOrEqual=" + DEFAULT_DISCOUNT_VALUE,
            "discountValue.lessThanOrEqual=" + SMALLER_DISCOUNT_VALUE
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountValueIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountValue is less than
        defaultPromotionFiltering("discountValue.lessThan=" + UPDATED_DISCOUNT_VALUE, "discountValue.lessThan=" + DEFAULT_DISCOUNT_VALUE);
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountValueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountValue is greater than
        defaultPromotionFiltering(
            "discountValue.greaterThan=" + SMALLER_DISCOUNT_VALUE,
            "discountValue.greaterThan=" + DEFAULT_DISCOUNT_VALUE
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByValidFromIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where validFrom equals to
        defaultPromotionFiltering("validFrom.equals=" + DEFAULT_VALID_FROM, "validFrom.equals=" + UPDATED_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllPromotionsByValidFromIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where validFrom in
        defaultPromotionFiltering("validFrom.in=" + DEFAULT_VALID_FROM + "," + UPDATED_VALID_FROM, "validFrom.in=" + UPDATED_VALID_FROM);
    }

    @Test
    @Transactional
    void getAllPromotionsByValidFromIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where validFrom is not null
        defaultPromotionFiltering("validFrom.specified=true", "validFrom.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByValidUntilIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where validUntil equals to
        defaultPromotionFiltering("validUntil.equals=" + DEFAULT_VALID_UNTIL, "validUntil.equals=" + UPDATED_VALID_UNTIL);
    }

    @Test
    @Transactional
    void getAllPromotionsByValidUntilIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where validUntil in
        defaultPromotionFiltering(
            "validUntil.in=" + DEFAULT_VALID_UNTIL + "," + UPDATED_VALID_UNTIL,
            "validUntil.in=" + UPDATED_VALID_UNTIL
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByValidUntilIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where validUntil is not null
        defaultPromotionFiltering("validUntil.specified=true", "validUntil.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByMaxUsesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where maxUses equals to
        defaultPromotionFiltering("maxUses.equals=" + DEFAULT_MAX_USES, "maxUses.equals=" + UPDATED_MAX_USES);
    }

    @Test
    @Transactional
    void getAllPromotionsByMaxUsesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where maxUses in
        defaultPromotionFiltering("maxUses.in=" + DEFAULT_MAX_USES + "," + UPDATED_MAX_USES, "maxUses.in=" + UPDATED_MAX_USES);
    }

    @Test
    @Transactional
    void getAllPromotionsByMaxUsesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where maxUses is not null
        defaultPromotionFiltering("maxUses.specified=true", "maxUses.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByMaxUsesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where maxUses is greater than or equal to
        defaultPromotionFiltering("maxUses.greaterThanOrEqual=" + DEFAULT_MAX_USES, "maxUses.greaterThanOrEqual=" + UPDATED_MAX_USES);
    }

    @Test
    @Transactional
    void getAllPromotionsByMaxUsesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where maxUses is less than or equal to
        defaultPromotionFiltering("maxUses.lessThanOrEqual=" + DEFAULT_MAX_USES, "maxUses.lessThanOrEqual=" + SMALLER_MAX_USES);
    }

    @Test
    @Transactional
    void getAllPromotionsByMaxUsesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where maxUses is less than
        defaultPromotionFiltering("maxUses.lessThan=" + UPDATED_MAX_USES, "maxUses.lessThan=" + DEFAULT_MAX_USES);
    }

    @Test
    @Transactional
    void getAllPromotionsByMaxUsesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where maxUses is greater than
        defaultPromotionFiltering("maxUses.greaterThan=" + SMALLER_MAX_USES, "maxUses.greaterThan=" + DEFAULT_MAX_USES);
    }

    @Test
    @Transactional
    void getAllPromotionsByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where isActive equals to
        defaultPromotionFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPromotionsByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where isActive in
        defaultPromotionFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPromotionsByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where isActive is not null
        defaultPromotionFiltering("isActive.specified=true", "isActive.specified=false");
    }

    private void defaultPromotionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPromotionShouldBeFound(shouldBeFound);
        defaultPromotionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPromotionShouldBeFound(String filter) throws Exception {
        restPromotionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(promotion.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].discountType").value(hasItem(DEFAULT_DISCOUNT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].discountValue").value(hasItem(sameNumber(DEFAULT_DISCOUNT_VALUE))))
            .andExpect(jsonPath("$.[*].validFrom").value(hasItem(DEFAULT_VALID_FROM.toString())))
            .andExpect(jsonPath("$.[*].validUntil").value(hasItem(DEFAULT_VALID_UNTIL.toString())))
            .andExpect(jsonPath("$.[*].maxUses").value(hasItem(DEFAULT_MAX_USES)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())));

        // Check, that the count call also returns 1
        restPromotionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPromotionShouldNotBeFound(String filter) throws Exception {
        restPromotionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPromotionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPromotion() throws Exception {
        // Get the promotion
        restPromotionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPromotion() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        promotionSearchRepository.save(promotion);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll());

        // Update the promotion
        Promotion updatedPromotion = promotionRepository.findById(promotion.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPromotion are not directly saved in db
        em.detach(updatedPromotion);
        updatedPromotion
            .code(UPDATED_CODE)
            .discountType(UPDATED_DISCOUNT_TYPE)
            .discountValue(UPDATED_DISCOUNT_VALUE)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL)
            .maxUses(UPDATED_MAX_USES)
            .isActive(UPDATED_IS_ACTIVE);
        PromotionDTO promotionDTO = promotionMapper.toDto(updatedPromotion);

        restPromotionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, promotionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(promotionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPromotionToMatchAllProperties(updatedPromotion);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Promotion> promotionSearchList = Streamable.of(promotionSearchRepository.findAll()).toList();
                Promotion testPromotionSearch = promotionSearchList.get(searchDatabaseSizeAfter - 1);

                assertPromotionAllPropertiesEquals(testPromotionSearch, updatedPromotion);
            });
    }

    @Test
    @Transactional
    void putNonExistingPromotion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        promotion.setId(longCount.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPromotionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, promotionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(promotionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchPromotion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        promotion.setId(longCount.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPromotionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(promotionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPromotion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        promotion.setId(longCount.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPromotionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdatePromotionWithPatch() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the promotion using partial update
        Promotion partialUpdatedPromotion = new Promotion();
        partialUpdatedPromotion.setId(promotion.getId());

        partialUpdatedPromotion.code(UPDATED_CODE).discountType(UPDATED_DISCOUNT_TYPE).maxUses(UPDATED_MAX_USES);

        restPromotionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPromotion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPromotion))
            )
            .andExpect(status().isOk());

        // Validate the Promotion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPromotionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPromotion, promotion),
            getPersistedPromotion(promotion)
        );
    }

    @Test
    @Transactional
    void fullUpdatePromotionWithPatch() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the promotion using partial update
        Promotion partialUpdatedPromotion = new Promotion();
        partialUpdatedPromotion.setId(promotion.getId());

        partialUpdatedPromotion
            .code(UPDATED_CODE)
            .discountType(UPDATED_DISCOUNT_TYPE)
            .discountValue(UPDATED_DISCOUNT_VALUE)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL)
            .maxUses(UPDATED_MAX_USES)
            .isActive(UPDATED_IS_ACTIVE);

        restPromotionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPromotion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPromotion))
            )
            .andExpect(status().isOk());

        // Validate the Promotion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPromotionUpdatableFieldsEquals(partialUpdatedPromotion, getPersistedPromotion(partialUpdatedPromotion));
    }

    @Test
    @Transactional
    void patchNonExistingPromotion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        promotion.setId(longCount.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPromotionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, promotionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(promotionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPromotion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        promotion.setId(longCount.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPromotionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(promotionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPromotion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        promotion.setId(longCount.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPromotionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deletePromotion() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);
        promotionRepository.save(promotion);
        promotionSearchRepository.save(promotion);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the promotion
        restPromotionMockMvc
            .perform(delete(ENTITY_API_URL_ID, promotion.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(promotionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchPromotion() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);
        promotionSearchRepository.save(promotion);

        // Search the promotion
        restPromotionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + promotion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(promotion.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].discountType").value(hasItem(DEFAULT_DISCOUNT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].discountValue").value(hasItem(sameNumber(DEFAULT_DISCOUNT_VALUE))))
            .andExpect(jsonPath("$.[*].validFrom").value(hasItem(DEFAULT_VALID_FROM.toString())))
            .andExpect(jsonPath("$.[*].validUntil").value(hasItem(DEFAULT_VALID_UNTIL.toString())))
            .andExpect(jsonPath("$.[*].maxUses").value(hasItem(DEFAULT_MAX_USES)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())));
    }

    protected long getRepositoryCount() {
        return promotionRepository.count();
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

    protected Promotion getPersistedPromotion(Promotion promotion) {
        return promotionRepository.findById(promotion.getId()).orElseThrow();
    }

    protected void assertPersistedPromotionToMatchAllProperties(Promotion expectedPromotion) {
        assertPromotionAllPropertiesEquals(expectedPromotion, getPersistedPromotion(expectedPromotion));
    }

    protected void assertPersistedPromotionToMatchUpdatableProperties(Promotion expectedPromotion) {
        assertPromotionAllUpdatablePropertiesEquals(expectedPromotion, getPersistedPromotion(expectedPromotion));
    }
}
