package ma.fullstackclone.airbnb.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import java.util.List;
import ma.fullstackclone.airbnb.domain.Promotion;
import ma.fullstackclone.airbnb.repository.PromotionRepository;
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
 * Spring Data Elasticsearch repository for the {@link Promotion} entity.
 */
public interface PromotionSearchRepository extends ElasticsearchRepository<Promotion, Long>, PromotionSearchRepositoryInternal {}

interface PromotionSearchRepositoryInternal {
    Page<Promotion> search(String query, Pageable pageable);

    Page<Promotion> search(Query query);

    @Async
    void index(Promotion entity);

    @Async
    void deleteFromIndexById(Long id);
}

class PromotionSearchRepositoryInternalImpl implements PromotionSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final PromotionRepository repository;

    PromotionSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, PromotionRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Promotion> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Promotion> search(Query query) {
        SearchHits<Promotion> searchHits = elasticsearchTemplate.search(query, Promotion.class);
        List<Promotion> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Promotion entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Promotion.class);
    }
}
