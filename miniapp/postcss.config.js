module.exports = {
	plugins: [
		require('postcss-font-base64')({
			match: { 'Ionicons': ['ionicons'] }
		}),
		require('autoprefixer')
	]
}