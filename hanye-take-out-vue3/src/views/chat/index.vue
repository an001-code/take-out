<template>
  <div class="chat-container">
    <div class="chat-header">
      <h2>AI助手</h2>
      <el-button
        type="danger"
        size="small"
        @click="clearChat"
        :disabled="isLoading"
      >
        清空对话
      </el-button>
    </div>

    <div class="chat-content" ref="chatContentRef">
      <div
        v-for="(message, index) in messages"
        :key="index"
        :class="['message', message.role]"
      >
        <div class="message-avatar">
          <el-avatar
            :size="32"
            :src="message.role === 'user' ? userAvatar : aiAvatar"
          />
        </div>
        <div class="message-content">
          <div class="message-bubble">
            <div v-if="message.role === 'user'" class="user-message">
              {{ message.content }}
            </div>
            <div v-else class="ai-message">
              {{ message.content }}
            </div>
          </div>
        </div>
      </div>
      
      <div v-if="isLoading" class="message ai">
        <div class="message-avatar">
          <el-avatar :size="32" :src="aiAvatar" />
        </div>
        <div class="message-content">
          <div class="message-bubble">
            <div class="ai-message">
              <div class="loading-indicator">
                <span>AI正在思考中</span>
                <span class="loading-dots">...</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="chat-input-area">
      <div class="input-container">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          placeholder="请输入您的问题..."
          :disabled="isLoading"
          @keydown.enter.exact.prevent="sendMessage"
          maxlength="1000"
          show-word-limit
        />
        <div class="input-actions">
          <el-button
            type="primary"
            @click="sendMessage"
            :disabled="!inputMessage.trim() || isLoading"
            :loading="isLoading"
          >
            发送
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { chatAPI, testConnectionAPI } from '@/api/chat'

interface Message {
  role: 'user' | 'ai'
  content: string
  timestamp: number
  isStreaming?: boolean
}

const messages = ref<Message[]>([])
const inputMessage = ref('')
const isLoading = ref(false)
const chatContentRef = ref<HTMLElement>()

const userAvatar = '/src/assets/image/user_default.png'
const aiAvatar = '/src/assets/image/logo.png'

const scrollToBottom = () => {
  nextTick(() => {
    if (chatContentRef.value) {
      chatContentRef.value.scrollTop = chatContentRef.value.scrollHeight
    }
  })
}

// 解码Unicode字符串（如 \u4F60\u597D -> 你好）
const decodeUnicode = (str: string): string => {
  try {
    // 使用JSON.parse来解码Unicode转义序列
    return JSON.parse('"' + str.replace(/"/g, '\\"') + '"')
  } catch (e) {
    // 如果解码失败，返回原字符串
    return str
  }
}

const sendMessage = async () => {
  if (!inputMessage.value.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }

  const userMessage: Message = {
    role: 'user',
    content: inputMessage.value.trim(),
    timestamp: Date.now()
  }

  messages.value.push(userMessage)
  const userInput = inputMessage.value
  inputMessage.value = ''
  isLoading.value = true
  
  scrollToBottom()

  try {
    // 调用API获取AI回复
    const response = await chatAPI(userInput)
    
    // 直接取data字段显示
    const content = response.data.data || '无回复内容'
    
    const aiMessage: Message = {
      role: 'ai',
      content: content,
      timestamp: Date.now()
    }
    messages.value.push(aiMessage)
    
  } catch (error: any) {
    console.error('发送消息失败:', error)
    
    // 显示错误信息
    const errorMessage: Message = {
      role: 'ai',
      content: error.response?.data?.msg || '发送消息失败，请稍后重试',
      timestamp: Date.now()
    }
    messages.value.push(errorMessage)
    
    ElMessage.error('发送消息失败')
  } finally {
    isLoading.value = false
    scrollToBottom()
  }
}

const clearChat = () => {
  messages.value = []
  ElMessage.success('对话已清空')
}

onMounted(() => {
  messages.value.push({
    role: 'ai',
    content: '您好！我是AI助手，有什么可以帮助您的吗？',
    timestamp: Date.now()
  })
  scrollToBottom()
})
</script>

<style scoped lang="less">
.chat-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background-color: #fff;
  border-bottom: 1px solid #e8e8e8;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

  h2 {
    margin: 0;
    color: #333;
    font-size: 18px;
    font-weight: 600;
  }
}

.chat-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #fafafa;

  .message {
    display: flex;
    margin-bottom: 20px;
    
    &.user {
      flex-direction: row-reverse;
      
      .message-content {
        margin-right: 12px;
        margin-left: 0;
      }
      
      .message-bubble {
        background-color: #1890ff;
        color: white;
        border-radius: 18px 18px 4px 18px;
      }
    }
    
    &.ai {
      .message-content {
        margin-left: 12px;
        margin-right: 0;
      }
      
      .message-bubble {
        background-color: white;
        color: #333;
        border: 1px solid #e8e8e8;
        border-radius: 18px 18px 18px 4px;
      }
    }
  }

  .message-avatar {
    flex-shrink: 0;
  }

  .message-content {
    max-width: 70%;
  }

  .message-bubble {
    padding: 12px 16px;
    word-wrap: break-word;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
    
    .streaming-content {
      .cursor {
        animation: blink 1s infinite;
        color: #1890ff;
      }
    }
    
    .loading-indicator {
      color: #666;
      
      .loading-dots {
        animation: dots 1.5s infinite;
      }
    }
  }
}

.chat-input-area {
  padding: 16px 24px;
  background-color: #fff;
  border-top: 1px solid #e8e8e8;

  .input-container {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .input-actions {
    display: flex;
    justify-content: flex-end;
  }
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}

@keyframes dots {
  0%, 20% { opacity: 0; }
  50% { opacity: 1; }
  100% { opacity: 0; }
}
</style>