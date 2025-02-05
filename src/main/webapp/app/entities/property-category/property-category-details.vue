<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="propertyCategory">
        <h2 class="jh-entity-heading" data-cy="propertyCategoryDetailsHeading">
          <span v-text="t$('jhipsterApp.propertyCategory.detail.title')"></span> {{ propertyCategory.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('jhipsterApp.propertyCategory.name')"></span>
          </dt>
          <dd>
            <span>{{ propertyCategory.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.propertyCategory.description')"></span>
          </dt>
          <dd>
            <span>{{ propertyCategory.description }}</span>
          </dd>
          <dt>
            <span v-text="t$('jhipsterApp.propertyCategory.property')"></span>
          </dt>
          <dd>
            <span v-for="(property, i) in propertyCategory.properties" :key="property.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'PropertyView', params: { propertyId: property.id } }">{{ property.id }}</router-link>
            </span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="propertyCategory.id"
          :to="{ name: 'PropertyCategoryEdit', params: { propertyCategoryId: propertyCategory.id } }"
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

<script lang="ts" src="./property-category-details.component.ts"></script>
