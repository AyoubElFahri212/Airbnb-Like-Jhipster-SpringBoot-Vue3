package ma.fullstackclone.airbnb.web.rest;

import static ma.fullstackclone.airbnb.domain.AmenityAsserts.*;
import static ma.fullstackclone.airbnb.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import ma.fullstackclone.airbnb.IntegrationTest;
import ma.fullstackclone.airbnb.domain.Amenity;
import ma.fullstackclone.airbnb.domain.Property;
import ma.fullstackclone.airbnb.repository.AmenityRepository;
import ma.fullstackclone.airbnb.repository.search.AmenitySearchRepository;
import ma.fullstackclone.airbnb.service.dto.AmenityDTO;
import ma.fullstackclone.airbnb.service.mapper.AmenityMapper;
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
 * Integration tests for the {@link AmenityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AmenityResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ICON_CLASS = "AAAAAAAAAA";
    private static final String UPDATED_ICON_CLASS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/amenities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/amenities/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AmenityRepository amenityRepository;

    @Autowired
    private AmenityMapper amenityMapper;

    @Autowired
    private AmenitySearchRepository amenitySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAmenityMockMvc;

    private Amenity amenity;

    private Amenity insertedAmenity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Amenity createEntity() {
        return new Amenity().name(DEFAULT_NAME).iconClass(DEFAULT_ICON_CLASS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Amenity createUpdatedEntity() {
        return new Amenity().name(UPDATED_NAME).iconClass(UPDATED_ICON_CLASS);
    }

    @BeforeEach
    public void initTest() {
        amenity = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAmenity != null) {
            amenityRepository.delete(insertedAmenity);
            amenitySearchRepository.delete(insertedAmenity);
            insertedAmenity = null;
        }
    }

    @Test
    @Transactional
    void createAmenity() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(amenitySearchRepository.findAll());
        // Create the Amenity
        AmenityDTO amenityDTO = amenityMapper.toDto(amenity);
        var returnedAmenityDTO = om.readValue(
            restAmenityMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(amenityDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AmenityDTO.class
        );

        // Validate the Amenity in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAmenity = amenityMapper.toEntity(returnedAmenityDTO);
        assertAmenityUpdatableFieldsEquals(returnedAmenity, getPersistedAmenity(returnedAmenity));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(amenitySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedAmenity = returnedAmenity;
    }

    @Test
    @Transactional
    void createAmenityWithExistingId() throws Exception {
        // Create the Amenity with an existing ID
        amenity.setId(1L);
        AmenityDTO amenityDTO = amenityMapper.toDto(amenity);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(amenitySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restAmenityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(amenityDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Amenity in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(amenitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(amenitySearchRepository.findAll());
        // set the field null
        amenity.setName(null);

        // Create the Amenity, which fails.
        AmenityDTO amenityDTO = amenityMapper.toDto(amenity);

        restAmenityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(amenityDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(amenitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllAmenities() throws Exception {
        // Initialize the database
        insertedAmenity = amenityRepository.saveAndFlush(amenity);

        // Get all the amenityList
        restAmenityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(amenity.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].iconClass").value(hasItem(DEFAULT_ICON_CLASS)));
    }

    @Test
    @Transactional
    void getAmenity() throws Exception {
        // Initialize the database
        insertedAmenity = amenityRepository.saveAndFlush(amenity);

        // Get the amenity
        restAmenityMockMvc
            .perform(get(ENTITY_API_URL_ID, amenity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(amenity.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.iconClass").value(DEFAULT_ICON_CLASS));
    }

    @Test
    @Transactional
    void getAmenitiesByIdFiltering() throws Exception {
        // Initialize the database
        insertedAmenity = amenityRepository.saveAndFlush(amenity);

        Long id = amenity.getId();

        defaultAmenityFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAmenityFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAmenityFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAmenitiesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAmenity = amenityRepository.saveAndFlush(amenity);

        // Get all the amenityList where name equals to
        defaultAmenityFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAmenitiesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAmenity = amenityRepository.saveAndFlush(amenity);

        // Get all the amenityList where name in
        defaultAmenityFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAmenitiesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAmenity = amenityRepository.saveAndFlush(amenity);

        // Get all the amenityList where name is not null
        defaultAmenityFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllAmenitiesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedAmenity = amenityRepository.saveAndFlush(amenity);

        // Get all the amenityList where name contains
        defaultAmenityFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAmenitiesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAmenity = amenityRepository.saveAndFlush(amenity);

        // Get all the amenityList where name does not contain
        defaultAmenityFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllAmenitiesByIconClassIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAmenity = amenityRepository.saveAndFlush(amenity);

        // Get all the amenityList where iconClass equals to
        defaultAmenityFiltering("iconClass.equals=" + DEFAULT_ICON_CLASS, "iconClass.equals=" + UPDATED_ICON_CLASS);
    }

    @Test
    @Transactional
    void getAllAmenitiesByIconClassIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAmenity = amenityRepository.saveAndFlush(amenity);

        // Get all the amenityList where iconClass in
        defaultAmenityFiltering("iconClass.in=" + DEFAULT_ICON_CLASS + "," + UPDATED_ICON_CLASS, "iconClass.in=" + UPDATED_ICON_CLASS);
    }

    @Test
    @Transactional
    void getAllAmenitiesByIconClassIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAmenity = amenityRepository.saveAndFlush(amenity);

        // Get all the amenityList where iconClass is not null
        defaultAmenityFiltering("iconClass.specified=true", "iconClass.specified=false");
    }

    @Test
    @Transactional
    void getAllAmenitiesByIconClassContainsSomething() throws Exception {
        // Initialize the database
        insertedAmenity = amenityRepository.saveAndFlush(amenity);

        // Get all the amenityList where iconClass contains
        defaultAmenityFiltering("iconClass.contains=" + DEFAULT_ICON_CLASS, "iconClass.contains=" + UPDATED_ICON_CLASS);
    }

    @Test
    @Transactional
    void getAllAmenitiesByIconClassNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAmenity = amenityRepository.saveAndFlush(amenity);

        // Get all the amenityList where iconClass does not contain
        defaultAmenityFiltering("iconClass.doesNotContain=" + UPDATED_ICON_CLASS, "iconClass.doesNotContain=" + DEFAULT_ICON_CLASS);
    }

    @Test
    @Transactional
    void getAllAmenitiesByPropertyIsEqualToSomething() throws Exception {
        Property property;
        if (TestUtil.findAll(em, Property.class).isEmpty()) {
            amenityRepository.saveAndFlush(amenity);
            property = PropertyResourceIT.createEntity();
        } else {
            property = TestUtil.findAll(em, Property.class).get(0);
        }
        em.persist(property);
        em.flush();
        amenity.addProperty(property);
        amenityRepository.saveAndFlush(amenity);
        Long propertyId = property.getId();
        // Get all the amenityList where property equals to propertyId
        defaultAmenityShouldBeFound("propertyId.equals=" + propertyId);

        // Get all the amenityList where property equals to (propertyId + 1)
        defaultAmenityShouldNotBeFound("propertyId.equals=" + (propertyId + 1));
    }

    private void defaultAmenityFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAmenityShouldBeFound(shouldBeFound);
        defaultAmenityShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAmenityShouldBeFound(String filter) throws Exception {
        restAmenityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(amenity.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].iconClass").value(hasItem(DEFAULT_ICON_CLASS)));

        // Check, that the count call also returns 1
        restAmenityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAmenityShouldNotBeFound(String filter) throws Exception {
        restAmenityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAmenityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAmenity() throws Exception {
        // Get the amenity
        restAmenityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAmenity() throws Exception {
        // Initialize the database
        insertedAmenity = amenityRepository.saveAndFlush(amenity);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        amenitySearchRepository.save(amenity);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(amenitySearchRepository.findAll());

        // Update the amenity
        Amenity updatedAmenity = amenityRepository.findById(amenity.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAmenity are not directly saved in db
        em.detach(updatedAmenity);
        updatedAmenity.name(UPDATED_NAME).iconClass(UPDATED_ICON_CLASS);
        AmenityDTO amenityDTO = amenityMapper.toDto(updatedAmenity);

        restAmenityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, amenityDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(amenityDTO))
            )
            .andExpect(status().isOk());

        // Validate the Amenity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAmenityToMatchAllProperties(updatedAmenity);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(amenitySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Amenity> amenitySearchList = Streamable.of(amenitySearchRepository.findAll()).toList();
                Amenity testAmenitySearch = amenitySearchList.get(searchDatabaseSizeAfter - 1);

                assertAmenityAllPropertiesEquals(testAmenitySearch, updatedAmenity);
            });
    }

    @Test
    @Transactional
    void putNonExistingAmenity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(amenitySearchRepository.findAll());
        amenity.setId(longCount.incrementAndGet());

        // Create the Amenity
        AmenityDTO amenityDTO = amenityMapper.toDto(amenity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAmenityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, amenityDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(amenityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Amenity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(amenitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchAmenity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(amenitySearchRepository.findAll());
        amenity.setId(longCount.incrementAndGet());

        // Create the Amenity
        AmenityDTO amenityDTO = amenityMapper.toDto(amenity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAmenityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(amenityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Amenity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(amenitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAmenity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(amenitySearchRepository.findAll());
        amenity.setId(longCount.incrementAndGet());

        // Create the Amenity
        AmenityDTO amenityDTO = amenityMapper.toDto(amenity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAmenityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(amenityDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Amenity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(amenitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateAmenityWithPatch() throws Exception {
        // Initialize the database
        insertedAmenity = amenityRepository.saveAndFlush(amenity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the amenity using partial update
        Amenity partialUpdatedAmenity = new Amenity();
        partialUpdatedAmenity.setId(amenity.getId());

        partialUpdatedAmenity.name(UPDATED_NAME);

        restAmenityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAmenity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAmenity))
            )
            .andExpect(status().isOk());

        // Validate the Amenity in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAmenityUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAmenity, amenity), getPersistedAmenity(amenity));
    }

    @Test
    @Transactional
    void fullUpdateAmenityWithPatch() throws Exception {
        // Initialize the database
        insertedAmenity = amenityRepository.saveAndFlush(amenity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the amenity using partial update
        Amenity partialUpdatedAmenity = new Amenity();
        partialUpdatedAmenity.setId(amenity.getId());

        partialUpdatedAmenity.name(UPDATED_NAME).iconClass(UPDATED_ICON_CLASS);

        restAmenityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAmenity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAmenity))
            )
            .andExpect(status().isOk());

        // Validate the Amenity in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAmenityUpdatableFieldsEquals(partialUpdatedAmenity, getPersistedAmenity(partialUpdatedAmenity));
    }

    @Test
    @Transactional
    void patchNonExistingAmenity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(amenitySearchRepository.findAll());
        amenity.setId(longCount.incrementAndGet());

        // Create the Amenity
        AmenityDTO amenityDTO = amenityMapper.toDto(amenity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAmenityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, amenityDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(amenityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Amenity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(amenitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAmenity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(amenitySearchRepository.findAll());
        amenity.setId(longCount.incrementAndGet());

        // Create the Amenity
        AmenityDTO amenityDTO = amenityMapper.toDto(amenity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAmenityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(amenityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Amenity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(amenitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAmenity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(amenitySearchRepository.findAll());
        amenity.setId(longCount.incrementAndGet());

        // Create the Amenity
        AmenityDTO amenityDTO = amenityMapper.toDto(amenity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAmenityMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(amenityDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Amenity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(amenitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteAmenity() throws Exception {
        // Initialize the database
        insertedAmenity = amenityRepository.saveAndFlush(amenity);
        amenityRepository.save(amenity);
        amenitySearchRepository.save(amenity);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(amenitySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the amenity
        restAmenityMockMvc
            .perform(delete(ENTITY_API_URL_ID, amenity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(amenitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchAmenity() throws Exception {
        // Initialize the database
        insertedAmenity = amenityRepository.saveAndFlush(amenity);
        amenitySearchRepository.save(amenity);

        // Search the amenity
        restAmenityMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + amenity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(amenity.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].iconClass").value(hasItem(DEFAULT_ICON_CLASS)));
    }

    protected long getRepositoryCount() {
        return amenityRepository.count();
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

    protected Amenity getPersistedAmenity(Amenity amenity) {
        return amenityRepository.findById(amenity.getId()).orElseThrow();
    }

    protected void assertPersistedAmenityToMatchAllProperties(Amenity expectedAmenity) {
        assertAmenityAllPropertiesEquals(expectedAmenity, getPersistedAmenity(expectedAmenity));
    }

    protected void assertPersistedAmenityToMatchUpdatableProperties(Amenity expectedAmenity) {
        assertAmenityAllUpdatablePropertiesEquals(expectedAmenity, getPersistedAmenity(expectedAmenity));
    }
}
