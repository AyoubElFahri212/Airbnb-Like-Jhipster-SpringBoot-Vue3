package ma.fullstackclone.airbnb.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import java.util.List;
import ma.fullstackclone.airbnb.domain.PropertyCategory;
import ma.fullstackclone.airbnb.repository.PropertyCategoryRepository;
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
 * Spring Data Elasticsearch repository for the {@link PropertyCategory} entity.
 */
public interface PropertyCategorySearchRepository
    extends ElasticsearchRepository<PropertyCategory, Long>, PropertyCategorySearchRepositoryInternal {}

interface PropertyCategorySearchRepositoryInternal {
    Page<PropertyCategory> search(String query, Pageable pageable);

    Page<PropertyCategory> search(Query query);

    @Async
    void index(PropertyCategory entity);

    @Async
    void deleteFromIndexById(Long id);
}

class PropertyCategorySearchRepositoryInternalImpl implements PropertyCategorySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final PropertyCategoryRepository repository;

    PropertyCategorySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, PropertyCategoryRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<PropertyCategory> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<PropertyCategory> search(Query query) {
        SearchHits<PropertyCategory> searchHits = elasticsearchTemplate.search(query, PropertyCategory.class);
        List<PropertyCategory> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(PropertyCategory entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), PropertyCategory.class);
    }
}
