// import {EventEmitter} from '@dokit/web-core'

class Connector {
  trigger(method, params) {
    this.emit(
      'message',
      JSON.stringify({
        method,
        params,
      })
    );
  }
}

export default new Connector();
