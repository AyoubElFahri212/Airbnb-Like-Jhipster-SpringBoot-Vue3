package ma.fullstackclone.airbnb.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import ma.fullstackclone.airbnb.repository.AmenityRepository;
import ma.fullstackclone.airbnb.service.AmenityQueryService;
import ma.fullstackclone.airbnb.service.AmenityService;
import ma.fullstackclone.airbnb.service.criteria.AmenityCriteria;
import ma.fullstackclone.airbnb.service.dto.AmenityDTO;
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
 * REST controller for managing {@link ma.fullstackclone.airbnb.domain.Amenity}.
 */
@RestController
@RequestMapping("/api/amenities")
public class AmenityResource {

    private static final Logger LOG = LoggerFactory.getLogger(AmenityResource.class);

    private static final String ENTITY_NAME = "amenity";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AmenityService amenityService;

    private final AmenityRepository amenityRepository;

    private final AmenityQueryService amenityQueryService;

    public AmenityResource(AmenityService amenityService, AmenityRepository amenityRepository, AmenityQueryService amenityQueryService) {
        this.amenityService = amenityService;
        this.amenityRepository = amenityRepository;
        this.amenityQueryService = amenityQueryService;
    }

    /**
     * {@code POST  /amenities} : Create a new amenity.
     *
     * @param amenityDTO the amenityDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new amenityDTO, or with status {@code 400 (Bad Request)} if the amenity has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AmenityDTO> createAmenity(@Valid @RequestBody AmenityDTO amenityDTO) throws URISyntaxException {
        LOG.debug("REST request to save Amenity : {}", amenityDTO);
        if (amenityDTO.getId() != null) {
            throw new BadRequestAlertException("A new amenity cannot already have an ID", ENTITY_NAME, "idexists");
        }
        amenityDTO = amenityService.save(amenityDTO);
        return ResponseEntity.created(new URI("/api/amenities/" + amenityDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, amenityDTO.getId().toString()))
            .body(amenityDTO);
    }

    /**
     * {@code PUT  /amenities/:id} : Updates an existing amenity.
     *
     * @param id the id of the amenityDTO to save.
     * @param amenityDTO the amenityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated amenityDTO,
     * or with status {@code 400 (Bad Request)} if the amenityDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the amenityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AmenityDTO> updateAmenity(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AmenityDTO amenityDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Amenity : {}, {}", id, amenityDTO);
        if (amenityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, amenityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!amenityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        amenityDTO = amenityService.update(amenityDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, amenityDTO.getId().toString()))
            .body(amenityDTO);
    }

    /**
     * {@code PATCH  /amenities/:id} : Partial updates given fields of an existing amenity, field will ignore if it is null
     *
     * @param id the id of the amenityDTO to save.
     * @param amenityDTO the amenityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated amenityDTO,
     * or with status {@code 400 (Bad Request)} if the amenityDTO is not valid,
     * or with status {@code 404 (Not Found)} if the amenityDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the amenityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AmenityDTO> partialUpdateAmenity(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AmenityDTO amenityDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Amenity partially : {}, {}", id, amenityDTO);
        if (amenityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, amenityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!amenityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AmenityDTO> result = amenityService.partialUpdate(amenityDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, amenityDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /amenities} : get all the amenities.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of amenities in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AmenityDTO>> getAllAmenities(
        AmenityCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Amenities by criteria: {}", criteria);

        Page<AmenityDTO> page = amenityQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /amenities/count} : count all the amenities.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAmenities(AmenityCriteria criteria) {
        LOG.debug("REST request to count Amenities by criteria: {}", criteria);
        return ResponseEntity.ok().body(amenityQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /amenities/:id} : get the "id" amenity.
     *
     * @param id the id of the amenityDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the amenityDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AmenityDTO> getAmenity(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Amenity : {}", id);
        Optional<AmenityDTO> amenityDTO = amenityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(amenityDTO);
    }

    /**
     * {@code DELETE  /amenities/:id} : delete the "id" amenity.
     *
     * @param id the id of the amenityDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAmenity(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Amenity : {}", id);
        amenityService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /amenities/_search?query=:query} : search for the amenity corresponding
     * to the query.
     *
     * @param query the query of the amenity search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<AmenityDTO>> searchAmenities(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Amenities for query {}", query);
        try {
            Page<AmenityDTO> page = amenityService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
