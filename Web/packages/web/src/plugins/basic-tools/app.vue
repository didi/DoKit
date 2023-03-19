<template>
  <div>
    <div class="header">
      <button @click="update()">刷新页面</button>
      <button @click="creatQrCode()">生成二维码</button>
    </div>
    <div id="qrcode"></div>
  </div>
</template>

<script>
import QRCode from 'qrcodejs2-fix'
export default {
  data(){
    return{
    }
  },
  methods:{
    update(){
      location.reload()
    },
    creatQrCode() {
      var qrcode = new QRCode(document.getElementById("qrcode"), {
        text: ' http://localhost:3000', // 需要转换为二维码的内容
        width: 100,
        height: 100,
        colorDark: '#000000',
        colorLight: '#ffffff',
        correctLevel: QRCode.CorrectLevel.H
      })
    },
  },
}
</script>



<style scoped>

</style>