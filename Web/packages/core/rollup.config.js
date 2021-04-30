import vuePlugin from 'rollup-plugin-vue'
import postcssPlugin from 'rollup-plugin-postcss'
import resolve from 'rollup-plugin-node-resolve'
import commonjs from 'rollup-plugin-commonjs'
import replace from 'rollup-plugin-replace'
import rAlias from '@rollup/plugin-alias'
import {terser} from 'rollup-plugin-terser'
const path = require('path')

const extendPlugins = []
if(process.env.NODE_ENV === 'production'){
  extendPlugins.push(terser())
}

export default {
  input: 'src/index.js',
  output: [
    {
      file: 'dist/index.js',
      format: 'es'
    }
  ],
  external: ['vue'],
  plugins: [
    rAlias({
      entries:{
        "@common": path.join(__dirname, './src/common'),
        "@store": path.join(__dirname, './src/store'),
        "@router": path.join(__dirname, './src/route')
      }
    }),
    vuePlugin({
      preprocessStyles: true
    }),
    replace({
      'process.env.NODE_ENV': JSON.stringify(process.env.NODE_ENV),
      'process.env.VUE_ENV': JSON.stringify('browser')
    }),
    postcssPlugin(),
    resolve({ extensions: ['.vue'] }),
    commonjs(),
    ...extendPlugins
  ]
}