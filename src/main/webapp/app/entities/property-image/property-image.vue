<template>
  <div>
    <h2 id="page-heading" data-cy="PropertyImageHeading">
      <span v-text="t$('jhipsterApp.propertyImage.home.title')" id="property-image-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('jhipsterApp.propertyImage.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'PropertyImageCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-property-image"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('jhipsterApp.propertyImage.home.createLabel')"></span>
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
              :placeholder="t$('jhipsterApp.propertyImage.home.search')"
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
    <div class="alert alert-warning" v-if="!isFetching && propertyImages && propertyImages.length === 0">
      <span v-text="t$('jhipsterApp.propertyImage.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="propertyImages && propertyImages.length > 0">
      <table class="table table-striped" aria-describedby="propertyImages">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('imageUrl')">
              <span v-text="t$('jhipsterApp.propertyImage.imageUrl')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'imageUrl'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('isMain')">
              <span v-text="t$('jhipsterApp.propertyImage.isMain')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'isMain'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('caption')">
              <span v-text="t$('jhipsterApp.propertyImage.caption')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'caption'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('property.id')">
              <span v-text="t$('jhipsterApp.propertyImage.property')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'property.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="propertyImage in propertyImages" :key="propertyImage.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'PropertyImageView', params: { propertyImageId: propertyImage.id } }">{{
                propertyImage.id
              }}</router-link>
            </td>
            <td>{{ propertyImage.imageUrl }}</td>
            <td>{{ propertyImage.isMain }}</td>
            <td>{{ propertyImage.caption }}</td>
            <td>
              <div v-if="propertyImage.property">
                <router-link :to="{ name: 'PropertyView', params: { propertyId: propertyImage.property.id } }">{{
                  propertyImage.property.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'PropertyImageView', params: { propertyImageId: propertyImage.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'PropertyImageEdit', params: { propertyImageId: propertyImage.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(propertyImage)"
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
        <span
          id="jhipsterApp.propertyImage.delete.question"
          data-cy="propertyImageDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-propertyImage-heading" v-text="t$('jhipsterApp.propertyImage.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-propertyImage"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removePropertyImage()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="propertyImages && propertyImages.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./property-image.component.ts"></script>
