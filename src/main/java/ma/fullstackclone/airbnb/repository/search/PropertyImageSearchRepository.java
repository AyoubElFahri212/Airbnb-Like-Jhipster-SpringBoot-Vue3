package ma.fullstackclone.airbnb.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import java.util.List;
import ma.fullstackclone.airbnb.domain.PropertyImage;
import ma.fullstackclone.airbnb.repository.PropertyImageRepository;
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
 * Spring Data Elasticsearch repository for the {@link PropertyImage} entity.
 */
public interface PropertyImageSearchRepository
    extends ElasticsearchRepository<PropertyImage, Long>, PropertyImageSearchRepositoryInternal {}

interface PropertyImageSearchRepositoryInternal {
    Page<PropertyImage> search(String query, Pageable pageable);

    Page<PropertyImage> search(Query query);

    @Async
    void index(PropertyImage entity);

    @Async
    void deleteFromIndexById(Long id);
}

class PropertyImageSearchRepositoryInternalImpl implements PropertyImageSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final PropertyImageRepository repository;

    PropertyImageSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, PropertyImageRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<PropertyImage> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<PropertyImage> search(Query query) {
        SearchHits<PropertyImage> searchHits = elasticsearchTemplate.search(query, PropertyImage.class);
        List<PropertyImage> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(PropertyImage entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), PropertyImage.class);
    }
}
