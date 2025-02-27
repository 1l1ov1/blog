<script setup>
import { ref, onMounted } from 'vue';
import UsernameForm from './components/UsernameForm.vue'
import PhoneForm from './components/PhoneForm.vue'
import { showErrorMessage, showSuccessMessage } from '@/utils/message'
import { login } from '@/api/auth'
import responseCode from '@/enums/responseCode'
const formRef = ref();
const InputType = ref('username'); // 当前登录方式：username（用户名）、phone（手机）
// 切换登录方式
const switchInputType = (type) => {
    InputType.value = type;
};

// 提交表单
const submitForm = async () => {
    const formDate = await formRef.value.getFormDate();
    if (formDate) {
        let form = {
            ...formDate,
            inputType: InputType.value
        }
        login(form).then(res => {
            if (res.code === responseCode.SUCCESS) {
                showSuccessMessage('登录成功！', {
                    grouping: true
                });
                // 登录成功，跳转到首页
                setTimeout(() => {
                    window.location.href = '/';
                }, 1000);
            } else {
                showErrorMessage(res.msg, {
                    grouping: true
                });
                // TODO 刷新验证码

            }
        })
    } else {
        // 如果说不存在
        showErrorMessage('表单验证失败，请检查输入信息！', {
            grouping: true
        });
    }
};
// 动态粒子初始化
onMounted(() => {
    const particles = document.querySelectorAll('.particle');
    particles.forEach(particle => {
        particle.style.width = `${Math.floor(Math.random() * 6 + 4)}px`;
        particle.style.height = `${Math.floor(Math.random() * 6 + 4)}px`;
        particle.style.left = `${Math.floor(Math.random() * 100)}%`;
        particle.style.top = `${Math.floor(Math.random() * 100)}%`;
        particle.style.animationDelay = `${Math.floor(Math.random() * 10) * -1}s`;
    });
});

</script>

<template>
    <div class="container">
        <div class="form-box">
            <div class="header">
                <h1>欢迎回来</h1>
                <p>开启您的数字之旅</p>
            </div>

            <!-- 登录方式切换 -->
            <div class="login-type">
                <el-button :type="InputType === 'username' ? 'primary' : 'text'" @click="switchInputType('username')">
                    用户名登录
                </el-button>
                <el-button :type="InputType === 'phone' ? 'primary' : 'text'" @click="switchInputType('phone')">
                    手机登录
                </el-button>

            </div>

            <!-- 用户名登录 -->
            <UsernameForm ref="formRef" :InputType="InputType" />

            <!-- 手机登录 -->
            <PhoneForm :InputType="InputType" />

            <!-- 提交按钮 -->
            <el-button type="primary" size="large" round class="submit-btn" @click="submitForm">
                立即登录
            </el-button>

            <!-- 忘记密码和注册链接 -->
            <div class="footer-links">
                <el-link type="info" @click="$router.push('/forgot-password')">
                    忘记密码
                </el-link>
                <el-link type="primary" @click="$router.push('/register')">
                    前往注册
                </el-link>
            </div>
        </div>

        <!-- 背景动态粒子 -->
        <div class="particles">
            <div v-for="n in 30" :key="n" class="particle"></div>
        </div>
    </div>
</template>

<style lang="less" scoped>
.container {
    --primary-color: #4361ee;
    --bg-gradient: linear-gradient(45deg, #e0c3fc 0%, #8ec5fc 100%);

    width: 100vw;
    height: 100vh;
    background: var(--bg-gradient);
    display: grid;
    place-items: center;
    position: relative;
    overflow: hidden;

    .form-box {
        width: min(90%, 400px);
        padding: 2.5rem;
        background: rgba(255, 255, 255, 0.95);
        border-radius: 1.5rem;
        box-shadow: 0 12px 30px rgba(67, 97, 238, 0.15);
        backdrop-filter: blur(8px);
        z-index: 2;
        transition: transform 0.3s;

        &:hover {
            transform: translateY(-5px);
        }

        .header {
            text-align: center;
            margin-bottom: 2rem;

            h1 {
                color: var(--primary-color);
                font-size: 2rem;
                margin-bottom: 0.5rem;
            }

            p {
                color: #6c757d;
                font-size: 0.9rem;
            }
        }

        .login-type {
            display: flex;
            justify-content: space-around;
            margin-bottom: 1.5rem;
        }

        .submit-btn {
            width: 100%;
            font-weight: bold;
            background: var(--primary-color);
            transition: all 0.3s;

            &:hover {
                opacity: 0.9;
                transform: scale(0.98);
            }
        }

        .footer-links {
            display: flex;
            justify-content: space-between;
            margin-top: 1rem;
        }



    }

    // 动态粒子背景
    .particles {
        position: absolute;
        width: 100%;
        height: 100%;

        .particle {
            position: absolute;
            background: rgba(255, 255, 255, 0.6);
            border-radius: 50%;
            animation: float 8s infinite linear;
        }
    }
}

@keyframes float {

    0%,
    100% {
        transform: translate(0, 0);
    }

    25% {
        transform: translate(5vw, -10vh);
    }

    50% {
        transform: translate(-3vw, 5vh);
    }

    75% {
        transform: translate(8vw, 7vh);
    }
}
</style>