<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="jhipsterApp.property.home.createOrEditLabel"
          data-cy="PropertyCreateUpdateHeading"
          v-text="t$('jhipsterApp.property.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="property.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="property.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.property.title')" for="property-title"></label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="property-title"
              data-cy="title"
              :class="{ valid: !v$.title.$invalid, invalid: v$.title.$invalid }"
              v-model="v$.title.$model"
              required
            />
            <div v-if="v$.title.$anyDirty && v$.title.$invalid">
              <small class="form-text text-danger" v-for="error of v$.title.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.property.description')" for="property-description"></label>
            <textarea
              class="form-control"
              name="description"
              id="property-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
              required
            ></textarea>
            <div v-if="v$.description.$anyDirty && v$.description.$invalid">
              <small class="form-text text-danger" v-for="error of v$.description.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.property.pricePerNight')" for="property-pricePerNight"></label>
            <input
              type="number"
              class="form-control"
              name="pricePerNight"
              id="property-pricePerNight"
              data-cy="pricePerNight"
              :class="{ valid: !v$.pricePerNight.$invalid, invalid: v$.pricePerNight.$invalid }"
              v-model.number="v$.pricePerNight.$model"
              required
            />
            <div v-if="v$.pricePerNight.$anyDirty && v$.pricePerNight.$invalid">
              <small class="form-text text-danger" v-for="error of v$.pricePerNight.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.property.address')" for="property-address"></label>
            <input
              type="text"
              class="form-control"
              name="address"
              id="property-address"
              data-cy="address"
              :class="{ valid: !v$.address.$invalid, invalid: v$.address.$invalid }"
              v-model="v$.address.$model"
              required
            />
            <div v-if="v$.address.$anyDirty && v$.address.$invalid">
              <small class="form-text text-danger" v-for="error of v$.address.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.property.latitude')" for="property-latitude"></label>
            <input
              type="number"
              class="form-control"
              name="latitude"
              id="property-latitude"
              data-cy="latitude"
              :class="{ valid: !v$.latitude.$invalid, invalid: v$.latitude.$invalid }"
              v-model.number="v$.latitude.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.property.longitude')" for="property-longitude"></label>
            <input
              type="number"
              class="form-control"
              name="longitude"
              id="property-longitude"
              data-cy="longitude"
              :class="{ valid: !v$.longitude.$invalid, invalid: v$.longitude.$invalid }"
              v-model.number="v$.longitude.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.property.numberOfRooms')" for="property-numberOfRooms"></label>
            <input
              type="number"
              class="form-control"
              name="numberOfRooms"
              id="property-numberOfRooms"
              data-cy="numberOfRooms"
              :class="{ valid: !v$.numberOfRooms.$invalid, invalid: v$.numberOfRooms.$invalid }"
              v-model.number="v$.numberOfRooms.$model"
              required
            />
            <div v-if="v$.numberOfRooms.$anyDirty && v$.numberOfRooms.$invalid">
              <small class="form-text text-danger" v-for="error of v$.numberOfRooms.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('jhipsterApp.property.numberOfBathrooms')"
              for="property-numberOfBathrooms"
            ></label>
            <input
              type="number"
              class="form-control"
              name="numberOfBathrooms"
              id="property-numberOfBathrooms"
              data-cy="numberOfBathrooms"
              :class="{ valid: !v$.numberOfBathrooms.$invalid, invalid: v$.numberOfBathrooms.$invalid }"
              v-model.number="v$.numberOfBathrooms.$model"
            />
            <div v-if="v$.numberOfBathrooms.$anyDirty && v$.numberOfBathrooms.$invalid">
              <small class="form-text text-danger" v-for="error of v$.numberOfBathrooms.$errors" :key="error.$uid">{{
                error.$message
              }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.property.maxGuests')" for="property-maxGuests"></label>
            <input
              type="number"
              class="form-control"
              name="maxGuests"
              id="property-maxGuests"
              data-cy="maxGuests"
              :class="{ valid: !v$.maxGuests.$invalid, invalid: v$.maxGuests.$invalid }"
              v-model.number="v$.maxGuests.$model"
            />
            <div v-if="v$.maxGuests.$anyDirty && v$.maxGuests.$invalid">
              <small class="form-text text-danger" v-for="error of v$.maxGuests.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.property.propertySize')" for="property-propertySize"></label>
            <input
              type="number"
              class="form-control"
              name="propertySize"
              id="property-propertySize"
              data-cy="propertySize"
              :class="{ valid: !v$.propertySize.$invalid, invalid: v$.propertySize.$invalid }"
              v-model.number="v$.propertySize.$model"
            />
            <div v-if="v$.propertySize.$anyDirty && v$.propertySize.$invalid">
              <small class="form-text text-danger" v-for="error of v$.propertySize.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('jhipsterApp.property.availabilityStart')"
              for="property-availabilityStart"
            ></label>
            <div class="d-flex">
              <input
                id="property-availabilityStart"
                data-cy="availabilityStart"
                type="datetime-local"
                class="form-control"
                name="availabilityStart"
                :class="{ valid: !v$.availabilityStart.$invalid, invalid: v$.availabilityStart.$invalid }"
                :value="convertDateTimeFromServer(v$.availabilityStart.$model)"
                @change="updateZonedDateTimeField('availabilityStart', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.property.availabilityEnd')" for="property-availabilityEnd"></label>
            <div class="d-flex">
              <input
                id="property-availabilityEnd"
                data-cy="availabilityEnd"
                type="datetime-local"
                class="form-control"
                name="availabilityEnd"
                :class="{ valid: !v$.availabilityEnd.$invalid, invalid: v$.availabilityEnd.$invalid }"
                :value="convertDateTimeFromServer(v$.availabilityEnd.$model)"
                @change="updateZonedDateTimeField('availabilityEnd', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.property.instantBook')" for="property-instantBook"></label>
            <input
              type="checkbox"
              class="form-check"
              name="instantBook"
              id="property-instantBook"
              data-cy="instantBook"
              :class="{ valid: !v$.instantBook.$invalid, invalid: v$.instantBook.$invalid }"
              v-model="v$.instantBook.$model"
              required
            />
            <div v-if="v$.instantBook.$anyDirty && v$.instantBook.$invalid">
              <small class="form-text text-danger" v-for="error of v$.instantBook.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.property.minimumStay')" for="property-minimumStay"></label>
            <input
              type="number"
              class="form-control"
              name="minimumStay"
              id="property-minimumStay"
              data-cy="minimumStay"
              :class="{ valid: !v$.minimumStay.$invalid, invalid: v$.minimumStay.$invalid }"
              v-model.number="v$.minimumStay.$model"
            />
            <div v-if="v$.minimumStay.$anyDirty && v$.minimumStay.$invalid">
              <small class="form-text text-danger" v-for="error of v$.minimumStay.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('jhipsterApp.property.cancellationPolicy')"
              for="property-cancellationPolicy"
            ></label>
            <input
              type="text"
              class="form-control"
              name="cancellationPolicy"
              id="property-cancellationPolicy"
              data-cy="cancellationPolicy"
              :class="{ valid: !v$.cancellationPolicy.$invalid, invalid: v$.cancellationPolicy.$invalid }"
              v-model="v$.cancellationPolicy.$model"
              required
            />
            <div v-if="v$.cancellationPolicy.$anyDirty && v$.cancellationPolicy.$invalid">
              <small class="form-text text-danger" v-for="error of v$.cancellationPolicy.$errors" :key="error.$uid">{{
                error.$message
              }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.property.houseRules')" for="property-houseRules"></label>
            <textarea
              class="form-control"
              name="houseRules"
              id="property-houseRules"
              data-cy="houseRules"
              :class="{ valid: !v$.houseRules.$invalid, invalid: v$.houseRules.$invalid }"
              v-model="v$.houseRules.$model"
            ></textarea>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.property.isActive')" for="property-isActive"></label>
            <input
              type="checkbox"
              class="form-check"
              name="isActive"
              id="property-isActive"
              data-cy="isActive"
              :class="{ valid: !v$.isActive.$invalid, invalid: v$.isActive.$invalid }"
              v-model="v$.isActive.$model"
              required
            />
            <div v-if="v$.isActive.$anyDirty && v$.isActive.$invalid">
              <small class="form-text text-danger" v-for="error of v$.isActive.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.property.host')" for="property-host"></label>
            <select class="form-control" id="property-host" data-cy="host" name="host" v-model="property.host">
              <option :value="null"></option>
              <option
                :value="property.host && userOption.id === property.host.id ? property.host : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.property.city')" for="property-city"></label>
            <select class="form-control" id="property-city" data-cy="city" name="city" v-model="property.city">
              <option :value="null"></option>
              <option
                :value="property.city && cityOption.id === property.city.id ? property.city : cityOption"
                v-for="cityOption in cities"
                :key="cityOption.id"
              >
                {{ cityOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label v-text="t$('jhipsterApp.property.amenities')" for="property-amenities"></label>
            <select
              class="form-control"
              id="property-amenities"
              data-cy="amenities"
              multiple
              name="amenities"
              v-if="property.amenities !== undefined"
              v-model="property.amenities"
            >
              <option
                :value="getSelected(property.amenities, amenityOption, 'id')"
                v-for="amenityOption in amenities"
                :key="amenityOption.id"
              >
                {{ amenityOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label v-text="t$('jhipsterApp.property.categories')" for="property-categories"></label>
            <select
              class="form-control"
              id="property-categories"
              data-cy="categories"
              multiple
              name="categories"
              v-if="property.categories !== undefined"
              v-model="property.categories"
            >
              <option
                :value="getSelected(property.categories, propertyCategoryOption, 'id')"
                v-for="propertyCategoryOption in propertyCategories"
                :key="propertyCategoryOption.id"
              >
                {{ propertyCategoryOption.id }}
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
<script lang="ts" src="./property-update.component.ts"></script>
