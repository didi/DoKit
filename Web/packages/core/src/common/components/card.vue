<template>
  <div class="card">
    <div class="card-title">
      <span class="card-title-text"> {{title}} </span>
    </div>
    <div class="item-list">
      <div class="item" v-for="(item,index) in list" :key="index" @click="handleClickItem(item)">
        <div class="item-icon">
          <img
            class="item-icon-image"
            :src="item.icon || defaultIcon"
          />
        </div>
        <div class="item-title">{{item.title || '默认功能'}}</div>
      </div>
    </div>
  </div>
</template>
<script>
import {DefaultItemIcon} from '../js/icon'
export default {
  props: {
    title: {
      default: '专区'
    },
    list: {
      default: []
    }
  },
  data(){
    return {
      defaultIcon: DefaultItemIcon
    }
  },
  methods: {
    handleClickItem(item){
      this.$router.push({
        name: item.name
      })
    }
  }
};
</script>
<style lang="less" scoped>
.card {
  margin-bottom: 10px;
  padding: 10px;
  background-color: white;
}
.card-title-text {
  font-size: 16px;
  color: #333333;
}
.item-list {
  display: flex;
  flex-wrap: wrap;
  margin-top: 5px;
}
.item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 25%;
  margin-top: 5px;
  .item-icon-image {
    width: 30px;
  }
  .item-title {
    font-size: 14px;
    margin-top: 5px;
  }
}
</style>