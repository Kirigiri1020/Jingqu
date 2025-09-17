
import './api/mock.js'
import uniIcons from '@dcloudio/uni-ui/lib/uni-icons/uni-icons.vue'
import {
	createSSRApp
} from "vue";
import uviewPlus from 'uview-plus';
import App from "./App.vue";
export function createApp() {
	const app = createSSRApp(App)
	app.component('uni-icons', uniIcons)
	app.use(uviewPlus)
	return {
		app,
	};
}
