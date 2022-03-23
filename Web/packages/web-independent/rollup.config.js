import vuePlugin from 'rollup-plugin-vue'
import postcssPlugin from 'rollup-plugin-postcss'
import resolve from 'rollup-plugin-node-resolve'
import replace from 'rollup-plugin-replace'
import {terser} from 'rollup-plugin-terser'
import rAlias from '@rollup/plugin-alias'
import commonjs from 'rollup-plugin-commonjs'
const path = require('path')

const extendPlugins = []
if(process.env.NODE_ENV === 'production'){
  extendPlugins.push(terser())
}

export default {
  input: path.join(__dirname, '../web/src/index.js'),
  output: {
    name: 'dokit',
    file: 'dist/dokit.js',
    format: 'iife',
  },
  plugins: [
    rAlias({
      entries:{
        "@common": path.join(__dirname, '../web/src/common')
      }
    }),
    vuePlugin(),
    replace({
      'process.env.NODE_ENV': JSON.stringify(process.env.NODE_ENV),
      'process.env.VUE_ENV': JSON.stringify('browser'),
      '__VUE_OPTIONS_API__': JSON.stringify(true),
      '__VUE_PROD_DEVTOOLS__': JSON.stringify(true)
    }),
    postcssPlugin(),
    resolve({ extensions: ['.vue'] }),
    commonjs(),
    ...extendPlugins
  ]
}