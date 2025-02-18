import axios from 'axios'
import { userStore } from '@/stores'
import responseCode from '@/enums/responseCode'
import { showErrorMessage, showSuccessMessage } from '@/utils/message'
const instance = axios.create({
  baseURL: '/api',
  timeout: 5000,
})

instance.defaults.withCredentials = true
// 添加请求拦截器
instance.interceptors.request.use(
  function (config) {
    // 在发送请求之前做些什么
    // 先得到用户信息
    const token = userStore.getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  function (error) {
    // 对请求错误做些什么
    return Promise.reject(error)
  },
)

// 添加响应拦截器
instance.interceptors.response.use(
  function (response) {
    // 2xx 范围内的状态码都会触发该函数。
    // 对响应数据做点什么
    if (response.data.type === 'image/jpeg') {
      return response
    }
    console.log(response)
    if (response.data.code !== responseCode.SUCCESS) {
      showErrorMessage(response.data.msg)
      return
    }
    return response.data
  },
  function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    return Promise.reject(error)
  },
)

export default instance
