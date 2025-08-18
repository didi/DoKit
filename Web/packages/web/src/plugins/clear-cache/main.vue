<template>
  <div class="storage-plugin">
    <localStorage></localStorage>
    <div>
      <input type="checkbox" id="check1" value="localStorage" v-model="checkedItem" />
      <label for="check1">清除</label>
    </div>

    <sessionStorage></sessionStorage>
    <div>
      <input type="checkbox" id="check2" value="sessionStorage" v-model="checkedItem" />
      <label for="check2">清除</label>
    </div>

    <cookie></cookie>
    <div>
      <input type="checkbox" id="check3" value="cookie" v-model="checkedItem" />
      <label for="check3">清除</label>
    </div>

    <div>
      <input type="checkbox" id="checkall" v-model="checkedAll" @change="changeAllChecked()" />
      <label for="checkall">全选</label>
    </div>

    <div class="portal-textarea-container">
      <div class="portal-opt-area">
        <div class="opt-btn" @click="clearCache">清除缓存</div>
      </div>
    </div>
  </div>

</template>
<script>
import localStorage from './local-storage';
import sessionStorage from './session-storage';
import cookie from './cookie';
import { clearCookie, removeCookieItem, getCookieMap } from './js/storage'

export default {
  data() {
    return {
      checkedAll : false,
      checkedItem: [],
      checkedAttr: ["localStorage", "sessionStorage", "cookie"],
    }
  },

  methods:{
    changeAllChecked() {
      if (this.checkedAll) {
        this.checkedItem = this.checkedAttr;
      } else {
        this.checkedItem = [];
      }
    },
    clearCache(){
      if (window.confirm(`是否确认清除所选缓存？`)) {
        for(var i = 0; i < checkedItem.length; i++){
          if(this.checkedItem[i]=="localStorage"){
            window.localStorage.clear()
          }else if(this.checkedItem[i]=="sessionStorage"){
            window.sessionStorage.clear()
          }else{
            clearCookie()
          }
        }
      }
    }
  },

  watch: {
    checkedItem() {
      if (this.checkedItem.length == this.checkedAttr.length) {
        this.checkedAll = true;
      } else {
        this.checkedAll = false;
      }
    }
  },

  components: {
    localStorage,
    sessionStorage,
    cookie
  }
}
</script>

<style lang="less" scoped>
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
.storage-plugin{
  padding: 5px;
}
</style>