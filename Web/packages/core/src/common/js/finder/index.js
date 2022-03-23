import cssesc from 'cssesc';

let Limit;
(function (Limit) {
  Limit[Limit.All = 0] = 'All';
  Limit[Limit.Two = 1] = 'Two';
  Limit[Limit.One = 2] = 'One';
}(Limit || (Limit = {})));
let config;
let rootDocument;
export default function (input, options) {
  if (input.nodeType !== Node.ELEMENT_NODE) {
    throw new Error('Can\'t generate CSS selector for non-element node type.');
  }
  if (input.tagName.toLowerCase() === 'html') {
    return 'html';
  }
  const defaults = {
    root: document.body,
    idName: name => true,
    className: (name, input) => true,
    tagName: name => true,
    attr: (name, value) => false,
    seedMinLength: 1,
    optimizedMinLength: 2,
    threshold: 1000,
  };
  config = Object.assign({}, defaults, options);
  rootDocument = findRootDocument(config.root, defaults);
  let path = bottomUpSearch(input, Limit.All, () => bottomUpSearch(input, Limit.Two, () => bottomUpSearch(input, Limit.One)));
  // console.log('bottomUpSearch:', path)
  if (path) {
    const optimized = sort(optimize(path, input));
    // console.log('optimized:', optimized)
    if (optimized.length > 0) {
      path = optimized[0];
    }
    // console.log('last path:', path)
    return selector(path);
  }

  throw new Error('Selector was not found.');
}
function findRootDocument(rootNode, defaults) {
  if (rootNode.nodeType === Node.DOCUMENT_NODE) {
    return rootNode;
  }
  if (rootNode === defaults.root) {
    return rootNode.ownerDocument;
  }
  return rootNode;
}
function bottomUpSearch(input, limit, fallback) {
  let path = null;
  const stack = [];
  let current = input;
  let i = 0;
  while (current && current !== config.root.parentElement) {
    let level = maybe(id(current)) || maybe(...attr(current)) || maybe(...classNames(current)) || maybe(tagName(current)) || [any()];
    const nth = index(current, level);
    // console.log('nth:', nth, 'level:', level)
    if (limit === Limit.All) {
      if (nth) {
        level = level.concat(level.filter(dispensableNth).map(node => nthChild(node, nth)));
      }
      // console.log('[limit all] after level:', level)
    } else if (limit === Limit.Two) {
      level = level.slice(0, 1);
      if (nth) {
        level = level.concat(level.filter(dispensableNth).map(node => nthChild(node, nth)));
      }
      // console.log('[limit Two] after level:', level)
    } else if (limit === Limit.One) {
      const [node] = level = level.slice(0, 1);
      if (nth && dispensableNth(node)) {
        level = [nthChild(node, nth)];
      }
      // console.log('[limit One] after level:', level)
    }
    for (const node of level) {
      node.level = i;
    }
    stack.push(level);
    if (stack.length >= config.seedMinLength) {
      path = findUniquePath(stack, fallback);
      if (path) {
        break;
      }
    }
    // console.log('stack:', stack)
    current = current.parentElement;
    i++;
  }
  if (!path) {
    path = findUniquePath(stack, fallback);
  }
  return path;
}
function findUniquePath(stack, fallback) {
  const paths = sort(combinations(stack));

  // console.log('findUniquePath:', paths)
  if (paths.length > config.threshold) {
    return fallback ? fallback() : null;
  }
  for (const candidate of paths) {
    if (unique(candidate)) {
      return candidate;
    }
  }
  return null;
}
function selector(path) {
  let node = path[0];
  let query = node.name;
  // console.log('selector:', node, path.slice(1))
  for (let i = 1; i < path.length; i++) {
    const level = path[i].level || 0;
    if (node.level === level - 1) {
      query = `${path[i].name} > ${query}`;
    } else {
      query = `${path[i].name} ${query}`;
    }
    node = path[i];
  }
  return query;
}
function penalty(path) {
  return path.map(node => node.penalty).reduce((acc, i) => acc + i, 0);
}
function unique(path) {
  const s = selector(path)
  const len = rootDocument.querySelectorAll(s).length
  // console.log('[unique] selector:', s, 'len:', len)
  switch (len) {
    case 0:
      throw new Error(`Can't select any node with this selector: ${selector(path)}`);
    case 1:
      return true;
    default:
      return false;
  }
}
function id(input) {
  const elementId = input.getAttribute('id');
  if (elementId && config.idName(elementId)) {
    return {
      name: `#${cssesc(elementId, { isIdentifier: true })}`,
      penalty: 0,
      type: 'id',
    };
  }
  return null;
}
function attr(input) {
  const attrs = Array.from(input.attributes).filter(attr => config.attr(attr.name, attr.value));
  return attrs.map(attr => ({
    name: `[${cssesc(attr.name, { isIdentifier: true })}="${cssesc(attr.value)}"]`,
    penalty: 0.5,
    type: 'attr',
  }));
}
function classNames(input) {
  const names = Array.from(input.classList)
    .filter((cName)=> config.className(cName, input));
  return names.map(name => ({
    name: `.${cssesc(name, { isIdentifier: true })}`,
    penalty: 1,
    type: 'className',
  }));
}
function tagName(input) {
  const name = input.tagName.toLowerCase();
  if (config.tagName(name)) {
    return {
      name,
      penalty: 2,
      type: 'tag',
    };
  }
  return null;
}
function any() {
  return {
    name: '*',
    penalty: 3,
    type: 'any',
  };
}
function index(input, level) {
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
    // hasSimilar(input, child, level[0])
    if (child.nodeType === Node.ELEMENT_NODE) {
      i++;
    }
    if (child === input) {
      break;
    }
    child = child.nextSibling;
  }
  return i;
}
function hasSimilar(input, compareNode, { type, name }) {
  if (type === 'className') {
    return Array.from(compareNode.classList).map(cName => cssesc(cName, { isIdentifier: true })).includes(name.slice(1));
  }
  if (type === 'tag') {
    return name === cssesc(compareNode.tagName.toLowerCase(), { isIdentifier: true });
  }
  return true;
}
function nthChild(node, i) {
  return {
    name: `${node.name}:nth-child(${i})`,
    penalty: node.penalty + 1,
    type: 'nth',
  };
}
function dispensableNth(node) {
  return node.name !== 'html' && !node.name.startsWith('#');
}
function maybe(...level) {
  const list = level.filter(notEmpty);
  if (list.length > 0) {
    return list;
  }
  return null;
}
function notEmpty(value) {
  return value !== null && value !== undefined;
}
function* combinations(stack, path = []) {
  if (stack.length > 0) {
    for (const node of stack[0]) {
      yield* combinations(stack.slice(1, stack.length), path.concat(node));
    }
  } else {
    yield path;
  }
}
function sort(paths) {
  return Array.from(paths).sort((a, b) => penalty(a) - penalty(b));
}
function* optimize(path, input) {
  // console.log('optimize start:', path.length > 2 && path.length > config.optimizedMinLength, path, input)
  if (path.length > 2 && path.length > config.optimizedMinLength) {
    // console.log('optimize start')
    for (let i = 1; i < path.length - 1; i++) {
      const newPath = [...path];
      newPath.splice(i, 1);
      // console.log('optimize newPath:', newPath)
      if (unique(newPath) && same(newPath, input)) {
        yield newPath;
        yield* optimize(newPath, input);
      }
    }
  }
}
function same(path, input) {
  return rootDocument.querySelector(selector(path)) === input;
}
