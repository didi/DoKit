<template>
  <div style="margin-top:20px;">
    <InfoCard :infoMap="storageMap" title="Cookie" @refresh="updateList" @clear="clear" @removeItem="removeItem"></InfoCard>
  </div>
</template>
<script>
import InfoCard from './info-card'
import { clearCookie, removeCookieItem, getCookieMap } from './js/storage'

export default {
  components: {
    InfoCard
  },
  data () {
    return {
      storageMap: {}
    }
  },
  created() {
    this.updateList()
  },
  methods: {
    updateList() {
      this.storageMap = getCookieMap()
    },
    removeItem(key) {
      removeCookieItem(key)
      this.updateList()
    },
    clear() {
      clearCookie()
      this.updateList()
    }
  },
}
</script>
<style lang="">
  
</style>