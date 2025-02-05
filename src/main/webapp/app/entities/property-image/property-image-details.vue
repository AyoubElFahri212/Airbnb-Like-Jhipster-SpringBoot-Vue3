<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="propertyImage">
        <h2 class="jh-entity-heading" data-cy="propertyImageDetailsHeading">
          <span v-text="t$('jhipsterApp.propertyImage.detail.title')"></span> {{ propertyImage.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('jhipsterApp.propertyImage.imageUrl')"></span>
          </dt>
          <dd>
            <span>{{ propertyImage.imageUrl }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.propertyImage.isMain')"></span>
          </dt>
          <dd>
            <span>{{ propertyImage.isMain }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.propertyImage.caption')"></span>
          </dt>
          <dd>
            <span>{{ propertyImage.caption }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.propertyImage.property')"></span>
          </dt>
          <dd>
            <div v-if="propertyImage.property">
              <router-link :to="{ name: 'PropertyView', params: { propertyId: propertyImage.property.id } }">{{
                propertyImage.property.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="propertyImage.id"
          :to="{ name: 'PropertyImageEdit', params: { propertyImageId: propertyImage.id } }"
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

<script lang="ts" src="./property-image-details.component.ts"></script>
