<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="jhipsterApp.review.home.createOrEditLabel"
          data-cy="ReviewCreateUpdateHeading"
          v-text="t$('jhipsterApp.review.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="review.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="review.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.review.rating')" for="review-rating"></label>
            <input
              type="number"
              class="form-control"
              name="rating"
              id="review-rating"
              data-cy="rating"
              :class="{ valid: !v$.rating.$invalid, invalid: v$.rating.$invalid }"
              v-model.number="v$.rating.$model"
              required
            />
            <div v-if="v$.rating.$anyDirty && v$.rating.$invalid">
              <small class="form-text text-danger" v-for="error of v$.rating.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.review.comment')" for="review-comment"></label>
            <textarea
              class="form-control"
              name="comment"
              id="review-comment"
              data-cy="comment"
              :class="{ valid: !v$.comment.$invalid, invalid: v$.comment.$invalid }"
              v-model="v$.comment.$model"
            ></textarea>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.review.reviewDate')" for="review-reviewDate"></label>
            <div class="d-flex">
              <input
                id="review-reviewDate"
                data-cy="reviewDate"
                type="datetime-local"
                class="form-control"
                name="reviewDate"
                :class="{ valid: !v$.reviewDate.$invalid, invalid: v$.reviewDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.reviewDate.$model)"
                @change="updateInstantField('reviewDate', $event)"
              />
            </div>
            <div v-if="v$.reviewDate.$anyDirty && v$.reviewDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.reviewDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.review.author')" for="review-author"></label>
            <select class="form-control" id="review-author" data-cy="author" name="author" v-model="review.author">
              <option :value="null"></option>
              <option
                :value="review.author && userOption.id === review.author.id ? review.author : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('jhipsterApp.review.property')" for="review-property"></label>
            <select class="form-control" id="review-property" data-cy="property" name="property" v-model="review.property">
              <option :value="null"></option>
              <option
                :value="review.property && propertyOption.id === review.property.id ? review.property : propertyOption"
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
<script lang="ts" src="./review-update.component.ts"></script>
