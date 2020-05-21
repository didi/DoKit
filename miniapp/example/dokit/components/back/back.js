Component({
  properties: {
    top: {
      type: String,
      value: '0',
    }
  },
  methods: {
    onbackDokitEntry () {
      this.triggerEvent('return')
    }
  }
})
