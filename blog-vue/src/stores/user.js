import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore(
  'user',
  () => {
    const user = ref({})

    const setUser = (data) => {
      if (typeof data !== 'object' || data === null) {
        console.error('Invalid data type. Expected an object.')
        return
      }
      user.value = data
    }

    const getUser = () => {
      return user.value
    }

    const clearUser = () => {
      user.value = {}
    }

    const getToken = () => {
      return user.value.token
    }

    const setToken = (token) => {
      // 输入验证：确保 token 是字符串
      if (typeof token !== 'string') {
        console.error('Invalid token format, expected a string')
        return
      }
      user.value.token = token
    }

    return {
      setUser,
      getUser,
      clearUser,
      getToken,
      setToken,
    }
  },
  {
    // 保存到浏览器的localStorage
    persist: {
      key: 'userInfo', // 存储的键名
      storage: localStorage, // 存储的方式
    },
  },
)
