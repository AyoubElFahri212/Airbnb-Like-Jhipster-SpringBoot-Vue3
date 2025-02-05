<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="jhipsterApp.booking.home.createOrEditLabel"
          data-cy="BookingCreateUpdateHeading"
          v-text="t$('jhipsterApp.booking.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="booking.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="booking.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.booking.checkInDate')" for="booking-checkInDate"></label>
            <div class="d-flex">
              <input
                id="booking-checkInDate"
                data-cy="checkInDate"
                type="datetime-local"
                class="form-control"
                name="checkInDate"
                :class="{ valid: !v$.checkInDate.$invalid, invalid: v$.checkInDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.checkInDate.$model)"
                @change="updateInstantField('checkInDate', $event)"
              />
            </div>
            <div v-if="v$.checkInDate.$anyDirty && v$.checkInDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.checkInDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.booking.checkOutDate')" for="booking-checkOutDate"></label>
            <div class="d-flex">
              <input
                id="booking-checkOutDate"
                data-cy="checkOutDate"
                type="datetime-local"
                class="form-control"
                name="checkOutDate"
                :class="{ valid: !v$.checkOutDate.$invalid, invalid: v$.checkOutDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.checkOutDate.$model)"
                @change="updateInstantField('checkOutDate', $event)"
              />
            </div>
            <div v-if="v$.checkOutDate.$anyDirty && v$.checkOutDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.checkOutDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.booking.totalPrice')" for="booking-totalPrice"></label>
            <input
              type="number"
              class="form-control"
              name="totalPrice"
              id="booking-totalPrice"
              data-cy="totalPrice"
              :class="{ valid: !v$.totalPrice.$invalid, invalid: v$.totalPrice.$invalid }"
              v-model.number="v$.totalPrice.$model"
              required
            />
            <div v-if="v$.totalPrice.$anyDirty && v$.totalPrice.$invalid">
              <small class="form-text text-danger" v-for="error of v$.totalPrice.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.booking.bookingDate')" for="booking-bookingDate"></label>
            <div class="d-flex">
              <input
                id="booking-bookingDate"
                data-cy="bookingDate"
                type="datetime-local"
                class="form-control"
                name="bookingDate"
                :class="{ valid: !v$.bookingDate.$invalid, invalid: v$.bookingDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.bookingDate.$model)"
                @change="updateInstantField('bookingDate', $event)"
              />
            </div>
            <div v-if="v$.bookingDate.$anyDirty && v$.bookingDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.bookingDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.booking.status')" for="booking-status"></label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="booking-status"
              data-cy="status"
              required
            >
              <option
                v-for="bookingStatus in bookingStatusValues"
                :key="bookingStatus"
                :value="bookingStatus"
                :label="t$('jhipsterApp.BookingStatus.' + bookingStatus)"
              >
                {{ bookingStatus }}
              </option>
            </select>
            <div v-if="v$.status.$anyDirty && v$.status.$invalid">
              <small class="form-text text-danger" v-for="error of v$.status.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.booking.specialRequests')" for="booking-specialRequests"></label>
            <textarea
              class="form-control"
              name="specialRequests"
              id="booking-specialRequests"
              data-cy="specialRequests"
              :class="{ valid: !v$.specialRequests.$invalid, invalid: v$.specialRequests.$invalid }"
              v-model="v$.specialRequests.$model"
            ></textarea>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.booking.guest')" for="booking-guest"></label>
            <select class="form-control" id="booking-guest" data-cy="guest" name="guest" v-model="booking.guest">
              <option :value="null"></option>
              <option
                :value="booking.guest && userOption.id === booking.guest.id ? booking.guest : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.booking.property')" for="booking-property"></label>
            <select class="form-control" id="booking-property" data-cy="property" name="property" v-model="booking.property">
              <option :value="null"></option>
              <option
                :value="booking.property && propertyOption.id === booking.property.id ? booking.property : propertyOption"
                v-for="propertyOption in properties"
                :key="propertyOption.id"
              >
                {{ propertyOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" @click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./booking-update.component.ts"></script>
