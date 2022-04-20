<template>
  <transition name="alert-fade">
    <div id="dokit-toast"
         v-show="visible"
         class="dokit-dialog-tips dokit-dialog-center">
      {{message}}
    </div>
  </transition>
</template>
<script>
export default {
  data () {
    return {
      visible: false,
      message: ''
    }
  },
  methods: {
     showToast(){
        this.visible = true
     },
     closeToast(){
      this.visible = false
     },
     setMessage(val){
       this.message = val
     }
  },
}
</script>
<style lang="less" scoped>
.alert-fade-enter-active,
.alert-fade-leave-active {
  transition: opacity 0.3s;
}
.alert-fade-enter,
.alert-fade-leave-to {
  opacity: 0;
}
.dokit-dialog-tips {
  position: fixed;
  z-index: 100001;
  max-width: 70%;
  padding: 8px 12px;
  border-radius: 15px;
  white-space: nowrap;
  background-color: rgba(0, 0, 0, 0.5);
  box-shadow: 0px 8px 30px 0 rgba(0, 0, 0, 0.363);
  text-align: center;
  color: #ffffff;
}
.dokit-dialog-center {
  top: 20%;
  left: 50%;
  transform: translate(-50%, -50%);
}
</style>