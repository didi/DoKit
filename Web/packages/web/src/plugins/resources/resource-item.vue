<template>
  <div class="resource-ltem">
    <div class="resource-preview" v-html="resourcePreview" @click="toggleDetail"></div>
    <div v-if="canShowDetail">
      <div class="resource-detail">
        <div class="type" v-html="detailType"></div>
        <div class="code" v-if="isCode" v-html="detailCode"></div>
        <div class="img-thumb" v-if="isImg" v-html="detailImgThumb"></div>
        <div class="img-size" v-if="isImg" v-html="detailImgSize"></div>
      </div>
      
    </div>
  </div>
</template>
<script>
import {ResourceMap, imgLoad, url2blobPromise,trimLeft} from './js/resources'


export default {
  components: {

  },
  props: {
    index: [Number],
    type: [Number],
    initiatorType: [String],
    entryName: [String],
    base64: [String],
  },
  data () {
    return {
      showDetail: false,
      codehtml: `Loading...`,
      imghtml: `Loading...`,
      sizehtml: `Loading...`,
      base64img:'',
    }
  },
  computed: {
    isImg(){
      return ResourceMap[this.type]==="img"
    },
    isCode(){
      return ResourceMap[this.type]==="css" || ResourceMap[this.type]==="script"
    },
    canShowDetail () {
      return this.showDetail
    },
    detailType(){
      let typehtml = `initiatorType:${this.initiatorType}`
      return typehtml
    },
    detailImgThumb(){
      let url;
      if(this.base64 !== ""){
        url = this.base64
      }else{
        url = this.entryName
      }
      this.imghtml = `<img src = "${url}" style="object-fit: cover;height:100px" />`
      return this.imghtml
    },
    detailImgSize(){
      let url;
      if(this.base64 !== ""){
        url = this.base64
      }else{
        url = this.entryName
      }
      imgLoad(url, (w, h) =>{
        this.sizehtml =  `${w}*${h}`
      })
      return this.sizehtml
    },
    detailCode(){
      url2blobPromise(this.entryName).then((res)=>{
          res = res.replace(/</g,'&lt;')
          res = res.replace(/>/g,'&gt;')
          let tmp = res
          tmp = tmp.split('\n')
          let len  = tmp.length
          let r = ``
          for(let i=0; i<len; i=i+1){
            tmp[i] = trimLeft(tmp[i])
            r += `<div class="codeline" style="display:flex;"><div class="codeindex" style="min-width: 40px;color: #1485ee;">${i+1}</div><div class="codevalue">${tmp[i]}</div></div>`
          }
          this.codehtml = r
          
        }).catch(()=>{
          this.codehtml = `fail to load resource`
        })
      return this.codehtml
    },
    resourcePreview () {
      let index = this.index
      let url = this.entryName
      let html = `<div>${index+1}.${url}</div>`
      return html
    },
    resourceDetail () {
     
      let type = ResourceMap[this.type]
      let url = this.entryName
      let initiatorType = this.initiatorType
      let typehtml = `<div class="type">initiatorType:${initiatorType}</div>`

      
      if(type === 'script' || type === 'css'){
        
        url2blobPromise(url).then((res)=>{
          res = res.replace(/</g,'&lt;')
          res = res.replace(/>/g,'&gt;')
          let tmp = res
          tmp = tmp.split('\n')
          let len  = tmp.length
          let r = ``
          for(let i=0; i<len; i=i+1){
            tmp[i] = trimLeft(tmp[i])
            r += `<div class="codeline"><div class="codeindex">${i+1}</div><div class="codevalue">${tmp[i]}</div></div>`
          }
          this.codehtml = `<div class="code">`+r+`</div>`
        }).catch(()=>{
          this.codehtml = `<div class="code">fail to load resource</div>`
        })
        return typehtml + this.codehtml
        
      }
      
      else if(type === 'img'){
        
        if(this.base64 !== ""){
          this.imghtml = `<img src = "${this.base64}" class = "img-thumb" />`
          imgLoad(this.base64, (w, h) =>{
            this.sizehtml =  `<div class="img-size">${w}*${h}</div>`
          })
        }
        else{
          this.imghtml = `<img src = "${url}" class = "img-thumb" />`
          imgLoad(url, (w, h) =>{
            this.sizehtml =  `<div class="img-size">${w}*${h}</div>`
          })
        }
        return typehtml+this.imghtml+this.sizehtml
      }
      else if(type === 'other'){
        typehtml = `<div class="type" style="border:0">initiatorType:${initiatorType}</div>`
        return typehtml
      }
      
    }
  },

  methods: {
    toggleDetail () {
      this.showDetail = !this.showDetail
    },
  }
}
</script>
<style lang="less" scoped>
  .resource-ltem{
    padding: 10px 20px;
    border-top: 1px solid #eee;
    text-align: left;
    font-size: 12px;
  }
  .resource-ltem:first-child {
    border: none;
  }
  .resource-ltem:nth-of-type(odd){
    background:#f3f3f3;
  }
  .resource-preview{
    
  }

  .resource-detail {
    margin: 5px 10px;
    border: 1px solid gray;
    text-align: center;
    .type  {
      padding: 5px 10px;
      font-size: 12px;
      text-align: center;
    }
    .code{
      border-top: 1px solid gray;
      padding: 10px 10px;
      max-height: 300px;
      overflow-y: scroll;
      text-align: left;
      .codeline{
        display: flex;
        flex: 1;
        .codeindex{
          min-width: 40px;
          color: #1485ee;
        }
        .codevalue{
          
        }
      }
    }
    .img-thumb{
      border-top: 1px solid gray;
      padding: 5px 0px;
      height: 100px;
    }
    .img-size{
      padding:5px 0px;
    }
  }
  
</style>