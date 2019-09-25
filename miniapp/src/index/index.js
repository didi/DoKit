Component({
  methods: {
      jumpToDebug() {
          wx.navigateTo({
              url: '../../dist/debug/debug'
          })
      }
  }
});
