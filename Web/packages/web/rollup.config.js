import vuePlugin from 'rollup-plugin-vue'
import postcssPlugin from 'rollup-plugin-postcss'
import resolve from 'rollup-plugin-node-resolve'
import replace from 'rollup-plugin-replace'
import {terser} from 'rollup-plugin-terser'

const extendPlugins = []
if(process.env.NODE_ENV === 'production'){
  extendPlugins.push(terser())
}

export default {
  input: 'src/index.js',
  output: {
    name: 'dokit',
    file: 'dist/dokit.js',
    globals: {
      vue: 'Vue'
    },
    format: 'iife'
  },
  external: ["vue", "js"],
  plugins: [
    vuePlugin(),
    replace({
      'process.env.NODE_ENV': JSON.stringify(process.env.NODE_ENV),
      'process.env.VUE_ENV': JSON.stringify('browser')
    }),
    postcssPlugin(),
    resolve({ extensions: ['.vue'] }),
    ...extendPlugins
  ]
}