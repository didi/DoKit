
<template>
  <div class="geo-portal">

    <div class="portal-textarea-container">
      <div class="portal-opt-area">
        <div id="check" class="opt-btn" @click="check">检测浏览器是否支持获取地理位置信息</div>
      </div>
    </div>

    <div class="portal-textarea-container">
      <div class="portal-opt-area">
        <div class="opt-btn" @click="getLocation">获取位置信息</div>
      </div>
    </div>

    
    <p id="output" class="portal-textarea">尚未获取位置...</p>
  </div>
</template>

<script> 

export default {
  data() {
  },

  created() {
  },

  methods: {
    check() {
        if (navigator.geolocation) {
            alert("您的浏览器支持获取地理位置信息。");
        } else {
            alert("您的浏览器不支持获取地理位置信息。");
        }
    },

    getLocation() {
        if (navigator.geolocation) {
            let text = document.getElementById('output');
            text.innerHTML = "正在获取位置信息......";
            navigator.geolocation.getCurrentPosition(
              function(position){
                var timestp = new Date(position.timestamp);
                var time = timestp.getFullYear()+"-"+(timestp.getMonth()+1) +"-"+timestp.getDate()+" "+timestp.getHours()+
                      ":"+timestp.getMinutes()+":"+timestp.getSeconds();
                text.innerHTML = "纬度：" + position.coords.latitude + "<br>经度：" + position.coords.longitude + "<br>获取时间：" + time;
              },
              function(error) {
                //var errorType = ['您拒绝共享位置信息', '获取不到位置信息', '获取位置信息超时'];
                text.innerHTML = "code:"+error.code + "<br>message:"+error.message;
              }
            );
        }
        else {
            alert("您的浏览器不支持获取地理位置信息。");
        }
    }
  }
};
</script>

<style lang="less" scoped>
.geo-portal {
  padding: 5px;
  textarea,input {
    font-size: 13px;
  }
  .portal-textarea-container{
    .portal-textarea{
      font-size: 13px;
      border-radius: 5px;
      box-sizing: border-box;
      width: 100%;
      border: 1px solid #d6e4ef;
      resize: vertical;
    }
    .portal-opt-area{
      margin-top: 5px;
      height: 32px;
      line-height: 32px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      .opt-btn{
        background-color: #337CC4;
        border-radius: 5px;
        font-size: 16px;
        width: 100%;
        text-align: center;
        color: #fff;
      }
    }
  }
}
</style>
