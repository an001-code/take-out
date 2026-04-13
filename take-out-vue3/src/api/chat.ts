import request from '@/utils/request'

/**
 * 聊天API - 使用统一的request工具，后端返回JSON
 * @param message 用户消息
 */
export const chatAPI = (message: string) => {
  return request({
    url: '/chat',  // 实际请求路径为 /api/chat，代理后变为 /admin/chat
    method: 'get',
    params: {
      msg: message
    }
  })
}

/**
 * 测试连接API
 */
export const testConnectionAPI = () => {
  return request({
    url: '/chat',
    method: 'get',
    params: {
      msg: 'test'
    }
  })
}