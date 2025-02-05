package ma.fullstackclone.airbnb.web.rest;

import static ma.fullstackclone.airbnb.domain.PropertyAsserts.*;
import static ma.fullstackclone.airbnb.web.rest.TestUtil.createUpdateProxyForBean;
import static ma.fullstackclone.airbnb.web.rest.TestUtil.sameInstant;
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
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import ma.fullstackclone.airbnb.IntegrationTest;
import ma.fullstackclone.airbnb.domain.Amenity;
import ma.fullstackclone.airbnb.domain.City;
import ma.fullstackclone.airbnb.domain.Property;
import ma.fullstackclone.airbnb.domain.PropertyCategory;
import ma.fullstackclone.airbnb.domain.User;
import ma.fullstackclone.airbnb.repository.PropertyRepository;
import ma.fullstackclone.airbnb.repository.UserRepository;
import ma.fullstackclone.airbnb.repository.search.PropertySearchRepository;
import ma.fullstackclone.airbnb.service.PropertyService;
import ma.fullstackclone.airbnb.service.dto.PropertyDTO;
import ma.fullstackclone.airbnb.service.mapper.PropertyMapper;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PropertyResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PropertyResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE_PER_NIGHT = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRICE_PER_NIGHT = new BigDecimal(1);
    private static final BigDecimal SMALLER_PRICE_PER_NIGHT = new BigDecimal(0 - 1);

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_LATITUDE = new BigDecimal(1);
    private static final BigDecimal UPDATED_LATITUDE = new BigDecimal(2);
    private static final BigDecimal SMALLER_LATITUDE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_LONGITUDE = new BigDecimal(1);
    private static final BigDecimal UPDATED_LONGITUDE = new BigDecimal(2);
    private static final BigDecimal SMALLER_LONGITUDE = new BigDecimal(1 - 1);

    private static final Integer DEFAULT_NUMBER_OF_ROOMS = 1;
    private static final Integer UPDATED_NUMBER_OF_ROOMS = 2;
    private static final Integer SMALLER_NUMBER_OF_ROOMS = 1 - 1;

    private static final Integer DEFAULT_NUMBER_OF_BATHROOMS = 1;
    private static final Integer UPDATED_NUMBER_OF_BATHROOMS = 2;
    private static final Integer SMALLER_NUMBER_OF_BATHROOMS = 1 - 1;

    private static final Integer DEFAULT_MAX_GUESTS = 1;
    private static final Integer UPDATED_MAX_GUESTS = 2;
    private static final Integer SMALLER_MAX_GUESTS = 1 - 1;

    private static final Integer DEFAULT_PROPERTY_SIZE = 0;
    private static final Integer UPDATED_PROPERTY_SIZE = 1;
    private static final Integer SMALLER_PROPERTY_SIZE = 0 - 1;

    private static final ZonedDateTime DEFAULT_AVAILABILITY_START = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_AVAILABILITY_START = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_AVAILABILITY_START = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_AVAILABILITY_END = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_AVAILABILITY_END = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_AVAILABILITY_END = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Boolean DEFAULT_INSTANT_BOOK = false;
    private static final Boolean UPDATED_INSTANT_BOOK = true;

    private static final Integer DEFAULT_MINIMUM_STAY = 1;
    private static final Integer UPDATED_MINIMUM_STAY = 2;
    private static final Integer SMALLER_MINIMUM_STAY = 1 - 1;

    private static final String DEFAULT_CANCELLATION_POLICY = "AAAAAAAAAA";
    private static final String UPDATED_CANCELLATION_POLICY = "BBBBBBBBBB";

    private static final String DEFAULT_HOUSE_RULES = "AAAAAAAAAA";
    private static final String UPDATED_HOUSE_RULES = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/properties";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/properties/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private PropertyRepository propertyRepositoryMock;

    @Autowired
    private PropertyMapper propertyMapper;

    @Mock
    private PropertyService propertyServiceMock;

    @Autowired
    private PropertySearchRepository propertySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPropertyMockMvc;

    private Property property;

    private Property insertedProperty;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Property createEntity() {
        return new Property()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .pricePerNight(DEFAULT_PRICE_PER_NIGHT)
            .address(DEFAULT_ADDRESS)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .numberOfRooms(DEFAULT_NUMBER_OF_ROOMS)
            .numberOfBathrooms(DEFAULT_NUMBER_OF_BATHROOMS)
            .maxGuests(DEFAULT_MAX_GUESTS)
            .propertySize(DEFAULT_PROPERTY_SIZE)
            .availabilityStart(DEFAULT_AVAILABILITY_START)
            .availabilityEnd(DEFAULT_AVAILABILITY_END)
            .instantBook(DEFAULT_INSTANT_BOOK)
            .minimumStay(DEFAULT_MINIMUM_STAY)
            .cancellationPolicy(DEFAULT_CANCELLATION_POLICY)
            .houseRules(DEFAULT_HOUSE_RULES)
            .isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Property createUpdatedEntity() {
        return new Property()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .pricePerNight(UPDATED_PRICE_PER_NIGHT)
            .address(UPDATED_ADDRESS)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .numberOfRooms(UPDATED_NUMBER_OF_ROOMS)
            .numberOfBathrooms(UPDATED_NUMBER_OF_BATHROOMS)
            .maxGuests(UPDATED_MAX_GUESTS)
            .propertySize(UPDATED_PROPERTY_SIZE)
            .availabilityStart(UPDATED_AVAILABILITY_START)
            .availabilityEnd(UPDATED_AVAILABILITY_END)
            .instantBook(UPDATED_INSTANT_BOOK)
            .minimumStay(UPDATED_MINIMUM_STAY)
            .cancellationPolicy(UPDATED_CANCELLATION_POLICY)
            .houseRules(UPDATED_HOUSE_RULES)
            .isActive(UPDATED_IS_ACTIVE);
    }

    @BeforeEach
    public void initTest() {
        property = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedProperty != null) {
            propertyRepository.delete(insertedProperty);
            propertySearchRepository.delete(insertedProperty);
            insertedProperty = null;
        }
    }

    @Test
    @Transactional
    void createProperty() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertySearchRepository.findAll());
        // Create the Property
        PropertyDTO propertyDTO = propertyMapper.toDto(property);
        var returnedPropertyDTO = om.readValue(
            restPropertyMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(propertyDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PropertyDTO.class
        );

        // Validate the Property in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProperty = propertyMapper.toEntity(returnedPropertyDTO);
        assertPropertyUpdatableFieldsEquals(returnedProperty, getPersistedProperty(returnedProperty));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedProperty = returnedProperty;
    }

    @Test
    @Transactional
    void createPropertyWithExistingId() throws Exception {
        // Create the Property with an existing ID
        property.setId(1L);
        PropertyDTO propertyDTO = propertyMapper.toDto(property);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restPropertyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(propertyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Property in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertySearchRepository.findAll());
        // set the field null
        property.setTitle(null);

        // Create the Property, which fails.
        PropertyDTO propertyDTO = propertyMapper.toDto(property);

        restPropertyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(propertyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPricePerNightIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertySearchRepository.findAll());
        // set the field null
        property.setPricePerNight(null);

        // Create the Property, which fails.
        PropertyDTO propertyDTO = propertyMapper.toDto(property);

        restPropertyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(propertyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAddressIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertySearchRepository.findAll());
        // set the field null
        property.setAddress(null);

        // Create the Property, which fails.
        PropertyDTO propertyDTO = propertyMapper.toDto(property);

        restPropertyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(propertyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNumberOfRoomsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertySearchRepository.findAll());
        // set the field null
        property.setNumberOfRooms(null);

        // Create the Property, which fails.
        PropertyDTO propertyDTO = propertyMapper.toDto(property);

        restPropertyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(propertyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkInstantBookIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertySearchRepository.findAll());
        // set the field null
        property.setInstantBook(null);

        // Create the Property, which fails.
        PropertyDTO propertyDTO = propertyMapper.toDto(property);

        restPropertyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(propertyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCancellationPolicyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertySearchRepository.findAll());
        // set the field null
        property.setCancellationPolicy(null);

        // Create the Property, which fails.
        PropertyDTO propertyDTO = propertyMapper.toDto(property);

        restPropertyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(propertyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertySearchRepository.findAll());
        // set the field null
        property.setIsActive(null);

        // Create the Property, which fails.
        PropertyDTO propertyDTO = propertyMapper.toDto(property);

        restPropertyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(propertyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllProperties() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList
        restPropertyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(property.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].pricePerNight").value(hasItem(sameNumber(DEFAULT_PRICE_PER_NIGHT))))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(sameNumber(DEFAULT_LATITUDE))))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(sameNumber(DEFAULT_LONGITUDE))))
            .andExpect(jsonPath("$.[*].numberOfRooms").value(hasItem(DEFAULT_NUMBER_OF_ROOMS)))
            .andExpect(jsonPath("$.[*].numberOfBathrooms").value(hasItem(DEFAULT_NUMBER_OF_BATHROOMS)))
            .andExpect(jsonPath("$.[*].maxGuests").value(hasItem(DEFAULT_MAX_GUESTS)))
            .andExpect(jsonPath("$.[*].propertySize").value(hasItem(DEFAULT_PROPERTY_SIZE)))
            .andExpect(jsonPath("$.[*].availabilityStart").value(hasItem(sameInstant(DEFAULT_AVAILABILITY_START))))
            .andExpect(jsonPath("$.[*].availabilityEnd").value(hasItem(sameInstant(DEFAULT_AVAILABILITY_END))))
            .andExpect(jsonPath("$.[*].instantBook").value(hasItem(DEFAULT_INSTANT_BOOK.booleanValue())))
            .andExpect(jsonPath("$.[*].minimumStay").value(hasItem(DEFAULT_MINIMUM_STAY)))
            .andExpect(jsonPath("$.[*].cancellationPolicy").value(hasItem(DEFAULT_CANCELLATION_POLICY)))
            .andExpect(jsonPath("$.[*].houseRules").value(hasItem(DEFAULT_HOUSE_RULES.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPropertiesWithEagerRelationshipsIsEnabled() throws Exception {
        when(propertyServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPropertyMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(propertyServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPropertiesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(propertyServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPropertyMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(propertyRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getProperty() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get the property
        restPropertyMockMvc
            .perform(get(ENTITY_API_URL_ID, property.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(property.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.pricePerNight").value(sameNumber(DEFAULT_PRICE_PER_NIGHT)))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.latitude").value(sameNumber(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.longitude").value(sameNumber(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.numberOfRooms").value(DEFAULT_NUMBER_OF_ROOMS))
            .andExpect(jsonPath("$.numberOfBathrooms").value(DEFAULT_NUMBER_OF_BATHROOMS))
            .andExpect(jsonPath("$.maxGuests").value(DEFAULT_MAX_GUESTS))
            .andExpect(jsonPath("$.propertySize").value(DEFAULT_PROPERTY_SIZE))
            .andExpect(jsonPath("$.availabilityStart").value(sameInstant(DEFAULT_AVAILABILITY_START)))
            .andExpect(jsonPath("$.availabilityEnd").value(sameInstant(DEFAULT_AVAILABILITY_END)))
            .andExpect(jsonPath("$.instantBook").value(DEFAULT_INSTANT_BOOK.booleanValue()))
            .andExpect(jsonPath("$.minimumStay").value(DEFAULT_MINIMUM_STAY))
            .andExpect(jsonPath("$.cancellationPolicy").value(DEFAULT_CANCELLATION_POLICY))
            .andExpect(jsonPath("$.houseRules").value(DEFAULT_HOUSE_RULES.toString()))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getPropertiesByIdFiltering() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        Long id = property.getId();

        defaultPropertyFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPropertyFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPropertyFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPropertiesByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where title equals to
        defaultPropertyFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllPropertiesByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where title in
        defaultPropertyFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllPropertiesByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where title is not null
        defaultPropertyFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllPropertiesByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where title contains
        defaultPropertyFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllPropertiesByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where title does not contain
        defaultPropertyFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllPropertiesByPricePerNightIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where pricePerNight equals to
        defaultPropertyFiltering("pricePerNight.equals=" + DEFAULT_PRICE_PER_NIGHT, "pricePerNight.equals=" + UPDATED_PRICE_PER_NIGHT);
    }

    @Test
    @Transactional
    void getAllPropertiesByPricePerNightIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where pricePerNight in
        defaultPropertyFiltering(
            "pricePerNight.in=" + DEFAULT_PRICE_PER_NIGHT + "," + UPDATED_PRICE_PER_NIGHT,
            "pricePerNight.in=" + UPDATED_PRICE_PER_NIGHT
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByPricePerNightIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where pricePerNight is not null
        defaultPropertyFiltering("pricePerNight.specified=true", "pricePerNight.specified=false");
    }

    @Test
    @Transactional
    void getAllPropertiesByPricePerNightIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where pricePerNight is greater than or equal to
        defaultPropertyFiltering(
            "pricePerNight.greaterThanOrEqual=" + DEFAULT_PRICE_PER_NIGHT,
            "pricePerNight.greaterThanOrEqual=" + UPDATED_PRICE_PER_NIGHT
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByPricePerNightIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where pricePerNight is less than or equal to
        defaultPropertyFiltering(
            "pricePerNight.lessThanOrEqual=" + DEFAULT_PRICE_PER_NIGHT,
            "pricePerNight.lessThanOrEqual=" + SMALLER_PRICE_PER_NIGHT
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByPricePerNightIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where pricePerNight is less than
        defaultPropertyFiltering("pricePerNight.lessThan=" + UPDATED_PRICE_PER_NIGHT, "pricePerNight.lessThan=" + DEFAULT_PRICE_PER_NIGHT);
    }

    @Test
    @Transactional
    void getAllPropertiesByPricePerNightIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where pricePerNight is greater than
        defaultPropertyFiltering(
            "pricePerNight.greaterThan=" + SMALLER_PRICE_PER_NIGHT,
            "pricePerNight.greaterThan=" + DEFAULT_PRICE_PER_NIGHT
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where address equals to
        defaultPropertyFiltering("address.equals=" + DEFAULT_ADDRESS, "address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllPropertiesByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where address in
        defaultPropertyFiltering("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS, "address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllPropertiesByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where address is not null
        defaultPropertyFiltering("address.specified=true", "address.specified=false");
    }

    @Test
    @Transactional
    void getAllPropertiesByAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where address contains
        defaultPropertyFiltering("address.contains=" + DEFAULT_ADDRESS, "address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllPropertiesByAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where address does not contain
        defaultPropertyFiltering("address.doesNotContain=" + UPDATED_ADDRESS, "address.doesNotContain=" + DEFAULT_ADDRESS);
    }

    @Test
    @Transactional
    void getAllPropertiesByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where latitude equals to
        defaultPropertyFiltering("latitude.equals=" + DEFAULT_LATITUDE, "latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllPropertiesByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where latitude in
        defaultPropertyFiltering("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE, "latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllPropertiesByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where latitude is not null
        defaultPropertyFiltering("latitude.specified=true", "latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllPropertiesByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where latitude is greater than or equal to
        defaultPropertyFiltering("latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE, "latitude.greaterThanOrEqual=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllPropertiesByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where latitude is less than or equal to
        defaultPropertyFiltering("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE, "latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllPropertiesByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where latitude is less than
        defaultPropertyFiltering("latitude.lessThan=" + UPDATED_LATITUDE, "latitude.lessThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllPropertiesByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where latitude is greater than
        defaultPropertyFiltering("latitude.greaterThan=" + SMALLER_LATITUDE, "latitude.greaterThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllPropertiesByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where longitude equals to
        defaultPropertyFiltering("longitude.equals=" + DEFAULT_LONGITUDE, "longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllPropertiesByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where longitude in
        defaultPropertyFiltering("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE, "longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllPropertiesByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where longitude is not null
        defaultPropertyFiltering("longitude.specified=true", "longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllPropertiesByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where longitude is greater than or equal to
        defaultPropertyFiltering("longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE, "longitude.greaterThanOrEqual=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllPropertiesByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where longitude is less than or equal to
        defaultPropertyFiltering("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE, "longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllPropertiesByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where longitude is less than
        defaultPropertyFiltering("longitude.lessThan=" + UPDATED_LONGITUDE, "longitude.lessThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllPropertiesByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where longitude is greater than
        defaultPropertyFiltering("longitude.greaterThan=" + SMALLER_LONGITUDE, "longitude.greaterThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllPropertiesByNumberOfRoomsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where numberOfRooms equals to
        defaultPropertyFiltering("numberOfRooms.equals=" + DEFAULT_NUMBER_OF_ROOMS, "numberOfRooms.equals=" + UPDATED_NUMBER_OF_ROOMS);
    }

    @Test
    @Transactional
    void getAllPropertiesByNumberOfRoomsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where numberOfRooms in
        defaultPropertyFiltering(
            "numberOfRooms.in=" + DEFAULT_NUMBER_OF_ROOMS + "," + UPDATED_NUMBER_OF_ROOMS,
            "numberOfRooms.in=" + UPDATED_NUMBER_OF_ROOMS
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByNumberOfRoomsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where numberOfRooms is not null
        defaultPropertyFiltering("numberOfRooms.specified=true", "numberOfRooms.specified=false");
    }

    @Test
    @Transactional
    void getAllPropertiesByNumberOfRoomsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where numberOfRooms is greater than or equal to
        defaultPropertyFiltering(
            "numberOfRooms.greaterThanOrEqual=" + DEFAULT_NUMBER_OF_ROOMS,
            "numberOfRooms.greaterThanOrEqual=" + UPDATED_NUMBER_OF_ROOMS
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByNumberOfRoomsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where numberOfRooms is less than or equal to
        defaultPropertyFiltering(
            "numberOfRooms.lessThanOrEqual=" + DEFAULT_NUMBER_OF_ROOMS,
            "numberOfRooms.lessThanOrEqual=" + SMALLER_NUMBER_OF_ROOMS
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByNumberOfRoomsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where numberOfRooms is less than
        defaultPropertyFiltering("numberOfRooms.lessThan=" + UPDATED_NUMBER_OF_ROOMS, "numberOfRooms.lessThan=" + DEFAULT_NUMBER_OF_ROOMS);
    }

    @Test
    @Transactional
    void getAllPropertiesByNumberOfRoomsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where numberOfRooms is greater than
        defaultPropertyFiltering(
            "numberOfRooms.greaterThan=" + SMALLER_NUMBER_OF_ROOMS,
            "numberOfRooms.greaterThan=" + DEFAULT_NUMBER_OF_ROOMS
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByNumberOfBathroomsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where numberOfBathrooms equals to
        defaultPropertyFiltering(
            "numberOfBathrooms.equals=" + DEFAULT_NUMBER_OF_BATHROOMS,
            "numberOfBathrooms.equals=" + UPDATED_NUMBER_OF_BATHROOMS
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByNumberOfBathroomsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where numberOfBathrooms in
        defaultPropertyFiltering(
            "numberOfBathrooms.in=" + DEFAULT_NUMBER_OF_BATHROOMS + "," + UPDATED_NUMBER_OF_BATHROOMS,
            "numberOfBathrooms.in=" + UPDATED_NUMBER_OF_BATHROOMS
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByNumberOfBathroomsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where numberOfBathrooms is not null
        defaultPropertyFiltering("numberOfBathrooms.specified=true", "numberOfBathrooms.specified=false");
    }

    @Test
    @Transactional
    void getAllPropertiesByNumberOfBathroomsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where numberOfBathrooms is greater than or equal to
        defaultPropertyFiltering(
            "numberOfBathrooms.greaterThanOrEqual=" + DEFAULT_NUMBER_OF_BATHROOMS,
            "numberOfBathrooms.greaterThanOrEqual=" + UPDATED_NUMBER_OF_BATHROOMS
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByNumberOfBathroomsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where numberOfBathrooms is less than or equal to
        defaultPropertyFiltering(
            "numberOfBathrooms.lessThanOrEqual=" + DEFAULT_NUMBER_OF_BATHROOMS,
            "numberOfBathrooms.lessThanOrEqual=" + SMALLER_NUMBER_OF_BATHROOMS
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByNumberOfBathroomsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where numberOfBathrooms is less than
        defaultPropertyFiltering(
            "numberOfBathrooms.lessThan=" + UPDATED_NUMBER_OF_BATHROOMS,
            "numberOfBathrooms.lessThan=" + DEFAULT_NUMBER_OF_BATHROOMS
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByNumberOfBathroomsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where numberOfBathrooms is greater than
        defaultPropertyFiltering(
            "numberOfBathrooms.greaterThan=" + SMALLER_NUMBER_OF_BATHROOMS,
            "numberOfBathrooms.greaterThan=" + DEFAULT_NUMBER_OF_BATHROOMS
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByMaxGuestsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where maxGuests equals to
        defaultPropertyFiltering("maxGuests.equals=" + DEFAULT_MAX_GUESTS, "maxGuests.equals=" + UPDATED_MAX_GUESTS);
    }

    @Test
    @Transactional
    void getAllPropertiesByMaxGuestsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where maxGuests in
        defaultPropertyFiltering("maxGuests.in=" + DEFAULT_MAX_GUESTS + "," + UPDATED_MAX_GUESTS, "maxGuests.in=" + UPDATED_MAX_GUESTS);
    }

    @Test
    @Transactional
    void getAllPropertiesByMaxGuestsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where maxGuests is not null
        defaultPropertyFiltering("maxGuests.specified=true", "maxGuests.specified=false");
    }

    @Test
    @Transactional
    void getAllPropertiesByMaxGuestsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where maxGuests is greater than or equal to
        defaultPropertyFiltering(
            "maxGuests.greaterThanOrEqual=" + DEFAULT_MAX_GUESTS,
            "maxGuests.greaterThanOrEqual=" + UPDATED_MAX_GUESTS
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByMaxGuestsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where maxGuests is less than or equal to
        defaultPropertyFiltering("maxGuests.lessThanOrEqual=" + DEFAULT_MAX_GUESTS, "maxGuests.lessThanOrEqual=" + SMALLER_MAX_GUESTS);
    }

    @Test
    @Transactional
    void getAllPropertiesByMaxGuestsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where maxGuests is less than
        defaultPropertyFiltering("maxGuests.lessThan=" + UPDATED_MAX_GUESTS, "maxGuests.lessThan=" + DEFAULT_MAX_GUESTS);
    }

    @Test
    @Transactional
    void getAllPropertiesByMaxGuestsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where maxGuests is greater than
        defaultPropertyFiltering("maxGuests.greaterThan=" + SMALLER_MAX_GUESTS, "maxGuests.greaterThan=" + DEFAULT_MAX_GUESTS);
    }

    @Test
    @Transactional
    void getAllPropertiesByPropertySizeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where propertySize equals to
        defaultPropertyFiltering("propertySize.equals=" + DEFAULT_PROPERTY_SIZE, "propertySize.equals=" + UPDATED_PROPERTY_SIZE);
    }

    @Test
    @Transactional
    void getAllPropertiesByPropertySizeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where propertySize in
        defaultPropertyFiltering(
            "propertySize.in=" + DEFAULT_PROPERTY_SIZE + "," + UPDATED_PROPERTY_SIZE,
            "propertySize.in=" + UPDATED_PROPERTY_SIZE
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByPropertySizeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where propertySize is not null
        defaultPropertyFiltering("propertySize.specified=true", "propertySize.specified=false");
    }

    @Test
    @Transactional
    void getAllPropertiesByPropertySizeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where propertySize is greater than or equal to
        defaultPropertyFiltering(
            "propertySize.greaterThanOrEqual=" + DEFAULT_PROPERTY_SIZE,
            "propertySize.greaterThanOrEqual=" + UPDATED_PROPERTY_SIZE
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByPropertySizeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where propertySize is less than or equal to
        defaultPropertyFiltering(
            "propertySize.lessThanOrEqual=" + DEFAULT_PROPERTY_SIZE,
            "propertySize.lessThanOrEqual=" + SMALLER_PROPERTY_SIZE
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByPropertySizeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where propertySize is less than
        defaultPropertyFiltering("propertySize.lessThan=" + UPDATED_PROPERTY_SIZE, "propertySize.lessThan=" + DEFAULT_PROPERTY_SIZE);
    }

    @Test
    @Transactional
    void getAllPropertiesByPropertySizeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where propertySize is greater than
        defaultPropertyFiltering("propertySize.greaterThan=" + SMALLER_PROPERTY_SIZE, "propertySize.greaterThan=" + DEFAULT_PROPERTY_SIZE);
    }

    @Test
    @Transactional
    void getAllPropertiesByAvailabilityStartIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where availabilityStart equals to
        defaultPropertyFiltering(
            "availabilityStart.equals=" + DEFAULT_AVAILABILITY_START,
            "availabilityStart.equals=" + UPDATED_AVAILABILITY_START
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByAvailabilityStartIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where availabilityStart in
        defaultPropertyFiltering(
            "availabilityStart.in=" + DEFAULT_AVAILABILITY_START + "," + UPDATED_AVAILABILITY_START,
            "availabilityStart.in=" + UPDATED_AVAILABILITY_START
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByAvailabilityStartIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where availabilityStart is not null
        defaultPropertyFiltering("availabilityStart.specified=true", "availabilityStart.specified=false");
    }

    @Test
    @Transactional
    void getAllPropertiesByAvailabilityStartIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where availabilityStart is greater than or equal to
        defaultPropertyFiltering(
            "availabilityStart.greaterThanOrEqual=" + DEFAULT_AVAILABILITY_START,
            "availabilityStart.greaterThanOrEqual=" + UPDATED_AVAILABILITY_START
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByAvailabilityStartIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where availabilityStart is less than or equal to
        defaultPropertyFiltering(
            "availabilityStart.lessThanOrEqual=" + DEFAULT_AVAILABILITY_START,
            "availabilityStart.lessThanOrEqual=" + SMALLER_AVAILABILITY_START
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByAvailabilityStartIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where availabilityStart is less than
        defaultPropertyFiltering(
            "availabilityStart.lessThan=" + UPDATED_AVAILABILITY_START,
            "availabilityStart.lessThan=" + DEFAULT_AVAILABILITY_START
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByAvailabilityStartIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where availabilityStart is greater than
        defaultPropertyFiltering(
            "availabilityStart.greaterThan=" + SMALLER_AVAILABILITY_START,
            "availabilityStart.greaterThan=" + DEFAULT_AVAILABILITY_START
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByAvailabilityEndIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where availabilityEnd equals to
        defaultPropertyFiltering(
            "availabilityEnd.equals=" + DEFAULT_AVAILABILITY_END,
            "availabilityEnd.equals=" + UPDATED_AVAILABILITY_END
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByAvailabilityEndIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where availabilityEnd in
        defaultPropertyFiltering(
            "availabilityEnd.in=" + DEFAULT_AVAILABILITY_END + "," + UPDATED_AVAILABILITY_END,
            "availabilityEnd.in=" + UPDATED_AVAILABILITY_END
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByAvailabilityEndIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where availabilityEnd is not null
        defaultPropertyFiltering("availabilityEnd.specified=true", "availabilityEnd.specified=false");
    }

    @Test
    @Transactional
    void getAllPropertiesByAvailabilityEndIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where availabilityEnd is greater than or equal to
        defaultPropertyFiltering(
            "availabilityEnd.greaterThanOrEqual=" + DEFAULT_AVAILABILITY_END,
            "availabilityEnd.greaterThanOrEqual=" + UPDATED_AVAILABILITY_END
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByAvailabilityEndIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where availabilityEnd is less than or equal to
        defaultPropertyFiltering(
            "availabilityEnd.lessThanOrEqual=" + DEFAULT_AVAILABILITY_END,
            "availabilityEnd.lessThanOrEqual=" + SMALLER_AVAILABILITY_END
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByAvailabilityEndIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where availabilityEnd is less than
        defaultPropertyFiltering(
            "availabilityEnd.lessThan=" + UPDATED_AVAILABILITY_END,
            "availabilityEnd.lessThan=" + DEFAULT_AVAILABILITY_END
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByAvailabilityEndIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where availabilityEnd is greater than
        defaultPropertyFiltering(
            "availabilityEnd.greaterThan=" + SMALLER_AVAILABILITY_END,
            "availabilityEnd.greaterThan=" + DEFAULT_AVAILABILITY_END
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByInstantBookIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where instantBook equals to
        defaultPropertyFiltering("instantBook.equals=" + DEFAULT_INSTANT_BOOK, "instantBook.equals=" + UPDATED_INSTANT_BOOK);
    }

    @Test
    @Transactional
    void getAllPropertiesByInstantBookIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where instantBook in
        defaultPropertyFiltering(
            "instantBook.in=" + DEFAULT_INSTANT_BOOK + "," + UPDATED_INSTANT_BOOK,
            "instantBook.in=" + UPDATED_INSTANT_BOOK
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByInstantBookIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where instantBook is not null
        defaultPropertyFiltering("instantBook.specified=true", "instantBook.specified=false");
    }

    @Test
    @Transactional
    void getAllPropertiesByMinimumStayIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where minimumStay equals to
        defaultPropertyFiltering("minimumStay.equals=" + DEFAULT_MINIMUM_STAY, "minimumStay.equals=" + UPDATED_MINIMUM_STAY);
    }

    @Test
    @Transactional
    void getAllPropertiesByMinimumStayIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where minimumStay in
        defaultPropertyFiltering(
            "minimumStay.in=" + DEFAULT_MINIMUM_STAY + "," + UPDATED_MINIMUM_STAY,
            "minimumStay.in=" + UPDATED_MINIMUM_STAY
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByMinimumStayIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where minimumStay is not null
        defaultPropertyFiltering("minimumStay.specified=true", "minimumStay.specified=false");
    }

    @Test
    @Transactional
    void getAllPropertiesByMinimumStayIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where minimumStay is greater than or equal to
        defaultPropertyFiltering(
            "minimumStay.greaterThanOrEqual=" + DEFAULT_MINIMUM_STAY,
            "minimumStay.greaterThanOrEqual=" + UPDATED_MINIMUM_STAY
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByMinimumStayIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where minimumStay is less than or equal to
        defaultPropertyFiltering(
            "minimumStay.lessThanOrEqual=" + DEFAULT_MINIMUM_STAY,
            "minimumStay.lessThanOrEqual=" + SMALLER_MINIMUM_STAY
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByMinimumStayIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where minimumStay is less than
        defaultPropertyFiltering("minimumStay.lessThan=" + UPDATED_MINIMUM_STAY, "minimumStay.lessThan=" + DEFAULT_MINIMUM_STAY);
    }

    @Test
    @Transactional
    void getAllPropertiesByMinimumStayIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where minimumStay is greater than
        defaultPropertyFiltering("minimumStay.greaterThan=" + SMALLER_MINIMUM_STAY, "minimumStay.greaterThan=" + DEFAULT_MINIMUM_STAY);
    }

    @Test
    @Transactional
    void getAllPropertiesByCancellationPolicyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where cancellationPolicy equals to
        defaultPropertyFiltering(
            "cancellationPolicy.equals=" + DEFAULT_CANCELLATION_POLICY,
            "cancellationPolicy.equals=" + UPDATED_CANCELLATION_POLICY
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByCancellationPolicyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where cancellationPolicy in
        defaultPropertyFiltering(
            "cancellationPolicy.in=" + DEFAULT_CANCELLATION_POLICY + "," + UPDATED_CANCELLATION_POLICY,
            "cancellationPolicy.in=" + UPDATED_CANCELLATION_POLICY
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByCancellationPolicyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where cancellationPolicy is not null
        defaultPropertyFiltering("cancellationPolicy.specified=true", "cancellationPolicy.specified=false");
    }

    @Test
    @Transactional
    void getAllPropertiesByCancellationPolicyContainsSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where cancellationPolicy contains
        defaultPropertyFiltering(
            "cancellationPolicy.contains=" + DEFAULT_CANCELLATION_POLICY,
            "cancellationPolicy.contains=" + UPDATED_CANCELLATION_POLICY
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByCancellationPolicyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where cancellationPolicy does not contain
        defaultPropertyFiltering(
            "cancellationPolicy.doesNotContain=" + UPDATED_CANCELLATION_POLICY,
            "cancellationPolicy.doesNotContain=" + DEFAULT_CANCELLATION_POLICY
        );
    }

    @Test
    @Transactional
    void getAllPropertiesByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where isActive equals to
        defaultPropertyFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPropertiesByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where isActive in
        defaultPropertyFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPropertiesByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        // Get all the propertyList where isActive is not null
        defaultPropertyFiltering("isActive.specified=true", "isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllPropertiesByHostIsEqualToSomething() throws Exception {
        User host;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            propertyRepository.saveAndFlush(property);
            host = UserResourceIT.createEntity();
        } else {
            host = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(host);
        em.flush();
        property.setHost(host);
        propertyRepository.saveAndFlush(property);
        Long hostId = host.getId();
        // Get all the propertyList where host equals to hostId
        defaultPropertyShouldBeFound("hostId.equals=" + hostId);

        // Get all the propertyList where host equals to (hostId + 1)
        defaultPropertyShouldNotBeFound("hostId.equals=" + (hostId + 1));
    }

    @Test
    @Transactional
    void getAllPropertiesByCityIsEqualToSomething() throws Exception {
        City city;
        if (TestUtil.findAll(em, City.class).isEmpty()) {
            propertyRepository.saveAndFlush(property);
            city = CityResourceIT.createEntity();
        } else {
            city = TestUtil.findAll(em, City.class).get(0);
        }
        em.persist(city);
        em.flush();
        property.setCity(city);
        propertyRepository.saveAndFlush(property);
        Long cityId = city.getId();
        // Get all the propertyList where city equals to cityId
        defaultPropertyShouldBeFound("cityId.equals=" + cityId);

        // Get all the propertyList where city equals to (cityId + 1)
        defaultPropertyShouldNotBeFound("cityId.equals=" + (cityId + 1));
    }

    @Test
    @Transactional
    void getAllPropertiesByAmenitiesIsEqualToSomething() throws Exception {
        Amenity amenities;
        if (TestUtil.findAll(em, Amenity.class).isEmpty()) {
            propertyRepository.saveAndFlush(property);
            amenities = AmenityResourceIT.createEntity();
        } else {
            amenities = TestUtil.findAll(em, Amenity.class).get(0);
        }
        em.persist(amenities);
        em.flush();
        property.addAmenities(amenities);
        propertyRepository.saveAndFlush(property);
        Long amenitiesId = amenities.getId();
        // Get all the propertyList where amenities equals to amenitiesId
        defaultPropertyShouldBeFound("amenitiesId.equals=" + amenitiesId);

        // Get all the propertyList where amenities equals to (amenitiesId + 1)
        defaultPropertyShouldNotBeFound("amenitiesId.equals=" + (amenitiesId + 1));
    }

    @Test
    @Transactional
    void getAllPropertiesByCategoriesIsEqualToSomething() throws Exception {
        PropertyCategory categories;
        if (TestUtil.findAll(em, PropertyCategory.class).isEmpty()) {
            propertyRepository.saveAndFlush(property);
            categories = PropertyCategoryResourceIT.createEntity();
        } else {
            categories = TestUtil.findAll(em, PropertyCategory.class).get(0);
        }
        em.persist(categories);
        em.flush();
        property.addCategories(categories);
        propertyRepository.saveAndFlush(property);
        Long categoriesId = categories.getId();
        // Get all the propertyList where categories equals to categoriesId
        defaultPropertyShouldBeFound("categoriesId.equals=" + categoriesId);

        // Get all the propertyList where categories equals to (categoriesId + 1)
        defaultPropertyShouldNotBeFound("categoriesId.equals=" + (categoriesId + 1));
    }

    private void defaultPropertyFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPropertyShouldBeFound(shouldBeFound);
        defaultPropertyShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPropertyShouldBeFound(String filter) throws Exception {
        restPropertyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(property.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].pricePerNight").value(hasItem(sameNumber(DEFAULT_PRICE_PER_NIGHT))))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(sameNumber(DEFAULT_LATITUDE))))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(sameNumber(DEFAULT_LONGITUDE))))
            .andExpect(jsonPath("$.[*].numberOfRooms").value(hasItem(DEFAULT_NUMBER_OF_ROOMS)))
            .andExpect(jsonPath("$.[*].numberOfBathrooms").value(hasItem(DEFAULT_NUMBER_OF_BATHROOMS)))
            .andExpect(jsonPath("$.[*].maxGuests").value(hasItem(DEFAULT_MAX_GUESTS)))
            .andExpect(jsonPath("$.[*].propertySize").value(hasItem(DEFAULT_PROPERTY_SIZE)))
            .andExpect(jsonPath("$.[*].availabilityStart").value(hasItem(sameInstant(DEFAULT_AVAILABILITY_START))))
            .andExpect(jsonPath("$.[*].availabilityEnd").value(hasItem(sameInstant(DEFAULT_AVAILABILITY_END))))
            .andExpect(jsonPath("$.[*].instantBook").value(hasItem(DEFAULT_INSTANT_BOOK.booleanValue())))
            .andExpect(jsonPath("$.[*].minimumStay").value(hasItem(DEFAULT_MINIMUM_STAY)))
            .andExpect(jsonPath("$.[*].cancellationPolicy").value(hasItem(DEFAULT_CANCELLATION_POLICY)))
            .andExpect(jsonPath("$.[*].houseRules").value(hasItem(DEFAULT_HOUSE_RULES.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())));

        // Check, that the count call also returns 1
        restPropertyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPropertyShouldNotBeFound(String filter) throws Exception {
        restPropertyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPropertyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProperty() throws Exception {
        // Get the property
        restPropertyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProperty() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        propertySearchRepository.save(property);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertySearchRepository.findAll());

        // Update the property
        Property updatedProperty = propertyRepository.findById(property.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProperty are not directly saved in db
        em.detach(updatedProperty);
        updatedProperty
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .pricePerNight(UPDATED_PRICE_PER_NIGHT)
            .address(UPDATED_ADDRESS)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .numberOfRooms(UPDATED_NUMBER_OF_ROOMS)
            .numberOfBathrooms(UPDATED_NUMBER_OF_BATHROOMS)
            .maxGuests(UPDATED_MAX_GUESTS)
            .propertySize(UPDATED_PROPERTY_SIZE)
            .availabilityStart(UPDATED_AVAILABILITY_START)
            .availabilityEnd(UPDATED_AVAILABILITY_END)
            .instantBook(UPDATED_INSTANT_BOOK)
            .minimumStay(UPDATED_MINIMUM_STAY)
            .cancellationPolicy(UPDATED_CANCELLATION_POLICY)
            .houseRules(UPDATED_HOUSE_RULES)
            .isActive(UPDATED_IS_ACTIVE);
        PropertyDTO propertyDTO = propertyMapper.toDto(updatedProperty);

        restPropertyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, propertyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(propertyDTO))
            )
            .andExpect(status().isOk());

        // Validate the Property in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPropertyToMatchAllProperties(updatedProperty);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Property> propertySearchList = Streamable.of(propertySearchRepository.findAll()).toList();
                Property testPropertySearch = propertySearchList.get(searchDatabaseSizeAfter - 1);

                assertPropertyAllPropertiesEquals(testPropertySearch, updatedProperty);
            });
    }

    @Test
    @Transactional
    void putNonExistingProperty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertySearchRepository.findAll());
        property.setId(longCount.incrementAndGet());

        // Create the Property
        PropertyDTO propertyDTO = propertyMapper.toDto(property);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPropertyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, propertyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(propertyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Property in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchProperty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertySearchRepository.findAll());
        property.setId(longCount.incrementAndGet());

        // Create the Property
        PropertyDTO propertyDTO = propertyMapper.toDto(property);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPropertyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(propertyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Property in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProperty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertySearchRepository.findAll());
        property.setId(longCount.incrementAndGet());

        // Create the Property
        PropertyDTO propertyDTO = propertyMapper.toDto(property);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPropertyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(propertyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Property in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdatePropertyWithPatch() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the property using partial update
        Property partialUpdatedProperty = new Property();
        partialUpdatedProperty.setId(property.getId());

        partialUpdatedProperty
            .pricePerNight(UPDATED_PRICE_PER_NIGHT)
            .address(UPDATED_ADDRESS)
            .latitude(UPDATED_LATITUDE)
            .propertySize(UPDATED_PROPERTY_SIZE)
            .availabilityEnd(UPDATED_AVAILABILITY_END)
            .instantBook(UPDATED_INSTANT_BOOK)
            .minimumStay(UPDATED_MINIMUM_STAY)
            .isActive(UPDATED_IS_ACTIVE);

        restPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProperty.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProperty))
            )
            .andExpect(status().isOk());

        // Validate the Property in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPropertyUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProperty, property), getPersistedProperty(property));
    }

    @Test
    @Transactional
    void fullUpdatePropertyWithPatch() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the property using partial update
        Property partialUpdatedProperty = new Property();
        partialUpdatedProperty.setId(property.getId());

        partialUpdatedProperty
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .pricePerNight(UPDATED_PRICE_PER_NIGHT)
            .address(UPDATED_ADDRESS)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .numberOfRooms(UPDATED_NUMBER_OF_ROOMS)
            .numberOfBathrooms(UPDATED_NUMBER_OF_BATHROOMS)
            .maxGuests(UPDATED_MAX_GUESTS)
            .propertySize(UPDATED_PROPERTY_SIZE)
            .availabilityStart(UPDATED_AVAILABILITY_START)
            .availabilityEnd(UPDATED_AVAILABILITY_END)
            .instantBook(UPDATED_INSTANT_BOOK)
            .minimumStay(UPDATED_MINIMUM_STAY)
            .cancellationPolicy(UPDATED_CANCELLATION_POLICY)
            .houseRules(UPDATED_HOUSE_RULES)
            .isActive(UPDATED_IS_ACTIVE);

        restPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProperty.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProperty))
            )
            .andExpect(status().isOk());

        // Validate the Property in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPropertyUpdatableFieldsEquals(partialUpdatedProperty, getPersistedProperty(partialUpdatedProperty));
    }

    @Test
    @Transactional
    void patchNonExistingProperty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertySearchRepository.findAll());
        property.setId(longCount.incrementAndGet());

        // Create the Property
        PropertyDTO propertyDTO = propertyMapper.toDto(property);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, propertyDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(propertyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Property in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProperty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertySearchRepository.findAll());
        property.setId(longCount.incrementAndGet());

        // Create the Property
        PropertyDTO propertyDTO = propertyMapper.toDto(property);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPropertyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(propertyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Property in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProperty() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertySearchRepository.findAll());
        property.setId(longCount.incrementAndGet());

        // Create the Property
        PropertyDTO propertyDTO = propertyMapper.toDto(property);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPropertyMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(propertyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Property in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteProperty() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);
        propertyRepository.save(property);
        propertySearchRepository.save(property);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the property
        restPropertyMockMvc
            .perform(delete(ENTITY_API_URL_ID, property.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchProperty() throws Exception {
        // Initialize the database
        insertedProperty = propertyRepository.saveAndFlush(property);
        propertySearchRepository.save(property);

        // Search the property
        restPropertyMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + property.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(property.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].pricePerNight").value(hasItem(sameNumber(DEFAULT_PRICE_PER_NIGHT))))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(sameNumber(DEFAULT_LATITUDE))))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(sameNumber(DEFAULT_LONGITUDE))))
            .andExpect(jsonPath("$.[*].numberOfRooms").value(hasItem(DEFAULT_NUMBER_OF_ROOMS)))
            .andExpect(jsonPath("$.[*].numberOfBathrooms").value(hasItem(DEFAULT_NUMBER_OF_BATHROOMS)))
            .andExpect(jsonPath("$.[*].maxGuests").value(hasItem(DEFAULT_MAX_GUESTS)))
            .andExpect(jsonPath("$.[*].propertySize").value(hasItem(DEFAULT_PROPERTY_SIZE)))
            .andExpect(jsonPath("$.[*].availabilityStart").value(hasItem(sameInstant(DEFAULT_AVAILABILITY_START))))
            .andExpect(jsonPath("$.[*].availabilityEnd").value(hasItem(sameInstant(DEFAULT_AVAILABILITY_END))))
            .andExpect(jsonPath("$.[*].instantBook").value(hasItem(DEFAULT_INSTANT_BOOK.booleanValue())))
            .andExpect(jsonPath("$.[*].minimumStay").value(hasItem(DEFAULT_MINIMUM_STAY)))
            .andExpect(jsonPath("$.[*].cancellationPolicy").value(hasItem(DEFAULT_CANCELLATION_POLICY)))
            .andExpect(jsonPath("$.[*].houseRules").value(hasItem(DEFAULT_HOUSE_RULES.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())));
    }

    protected long getRepositoryCount() {
        return propertyRepository.count();
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

    protected Property getPersistedProperty(Property property) {
        return propertyRepository.findById(property.getId()).orElseThrow();
    }

    protected void assertPersistedPropertyToMatchAllProperties(Property expectedProperty) {
        assertPropertyAllPropertiesEquals(expectedProperty, getPersistedProperty(expectedProperty));
    }

    protected void assertPersistedPropertyToMatchUpdatableProperties(Property expectedProperty) {
        assertPropertyAllUpdatablePropertiesEquals(expectedProperty, getPersistedProperty(expectedProperty));
    }
}
