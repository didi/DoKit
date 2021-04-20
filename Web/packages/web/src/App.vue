<template>
  <div class="dokit-app">
    <div class="dokit-entry-btn" v-dragable @click="toggleContainer"></div>
    <transition name="slide-tools">
      <ToolsContainer v-show="showContainer"></ToolsContainer>
    </transition>
  </div>
    
</template>

<script>
import dragable from './directives/dragable'
import ToolsContainer from './components/ToolsContainer'

export default {
  components: {
    ToolsContainer
  },
  directives: {
    dragable
  },
  data () {
    return {
      showContainer: false
    }
  },
  methods: {
    toggleContainer () {
      this.showContainer = !this.showContainer
    }
  }
}

</script>

<style lang="less">
.dokit-app{
  font-family: Helvetica Neue,Helvetica,Arial,sans-serif;
}
.dokit-entry-btn{
  width: 50px;
  height: 50px;
  padding: 10px;
  box-sizing: border-box;
  background-image: url(//pt-starimg.didistatic.com/static/starimg/img/OzaetKDzHr1618905183992.png);
  background-size: 50px;
  background-position: center;
  background-repeat: no-repeat;
}

.slide-tools-enter-active {
  transition: all 0.1s ease-out;
}

.slide-tools-leave-active {
  transition: all 0.1s ease-in;
}

.slide-tools-enter-from,
.slide-tools-leave-to {
  height: 0;
}

</style>