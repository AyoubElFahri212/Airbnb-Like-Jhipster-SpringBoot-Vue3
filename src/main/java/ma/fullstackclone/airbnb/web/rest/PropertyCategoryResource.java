package ma.fullstackclone.airbnb.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import ma.fullstackclone.airbnb.repository.PropertyCategoryRepository;
import ma.fullstackclone.airbnb.service.PropertyCategoryQueryService;
import ma.fullstackclone.airbnb.service.PropertyCategoryService;
import ma.fullstackclone.airbnb.service.criteria.PropertyCategoryCriteria;
import ma.fullstackclone.airbnb.service.dto.PropertyCategoryDTO;
import ma.fullstackclone.airbnb.web.rest.errors.BadRequestAlertException;
import ma.fullstackclone.airbnb.web.rest.errors.ElasticsearchExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ma.fullstackclone.airbnb.domain.PropertyCategory}.
 */
@RestController
@RequestMapping("/api/property-categories")
public class PropertyCategoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyCategoryResource.class);

    private static final String ENTITY_NAME = "propertyCategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PropertyCategoryService propertyCategoryService;

    private final PropertyCategoryRepository propertyCategoryRepository;

    private final PropertyCategoryQueryService propertyCategoryQueryService;

    public PropertyCategoryResource(
        PropertyCategoryService propertyCategoryService,
        PropertyCategoryRepository propertyCategoryRepository,
        PropertyCategoryQueryService propertyCategoryQueryService
    ) {
        this.propertyCategoryService = propertyCategoryService;
        this.propertyCategoryRepository = propertyCategoryRepository;
        this.propertyCategoryQueryService = propertyCategoryQueryService;
    }

    /**
     * {@code POST  /property-categories} : Create a new propertyCategory.
     *
     * @param propertyCategoryDTO the propertyCategoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new propertyCategoryDTO, or with status {@code 400 (Bad Request)} if the propertyCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PropertyCategoryDTO> createPropertyCategory(@Valid @RequestBody PropertyCategoryDTO propertyCategoryDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PropertyCategory : {}", propertyCategoryDTO);
        if (propertyCategoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new propertyCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        propertyCategoryDTO = propertyCategoryService.save(propertyCategoryDTO);
        return ResponseEntity.created(new URI("/api/property-categories/" + propertyCategoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, propertyCategoryDTO.getId().toString()))
            .body(propertyCategoryDTO);
    }

    /**
     * {@code PUT  /property-categories/:id} : Updates an existing propertyCategory.
     *
     * @param id the id of the propertyCategoryDTO to save.
     * @param propertyCategoryDTO the propertyCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated propertyCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the propertyCategoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the propertyCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PropertyCategoryDTO> updatePropertyCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PropertyCategoryDTO propertyCategoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PropertyCategory : {}, {}", id, propertyCategoryDTO);
        if (propertyCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, propertyCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!propertyCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        propertyCategoryDTO = propertyCategoryService.update(propertyCategoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, propertyCategoryDTO.getId().toString()))
            .body(propertyCategoryDTO);
    }

    /**
     * {@code PATCH  /property-categories/:id} : Partial updates given fields of an existing propertyCategory, field will ignore if it is null
     *
     * @param id the id of the propertyCategoryDTO to save.
     * @param propertyCategoryDTO the propertyCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated propertyCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the propertyCategoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the propertyCategoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the propertyCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PropertyCategoryDTO> partialUpdatePropertyCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PropertyCategoryDTO propertyCategoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PropertyCategory partially : {}, {}", id, propertyCategoryDTO);
        if (propertyCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, propertyCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!propertyCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PropertyCategoryDTO> result = propertyCategoryService.partialUpdate(propertyCategoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, propertyCategoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /property-categories} : get all the propertyCategories.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of propertyCategories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PropertyCategoryDTO>> getAllPropertyCategories(
        PropertyCategoryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get PropertyCategories by criteria: {}", criteria);

        Page<PropertyCategoryDTO> page = propertyCategoryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /property-categories/count} : count all the propertyCategories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPropertyCategories(PropertyCategoryCriteria criteria) {
        LOG.debug("REST request to count PropertyCategories by criteria: {}", criteria);
        return ResponseEntity.ok().body(propertyCategoryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /property-categories/:id} : get the "id" propertyCategory.
     *
     * @param id the id of the propertyCategoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the propertyCategoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PropertyCategoryDTO> getPropertyCategory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PropertyCategory : {}", id);
        Optional<PropertyCategoryDTO> propertyCategoryDTO = propertyCategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(propertyCategoryDTO);
    }

    /**
     * {@code DELETE  /property-categories/:id} : delete the "id" propertyCategory.
     *
     * @param id the id of the propertyCategoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePropertyCategory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PropertyCategory : {}", id);
        propertyCategoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /property-categories/_search?query=:query} : search for the propertyCategory corresponding
     * to the query.
     *
     * @param query the query of the propertyCategory search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<PropertyCategoryDTO>> searchPropertyCategories(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of PropertyCategories for query {}", query);
        try {
            Page<PropertyCategoryDTO> page = propertyCategoryService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
