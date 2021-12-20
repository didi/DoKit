<template>
  <div class="dokit-margin">
    <div class="dokit-label">margin</div>
    <div class="dokit-top">{{ highlightnNode.boxStyle.marginTop }}</div>
    <br />
    <div class="dokit-left">{{ highlightnNode.boxStyle.marginLeft }}</div>
    <div class="dokit-border">
      <div class="dokit-label">border</div>
      <div class="dokit-top">
        {{ highlightnNode.boxStyle.borderTopWidth }}
      </div>
      <br />
      <div class="dokit-left">
        {{ highlightnNode.boxStyle.borderLeftWidth }}
      </div>
      <div class="dokit-padding">
        <div class="dokit-label">padding</div>
        <div class="dokit-top">
          {{ highlightnNode.boxStyle.paddingTop }}
        </div>
        <br />
        <div class="dokit-left">
          {{ highlightnNode.boxStyle.paddingLeft }}
        </div>
        <div class="dokit-content">
          <span>{{ contentWidth }}</span
          >&nbsp;×&nbsp;<span>{{ contentHeight }}</span>
        </div>
        <div class="dokit-right">
          {{ highlightnNode.boxStyle.paddingRight }}
        </div>
        <br />
        <div class="dokit-bottom">
          {{ highlightnNode.boxStyle.paddingBottom }}
        </div>
      </div>
      <div class="dokit-right">
        {{ highlightnNode.boxStyle.borderRightWidth }}
      </div>
      <br />
      <div class="dokit-bottom">
        {{ highlightnNode.boxStyle.borderBottomWidth }}
      </div>
    </div>
    <div class="dokit-right">
      {{ highlightnNode.boxStyle.marginRight }}
    </div>
    <br />
    <div class="dokit-bottom">
      {{ highlightnNode.boxStyle.marginBottom }}
    </div>
  </div>
</template>

<script>
export default {
  props: {
    highlightnNode: Object,
    contentWidth: String | Number,
    contentHeight: String | Number,
  },
};
</script>

<style>
</style>