<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="property">
        <h2 class="jh-entity-heading" data-cy="propertyDetailsHeading">
          <span v-text="t$('jhipsterApp.property.detail.title')"></span> {{ property.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('jhipsterApp.property.title')"></span>
          </dt>
          <dd>
            <span>{{ property.title }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.property.description')"></span>
          </dt>
          <dd>
            <span>{{ property.description }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.property.pricePerNight')"></span>
          </dt>
          <dd>
            <span>{{ property.pricePerNight }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.property.address')"></span>
          </dt>
          <dd>
            <span>{{ property.address }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.property.latitude')"></span>
          </dt>
          <dd>
            <span>{{ property.latitude }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.property.longitude')"></span>
          </dt>
          <dd>
            <span>{{ property.longitude }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.property.numberOfRooms')"></span>
          </dt>
          <dd>
            <span>{{ property.numberOfRooms }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.property.numberOfBathrooms')"></span>
          </dt>
          <dd>
            <span>{{ property.numberOfBathrooms }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.property.maxGuests')"></span>
          </dt>
          <dd>
            <span>{{ property.maxGuests }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.property.propertySize')"></span>
          </dt>
          <dd>
            <span>{{ property.propertySize }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.property.availabilityStart')"></span>
          </dt>
          <dd>
            <span v-if="property.availabilityStart">{{ formatDateLong(property.availabilityStart) }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.property.availabilityEnd')"></span>
          </dt>
          <dd>
            <span v-if="property.availabilityEnd">{{ formatDateLong(property.availabilityEnd) }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.property.instantBook')"></span>
          </dt>
          <dd>
            <span>{{ property.instantBook }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.property.minimumStay')"></span>
          </dt>
          <dd>
            <span>{{ property.minimumStay }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.property.cancellationPolicy')"></span>
          </dt>
          <dd>
            <span>{{ property.cancellationPolicy }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.property.houseRules')"></span>
          </dt>
          <dd>
            <span>{{ property.houseRules }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.property.isActive')"></span>
          </dt>
          <dd>
            <span>{{ property.isActive }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.property.host')"></span>
          </dt>
          <dd>
            {{ property.host ? property.host.id : '' }}
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.property.city')"></span>
          </dt>
          <dd>
            <div v-if="property.city">
              <router-link :to="{ name: 'CityView', params: { cityId: property.city.id } }">{{ property.city.id }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.property.amenities')"></span>
          </dt>
          <dd>
            <span v-for="(amenities, i) in property.amenities" :key="amenities.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'AmenityView', params: { amenityId: amenities.id } }">{{ amenities.id }}</router-link>
            </span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.property.categories')"></span>
          </dt>
          <dd>
            <span v-for="(categories, i) in property.categories" :key="categories.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'PropertyCategoryView', params: { propertyCategoryId: categories.id } }">{{
                categories.id
              }}</router-link>
            </span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="property.id" :to="{ name: 'PropertyEdit', params: { propertyId: property.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./property-details.component.ts"></script>
