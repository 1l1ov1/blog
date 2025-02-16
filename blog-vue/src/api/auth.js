import request from '@/utils/request'

export const getCaptcha = () => {
  return request.get('/auth/captcha', { responseType: 'blob' })
}
