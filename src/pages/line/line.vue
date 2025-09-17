<template>
	<view class="line">
		<view class="mapBox">
			<map v-if="detailInfo.id && detailInfo.location && detailInfo.location.length >= 2" style="width: 100%;height: 900rpx;" :markers="detailInfo.markers"
				:latitude="detailInfo.location[0]" :longitude="detailInfo.location[1]" :show-scale="true"></map>
			<view v-else class="map-placeholder">
				<text>地图加载中...</text>
			</view>
		</view>
		<view class="infos">
			<view class="tit">
				当前游玩项目:{{ detailInfo.title }}
			</view>
			<view class="stars">
				<text>项目推荐</text>
				<up-rate :count="count" v-model="detailInfo.count"></up-rate>
			</view>
			<view class="scrollView" v-if="detailInfo.similiar && detailInfo.similiar.length > 0">
				<up-scroll-list :indicator="true" indicatorColor="#fff0f0" indicatorActiveColor="#f56c6c">
					<view class="items" v-for="(item, index) in detailInfo.similiar" :key="index">
						<image class="img" :src="item.image" mode="aspectFill" />
						<view class="title">{{ item.title }}</view>
					</view>
				</up-scroll-list>
			</view>
			<view v-else class="no-similar">
				<text>没有相似项目</text>
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
import { onLoad } from '@dcloudio/uni-app';
import { projectInfo } from '../../api/api.js'
import { ref } from "vue"

const detailInfo = ref({})

const count = ref(5)

onLoad((props) => {
	console.log("页面参数:", props)
	// 获取当前项目的详细信息
	projectInfo(props.id).then((data) => {
		console.log("API response:", data)
		// 使用传递的经纬度参数，如果没有则使用默认值
		if (props.latitude && props.longitude) {
			data.location = [parseFloat(props.latitude), parseFloat(props.longitude)]
		} else if (data.address && Array.isArray(data.address)) {
			// 如果没有传递经纬度，但API返回了address数组，使用address作为位置
			data.location = data.address.map(coord => parseFloat(coord))
		} else if (!data.location) {
			// 如果没有location和address，设置一个默认的北京位置
			data.location = [39.916344, 116.397155]
		}
		
		// 确保有markers数组
		if (!data.markers) {
			data.markers = [{
				id: 0,
				latitude: data.location[0],
				longitude: data.location[1],
				title: data.title || "项目位置"
			}]
		}
		
		
		detailInfo.value = data
		
	}).catch((error) => {
		console.error("API请求失败:", error)
		// 如果API请求失败，尝试从detail页面传递的数据中获取项目名称
		let projectTitle = "游玩项目"
		
		// 尝试从页面参数中获取项目名称
		if (props.title) {
			projectTitle = decodeURIComponent(props.title)
		}
		
		// 使用传递的经纬度创建基本数据
		const fallbackData = {
			id: props.id,
			title: projectTitle,
			count: 4,
			similiar: [],
			markers: []
		}
		if (props.latitude && props.longitude) {
			fallbackData.location = [parseFloat(props.latitude), parseFloat(props.longitude)]
			fallbackData.markers = [{
				id: 0,
				latitude: parseFloat(props.latitude),
				longitude: parseFloat(props.longitude),
				title: projectTitle
			}]
		} else {
			// 默认的北京位置
			fallbackData.location = [39.916344, 116.397155]
			fallbackData.markers = [{
				id: 0,
				latitude: 39.916344,
				longitude: 116.397155,
				title: projectTitle
			}]
		}
		detailInfo.value = fallbackData
	})
})
</script>

<style lang="scss" scoped>
.infos{
	padding: 20rpx;
	box-sizing: border-box;
	.tit{
		font-size: 34rpx;
		font-weight: 600;
		color: #333;

	}
	.stars{
		font-size: 20rpx;
		margin: 20rpx 0 40rpx;
		display: flex;
	}
	.items{
		margin-right: 30rpx;
		text-align: center;
		flex-shrink: 0;  
	}
	.img{
		width: 320rpx;
		height: 200rpx;
		border-radius: 14rpx;
	}
.title{
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}
}

.map-placeholder {
	width: 100%;
	height: 900rpx;
	display: flex;
	justify-content: center;
	align-items: center;
	background-color: #f5f5f5;
	color: #999;
	font-size: 28rpx;
}

.no-similar {
	text-align: center;
	padding: 40rpx 0;
	color: #999;
	font-size: 28rpx;
}
</style>
