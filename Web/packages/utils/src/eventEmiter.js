export class EventEmitter{
  constructor(){
    this._events = {}
  }

  once(event, listener){
    let _this = this
    let tempListener = function(){
      listener.apply(_this, arguments)
      _this.off(event, tempListener)
    }
    _this.on(event, tempListener)
    return this
  }
  on(event, listener){
    this._events[event] = this._events[event] || []
    this._events[event].push(listener)

    return this
  }

  off(event, listener){
    if(!this._events[event] || this._events[event].length === 0){
      return
    }
    let index = this._events[event].indexOf(listener);
    if(index >= 0){
      this._events[event].splice(index, 1)
    }
  }
  emit(event, info){
    if(!this._events[event] || this._events[event].length === 0){
      return
    }
    this._events[event].forEach(listener => {
      listener.call(this, info)
    })
    return this
  }
}