/**
 * 防抖函数，用于限制目标函数的调用频率
 * @param {Function} func - 需要防抖的目标函数，会在延迟结束后调用
 * @param {number} delay - 防抖延迟时间，单位毫秒
 * @returns {Function} 经过防抖处理的新函数
 */
function debounce(func, delay) {
  let timer = null

  return function (...args) {
    const context = this

    // 清除之前的定时器，重置计时周期
    if (timer) clearTimeout(timer)

    // 设置新的定时器，确保在延迟时间后执行目标函数
    // 使用 apply 保持原始函数的上下文和参数
    timer = setTimeout(() => {
      func.apply(context, args)
    }, delay)
  }
}

/**
 * 节流函数，用于限制目标函数的执行频率
 * 在指定延迟时间内，连续多次调用只会执行一次目标函数
 *
 * @param {Function} func - 需要被节流的原始函数
 * @param {number} delay - 节流延迟时间（单位：毫秒）
 * @returns {Function} - 包装后的节流函数，具有和原始函数相同的参数列表
 */
function throttle(func, delay) {
  let timer = null

  // 返回包装后的节流函数，保留原始函数的参数列表
  return function (...args) {
    const context = this

    // 仅在定时器未激活时创建新定时器
    if (!timer) {
      timer = setTimeout(() => {
        // 执行原始函数并清除定时器引用
        func.apply(context, args)
        timer = null
      }, delay)
    }
  }
}

export { debounce, throttle }
