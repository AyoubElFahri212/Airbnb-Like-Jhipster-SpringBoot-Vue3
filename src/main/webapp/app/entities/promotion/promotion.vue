<template>
  <div>
    <h2 id="page-heading" data-cy="PromotionHeading">
      <span v-text="t$('jhipsterApp.promotion.home.title')" id="promotion-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('jhipsterApp.promotion.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'PromotionCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-promotion"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('jhipsterApp.promotion.home.createLabel')"></span>
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
              :placeholder="t$('jhipsterApp.promotion.home.search')"
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
    <div class="alert alert-warning" v-if="!isFetching && promotions && promotions.length === 0">
      <span v-text="t$('jhipsterApp.promotion.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="promotions && promotions.length > 0">
      <table class="table table-striped" aria-describedby="promotions">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('code')">
              <span v-text="t$('jhipsterApp.promotion.code')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'code'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('discountType')">
              <span v-text="t$('jhipsterApp.promotion.discountType')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'discountType'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('discountValue')">
              <span v-text="t$('jhipsterApp.promotion.discountValue')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'discountValue'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('validFrom')">
              <span v-text="t$('jhipsterApp.promotion.validFrom')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'validFrom'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('validUntil')">
              <span v-text="t$('jhipsterApp.promotion.validUntil')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'validUntil'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('maxUses')">
              <span v-text="t$('jhipsterApp.promotion.maxUses')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'maxUses'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('isActive')">
              <span v-text="t$('jhipsterApp.promotion.isActive')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'isActive'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="promotion in promotions" :key="promotion.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'PromotionView', params: { promotionId: promotion.id } }">{{ promotion.id }}</router-link>
            </td>
            <td>{{ promotion.code }}</td>
            <td v-text="t$('jhipsterApp.DiscountType.' + promotion.discountType)"></td>
            <td>{{ promotion.discountValue }}</td>
            <td>{{ formatDateShort(promotion.validFrom) || '' }}</td>
            <td>{{ formatDateShort(promotion.validUntil) || '' }}</td>
            <td>{{ promotion.maxUses }}</td>
            <td>{{ promotion.isActive }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'PromotionView', params: { promotionId: promotion.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'PromotionEdit', params: { promotionId: promotion.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(promotion)"
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
        <span id="jhipsterApp.promotion.delete.question" data-cy="promotionDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-promotion-heading" v-text="t$('jhipsterApp.promotion.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-promotion"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removePromotion()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="promotions && promotions.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./promotion.component.ts"></script>
