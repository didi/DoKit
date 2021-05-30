<template>
  <div class="storage-plugin">
    <localStorage></localStorage>
    <sessionStorage></sessionStorage>
    <cookie></cookie>
  </div>
</template>
<script>
import localStorage from './local-storage';
import sessionStorage from './session-storage';
import cookie from './cookie';

export default {
  components: {
    localStorage,
    sessionStorage,
    cookie
  }
}
</script>
<style>
.storage-plugin{
  padding: 5px;
}
</style>