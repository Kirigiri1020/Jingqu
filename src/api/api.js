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

export const getUserInfo = () => {
    return http('/getUserInfo')
}

export const detailProject = () => {
    return http('/detail/project')
}

export const projectInfo = (id) => {
    return http('/project/info', { id }, "POST")
}

export const likeList = () => {
    return http('/like/list')
}

export const getliketag = (id) => {
    return http('/like/gettag', { id }, "POST")
}

export const changeliketag = (id) => {
    return http('/like/changetag', { id }, "POST")
}

export const isadmin = () => {
    return http('/admin')
}

export const getalluserinfo = () => {
    return http('/admin/users/info')
}

export const deleteuserinfo = (id) => {
    return http('/admin/user/delete', {id}, "POST")
}

export const updateeuserinfo = (data) => {
  return http('/admin/user/update', data, "POST")
}

export const getalljingquinfo = () => {
    return http('/admin/jingqu/info')
}

export const addjingquinfo = (data) => {
    return http('/admin/jingqu/add', data, "POST")
}

export const deletejingquinfo = (id) => {
    return http('/admin/jingqu/delete', {id}, "POST")
}

export const updatejingquinfo = (data) => {
    return http('/admin/jingqu/update', data, "POST")
}

export const getallprojectinfo = () => {
    return http('/admin/project/info')
}

export const addprojectinfo = (data) => {
  return http('/admin/project/add', data, "POST")
}

export const deleteprojectinfo = (id) => {
  return http('/admin/project/delete', {id}, "POST")
}

export const updatejprojectinfo = (data) => {
  return http('/admin/project/update', data, "POST")
}

export const jingqusimilar = (id) => {
  return http('/jingqu/similar', {id}, "POST")
}

export const getcountList = () => {
    return http('/user/getcountList')
}