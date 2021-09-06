import ApiMock from './main.vue'
import {getGlobalData, RouterPlugin} from '@dokit/web-core'
import { getPartUrlByParam } from "@dokit/web-utils";
import { request } from './../../assets/util'

const mockBaseUrl = "https://www.dokit.cn";

const getCheckedInterfaceList = function (interfaceList) {
  return interfaceList.filter(i => i.checked)
}

// 将本地接口数据 和 线上数据合并  主要是确认哪些接口/场景已经在本地开启
// TODO: 性能有待优化
const getMergeData = function (list) {
  let localList = JSON.parse(localStorage.getItem('dokit-interface-list') || '[]')
  list.forEach(i => {
    localList.forEach(l => {
      if (i._id === l._id) {
        i.checked = l.checked
        i.sceneList.forEach(scene => {
          l.sceneList.forEach(s => {
            if (scene._id === s._id) {
              scene.checked = s.checked
            }
          })
        });
      }
    })
  })

  return list
}

export default new RouterPlugin({
  name: 'ApiMock',
  nameZh: '数据mock',
  component: ApiMock,
  icon: 'https://pt-starimg.didistatic.com/static/starimg/img/GEAC1clsH81623297652210.png',
  onProductReady(){
    let state = getGlobalData();
    // 获取接口数据
    fetch(
      `${mockBaseUrl}/api/app/interface?projectId=${state.productId}&isfull=1`,
      {
        mode: "cors",
      }
    )
      .then((response) => {
        return response.json();
      })
      .then((info) => {
        let list = (info.data && info.data.datalist) || [];
        list.forEach((element) => {
          element.checked = false;
          element.sceneList.forEach((scene, index) => {
            scene.checked = false;
            if (index === 0) {
              scene.checked = true;
            }
          });
        });
        
        state.interfaceList = getMergeData(list)
      });



      request.hookFetch({
        onBeforeFetch: (fetchArgs) => {
          let checkedInterfaceList = getCheckedInterfaceList(state.interfaceList)
          let url = fetchArgs[0];
          let path = `/`+getPartUrlByParam(url, 'path')
          let sceneId = ''
          checkedInterfaceList.forEach(i => {
            if(i.path === path) {
              i.sceneList.forEach(scene => {
                if(scene.checked) {
                  sceneId = scene._id
                }
              })
            }
          })
          sceneId && fetchArgs[1] && (fetchArgs[1].method = 'get') && (fetchArgs[1].headers && delete fetchArgs[1].headers)
          sceneId && (fetchArgs[0] = `${mockBaseUrl}/api/app/scene/${sceneId}`)
          return fetchArgs;
        },
        
      });
      request.hookXhr({
        onBeforeOpen: (args) => {
          let checkedInterfaceList = getCheckedInterfaceList(state.interfaceList)
          let url = args[1];
          let path = `/`+getPartUrlByParam(url, 'path')
          let sceneId = ''
          checkedInterfaceList.forEach(i => {
            if(i.path === path) {
              i.sceneList.forEach(scene => {
                if(scene.checked) {
                  sceneId = scene._id
                }
              })
            }
          })
          sceneId && (args[0] = 'get')
          sceneId && (args[1] = `${mockBaseUrl}/api/app/scene/${sceneId}`)
          return args;
        },
        onBeforeSetRequestHeader: (args, config) => {
          let checkedInterfaceList = getCheckedInterfaceList(state.interfaceList)
          let url = config.originRequestInfo.url;
          let path = `/`+getPartUrlByParam(url, 'path')
          let sceneId = ''
          checkedInterfaceList.forEach(i => {
            if(i.path === path) {
              i.sceneList.forEach(scene => {
                if(scene.checked) {
                  sceneId = scene._id
                }
              })
            }
          })
          if (sceneId) {
            return false
          }
          return args
        }
      });
  },
  onUnload(){}
})