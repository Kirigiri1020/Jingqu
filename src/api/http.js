let baseUrl = ''
if (process.env.NODE_ENV === 'development') {
	//开发环境
	//baseUrl = 'http://localhost:5173/api'
	baseUrl = 'http://192.168.137.232:8083/api'
}
else {
	baseUrl = 'http://159.75.169.224:4200/api'
}


export default function http(url, data = {}, method = "GET") {
	return new Promise((resolve, reject) => {
		uni.request({
			url: baseUrl + url,
			data,
			method,
			header: {
				'token': uni.getStorageSync('token') || ''
			},
			success: res => {
				console.log('API Response:', res)
				if (res.statusCode == 200) {
					if (res.data.code == 1) {
						resolve(res.data.data)
					} else if (res.data.code == 0) {
						uni.showToast({
							title: res.data.msg,
							icon: 'none'
						})
						reject(res.data.msg)
					} else {
						console.warn('Unexpected response code:', res.data.code)
						reject('Unexpected response code: ' + res.data.code)
					}
				} else {
					console.warn('Unexpected status code:', res.statusCode)
					reject('Unexpected status code: ' + res.statusCode)
				}
			},
			fail: (err) => {
				console.error('Request failed:', err)
				uni.showToast({
					title: '服务器请求异常',
					icon: 'none'
				})
				reject(err)
			}
		})
	})
}
