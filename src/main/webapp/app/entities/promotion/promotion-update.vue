<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="jhipsterApp.promotion.home.createOrEditLabel"
          data-cy="PromotionCreateUpdateHeading"
          v-text="t$('jhipsterApp.promotion.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="promotion.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="promotion.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.promotion.code')" for="promotion-code"></label>
            <input
              type="text"
              class="form-control"
              name="code"
              id="promotion-code"
              data-cy="code"
              :class="{ valid: !v$.code.$invalid, invalid: v$.code.$invalid }"
              v-model="v$.code.$model"
              required
            />
            <div v-if="v$.code.$anyDirty && v$.code.$invalid">
              <small class="form-text text-danger" v-for="error of v$.code.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.promotion.discountType')" for="promotion-discountType"></label>
            <select
              class="form-control"
              name="discountType"
              :class="{ valid: !v$.discountType.$invalid, invalid: v$.discountType.$invalid }"
              v-model="v$.discountType.$model"
              id="promotion-discountType"
              data-cy="discountType"
              required
            >
              <option
                v-for="discountType in discountTypeValues"
                :key="discountType"
                :value="discountType"
                :label="t$('jhipsterApp.DiscountType.' + discountType)"
              >
                {{ discountType }}
              </option>
            </select>
            <div v-if="v$.discountType.$anyDirty && v$.discountType.$invalid">
              <small class="form-text text-danger" v-for="error of v$.discountType.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.promotion.discountValue')" for="promotion-discountValue"></label>
            <input
              type="number"
              class="form-control"
              name="discountValue"
              id="promotion-discountValue"
              data-cy="discountValue"
              :class="{ valid: !v$.discountValue.$invalid, invalid: v$.discountValue.$invalid }"
              v-model.number="v$.discountValue.$model"
              required
            />
            <div v-if="v$.discountValue.$anyDirty && v$.discountValue.$invalid">
              <small class="form-text text-danger" v-for="error of v$.discountValue.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.promotion.validFrom')" for="promotion-validFrom"></label>
            <div class="d-flex">
              <input
                id="promotion-validFrom"
                data-cy="validFrom"
                type="datetime-local"
                class="form-control"
                name="validFrom"
                :class="{ valid: !v$.validFrom.$invalid, invalid: v$.validFrom.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.validFrom.$model)"
                @change="updateInstantField('validFrom', $event)"
              />
            </div>
            <div v-if="v$.validFrom.$anyDirty && v$.validFrom.$invalid">
              <small class="form-text text-danger" v-for="error of v$.validFrom.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.promotion.validUntil')" for="promotion-validUntil"></label>
            <div class="d-flex">
              <input
                id="promotion-validUntil"
                data-cy="validUntil"
                type="datetime-local"
                class="form-control"
                name="validUntil"
                :class="{ valid: !v$.validUntil.$invalid, invalid: v$.validUntil.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.validUntil.$model)"
                @change="updateInstantField('validUntil', $event)"
              />
            </div>
            <div v-if="v$.validUntil.$anyDirty && v$.validUntil.$invalid">
              <small class="form-text text-danger" v-for="error of v$.validUntil.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.promotion.maxUses')" for="promotion-maxUses"></label>
            <input
              type="number"
              class="form-control"
              name="maxUses"
              id="promotion-maxUses"
              data-cy="maxUses"
              :class="{ valid: !v$.maxUses.$invalid, invalid: v$.maxUses.$invalid }"
              v-model.number="v$.maxUses.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.promotion.isActive')" for="promotion-isActive"></label>
            <input
              type="checkbox"
              class="form-check"
              name="isActive"
              id="promotion-isActive"
              data-cy="isActive"
              :class="{ valid: !v$.isActive.$invalid, invalid: v$.isActive.$invalid }"
              v-model="v$.isActive.$model"
              required
            />
            <div v-if="v$.isActive.$anyDirty && v$.isActive.$invalid">
              <small class="form-text text-danger" v-for="error of v$.isActive.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
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
<script lang="ts" src="./promotion-update.component.ts"></script>
