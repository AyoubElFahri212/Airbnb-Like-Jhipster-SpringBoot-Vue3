package ma.fullstackclone.airbnb.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import java.util.List;
import ma.fullstackclone.airbnb.domain.Booking;
import ma.fullstackclone.airbnb.repository.BookingRepository;
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
 * Spring Data Elasticsearch repository for the {@link Booking} entity.
 */
public interface BookingSearchRepository extends ElasticsearchRepository<Booking, Long>, BookingSearchRepositoryInternal {}

interface BookingSearchRepositoryInternal {
    Page<Booking> search(String query, Pageable pageable);

    Page<Booking> search(Query query);

    @Async
    void index(Booking entity);

    @Async
    void deleteFromIndexById(Long id);
}

class BookingSearchRepositoryInternalImpl implements BookingSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final BookingRepository repository;

    BookingSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, BookingRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Booking> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Booking> search(Query query) {
        SearchHits<Booking> searchHits = elasticsearchTemplate.search(query, Booking.class);
        List<Booking> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Booking entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Booking.class);
    }
}
