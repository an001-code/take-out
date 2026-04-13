/**
 * 测试CORS问题的临时文件
 * 用于验证后端是否配置了CORS
 */

// 测试后端接口是否可访问
export const testBackendConnection = async () => {
  try {
    const response = await fetch('http://localhost:8081/admin/chat?msg=test', {
      method: 'GET',
      mode: 'cors', // 明确指定CORS模式
      headers: {
        'Content-Type': 'application/json',
      },
    })
    
    console.log('Response status:', response.status)
    console.log('Response headers:', response.headers)
    
    if (response.ok) {
      const text = await response.text()
      console.log('Response text:', text)
      return true
    } else {
      console.log('Response not OK')
      return false
    }
  } catch (error) {
    console.error('CORS test error:', error)
    return false
  }
}