package ma.fullstackclone.airbnb.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import ma.fullstackclone.airbnb.repository.PropertyRepository;
import ma.fullstackclone.airbnb.service.PropertyQueryService;
import ma.fullstackclone.airbnb.service.PropertyService;
import ma.fullstackclone.airbnb.service.criteria.PropertyCriteria;
import ma.fullstackclone.airbnb.service.dto.PropertyDTO;
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
 * REST controller for managing {@link ma.fullstackclone.airbnb.domain.Property}.
 */
@RestController
@RequestMapping("/api/properties")
public class PropertyResource {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyResource.class);

    private static final String ENTITY_NAME = "property";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PropertyService propertyService;

    private final PropertyRepository propertyRepository;

    private final PropertyQueryService propertyQueryService;

    public PropertyResource(
        PropertyService propertyService,
        PropertyRepository propertyRepository,
        PropertyQueryService propertyQueryService
    ) {
        this.propertyService = propertyService;
        this.propertyRepository = propertyRepository;
        this.propertyQueryService = propertyQueryService;
    }

    /**
     * {@code POST  /properties} : Create a new property.
     *
     * @param propertyDTO the propertyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new propertyDTO, or with status {@code 400 (Bad Request)} if the property has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PropertyDTO> createProperty(@Valid @RequestBody PropertyDTO propertyDTO) throws URISyntaxException {
        LOG.debug("REST request to save Property : {}", propertyDTO);
        if (propertyDTO.getId() != null) {
            throw new BadRequestAlertException("A new property cannot already have an ID", ENTITY_NAME, "idexists");
        }
        propertyDTO = propertyService.save(propertyDTO);
        return ResponseEntity.created(new URI("/api/properties/" + propertyDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, propertyDTO.getId().toString()))
            .body(propertyDTO);
    }

    /**
     * {@code PUT  /properties/:id} : Updates an existing property.
     *
     * @param id the id of the propertyDTO to save.
     * @param propertyDTO the propertyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated propertyDTO,
     * or with status {@code 400 (Bad Request)} if the propertyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the propertyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PropertyDTO> updateProperty(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PropertyDTO propertyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Property : {}, {}", id, propertyDTO);
        if (propertyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, propertyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!propertyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        propertyDTO = propertyService.update(propertyDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, propertyDTO.getId().toString()))
            .body(propertyDTO);
    }

    /**
     * {@code PATCH  /properties/:id} : Partial updates given fields of an existing property, field will ignore if it is null
     *
     * @param id the id of the propertyDTO to save.
     * @param propertyDTO the propertyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated propertyDTO,
     * or with status {@code 400 (Bad Request)} if the propertyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the propertyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the propertyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PropertyDTO> partialUpdateProperty(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PropertyDTO propertyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Property partially : {}, {}", id, propertyDTO);
        if (propertyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, propertyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!propertyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PropertyDTO> result = propertyService.partialUpdate(propertyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, propertyDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /properties} : get all the properties.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of properties in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PropertyDTO>> getAllProperties(
        PropertyCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Properties by criteria: {}", criteria);

        Page<PropertyDTO> page = propertyQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /properties/count} : count all the properties.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countProperties(PropertyCriteria criteria) {
        LOG.debug("REST request to count Properties by criteria: {}", criteria);
        return ResponseEntity.ok().body(propertyQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /properties/:id} : get the "id" property.
     *
     * @param id the id of the propertyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the propertyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PropertyDTO> getProperty(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Property : {}", id);
        Optional<PropertyDTO> propertyDTO = propertyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(propertyDTO);
    }

    /**
     * {@code DELETE  /properties/:id} : delete the "id" property.
     *
     * @param id the id of the propertyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Property : {}", id);
        propertyService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /properties/_search?query=:query} : search for the property corresponding
     * to the query.
     *
     * @param query the query of the property search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<PropertyDTO>> searchProperties(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Properties for query {}", query);
        try {
            Page<PropertyDTO> page = propertyService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
