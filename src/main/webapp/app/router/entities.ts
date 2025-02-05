import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore
const Entities = () => import('@/entities/entities.vue');

const Property = () => import('@/entities/property/property.vue');
const PropertyUpdate = () => import('@/entities/property/property-update.vue');
const PropertyDetails = () => import('@/entities/property/property-details.vue');

const Country = () => import('@/entities/country/country.vue');
const CountryUpdate = () => import('@/entities/country/country-update.vue');
const CountryDetails = () => import('@/entities/country/country-details.vue');

const City = () => import('@/entities/city/city.vue');
const CityUpdate = () => import('@/entities/city/city-update.vue');
const CityDetails = () => import('@/entities/city/city-details.vue');

const Amenity = () => import('@/entities/amenity/amenity.vue');
const AmenityUpdate = () => import('@/entities/amenity/amenity-update.vue');
const AmenityDetails = () => import('@/entities/amenity/amenity-details.vue');

const PropertyCategory = () => import('@/entities/property-category/property-category.vue');
const PropertyCategoryUpdate = () => import('@/entities/property-category/property-category-update.vue');
const PropertyCategoryDetails = () => import('@/entities/property-category/property-category-details.vue');

const PropertyImage = () => import('@/entities/property-image/property-image.vue');
const PropertyImageUpdate = () => import('@/entities/property-image/property-image-update.vue');
const PropertyImageDetails = () => import('@/entities/property-image/property-image-details.vue');

const Booking = () => import('@/entities/booking/booking.vue');
const BookingUpdate = () => import('@/entities/booking/booking-update.vue');
const BookingDetails = () => import('@/entities/booking/booking-details.vue');

const Review = () => import('@/entities/review/review.vue');
const ReviewUpdate = () => import('@/entities/review/review-update.vue');
const ReviewDetails = () => import('@/entities/review/review-details.vue');

const Promotion = () => import('@/entities/promotion/promotion.vue');
const PromotionUpdate = () => import('@/entities/promotion/promotion-update.vue');
const PromotionDetails = () => import('@/entities/promotion/promotion-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'property',
      name: 'Property',
      component: Property,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'property/new',
      name: 'PropertyCreate',
      component: PropertyUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'property/:propertyId/edit',
      name: 'PropertyEdit',
      component: PropertyUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'property/:propertyId/view',
      name: 'PropertyView',
      component: PropertyDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'country',
      name: 'Country',
      component: Country,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'country/new',
      name: 'CountryCreate',
      component: CountryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'country/:countryId/edit',
      name: 'CountryEdit',
      component: CountryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'country/:countryId/view',
      name: 'CountryView',
      component: CountryDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'city',
      name: 'City',
      component: City,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'city/new',
      name: 'CityCreate',
      component: CityUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'city/:cityId/edit',
      name: 'CityEdit',
      component: CityUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'city/:cityId/view',
      name: 'CityView',
      component: CityDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'amenity',
      name: 'Amenity',
      component: Amenity,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'amenity/new',
      name: 'AmenityCreate',
      component: AmenityUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'amenity/:amenityId/edit',
      name: 'AmenityEdit',
      component: AmenityUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'amenity/:amenityId/view',
      name: 'AmenityView',
      component: AmenityDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'property-category',
      name: 'PropertyCategory',
      component: PropertyCategory,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'property-category/new',
      name: 'PropertyCategoryCreate',
      component: PropertyCategoryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'property-category/:propertyCategoryId/edit',
      name: 'PropertyCategoryEdit',
      component: PropertyCategoryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'property-category/:propertyCategoryId/view',
      name: 'PropertyCategoryView',
      component: PropertyCategoryDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'property-image',
      name: 'PropertyImage',
      component: PropertyImage,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'property-image/new',
      name: 'PropertyImageCreate',
      component: PropertyImageUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'property-image/:propertyImageId/edit',
      name: 'PropertyImageEdit',
      component: PropertyImageUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'property-image/:propertyImageId/view',
      name: 'PropertyImageView',
      component: PropertyImageDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'booking',
      name: 'Booking',
      component: Booking,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'booking/new',
      name: 'BookingCreate',
      component: BookingUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'booking/:bookingId/edit',
      name: 'BookingEdit',
      component: BookingUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'booking/:bookingId/view',
      name: 'BookingView',
      component: BookingDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'review',
      name: 'Review',
      component: Review,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'review/new',
      name: 'ReviewCreate',
      component: ReviewUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'review/:reviewId/edit',
      name: 'ReviewEdit',
      component: ReviewUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'review/:reviewId/view',
      name: 'ReviewView',
      component: ReviewDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'promotion',
      name: 'Promotion',
      component: Promotion,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'promotion/new',
      name: 'PromotionCreate',
      component: PromotionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'promotion/:promotionId/edit',
      name: 'PromotionEdit',
      component: PromotionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'promotion/:promotionId/view',
      name: 'PromotionView',
      component: PromotionDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
