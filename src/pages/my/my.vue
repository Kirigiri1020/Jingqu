<template>
	<view class="content">
		<view class="topBox">
			<view class="setBox">
				<view class="set-left">
					<uni-icons type="calendar" size="30" color="#fff"></uni-icons>
					<view class="txt">签到</view>
				</view>
				<view class="set-right">
					<uni-icons type="gear" size="30" color="#fff"></uni-icons>
					<uni-icons type="chat" size="30" color="#fff"></uni-icons>
				</view>
			</view>
			<view class="users" @click="setFun">
				<view class="u-top">
					<template v-if="!userInfo.nickName">
						<image src="../../static/logo.png" mode="aspectFill" />
						<view class="tit">
							注册/登录
						</view>
					</template>
					<template v-else>
						<image :src="userInfo.avatarUrl" mode="aspectFill" />
						<view class="tit">
							{{ userInfo.nickName }}
						</view>
					</template>
				</view>
				<view class="u-bottom">
					<view class="u-item">
						<view class="num">12</view>
						<view class="u-itit">点赞</view>
					</view>
					<view class="u-item">
						<view class="num">12</view>
						<view class="u-itit">喜欢</view>
					</view>
					<view class="u-item">
						<view class="num">12</view>
						<view class="u-itit">浏览</view>
					</view>
					<view class="u-item">
						<view class="num">12</view>
						<view class="u-itit">收藏</view>
					</view>
				</view>
			</view>
		</view>
		<view class="listBox">
			<view class="lists">
				<uni-list>
					<uni-list-item :show-extra-icon="true" :extra-icon="extraIcon1" showArrow title="个人信息" clickable @click="goToProfile"></uni-list-item>
					<uni-list-item :show-extra-icon="true" :extra-icon="extraIcon2" showArrow title="我的门票"clickable></uni-list-item>
					<uni-list-item :show-extra-icon="true" :extra-icon="extraIcon3" showArrow title="用户反馈"clickable></uni-list-item>
					<uni-list-item :show-extra-icon="true" :extra-icon="extraIcon4" showArrow title="我的邮件"clickable></uni-list-item>
					<uni-list-item :show-extra-icon="true" :extra-icon="extraIcon5" showArrow title="分享收藏"clickable></uni-list-item>
				</uni-list>
			</view>
		</view>
		<up-popup :show="show" @close="close" closeable round="20">
			<view class="popup">
				<view class="title">获取您的昵称和头像</view>
				<view class="flex">
					<view class="label">获取用户头像</view>
					<button class="avatar-warpper" open-type="chooseAvatar" @chooseavatar="onChooseavatar">
						<image class="avatar" :src=tempUserInfo.avatarUrl />
					</button>
				</view>
				<view class="flex">
					<view class="label">获取用户昵称</view>
					<input v-model="tempUserInfo.nickName" type="nickname" />
				</view>
			</view>
			<button size="default" type='primary' @click="userSubmit">确定</button>
		</up-popup>
	</view>
</template>
<script setup lang="ts">
import { ref, reactive } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { login, getUserInfo } from '../../api/api.js';
import uniList from '@dcloudio/uni-ui/lib/uni-list/uni-list.vue';
import uniListItem from '@dcloudio/uni-ui/lib/uni-list-item/uni-list-item.vue';

onLoad(async () => {
	// 只有在有token且有userInfo存储时才设置用户信息
	if (uni.getStorageSync('token') && uni.getStorageSync('userInfo')) {
		const { avatarUrl, nickName } = JSON.parse(uni.getStorageSync('userInfo'))
		userInfo.avatarUrl = avatarUrl
		userInfo.nickName = nickName
	}
})

const setFun = () => {
	// 如果已经登录且有用户信息，直接显示popup进行编辑
	if (userInfo.nickName) {
		// 复制当前用户信息到临时变量
		tempUserInfo.nickName = userInfo.nickName
		tempUserInfo.avatarUrl = userInfo.avatarUrl
		return
	}

	// 未登录，需要先登录
	uni.showModal({
		title: '温馨提示',
		content: '需要授权登录后才能正常使用',
		success(res) {
			if (res.confirm) {
				uni.login({
					success: async (data) => {
						console.log(data)
						const { token } = await login(data.code)
						console.log(token, 'token')
						uni.setStorageSync('token', token)
						const { avatarUrl, nickName } = await getUserInfo()
						// 不立即设置到userInfo，只在临时变量中存储
						// 重置昵称为空让用户重新输入，但保留头像
						tempUserInfo.avatarUrl = avatarUrl
						tempUserInfo.nickName = ''
						show.value = true
					}
				})
			}
		}
	})
}

const userInfo = reactive({
	nickName: '',
	avatarUrl: ''
})

// 临时存储popup中的修改
const tempUserInfo = reactive({
	nickName: '',
	avatarUrl: ''
})

const show = ref(false)

const userSubmit = () => {
	// 验证昵称不能为空或只有空格
	if (!tempUserInfo.nickName || tempUserInfo.nickName.trim() === '') {
		uni.showModal({
			title: '提示',
			content: '昵称不能为空',
			showCancel: false
		})
		return // 不关闭popup
	}

	// 应用临时修改到正式用户信息
	userInfo.nickName = tempUserInfo.nickName
	userInfo.avatarUrl = tempUserInfo.avatarUrl

	uni.setStorageSync('userInfo', JSON.stringify(userInfo))
	show.value = false
}

const close = () => {
	// 直接关闭popup，不进行任何保存操作，丢弃临时修改
	show.value = false
}

const onChooseavatar = (e) => {
  tempUserInfo.avatarUrl = e.detail.avatarUrl
}

// 跳转到个人信息页面
const goToProfile = () => {
  uni.navigateTo({
    url: `/pages/my/profile`
  })
}

//icon图标
const extraIcon1 = reactive({
	color: '#666666',
	size: '22',
	type: 'auth'
})

const extraIcon2 = reactive({
	color: '#666666',
	size: '22',
	type: 'cart'
})

const extraIcon3 = reactive({
	color: '#666666',
	size: '22',
	type: 'chatboxes'
})

const extraIcon4 = reactive({
	color: '#666666',
	size: '22',
	type: 'email'
})

const extraIcon5 = reactive({
	color: '#666666',
	size: '22',
	type: 'redo'
})


</script>

<style lang="scss" scoped>
.content {
	height: 100vh;
	background-color: #f5f5f5;

	.topBox {
		width: 100%;
		position: relative;
		z-index: 1;
		overflow: hidden;
		padding: 40rpx 20rpx 40rpx;
	}

	.topBox::after {
		content: "";
		width: 140%;
		height: 200px;
		position: absolute;
		top: 0;
		left: -20%;
		background-color: #00aaff;
		border-radius: 0 0 50% 50%;
		z-index: -1;
	}

	.setBox {
		display: flex;
		justify-content: space-between;
		align-items: center;

		.set-left {
			width: 18%;
			display: flex;
			justify-content: space-between;
			align-items: center;
		}

		.set-right {
			display: flex;
			align-items: center;
			width: 18%;
			justify-content: flex-end;
			gap: 15rpx;
			margin-right: 30rpx;
		}

		.txt {
			color: #fff;
			font-size: 30rpx;
		}
	}

	.users {
		margin-top: 35rpx;
		margin-right: 40rpx;
		padding: 30rpx;
		box-sizing: border-box;
		height: 280rpx;
		background-color: #fff;
		box-shadow: 1px 10rpx 20rpx #ececec;
		border-radius: 16rpx;

		.u-top {
			display: flex;
			justify-content: flex-start;
			align-items: center;
			margin-bottom: 30rpx;

			image {
				width: 100rpx;
				height: 100rpx;
				border-radius: 50%;
				margin-right: 20rpx;
			}

			.tit {
				font-size: 30rpx;
				font-weight: 700;
				color: #333;
			}
		}

		.u-bottom {
			display: flex;
			justify-content: space-around;
			align-items: center;

			.u-item {
				text-align: center;

				.u-tit {
					color: #757575;
					font-size: 26rpx;
					margin-top: 10rpx;
				}

				.num {
					color: #000;
					font-size: 33rpx;
					font-weight: 700;
				}
			}
		}
	}

	.popup {
		padding: 20rpx;
		border-radius: 20rpx 20rpx 0 0;

		.title {
			margin-bottom: 20rpx;
			font-size: 40rpx;
			font-size: 40rpx;
			text-align: center;
		}

		.flex {

			display: flex;
			justify-content: flex-start;
			align-items: center;
			border-bottom: 1px solid #f5f5f5;
			padding: 24rpx 0;

			input {
				margin-left: 20rpx;
			}
		}

		image {
			width: 70rpx;
			height: 70rpx;
		}

		.avatar-warpper {
			border: none;
			border-radius: 10rpx;
			width: 70rpx;
			height: 70rpx;
			margin-left: 20rpx;
			padding: 0;
		}
	}
	.listBox{
		height: 300rpx;
		margin: -10rpx auto 0;
		padding: 20rpx;
		box-sizing: border-box;
		border-radius: 12rpx;
	}
}
</style>
