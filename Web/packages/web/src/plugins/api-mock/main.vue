<template>
  <div class="dokit-api-mock-plugin">
    <interfaceItem
      v-for="(interfaceItem, index) in interfaceList"
      :key="interfaceItem._id"
      :interfaceItem="interfaceItem"
      :interfaceIndex="index"
      @toggleInterfaceSwitch="toggleInterfaceSwitch"
      @setScene="setScene"
    ></interfaceItem>
  </div>
</template>
<script>
import interfaceItem from "./interface-item";

export default {
  components: {
    interfaceItem,
  },
  data() {
    return {
      interfaceList: this.$store.state.interfaceList
    }
  },
  created() {
    localStorage.setItem('dokit-interface-list', JSON.stringify(this.$store.state.interfaceList || []))
  },
  methods: {
    toggleInterfaceSwitch(info) {
      this.$store.state.interfaceList[info.interfaceIndex].checked = !this.interfaceList[
        info.interfaceIndex
      ].checked;

      localStorage.setItem('dokit-interface-list', JSON.stringify(this.$store.state.interfaceList))
    },
    setScene(info) {
      this.$store.state.interfaceList[info.interfaceIndex].sceneList.forEach((e, index) => {
        e.checked = false;
        if (index === info.sceneIndex) {
          e.checked = true;
        }
      });
      localStorage.setItem('dokit-interface-list', JSON.stringify(this.$store.state.interfaceList))
    },
  },
};
</script>
<style>
.dokit-api-mock-plugin {
  padding: 5px;
}
</style>