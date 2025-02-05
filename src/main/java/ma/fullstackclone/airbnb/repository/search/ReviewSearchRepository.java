package ma.fullstackclone.airbnb.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import java.util.List;
import ma.fullstackclone.airbnb.domain.Review;
import ma.fullstackclone.airbnb.repository.ReviewRepository;
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
 * Spring Data Elasticsearch repository for the {@link Review} entity.
 */
public interface ReviewSearchRepository extends ElasticsearchRepository<Review, Long>, ReviewSearchRepositoryInternal {}

interface ReviewSearchRepositoryInternal {
    Page<Review> search(String query, Pageable pageable);

    Page<Review> search(Query query);

    @Async
    void index(Review entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ReviewSearchRepositoryInternalImpl implements ReviewSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ReviewRepository repository;

    ReviewSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ReviewRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Review> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Review> search(Query query) {
        SearchHits<Review> searchHits = elasticsearchTemplate.search(query, Review.class);
        List<Review> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Review entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Review.class);
    }
}
