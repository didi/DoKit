Component({
  properties: {
    projectId: {
      type: String,
      value: '',
    }
  },
  data: {
    curCom: 'dokit',
  },
  methods: {
      tooggleComponent(e) {
        const componentType = e.currentTarget.dataset.type || e.detail.componentType
          this.setData({
            curCom: componentType
          })
      }
  }
});
