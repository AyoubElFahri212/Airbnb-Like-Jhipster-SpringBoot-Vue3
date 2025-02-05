<template>
  <div>
    <h2 id="page-heading" data-cy="PropertyHeading">
      <span v-text="t$('jhipsterApp.property.home.title')" id="property-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('jhipsterApp.property.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'PropertyCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-property"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('jhipsterApp.property.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <div class="row">
      <div class="col-sm-12">
        <form name="searchForm" class="form-inline" @submit.prevent="search(currentSearch)">
          <div class="input-group w-100 mt-3">
            <input
              type="text"
              class="form-control"
              name="currentSearch"
              id="currentSearch"
              :placeholder="t$('jhipsterApp.property.home.search')"
              v-model="currentSearch"
            />
            <button type="button" id="launch-search" class="btn btn-primary" @click="search(currentSearch)">
              <font-awesome-icon icon="search"></font-awesome-icon>
            </button>
            <button type="button" id="clear-search" class="btn btn-secondary" @click="clear()" v-if="currentSearch">
              <font-awesome-icon icon="trash"></font-awesome-icon>
            </button>
          </div>
        </form>
      </div>
    </div>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && properties && properties.length === 0">
      <span v-text="t$('jhipsterApp.property.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="properties && properties.length > 0">
      <table class="table table-striped" aria-describedby="properties">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('title')">
              <span v-text="t$('jhipsterApp.property.title')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'title'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('description')">
              <span v-text="t$('jhipsterApp.property.description')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'description'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('pricePerNight')">
              <span v-text="t$('jhipsterApp.property.pricePerNight')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'pricePerNight'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('address')">
              <span v-text="t$('jhipsterApp.property.address')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'address'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('latitude')">
              <span v-text="t$('jhipsterApp.property.latitude')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'latitude'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('longitude')">
              <span v-text="t$('jhipsterApp.property.longitude')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'longitude'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('numberOfRooms')">
              <span v-text="t$('jhipsterApp.property.numberOfRooms')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'numberOfRooms'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('numberOfBathrooms')">
              <span v-text="t$('jhipsterApp.property.numberOfBathrooms')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'numberOfBathrooms'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('maxGuests')">
              <span v-text="t$('jhipsterApp.property.maxGuests')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'maxGuests'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('propertySize')">
              <span v-text="t$('jhipsterApp.property.propertySize')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'propertySize'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('availabilityStart')">
              <span v-text="t$('jhipsterApp.property.availabilityStart')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'availabilityStart'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('availabilityEnd')">
              <span v-text="t$('jhipsterApp.property.availabilityEnd')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'availabilityEnd'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('instantBook')">
              <span v-text="t$('jhipsterApp.property.instantBook')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'instantBook'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('minimumStay')">
              <span v-text="t$('jhipsterApp.property.minimumStay')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'minimumStay'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('cancellationPolicy')">
              <span v-text="t$('jhipsterApp.property.cancellationPolicy')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'cancellationPolicy'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('houseRules')">
              <span v-text="t$('jhipsterApp.property.houseRules')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'houseRules'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('isActive')">
              <span v-text="t$('jhipsterApp.property.isActive')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'isActive'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('host.id')">
              <span v-text="t$('jhipsterApp.property.host')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'host.id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('city.id')">
              <span v-text="t$('jhipsterApp.property.city')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'city.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="property in properties" :key="property.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'PropertyView', params: { propertyId: property.id } }">{{ property.id }}</router-link>
            </td>
            <td>{{ property.title }}</td>
            <td>{{ property.description }}</td>
            <td>{{ property.pricePerNight }}</td>
            <td>{{ property.address }}</td>
            <td>{{ property.latitude }}</td>
            <td>{{ property.longitude }}</td>
            <td>{{ property.numberOfRooms }}</td>
            <td>{{ property.numberOfBathrooms }}</td>
            <td>{{ property.maxGuests }}</td>
            <td>{{ property.propertySize }}</td>
            <td>{{ formatDateShort(property.availabilityStart) || '' }}</td>
            <td>{{ formatDateShort(property.availabilityEnd) || '' }}</td>
            <td>{{ property.instantBook }}</td>
            <td>{{ property.minimumStay }}</td>
            <td>{{ property.cancellationPolicy }}</td>
            <td>{{ property.houseRules }}</td>
            <td>{{ property.isActive }}</td>
            <td>
              {{ property.host ? property.host.id : '' }}
            </td>
            <td>
              <div v-if="property.city">
                <router-link :to="{ name: 'CityView', params: { cityId: property.city.id } }">{{ property.city.id }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'PropertyView', params: { propertyId: property.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'PropertyEdit', params: { propertyId: property.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(property)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="t$('entity.action.delete')"></span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span id="jhipsterApp.property.delete.question" data-cy="propertyDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-property-heading" v-text="t$('jhipsterApp.property.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-property"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeProperty()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="properties && properties.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./property.component.ts"></script>
