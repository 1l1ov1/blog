import request from '@/utils/request'

export const getCaptcha = () => {
  return request.get('/auth/captcha', { responseType: 'blob' })
}

export const login = (data) => {
  return request.post('/auth/login', data, {
    headers: {
      'x-captcha-key': data.captchaKey,
    },
  })
}
