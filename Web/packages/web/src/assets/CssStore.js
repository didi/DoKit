function formatStyle(style) {
  const ret = {}
  for (let i = 0, len = style.length; i < len; i++) {
    const name = style[i]
    if (style[name] === 'initial') continue
    ret[name] = style[name]
  }
  return ret
}

const elProto = Element.prototype

let matchesSel = function () {
  return false
}

if (elProto.webkitMatchesSelector) {
  matchesSel = (el, selText) => el.webkitMatchesSelector(selText)
} else if (elProto.mozMatchesSelector) {
  matchesSel = (el, selText) => el.mozMatchesSelector(selText)
}

export default class CssStore {
  constructor(el) {
    this._el = el
  }
  getComputedStyle() {
    const computedStyle = window.getComputedStyle(this._el)

    return formatStyle(computedStyle)
  }
  getMatchedCSSRules() {
    const ret = [];
    [].slice.call(document.styleSheets).forEach(styleSheet => {
      try {
        // Started with version 64, Chrome does not allow cross origin script to access this property.
        if (!styleSheet.cssRules) return
      } catch (e) {
        return
      }
      for (let key in styleSheet.cssRules) {
        let matchesEl = false
        let cssRule = styleSheet.cssRules[key]
        // Mobile safari will throw DOM Exception 12 error, need to try catch it.
        try {
          matchesEl = this._elMatchesSel(cssRule.selectorText)
          /* eslint-disable no-empty */
        } catch (e) {}

        if (!matchesEl) return

        ret.push({
          selectorText: cssRule.selectorText,
          style: formatStyle(cssRule.style),
        })
      }
    })

    return ret
  }
  _elMatchesSel(selText) {
    return matchesSel(this._el, selText)
  }
}