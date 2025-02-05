<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="jhipsterApp.propertyImage.home.createOrEditLabel"
          data-cy="PropertyImageCreateUpdateHeading"
          v-text="t$('jhipsterApp.propertyImage.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="propertyImage.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="propertyImage.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.propertyImage.imageUrl')" for="property-image-imageUrl"></label>
            <input
              type="text"
              class="form-control"
              name="imageUrl"
              id="property-image-imageUrl"
              data-cy="imageUrl"
              :class="{ valid: !v$.imageUrl.$invalid, invalid: v$.imageUrl.$invalid }"
              v-model="v$.imageUrl.$model"
              required
            />
            <div v-if="v$.imageUrl.$anyDirty && v$.imageUrl.$invalid">
              <small class="form-text text-danger" v-for="error of v$.imageUrl.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.propertyImage.isMain')" for="property-image-isMain"></label>
            <input
              type="checkbox"
              class="form-check"
              name="isMain"
              id="property-image-isMain"
              data-cy="isMain"
              :class="{ valid: !v$.isMain.$invalid, invalid: v$.isMain.$invalid }"
              v-model="v$.isMain.$model"
              required
            />
            <div v-if="v$.isMain.$anyDirty && v$.isMain.$invalid">
              <small class="form-text text-danger" v-for="error of v$.isMain.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.propertyImage.caption')" for="property-image-caption"></label>
            <input
              type="text"
              class="form-control"
              name="caption"
              id="property-image-caption"
              data-cy="caption"
              :class="{ valid: !v$.caption.$invalid, invalid: v$.caption.$invalid }"
              v-model="v$.caption.$model"
            />
            <div v-if="v$.caption.$anyDirty && v$.caption.$invalid">
              <small class="form-text text-danger" v-for="error of v$.caption.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.propertyImage.property')" for="property-image-property"></label>
            <select class="form-control" id="property-image-property" data-cy="property" name="property" v-model="propertyImage.property">
              <option :value="null"></option>
              <option
                :value="propertyImage.property && propertyOption.id === propertyImage.property.id ? propertyImage.property : propertyOption"
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
<script lang="ts" src="./property-image-update.component.ts"></script>
