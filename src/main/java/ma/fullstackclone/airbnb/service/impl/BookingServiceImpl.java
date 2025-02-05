package ma.fullstackclone.airbnb.service.impl;

import java.util.Optional;
import ma.fullstackclone.airbnb.domain.Booking;
import ma.fullstackclone.airbnb.repository.BookingRepository;
import ma.fullstackclone.airbnb.repository.search.BookingSearchRepository;
import ma.fullstackclone.airbnb.service.BookingService;
import ma.fullstackclone.airbnb.service.dto.BookingDTO;
import ma.fullstackclone.airbnb.service.mapper.BookingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ma.fullstackclone.airbnb.domain.Booking}.
 */
@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private static final Logger LOG = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;

    private final BookingMapper bookingMapper;

    private final BookingSearchRepository bookingSearchRepository;

    public BookingServiceImpl(
        BookingRepository bookingRepository,
        BookingMapper bookingMapper,
        BookingSearchRepository bookingSearchRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.bookingSearchRepository = bookingSearchRepository;
    }

    @Override
    public BookingDTO save(BookingDTO bookingDTO) {
        LOG.debug("Request to save Booking : {}", bookingDTO);
        Booking booking = bookingMapper.toEntity(bookingDTO);
        booking = bookingRepository.save(booking);
        bookingSearchRepository.index(booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDTO update(BookingDTO bookingDTO) {
        LOG.debug("Request to update Booking : {}", bookingDTO);
        Booking booking = bookingMapper.toEntity(bookingDTO);
        booking = bookingRepository.save(booking);
        bookingSearchRepository.index(booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    public Optional<BookingDTO> partialUpdate(BookingDTO bookingDTO) {
        LOG.debug("Request to partially update Booking : {}", bookingDTO);

        return bookingRepository
            .findById(bookingDTO.getId())
            .map(existingBooking -> {
                bookingMapper.partialUpdate(existingBooking, bookingDTO);

                return existingBooking;
            })
            .map(bookingRepository::save)
            .map(savedBooking -> {
                bookingSearchRepository.index(savedBooking);
                return savedBooking;
            })
            .map(bookingMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookingDTO> findOne(Long id) {
        LOG.debug("Request to get Booking : {}", id);
        return bookingRepository.findById(id).map(bookingMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Booking : {}", id);
        bookingRepository.deleteById(id);
        bookingSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Bookings for query {}", query);
        return bookingSearchRepository.search(query, pageable).map(bookingMapper::toDto);
    }
}
