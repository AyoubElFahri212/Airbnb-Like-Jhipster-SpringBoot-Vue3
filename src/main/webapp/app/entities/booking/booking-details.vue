<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="booking">
        <h2 class="jh-entity-heading" data-cy="bookingDetailsHeading">
          <span v-text="t$('jhipsterApp.booking.detail.title')"></span> {{ booking.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('jhipsterApp.booking.checkInDate')"></span>
          </dt>
          <dd>
            <span v-if="booking.checkInDate">{{ formatDateLong(booking.checkInDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.booking.checkOutDate')"></span>
          </dt>
          <dd>
            <span v-if="booking.checkOutDate">{{ formatDateLong(booking.checkOutDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.booking.totalPrice')"></span>
          </dt>
          <dd>
            <span>{{ booking.totalPrice }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.booking.bookingDate')"></span>
          </dt>
          <dd>
            <span v-if="booking.bookingDate">{{ formatDateLong(booking.bookingDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.booking.status')"></span>
          </dt>
          <dd>
            <span v-text="t$('jhipsterApp.BookingStatus.' + booking.status)"></span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.booking.specialRequests')"></span>
          </dt>
          <dd>
            <span>{{ booking.specialRequests }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.booking.guest')"></span>
          </dt>
          <dd>
            {{ booking.guest ? booking.guest.id : '' }}
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.booking.property')"></span>
          </dt>
          <dd>
            <div v-if="booking.property">
              <router-link :to="{ name: 'PropertyView', params: { propertyId: booking.property.id } }">{{
                booking.property.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="booking.id" :to="{ name: 'BookingEdit', params: { bookingId: booking.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./booking-details.component.ts"></script>
