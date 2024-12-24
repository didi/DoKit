<template>
  <div class="dokit-info-card">
    <div class="dokit-info-card-header">
      <div class="dokit-info-card-header__title">{{ title }}</div>
      <div class="dokit-info-card-header__opt">
        <div class="dokit-filter-box" :class="keyword ? 'dokit-filter-box-actived' : ''">
          <span class="dokit-filter-text">{{ keyword }}</span>
          <span class="dokit-filter dokit-opt-icon" @click="openPrompt"></span>
        </div>
        <span class="dokit-clear-all dokit-opt-icon" @click="clearAll"></span>
        <span class="dokit-refresh dokit-opt-icon" @click="refresh"></span>
      </div>
    </div>
    <div class="dokit-info-card-body">
      <table v-show="Object.keys(filteredMap).length">
        <tbody>
          <tr class="" v-for="(value, key) in filteredMap" :key="key">
            <td class="dokit-info-key">{{ key }}</td>
            <td class="dokit-info-value">{{ value }}</td>
            <td class="dokit-info-opt">
              <span class="dokit-info-delete" @click="removeItem(key)"></span>
            </td>
          </tr>
        </tbody>
      </table>
      <div class="dokit-empty" v-show="Object.keys(filteredMap).length === 0">
        <span>empty</span>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    title: {
      default: "",
    },
    infoMap: {
      default: {},
    },
  },
  data() {
    return {
      keyword: "",
    };
  },
  computed: {
    // 过滤地图
    filteredMap() {
      if (this.keyword) {
        let map = Object.create({})
        for (const key in this.infoMap) {
          if (Object.hasOwnProperty.call(this.infoMap, key)) {
            if(this.infoMap[key].indexOf(this.keyword) > -1 || key.indexOf(this.keyword) > -1) {
              map[key] = this.infoMap[key]
            }
          }
        }
        return map
      } else {
        return this.infoMap
      }
      
    }
  },
  methods: {
    /**
     * 
     * @param {关键字} key 
     */
    removeItem(key) {
      this.$emit("removeItem", key);
    },
    openPrompt() {
      this.keyword = window.prompt(
        "请输入过滤关键词",
        this.keyword ? this.keyword : ""
      );
    },
    clearAll() {
      if (window.confirm(`将清空所有${this.title}数据，是否确认清空？`)) {
        this.$emit("clear");
      }
    },
    /**
     * 刷新
     */
    refresh() {
      this.$emit("refresh");
    }
  },
};
</script>
<style lang="less">
.dokit-info-card {
  border-radius: 5px;
  background-color: #d9e1e8;
  overflow: hidden;
}

.dokit-info-card-header {
  border-bottom: 1px solid #eeeeee;
  background-color: #2b90d9;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;

  .dokit-info-card-header__title {
    padding: 5px;
    font-size: 14px;
    font-weight: bold;
    font-style: italic;
  }
  .dokit-info-card-header__opt {
    display: flex;
    .dokit-opt-icon {
      display: inline-block;
      width: 15px;
      height: 15px;
      background-size: 15px;
      background-repeat: no-repeat;
      margin: 0 10px;
    }
    .dokit-refresh {
      background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMgAAADICAMAAACahl6sAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAAgY0hSTQAAeiYAAICEAAD6AAAAgOgAAHUwAADqYAAAOpgAABdwnLpRPAAAAr5QTFRFAAAA////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////0dICkAAAAOl0Uk5TAAcuVXuiwcjQ2N/n7/b+8eXLv7KlmYx/USMgYJO54PXHaz4QndnhqghEjsqpWRR9zGUYZ7azVDbwxGY9oNZ4GajoiStMr/qS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');
    }
    .dokit-clear-all {
      background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMgAAADICAMAAACahl6sAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAAgY0hSTQAAeiYAAICEAAD6AAAAgOgAAHUwAADqYAAAOpgAABdwnLpRPAAAAvRQTFRFAAAA////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////ScVxIwAAAPt0Uk5TAB41BcjfTK71YpV5fJCmE0m9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');
    }
    .dokit-filter-box {
      padding-left: 10px;
      display: inline-flex;
      justify-content: space-between;
      align-items: center;
      .dokit-filter-text {
        font-size: 12px;
      }
    }
    .dokit-filter-box-actived {
      border-radius: 10px;
      border: 1px solid #d9e1e8;
    }
    .dokit-filter {
      background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMgAAADICAMAAACahl6sAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAAgY0hSTQAAeiYAAICEAAD6AAAAgOgAAHUwAADqYAAAOpgAABdwnLpRPAAAAspQTFRFAAAA////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////17LOWQAAAO10Uk5TADFiiK2/n3ZVFBCE99pmUta5KkH11DDhKbWsWTPFjlHoC387avCa8sDVtqYv4JfQozrroEVUXZ1P6hsS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');
    }
  }
}
.dokit-info-card-body {
  table {
    width: 100%;
  }
  td {
    padding: 5px;
    font-size: 12px;
  }
  .dokit-empty{
    padding: 10px;
    text-align: center;
    line-height: 20px;
    color: #9baec8;
    font-weight: bold;
  }
  .dokit-info-key {
    font-weight: bolder;
    max-width: 150px;
    overflow-x: scroll;
  }
  .dokit-info-value {
  }
  .dokit-info-opt {
    width: 15px;
    span {
      display: inline-block;
      width: 15px;
      height: 15px;
      background-image: url("data:image/png;base64,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");
      background-size: 15px;
      background-repeat: no-repeat;
    }
  }
}
</style>