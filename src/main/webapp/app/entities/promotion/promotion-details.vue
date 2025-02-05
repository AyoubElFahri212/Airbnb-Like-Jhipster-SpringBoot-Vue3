<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="promotion">
        <h2 class="jh-entity-heading" data-cy="promotionDetailsHeading">
          <span v-text="t$('jhipsterApp.promotion.detail.title')"></span> {{ promotion.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('jhipsterApp.promotion.code')"></span>
          </dt>
          <dd>
            <span>{{ promotion.code }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.promotion.discountType')"></span>
          </dt>
          <dd>
            <span v-text="t$('jhipsterApp.DiscountType.' + promotion.discountType)"></span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.promotion.discountValue')"></span>
          </dt>
          <dd>
            <span>{{ promotion.discountValue }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.promotion.validFrom')"></span>
          </dt>
          <dd>
            <span v-if="promotion.validFrom">{{ formatDateLong(promotion.validFrom) }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.promotion.validUntil')"></span>
          </dt>
          <dd>
            <span v-if="promotion.validUntil">{{ formatDateLong(promotion.validUntil) }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.promotion.maxUses')"></span>
          </dt>
          <dd>
            <span>{{ promotion.maxUses }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.promotion.isActive')"></span>
          </dt>
          <dd>
            <span>{{ promotion.isActive }}</span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="promotion.id"
          :to="{ name: 'PromotionEdit', params: { promotionId: promotion.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./promotion-details.component.ts"></script>
