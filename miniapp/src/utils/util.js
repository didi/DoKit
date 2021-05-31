const formatTime = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()

  return [year, month, day].map(formatNumber).join('/') + ' ' + [hour, minute, second].map(formatNumber).join(':')
}

const formatNumber = n => {
  n = n.toString()
  return n[1] ? n : '0' + n
}
const goToLink = url => {
  wx.navigateTo({
    url
  })
}

const getPartUrlByParam = (url, param) => {
  const reg = /^(?:([A-Za-z]+):)?(\/{0,3})([0-9.\-A-Za-z]+)(?::(\d+))?(?:\/([^?#]*))?(?:\?([^#]*))?(?:#(.*))?$/;
  const res = reg.exec(url)
  const fields = ['url', 'scheme', 'slash', 'host', 'port', 'path', 'query', 'hash'];
  return res[fields.indexOf(param)]
}

const search2Json = search => {
  if (search != undefined) {
    let o = {}
    const arr = search.split('&')
    arr.forEach(item => {
        const a = item.split('=')
        o = {
            [a[0]]: a[1]
        }
    })
    return o
  } else {
    return {}
  }
}

const isArray = list => {
  return Object.prototype.toString.call(list).slice(8, -1) == 'Array'
}

const deepClone = obj => {
  if(typeof obj !="object"){
      return obj
  }
  var newObj = {};
  for (var key in obj) {
    newObj[key] = deepClone(obj[key])
  }
  return newObj;
}
const obj2str = function(obj){
  if(obj === null) return 'null';
  let res = '';
  if(typeof obj !="object"){
      return res + String(obj);
  }
  res += '{\n ';
  for (var key in obj) {
    res += key + ':' + obj2str(obj[key]) +'\n';
  }
  var objSymbols = Object.getOwnPropertySymbols(obj);
  for(let i = 0; i < objSymbols.length; i++){
    res += String(objSymbols[i]) + ':' + obj2str(obj[objSymbols[i]]) + '\n';
  }
  res += '}';
  return res; 
}


module.exports = {
  formatTime: formatTime,
  goToLink,
  search2Json,
  getPartUrlByParam,
  isArray,
  deepClone,
  obj2str
}
