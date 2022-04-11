<template>
  <div class="dokit-interface-item">
    <div class="dokit-interface-title" @click="showContent=!showContent">
      <span class="dokit-interface-title-text">{{interfaceItem.name}}</span>
      <div class="dokit-interface-title-opt" @click.stop="toggleInterfaceSwitch">
        <img v-if="!interfaceItem.checked" src="data:image/png;base64,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" alt="">
        <img v-else src="data:image/png;base64,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" alt="" srcset="">
      </div>
      <span class="dokit-interface-title-arrow">
        <span v-if="!showContent">▸</span>
        <span v-else>▾</span>
      </span>
    </div>
    <div class="dokit-interface-content" v-show="showContent">
      <div class="dokit-interface-sub-title">接口信息</div>
      <div class="dokit-interface-info">
        <DoRow class="dokit-interface-info-item">
          <DoCol :span=8>请求路径</DoCol>
          <DoCol :span=16>{{interfaceItem.path}}</DoCol>
        </DoRow>
        <DoRow class="dokit-interface-info-item">
          <DoCol :span=8>query</DoCol>
          <DoCol :span=16>{{interfaceItem.query}}</DoCol>
        </DoRow>
        <DoRow class="dokit-interface-info-item">
          <DoCol :span=8>body</DoCol>
          <DoCol :span=16>{{interfaceItem.body}}</DoCol>
        </DoRow>
        <DoRow class="dokit-interface-info-item">
          <DoCol :span=8>分类</DoCol>
          <DoCol :span=16>{{interfaceItem.categoryName}}</DoCol>
        </DoRow>
        <DoRow class="dokit-interface-info-item">
          <DoCol :span=8>创建人</DoCol>
          <DoCol :span=16>{{interfaceItem.owner.name}}</DoCol>
        </DoRow>
      </div>
      <div class="dokit-scene-list">
        <div class="dokit-interface-sub-title">场景选择</div>
        <div class="dokit-scene-item" :class="item.checked ? 'dokit-actived-scene': ''" v-for="(item, index) in interfaceItem.sceneList" :key="item._id" @click="setScene(index)">
          <span class="dokit-scene-checkbox"></span>
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
.dokit-interface-item{
  margin-bottom: 20px;
  border: 1px solid #d6e4ef;
  border-radius: 5px;
  overflow: hidden;
  .dokit-interface-title{
    padding: 0 8px;
    font-size: 18px;
    color: #2c405a;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-bottom: 1px solid #d6e4ef;
    img{
      width: 40px;
    }
    .dokit-interface-title-opt{
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-style: normal;
    }
    .dokit-interface-title-text{
      flex: 10;
    }
    .dokit-interface-title-arrow{
      flex: 1;
      text-align: right;
    }
  }
  .dokit-interface-sub-title{
    text-align: center;
    font-weight: bold;
  }
  .dokit-interface-content{
    .dokit-interface-info{
      font-size: 14px;
      .dokit-interface-info-item{
        padding: 5px 0;
      }
    }
    padding: 0 10px;
    .dokit-scene-list{
      border-top: 1px solid #eee;
      .dokit-scene-item{
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
        .dokit-scene-checkbox{
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
      .dokit-actived-scene {
        background: #337CC4;
        
          .dokit-scene-checkbox{
            background: #337CC4;
          }
      }
    }
  }
}
</style>