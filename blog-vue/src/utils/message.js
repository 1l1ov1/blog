// message.js
import { ElMessage } from 'element-plus'

// 默认配置
const defaultOptions = {
  duration: 2000, // 消息显示的持续时间，单位为毫秒
  showClose: false, // 是否显示关闭按钮
  center: true, // 是否居中显示
  grouping: false, // 是否将相同类型的消息分组显示
}

// 消息类型枚举
const messageType = {
  SUCCESS: 'success', // 成功类型消息
  WARNING: 'warning', // 警告类型消息
  ERROR: 'error', // 错误类型消息
  INFO: 'info', // 信息类型消息
}

/**
 * 封装通用的消息提示函数
 * @param {string} message - 要显示的消息内容
 * @param {string} type - 消息类型，必须是 messageType 中的一种
 * @param {Object} [options={}] - 自定义配置选项，会覆盖默认配置
 * @returns {Object} - 返回 ElMessage 实例
 * @throws {Error} - 如果传入的消息类型无效，抛出错误
 */
const showMessage = (message, type, options = {}) => {
  // 检查消息类型是否有效
  if (!Object.values(messageType).includes(type)) {
    throw new Error(`无效的消息类型: ${type}`)
  }
  // 合并默认配置和自定义配置
  const finalOptions = {
    ...defaultOptions,
    message,
    type,
    ...options,
  }
  // 调用 ElMessage 显示消息
  return ElMessage(finalOptions)
}

/**
 * 显示成功类型的消息提示
 * @param {string} message - 要显示的消息内容
 * @param {Object} [options={}] - 自定义配置选项，会覆盖默认配置
 * @returns {Object} - 返回 ElMessage 实例
 */
export const showSuccessMessage = (message, options = {}) => {
  return showMessage(message, messageType.SUCCESS, options)
}

/**
 * 显示警告类型的消息提示
 * @param {string} message - 要显示的消息内容
 * @param {Object} [options={}] - 自定义配置选项，会覆盖默认配置
 * @returns {Object} - 返回 ElMessage 实例
 */
export const showWarningMessage = (message, options = {}) => {
  return showMessage(message, messageType.WARNING, options)
}

/**
 * 显示错误类型的消息提示
 * @param {string} message - 要显示的消息内容
 * @param {Object} [options={}] - 自定义配置选项，会覆盖默认配置
 * @returns {Object} - 返回 ElMessage 实例
 */
export const showErrorMessage = (message, options = {}) => {
  return showMessage(message, messageType.ERROR, options)
}

/**
 * 显示信息类型的消息提示
 * @param {string} message - 要显示的消息内容
 * @param {Object} [options={}] - 自定义配置选项，会覆盖默认配置
 * @returns {Object} - 返回 ElMessage 实例
 */
export const showInfoMessage = (message, options = {}) => {
  return showMessage(message, messageType.INFO, options)
}
