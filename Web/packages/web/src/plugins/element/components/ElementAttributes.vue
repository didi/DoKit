<template>
  <div class="dokit-attributes dokit-section">
    <h2>Attributes</h2>
    <div class="dokit-table-wrapper">
      <table>
        <tbody v-if="highlightnNode">
          <tr v-if="highlightnNode.attributes.length === 0">
            <td>Empty</td>
          </tr>
          <tr
            v-else
            v-for="(item, index) in highlightnNode.attributes"
            :key="index"
          >
            <td class="dokit-attribute-name-color">{{ item.name }}</td>
            <td
              class="dokit-string-color"
              v-if="item.name === 'href' || item.name === 'src'"
            >
              <a :href="item.value" target="_blank">{{ item.value }}</a>
            </td>
            <td class="dokit-string-color" v-else>
              {{ item.value }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    highlightnNode: Object,
  },
};
</script>

<style lang="less" scoped>
.dokit-attributes {
  font-size: 15px;
  margin-top: 10px;
  .dokit-table-wrapper {
    overflow-x: auto;
  }
  .dokit-attribute-name-color {
    color: #994500;
  }
  .dokit-string-color {
    color: #1a1aa6;
  }
  table {
    td {
      padding: 5px 10px;
      vertical-align: baseline;
    }
  }
}
</style>