<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="jhipsterApp.city.home.createOrEditLabel"
          data-cy="CityCreateUpdateHeading"
          v-text="t$('jhipsterApp.city.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="city.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="city.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.city.name')" for="city-name"></label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="city-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
              required
            />
            <div v-if="v$.name.$anyDirty && v$.name.$invalid">
              <small class="form-text text-danger" v-for="error of v$.name.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.city.postalCode')" for="city-postalCode"></label>
            <input
              type="text"
              class="form-control"
              name="postalCode"
              id="city-postalCode"
              data-cy="postalCode"
              :class="{ valid: !v$.postalCode.$invalid, invalid: v$.postalCode.$invalid }"
              v-model="v$.postalCode.$model"
            />
            <div v-if="v$.postalCode.$anyDirty && v$.postalCode.$invalid">
              <small class="form-text text-danger" v-for="error of v$.postalCode.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.city.latitude')" for="city-latitude"></label>
            <input
              type="number"
              class="form-control"
              name="latitude"
              id="city-latitude"
              data-cy="latitude"
              :class="{ valid: !v$.latitude.$invalid, invalid: v$.latitude.$invalid }"
              v-model.number="v$.latitude.$model"
              required
            />
            <div v-if="v$.latitude.$anyDirty && v$.latitude.$invalid">
              <small class="form-text text-danger" v-for="error of v$.latitude.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.city.longitude')" for="city-longitude"></label>
            <input
              type="number"
              class="form-control"
              name="longitude"
              id="city-longitude"
              data-cy="longitude"
              :class="{ valid: !v$.longitude.$invalid, invalid: v$.longitude.$invalid }"
              v-model.number="v$.longitude.$model"
              required
            />
            <div v-if="v$.longitude.$anyDirty && v$.longitude.$invalid">
              <small class="form-text text-danger" v-for="error of v$.longitude.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.city.country')" for="city-country"></label>
            <select class="form-control" id="city-country" data-cy="country" name="country" v-model="city.country">
              <option :value="null"></option>
              <option
                :value="city.country && countryOption.id === city.country.id ? city.country : countryOption"
                v-for="countryOption in countries"
                :key="countryOption.id"
              >
                {{ countryOption.id }}
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
<script lang="ts" src="./city-update.component.ts"></script>
