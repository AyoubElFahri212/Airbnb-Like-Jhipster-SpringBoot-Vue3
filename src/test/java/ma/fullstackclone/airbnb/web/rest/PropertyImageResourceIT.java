package ma.fullstackclone.airbnb.web.rest;

import static ma.fullstackclone.airbnb.domain.PropertyImageAsserts.*;
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
import ma.fullstackclone.airbnb.domain.PropertyImage;
import ma.fullstackclone.airbnb.repository.PropertyImageRepository;
import ma.fullstackclone.airbnb.repository.search.PropertyImageSearchRepository;
import ma.fullstackclone.airbnb.service.dto.PropertyImageDTO;
import ma.fullstackclone.airbnb.service.mapper.PropertyImageMapper;
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
 * Integration tests for the {@link PropertyImageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PropertyImageResourceIT {

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_MAIN = false;
    private static final Boolean UPDATED_IS_MAIN = true;

    private static final String DEFAULT_CAPTION = "AAAAAAAAAA";
    private static final String UPDATED_CAPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/property-images";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/property-images/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PropertyImageRepository propertyImageRepository;

    @Autowired
    private PropertyImageMapper propertyImageMapper;

    @Autowired
    private PropertyImageSearchRepository propertyImageSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPropertyImageMockMvc;

    private PropertyImage propertyImage;

    private PropertyImage insertedPropertyImage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PropertyImage createEntity() {
        return new PropertyImage().imageUrl(DEFAULT_IMAGE_URL).isMain(DEFAULT_IS_MAIN).caption(DEFAULT_CAPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PropertyImage createUpdatedEntity() {
        return new PropertyImage().imageUrl(UPDATED_IMAGE_URL).isMain(UPDATED_IS_MAIN).caption(UPDATED_CAPTION);
    }

    @BeforeEach
    public void initTest() {
        propertyImage = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedPropertyImage != null) {
            propertyImageRepository.delete(insertedPropertyImage);
            propertyImageSearchRepository.delete(insertedPropertyImage);
            insertedPropertyImage = null;
        }
    }

    @Test
    @Transactional
    void createPropertyImage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());
        // Create the PropertyImage
        PropertyImageDTO propertyImageDTO = propertyImageMapper.toDto(propertyImage);
        var returnedPropertyImageDTO = om.readValue(
            restPropertyImageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(propertyImageDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PropertyImageDTO.class
        );

        // Validate the PropertyImage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPropertyImage = propertyImageMapper.toEntity(returnedPropertyImageDTO);
        assertPropertyImageUpdatableFieldsEquals(returnedPropertyImage, getPersistedPropertyImage(returnedPropertyImage));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedPropertyImage = returnedPropertyImage;
    }

    @Test
    @Transactional
    void createPropertyImageWithExistingId() throws Exception {
        // Create the PropertyImage with an existing ID
        propertyImage.setId(1L);
        PropertyImageDTO propertyImageDTO = propertyImageMapper.toDto(propertyImage);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restPropertyImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(propertyImageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PropertyImage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkImageUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());
        // set the field null
        propertyImage.setImageUrl(null);

        // Create the PropertyImage, which fails.
        PropertyImageDTO propertyImageDTO = propertyImageMapper.toDto(propertyImage);

        restPropertyImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(propertyImageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIsMainIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());
        // set the field null
        propertyImage.setIsMain(null);

        // Create the PropertyImage, which fails.
        PropertyImageDTO propertyImageDTO = propertyImageMapper.toDto(propertyImage);

        restPropertyImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(propertyImageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllPropertyImages() throws Exception {
        // Initialize the database
        insertedPropertyImage = propertyImageRepository.saveAndFlush(propertyImage);

        // Get all the propertyImageList
        restPropertyImageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(propertyImage.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].isMain").value(hasItem(DEFAULT_IS_MAIN.booleanValue())))
            .andExpect(jsonPath("$.[*].caption").value(hasItem(DEFAULT_CAPTION)));
    }

    @Test
    @Transactional
    void getPropertyImage() throws Exception {
        // Initialize the database
        insertedPropertyImage = propertyImageRepository.saveAndFlush(propertyImage);

        // Get the propertyImage
        restPropertyImageMockMvc
            .perform(get(ENTITY_API_URL_ID, propertyImage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(propertyImage.getId().intValue()))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL))
            .andExpect(jsonPath("$.isMain").value(DEFAULT_IS_MAIN.booleanValue()))
            .andExpect(jsonPath("$.caption").value(DEFAULT_CAPTION));
    }

    @Test
    @Transactional
    void getPropertyImagesByIdFiltering() throws Exception {
        // Initialize the database
        insertedPropertyImage = propertyImageRepository.saveAndFlush(propertyImage);

        Long id = propertyImage.getId();

        defaultPropertyImageFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPropertyImageFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPropertyImageFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPropertyImagesByImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPropertyImage = propertyImageRepository.saveAndFlush(propertyImage);

        // Get all the propertyImageList where imageUrl equals to
        defaultPropertyImageFiltering("imageUrl.equals=" + DEFAULT_IMAGE_URL, "imageUrl.equals=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllPropertyImagesByImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPropertyImage = propertyImageRepository.saveAndFlush(propertyImage);

        // Get all the propertyImageList where imageUrl in
        defaultPropertyImageFiltering("imageUrl.in=" + DEFAULT_IMAGE_URL + "," + UPDATED_IMAGE_URL, "imageUrl.in=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllPropertyImagesByImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPropertyImage = propertyImageRepository.saveAndFlush(propertyImage);

        // Get all the propertyImageList where imageUrl is not null
        defaultPropertyImageFiltering("imageUrl.specified=true", "imageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllPropertyImagesByImageUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedPropertyImage = propertyImageRepository.saveAndFlush(propertyImage);

        // Get all the propertyImageList where imageUrl contains
        defaultPropertyImageFiltering("imageUrl.contains=" + DEFAULT_IMAGE_URL, "imageUrl.contains=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllPropertyImagesByImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPropertyImage = propertyImageRepository.saveAndFlush(propertyImage);

        // Get all the propertyImageList where imageUrl does not contain
        defaultPropertyImageFiltering("imageUrl.doesNotContain=" + UPDATED_IMAGE_URL, "imageUrl.doesNotContain=" + DEFAULT_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllPropertyImagesByIsMainIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPropertyImage = propertyImageRepository.saveAndFlush(propertyImage);

        // Get all the propertyImageList where isMain equals to
        defaultPropertyImageFiltering("isMain.equals=" + DEFAULT_IS_MAIN, "isMain.equals=" + UPDATED_IS_MAIN);
    }

    @Test
    @Transactional
    void getAllPropertyImagesByIsMainIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPropertyImage = propertyImageRepository.saveAndFlush(propertyImage);

        // Get all the propertyImageList where isMain in
        defaultPropertyImageFiltering("isMain.in=" + DEFAULT_IS_MAIN + "," + UPDATED_IS_MAIN, "isMain.in=" + UPDATED_IS_MAIN);
    }

    @Test
    @Transactional
    void getAllPropertyImagesByIsMainIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPropertyImage = propertyImageRepository.saveAndFlush(propertyImage);

        // Get all the propertyImageList where isMain is not null
        defaultPropertyImageFiltering("isMain.specified=true", "isMain.specified=false");
    }

    @Test
    @Transactional
    void getAllPropertyImagesByCaptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPropertyImage = propertyImageRepository.saveAndFlush(propertyImage);

        // Get all the propertyImageList where caption equals to
        defaultPropertyImageFiltering("caption.equals=" + DEFAULT_CAPTION, "caption.equals=" + UPDATED_CAPTION);
    }

    @Test
    @Transactional
    void getAllPropertyImagesByCaptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPropertyImage = propertyImageRepository.saveAndFlush(propertyImage);

        // Get all the propertyImageList where caption in
        defaultPropertyImageFiltering("caption.in=" + DEFAULT_CAPTION + "," + UPDATED_CAPTION, "caption.in=" + UPDATED_CAPTION);
    }

    @Test
    @Transactional
    void getAllPropertyImagesByCaptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPropertyImage = propertyImageRepository.saveAndFlush(propertyImage);

        // Get all the propertyImageList where caption is not null
        defaultPropertyImageFiltering("caption.specified=true", "caption.specified=false");
    }

    @Test
    @Transactional
    void getAllPropertyImagesByCaptionContainsSomething() throws Exception {
        // Initialize the database
        insertedPropertyImage = propertyImageRepository.saveAndFlush(propertyImage);

        // Get all the propertyImageList where caption contains
        defaultPropertyImageFiltering("caption.contains=" + DEFAULT_CAPTION, "caption.contains=" + UPDATED_CAPTION);
    }

    @Test
    @Transactional
    void getAllPropertyImagesByCaptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPropertyImage = propertyImageRepository.saveAndFlush(propertyImage);

        // Get all the propertyImageList where caption does not contain
        defaultPropertyImageFiltering("caption.doesNotContain=" + UPDATED_CAPTION, "caption.doesNotContain=" + DEFAULT_CAPTION);
    }

    @Test
    @Transactional
    void getAllPropertyImagesByPropertyIsEqualToSomething() throws Exception {
        Property property;
        if (TestUtil.findAll(em, Property.class).isEmpty()) {
            propertyImageRepository.saveAndFlush(propertyImage);
            property = PropertyResourceIT.createEntity();
        } else {
            property = TestUtil.findAll(em, Property.class).get(0);
        }
        em.persist(property);
        em.flush();
        propertyImage.setProperty(property);
        propertyImageRepository.saveAndFlush(propertyImage);
        Long propertyId = property.getId();
        // Get all the propertyImageList where property equals to propertyId
        defaultPropertyImageShouldBeFound("propertyId.equals=" + propertyId);

        // Get all the propertyImageList where property equals to (propertyId + 1)
        defaultPropertyImageShouldNotBeFound("propertyId.equals=" + (propertyId + 1));
    }

    private void defaultPropertyImageFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPropertyImageShouldBeFound(shouldBeFound);
        defaultPropertyImageShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPropertyImageShouldBeFound(String filter) throws Exception {
        restPropertyImageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(propertyImage.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].isMain").value(hasItem(DEFAULT_IS_MAIN.booleanValue())))
            .andExpect(jsonPath("$.[*].caption").value(hasItem(DEFAULT_CAPTION)));

        // Check, that the count call also returns 1
        restPropertyImageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPropertyImageShouldNotBeFound(String filter) throws Exception {
        restPropertyImageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPropertyImageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPropertyImage() throws Exception {
        // Get the propertyImage
        restPropertyImageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPropertyImage() throws Exception {
        // Initialize the database
        insertedPropertyImage = propertyImageRepository.saveAndFlush(propertyImage);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        propertyImageSearchRepository.save(propertyImage);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());

        // Update the propertyImage
        PropertyImage updatedPropertyImage = propertyImageRepository.findById(propertyImage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPropertyImage are not directly saved in db
        em.detach(updatedPropertyImage);
        updatedPropertyImage.imageUrl(UPDATED_IMAGE_URL).isMain(UPDATED_IS_MAIN).caption(UPDATED_CAPTION);
        PropertyImageDTO propertyImageDTO = propertyImageMapper.toDto(updatedPropertyImage);

        restPropertyImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, propertyImageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(propertyImageDTO))
            )
            .andExpect(status().isOk());

        // Validate the PropertyImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPropertyImageToMatchAllProperties(updatedPropertyImage);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<PropertyImage> propertyImageSearchList = Streamable.of(propertyImageSearchRepository.findAll()).toList();
                PropertyImage testPropertyImageSearch = propertyImageSearchList.get(searchDatabaseSizeAfter - 1);

                assertPropertyImageAllPropertiesEquals(testPropertyImageSearch, updatedPropertyImage);
            });
    }

    @Test
    @Transactional
    void putNonExistingPropertyImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());
        propertyImage.setId(longCount.incrementAndGet());

        // Create the PropertyImage
        PropertyImageDTO propertyImageDTO = propertyImageMapper.toDto(propertyImage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPropertyImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, propertyImageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(propertyImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PropertyImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchPropertyImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());
        propertyImage.setId(longCount.incrementAndGet());

        // Create the PropertyImage
        PropertyImageDTO propertyImageDTO = propertyImageMapper.toDto(propertyImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPropertyImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(propertyImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PropertyImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPropertyImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());
        propertyImage.setId(longCount.incrementAndGet());

        // Create the PropertyImage
        PropertyImageDTO propertyImageDTO = propertyImageMapper.toDto(propertyImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPropertyImageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(propertyImageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PropertyImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdatePropertyImageWithPatch() throws Exception {
        // Initialize the database
        insertedPropertyImage = propertyImageRepository.saveAndFlush(propertyImage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the propertyImage using partial update
        PropertyImage partialUpdatedPropertyImage = new PropertyImage();
        partialUpdatedPropertyImage.setId(propertyImage.getId());

        restPropertyImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPropertyImage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPropertyImage))
            )
            .andExpect(status().isOk());

        // Validate the PropertyImage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPropertyImageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPropertyImage, propertyImage),
            getPersistedPropertyImage(propertyImage)
        );
    }

    @Test
    @Transactional
    void fullUpdatePropertyImageWithPatch() throws Exception {
        // Initialize the database
        insertedPropertyImage = propertyImageRepository.saveAndFlush(propertyImage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the propertyImage using partial update
        PropertyImage partialUpdatedPropertyImage = new PropertyImage();
        partialUpdatedPropertyImage.setId(propertyImage.getId());

        partialUpdatedPropertyImage.imageUrl(UPDATED_IMAGE_URL).isMain(UPDATED_IS_MAIN).caption(UPDATED_CAPTION);

        restPropertyImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPropertyImage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPropertyImage))
            )
            .andExpect(status().isOk());

        // Validate the PropertyImage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPropertyImageUpdatableFieldsEquals(partialUpdatedPropertyImage, getPersistedPropertyImage(partialUpdatedPropertyImage));
    }

    @Test
    @Transactional
    void patchNonExistingPropertyImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());
        propertyImage.setId(longCount.incrementAndGet());

        // Create the PropertyImage
        PropertyImageDTO propertyImageDTO = propertyImageMapper.toDto(propertyImage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPropertyImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, propertyImageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(propertyImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PropertyImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPropertyImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());
        propertyImage.setId(longCount.incrementAndGet());

        // Create the PropertyImage
        PropertyImageDTO propertyImageDTO = propertyImageMapper.toDto(propertyImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPropertyImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(propertyImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PropertyImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPropertyImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());
        propertyImage.setId(longCount.incrementAndGet());

        // Create the PropertyImage
        PropertyImageDTO propertyImageDTO = propertyImageMapper.toDto(propertyImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPropertyImageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(propertyImageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PropertyImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deletePropertyImage() throws Exception {
        // Initialize the database
        insertedPropertyImage = propertyImageRepository.saveAndFlush(propertyImage);
        propertyImageRepository.save(propertyImage);
        propertyImageSearchRepository.save(propertyImage);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the propertyImage
        restPropertyImageMockMvc
            .perform(delete(ENTITY_API_URL_ID, propertyImage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(propertyImageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchPropertyImage() throws Exception {
        // Initialize the database
        insertedPropertyImage = propertyImageRepository.saveAndFlush(propertyImage);
        propertyImageSearchRepository.save(propertyImage);

        // Search the propertyImage
        restPropertyImageMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + propertyImage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(propertyImage.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].isMain").value(hasItem(DEFAULT_IS_MAIN.booleanValue())))
            .andExpect(jsonPath("$.[*].caption").value(hasItem(DEFAULT_CAPTION)));
    }

    protected long getRepositoryCount() {
        return propertyImageRepository.count();
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

    protected PropertyImage getPersistedPropertyImage(PropertyImage propertyImage) {
        return propertyImageRepository.findById(propertyImage.getId()).orElseThrow();
    }

    protected void assertPersistedPropertyImageToMatchAllProperties(PropertyImage expectedPropertyImage) {
        assertPropertyImageAllPropertiesEquals(expectedPropertyImage, getPersistedPropertyImage(expectedPropertyImage));
    }

    protected void assertPersistedPropertyImageToMatchUpdatableProperties(PropertyImage expectedPropertyImage) {
        assertPropertyImageAllUpdatablePropertiesEquals(expectedPropertyImage, getPersistedPropertyImage(expectedPropertyImage));
    }
}
