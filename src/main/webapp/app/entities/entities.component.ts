import { defineComponent, provide } from 'vue';

import PropertyService from './property/property.service';
import CountryService from './country/country.service';
import CityService from './city/city.service';
import AmenityService from './amenity/amenity.service';
import PropertyCategoryService from './property-category/property-category.service';
import PropertyImageService from './property-image/property-image.service';
import BookingService from './booking/booking.service';
import ReviewService from './review/review.service';
import PromotionService from './promotion/promotion.service';
import UserService from '@/entities/user/user.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('propertyService', () => new PropertyService());
    provide('countryService', () => new CountryService());
    provide('cityService', () => new CityService());
    provide('amenityService', () => new AmenityService());
    provide('propertyCategoryService', () => new PropertyCategoryService());
    provide('propertyImageService', () => new PropertyImageService());
    provide('bookingService', () => new BookingService());
    provide('reviewService', () => new ReviewService());
    provide('promotionService', () => new PromotionService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
