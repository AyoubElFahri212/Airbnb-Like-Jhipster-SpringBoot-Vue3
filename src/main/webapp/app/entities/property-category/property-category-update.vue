<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="jhipsterApp.propertyCategory.home.createOrEditLabel"
          data-cy="PropertyCategoryCreateUpdateHeading"
          v-text="t$('jhipsterApp.propertyCategory.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="propertyCategory.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="propertyCategory.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.propertyCategory.name')" for="property-category-name"></label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="property-category-name"
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
            <label
              class="form-control-label"
              v-text="t$('jhipsterApp.propertyCategory.description')"
              for="property-category-description"
            ></label>
            <textarea
              class="form-control"
              name="description"
              id="property-category-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            ></textarea>
          </div>
          <div class="form-group">
            <label v-text="t$('jhipsterApp.propertyCategory.property')" for="property-category-property"></label>
            <select
              class="form-control"
              id="property-category-properties"
              data-cy="property"
              multiple
              name="property"
              v-if="propertyCategory.properties !== undefined"
              v-model="propertyCategory.properties"
            >
              <option
                :value="getSelected(propertyCategory.properties, propertyOption, 'id')"
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
<script lang="ts" src="./property-category-update.component.ts"></script>
