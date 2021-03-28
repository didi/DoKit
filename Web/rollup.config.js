import vuePlugin from 'rollup-plugin-vue'
import postcssPlugin from 'rollup-plugin-postcss'
import resolve from 'rollup-plugin-node-resolve'
import commonjs from 'rollup-plugin-commonjs'
import replace from 'rollup-plugin-replace'


export default {
  input: 'src/main.js',
  output: {
    name: 'dokit.js',
    file: 'dist/dokit.js',
    format: 'umd'
  },
  plugins: [
    vuePlugin({
      preprocessStyles: true
    }),
    replace({
      'process.env.NODE_ENV': JSON.stringify('development'),
      'process.env.VUE_ENV': JSON.stringify('browser')
    }),
    postcssPlugin(),
    resolve({ extensions: ['.vue'] }),
    commonjs()
  ]
}