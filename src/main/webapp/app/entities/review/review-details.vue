<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="review">
        <h2 class="jh-entity-heading" data-cy="reviewDetailsHeading">
          <span v-text="t$('jhipsterApp.review.detail.title')"></span> {{ review.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('jhipsterApp.review.rating')"></span>
          </dt>
          <dd>
            <span>{{ review.rating }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.review.comment')"></span>
          </dt>
          <dd>
            <span>{{ review.comment }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.review.reviewDate')"></span>
          </dt>
          <dd>
            <span v-if="review.reviewDate">{{ formatDateLong(review.reviewDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.review.author')"></span>
          </dt>
          <dd>
            {{ review.author ? review.author.id : '' }}
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.review.property')"></span>
          </dt>
          <dd>
            <div v-if="review.property">
              <router-link :to="{ name: 'PropertyView', params: { propertyId: review.property.id } }">{{ review.property.id }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="review.id" :to="{ name: 'ReviewEdit', params: { reviewId: review.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./review-details.component.ts"></script>
