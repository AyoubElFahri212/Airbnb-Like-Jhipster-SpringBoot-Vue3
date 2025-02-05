package ma.fullstackclone.airbnb.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import java.util.List;
import ma.fullstackclone.airbnb.domain.Amenity;
import ma.fullstackclone.airbnb.repository.AmenityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Amenity} entity.
 */
public interface AmenitySearchRepository extends ElasticsearchRepository<Amenity, Long>, AmenitySearchRepositoryInternal {}

interface AmenitySearchRepositoryInternal {
    Page<Amenity> search(String query, Pageable pageable);

    Page<Amenity> search(Query query);

    @Async
    void index(Amenity entity);

    @Async
    void deleteFromIndexById(Long id);
}

class AmenitySearchRepositoryInternalImpl implements AmenitySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final AmenityRepository repository;

    AmenitySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, AmenityRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Amenity> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Amenity> search(Query query) {
        SearchHits<Amenity> searchHits = elasticsearchTemplate.search(query, Amenity.class);
        List<Amenity> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Amenity entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Amenity.class);
    }
}
