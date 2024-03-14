<template>
  <div style="margin-top:20px;">
    <InfoCard :infoMap="storageMap" title="sessionStorage" @refresh="updateList" @clear="clear" @removeItem="removeItem"></InfoCard>
  </div>
</template>
<script>
import InfoCard from './info-card'
import {overrideSessionStorage} from './js/storage'

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
    overrideSessionStorage(() => {
      this.updateList()
    });
    this.updateList()
  },
  methods: {
    updateList() {
      let storageMap = {...window.sessionStorage}
      // 有一些属性不需要展示
      for (const key in storageMap) {
        if (Object.hasOwnProperty.call(storageMap, key)) {
          if (~key.indexOf('dokit') || typeof storageMap[key] !== 'string') delete storageMap[key]
        }
      }
      this.storageMap = storageMap
    },
    removeItem(key) {
      window.sessionStorage.removeItem(key)
    },
    clear() {
      window.sessionStorage.clear()
    }
  },
}
</script>
<style lang="">
  
</style>