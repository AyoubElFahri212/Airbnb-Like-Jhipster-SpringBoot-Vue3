package ma.fullstackclone.airbnb.web.rest;

import static ma.fullstackclone.airbnb.domain.PropertyCategoryAsserts.*;
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
import ma.fullstackclone.airbnb.domain.Property;
import ma.fullstackclone.airbnb.domain.PropertyCategory;
import ma.fullstackclone.airbnb.repository.PropertyCategoryRepository;
import ma.fullstackclone.airbnb.repository.search.PropertyCategorySearchRepository;
import ma.fullstackclone.airbnb.service.dto.PropertyCategoryDTO;
import ma.fullstackclone.airbnb.service.mapper.PropertyCategoryMapper;
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
 * Integration tests for the {@link PropertyCategoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PropertyCategoryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/property-categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/property-categories/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PropertyCategoryRepository propertyCategoryRepository;

    @Autowired
    private PropertyCategoryMapper propertyCategoryMapper;

    @Autowired
    private PropertyCategorySearchRepository propertyCategorySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPropertyCategoryMockMvc;

    private PropertyCategory propertyCategory;

    private PropertyCategory insertedPropertyCategory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PropertyCategory createEntity() {
        return new PropertyCategory().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PropertyCategory createUpdatedEntity() {
        return new PropertyCategory().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    public void initTest() {
        propertyCategory = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedPropertyCategory != null) {
            propertyCategoryRepository.delete(insertedPropertyCategory);
            propertyCategorySearchRepository.delete(insertedPropertyCategory);
            insertedPropertyCategory = null;
        }
    }

    @Test
    @Transactional
    void createPropertyCategory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyCategorySearchRepository.findAll());
        // Create the PropertyCategory
        PropertyCategoryDTO propertyCategoryDTO = propertyCategoryMapper.toDto(propertyCategory);
        var returnedPropertyCategoryDTO = om.readValue(
            restPropertyCategoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(propertyCategoryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PropertyCategoryDTO.class
        );

        // Validate the PropertyCategory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPropertyCategory = propertyCategoryMapper.toEntity(returnedPropertyCategoryDTO);
        assertPropertyCategoryUpdatableFieldsEquals(returnedPropertyCategory, getPersistedPropertyCategory(returnedPropertyCategory));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyCategorySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedPropertyCategory = returnedPropertyCategory;
    }

    @Test
    @Transactional
    void createPropertyCategoryWithExistingId() throws Exception {
        // Create the PropertyCategory with an existing ID
        propertyCategory.setId(1L);
        PropertyCategoryDTO propertyCategoryDTO = propertyCategoryMapper.toDto(propertyCategory);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyCategorySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restPropertyCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(propertyCategoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PropertyCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyCategorySearchRepository.findAll());
        // set the field null
        propertyCategory.setName(null);

        // Create the PropertyCategory, which fails.
        PropertyCategoryDTO propertyCategoryDTO = propertyCategoryMapper.toDto(propertyCategory);

        restPropertyCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(propertyCategoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllPropertyCategories() throws Exception {
        // Initialize the database
        insertedPropertyCategory = propertyCategoryRepository.saveAndFlush(propertyCategory);

        // Get all the propertyCategoryList
        restPropertyCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(propertyCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    void getPropertyCategory() throws Exception {
        // Initialize the database
        insertedPropertyCategory = propertyCategoryRepository.saveAndFlush(propertyCategory);

        // Get the propertyCategory
        restPropertyCategoryMockMvc
            .perform(get(ENTITY_API_URL_ID, propertyCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(propertyCategory.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    void getPropertyCategoriesByIdFiltering() throws Exception {
        // Initialize the database
        insertedPropertyCategory = propertyCategoryRepository.saveAndFlush(propertyCategory);

        Long id = propertyCategory.getId();

        defaultPropertyCategoryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPropertyCategoryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPropertyCategoryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPropertyCategoriesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPropertyCategory = propertyCategoryRepository.saveAndFlush(propertyCategory);

        // Get all the propertyCategoryList where name equals to
        defaultPropertyCategoryFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPropertyCategoriesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPropertyCategory = propertyCategoryRepository.saveAndFlush(propertyCategory);

        // Get all the propertyCategoryList where name in
        defaultPropertyCategoryFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPropertyCategoriesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPropertyCategory = propertyCategoryRepository.saveAndFlush(propertyCategory);

        // Get all the propertyCategoryList where name is not null
        defaultPropertyCategoryFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllPropertyCategoriesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedPropertyCategory = propertyCategoryRepository.saveAndFlush(propertyCategory);

        // Get all the propertyCategoryList where name contains
        defaultPropertyCategoryFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPropertyCategoriesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPropertyCategory = propertyCategoryRepository.saveAndFlush(propertyCategory);

        // Get all the propertyCategoryList where name does not contain
        defaultPropertyCategoryFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllPropertyCategoriesByPropertyIsEqualToSomething() throws Exception {
        Property property;
        if (TestUtil.findAll(em, Property.class).isEmpty()) {
            propertyCategoryRepository.saveAndFlush(propertyCategory);
            property = PropertyResourceIT.createEntity();
        } else {
            property = TestUtil.findAll(em, Property.class).get(0);
        }
        em.persist(property);
        em.flush();
        propertyCategory.addProperty(property);
        propertyCategoryRepository.saveAndFlush(propertyCategory);
        Long propertyId = property.getId();
        // Get all the propertyCategoryList where property equals to propertyId
        defaultPropertyCategoryShouldBeFound("propertyId.equals=" + propertyId);

        // Get all the propertyCategoryList where property equals to (propertyId + 1)
        defaultPropertyCategoryShouldNotBeFound("propertyId.equals=" + (propertyId + 1));
    }

    private void defaultPropertyCategoryFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPropertyCategoryShouldBeFound(shouldBeFound);
        defaultPropertyCategoryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPropertyCategoryShouldBeFound(String filter) throws Exception {
        restPropertyCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(propertyCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));

        // Check, that the count call also returns 1
        restPropertyCategoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPropertyCategoryShouldNotBeFound(String filter) throws Exception {
        restPropertyCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPropertyCategoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPropertyCategory() throws Exception {
        // Get the propertyCategory
        restPropertyCategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPropertyCategory() throws Exception {
        // Initialize the database
        insertedPropertyCategory = propertyCategoryRepository.saveAndFlush(propertyCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        propertyCategorySearchRepository.save(propertyCategory);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyCategorySearchRepository.findAll());

        // Update the propertyCategory
        PropertyCategory updatedPropertyCategory = propertyCategoryRepository.findById(propertyCategory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPropertyCategory are not directly saved in db
        em.detach(updatedPropertyCategory);
        updatedPropertyCategory.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        PropertyCategoryDTO propertyCategoryDTO = propertyCategoryMapper.toDto(updatedPropertyCategory);

        restPropertyCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, propertyCategoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(propertyCategoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the PropertyCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPropertyCategoryToMatchAllProperties(updatedPropertyCategory);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyCategorySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<PropertyCategory> propertyCategorySearchList = Streamable.of(propertyCategorySearchRepository.findAll()).toList();
                PropertyCategory testPropertyCategorySearch = propertyCategorySearchList.get(searchDatabaseSizeAfter - 1);

                assertPropertyCategoryAllPropertiesEquals(testPropertyCategorySearch, updatedPropertyCategory);
            });
    }

    @Test
    @Transactional
    void putNonExistingPropertyCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyCategorySearchRepository.findAll());
        propertyCategory.setId(longCount.incrementAndGet());

        // Create the PropertyCategory
        PropertyCategoryDTO propertyCategoryDTO = propertyCategoryMapper.toDto(propertyCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPropertyCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, propertyCategoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(propertyCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PropertyCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchPropertyCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyCategorySearchRepository.findAll());
        propertyCategory.setId(longCount.incrementAndGet());

        // Create the PropertyCategory
        PropertyCategoryDTO propertyCategoryDTO = propertyCategoryMapper.toDto(propertyCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPropertyCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(propertyCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PropertyCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPropertyCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyCategorySearchRepository.findAll());
        propertyCategory.setId(longCount.incrementAndGet());

        // Create the PropertyCategory
        PropertyCategoryDTO propertyCategoryDTO = propertyCategoryMapper.toDto(propertyCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPropertyCategoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(propertyCategoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PropertyCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdatePropertyCategoryWithPatch() throws Exception {
        // Initialize the database
        insertedPropertyCategory = propertyCategoryRepository.saveAndFlush(propertyCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the propertyCategory using partial update
        PropertyCategory partialUpdatedPropertyCategory = new PropertyCategory();
        partialUpdatedPropertyCategory.setId(propertyCategory.getId());

        partialUpdatedPropertyCategory.description(UPDATED_DESCRIPTION);

        restPropertyCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPropertyCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPropertyCategory))
            )
            .andExpect(status().isOk());

        // Validate the PropertyCategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPropertyCategoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPropertyCategory, propertyCategory),
            getPersistedPropertyCategory(propertyCategory)
        );
    }

    @Test
    @Transactional
    void fullUpdatePropertyCategoryWithPatch() throws Exception {
        // Initialize the database
        insertedPropertyCategory = propertyCategoryRepository.saveAndFlush(propertyCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the propertyCategory using partial update
        PropertyCategory partialUpdatedPropertyCategory = new PropertyCategory();
        partialUpdatedPropertyCategory.setId(propertyCategory.getId());

        partialUpdatedPropertyCategory.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restPropertyCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPropertyCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPropertyCategory))
            )
            .andExpect(status().isOk());

        // Validate the PropertyCategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPropertyCategoryUpdatableFieldsEquals(
            partialUpdatedPropertyCategory,
            getPersistedPropertyCategory(partialUpdatedPropertyCategory)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPropertyCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyCategorySearchRepository.findAll());
        propertyCategory.setId(longCount.incrementAndGet());

        // Create the PropertyCategory
        PropertyCategoryDTO propertyCategoryDTO = propertyCategoryMapper.toDto(propertyCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPropertyCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, propertyCategoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(propertyCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PropertyCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPropertyCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyCategorySearchRepository.findAll());
        propertyCategory.setId(longCount.incrementAndGet());

        // Create the PropertyCategory
        PropertyCategoryDTO propertyCategoryDTO = propertyCategoryMapper.toDto(propertyCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPropertyCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(propertyCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PropertyCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPropertyCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyCategorySearchRepository.findAll());
        propertyCategory.setId(longCount.incrementAndGet());

        // Create the PropertyCategory
        PropertyCategoryDTO propertyCategoryDTO = propertyCategoryMapper.toDto(propertyCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPropertyCategoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(propertyCategoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PropertyCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deletePropertyCategory() throws Exception {
        // Initialize the database
        insertedPropertyCategory = propertyCategoryRepository.saveAndFlush(propertyCategory);
        propertyCategoryRepository.save(propertyCategory);
        propertyCategorySearchRepository.save(propertyCategory);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the propertyCategory
        restPropertyCategoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, propertyCategory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchPropertyCategory() throws Exception {
        // Initialize the database
        insertedPropertyCategory = propertyCategoryRepository.saveAndFlush(propertyCategory);
        propertyCategorySearchRepository.save(propertyCategory);

        // Search the propertyCategory
        restPropertyCategoryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + propertyCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(propertyCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    protected long getRepositoryCount() {
        return propertyCategoryRepository.count();
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

    protected PropertyCategory getPersistedPropertyCategory(PropertyCategory propertyCategory) {
        return propertyCategoryRepository.findById(propertyCategory.getId()).orElseThrow();
    }

    protected void assertPersistedPropertyCategoryToMatchAllProperties(PropertyCategory expectedPropertyCategory) {
        assertPropertyCategoryAllPropertiesEquals(expectedPropertyCategory, getPersistedPropertyCategory(expectedPropertyCategory));
    }

    protected void assertPersistedPropertyCategoryToMatchUpdatableProperties(PropertyCategory expectedPropertyCategory) {
        assertPropertyCategoryAllUpdatablePropertiesEquals(
            expectedPropertyCategory,
            getPersistedPropertyCategory(expectedPropertyCategory)
        );
    }
}
