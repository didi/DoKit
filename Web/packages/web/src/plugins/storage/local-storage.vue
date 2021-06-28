<template>
  <div>
    <InfoCard :infoMap="storageMap" title="localStorage" @refresh="updateList" @clear="clear" @removeItem="removeItem"></InfoCard>
  </div>
</template>
<script>
import InfoCard from './info-card'
import {overrideLocalStorage} from './js/storage'

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
    overrideLocalStorage(() => {
      this.updateList()
    });
    this.updateList()
  },
  methods: {
    updateList() {
      let storageMap = {...window.localStorage}
      // 有一些属性不需要展示
      for (const key in storageMap) {
        if (Object.hasOwnProperty.call(storageMap, key)) {
          if (~key.indexOf('dokit') || typeof storageMap[key] !== 'string') delete storageMap[key]
        }
      }
      this.storageMap = storageMap
    },
    removeItem(key) {
      window.localStorage.removeItem(key)
    },
    clear() {
      window.localStorage.clear()
    }
  },
}
</script>
<style lang="">
  
</style>