package ma.fullstackclone.airbnb.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import ma.fullstackclone.airbnb.repository.PropertyImageRepository;
import ma.fullstackclone.airbnb.service.PropertyImageQueryService;
import ma.fullstackclone.airbnb.service.PropertyImageService;
import ma.fullstackclone.airbnb.service.criteria.PropertyImageCriteria;
import ma.fullstackclone.airbnb.service.dto.PropertyImageDTO;
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
 * REST controller for managing {@link ma.fullstackclone.airbnb.domain.PropertyImage}.
 */
@RestController
@RequestMapping("/api/property-images")
public class PropertyImageResource {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyImageResource.class);

    private static final String ENTITY_NAME = "propertyImage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PropertyImageService propertyImageService;

    private final PropertyImageRepository propertyImageRepository;

    private final PropertyImageQueryService propertyImageQueryService;

    public PropertyImageResource(
        PropertyImageService propertyImageService,
        PropertyImageRepository propertyImageRepository,
        PropertyImageQueryService propertyImageQueryService
    ) {
        this.propertyImageService = propertyImageService;
        this.propertyImageRepository = propertyImageRepository;
        this.propertyImageQueryService = propertyImageQueryService;
    }

    /**
     * {@code POST  /property-images} : Create a new propertyImage.
     *
     * @param propertyImageDTO the propertyImageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new propertyImageDTO, or with status {@code 400 (Bad Request)} if the propertyImage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PropertyImageDTO> createPropertyImage(@Valid @RequestBody PropertyImageDTO propertyImageDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PropertyImage : {}", propertyImageDTO);
        if (propertyImageDTO.getId() != null) {
            throw new BadRequestAlertException("A new propertyImage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        propertyImageDTO = propertyImageService.save(propertyImageDTO);
        return ResponseEntity.created(new URI("/api/property-images/" + propertyImageDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, propertyImageDTO.getId().toString()))
            .body(propertyImageDTO);
    }

    /**
     * {@code PUT  /property-images/:id} : Updates an existing propertyImage.
     *
     * @param id the id of the propertyImageDTO to save.
     * @param propertyImageDTO the propertyImageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated propertyImageDTO,
     * or with status {@code 400 (Bad Request)} if the propertyImageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the propertyImageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PropertyImageDTO> updatePropertyImage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PropertyImageDTO propertyImageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PropertyImage : {}, {}", id, propertyImageDTO);
        if (propertyImageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, propertyImageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!propertyImageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        propertyImageDTO = propertyImageService.update(propertyImageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, propertyImageDTO.getId().toString()))
            .body(propertyImageDTO);
    }

    /**
     * {@code PATCH  /property-images/:id} : Partial updates given fields of an existing propertyImage, field will ignore if it is null
     *
     * @param id the id of the propertyImageDTO to save.
     * @param propertyImageDTO the propertyImageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated propertyImageDTO,
     * or with status {@code 400 (Bad Request)} if the propertyImageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the propertyImageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the propertyImageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PropertyImageDTO> partialUpdatePropertyImage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PropertyImageDTO propertyImageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PropertyImage partially : {}, {}", id, propertyImageDTO);
        if (propertyImageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, propertyImageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!propertyImageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PropertyImageDTO> result = propertyImageService.partialUpdate(propertyImageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, propertyImageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /property-images} : get all the propertyImages.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of propertyImages in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PropertyImageDTO>> getAllPropertyImages(
        PropertyImageCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get PropertyImages by criteria: {}", criteria);

        Page<PropertyImageDTO> page = propertyImageQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /property-images/count} : count all the propertyImages.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPropertyImages(PropertyImageCriteria criteria) {
        LOG.debug("REST request to count PropertyImages by criteria: {}", criteria);
        return ResponseEntity.ok().body(propertyImageQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /property-images/:id} : get the "id" propertyImage.
     *
     * @param id the id of the propertyImageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the propertyImageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PropertyImageDTO> getPropertyImage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PropertyImage : {}", id);
        Optional<PropertyImageDTO> propertyImageDTO = propertyImageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(propertyImageDTO);
    }

    /**
     * {@code DELETE  /property-images/:id} : delete the "id" propertyImage.
     *
     * @param id the id of the propertyImageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePropertyImage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PropertyImage : {}", id);
        propertyImageService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /property-images/_search?query=:query} : search for the propertyImage corresponding
     * to the query.
     *
     * @param query the query of the propertyImage search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<PropertyImageDTO>> searchPropertyImages(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of PropertyImages for query {}", query);
        try {
            Page<PropertyImageDTO> page = propertyImageService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
