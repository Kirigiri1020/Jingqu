import http from './http.js'

export const getBanner = () => {
    return http('/user/getBanner')
}

export const getHomeList = () => {
    return http('/user/getHomeList')
}

export const login = (data) => {
    return http('/login', data, "POST")
}

export const getUserInfo = () =>{
    return http('/getUserInfo')
}

export const detailProject = () =>{
    return http('/detail/project')
}

export const projectInfo = (id) =>{
	return http('/project/info', { id }, "POST")
}

export const likeList = () =>{
    return http('/like/list')
}
