const getNodeLevel = (node, treeNum = 0, maxDepth = 3) => {
  if (treeNum > maxDepth) {
    return treeNum;
  }
  const children = Array.from(node.childNodes || []);
  if (node.nodeType !== Node.ELEMENT_NODE) {
    return treeNum;
  }
  if (children.length === 0) {
    return treeNum;
  }
  const levels = children.map(cNode => getNodeLevel(cNode, treeNum + 1, maxDepth));
  return Math.max(...levels);
};

const getText = (el) => {
  switch (el.nodeType) {
    case Node.ELEMENT_NODE: // element
      return el.innerHTML;
    case Node.TEXT_NODE: // text
      return el.nodeValue;
    default:
      return '';
  }
};

const getNodeText = (node, treeNum = 3) => {
  if (getNodeLevel(node) > treeNum) {
    return '';
  }

  return getText(node);
};

export default getNodeText;