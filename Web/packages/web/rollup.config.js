import vuePlugin from 'rollup-plugin-vue'
import postcssPlugin from 'rollup-plugin-postcss'
import resolve from 'rollup-plugin-node-resolve'
import commonjs from 'rollup-plugin-commonjs'
import replace from 'rollup-plugin-replace'

import {
  terser
} from 'rollup-plugin-terser'
import rAlias from '@rollup/plugin-alias'
const path = require('path')

const extendPlugins = []
if (process.env.NODE_ENV === 'production') {
  extendPlugins.push(terser())
}

export default {
  input: 'src/index.js',
  output: {
    name: 'dokit',
    file: 'dist/dokit.js',
    globals: {
      vue: 'Vue',
    },
    format: 'iife'
  },
  external: ["vue"],
  plugins: [
    rAlias({
      entries: {
        "@common": path.join(__dirname, './src/common')
      }
    }),
    vuePlugin(),
    replace({
      'process.env.NODE_ENV': JSON.stringify(process.env.NODE_ENV),
      'process.env.VUE_ENV': JSON.stringify('browser')
    }),
    postcssPlugin(),
    resolve({
      extensions: ['.vue']
    }),
    commonjs(),
    ...extendPlugins
  ]
}