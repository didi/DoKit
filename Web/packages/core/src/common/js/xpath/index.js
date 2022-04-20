class XPath {
  static _findRootDocument(rootNode, defaultRoot) {
    if (rootNode.nodeType === Node.DOCUMENT_NODE) {
      return rootNode;
    }
    if (rootNode === defaultRoot) {
      return rootNode.ownerDocument;
    }
    return rootNode;
  }

  static index(input) {
    const parent = input.parentNode;
    if (!parent) {
      return null;
    }
    let child = parent.firstChild;
    if (!child) {
      return null;
    }
    let i = 0;
    while (child) {
      if (child.nodeType === Node.ELEMENT_NODE) {
        i += 1;
      }
      if (child === input) {
        break;
      }
      child = child.nextSibling;
    }
    return i;
  }

  static any() {
    return {
      name: '*',
      weight: 1,
      type: 'any',
    };
  }

  static nthChild(node, i) {
    return {
      name: `${node.name}:nth-child(${i})`,
      penalty: node.weight + 1,
      type: 'nth',
    };
  }

  static filterNth(node) {
    return node.name !== 'html' && !node.name.startsWith('#');
  }

  constructor(opts) {
    const defaultOpts = {
      root: document.body,
      tagName: name => true,
    };
    this._opts = Object.assign({}, defaultOpts, opts);
    this._rootDoc = XPath._findRootDocument(this._opts.root, defaultOpts.root);
  }

  bubbleUp(input) {
    const { root } = this._opts;
    const stack = [];
    let current = input;
    let i = 0;
    while (current && current !== root.parentElement) {
      let node = this.tagName(current) || XPath.any();
      const nth = XPath.index(current);
      if (nth && node.name !== 'html') {
        node = XPath.nthChild(node, nth);
      }

      node.level = i;
      stack.push(node);
      current = current.parentElement;
      i += 1;
    }
    return stack;
  }

  tagName(input) {
    const name = input.tagName.toLowerCase();
    const { tagName } = this._opts;
    if (tagName(name)) {
      return {
        name,
        weight: 2,
        type: 'tag',
      };
    }
    return null;
  }

  find(input) {
    const stack = this.bubbleUp(input);
    const selector = stack.sort((a, b) => b.level - a.level).reduce((selector, node) => {
      selector += `${node.name}>`;
      return selector;
    }, '');
    return selector.length ? selector.slice(0, selector.length - 1) : selector;
  }
}

export default function xpath(opts) {
  return new XPath(opts);
}
