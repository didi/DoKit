<template>
  <div class="interface-item">
    <div class="interface-title" @click="showContent=!showContent">
      <span class="interface-title-text">{{interfaceItem.name}}</span>
      <div class="interface-title-opt" @click.stop="toggleInterfaceSwitch">
        <img v-if="!interfaceItem.checked" src="https://pt-starimg.didistatic.com/static/starimg/img/HJKp5ITImv1623845535156.png" alt="">
        <img v-else src="https://pt-starimg.didistatic.com/static/starimg/img/rCPATxI2Wv1623845535211.png" alt="" srcset="">
      </div>
      <span class="interface-title-arrow">
        <span v-if="!showContent">▸</span>
        <span v-else>▾</span>
      </span>
    </div>
    <div class="interface-content" v-show="showContent">
      <div class="interface-sub-title">接口信息</div>
      <div class="interface-info">
        <div class="interface-info-item">
          <span>请求路径</span>
          <span>{{interfaceItem.path}}</span>
        </div>
        <div class="interface-info-item">
          <span>query</span>
          <span>{{interfaceItem.query}}</span>
        </div>
        <div class="interface-info-item">
          <span>body</span>
          <span>{{interfaceItem.body}}</span>
        </div>
        <div class="interface-info-item">
          <span>分类</span>
          <span>{{interfaceItem.categoryName}}</span>
        </div>
        <div class="interface-info-item">
          <span>创建人</span>
          <span>{{interfaceItem.owner.name}}</span>
        </div>
      </div>
      <div class="scene-list">
        <div class="interface-sub-title">场景选择</div>
        <div class="scene-item" :class="item.checked ? 'actived-scene': ''" v-for="(item, index) in interfaceItem.sceneList" :key="item._id" @click="setScene(index)">
          <span class="scene-checkbox"></span>
          {{item.name}}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    interfaceIndex: [Number],
    interfaceItem: [Object]
  },
  data() {
    return {
      showContent: false
    }
  },
  methods: {
    toggleInterfaceSwitch() {
      this.$emit('toggleInterfaceSwitch', {
        interfaceIndex: this.interfaceIndex
      })
    },
    setScene(index) {
      this.$emit('setScene', {
        interfaceIndex: this.interfaceIndex,
        sceneIndex: index
      })
    }
  },
}
</script>
<style lang="less">
.interface-item{
  margin-bottom: 20px;
  background-color: #d9e1e8;
  border-radius: 5px;
  box-shadow: 0 8px 12px #ebedf0;
  overflow: hidden;
  .interface-title{
    padding: 0 10px;
    font-size: 18px;
    line-height: 34px;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-bottom: 1px solid #eee;
    img{
      width: 40px;
    }
    .interface-title-opt{
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-style: normal;
    }
    .interface-title-text{
      flex: 10;
    }
    .interface-title-arrow{
      flex: 1;
      text-align: right;
    }
  }
  .interface-sub-title{
    text-align: center;
    font-weight: bold;
  }
  .interface-content{
    padding: 0 10px;
    .interface-info-item{
      display: flex;
      span{
        flex: 1;
      }
    }
    .scene-list{
      border-top: 1px solid #eee;
      .scene-item{
        margin: 5px 0;
        padding: 0 10px;
        border-radius: 5px;
        border: 1px solid #fff;
        background: #cccccc;
        color: white;
        height: 30px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        .scene-checkbox{
          display: inline-block;
          box-sizing: border-box;
          line-height: 30px;
          width: 20px;
          height: 20px;
          border-radius: 10px;
          background: #eee;
          border: 5px solid #fff;
          font-size: 18px;
        }
      }
      .actived-scene {
        background: rgb(8, 158, 8);
        
          .scene-checkbox{
            background: rgb(8, 158, 8);
          }
      }
    }
  }
}
</style>