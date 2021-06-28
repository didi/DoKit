
import { Col, Row } from "./layout/index"

import './layout/col.css'
import './layout/row.css'

export default {
  install: app => {
    app.component('DoCol', Col)
    app.component('DoRow', Row)
  }
}