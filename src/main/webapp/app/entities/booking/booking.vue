<template>
  <div>
    <h2 id="page-heading" data-cy="BookingHeading">
      <span v-text="t$('jhipsterApp.booking.home.title')" id="booking-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('jhipsterApp.booking.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'BookingCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-booking"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('jhipsterApp.booking.home.createLabel')"></span>
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
              :placeholder="t$('jhipsterApp.booking.home.search')"
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
    <div class="alert alert-warning" v-if="!isFetching && bookings && bookings.length === 0">
      <span v-text="t$('jhipsterApp.booking.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="bookings && bookings.length > 0">
      <table class="table table-striped" aria-describedby="bookings">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('checkInDate')">
              <span v-text="t$('jhipsterApp.booking.checkInDate')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'checkInDate'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('checkOutDate')">
              <span v-text="t$('jhipsterApp.booking.checkOutDate')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'checkOutDate'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('totalPrice')">
              <span v-text="t$('jhipsterApp.booking.totalPrice')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'totalPrice'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('bookingDate')">
              <span v-text="t$('jhipsterApp.booking.bookingDate')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'bookingDate'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('status')">
              <span v-text="t$('jhipsterApp.booking.status')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'status'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('specialRequests')">
              <span v-text="t$('jhipsterApp.booking.specialRequests')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'specialRequests'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('guest.id')">
              <span v-text="t$('jhipsterApp.booking.guest')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'guest.id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('property.id')">
              <span v-text="t$('jhipsterApp.booking.property')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'property.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="booking in bookings" :key="booking.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'BookingView', params: { bookingId: booking.id } }">{{ booking.id }}</router-link>
            </td>
            <td>{{ formatDateShort(booking.checkInDate) || '' }}</td>
            <td>{{ formatDateShort(booking.checkOutDate) || '' }}</td>
            <td>{{ booking.totalPrice }}</td>
            <td>{{ formatDateShort(booking.bookingDate) || '' }}</td>
            <td v-text="t$('jhipsterApp.BookingStatus.' + booking.status)"></td>
            <td>{{ booking.specialRequests }}</td>
            <td>
              {{ booking.guest ? booking.guest.id : '' }}
            </td>
            <td>
              <div v-if="booking.property">
                <router-link :to="{ name: 'PropertyView', params: { propertyId: booking.property.id } }">{{
                  booking.property.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'BookingView', params: { bookingId: booking.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'BookingEdit', params: { bookingId: booking.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(booking)"
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
        <span id="jhipsterApp.booking.delete.question" data-cy="bookingDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-booking-heading" v-text="t$('jhipsterApp.booking.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-booking"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeBooking()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="bookings && bookings.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./booking.component.ts"></script>
