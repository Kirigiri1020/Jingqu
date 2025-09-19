<template>
	<view class="detail">
		<up-navbar bg-color="#00000000" title="" :autoBack="true" left-icon-color="#fff"></up-navbar>
		<view class="d-con">
			<image :src="details.dt.img" mode="aspectFill" />
			<view class="d-content">
				<view class="j-con">
					<view class="tit" style="display: flex; align-items: center;">
						<text style="margin-right: 7px; font-size: 17px; line-height: 1;">{{ details.dt.title }}</text>
						<up-tag :text="details.dt.tag[1]" size="mini" shape="circle" style="margin-top: 1px; display: flex; align-items: center; justify-content: center;"></up-tag>
					</view>
					<view class="like-icon" @click="handleLikeClick">
						<up-icon 
							:name="likeStatus === 1 ? 'heart-fill' : 'heart'" 
							:color="likeStatus === 1 ? '#ff0000' : '#000000'"
							size="24"
						></up-icon>
					</view>
					<view class="jj">
						<view style="font-weight: 700;font-size: 14px;">景区介绍</view>
						<view class="nr">{{ details.dt.introduce }}</view>
					</view>
					<view class="j-con">
						<view class="jj" style="display: flex;align-items: center">
							<view style="font-weight: 700;font-size: 14px;margin-right: 10rpx">开放时间:</view>
							<view class="nr">{{ details.dt.times }}</view>
						</view>
					</view>
					<view class="j-con ls">
						<view class="tit" style="font-size: 34rpx">游玩推荐</view>
						<view class="jj tj-list">
							<view class="item" v-for="(item, index) in projectList" :key="index" @click="goLine(item)">
								<image :src="item.url" mode="aspectFill" />
								<view class="topFixed">
									{{ item.tag }}
								</view>
								<view class="infos">
									<view class="tit">
										{{ item.title }}
									</view>
									<view class="desc">
										<up-icon name="map" color="#9c9c9c" size="16"></up-icon>
										<text class="text">{{ item.desc }}</text>
									</view>
								</view>
							</view>
						</view>
					</view>
					
					<!-- 推荐景区部分 -->
					<view class="j-con ls" v-if="similarJingquList.length > 0">
						<view class="tit" style="font-size: 34rpx">推荐景区</view>
						<view class="scrollView">
							<up-scroll-list :indicator="true" indicatorColor="#fff0f0" indicatorActiveColor="#f56c6c">
								<view class="items" v-for="(item, index) in similarJingquList" :key="index">
									<image class="img" :src="item.image" mode="aspectFill" />
									<view class="title">{{ item.title }}</view>
								</view>
							</up-scroll-list>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
import { onLoad } from '@dcloudio/uni-app';
import { ref, reactive } from 'vue';
import { detailProject ,getliketag,changeliketag, jingqusimilar} from '../../api/api.js';

const projectList = ref([])
const likeStatus = ref(0) // 0: 不喜欢, 1: 喜欢
const similarJingquList = ref([]) // 相似景区列表

const details = reactive({
	dt: ''
})

const goLine = (item) =>{
	uni.navigateTo({
		url:`/pages/line/line?id=${item.id}&latitude=${item.latitude}&longitude=${item.longitude}&title=${encodeURIComponent(item.title)}`
	})
}

onLoad((opt) => {
	details.dt = JSON.parse(decodeURIComponent(opt.item))
	
	// 获取当前景区的喜欢状态
	const scenicId = details.dt.id
	getliketag(scenicId).then(res => {
		console.log("获取喜欢状态响应:", res)
		if (res && res.tag !== undefined) {
			likeStatus.value = res.tag
			console.log("设置likeStatus为:", likeStatus.value)
		} else {
			console.warn("喜欢状态响应格式不正确:", res)
			likeStatus.value = 0
		}
	}).catch(error => {
		console.error("获取喜欢状态失败:", error)
	})
	
	detailProject().then(res => {
		// 过滤项目列表，只显示belong属性与当前景区id相同的项目
		const currentScenicId = details.dt.id
		projectList.value = res.filter(item => item.belong === currentScenicId)
		console.log("filtered projectList", projectList.value)
	}).catch((error) => {
		console.error("获取项目列表失败:", error)
		projectList.value = []
	})
	
	// 获取相似景区推荐
	jingqusimilar(details.dt.id).then(res => {
		console.log("相似景区响应:", res)
		if (res && Array.isArray(res)) {
			// 只提取需要的image和title字段
			similarJingquList.value = res.map(item => ({
				image: item.image,
				title: item.title
			}))
			console.log("相似景区列表:", similarJingquList.value)
		} else {
			console.warn("相似景区响应格式不正确:", res)
			similarJingquList.value = []
		}
	}).catch(error => {
		console.error("获取相似景区失败:", error)
		similarJingquList.value = []
	})
})

// 处理爱心图标点击事件
const handleLikeClick = async () => {
	try {
		const scenicId = details.dt.id
		const res = await changeliketag(scenicId)
		console.log("切换喜欢状态响应:", res)
		if (res && res.tag !== undefined) {
			likeStatus.value = res.tag
			uni.showToast({
				title: likeStatus.value === 1 ? '已添加到喜欢' : '已取消喜欢',
				icon: 'success'
			})
		} else {
			console.warn("切换喜欢状态响应格式不正确:", res)
		}
	} catch (error) {
		console.error("切换喜欢状态失败:", error)
		uni.showToast({
			title: '操作失败',
			icon: 'error'
		})
	}
}


</script>

<style lang="scss" scoped>
.detail {
	background-color: #f5f5f5;

	.d-con {
		image {
			width: 100%;
			height: 600rpx;
		}

		.d-content {
			width: 100%;
			height: 700rpx;
			margin-top: -40rpx;
			background-color: #fff;
			padding: 35rpx 30rpx;
			box-sizing: border-box;
			border-radius: 0 30rpx;
			position: relative;
			z-index: 9;
		}

		.j-con {
			margin-top: 30rpx;
			margin-bottom: 30rpx;
			
			.like-icon {
				margin: 5rpx 0;
				padding: 8rpx;
				display: inline-block;
				border-radius: 50%;
				cursor: pointer;
				transition: all 0.3s ease;
				
				&:active {
					transform: scale(0.95);
				}
			}

			.tit {
				font-size: 36rpx;
				font-weight: 700;
				color: #111;
				margin-bottom: 10rpx
			}

			.jj {
				.nr {
					font-size: 26rpx;
					color: #8a8a8a;
					line-height: 40rpx;
				}
			}

			.tj-list {
				display: flex;
				flex-wrap: wrap;
				justify-content: space-between;

				.item {
					position: relative;
					width: 48%;
					margin-bottom: 20rpx;
					box-shadow: 1px 2px 3px #e5e5e5;
					border-top-left-radius: 20rpx;
					border-top-right-radius: 20rpx;
					overflow: hidden;

					.topFixed {
						position: absolute;
						top: 0;
						left: 0;
						border-top-left-radius: 20rpx;
						border-bottom-right-radius: 20rpx;
						background-color: #ffaa7f;
						color: #fff;
						text-align: center;
						font-size: 22rpx;
						padding: 5rpx 20rpx;
						box-sizing: border-box;
					}

					image {
						width: 100%;
						height: 200rpx;
					}

					.infos {
						padding: 10rpx 15rpx;

						.tit {
							font-size: 28rpx;
							font-weight: 700;
							color: #111;
							margin-bottom: 15rpx;
							text-overflow: ellipsis;
						}

						.desc {
							display: flex;
							justify-content: flex-start;
							align-items: center;

							.text {
								font-size: 26rpx;
								color: #8a8a8a;
							}
						}
					}
				}
			}
			
			// 推荐景区样式
			.scrollView {
				margin-top: 20rpx;
				
				.items {
					margin-right: 30rpx;
					text-align: center;
					flex-shrink: 0;  
				}
				
				.img {
					width: 320rpx;
					height: 200rpx;
					border-radius: 14rpx;
				}
				
				.title {
					font-size: 28rpx;
					font-weight: 600;
					color: #333;
					margin-top: 10rpx;
				}
			}
		}
	}
}
</style>
