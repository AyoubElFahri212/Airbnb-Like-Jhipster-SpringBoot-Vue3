<template>
  <div>
    <b-dropdown-item to="/property">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.property')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/country">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.country')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/city">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.city')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/amenity">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.amenity')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/property-category">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.propertyCategory')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/property-image">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.propertyImage')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/booking">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.booking')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/review">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.review')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/promotion">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.promotion')"></span>
    </b-dropdown-item>
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>
