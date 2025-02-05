package ma.fullstackclone.airbnb.web.rest;

import static ma.fullstackclone.airbnb.domain.CityAsserts.*;
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
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import ma.fullstackclone.airbnb.IntegrationTest;
import ma.fullstackclone.airbnb.domain.City;
import ma.fullstackclone.airbnb.domain.Country;
import ma.fullstackclone.airbnb.repository.CityRepository;
import ma.fullstackclone.airbnb.repository.search.CitySearchRepository;
import ma.fullstackclone.airbnb.service.dto.CityDTO;
import ma.fullstackclone.airbnb.service.mapper.CityMapper;
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
 * Integration tests for the {@link CityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CityResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_POSTAL_CODE = "AAAAAAAAAA";
    private static final String UPDATED_POSTAL_CODE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_LATITUDE = new BigDecimal(1);
    private static final BigDecimal UPDATED_LATITUDE = new BigDecimal(2);
    private static final BigDecimal SMALLER_LATITUDE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_LONGITUDE = new BigDecimal(1);
    private static final BigDecimal UPDATED_LONGITUDE = new BigDecimal(2);
    private static final BigDecimal SMALLER_LONGITUDE = new BigDecimal(1 - 1);

    private static final String ENTITY_API_URL = "/api/cities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/cities/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CityMapper cityMapper;

    @Autowired
    private CitySearchRepository citySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCityMockMvc;

    private City city;

    private City insertedCity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static City createEntity() {
        return new City().name(DEFAULT_NAME).postalCode(DEFAULT_POSTAL_CODE).latitude(DEFAULT_LATITUDE).longitude(DEFAULT_LONGITUDE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static City createUpdatedEntity() {
        return new City().name(UPDATED_NAME).postalCode(UPDATED_POSTAL_CODE).latitude(UPDATED_LATITUDE).longitude(UPDATED_LONGITUDE);
    }

    @BeforeEach
    public void initTest() {
        city = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCity != null) {
            cityRepository.delete(insertedCity);
            citySearchRepository.delete(insertedCity);
            insertedCity = null;
        }
    }

    @Test
    @Transactional
    void createCity() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll());
        // Create the City
        CityDTO cityDTO = cityMapper.toDto(city);
        var returnedCityDTO = om.readValue(
            restCityMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CityDTO.class
        );

        // Validate the City in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCity = cityMapper.toEntity(returnedCityDTO);
        assertCityUpdatableFieldsEquals(returnedCity, getPersistedCity(returnedCity));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedCity = returnedCity;
    }

    @Test
    @Transactional
    void createCityWithExistingId() throws Exception {
        // Create the City with an existing ID
        city.setId(1L);
        CityDTO cityDTO = cityMapper.toDto(city);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityDTO)))
            .andExpect(status().isBadRequest());

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll());
        // set the field null
        city.setName(null);

        // Create the City, which fails.
        CityDTO cityDTO = cityMapper.toDto(city);

        restCityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLatitudeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll());
        // set the field null
        city.setLatitude(null);

        // Create the City, which fails.
        CityDTO cityDTO = cityMapper.toDto(city);

        restCityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLongitudeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll());
        // set the field null
        city.setLongitude(null);

        // Create the City, which fails.
        CityDTO cityDTO = cityMapper.toDto(city);

        restCityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCities() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList
        restCityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(city.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(sameNumber(DEFAULT_LATITUDE))))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(sameNumber(DEFAULT_LONGITUDE))));
    }

    @Test
    @Transactional
    void getCity() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get the city
        restCityMockMvc
            .perform(get(ENTITY_API_URL_ID, city.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(city.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.postalCode").value(DEFAULT_POSTAL_CODE))
            .andExpect(jsonPath("$.latitude").value(sameNumber(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.longitude").value(sameNumber(DEFAULT_LONGITUDE)));
    }

    @Test
    @Transactional
    void getCitiesByIdFiltering() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        Long id = city.getId();

        defaultCityFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCityFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCityFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCitiesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where name equals to
        defaultCityFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCitiesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where name in
        defaultCityFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCitiesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where name is not null
        defaultCityFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllCitiesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where name contains
        defaultCityFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCitiesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where name does not contain
        defaultCityFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllCitiesByPostalCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where postalCode equals to
        defaultCityFiltering("postalCode.equals=" + DEFAULT_POSTAL_CODE, "postalCode.equals=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    void getAllCitiesByPostalCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where postalCode in
        defaultCityFiltering("postalCode.in=" + DEFAULT_POSTAL_CODE + "," + UPDATED_POSTAL_CODE, "postalCode.in=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    void getAllCitiesByPostalCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where postalCode is not null
        defaultCityFiltering("postalCode.specified=true", "postalCode.specified=false");
    }

    @Test
    @Transactional
    void getAllCitiesByPostalCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where postalCode contains
        defaultCityFiltering("postalCode.contains=" + DEFAULT_POSTAL_CODE, "postalCode.contains=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    void getAllCitiesByPostalCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where postalCode does not contain
        defaultCityFiltering("postalCode.doesNotContain=" + UPDATED_POSTAL_CODE, "postalCode.doesNotContain=" + DEFAULT_POSTAL_CODE);
    }

    @Test
    @Transactional
    void getAllCitiesByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where latitude equals to
        defaultCityFiltering("latitude.equals=" + DEFAULT_LATITUDE, "latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllCitiesByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where latitude in
        defaultCityFiltering("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE, "latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllCitiesByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where latitude is not null
        defaultCityFiltering("latitude.specified=true", "latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllCitiesByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where latitude is greater than or equal to
        defaultCityFiltering("latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE, "latitude.greaterThanOrEqual=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllCitiesByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where latitude is less than or equal to
        defaultCityFiltering("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE, "latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllCitiesByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where latitude is less than
        defaultCityFiltering("latitude.lessThan=" + UPDATED_LATITUDE, "latitude.lessThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllCitiesByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where latitude is greater than
        defaultCityFiltering("latitude.greaterThan=" + SMALLER_LATITUDE, "latitude.greaterThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllCitiesByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where longitude equals to
        defaultCityFiltering("longitude.equals=" + DEFAULT_LONGITUDE, "longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllCitiesByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where longitude in
        defaultCityFiltering("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE, "longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllCitiesByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where longitude is not null
        defaultCityFiltering("longitude.specified=true", "longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllCitiesByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where longitude is greater than or equal to
        defaultCityFiltering("longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE, "longitude.greaterThanOrEqual=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllCitiesByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where longitude is less than or equal to
        defaultCityFiltering("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE, "longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllCitiesByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where longitude is less than
        defaultCityFiltering("longitude.lessThan=" + UPDATED_LONGITUDE, "longitude.lessThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllCitiesByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        // Get all the cityList where longitude is greater than
        defaultCityFiltering("longitude.greaterThan=" + SMALLER_LONGITUDE, "longitude.greaterThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllCitiesByCountryIsEqualToSomething() throws Exception {
        Country country;
        if (TestUtil.findAll(em, Country.class).isEmpty()) {
            cityRepository.saveAndFlush(city);
            country = CountryResourceIT.createEntity();
        } else {
            country = TestUtil.findAll(em, Country.class).get(0);
        }
        em.persist(country);
        em.flush();
        city.setCountry(country);
        cityRepository.saveAndFlush(city);
        Long countryId = country.getId();
        // Get all the cityList where country equals to countryId
        defaultCityShouldBeFound("countryId.equals=" + countryId);

        // Get all the cityList where country equals to (countryId + 1)
        defaultCityShouldNotBeFound("countryId.equals=" + (countryId + 1));
    }

    private void defaultCityFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCityShouldBeFound(shouldBeFound);
        defaultCityShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCityShouldBeFound(String filter) throws Exception {
        restCityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(city.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(sameNumber(DEFAULT_LATITUDE))))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(sameNumber(DEFAULT_LONGITUDE))));

        // Check, that the count call also returns 1
        restCityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCityShouldNotBeFound(String filter) throws Exception {
        restCityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCity() throws Exception {
        // Get the city
        restCityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCity() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        citySearchRepository.save(city);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll());

        // Update the city
        City updatedCity = cityRepository.findById(city.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCity are not directly saved in db
        em.detach(updatedCity);
        updatedCity.name(UPDATED_NAME).postalCode(UPDATED_POSTAL_CODE).latitude(UPDATED_LATITUDE).longitude(UPDATED_LONGITUDE);
        CityDTO cityDTO = cityMapper.toDto(updatedCity);

        restCityMockMvc
            .perform(put(ENTITY_API_URL_ID, cityDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityDTO)))
            .andExpect(status().isOk());

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCityToMatchAllProperties(updatedCity);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<City> citySearchList = Streamable.of(citySearchRepository.findAll()).toList();
                City testCitySearch = citySearchList.get(searchDatabaseSizeAfter - 1);

                assertCityAllPropertiesEquals(testCitySearch, updatedCity);
            });
    }

    @Test
    @Transactional
    void putNonExistingCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll());
        city.setId(longCount.incrementAndGet());

        // Create the City
        CityDTO cityDTO = cityMapper.toDto(city);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCityMockMvc
            .perform(put(ENTITY_API_URL_ID, cityDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityDTO)))
            .andExpect(status().isBadRequest());

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll());
        city.setId(longCount.incrementAndGet());

        // Create the City
        CityDTO cityDTO = cityMapper.toDto(city);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll());
        city.setId(longCount.incrementAndGet());

        // Create the City
        CityDTO cityDTO = cityMapper.toDto(city);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cityDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCityWithPatch() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the city using partial update
        City partialUpdatedCity = new City();
        partialUpdatedCity.setId(city.getId());

        restCityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCity))
            )
            .andExpect(status().isOk());

        // Validate the City in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCityUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCity, city), getPersistedCity(city));
    }

    @Test
    @Transactional
    void fullUpdateCityWithPatch() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the city using partial update
        City partialUpdatedCity = new City();
        partialUpdatedCity.setId(city.getId());

        partialUpdatedCity.name(UPDATED_NAME).postalCode(UPDATED_POSTAL_CODE).latitude(UPDATED_LATITUDE).longitude(UPDATED_LONGITUDE);

        restCityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCity))
            )
            .andExpect(status().isOk());

        // Validate the City in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCityUpdatableFieldsEquals(partialUpdatedCity, getPersistedCity(partialUpdatedCity));
    }

    @Test
    @Transactional
    void patchNonExistingCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll());
        city.setId(longCount.incrementAndGet());

        // Create the City
        CityDTO cityDTO = cityMapper.toDto(city);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cityDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(cityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll());
        city.setId(longCount.incrementAndGet());

        // Create the City
        CityDTO cityDTO = cityMapper.toDto(city);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll());
        city.setId(longCount.incrementAndGet());

        // Create the City
        CityDTO cityDTO = cityMapper.toDto(city);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCityMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(cityDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCity() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);
        cityRepository.save(city);
        citySearchRepository.save(city);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(citySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the city
        restCityMockMvc
            .perform(delete(ENTITY_API_URL_ID, city.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(citySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCity() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.saveAndFlush(city);
        citySearchRepository.save(city);

        // Search the city
        restCityMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + city.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(city.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(sameNumber(DEFAULT_LATITUDE))))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(sameNumber(DEFAULT_LONGITUDE))));
    }

    protected long getRepositoryCount() {
        return cityRepository.count();
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

    protected City getPersistedCity(City city) {
        return cityRepository.findById(city.getId()).orElseThrow();
    }

    protected void assertPersistedCityToMatchAllProperties(City expectedCity) {
        assertCityAllPropertiesEquals(expectedCity, getPersistedCity(expectedCity));
    }

    protected void assertPersistedCityToMatchUpdatableProperties(City expectedCity) {
        assertCityAllUpdatablePropertiesEquals(expectedCity, getPersistedCity(expectedCity));
    }
}
